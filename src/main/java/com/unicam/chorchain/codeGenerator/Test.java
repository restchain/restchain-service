package com.unicam.chorchain.codeGenerator;

import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.xml.instance.ModelElementInstance;

import java.io.File;

public class Test {
    @org.junit.Test
    public void testProcessGen() {
        SolidityGenerator sg = new SolidityGenerator();
        File file = sg.load("OnlinePurchaseNew.bpmn").toFile();
        BpmnModelInstance modelInstance = Bpmn.readModelFromFile(file);
        ModelElementInstance start = modelInstance.getModelElementById("sid-0EC70E7E-A42A-4C9E-B120-16B25BDACE7A");
        sg.traverse(Factories.bpmnModelFactory.create(modelInstance.getModelElementById("sid-0EC70E7E-A42A-4C9E-B120-16B25BDACE7A")));
        sg.eleab( modelInstance);
    }
}
