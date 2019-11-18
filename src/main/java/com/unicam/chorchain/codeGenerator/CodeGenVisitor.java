package com.unicam.chorchain.codeGenerator;

import com.unicam.chorchain.codeGenerator.adapter.*;
import com.unicam.chorchain.codeGenerator.solidity.Function;
import com.unicam.chorchain.codeGenerator.solidity.IfConstruct;
import com.unicam.chorchain.codeGenerator.solidity.SolidityInstance;
import com.unicam.chorchain.codeGenerator.solidity.Types;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.model.xml.ModelInstance;
import org.camunda.bpm.model.xml.instance.ModelElementInstance;

import java.util.*;
import java.util.stream.Collectors;

import static com.unicam.chorchain.codeGenerator.adapter.ChoreographyTaskAdapter.TaskType.ONEWAY;

@Slf4j
public class CodeGenVisitor implements Visitor {

    @Getter
    private SolidityInstance instance;

    public CodeGenVisitor(SolidityInstance solidityInstance) {
        this.instance = solidityInstance;
    }

    @Override
    public void visit(BpmnModelAdapter node) {
    }

    @Override
    public void visitStartEvent(StartEventAdapter node) {
        if (this.instance.getStartPointId() == null) {
            this.instance.setStartPointId(node.getId());
        }
        log.debug("********StartEvent *****");
        this.instance.addTxt(Function
                .builder()
                .functionComment("StarEvent(" + node.getName() + ") " + node.getOrigId())
                .name(processAsElementId(node.getId()))
                .sourceId(node.getId())
                .enable(nextElementId(node.getModelInstance(), node.getOutgoing().get(0)))
                .visibility(Types.visibility.PRIVATE)
                .build().toString());
    }

    @Override
    public void visitEndEvent(EndEventAdapter node) {
        log.debug("********EndEvent *****");
        this.instance.addTxt(Function
                .builder()
                .functionComment("EndEvent(" + node.getName() + "): " + node.getOrigId())
                .name(processAsElementId(node.getId()))
                .sourceId(node.getId())
                .visibility(Types.visibility.PRIVATE)
                .build().toString());
    }

    @Override
    public void visitParallelGateway(ParallelGatewayAdapter node) {
        log.debug("********ParallelGateway *****");
        this.instance.addTxt(Function
                .builder()
                .functionComment("ParallelGateway(" + node.getName() + "): " + node.getOrigId())
                .name(processAsElementId(node.getId()))
                .enables(node.getOutgoing().stream().map(BpmnModelAdapter::getId).collect(Collectors.toList()))
                .sourceId(node.getId())
                .visibility(Types.visibility.PRIVATE)
                .build().toString());
    }

    @Override
    public void visitExclusiveGateway(ExclusiveGatewayAdapter node) {
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

        this.instance.addTxt(Function
                .builder()
                .functionComment("ExclusiveGateway(" + node.getName() + "):" + node.getOrigId() + " Dir: " + node.getDirection())
                .name(processAsElementId(node.getId()))
                .sourceId(node.getId())
                .visibility(Types.visibility.PRIVATE)
                .ifConstructs(ifConstructs)
                .enables(toEnable)
                .build().toString());
    }

    @Override
    public void visitEventBasedGateway(EventBasedGatewayAdapter node) {
        log.debug("********EventBasedGateway *****");
        this.instance.addTxt(Function
                .builder()
                .functionComment("EventBasedGateway(" + node.getName() + "): " + node.getOrigId())
                .enables(node.getOutgoing().stream().map(BpmnModelAdapter::getId).collect(Collectors.toList()))
                .name(processAsElementId(node.getId()))
                .sourceId(node.getId())
                .visibility(Types.visibility.PRIVATE)
                .build().toString());
    }

