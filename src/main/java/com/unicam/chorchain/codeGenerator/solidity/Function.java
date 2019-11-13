package com.unicam.chorchain.codeGenerator.solidity;

import lombok.Builder;


@Builder
public class Function {
    private String name;
    private String visibility;  // ci va o un enum o una classe
    private String source;
    private String target;

    public String toString() {
        StringBuffer out = new StringBuffer();
        out.append("function ").append(source);
        out.append("(").append(")" ).append(visibility).append(" {\n");
        out.append("\trequire(elements[position[\"").append(source).append("\"]].status == State.ENABLED);");
        return out.toString();
    }
}
