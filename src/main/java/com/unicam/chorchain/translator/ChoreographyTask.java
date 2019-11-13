package com.unicam.chorchain.translator;

import lombok.Getter;
import lombok.Setter;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.MessageFlow;
import org.camunda.bpm.model.bpmn.instance.Participant;
import org.camunda.bpm.model.bpmn.instance.SequenceFlow;
import org.camunda.bpm.model.xml.impl.instance.ModelElementInstanceImpl;
import org.camunda.bpm.model.xml.instance.DomElement;
import org.camunda.bpm.model.xml.instance.ModelElementInstance;

import java.util.ArrayList;

@Getter
@Setter
public class ChoreographyTask {

    private ModelElementInstanceImpl task;
    private ArrayList<SequenceFlow> incoming, outgoing;
    private Participant participantRef = null;
    private MessageFlow request = null, response = null;
    private Participant initialParticipant;
    private String id, name;
    private BpmnModelInstance model;
    private TaskType type;

    public enum TaskType {
        ONEWAY, TWOWAY
    }

    public ChoreographyTask(ModelElementInstanceImpl task, BpmnModelInstance modelInstance) {
        this.model = modelInstance;
        this.task = task;
        this.incoming = new ArrayList<SequenceFlow>();
        this.outgoing = new ArrayList<SequenceFlow>();
        this.initialParticipant = model.getModelElementById(task.getAttributeValue("initiatingParticipantRef"));
        this.id = task.getAttributeValue("id");
        this.name = task.getAttributeValue("name");
        init();
    }

    private void init() {
        for (DomElement childElement : task.getDomElement().getChildElements()) {
            String type = childElement.getLocalName();
            switch (type) {
                case "incoming":
                    incoming.add((SequenceFlow) model.getModelElementById(childElement.getTextContent()));
                    break;
                case "outgoing":
                    outgoing.add((SequenceFlow) model.getModelElementById(childElement.getTextContent()));
                    break;
                case "participantRef":
                    Participant p = model.getModelElementById(childElement.getTextContent());
                    if (!p.equals(initialParticipant)) {
                        participantRef = p;
                    }
                    break;
                case "messageFlowRef":
                    //System.out.println(task.getAttributeValue("id"));
                    MessageFlow m = model.getModelElementById(childElement.getTextContent());
                    //System.out.println("CHILD TEXT CONTENT: " + childElement.getTextContent());

                    //System.out.println("MESSAGE FLOW �: " + m.getId() + "con nome: " + m.getName() + "con messaggio: " + m.getMessage().getId());
                    if (m.getSource().getId().equals(initialParticipant.getId())) {
                        request = m;
                    } else {
                        response = m;
                    }

                    break;
                case "extensionElements":
                    break;
                default:
                    throw new IllegalArgumentException("Invalid element in the xml: " + type);

            }
        }

        if (response != null) {
            type = TaskType.TWOWAY;
        } else {
            type = TaskType.ONEWAY;
        }
    }
}