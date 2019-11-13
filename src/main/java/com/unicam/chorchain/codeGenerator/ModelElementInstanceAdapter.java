package com.unicam.chorchain.codeGenerator;

import com.unicam.chorchain.translator.ChoreographyTask;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.model.xml.instance.ModelElementInstance;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class ModelElementInstanceAdapter implements TreeNode, Visitable {

    private final ModelElementInstance value;
    private final ChoreographyTask choreographyTask;

    ModelElementInstanceAdapter(ModelElementInstance value) {
        log.debug(value.getClass().toString());
        this.value = value;
        choreographyTask = new ChoreographyTask(value);
    }

    @Override
    public String getId() {
        return choreographyTask.getId();
    }

    @Override
    public List<TreeNode> getIncoming() {
        return choreographyTask.getIncoming()
                .stream()
                .map(n -> Factories.nodeFactory.create(n)).collect(Collectors.toList());
    }

    @Override
    public List<TreeNode> getOutgoing() {
        return choreographyTask.getOutgoing()
                .stream()
                .map(n -> Factories.nodeFactory.create(n)).collect(Collectors.toList());
    }

    @Override
    public void accept(Visitor visitor) {
         visitor.visit(this);
    }
}
