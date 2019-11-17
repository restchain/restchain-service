package com.unicam.chorchain.codeGenerator.solidity;

import lombok.Builder;
import lombok.Singular;
import lombok.extern.slf4j.Slf4j;

import javax.validation.constraints.NotEmpty;
import java.util.Collection;

@Builder
@Slf4j
public class Contract {
    @NotEmpty
    private String pragmaVersion;
    @NotEmpty
    private String fileName;
//    @NotEmpty
//    private String constructor;
    @NotEmpty
    private String constructorBody;

    private @Singular
    Collection<Struct> structs;
    private @Singular
    Collection<Mapping> mappings;
    private @Singular
    Collection<Enum> enumElements;
    private @Singular
    Collection<Event> events;
    private
    Collection<Function> functions;
    private @Singular
    Collection<String> customBodyStrings;

//    public void addStruct(Struct s1) {
//        structs.add(s1);
//    }

    public String toString() {
        StringBuffer out = new StringBuffer();

        out.append("\n");
        out.append("pragma solidity ").append(pragmaVersion).append(";\n");
        out.append("pragma experimental ABIEncoderV2;\n");
        out.append("\n");

        out.append("contract ").append(fileName).append("{\n");
        out.append("\n\n");

        out.append("constructor() public {\n");
        out.append("\t//constructor body\n");
        if (constructorBody != null) {
            out.append("\t").append(constructorBody).append("\n");
        }
        out.append("}\n");
        out.append("\n");

        out.append("uint counter;\n");
        out.append("event stateChanged(uint);\n");

        if (structs != null) {
            out.append("//Mappings\n");
            mappings.forEach((s) -> out.append(s.toString()).append("\n"));
            out.append("\n");
        }

        if (structs != null) {
            out.append("//Structs\n");
            structs.forEach((s) -> out.append(s.toString()).append("\n"));
            out.append("\n");
        }

        if (enumElements != null) {
            out.append("//Enums\n");
            enumElements.forEach((s) -> out.append(s.toString()).append("\n"));
        }

        if (events != null) {
            out.append("//Events\n");
            events.forEach((s) -> out.append(s.toString()).append("\n"));
        }

        if (functions != null) {
            out.append("//Functions\n");
            functions.forEach((s) -> out.append(s.toString()).append("\n"));
        }

        if (customBodyStrings != null) {
            out.append("//CustomStringsBody\n");
            customBodyStrings.forEach((s) -> out.append(s).append("\n"));
        }


        out.append("\n\n");
        out.append("}");
        out.append("//Contract end\n");
        out.append("\n");

        return out.toString();
    }

}
