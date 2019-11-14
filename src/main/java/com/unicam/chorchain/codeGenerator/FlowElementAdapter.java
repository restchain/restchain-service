package com.unicam.chorchain.codeGenerator;

import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.model.bpmn.instance.FlowElement;
import org.camunda.bpm.model.xml.instance.ModelElementInstance;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class FlowElementAdapter implements TreeNode, Visitable {

    private final FlowElement value;

    FlowElementAdapter(FlowElement value) {
        log.debug(value.getClass().getSimpleName());
        this.value = value;
    }

    @Override
    public String getId() {
        return value.getId();
    }

    @Override
    public String getClassSimpleName() {
        return value.getClass().getSimpleName();
    }

    @Override
    public List<TreeNode> getIncoming() {
        String sourceId = value.getAttributeValue("sourceRef");
        ArrayList<TreeNode> treeNodes = new ArrayList<>();
        ModelElementInstance a = value.getModelInstance().getModelElementById(sourceId);
        treeNodes.add((Factories.nodeFactory.create(a)));
        return treeNodes;
    }

    @Override
    public List<TreeNode> getOutgoing() {
        String targetId = value.getAttributeValue("targetRef");
        log.debug(targetId);
        ArrayList<TreeNode> treeNodes = new ArrayList<>();
        ModelElementInstance a = value.getModelInstance().getModelElementById(targetId);
        treeNodes.add((Factories.nodeFactory.create(a)));
        return treeNodes;
    }

    @Override
    public String accept(Visitor visitor) {
        return visitor.visit(this);
    }
}
