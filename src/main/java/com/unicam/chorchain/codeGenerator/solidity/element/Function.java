package com.unicam.chorchain.codeGenerator.solidity.element;

import lombok.Builder;
import lombok.Singular;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Builder
public class Function {
    private String functionComment;
    private String name;
    private String visibility;  // ci va o un enum o una classe
    private String sourceId;
    private String target;
    private String modifier;
    @Singular
    private List<String> bodyStrings;
    private boolean payable;
    private boolean transferTo;
    @Singular
    private List<String> enables;
    @Singular
    private List<String> calls;
    private String disable;
    @Singular
    private List<String> parameters;
    @Singular
    private List<String> varAssignments;
    @Singular
    private List<IfConstruct> ifConstructs;
    @Singular
    private Map<String, Boolean> enableAndActiveTasks;
    private String globalVariabilePrefix;
    private boolean internal;

    public String toString() {
        StringBuffer out = new StringBuffer();
        if (functionComment != null) {
            out.append("\t//").append(functionComment).append("\n");
        }
        out.append("\tfunction ").append(name);
        out.append("(");
        if (parameters != null) {
//            out.append(parameters.stream()
            out.append(parameters.stream().filter((p -> !p.contains("address msg.sender")))
                    .map(String::trim).collect(Collectors.joining(", ")));
        }
        out.append(") ").append(visibility);
        if (payable) {
            out.append(" payable ");
        }
        if (modifier != null) {
            out.append(modifier);
        }
        out.append(" {\n");
        if (!internal) {
            out.append("\t\trequire(elements[position[\"").append(sourceId).append("\"]].status == State.ENABLED);\n");
            out.append("\t\tdone(\"").append(sourceId).append("\");\n");
        }
        if (ifConstructs != null) {
            ifConstructs.forEach(d -> out.append("\t").append(d).append("\n"));
        }

        if (transferTo) {
            out.append("\t\t").append("to.transfer(msg.value);\n");
        } else {
            if (varAssignments != null) {

                varAssignments.stream()
                        .filter(p -> !p.contains("address msg.sender"))
                        .forEach(d -> out.append("\t\t")
                                .append(globalVariabilePrefix)
                                .append(".")
                                .append(d.substring(d.lastIndexOf(' '), d.length()).trim())
                                .append(" = ")
                                .append(d.substring(d.lastIndexOf(' '), d.length()).trim())
                                .append(";\n"));
            }
        }
        if (bodyStrings != null) {
            bodyStrings.forEach(d -> out.append("\t\t").append(d == null ? "//empty" : d).append("\n"));
        }
        if (disable != null) {
            out.append("\t\tdisable(\"").append(disable).append("\");\n");
        }
        if (enables != null) {
            enables.forEach(d -> out.append("\t\tenable(\"").append(d).append("\");\n"));
        }

        if (calls != null) {
            calls.forEach(d -> out.append("\t\t").append(d.replace("-", "_")).append("();").append("\n"));
        }
        if (enableAndActiveTasks != null) {
            enableAndActiveTasks.forEach(
                    (k, v) -> {
                        out.append("\t\tenable(\"").append(k).append("\");\n");
                        if (v) {
                            out.append("\t\t").append(k.replace("-", "_")).append("();").append("\n");
                        }
                    }
            );


        }
        out.append("\t}\n\n");
        return out.toString();
    }

}


