package com.unicam.chorchain.codeGenerator;

interface Visitor {
    String visitStartEvent(TreeNode mode);
    String visitModelElementIstance(TreeNode mode);
    String visitExclusiveGateway(TreeNode mode);
    String visit(TreeNode node);
}
