package com.unicam.chorchain.codeGenerator;

import com.unicam.chorchain.codeGenerator.adapter.BpmnModelAdapter;
import com.unicam.chorchain.codeGenerator.solidity.*;
import com.unicam.chorchain.model.Instance;
import com.unicam.chorchain.storage.StorageFileNotFoundException;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

@Slf4j
public class SolidityGenerator {


    //Costruttore con Modello Bpmn
    private SolidityInstance solidityInstance;
    private Set<String> visited = new HashSet<>();
    private List<Visitable> bpmnTree = new ArrayList<>();

    public SolidityGenerator(Instance instance) {
        this.solidityInstance = new SolidityInstance(instance);
    }

    //Traverse all the Bpmn elementAdapters to build the sequenceFlow tree.
    public void traverse(BpmnModelAdapter node) {
        if (!visited.contains(node.getId())) {
            log.debug("id: {} - n°:{}", node.getId(), visited.size() + 1);
            bpmnTree.add(node);
            visited.add(node.getId());

            node.getOutgoing().forEach(this::traverse);
            //Ci vuole?
//            node.getIncoming().forEach(this::traverse);
        }
    }

    //Uses visitors to start building the solidity file
    public String build() {
        bpmnTree.forEach(v -> v.accept(new CodeGenVisitor(solidityInstance)));
        String code = solidityInstance.build();
        log.debug("solidity :\n\n{}\n", code);
        return code;
    }
}
