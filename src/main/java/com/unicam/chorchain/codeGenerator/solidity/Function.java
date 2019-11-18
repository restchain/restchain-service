package com.unicam.chorchain.codeGenerator.solidity;

import lombok.Builder;
import lombok.Singular;

import java.util.List;
import java.util.Map;


@Builder
public class Function {
    private String functionComment;
    private String name;
    private String visibility;  // ci va o un enum o una classe
    private String sourceId;
    private String target;
    private String modifier;
    private boolean payable;
    private boolean transferTo;
    @Singular
    private List<String> enables;
    @Singular
    private List<String> parameters;
    @Singular
    private List<String> varAssignments;
    @Singular
    private List<IfConstruct> ifConstructs;
    @Singular
    private Map<String, Boolean> enableAndActiveTasks;
    private String globalVariabilePrefix;

    public String toString() {
        StringBuffer out = new StringBuffer();
        out.append("\t//").append(functionComment).append("\n");
        out.append("\tfunction ").append(name);
        out.append("(");
        if (parameters != null) {
            parameters.forEach(d -> out.append(d.trim().replace("string", "string memory")));
        }
        out.append(") ").append(visibility);
        if (payable) {
            out.append(" payable ");
        }
        if (modifier != null) {
            out.append(modifier);
        }
        out.append(" {\n");
        out.append("\t\trequire(elements[position[\"").append(sourceId).append("\"]].status == State.ENABLED);\n");
        out.append("\t\tdone(\"").append(sourceId).append("\");\n");
        if (ifConstructs != null) {
            ifConstructs.forEach(d -> out.append("\t").append(d).append("\n"));
        }

        if (transferTo) {
            out.append("\t\t").append("to.transfer(msg.value);\n");
        } else {
            if (varAssignments != null) {
                varAssignments.forEach(d -> out.append("\t\t")
                        .append(globalVariabilePrefix)
                        .append(".")
                        .append(d)
                        .append(" = ")
                        .append(d)
                        .append(";\n"));
            }
        }
        if (enables != null) {
            enables.forEach(d -> out.append("\t\tenable(\"").append(d).append("\");\n"));
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
        out.append("\t}\n");
        return out.toString();
    }
}
