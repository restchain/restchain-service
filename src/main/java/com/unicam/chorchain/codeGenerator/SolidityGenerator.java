package com.unicam.chorchain.codeGenerator;

import com.unicam.chorchain.codeGenerator.solidity.Enum;
import com.unicam.chorchain.codeGenerator.solidity.*;
import com.unicam.chorchain.storage.StorageFileNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.Message;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Slf4j
public class SolidityGenerator {

    private Set<String> visited = new HashSet<>();
    private List<Visitable> items = new ArrayList<>();

    public void traverse(TreeNode node) {


        if (!visited.contains(node.getId())) {
            log.debug("id: {} - n°:{}", node.getId(), visited.size() + 1);
            items.add(node);
            visited.add(node.getId());

            node.getOutgoing().forEach(this::traverse);
            node.getIncoming().forEach(this::traverse);
        }
    }

    public void eleab(BpmnModelInstance modelInstance) {

        StringBuffer bf = new StringBuffer();
        CodeGenVisitor codeGenVisitor = new CodeGenVisitor();

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

        bf.append(sol.toString());

        items.forEach(v -> bf.append(v.accept(codeGenVisitor)));
        log.debug(bf.toString());
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
}
