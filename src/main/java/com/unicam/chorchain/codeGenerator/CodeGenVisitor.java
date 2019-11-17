package com.unicam.chorchain.codeGenerator;

import com.unicam.chorchain.codeGenerator.adapter.*;
import com.unicam.chorchain.codeGenerator.solidity.Function;
import com.unicam.chorchain.codeGenerator.solidity.IfConstruct;
import com.unicam.chorchain.codeGenerator.solidity.Types;
import com.unicam.chorchain.model.Instance;
import com.unicam.chorchain.model.InstanceParticipantUser;
import com.unicam.chorchain.model.Participant;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.model.xml.ModelInstance;
import org.camunda.bpm.model.xml.instance.ModelElementInstance;

import java.util.*;
import java.util.stream.Collectors;

import static com.unicam.chorchain.codeGenerator.adapter.ChoreographyTaskAdapter.TaskType.ONEWAY;

@Slf4j
public class CodeGenVisitor implements Visitor {

    private Instance instance;
    private List<String> mandatoryParticipantsSol;
    private List<String> optionalParticipantSol;

    public CodeGenVisitor(Instance instance) {
        this.instance = instance;
        this.mandatoryParticipantsSol = instance.getMandatoryParticipants()
                .stream()
                .filter(p -> p.getUser() != null)
                .map(p -> p.getParticipant().getName())
                .collect(Collectors.toList());

        this.optionalParticipantSol = instance.getChoreography().getParticipants().stream().filter((p) ->
                !mandatoryParticipantsSol.contains(p.getName())).map(p -> p.getName()).collect(Collectors.toList());
    }

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


        Collection<IfConstruct> ifConstructs = new ArrayList<>(0);
        Collection<String> toEnable = new ArrayList<>(0);

        if (node.getDirection().equals("Diverging")) {
            node.getOutgoing().stream().forEach(out -> {
                        ifConstructs.add(IfConstruct.builder()
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
                .ifConstructs(ifConstructs)
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
        log.debug(" ** part: {} - {} - {}    **",
                node.getParticipantRef().getName(),
                getParticipantModifier(node.getParticipantRef().getName()),
                printRoleList());

        if (node.getType() == ONEWAY) {

            sb.append(Function.builder()
                    .functionComment("Task(" + node.getName() + "): " + node.getId() + " - TYPE: " + node.getType() + " - " + node
                            .getRequestMessage()
                            .getMessage()
                            .getName())
                    .name(normalizeId(node.getRequestMessage().getMessage().getId()))
                    .visibility(Types.visibility.PUBLIC)
                    .modifier(getParticipantModifier(node.getParticipantRef().getName()))
                    .sourceId(node.getRequestMessage().getMessage().getId())
                    .globalVariabilePrefix("currentMemory")
                    .parameters(getParamsList(node.getRequestMessage().getMessage().getName()))
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
                    .visibility(Types.visibility.PUBLIC)
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
                    .visibility(Types.visibility.PUBLIC)
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
                    node.getRequestMessage().getMessage().getName()
            );

//            SequenceFlowAdapter nextElement = (SequenceFlowAdapter) Factories.bpmnModelFactory.create(node.getOutgoingElement());


        }

        if (node.getResponseMessage() != null) {
            log.debug(" \nRESP messageFlow\n\tid: {}\n\tname: {}\n\tsource:{}\n\ttarget: {}\n\tmessaggio: {}\n ",
                    node.getResponseMessage().getId(),
                    node.getResponseMessage().getName(),
                    node.getResponseMessage().getSource().getId(),
                    node.getResponseMessage().getTarget().getId(),
                    node.getResponseMessage().getMessage().getName()
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

    // Performs a - replacing in _
    private String normalizeId(String id) {
        return id.replace("-", "_");
    }

    //Returns the next elementId pointed by the passed sequenceFlow
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

    // returns a List of all params name contained in the passed signature (msg)
    private Collection<String> getParamsList(String msg) {
        log.debug("signature {}:", msg);
        String add = "";
        String n = msg.replace("string", "").replace("uint", "").replace("bool", "").replace(" ", "");
        String r = n.replace(")", "");
        String[] t = r.split("\\(");
        String[] m = t[1].split(",");

        List<String> res = new ArrayList<>();
        res.addAll(Arrays.asList(m));
        return res;
    }

    // returns a modifier function call depending of the participant role
    private String getParticipantModifier(String participantFromModel) {
        //If participantFromRole is contained in tha Mandatory list is Mandatory else is Optional
        if (mandatoryParticipantsSol.contains(participantFromModel)) {
            return roleModifierFormatter(Types.Mandatory_modifier, participantFromModel);

        } else {
            return roleModifierFormatter(Types.Optional_modifier, participantFromModel);
        }
    }

    // String formatter for  a modifier function call
    private String roleModifierFormatter(String type, String name) {
        StringBuffer sb = new StringBuffer();
        return sb.append(" ")
                .append(type)
                .append("(").append(Types.Global_RoleList)
                .append("[")
                .append(mandatoryParticipantsSol.indexOf(name))
                .append("]) ")
                .toString();
    }

    // returns a string of the array declarations about participant roles (mandatory or optional)
    private String printRoleList() {
        StringBuffer sb = new StringBuffer();

        if (mandatoryParticipantsSol.size() > 0) {
            sb.append("string [] ").append(Types.Global_RoleList).append(" = ");
            sb.append("[");
            mandatoryParticipantsSol.forEach(r -> sb.append("\"").append(r).append("\","));
            sb.deleteCharAt(sb.length() - 1); //remove last comma
            sb.append("]");
        }

        if (optionalParticipantSol.size() > 0) {
            sb.append("string [] ").append(Types.Global_OptionalList).append(" = ");
            sb.append("[");
            optionalParticipantSol.forEach(r -> sb.append("\"").append(r).append("\","));
            sb.deleteCharAt(sb.length() - 1); //remove last comma
            sb.append("]");
        }
        return sb.toString();
    }

}
