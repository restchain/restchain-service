package com.unicam.chorchain;

import com.sun.tools.javadoc.Start;
import com.unicam.chorchain.solidity.Enum;
import com.unicam.chorchain.solidity.*;
import com.unicam.chorchain.solidity.Event;
import com.unicam.chorchain.storage.StorageFileNotFoundException;
import com.unicam.chorchain.translator.ChoreographyTask;
import javafx.beans.binding.ObjectExpression;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.pqc.crypto.gmss.util.GMSSRandom;
import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.impl.BpmnModelInstanceImpl;
import org.camunda.bpm.model.bpmn.impl.instance.ExclusiveGatewayImpl;
import org.camunda.bpm.model.bpmn.instance.Process;
import org.camunda.bpm.model.bpmn.instance.*;
import org.camunda.bpm.model.xml.impl.instance.ModelElementInstanceImpl;
import org.camunda.bpm.model.xml.instance.ModelElementInstance;
import org.camunda.bpm.model.xml.type.ModelElementType;
import org.junit.Test;
import org.mapstruct.ap.internal.model.common.ModelElement;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.web3j.abi.datatypes.Int;

import java.io.File;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Slf4j
public class SolidityGen {

    private Set<String> visited = new HashSet<>();

    private String getId(Object node, BpmnModelInstance modelInstance) {
        String nodeId;
        if (node instanceof ModelElementInstanceImpl) {
            ChoreographyTask task = new ChoreographyTask((ModelElementInstanceImpl) node, modelInstance);
            nodeId = task.getId();
        } else if (node instanceof BpmnModelInstanceImpl) {
            log.debug("");
            nodeId = "";
        } else {
            nodeId = ((BaseElement) node).getId();
        }
        return nodeId;
    }

    private String getType(Object node) {
        String typeName;
        if (node instanceof ModelElementInstanceImpl) {
            typeName = ((ModelElementInstanceImpl) node).getElementType().getTypeName();
        } else {
            typeName = ((BaseElement) node).getElementType().getTypeName();
        }
        return typeName;
    }

    void traverse(Object node, BpmnModelInstance modelInstance) {
//        log.debug(node.getClass().toString());
//        log.debug("id: {} - type: {}", getId(node, modelInstance), getType(node));

        if (!visited.contains(getId(node, modelInstance))) {
            log.debug("id: {} - type: {} - n°:{}", getId(node, modelInstance), getType(node), visited.size() + 1);
            visited.add(getId(node, modelInstance));

            if (node instanceof StartEvent) {

                StartEvent s = ((StartEvent) node);
                s.getIncoming().forEach(n -> traverse(n, modelInstance));
                s.getOutgoing().forEach(n -> traverse(n, modelInstance));

            } else if (node instanceof EndEvent) {

                EndEvent s = ((EndEvent) node);
                s.getIncoming().forEach(n -> traverse(n, modelInstance));
                s.getOutgoing().forEach(n -> traverse(n, modelInstance));

            } else if (node instanceof ExclusiveGateway) {

                ExclusiveGateway f = ((ExclusiveGatewayImpl) node);
                f.getOutgoing().forEach(n -> traverse(modelInstance.getModelElementById(n.getId()), modelInstance));
                f.getIncoming().forEach(n -> traverse(modelInstance.getModelElementById(n.getId()), modelInstance));

            } else if (node instanceof EventBasedGateway) {

                EventBasedGateway f = ((EventBasedGateway) node);
                f.getOutgoing().forEach(n -> traverse(modelInstance.getModelElementById(n.getId()), modelInstance));
                f.getIncoming().forEach(n -> traverse(modelInstance.getModelElementById(n.getId()), modelInstance));

            } else if (node instanceof ParallelGateway) {

                ParallelGateway f = ((ParallelGateway) node);
                f.getOutgoing().forEach(n -> traverse(modelInstance.getModelElementById(n.getId()), modelInstance));
                f.getIncoming().forEach(n -> traverse(modelInstance.getModelElementById(n.getId()), modelInstance));

            } else if (node instanceof SequenceFlow) {

                SequenceFlow s = ((SequenceFlow) node);
                String targetId = s.getAttributeValue("targetRef");
                String sourceId = s.getAttributeValue("sourceRef");
                traverse(modelInstance.getModelElementById(targetId), modelInstance);
                traverse(modelInstance.getModelElementById(sourceId), modelInstance);

            } else if (node instanceof FlowNode) {

                FlowNode f = ((FlowNode) node);
                f.getOutgoing().forEach(n -> traverse(n.getId(), modelInstance));
                f.getIncoming().forEach(n -> traverse(n.getId(), modelInstance));

            } else if (node instanceof ModelElementInstanceImpl) {

                ModelElementInstanceImpl n = ((ModelElementInstanceImpl) node);
                ChoreographyTask task = new ChoreographyTask((ModelElementInstanceImpl) node, modelInstance);
                log.debug("type {}", task.getType());
                task.getOutgoing().forEach(t -> {
//                    log.debug("outgoing id: {} - type: {}", t.getId(), getType(t));
                    traverse(modelInstance.getModelElementById(t.getId()), modelInstance);
                });
                task.getIncoming().forEach(t -> {
//                    log.debug("incoming: {} - type: {}", t.getId(), getType(t));
                    traverse(modelInstance.getModelElementById(t.getId()), modelInstance);
                });

            } else {
                log.debug("unknow type");
            }
        }
    }

