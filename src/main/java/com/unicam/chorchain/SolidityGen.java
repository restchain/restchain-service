package com.unicam.chorchain;

import com.unicam.chorchain.solidity.Enum;
import com.unicam.chorchain.solidity.*;
import com.unicam.chorchain.solidity.Event;
import com.unicam.chorchain.storage.StorageFileNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.Process;
import org.camunda.bpm.model.bpmn.instance.*;
import org.camunda.bpm.model.xml.instance.ModelElementInstance;
import org.camunda.bpm.model.xml.type.ModelElementType;
import org.junit.Test;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

import java.io.File;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Slf4j
public class SolidityGen {

    public String genSimpleProcess(File file) {

        // create an empty model
        BpmnModelInstance modelInstance = Bpmn.readModelFromFile(file);
        //Get all nodes {Start,End, ExGateway, EventBasedGateway,ParralleGateway...}
        Collection<FlowNode> allNodes = modelInstance.getModelElementsByType(FlowNode.class);
        Collection<Task> allTasks = modelInstance.getModelElementsByType(Task.class);


        // find all elements of the type task
        ModelElementType taskType = modelInstance.getModel().getType(Task.class);
        Collection<ModelElementInstance> taskInstances = modelInstance.getModelElementsByType(taskType);

        log.debug("Message");
        modelInstance.getModelElementsByType(Message.class).forEach((a) -> log.debug(a.toString()));
        modelInstance.getModelElementsByType(Message.class)
                .forEach((a) -> messageNameToParams(a.getName()).forEach(log::debug));
        log.debug("Nodes");
        allNodes.forEach((a) -> log.debug(a.toString()));
        log.debug("Task");
        allTasks.forEach((a) -> log.debug(a.toString()));
        log.debug("TaskInstances");
        taskInstances.forEach((a) -> log.debug(a.toString()));
        //SequenceFlow => connector between two elements of a process.
        log.debug("SequenceFlow");
        modelInstance.getModelElementsByType(SequenceFlow.class).forEach((a) -> log.debug(a.toString()));


//        StartEvent start = (StartEvent) modelInstance.getModelElementsByType(StartEvent.class);
        modelInstance.getModelElementsByType(StartEvent.class).forEach((a) -> log.debug(a.getTextContent()));


        //*** starts here ***/

        //Get all message elements to obtain global variables
        List<String> structVariables = new ArrayList<>();
        modelInstance.getModelElementsByType(Message.class)
                .forEach((a) -> structVariables.addAll(messageNameToParams(a.getName())));


        //Create a general Struct
        Struct structGlobal = Struct.builder()
                .variableMap(Types.string, "ID")
                .variableMap("State", "status")
                .name("general")
                .build();

        //Generic struct
        Struct structElement = Struct.builder()
                .variables(structVariables)
                .name("StateMemory")
                .build();

        Enum solEnum = Enum.builder()
                .name("State")
                .variableName("s")
                .element("DISABLED")
                .element("ENABLED")
                .element("DONE")
                .build();

//        mapping(string => uint) position;
        Mapping map1 = Mapping.builder()
                .name("position")
                .key("string")
                .value("uint")
                .build();


        Mapping map2 = Mapping.builder()
                .name("operator")
                .key("string")
                .value("string")
                .build();

        Event event1 = Event.builder()
                .name("FailedOffer")
                .parameter("uint","time")
                .parameter("address","sender")
                .parameter("uint","amount")
                .parameter("bytes32","lot")
                .parameter("string","reason")
                .build();

        Contract sol = Contract.builder()
                .pragmaVersion("5.4.3")
                .fileName("primaProva")
                .struct(structGlobal)
                .struct(structElement)
                .mapping(map1)
                .mapping(map2)
                .enumElement(solEnum)
                .constructorBody("owner = msg.sender;")
                .event(event1)
                .build();
        log.debug(sol.toString());


//        Definitions definitions = modelInstance.newInstance(Definitions.class);
//        definitions.setTargetNamespace("http://camunda.org/examples");
//        modelInstance.setDefinitions(definitions);
//
//        // create the process
//        Process process = createElement(modelInstance, definitions,
//                "generated-process", Process.class);
//
//        // create start event, user task and end event
//        StartEvent startEvent = createElement(modelInstance, process, "start",
//                StartEvent.class);
//        UserTask task1 = createElement(modelInstance, process, "task1",
//                UserTask.class);
//        task1.setName("User Task");
//
//        EndEvent endEvent = createElement(modelInstance, process, "end",
//                EndEvent.class);
//
//        // create the connections between the elements
//        createSequenceFlow(modelInstance, process, startEvent, task1);
//        createSequenceFlow(modelInstance, process, task1, endEvent);
//
//        // validate and write model to file
//        Bpmn.validateModel(modelInstance);
//        File file;
//        file = new File("generated-process.bpmn");
//        Bpmn.writeModelToFile(file, modelInstance);
//        return file;
        return sol.toString();
    }

    protected <T extends BpmnModelElementInstance> T createElement(
            BpmnModelInstance modelInstance,
            BpmnModelElementInstance parentElement, String id,
            Class<T> elementClass) {
        T element = modelInstance.newInstance(elementClass);
        element.setAttributeValue("id", id, true);
        parentElement.addChildElement(element);
        return element;
    }

    protected SequenceFlow createSequenceFlow(BpmnModelInstance modelInstance,
                                              Process process, FlowNode from, FlowNode to) {
        String identifier = from.getId() + "-" + to.getId();
        SequenceFlow sequenceFlow = createElement(modelInstance, process,
                identifier, SequenceFlow.class);
        process.addChildElement(sequenceFlow);
        sequenceFlow.setSource(from);
        from.getOutgoing().add(sequenceFlow);
        sequenceFlow.setTarget(to);
        to.getIncoming().add(sequenceFlow);
        return sequenceFlow;
    }

    public Path load(String filename) {
        Path rootLocation = Paths.get("bpmn/");
        return rootLocation.resolve(filename);
    }

    public org.springframework.core.io.Resource loadAsResource(String filename) {
        try {
            Path file = load(filename);
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new StorageFileNotFoundException(
                        "Could not read file: " + filename);

            }
        } catch (MalformedURLException e) {
            throw new StorageFileNotFoundException("Could not read file: " + filename, e);
        }
    }


    private List<String> messageNameToParams(String name) {
        String r = name.replace(")", "");
        String[] type = r.split("\\(");
        String[] m = type[1].split(",");
        List<String> result = new ArrayList<>();
        Collections.addAll(result, m);
        return result;
    }


    @Test
    public void testProcessGen() {
        File f = load("OnlinePurchaseNew.bpmn").toFile();
        SolidityGen p = new SolidityGen();
        p.genSimpleProcess(f);
        // assert true;

    }
}