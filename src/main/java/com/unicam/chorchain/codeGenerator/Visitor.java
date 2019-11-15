package com.unicam.chorchain.codeGenerator;

import com.unicam.chorchain.codeGenerator.adapter.*;
import org.camunda.bpm.model.bpmn.instance.*;

public interface Visitor {
    String visit(BpmnModelAdapter node);
    String visitStartEvent(StartEventAdapter node);
    String visitEndEvent(EndEventAdapter node);
    String visitParallelGateway(ParallelGatewayAdapter node);
    String visitExclusiveGateway(ExclusiveGatewayAdapter node);
    String visitEventBasedGateway(EventBasedGatewayAdapter node);
    String visitChoreographyTask(ChoreographyTaskAdapter node);
}
