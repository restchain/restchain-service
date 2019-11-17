package com.unicam.chorchain.codeGenerator.solidity;

import lombok.Builder;
import lombok.Singular;

import java.util.Map;


@Builder
public class IfConstruct {
    private String condition;
    @Singular
    private Map<String, Boolean> enableAndActiveTasks;

    public String toString() {
        StringBuffer out = new StringBuffer();
        out.append("if (")
                .append(Types.GlobaStateMemory_varName)
                .append(".")
                .append(condition.replace("\n", " "))
                .append(") {\n");
        if (enableAndActiveTasks != null) {
            enableAndActiveTasks.forEach(
                    (k, v) -> {
                        out.append("\t\tenable(").append(k).append("\");\n");
                        if (v) {
                            out.append("\t\t").append(k.replace("-", "_")).append("()").append("\n");
                        }
                    }
            );


        }
        out.append("\t}\n");
        return out.toString();
    }
}

