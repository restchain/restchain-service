package com.unicam.chorchain.codeGenerator;

import org.camunda.bpm.model.bpmn.impl.instance.StartEventImpl;
import org.camunda.bpm.model.bpmn.instance.FlowElement;
import org.camunda.bpm.model.bpmn.instance.StartEvent;

interface Visitor {
//    String visitStartEvent(TreeNode mode);
//    String visitModelElementIstance(TreeNode mode);
//    String visitExclusiveGateway(TreeNode mode);
//    String visit(StartEventImpl startEvent);
    String visit(TreeNode node);
}
