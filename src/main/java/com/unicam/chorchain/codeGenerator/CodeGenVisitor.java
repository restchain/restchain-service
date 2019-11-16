package com.unicam.chorchain.codeGenerator;

import com.unicam.chorchain.codeGenerator.adapter.*;
import com.unicam.chorchain.codeGenerator.solidity.Function;
import com.unicam.chorchain.codeGenerator.solidity.Types;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.model.xml.Model;
import org.camunda.bpm.model.xml.ModelInstance;
import org.camunda.bpm.model.xml.instance.ModelElementInstance;

import java.util.stream.Collectors;

@Slf4j
public class CodeGenVisitor implements Visitor {


    @Override
    public String visit(BpmnModelAdapter node) {
        return "";
    }

    @Override
    public String visitStartEvent(StartEventAdapter node) {
        log.debug("********StartEvent *****");

        SequenceFlowAdapter sequenceFlow = (SequenceFlowAdapter) node.getOutgoing().get(0);
        ModelElementInstance modelElementById = node.getModelInstance()
                .getModelElementById(sequenceFlow.getTargetRefId());
        BpmnModelAdapter nextElement = Factories.bpmnModelFactory.create(modelElementById);
        log.debug("test type {} - {}",
                modelElementById.getClass().toString(),
                nextElement.getClass().getSimpleName());
        String nextElementId;

        //Se il next element è di tipo  ChorTask allor prendi l'id  del messaggio di Reeuest
        //altrimenti in tutti gli altri casi prendi l'id dell'elemento stesso;
        if (nextElement instanceof ChoreographyTaskAdapter) {
            nextElementId  = ((ChoreographyTaskAdapter) nextElement).getRequestMessage().getMessage().getId();
        } else {
            nextElementId = nextElement.getId();
        }

        return Function
                .builder()
                .functionComment("StarEvent(" + node.getName() + ") " + node.getOrigId())
                .name(normalizeId(node.getId()))
                .sourceId(node.getId())
                .enable(nextElementId)
                .visibility(Types.visibility.PRIVATE)
                .build().toString();
    }

    @Override
    public String visitEndEvent(EndEventAdapter node) {
        log.debug("********EndEvent *****");
        return Function
                .builder()
                .functionComment("EndEvent(" + node.getName() + "): " + node.getOrigId())
                .name(node.getId())
                .sourceId(node.getId())
                .visibility(Types.visibility.PRIVATE)
                .build().toString();
    }

    @Override
    public String visitParallelGateway(ParallelGatewayAdapter node) {
        log.debug("********ParallelGateway *****");
        return Function
                .builder()
                .functionComment("ParallelGateway(" + node.getName() + "): " + node.getOrigId())
                .name(node.getId())
                .enables(node.getOutgoing().stream().map(BpmnModelAdapter::getId).collect(Collectors.toList()))
                .sourceId(node.getId())
                .visibility(Types.visibility.PRIVATE)
                .build().toString();
    }

    @Override
    public String visitExclusiveGateway(ExclusiveGatewayAdapter node) {
        log.debug("********ExclusiveGateway: *****");
        return Function
                .builder()
                .functionComment("ExclusiveGateway(" + node.getName() + "):" + node.getOrigId())
                .name(node.getId())
                .sourceId(node.getId())
                .visibility(Types.visibility.PRIVATE)
                .build().toString();
    }

    @Override
    public String visitEventBasedGateway(EventBasedGatewayAdapter node) {
        log.debug("********EventBasedGateway *****");
        return Function
                .builder()
                .functionComment("EventBasedGateway(" + node.getName() + "): " + node.getOrigId())
                .enables(node.getOutgoing().stream().map(BpmnModelAdapter::getId).collect(Collectors.toList()))
                .name(node.getId())
                .sourceId(node.getId())
                .visibility(Types.visibility.PRIVATE)
                .build().toString();
    }

    @Override
    public String visitChoreographyTask(ChoreographyTaskAdapter node) {
        // checkOpt o checkMand sono modifier, li metti su tutte le funzioni dei messaggi dipendentemente se deve essre eseguita da un ruolo opzionale o mandatory


        if (node.getType().equals(ChoreographyTaskAdapter.TaskType.TWOWAY)) {

        } else {

        }

        log.debug(" ** type: {} - {} - {}    **", node.getType(), node.getName(), node.getId());
        StringBuffer sb = new StringBuffer();


        if (node.getRequestMessage() != null) {
            log.debug(" \nReq MessageFlow\n\tid: {}\n\tname: {}\n\tsource:{}\n\ttarget: {}\n\tmessaggio: {}\n ",
                    node.getRequestMessage().getId(),
                    node.getRequestMessage().getName(),
                    node.getRequestMessage().getSource().getId(),
                    node.getRequestMessage().getTarget().getId(),
                    node.getRequestMessage().getMessage().getId()
            );

//            SequenceFlowAdapter nextElement = (SequenceFlowAdapter) Factories.bpmnModelFactory.create(node.getOutgoingElement());
            SequenceFlowAdapter nextElement = (SequenceFlowAdapter) node.getOutgoing().get(0);
            sb.append(Function.builder()
                    .functionComment("Task - message ")
                    .name(node.getRequestMessage().getMessage().getId())
                    .sourceId(node.getRequestMessage().getMessage().getId())
//                    .enable(node.getNextTaskElement().getTargetId())
                    .taskEnableActive_(nextElement.getTargetRefId(),
                            nextElement.isTargetGatewayOrNot())
                    .build());
            sb.append("\n\n");

        }

        if (node.getResponseMessage() != null) {
            log.debug(" \nRESP messageFlow\n\tid: {}\n\tname: {}\n\tsource:{}\n\ttarget: {}\n\tmessaggio: {}\n ",
                    node.getResponseMessage().getId(),
                    node.getResponseMessage().getName(),
                    node.getResponseMessage().getSource().getId(),
                    node.getResponseMessage().getTarget().getId(),
                    node.getResponseMessage().getMessage().getId()
            );


        }

        log.debug("********ModelElement *****");
        sb.append(Function
                        .builder()
                        .functionComment("Task(" + node.getName() + "): " + node.getId())
//                .name(task.getNextTaskElement().getTargetId())
                        .sourceId(node.getId())
                        .visibility(Types.visibility.PRIVATE)
                        .build().toString()
        );
        return sb.toString();


    }

    private String normalizeId(String id) {
        return id.replace("-", "_");
    }

    private ModelElementInstance loadElement(ModelInstance instance, String id) {
        return instance.getModelElementById(id);
    }
}
