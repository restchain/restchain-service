package com.unicam.chorchain.codeGenerator;

import com.unicam.chorchain.codeGenerator.adapter.*;
import com.unicam.chorchain.codeGenerator.solidity.Function;
import com.unicam.chorchain.codeGenerator.solidity.IfConstruct;
import com.unicam.chorchain.codeGenerator.solidity.Types;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.model.xml.ModelInstance;
import org.camunda.bpm.model.xml.instance.ModelElementInstance;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static com.unicam.chorchain.codeGenerator.adapter.ChoreographyTaskAdapter.TaskType.ONEWAY;

@Slf4j
public class CodeGenVisitor implements Visitor {


    @Override
    public String visit(BpmnModelAdapter node) {
        return "";
    }

    @Override
    public String visitStartEvent(StartEventAdapter node) {
        log.debug("********StartEvent *****");


        return Function
                .builder()
                .functionComment("StarEvent(" + node.getName() + ") " + node.getOrigId())
                .name(normalizeId(node.getId()))
                .sourceId(node.getId())
                .enable(nextElementId(node.getModelInstance(), node.getOutgoing().get(0)))
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


        Collection<IfConstruct> ifs = new ArrayList<>(0);
        Collection<String> toEnable = new ArrayList<>(0);

        if (node.getDirection().equals("Diverging")) {
            node.getOutgoing().stream().forEach(out -> {
                        ifs.add(IfConstruct.builder()
                                .condition(out.getName())
                                .enableAndActiveTask(((SequenceFlowAdapter) out).getTargetRefId(), true)
                                .build());
                    }
            );
        }

        if (node.getDirection().equals("Converging")) {
            toEnable.add(nextElementId(node.getModelInstance(), node.getOutgoing().get(0)));
        }

        return Function
                .builder()
                .functionComment("ExclusiveGateway(" + node.getName() + "):" + node.getOrigId() + " Dir: " + node.getDirection())
                .name(normalizeId(node.getId()))
                .sourceId(node.getId())
                .visibility(Types.visibility.PRIVATE)
                .ifConstructs(ifs)
                .enables(toEnable)
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
        StringBuffer sb = new StringBuffer();
        SequenceFlowAdapter nextElement = (SequenceFlowAdapter) node.getOutgoing().get(0);

        log.debug(" ** type: {} - {} - {}    **", node.getType(), node.getName(), node.getId());


        if (node.getType() == ONEWAY) {

            sb.append(Function.builder()
                    .functionComment("Task(" + node.getName() + "): " + node.getId() + " - TYPE: " + node.getType() + " - " + node
                            .getRequestMessage()
                            .getMessage()
                            .getName())
                    .name(normalizeId(node.getRequestMessage().getMessage().getId()))
                    .sourceId(node.getRequestMessage().getMessage().getId())
                    .globalVariabilePrefix("currentMemory")
                    .parameters(getParamsList(node.getRequestMessage().getMessage().getName()))
//                    .enable(node.getNextTaskElement().getTargetId())
                    .enableAndActiveTask(nextElement.getTargetRefId(),
                            nextElement.isTargetGatewayOrNot())
                    .build());
            sb.append("\n\n");

        } else {
            //Upper part - requestMessage
            sb.append(Function.builder()
                    .functionComment("Task(" + node.getName() + "): " + node.getId() + " - TYPE: " + node.getType())
                    .name(normalizeId(node.getRequestMessage().getMessage().getId()))
                    .sourceId(node.getRequestMessage().getMessage().getId())
                    .enable(node.getResponseMessage().getMessage().getId())
                    .globalVariabilePrefix("currentMemory")
                    .parameters(getParamsList(node.getRequestMessage().getMessage().getName()))
                    .build());
            sb.append("\n\n");

            //lower part - requestMessage
            sb.append(Function.builder()
                    .functionComment("Task(" + node.getName() + "): " + node.getId() + " - TYPE: " + node.getType())
                    .name(normalizeId(node.getResponseMessage().getMessage().getId()))
                    .sourceId(node.getResponseMessage().getMessage().getId())
                    .globalVariabilePrefix("currentMemory")
                    .parameters(getParamsList(node.getResponseMessage().getMessage().getName()))
                    .enableAndActiveTask(nextElement.getTargetRefId(),
                            nextElement.isTargetGatewayOrNot()).build());
            sb.append("\n\n");

        }


        if (node.getRequestMessage() != null) {
            log.debug(" \nReq MessageFlow\n\tid: {}\n\tname: {}\n\tsource:{}\n\ttarget: {}\n\tmessaggio: {}\n ",
                    node.getRequestMessage().getId(),
                    node.getRequestMessage().getName(),
                    node.getRequestMessage().getSource().getId(),
                    node.getRequestMessage().getTarget().getId(),
                    node.getRequestMessage().getMessage().getId()
            );

//            SequenceFlowAdapter nextElement = (SequenceFlowAdapter) Factories.bpmnModelFactory.create(node.getOutgoingElement());


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

//        log.debug("********ModelElement *****");
//        sb.append(Function
//                        .builder()
//                        .functionComment("Task(" + node.getName() + "): " + node.getId())
////                .name(task.getNextTaskElement().getTargetId())
//                        .sourceId(node.getId())
//                        .visibility(Types.visibility.PRIVATE)
//                        .build().toString()
//        );
        return sb.toString();


    }

    private String normalizeId(String id) {
        return id.replace("-", "_");
    }

    private ModelElementInstance loadElement(ModelInstance instance, String id) {
        return instance.getModelElementById(id);
    }

    private String nextElementId(ModelInstance instance, BpmnModelAdapter startNode) {
        SequenceFlowAdapter sequenceFlow = (SequenceFlowAdapter) startNode;
        ModelElementInstance targetElementId = instance
                .getModelElementById(sequenceFlow.getTargetRefId());
        BpmnModelAdapter targetElement = Factories.bpmnModelFactory.create(targetElementId);
        //Se il targetElement è di tipo  ChoreographyTaskAdapter allora prendi l'id  del messaggio di Request
        //altrimenti in tutti gli altri casi prendi l'id dell'elemento stesso;
        if (targetElement instanceof ChoreographyTaskAdapter) {
            return ((ChoreographyTaskAdapter) targetElement).getRequestMessage().getMessage().getId();
        } else {
            return targetElement.getId();
        }
    }

    private Collection<String> getParamsList(String msg) {
        log.debug("singture {}:", msg);
        String add = "";
        String n = msg.replace("string", "").replace("uint", "").replace("bool", "").replace(" ", "");
        String r = n.replace(")", "");
        String[] t = r.split("\\(");
        String[] m = t[1].split(",");

        List<String> res = new ArrayList<>();
        res.addAll(Arrays.asList(m));
        return res;
    }
}
