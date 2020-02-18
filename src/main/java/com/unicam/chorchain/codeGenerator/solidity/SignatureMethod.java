package com.unicam.chorchain.codeGenerator.solidity;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.model.bpmn.instance.ExtensionElements;
import org.camunda.bpm.model.bpmn.instance.camunda.CamundaFormField;
import org.camunda.bpm.model.xml.instance.ModelElementInstance;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

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
    private String interfaceImplName;
    private String name;


    public SignatureMethod(ModelElementInstance message) {
        interfaceMethod = false;
        this.message = message;
        this.extensionElements = message.getChildElementsByType(ExtensionElements.class)
                .stream()
                .findFirst()
                .orElse(null);
        init();
        interfaceImplName = interfaceName + "Impl";
    }

    private void init() {
        String namespace = "http://chorchain.com/schema/bpmn/cc";

        ModelElementInstance attachedSignature =
                extensionElements
                        .getUniqueChildElementByNameNs(namespace, "signature");

//        List<DomElement> childElements =
//                attachedSignature
//                        .getDomElement()
//                        .getChildElements();
//
//
//        for (DomElement childElement : childElements) {

        if (attachedSignature.getAttributeValue("paramsName") != null) {
            String[] pNames = attachedSignature.getAttributeValue("paramsName").split(",");
            String[] pTypes = attachedSignature.getAttributeValue("paramsType").split(",");
            //Params
            int index = 0;
            for (String t : pTypes) {
                parameters.add(t.concat(" ").concat(pNames[index]));
                index++;
            }
        }

        if (attachedSignature.getAttributeValue("returnsName") != null) {
            String[] rNames = attachedSignature.getAttributeValue("returnsName").split(",");
            String[] rTypes = attachedSignature.getAttributeValue("returnsType").split(",");

            int index = 0;
            for (String t : rTypes) {
                returns.add(t.concat(" ").concat(rNames[index]));
                index++;
            }

        }

        //Returns


        //Interface attributes
        if (attachedSignature.getAttributeValue("interfaceMethod") != null) {
            interfaceMethod = Boolean.valueOf(attachedSignature.getAttributeValue("interfaceMethod"));
            interfaceName = attachedSignature.getAttributeValue("interfaceName");
        }

        if (attachedSignature.getAttributeValue("name") != null) {
            name = attachedSignature.getAttributeValue("name");
        }
    }


    public String getSignature() {
//        function getSummary() public returns (bytes32[] memory  domains, address[] memory bidder, uint[] memory bids);
        String result = name + "( " + String.join(",", parameters) + " ) public ";

        if (returns.size() > 0) {
            result = result + " returns (" + String.join(",", returns) + ")";
        }
        return result;
    }

    public String getCalls(String prefix) {
        StringBuilder sb = new StringBuilder();
        if (returns.size() > 0) {
            sb.append("(")
                    .append(returns.stream().map(s -> prefix + "." + lastWord(s)).collect(Collectors.joining(",")))
                    .append(") = ");
        }

        sb.append(interfaceName.toLowerCase()).append(".").append(name).append("(").append(
                parameters.stream().map(this::lastWord).collect(Collectors.joining(","))
        ).append(");");
        return sb.toString();
    }

    private String lastWord(String s) {
        return s.substring(s.lastIndexOf(' '), s.length()).trim();
    }


}
