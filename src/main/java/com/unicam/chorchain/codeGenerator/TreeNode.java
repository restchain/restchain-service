package com.unicam.chorchain.codeGenerator;

import org.camunda.bpm.model.xml.ModelInstance;

import java.util.List;

public interface TreeNode extends Visitable {
    String getId();
    String getOrigId();
    String getName();
    ModelInstance getModelInstane();

    String getClassSimpleName();

    List<TreeNode> getIncoming();

    List<TreeNode> getOutgoing();

}
