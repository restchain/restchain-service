pragma solidity ^0.5.3;
pragma experimental ABIEncoderV2;

contract sub1{


	/* constructor */ 
	constructor () public {

		for (uint i = 0; i < elementsID.length; i ++) {
			elements.push(Element(elementsID[i], State.DISABLED));
			position[elementsID[i]]=i;
		}
		//roles definition
		//mettere address utenti in base ai ruoli
		roles["Participant 1"] = 0x535CCa8697F29DaC037a734D6984eeD7EA943A85;
		roles["Participant 2"] = 0x535CCa8697F29DaC037a734D6984eeD7EA943A85;
		//enable the start process
		_init();
	}
	
	/* Mappings */
	mapping(string => uint) position;
	mapping(string => string) operator;
	mapping(string => address) roles;
	mapping(string => address) optionalRoles;

	/* Structs */
	struct Element {
		string ID;
		State status;
	}

	struct StateMemory {
		uint256 ggg;
		uint256 ddd;
	}


	/* Enums */
	enum State {DISABLED, ENABLED, DONE} State s;

	/* Variables */
	address payable public owner;


	Element[] elements;
	StateMemory currentMemory;
	uint counter;
	string [] roleList = ["Participant 1","Participant 2"];

	string [] elementsID = [
		"EndEvent_03xyksv",	"StartEvent_1hyj61g",	"Message_0qwi6i3",	"Message_050enkq"
	];

	/* Events */ 
	event stateChanged(uint);

	event functionDone(string);

	/* Modifiers */ 
	modifier checkMand(string storage role){
		require(msg.sender == roles[role]);
		_;
	}

	modifier checkOpt(string storage role){
		require(msg.sender == optionalRoles[role]);
		_;
	}

	modifier Owner(string memory task){
		require(elements[position[task]].status == State.ENABLED);
		_;
	}

	/* Functions */

		//StarEvent() StartEvent_1hyj61g choreography
	function StartEvent_1hyj61g() private {
		require(elements[position["StartEvent_1hyj61g"]].status == State.ENABLED);
		done("StartEvent_1hyj61g");
		enable("Message_050enkq");
	}

	//Task(New Activity): ChoreographyTask_09fseop - TYPE: ONEWAY - aa
	function Message_050enkq() public checkMand(roleList[1])  {
		require(elements[position["Message_050enkq"]].status == State.ENABLED);
		done("Message_050enkq");
		enable("Message_0qwi6i3");
	}



	//Task(New Activity): ChoreographyTask_0t1wjds - TYPE: ONEWAY - fff
	function Message_0qwi6i3() public checkMand(roleList[0])  {
		require(elements[position["Message_0qwi6i3"]].status == State.ENABLED);
		done("Message_0qwi6i3");
		enable("EndEvent_03xyksv");
		EndEvent_03xyksv();
	}



	//EndEvent(): EndEvent_03xyksv
	function EndEvent_03xyksv() private {
		require(elements[position["EndEvent_03xyksv"]].status == State.ENABLED);
		done("EndEvent_03xyksv");
	}


	/* Custom */
		function subscribe_as_participant(string memory _role) public {
		if (optionalRoles[_role] == 0x0000000000000000000000000000000000000000) {
			optionalRoles[_role] = msg.sender;
		}
	}

	function() external payable {}

	function enable(string memory _taskID) internal {elements[position[_taskID]].status = State.ENABLED;
		emit stateChanged(counter++);
	}
	function disable(string memory _taskID) internal {elements[position[_taskID]].status = State.DISABLED;}

	function done(string memory _taskID) internal {elements[position[_taskID]].status = State.DONE;
		emit functionDone(_taskID);
	}
	function getCurrentState() public view returns (Element[] memory, StateMemory memory){
		// emit stateChanged(elements, currentMemory);
		return (elements, currentMemory);
	}

	function compareStrings(string memory a, string memory b) internal pure returns (bool) {
		return keccak256(abi.encode(a)) == keccak256(abi.encode(b));
	}

	function _init() internal {
		bool result = true;
		for (uint i = 0; i < roleList.length; i++) {
			if (roles[roleList[i]] == 0x0000000000000000000000000000000000000000) {
				result = false;
				break;
			}
		}
		if (result) {
		//Questo è lo start
				enable("StartEvent_1hyj61g");
				StartEvent_1hyj61g();
				emit functionDone("Contract creation"); 
		}
	}



}//Contract end

/*Interface generation*/
contract IParticipant1{
	function aa( uint256 ddd ) public ;
	function fff( uint256 ggg ) public ;
}//Interface End


/*InterfaceImpl generation, provides function stubs*/
contract IParticipant1Impl is IParticipant1{

	function aa( uint256 ddd ) public {

		//stub generated -- insert here your code 

	}

	function fff( uint256 ggg ) public {

		//stub generated -- insert here your code 

	}

}//InterfaceImplementation End