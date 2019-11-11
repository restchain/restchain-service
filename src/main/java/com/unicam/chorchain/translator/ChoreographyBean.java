package com.unicam.chorchain.translator;

import com.unicam.chorchain.model.ContractObject;
import lombok.Data;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.FlowNode;
import org.camunda.bpm.model.bpmn.instance.SequenceFlow;
import org.camunda.bpm.model.xml.instance.DomElement;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;

@Data
public class ChoreographyBean {


    private BpmnModelInstance modelInstance;

    public int startint = 0;
    private String startEventAdd;
    public String request = "";
    private String choreographyFile;
    public String response = "";
    public int startCounter = 0;
    public Integer xorCounter = 0;
    public Integer parallelCounter = 0;
    public Integer eventBasedCounter = 0;
    public Integer endEventCounter = 0;
    public ArrayList<DomElement> participantsTask = new ArrayList<DomElement>();
    public ArrayList<DomElement> msgTask = new ArrayList<DomElement>();
    public ArrayList<SequenceFlow> taskIncoming = new ArrayList<SequenceFlow>();
    public ArrayList<SequenceFlow> taskOutgoing = new ArrayList<SequenceFlow>();
    public ArrayList<String> nodeSet = new ArrayList<String>();
    public ArrayList<String> participantsWithoutDuplicates = new ArrayList<String>();
    public ArrayList<String> partecipants = new ArrayList<String>();
    public ArrayList<String> functions = new ArrayList<String>();
    public List<String> elementsID = new ArrayList<String>();
    private List<String> roleFortask = new ArrayList<String>();
    public ArrayList<String> gatewayGuards = new ArrayList<String>();
    public ArrayList<String> toBlock = new ArrayList<String>();
    public List<String> tasks = new ArrayList<String>();
    public ContractObject finalContract;
    private LinkedHashMap<String, String> taskIdAndRole = new LinkedHashMap<String, String>();
    public Collection<FlowNode> allNodes;
}

