package com.unicam.chorchain.codeGenerator;

import com.unicam.chorchain.translator.ChoreographyTask;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.model.xml.ModelInstance;
import org.camunda.bpm.model.xml.instance.ModelElementInstance;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Slf4j
public class ModelElementInstanceAdapter implements TreeNode, Visitable {

    private final ModelElementInstance value;
    private final ChoreographyTask choreographyTask;

    ModelElementInstanceAdapter(ModelElementInstance value) {
        log.debug(value.getClass().getSimpleName());
        this.value = value;
        choreographyTask = new ChoreographyTask(value);
    }

    @Override
    public ModelElementInstance getNode() {
        return this.value;
    }

    @Override
    public String getId() {
        return choreographyTask.getId().replace("-", "_");
    }

    @Override
    public String getOrigId() {
        return choreographyTask.getId();
    }

    @Override
    public String getName() {

        return choreographyTask.getName() != null ? choreographyTask.getName().replace("\n", " ") : "";
    }

    @Override
    public ModelInstance getModelInstance() {
        return this.value.getModelInstance();
    }

    @Override
    public String getClassSimpleName() {
        return value.getClass().getSimpleName();
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
    public String accept(Visitor visitor) {
        return visitor.visit(this);
    }
}
