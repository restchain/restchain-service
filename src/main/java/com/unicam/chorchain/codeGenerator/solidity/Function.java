package com.unicam.chorchain.codeGenerator.solidity;

import lombok.Builder;
import lombok.Singular;

import java.util.List;


@Builder
public class Function {
    private String functionComment;
    private String name;
    private String visibility;  // ci va o un enum o una classe
    private String source;
    private String target;
    @Singular
    private List<String> enables;

    public String toString() {
        StringBuffer out = new StringBuffer();
        out.append("//").append(functionComment).append("\n");
        out.append("function ").append(source);
        out.append("(").append(")" ).append(visibility).append(" {\n");
        out.append("\trequire(elements[position[\"").append(source).append("\"]].status == State.ENABLED);\n");
        out.append("\tdone(").append(source).append("\");\n");
        if (enables != null) {
            enables.forEach(d -> out.append("\tenable(").append(d).append("\");\n"));
        }
        out.append("}\n");
        return out.toString();
    }
}
