package com.unicam.chorchain.codeGenerator;

import org.camunda.bpm.model.xml.ModelInstance;
import org.camunda.bpm.model.xml.instance.ModelElementInstance;

import java.util.List;

public interface TreeNode extends Visitable {
    ModelElementInstance getNode();

    String getId();

    String getOrigId();

    String getName();

    ModelInstance getModelInstance();

    String getClassSimpleName();

    List<TreeNode> getIncoming();

    List<TreeNode> getOutgoing();

}
