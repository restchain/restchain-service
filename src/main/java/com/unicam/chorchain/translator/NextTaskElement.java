package com.unicam.chorchain.translator;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.camunda.bpm.model.bpmn.instance.EndEvent;
import org.camunda.bpm.model.bpmn.instance.Gateway;
import org.camunda.bpm.model.bpmn.instance.SequenceFlow;
import org.camunda.bpm.model.xml.instance.ModelElementInstance;
import org.mapstruct.ap.internal.model.common.ModelElement;


public  class NextTaskElement {
    private SequenceFlow element;

    public NextTaskElement(SequenceFlow element) {
        this.element = element;
    }

    public String getId(){
        return this.element.getId();
    }

    public  boolean isGatewayOrEndEvent(){
        ModelElementInstance e = element.getModelInstance().getModelElementById(this.element.getAttributeValue("targetRef"));
        return e instanceof Gateway || e instanceof EndEvent;
    }

    public SequenceFlow get(){
        return this.element;
    }


    public String getTargetId(){
        return this.element.getAttributeValue("targetRef");
    }
}
