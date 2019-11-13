package com.unicam.chorchain.codeGenerator.solidity;

import lombok.Builder;
import lombok.Singular;

import javax.validation.constraints.NotEmpty;
import java.util.List;
import java.util.Map;

@Builder
public class Struct {
    @NotEmpty
    String name;
    @NotEmpty
    @Singular
    Map<String, String> variableMaps;
    @Singular
    List<String> variables;

    public String toString() {
        StringBuffer out = new StringBuffer();
        out.append("struct ").append(name).append(" {\n");
        if (variableMaps != null) {
            variableMaps.forEach((type, name) -> out.append("\t").append(type).append(" ").append(name).append(";\n"));
        }
        if (variables != null) {
            variables.forEach((var) -> out.append("\t").append(var.trim()).append(";\n"));
        }
        out.append("}\n");
        return out.toString();
    }
}