    @Override
    public void visitChoreographyTask(ChoreographyTaskAdapter node) {
        log.debug("********ChoreographyTask *****");
        SequenceFlowAdapter nextElement = (SequenceFlowAdapter) node.getOutgoing().get(0);


        if (node.getType() == ONEWAY) {
            boolean payableReq = node.getRequestMessage().getMessage().getName().contains("payment");
            if (!payableReq) {
                addGlobal(node.getRequestMessage().getMessage().getName());
            }


            this.instance.addTxt(Function.builder()
                    .functionComment("Task(" + node.getName() + "): " + node.getId() + " - TYPE: " + node.getType() + " - " + node
                            .getRequestMessage()
                            .getMessage()
                            .getName())
                    .name(processAsElementId(node.getRequestMessage().getMessage().getId()))
                    .visibility(Types.visibility.PUBLIC)
                    .payable(payableReq)
                    .parameter(getParameters(node.getRequestMessage().getMessage().getName()))
                    .modifier(getParticipantModifier(node.getParticipantRef().getName()))
                    .sourceId(node.getRequestMessage().getMessage().getId())
                    .globalVariabilePrefix(Types.GlobaStateMemory_varName)
                    .varAssignments(getParamsList(node.getRequestMessage().getMessage().getName()))
                    .transferTo(node.getRequestMessage().getMessage().getName().contains("payment"))
                    .enableAndActiveTask(nextElement.getTargetRefId(), nextElement.isTargetGatewayOrNot())
                    .build().toString());
            this.instance.addTxt("\n\n");

        } else {

            boolean payableResp = node.getResponseMessage().getMessage().getName().contains("payment");
            if (!payableResp) {
                addGlobal(node.getResponseMessage().getMessage().getName());
            }
            boolean payableReq = node.getRequestMessage().getMessage().getName().contains("payment");
            if (!payableReq) {
                addGlobal(node.getRequestMessage().getMessage().getName());
            }

            //Upper part - requestMessage
            this.instance.addTxt(Function.builder()
                    .functionComment("Task(" + node.getName() + "): " + node.getId() + " - TYPE: " + node.getType())
                    .name(processAsElementId(node.getRequestMessage().getMessage().getId()))
                    .visibility(Types.visibility.PUBLIC)
                    .payable(payableReq)
                    .parameter(getParameters(node.getRequestMessage().getMessage().getName()))
                    .modifier(getParticipantModifier(node.getParticipantRef().getName()))
                    .sourceId(node.getRequestMessage().getMessage().getId())
                    .globalVariabilePrefix(Types.GlobaStateMemory_varName)
                    .varAssignments(getParamsList(node.getRequestMessage().getMessage().getName()))
                    .enable(node.getResponseMessage().getMessage().getId())
                    .build().toString());
            this.instance.addTxt("\n\n");

            //lower part - requestMessage
            this.instance.addTxt(Function.builder()
                    .functionComment("Task(" + node.getName() + "): " + node.getId() + " - TYPE: " + node.getType())
                    .name(processAsElementId(node.getResponseMessage().getMessage().getId()))
                    .parameter(getParameters(node.getResponseMessage().getMessage().getName()))
                    .visibility(Types.visibility.PUBLIC)
                    .payable(payableResp)
                    .modifier(getParticipantModifier(node.getParticipantRef().getName()))
                    .globalVariabilePrefix(Types.GlobaStateMemory_varName)
                    .sourceId(node.getResponseMessage().getMessage().getId())
                    .varAssignments(getParamsList(node.getResponseMessage().getMessage().getName()))
                    .enableAndActiveTask(nextElement.getTargetRefId(),
                            nextElement.isTargetGatewayOrNot()).build().toString());
            this.instance.addTxt("\n\n");

        }
    }

    // Performs a - replacing in _
    private String processAsElementId(String id) {
        this.instance.getElementsId().add(id);
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
        if (this.instance.getMandatoryParticipants().contains(participantFromModel)) {
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
                .append(this.instance.getMandatoryParticipants().indexOf(name))
                .append("]) ")
                .toString();
    }

    // Function for gettingi all the parameters presents in function signature,
    private static String getParameters(String messageName) {
        String[] parsedMsgName = messageName.split("\\(");
        return parsedMsgName[1].replace(")", "   ");
    }

    private void addGlobal(String name) {
        String r = name.replace(")", "");
        String[] t = r.split("\\(");
        String[] m = t[1].split(",");
        for (String param : m) {
            instance.getStructVariables().add(param);
        }
    }


}
