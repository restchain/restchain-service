package com.unicam.chorchain.codeGenerator;

import com.unicam.chorchain.codeGenerator.adapter.*;
import com.unicam.chorchain.codeGenerator.solidity.AdditionalFunction;
import com.unicam.chorchain.codeGenerator.solidity.SignatureMethod;
import com.unicam.chorchain.codeGenerator.solidity.SolidityInstance;
import com.unicam.chorchain.codeGenerator.solidity.Types;
import com.unicam.chorchain.codeGenerator.solidity.element.Function;
import com.unicam.chorchain.codeGenerator.solidity.element.IfConstruct;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.model.bpmn.instance.Message;
import org.camunda.bpm.model.xml.ModelInstance;
import org.camunda.bpm.model.xml.instance.ModelElementInstance;

import java.util.*;
import java.util.stream.Collectors;

import static com.unicam.chorchain.codeGenerator.adapter.ChoreographyTaskAdapter.TaskType.ONEWAY;

/*** Provide to populate the SolidityInstance with all the needed informations ***/
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

        boolean isInsideSubChoreography = node.getDomElement()
                .getParentElement()
                .getLocalName()
                .equals("subChoregraphy");

        this.instance.addTxt(Function
                .builder()
                .functionComment("StarEvent(" + node.getName() + ") " + node.getOrigId() + " " + node.getDomElement()
                        .getParentElement()
                        .getLocalName())
                .name(processAsElementId(node.getId()))
                .sourceId(node.getId())
                .enable(nextElementId(node.getModelInstance(), node.getOutgoing().get(0)))
                .visibility(Types.visibility.PRIVATE)
                .build().toString());
    }

    @Override
    public void visitEndEvent(EndEventAdapter node) {
        log.debug("********EndEvent ***** ");
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
        List<String> listCalls = new ArrayList<>();
        List<String> enables = new ArrayList<>();
        node.getOutgoing()
                .forEach((item) -> {
                    BpmnModelAdapter element = nextElement(node.getModelInstance(), item);
                    String nextId=nextElementId(node.getModelInstance(), item);
                    if(!element.getClass().getSimpleName().equals("ChoreographyTaskAdapter")){
                        listCalls.add(nextId);
                    }
                    enables.add(nextId);
                });

        this.instance.addTxt(Function
                .builder()
                .functionComment("ParallelGateway(" + node.getName() + "): " + node.getOrigId())
                .name(processAsElementId(node.getId()))
                .enables(enables)
                .calls(listCalls)
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
            node.getOutgoing().forEach(out -> {

                        BpmnModelAdapter element = nextElement(node.getModelInstance(), node.getOutgoing().get(0));

                        if(element.getClass().getSimpleName().equals("ChoreographyTaskAdapter")){
                            ifConstructs.add(IfConstruct.builder()
                                    .condition(out.getName())
                                    .enableAndActiveTask(nextElementId(node.getModelInstance(), node.getOutgoing().get(0)), false)
                                    .build());
                        } else {
                            ifConstructs.add(IfConstruct.builder()
                                    .condition(out.getName())
                                    .enableAndActiveTask(nextElementId(node.getModelInstance(), node.getOutgoing().get(0)), true)
                                    .build());
                        }
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
                .enables(node.getOutgoing()
                        .stream()
                        .map((item) -> nextElementId(node.getModelInstance(), item))
                        .collect(Collectors.toList()))
                .name(processAsElementId(node.getId()))
                .sourceId(node.getId())
                .visibility(Types.visibility.PRIVATE)
                .build().toString());
    }

    @Override
    public void visitChoreographyTask(ChoreographyTaskAdapter node) {
        log.debug("********ChoreographyTask *****");
        SequenceFlowAdapter nextElement = (SequenceFlowAdapter) node.getOutgoing().get(0);

        Map<String, String> disabledMap = new HashMap<>();
        //Try to understand if the incoming element is a EventGateway, if yes remembers wich sid needs to be disabled
        if (previousElement(node.getModelInstance(), node.getIncoming().get(0)) instanceof EventBasedGatewayAdapter) {
            List<String> gatewayOutgoing = previousElement(node.getModelInstance(),
                    node.getIncoming().get(0)).getOutgoing()
                    .stream()
                    .map((item) -> nextElementId(node.getModelInstance(), item))
                    .collect(Collectors.toList());
            if (gatewayOutgoing.size() == 2) {
                disabledMap.put(gatewayOutgoing.get(0), gatewayOutgoing.get(1));
                disabledMap.put(gatewayOutgoing.get(1), gatewayOutgoing.get(0));
            }


        }

        Message reqMessage = node.getRequestMessage().getMessage();
        SignatureMethod reqMessageSignature = new SignatureMethod(reqMessage);
        AdditionalFunction reqMessageAdapter = new AdditionalFunction(reqMessage);

        instance.getStructVariables().addAll(reqMessageSignature.getParameters());
        instance.getStructVariables().addAll(reqMessageSignature.getReturns());

        if (reqMessageSignature.getInterfaceMethod()) {
            instance.elabInterface(reqMessageSignature);
        }


        /** ONE WAY **/
        if (node.getType() == ONEWAY) {


            boolean payableReq = reqMessage.getName().contains("payment");
//            if (!payableReq) {
//                addParamToGlobalSolVariables(reqMessage.getName());
//            }


            //TODO works on this, change the approach regarding how to populate the getParams..
            getParameters(reqMessage.getName());

//            List<String> params = new ArrayList<>(0);
//            String tmp = getParameters(reqMessage.getName());
//            if (!tmp.equals("")) {
//                params.add(tmp);
//            } else {
//                params.addAll(reqMessageAdapter.getParameters());
//            }


            //Add to global
            //params.forEach(p -> instance.getStructVariables().add(p.trim()));


            this.instance.addTxt(Function.builder()
                    .functionComment("Task(" + node.getName() + "): " + node.getId() + " - TYPE: " + node.getType() + " - " + reqMessage
                            .getName())
                    .name(processAsElementId(reqMessage.getId()))
                    .visibility(Types.visibility.PUBLIC)
                    .payable(payableReq)
                    .parameters(reqMessageSignature.getParameters())
//                    .parameters(reqMessageSignature.getReturns())
                    .modifier(getParticipantModifier(node.getInitialParticipant().getName()))
                    .sourceId(reqMessage.getId())
                    .globalVariabilePrefix(Types.GlobaStateMemory_varName)
                    .varAssignments(reqMessageSignature.getParameters())
                    .bodyString(reqMessageSignature.getCalls(Types.GlobaStateMemory_varName))
//                    .bodyStrings(reqMessageAdapter.getFunctionCalls()
//                            .stream()
//                            .map(s -> s.concat(";"))
//                            .collect(Collectors.toList()))
                    .transferTo(reqMessage.getName().contains("payment"))
                    .disable(disabledMap.get(reqMessage.getId()))
                    .enableAndActiveTask(nextElementId(node.getModelInstance(), node.getOutgoing().get(0)),
                            nextElement.isTargetGatewayOrNot())
                    .build().toString());
            this.instance.addTxt("\n\n");

            this.instance.addTxt(reqMessageAdapter.getFunctions().stream().collect(Collectors.joining("\n")));

        } else {

            /** TWOWAY **/

            Message respMessage = node.getResponseMessage().getMessage();
            SignatureMethod respMessageSignature = new SignatureMethod(respMessage);
            AdditionalFunction respMessageAdapter = new AdditionalFunction(respMessage);

            instance.getStructVariables().addAll(respMessageSignature.getParameters());
            instance.getStructVariables().addAll(respMessageSignature.getReturns());

            if (reqMessageSignature.getInterfaceMethod()) {
                instance.elabInterface(respMessageSignature);
            }


            boolean payableResp = node.getResponseMessage().getMessage().getName().contains("payment");
            if (!payableResp) {
//                addParamToGlobalSolVariables(respMessage.getName());
            }
            boolean payableReq = reqMessage.getName().contains("payment");
            if (!payableReq) {
//                addParamToGlobalSolVariables(reqMessage.getName());
            }

            //Upper part - requestMessage
            this.instance.addTxt(Function.builder()
                    .functionComment("Task(" + node.getName() + "): " + node.getId() + " - TYPE: " + node.getType())
                    .name(processAsElementId(reqMessage.getId()))
                    .visibility(Types.visibility.PUBLIC)
                    .payable(payableReq)
                    .parameters(reqMessageSignature.getParameters())
//                    .parameters(reqMessageSignature.getReturns())
                    .modifier(getParticipantModifier(node.getParticipantRef().getName()))
                    .sourceId(reqMessage.getId())
                    .globalVariabilePrefix(Types.GlobaStateMemory_varName)
                    .varAssignments(reqMessageSignature.getParameters())
                    .bodyString(reqMessageSignature.getCalls(Types.GlobaStateMemory_varName))
                    .disable(disabledMap.get(reqMessage.getId()))
                    .enable(respMessage.getId())
                    .build().toString());
            this.instance.addTxt("\n\n");

            //lower part - requestMessage
            this.instance.addTxt(Function.builder()
                    .functionComment("Task(" + node.getName() + "): " + node.getId() + " - TYPE: " + node.getType())
                    .name(processAsElementId(respMessage.getId()))
                    .parameters(respMessageSignature.getParameters())
//                    .parameters(respMessageSignature.getReturns())
                    .visibility(Types.visibility.PUBLIC)
                    .payable(payableResp)
                    .modifier(getParticipantModifier(node.getParticipantRef().getName()))
                    .globalVariabilePrefix(Types.GlobaStateMemory_varName)
                    .varAssignments(respMessageSignature.getParameters())
                    .bodyString(respMessageSignature.getCalls(Types.GlobaStateMemory_varName))
                    .sourceId(respMessage.getId())
                    .disable(disabledMap.get(respMessage.getId()))
                    .enableAndActiveTask(nextElementId(node.getModelInstance(), node.getOutgoing().get(0)),
                            nextElement.isTargetGatewayOrNot()).build().toString());
            this.instance.addTxt("\n\n");

        }
    }

    @Override
    public void visitSubChoreographyTask(SubChoreographyTaskAdapter node) {
        log.debug("********SubChoreographyTask *****");

//        this.instance.addTxt(Function
//                .builder()
//                .functionComment("SubChoreography(" + node.getName() + "): " + node.getOrigId())
//                .enables(node.getOutgoing()
//                        .stream()
//                        .map((item) -> nextElementId(node.getModelInstance(), item))
//                        .collect(Collectors.toList()))
//                .name(processAsElementId(node.getId()))
//                .sourceId(node.getId())
//                .visibility(Types.visibility.PRIVATE)
//                .build().toString());

    }

    // Performs a - replacing in _
    private String processAsElementId(String id) {
        this.instance.addElementId(id);
        return id.replace("-", "_");
    }

    //Returns the next elementId pointed by the passed sequenceFlow
    private String nextElementId(ModelInstance instance, BpmnModelAdapter startNode) {
        BpmnModelAdapter targetElement =nextElement(instance,startNode);

        if (targetElement instanceof SubChoreographyTaskAdapter) {
            return ((SubChoreographyTaskAdapter) targetElement).getStartEvent().getSource().getId();
        } else if (targetElement instanceof ChoreographyTaskAdapter) {
            return ((ChoreographyTaskAdapter) targetElement).getRequestMessage().getMessage().getId();
        } else {
            return targetElement.getId();
        }
    }

    //Returns the next elementId pointed by the passed sequenceFlow
    private BpmnModelAdapter nextElement(ModelInstance instance, BpmnModelAdapter startNode) {
        SequenceFlowAdapter sequenceFlow = (SequenceFlowAdapter) startNode;
        ModelElementInstance targetElementId = instance
                .getModelElementById(sequenceFlow.getTargetRefId());
        BpmnModelAdapter targetElement = Factories.bpmnModelFactory.create(targetElementId);
        //Se il targetElement è di tipo  ChoreographyTaskAdapter allora prendi l'id  del messaggio di Request
        //altrimenti in tutti gli altri casi prendi l'id dell'elemento stesso;
        return  targetElement;
    }

    //Returns the next elementId pointed by the passed sequenceFlow
    private BpmnModelAdapter previousElement(ModelInstance instance, BpmnModelAdapter startNode) {
        SequenceFlowAdapter sequenceFlow = (SequenceFlowAdapter) startNode;
        ModelElementInstance sourceId = instance
                .getModelElementById(sequenceFlow.getSourceId());
        BpmnModelAdapter targetElement = Factories.bpmnModelFactory.create(sourceId);
        //Se il targetElement è di tipo  ChoreographyTaskAdapter allora prendi l'id  del messaggio di Request
        //altrimenti in tutti gli altri casi prendi l'id dell'elemento stesso;
        return targetElement;
    }


    // returns a List of all params name contained in the passed signature (msg)
    private Collection<String> getParamsList(String msg) {
        log.debug("signature {}:", msg);
        List<String> res = new ArrayList<>();
        String add = "";
        String n = msg.replace("string", "").replace("uint", "").replace("bool", "").replace(" ", "");
        String r = n.replace(")", "");
        String[] t = r.split("\\(");
        if (t.length > 1) {
            String[] m = t[1].split(",");

            res.addAll(Arrays.asList(m));

        }
        return res;
    }

    // returns a modifier function call depending of the participant role
    private String getParticipantModifier(String participantName) {
        //If participantFromRole is contained in tha Mandatory list is Mandatory else is Optional
        if (this.instance.getMandatoryParticipants().contains(participantName)) {
            return roleModifierFormatter(Types.Mandatory_modifier,
                    participantName,
                    Types.Global_RoleList,
                    instance.getMandatoryParticipants());
        } else {
            return roleModifierFormatter(Types.Optional_modifier,
                    participantName,
                    Types.Global_OptionalList,
                    instance.getOptionalParticipants());
        }
    }

    // String formatter for  a modifier function call
    private String roleModifierFormatter(String type, String name, String modifierName, List<String> participantList) {
        StringBuffer sb = new StringBuffer();
        return sb.append(" ")
                .append(type)
                .append("(").append(modifierName)
                .append("[")
                .append(participantList.indexOf(name))
                .append("]) ")
                .toString();
    }

    // use to obtain the parameters present in the function's signature
    private String getParameters(String messageName) {
        String[] parsedMsgName = messageName.split("\\(");
        if (parsedMsgName.length > 1)
            return parsedMsgName[1].replace(")", "   ");

        return "";
    }


    //Add "name" to the Solidity global variables declaration
    private void addParamToGlobalSolVariables(String name) {
        String r = name.replace(")", "");
        String[] t = r.split("\\(");
        if (t.length > 1) {
            String[] m = t[1].split(",");
            for (String param : m) {
                instance.getStructVariables().add(param.trim());
            }
        }
    }
}
