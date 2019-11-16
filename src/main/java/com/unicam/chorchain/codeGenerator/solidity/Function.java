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
    @Singular
    private List<String> enables;
    @Singular
    private List<String> parameters;
    @Singular
    private Map<String, Boolean> taskEnableActive_s;
    private String globalVariabilePrefix;

    public String toString() {
        StringBuffer out = new StringBuffer();
        out.append("//").append(functionComment).append("\n");
        out.append("function ").append(name);
        out.append("(").append(") ").append(visibility).append(" {\n");
        out.append("\trequire(elements[position[\"").append(sourceId).append("\"]].status == State.ENABLED);\n");
        out.append("\tdone(").append(sourceId).append("\");\n");
        if (parameters != null) {
            parameters.forEach(d -> out.append("\t")
                    .append(globalVariabilePrefix)
                    .append(".")
                    .append(d)
                    .append("=")
                    .append(d)
                    .append(";\n"));
        }
        if (enables != null) {
            enables.forEach(d -> out.append("\tenable(").append(d).append("\");\n"));
        }
        if (taskEnableActive_s != null) {
            taskEnableActive_s.forEach(
                    (k, v) -> {
                        out.append("\tenable(").append(k).append("\");\n");
                        if (v) {
                            out.append("\t").append(k.replace("-", "_")).append("()").append("\n");
                        }
                    }
            );


        }
        out.append("}\n");
        return out.toString();
    }
}