    public String genSimpleProcess(File file) {

        // create an empty model
        BpmnModelInstance modelInstance = Bpmn.readModelFromFile(file);
        //Get all nodes {Start,End, ExGateway, EventBasedGateway,ParralleGateway...}
        Collection<FlowNode> allNodes = modelInstance.getModelElementsByType(FlowNode.class);
        Collection<Task> allTasks = modelInstance.getModelElementsByType(Task.class);


        //Traversing BPMN model, starting from START event
        StartEvent aa = (StartEvent) modelInstance.getModelElementById("sid-0EC70E7E-A42A-4C9E-B120-16B25BDACE7A");
        traverse(modelInstance.getModelElementById("sid-0EC70E7E-A42A-4C9E-B120-16B25BDACE7A"), modelInstance);
//
//        SequenceFlow sequenceFlow = (SequenceFlow) modelInstance.getModelElementById("sid-6BFD2DBA-69AD-4793-AEF9-7250E705C43D");
//
//// get the source and target element
//        FlowNode source = sequenceFlow.getSource();
//        FlowNode target = sequenceFlow.getTarget();
//
//
//// get all outgoing sequence flows of the source
//        Collection<SequenceFlow> outgoing = source.getOutgoing();


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
                .parameter("uint", "time")
                .parameter("address", "sender")
                .parameter("uint", "amount")
                .parameter("bytes32", "lot")
                .parameter("string", "reason")
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

//    // A function used by DFS
//    void DFSUtil(int v,boolean visited[])
//    {
//        // Mark the current node as visited and print it
//        visited[v] = true;
//        System.out.print(v+" ");
//
//        // Recur for all the vertices adjacent to this vertex
//        Iterator<Integer> i = adj[v].listIterator();
//        while (i.hasNext())
//        {
//            int n = i.next();
//            if (!visited[n])
//                DFSUtil(n, visited);
//        }
//    }
//
//    // The function to do DFS traversal. It uses recursive DFSUtil()
//    void DFS(int v)
//    {
//        // Mark all the vertices as not visited(set as
//        // false by default in java)
//        boolean visited[] = new boolean[V];
//
//        // Call the recursive helper function to print DFS traversal
//        DFSUtil(v, visited);
//    }


    @Test
    public void testProcessGen() {
        File f = load("OnlinePurchaseNew.bpmn").toFile();
        SolidityGen p = new SolidityGen();
        p.genSimpleProcess(f);
        // assert true;

    }
}