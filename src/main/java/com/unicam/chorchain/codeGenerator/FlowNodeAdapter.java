package com.unicam.chorchain.codeGenerator;

import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.model.bpmn.instance.FlowNode;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class FlowNodeAdapter implements TreeNode, Visitable {

    private final FlowNode value;

    FlowNodeAdapter(FlowNode value) {
        this.value = value;
    }

    @Override
    public String getId() {
        return value.getId();
    }


    @Override
    public List<TreeNode> getIncoming() {
        return value.getIncoming()
                .stream()
                .map(n -> Factories.nodeFactory.create(n)).collect(Collectors.toList());
    }

    @Override
    public List<TreeNode> getOutgoing() {
        return value.getOutgoing()
                .stream()
                .map(n -> Factories.nodeFactory.create(n)).collect(Collectors.toList());
    }

    @Override
    public void accept(Visitor visitor) {
         visitor.visit(this);
    }
}
