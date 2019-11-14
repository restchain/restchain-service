package com.unicam.chorchain.codeGenerator;

import com.unicam.chorchain.codeGenerator.solidity.Function;
import com.unicam.chorchain.codeGenerator.solidity.Types;
import com.unicam.chorchain.translator.ChoreographyTask;
import lombok.extern.slf4j.Slf4j;

import java.util.stream.Collectors;

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

        switch (node.getClassSimpleName()) {
            case "StartEventImpl":
                return visitStartEvent(node);
//            case "SequenceFlowImpl":
//                return visitSequenceFlow(node);
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
//                return (" Visited: " + node.getId() + " " + node.getClass() + " " + node.getClassSimpleName() + "\n");
                return "";
        }
    }


    private String visitStartEvent(TreeNode node) {
        log.debug("********StartEvent *****");
        return Function
                .builder()
                .functionComment("StarEvent(" + node.getName() + ") " + node.getOrigId())
                .name(node.getId())
                .sourceId(node.getId())
                .visibility(Types.visibility.PUBLIC)
                .build().toString();
    }

    private String visitEndEvent(TreeNode node) {
        log.debug("********EndEvent *****");
        return Function
                .builder()
                .functionComment("EndEvent(" + node.getName() + "): " + node.getOrigId())
                .name(node.getId())
                .sourceId(node.getId())
                .visibility(Types.visibility.PUBLIC)
                .build().toString();
    }

    private String visitEventBasedGateway(TreeNode node) {
        log.debug("********EventBasedGateway *****");
        return Function
                .builder()
                .functionComment("EventBasedGateway(" + node.getName() + "): " + node.getOrigId())
                .enables(node.getOutgoing().stream().map(TreeNode::getId).collect(Collectors.toList()))
                .name(node.getId())
                .sourceId(node.getId())
                .visibility(Types.visibility.PUBLIC)
                .build().toString();
    }

    private String visitModelElementInstance(TreeNode node) {

        // checkOpt o checkMand sono modifier, li metti su tutte le funzioni dei messaggi dipendentemente se deve essre eseguita da un ruolo opzionale o mandatory


        ChoreographyTask task = new ChoreographyTask(node.getNode());

        if (task.getType().equals(ChoreographyTask.TaskType.TWOWAY)) {

        } else {

        }


        log.debug(" ** type: {} - {} - {}    **", task.getType(), node.getName(), node.getId());
        StringBuffer sb = new StringBuffer();


        if (task.getRequestMessage() != null) {
            log.debug(" \nReq MessageFlow\n\tid: {}\n\tname: {}\n\tsource:{}\n\ttarget: {}\n\tmessaggio: {}\n ",
                    task.getRequestMessage().getId(),
                    task.getRequestMessage().getName(),
                    task.getRequestMessage().getSource().getId(),
                    task.getRequestMessage().getTarget().getId(),
                    task.getRequestMessage().getMessage().getId()
            );

            sb.append(Function.builder()
                    .functionComment("Task - message ")
                    .name(task.getRequestMessage().getMessage().getId())
                    .sourceId(task.getRequestMessage().getMessage().getId())
//                    .enable(task.getNextTaskElement().getTargetId())
                    .taskEnableActive_(task.getNextTaskElement().getTargetId(),task.getNextTaskElement().isGatewayOrEndEvent())
                    .build());
            sb.append("\n\n");

        }

        if (task.getResponseMessage() != null) {
            log.debug(" \nRESP messageFlow\n\tid: {}\n\tname: {}\n\tsource:{}\n\ttarget: {}\n\tmessaggio: {}\n ",
                    task.getResponseMessage().getId(),
                    task.getResponseMessage().getName(),
                    task.getResponseMessage().getSource().getId(),
                    task.getResponseMessage().getTarget().getId(),
                    task.getResponseMessage().getMessage().getId()
            );


        }

        log.debug("********ModelElement *****");
        sb.append(Function
                .builder()
                .functionComment("Task(" + node.getName() + "): " + node.getId())
//                .name(task.getNextTaskElement().getTargetId())
                .sourceId(node.getId())
                .visibility(Types.visibility.PUBLIC)
                .build().toString()
        );
        return sb.toString();
    }

    private String visitParallelGateway(TreeNode node) {
        log.debug("********ParallelGateway *****");
        return Function
                .builder()
                .functionComment("ParallelGateway(" + node.getName() + "): " + node.getOrigId())
                .name(node.getId())
                .enables(node.getOutgoing().stream().map(TreeNode::getId).collect(Collectors.toList()))
                .sourceId(node.getId())
                .visibility(Types.visibility.PUBLIC)
                .build().toString();
    }

    private String visitExclusiveGateway(TreeNode node) {
        log.debug("********ExclusiveGateway: *****");
        return Function
                .builder()
                .functionComment("ExclusiveGateway(" + node.getName() + "):" + node.getOrigId())
                .name(node.getId())
                .sourceId(node.getId())
                .visibility(Types.visibility.PUBLIC)
                .build().toString();
    }

    private String visitSequenceFlow(TreeNode node) {
        log.debug("********SequenceFlow *****");
        return Function
                .builder()
                .functionComment("SequenceFlow(" + node.getName() + "): " + node.getOrigId())
                .name(node.getId())
                .sourceId(node.getId())
                .visibility(Types.visibility.PUBLIC)
                .build().toString();
    }
}
