package com.unicam.chorchain.codeGenerator.solidity;

import com.unicam.chorchain.model.Instance;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SolidityInstance {
    @Getter
    private List<String> mandatoryParticipants;//List of mandatory Participants
    @Getter
    private List<String> optionalParticipants;//List of optional Participants
    @Getter
    private List<String> structVariables;
    @Getter
    private StringBuilder text;//Growing solidity text of code
    private StringBuilder body;//Growing solidity text of code
    @Getter
    @Setter
    private String startPointId; //If of the starting Point
    private Instance instance;

    public void addTxt(String text) {
        this.body.append(text);
    }

    public SolidityInstance(Instance instance) {
        this.instance = instance;
        this.text = new StringBuilder();
        this.structVariables = new ArrayList<>();
        this.body = new StringBuilder();
        this.mandatoryParticipants = instance.getMandatoryParticipants()
                .stream()
                .filter(p -> p.getUser() != null)
                .map(p -> p.getParticipant().getName())
                .collect(Collectors.toList());

        this.optionalParticipants = instance.getChoreography().getParticipants().stream().filter((p) ->
                !mandatoryParticipants.contains(p.getName())).map(p -> p.getName()).collect(Collectors.toList());
    }

    public String build() {

        //*** starts here ***/

        Struct structGlobal = Struct.builder()
                .variableMap(Types.string, "ID")
                .variableMap("State", "status")
                .name("Element")
                .build();

        Struct structElement = Struct.builder()
                .variables(structVariables)
                .name("StateMemory")
                .build();


        List<String> structs = new ArrayList<>();
        structs.add(structGlobal.toString());
        structs.add(structElement.toString());

        List<String> maps = new ArrayList<>();
        maps.add("mapping(string => uint) position;");
        maps.add("mapping(string => string) operator;");
        maps.add("mapping(string => address) roles;");
        maps.add("mapping(string => address) optionalRoles;");

        Event event1 = Event.builder()
                .name("FailedOffer")
                .parameter("uint", "time")
                .parameter("address", "sender")
                .parameter("uint", "amount")
                .parameter("bytes32", "lot")
                .parameter("string", "reason")
                .build();

        List<String> modifiers = new ArrayList<>();
        modifiers.add(
                "modifier checkMand(string storage role){\n\trequire(msg.sender == roles[role]);\n\t_;\n}\n");
        modifiers.add(
                "modifier checkOpt(string storage role){\n\trequire(msg.sender == optionalRoles[role]);\n\t_;\n}\n");
        modifiers.add(
                "modifier Owner(string memory task){\n\trequire(elements[position[task]].status == State.ENABLED);\n\t_;\n}\n");


        List<String> variables = new ArrayList<>();
        variables.add("Element[] elements;");
        variables.add("StateMemory currentMemory;");
        variables.add(printRoleList());


        Contract sol = Contract.builder()
                .pragmaVersion("^5.4.3")
                .fileName("primaProva")
                .enumElement("enum State {DISABLED, ENABLED, DONE} State s;\n")
                .mappings(maps)
                .modifiers(modifiers)
                .event("event stateChanged(uint);\n")
                .structs(structs)
                .variables(variables)
                .constructorBody(printConstructorBody())
                .function(this.body.toString())
                .custom(printOtherFunctions())
                .custom(printFunctionInit(this.getStartPointId()))
                .build();

        return text.append(sol.toString()).toString();
    }

    private String printFunctionInit(String startPointId) {
        StringBuilder sb = new StringBuilder();


        sb.append("function init() internal {\n");
        sb.append("\tbool result = true;\n");
        sb.append("\tfor (uint i = 0; i < roleList.length; i++) {\n");
        sb.append("\t\tif (roles[roleList[i]] == 0x0000000000000000000000000000000000000000) {\n");
        sb.append("\t\t\tresult = false;\n");
        sb.append("\t\t\tbreak;\n");
        sb.append("\t\t}\n");
        sb.append("\t}\n");
        sb.append("\tif (result) {\n");
        sb.append("\t//Questo è lo start\n");
        sb.append("\t\t\tenable(\"").append(startPointId).append("\");\n");
        sb.append("\t\t\t").append(startPointId.replace("-", "_")).append("();\n\t}\n}\n");
        return sb.toString();
    }


    private String printConstructorBody() {
        StringBuilder sb = new StringBuilder();
        sb.append("\tfor (uint i = 0; i < elementsID.length; i ++) {\n");
        sb.append("\t\t\telements.push(Element(elementsID[i], State.DISABLED));\n");
        sb.append("\t\t\tposition[elementsID[i]]=i;\n\t\t}\n");
        sb.append("\t\t//roles definition\n\t\t//mettere address utenti in base ai ruoli\n");
        instance.getMandatoryParticipants().forEach((p) ->
                sb.append("\t\troles[\"")
                        .append(p.getParticipant().getName())
                        .append("\"] = ")
                        .append(p.getUser().getAddress())
                        .append(";\n"));

        this.optionalParticipants.forEach((a) ->
                sb.append("\t\toptionalRoles[\"")
                        .append(a)
                        .append("\"] = ")
                        .append("0x0000000000000000000000000000000000000000")
                        .append(";\n"));
        sb.append("\t\t//enable the start process\n\t\tinit();\n");
        return sb.toString();
    }

    // returns a string of the array declarations about participant roles (mandatory or optional)
    private String printRoleList() {
        StringBuffer sb = new StringBuffer();

        if (mandatoryParticipants.size() > 0) {
            sb.append("string [] ").append(Types.Global_RoleList).append(" = ");
            sb.append("[");
            mandatoryParticipants.forEach(r -> sb.append("\"").append(r).append("\","));
            sb.deleteCharAt(sb.length() - 1); //remove last comma
            sb.append("];");
        }

        if (optionalParticipants.size() > 0) {
            sb.append("string [] ").append(Types.Global_OptionalList).append(" = ");
            sb.append("[");
            optionalParticipants.forEach(r -> sb.append("\"").append(r).append("\","));
            sb.deleteCharAt(sb.length() - 1); //remove last comma
            sb.append("];");

        }
        sb.append("\n");
        return sb.toString();
    }

    private String printOtherFunctions() {
        StringBuilder sb = new StringBuilder();
        sb.append("function subscribe_as_participant(string memory _role) public {\n");
        sb.append("\t\tif (optionalRoles[_role] == 0x0000000000000000000000000000000000000000) {\n");
        sb.append("\t\t\toptionalRoles[_role] = msg.sender;\n\t\t}\n\t}\n\n");
        sb.append("\tfunction() external payable {}\n\n");
        sb.append(
                "\tfunction enable(string memory _taskID) internal {elements[position[_taskID]].status = State.ENABLED;\n");
        sb.append("\t\temit stateChanged(counter++);\n\t}\n");
        sb.append(
                "\tfunction disable(string memory _taskID) internal {elements[position[_taskID]].status = State.DISABLED;}\n\n");
        sb.append("\tfunction done(string memory _taskID) internal {elements[position[_taskID]].status = State.DONE;}\n\n");
        sb.append("\tfunction getCurrentState() public view returns (Element[] memory, StateMemory memory){\n");
        sb.append("\t\t// emit stateChanged(elements, currentMemory);\n");
        sb.append("\t\treturn (elements, currentMemory);\n\t}\n\n");
        sb.append("\tfunction compareStrings(string memory a, string memory b) internal pure returns (bool) {\n");
        sb.append("\t\treturn keccak256(abi.encode(a)) == keccak256(abi.encode(b));\n\t}\n");
        return sb.toString();
    }


}
