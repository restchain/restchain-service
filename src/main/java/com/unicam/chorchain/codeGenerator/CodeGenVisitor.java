package com.unicam.chorchain.codeGenerator;

import com.unicam.chorchain.codeGenerator.solidity.Function;
import com.unicam.chorchain.codeGenerator.solidity.Types;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CodeGenVisitor implements Visitor {

    @Override
    public String visit(TreeNode node) {
        return (" Visited: " + node.getId() + " " + node.getClass());
    }

    @Override
    public String visitStartEvent(TreeNode node) {
        log.debug("********StartEvent *****");
        return Function
                .builder()
                .name(node.getId())
                .source(node.getId())
                .visibility(Types.visibility.PUBLIC)
                .build().toString();
    }

    @Override
    public String visitModelElementIstance(TreeNode node) {
       return " Visited: ModelELement";

    }

    @Override
    public String visitExclusiveGateway(TreeNode node) {
        return " Visited: Exclusive ";
    }

}
