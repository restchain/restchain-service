package com.unicam.chorchain.codeGenerator;

import java.util.List;

public interface TreeNode extends Visitable {
    String getId();

    List<TreeNode> getIncoming();

    List<TreeNode> getOutgoing();

}
