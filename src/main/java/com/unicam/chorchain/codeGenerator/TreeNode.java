package com.unicam.chorchain.codeGenerator;

import java.util.List;

public interface TreeNode extends Visitable {
    String getId();
    String getOrigId();
    String getName();

    String getClassSimpleName();

    List<TreeNode> getIncoming();

    List<TreeNode> getOutgoing();

}
