package com.unicam.chorchain.codeGenerator;

import com.unicam.chorchain.codeGenerator.solidity.Function;
import com.unicam.chorchain.codeGenerator.solidity.Types;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CodeGenVisitor implements Visitor {

    /*
    StartEvent
    EndEvent
    SequenceFlow
    ExclusiveGateway
    EventBasedGateway
    ParallelGateway
    ModelElementInstance
     */

    @Override
    public String visit(TreeNode node) {
//        return (" Visited: " + node.getId() + " " + node.getClassSimpleName() + "\n");

        switch (node.getClassSimpleName()) {
            case "StartEventImpl":
                return visitStartEvent(node);
            case "SequenceFlowImpl":
                return visitSequenceFlow(node);
            case "EndEventImpl":
                return visitEndEvent(node);
            case "ExclusiveGatewayImpl":
                return visitExclusiveGateway(node);
            case "EventBasedGatewayImpl":
                return visitEventBasedGateway(node);
            case "ParallelGatewayImpl":
                return visitParallelGateway(node);
            case "ModelElementInstanceImpl":
                return visitModelElementInstance(node);
            default:
                return (" Visited: " + node.getId() + " " + node.getClass() + " " + node.getClassSimpleName() + "\n");
        }
    }

    private String visitStartEvent(TreeNode node) {
        log.debug("********StartEvent *****");
        return Function
                .builder()
                .functionComment("StarEvent(" + node.getName() + ") " + node.getId())
                .name(node.getId())
                .source(node.getId())
                .visibility(Types.visibility.PUBLIC)
                .build().toString();
    }

    private String visitEndEvent(TreeNode node) {
        log.debug("********EndEvent *****");
        return Function
                .builder()
                .functionComment("EndEvent(" + node.getName() + "): " + node.getId())
                .name(node.getId())
                .source(node.getId())
                .visibility(Types.visibility.PUBLIC)
                .build().toString();
    }

    private String visitEventBasedGateway(TreeNode node) {
        log.debug("********EventBasedGateway *****");
        return Function
                .builder()
                .functionComment("EventBasedGateway(" + node.getName() + "): " + node.getId())
                .name(node.getId())
                .source(node.getId())
                .visibility(Types.visibility.PUBLIC)
                .build().toString();
    }

    private String visitModelElementInstance(TreeNode node) {
        log.debug("********ModelElement *****");
        return Function
                .builder()
                .functionComment("Task(" + node.getName() + "): " + node.getId())
                .name(node.getId())
                .source(node.getId())
                .visibility(Types.visibility.PUBLIC)
                .build().toString();
    }

    private String visitParallelGateway(TreeNode node) {
        log.debug("********ParallelGateway *****");
        return Function
                .builder()
                .functionComment("ParallelGateway(" + node.getName() + "): " + node.getId())
                .name(node.getId())
                .source(node.getId())
                .visibility(Types.visibility.PUBLIC)
                .build().toString();
    }

    private String visitExclusiveGateway(TreeNode node) {
        log.debug("********ExclusiveGateway: *****");
        return Function
                .builder()
                .functionComment("ExclusiveGateway(" + node.getName() + "):" + node.getId())
                .name(node.getId())
                .source(node.getId())
                .visibility(Types.visibility.PUBLIC)
                .build().toString();
    }

    private String visitSequenceFlow(TreeNode node) {
        log.debug("********SequenceFlow *****");
        return Function
                .builder()
                .functionComment("SequenceFlow(" + node.getName() + "): " + node.getId())
                .name(node.getId())
                .source(node.getId())
                .visibility(Types.visibility.PUBLIC)
                .build().toString();
    }
}
