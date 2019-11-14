package com.unicam.chorchain.codeGenerator.solidity;

import lombok.Builder;


@Builder
public class Function {
    private String functionComment;
    private String name;
    private String visibility;  // ci va o un enum o una classe
    private String source;
    private String target;

    public String toString() {
        StringBuffer out = new StringBuffer();
        out.append("//").append(functionComment).append("\n");
        out.append("function ").append(source);
        out.append("(").append(")" ).append(visibility).append(" {\n");
        out.append("\trequire(elements[position[\"").append(source).append("\"]].status == State.ENABLED);\n");
        out.append("\tdone(").append(source).append("\");");
        out.append("}\n");
        return out.toString();
    }
}
