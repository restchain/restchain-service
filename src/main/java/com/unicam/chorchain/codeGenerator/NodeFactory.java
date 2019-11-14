package com.unicam.chorchain.codeGenerator;

import org.camunda.bpm.model.bpmn.instance.FlowElement;
import org.camunda.bpm.model.bpmn.instance.FlowNode;
import org.camunda.bpm.model.xml.instance.ModelElementInstance;

//Singleton
public class NodeFactory {
    public TreeNode create(FlowElement value) {
        return new FlowElementAdapter(value);
    }

    public TreeNode create(ModelElementInstance value) {
        return new ModelElementInstanceAdapter(value);
    }

}