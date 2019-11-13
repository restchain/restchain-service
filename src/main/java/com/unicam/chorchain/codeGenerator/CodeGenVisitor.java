package com.unicam.chorchain.codeGenerator;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CodeGenVisitor implements Visitor {

    @Override
    public void visit(TreeNode mode) {
        System.out.println (" hi" + mode.getId());
    }
}
