package com.unicam.chorchain.codeGenerator.solidity;

import lombok.Data;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.model.bpmn.instance.ExtensionElements;
import org.camunda.bpm.model.bpmn.instance.Message;
import org.camunda.bpm.model.bpmn.instance.camunda.CamundaFormField;
import org.camunda.bpm.model.xml.instance.DomElement;
import org.camunda.bpm.model.xml.instance.ModelElementInstance;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

//https://forum.camunda.org/t/how-to-read-custom-elements-in-delegates/975/3

@Slf4j
@Getter
public class SignatureMethod {
    private final ModelElementInstance message;
    private ExtensionElements extensionElements;
    private Collection<CamundaFormField> camundaFormFields;
    private List<String> parameters = new ArrayList<String>(0);
    private List<String> returns = new ArrayList<String>(0);
    private Boolean interfaceMethod;
    private String interfaceName;

    public SignatureMethod(ModelElementInstance message) {
        this.message = message;
        this.extensionElements =  message.getChildElementsByType(ExtensionElements.class).stream().findFirst().orElse(null);
        init();
    }

    private void init() {
        String namespace = "http://chorchain.com/schema/bpmn";

        ModelElementInstance attachedSignature =
                extensionElements
                        .getUniqueChildElementByNameNs(namespace, "signature");

        List<DomElement> childElements =
                attachedSignature
                        .getDomElement()
                        .getChildElements();


        
        for (DomElement childElement : childElements) {
            String[] pNames = childElement.getAttribute("paramsName").split(",");
            String[] pTypes = childElement.getAttribute("paramsType").split(",");
            String[] rNames = childElement.getAttribute("returnsName").split(",");
            String[] rTypes = childElement.getAttribute("returnsType").split(",");

            //Params
            int index = 0;
            for(String t : pTypes) {
                parameters.add(t.concat(" ").concat(pNames[index]));
                index++;
            }

            //Returns
            index = 0;
            for(String t : rTypes) {
                returns.add(t.concat(" ").concat(rNames[index]));
                index++;
            }

            //Interface attributes
            interfaceMethod = Boolean.valueOf(childElement.getAttribute("interfaceMethod"));
            interfaceName = childElement.getAttribute("interfaceName");
        }
    }


}
