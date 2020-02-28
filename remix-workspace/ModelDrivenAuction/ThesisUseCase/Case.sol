
pragma solidity ^0.5.3;
pragma experimental ABIEncoderV2;

contract rev1{


	/* constructor */ 
	constructor () public {

		for (uint i = 0; i < elementsID.length; i ++) {
			elements.push(Element(elementsID[i], State.DISABLED));
			position[elementsID[i]]=i;
		}
		//roles definition
		//mettere address utenti in base ai ruoli
		roles["Registry"] = 0xCA35b7d915458EF540aDe6068dFe2F44E8fa733c;
		roles["Auctioneer"] = 0xCA35b7d915458EF540aDe6068dFe2F44E8fa733c;
		optionalRoles["Participant2"] = 0x0000000000000000000000000000000000000000;
		optionalRoles["Participant1"] = 0x0000000000000000000000000000000000000000;
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
		uint256 auctionId;
		uint256 oCodeP1;
		uint256 oCodeP2;
		bytes32[]  domainList;
		address[]  participants;
		bytes32[]  itemsList;
		address oAddP2;
		bytes32 oDomainP1;
		bytes32 oDomainP2;
		address jAddrP2;
		address jAddrP1;
		bool oFailP1;
		bool oFailP2;
		uint256 endTime;
		uint256 duration;
		string  oMessageP1;
		uint[]  amounts;
		string  oMessageP2;
		address oAddP1;
		bool isOver2;
		bool isOver1;
		uint256 oValueP1;
		bytes32[]  itemList;
		uint256 oValueP2;
	}


	/* Enums */
	enum State {DISABLED, ENABLED, DONE} State s;

	/* Variables */
	address payable public owner;


	string [] roleList = ["Registry","Auctioneer"];
	string [] optionalList = ["Participant2","Participant1"];

	IRegistryImpl iregistry= new IRegistryImpl();


	Element[] elements;
	StateMemory currentMemory;
	uint counter;
	string [] elementsID = [
		"Message_06wzatw",	"Message_149c4zv",	"ExclusiveGateway_0uxrv04",	"Message_0lbh34o",	"Message_04rqloh",	"Message_1kw5wo9",	"ExclusiveGateway_0k015dt",	"Message_1aua4l6",	"Message_1rxaze1",	"Message_1g3cklt",	"Message_07eohqm",	"ParallelGateway_0dzfiy0",	"Message_1dm5hwq",	"Message_0mkzly1",	"StartEvent_091f5ys",	"ParallelGateway_1p3cekg",	"Message_1qc0ry7",	"ExclusiveGateway_1tocbxb",	"ExclusiveGateway_1rxvphn",	"ExclusiveGateway_0raza8h",	"EndEvent_1b14i9a",	"ExclusiveGateway_1gnzbnk"
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

		//StarEvent() StartEvent_091f5ys choreography
	function StartEvent_091f5ys() private {
		require(elements[position["StartEvent_091f5ys"]].status == State.ENABLED);
		done("StartEvent_091f5ys");
		enable("Message_1aua4l6");
	}

	//Task(init): ChoreographyTask_182htvs - TYPE: ONEWAY - init (bytes32[] memory domainList) returns (uint256 auctionId)
	function Message_1aua4l6(bytes32[] memory domainList) public checkMand(roleList[0])  {
		require(elements[position["Message_1aua4l6"]].status == State.ENABLED);
		done("Message_1aua4l6");
		currentMemory.domainList = domainList;
		(currentMemory.auctionId) = iregistry.init(domainList);
		enable("ParallelGateway_1p3cekg");
		ParallelGateway_1p3cekg();
	}



	//ParallelGateway(): ParallelGateway_1p3cekg
	function ParallelGateway_1p3cekg() private {
		require(elements[position["ParallelGateway_1p3cekg"]].status == State.ENABLED);
		done("ParallelGateway_1p3cekg");
		enable("Message_1qc0ry7");
		enable("Message_1g3cklt");
	}

	//Task(join): ChoreographyTask_0p4ft52 - TYPE: ONEWAY - joinP2(address jAddrP2)
	function Message_1qc0ry7(address jAddrP2) public checkMand(roleList[0])  {
		require(elements[position["Message_1qc0ry7"]].status == State.ENABLED);
		done("Message_1qc0ry7");
		currentMemory.jAddrP2 = jAddrP2;
		iregistry.joinP2(jAddrP2);
		enable("ExclusiveGateway_1tocbxb");
		ExclusiveGateway_1tocbxb();
	}



	//ExclusiveGateway():ExclusiveGateway_1tocbxb Dir: Converging
	function ExclusiveGateway_1tocbxb() private {
		require(elements[position["ExclusiveGateway_1tocbxb"]].status == State.ENABLED);
		done("ExclusiveGateway_1tocbxb");
		enable("Message_0mkzly1");
	}

	//Task(start): ChoreographyTask_0265lxt - TYPE: ONEWAY - start (uint256 duration) returns (uint256 endTime)
	function Message_0mkzly1(uint256 duration) public checkMand(roleList[0])  {
		require(elements[position["Message_0mkzly1"]].status == State.ENABLED);
		done("Message_0mkzly1");
		currentMemory.duration = duration;
		(currentMemory.endTime) = iregistry.start(duration);
		enable("ParallelGateway_0dzfiy0");
		ParallelGateway_0dzfiy0();
	}



	//ParallelGateway(): ParallelGateway_0dzfiy0
	function ParallelGateway_0dzfiy0() private {
		require(elements[position["ParallelGateway_0dzfiy0"]].status == State.ENABLED);
		done("ParallelGateway_0dzfiy0");
		enable("ExclusiveGateway_0raza8h");
		enable("ExclusiveGateway_1gnzbnk");
		ExclusiveGateway_0raza8h();
		ExclusiveGateway_1gnzbnk();
	}

	//ExclusiveGateway():ExclusiveGateway_0raza8h Dir: Converging
	function ExclusiveGateway_0raza8h() private {
		require(elements[position["ExclusiveGateway_0raza8h"]].status == State.ENABLED);
		done("ExclusiveGateway_0raza8h");
		enable("Message_149c4zv");
	}

	//Task(checkTime): ChoreographyTask_09kwqu7 - TYPE: ONEWAY - checkAuctionOverP1() returns (bool isOver1)
	function Message_149c4zv() public checkMand(roleList[1])  {
		require(elements[position["Message_149c4zv"]].status == State.ENABLED);
		done("Message_149c4zv");
		(currentMemory.isOver1) = iregistry.checkAuctionOverP1();
		enable("ExclusiveGateway_1rxvphn");
		ExclusiveGateway_1rxvphn();
	}



	//ExclusiveGateway():ExclusiveGateway_1rxvphn Dir: Diverging
	function ExclusiveGateway_1rxvphn() private {
		require(elements[position["ExclusiveGateway_1rxvphn"]].status == State.ENABLED);
		done("ExclusiveGateway_1rxvphn");
		if (currentMemory.isOver1==false) {
			enable("Message_04rqloh");
		}

		if (currentMemory.isOver1==true) {
			enable("Message_04rqloh");
		}

	}

	//Task(New Activity): ChoreographyTask_1sxh6yx - TYPE: ONEWAY - offer1 (bytes32 oDomainP1,uint256 oValueP1, address oAddP1) returns (string memory oMessageP1,bool oFailP1,uint256 oCodeP1)
	function Message_04rqloh(address oAddP1, bytes32 oDomainP1, uint256 oValueP1) public checkMand(roleList[0])  {
		require(elements[position["Message_04rqloh"]].status == State.ENABLED);
		done("Message_04rqloh");
		currentMemory.oAddP1 = oAddP1;
		currentMemory.oDomainP1 = oDomainP1;
		currentMemory.oValueP1 = oValueP1;
		(currentMemory.oCodeP1,currentMemory.oFailP1,currentMemory.oMessageP1) = iregistry.offerP1(oAddP1,oDomainP1,oValueP1);
		enable("Message_06wzatw");
	}



	//Task(New Activity): ChoreographyTask_1hc4gby - TYPE: ONEWAY - showBids1() returns  (bytes32[] memory itemsList, address[] memory participants, uint[] memory amounts)
	function Message_06wzatw() public checkMand(roleList[0])  {
		require(elements[position["Message_06wzatw"]].status == State.ENABLED);
		done("Message_06wzatw");
		(currentMemory.amounts,currentMemory.itemList,currentMemory.participants) = iregistry.showBids1();
		enable("ExclusiveGateway_0raza8h");
		ExclusiveGateway_0raza8h();
	}



	//ExclusiveGateway():ExclusiveGateway_0uxrv04 Dir: Converging
	function ExclusiveGateway_0uxrv04() private {
		require(elements[position["ExclusiveGateway_0uxrv04"]].status == State.ENABLED);
		done("ExclusiveGateway_0uxrv04");
		enable("Message_07eohqm");
	}

	//Task(summary): ChoreographyTask_1ek1cs3 - TYPE: ONEWAY - showSummary() returns (bytes32[] memory itemsList,address[] memory participants,uint[] memory amounts)
	function Message_07eohqm() public checkMand(roleList[0])  {
		require(elements[position["Message_07eohqm"]].status == State.ENABLED);
		done("Message_07eohqm");
		(currentMemory.amounts,currentMemory.itemsList,currentMemory.participants) = iregistry.showSummary();
		enable("Message_1dm5hwq");
	}



	//Task(closing): ChoreographyTask_0gvxeqt - TYPE: ONEWAY - close()
	function Message_1dm5hwq() public checkMand(roleList[0])  {
		require(elements[position["Message_1dm5hwq"]].status == State.ENABLED);
		done("Message_1dm5hwq");
		iregistry.close();
		enable("EndEvent_1b14i9a");
		EndEvent_1b14i9a();
	}


	//EndEvent(): EndEvent_1b14i9a
	function EndEvent_1b14i9a() private {
		require(elements[position["EndEvent_1b14i9a"]].status == State.ENABLED);
		done("EndEvent_1b14i9a");
	}

	//ExclusiveGateway():ExclusiveGateway_1gnzbnk Dir: Converging
	function ExclusiveGateway_1gnzbnk() private {
		require(elements[position["ExclusiveGateway_1gnzbnk"]].status == State.ENABLED);
		done("ExclusiveGateway_1gnzbnk");
		enable("Message_1kw5wo9");
	}

	//Task(chekTime): ChoreographyTask_0amjs1m - TYPE: ONEWAY - checkAuctionOverP2() returns (bool isOver2)
	function Message_1kw5wo9() public checkMand(roleList[1])  {
		require(elements[position["Message_1kw5wo9"]].status == State.ENABLED);
		done("Message_1kw5wo9");
		(currentMemory.isOver2) = iregistry.checkAuctionOverP2();
		enable("ExclusiveGateway_0k015dt");
		ExclusiveGateway_0k015dt();
	}



	//ExclusiveGateway():ExclusiveGateway_0k015dt Dir: Diverging
	function ExclusiveGateway_0k015dt() private {
		require(elements[position["ExclusiveGateway_0k015dt"]].status == State.ENABLED);
		done("ExclusiveGateway_0k015dt");
		if (currentMemory.isOver2==false) {
			enable("Message_1rxaze1");
		}

		if (currentMemory.isOver2==true) {
			enable("Message_1rxaze1");
		}

	}

	//Task(New Activity): ChoreographyTask_0hghogr - TYPE: ONEWAY - offer2 (bytes32 oDomainP2,uint256 oValueP2, address oAddP2) returns (string memory oMessageP2,bool oFailP2,uint256 oCodeP2)
	function Message_1rxaze1(address oAddP2, bytes32 oDomainP2, uint256 oValueP2) public checkMand(roleList[0])  {
		require(elements[position["Message_1rxaze1"]].status == State.ENABLED);
		done("Message_1rxaze1");
		currentMemory.oAddP2 = oAddP2;
		currentMemory.oDomainP2 = oDomainP2;
		currentMemory.oValueP2 = oValueP2;
		(currentMemory.oCodeP2,currentMemory.oFailP2,currentMemory.oMessageP2) = iregistry.offerP2(oAddP2,oDomainP2,oValueP2);
		enable("Message_0lbh34o");
	}



	//Task(New Activity): ChoreographyTask_12m5io9 - TYPE: ONEWAY - showBids2() returns  (bytes32[] memory itemsList, address[] memory participants, uint[] memory amounts)
	function Message_0lbh34o() public checkMand(roleList[0])  {
		require(elements[position["Message_0lbh34o"]].status == State.ENABLED);
		done("Message_0lbh34o");
		(currentMemory.amounts,currentMemory.itemList,currentMemory.participants) = iregistry.showBids2();
		enable("ExclusiveGateway_1gnzbnk");
		ExclusiveGateway_1gnzbnk();
	}



	//Task(join): ChoreographyTask_0lt07q0 - TYPE: ONEWAY - joinP1(address jAddrP1)
	function Message_1g3cklt(address jAddrP1) public checkMand(roleList[0])  {
		require(elements[position["Message_1g3cklt"]].status == State.ENABLED);
		done("Message_1g3cklt");
		currentMemory.jAddrP1 = jAddrP1;
		iregistry.joinP1(jAddrP1);
		enable("ExclusiveGateway_1tocbxb");
		ExclusiveGateway_1tocbxb();
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
				enable("StartEvent_091f5ys");
				StartEvent_091f5ys();
				emit functionDone("Contract creation"); 
		}
	}



}//Contract end

import "./Registry.sol";


/*Interface generation*/
contract IRegistry{
	function joinP1( address jAddrP1 ) public ;
	function init( bytes32[] memory domainList ) public  returns (uint256 auctionId);
	function checkAuctionOverP2(  ) public  returns (bool isOver2);
	function offerP2( address oAddP2,bytes32 oDomainP2,uint256 oValueP2 ) public  returns (uint256 oCodeP2,bool oFailP2,string memory oMessageP2);
	function joinP2( address jAddrP2 ) public ;
	function checkAuctionOverP1(  ) public  returns (bool isOver1);
	function showSummary(  ) public  returns (uint[] memory amounts,bytes32[] memory itemsList,address[] memory participants);
	function close(  ) public ;
	function showBids2(  ) public  returns (uint[] memory amounts,bytes32[] memory itemList,address[] memory participants);
	function start( uint256 duration ) public  returns (uint256 endTime);
	function offerP1( address oAddP1,bytes32 oDomainP1,uint256 oValueP1 ) public  returns (uint256 oCodeP1,bool oFailP1,string memory oMessageP1);
	function showBids1(  ) public  returns (uint[] memory amounts,bytes32[] memory itemList,address[] memory participants);
}//Interface End

import "./Registry.sol";

/*InterfaceImpl generation, provides function stubs*/
contract IRegistryImpl is IRegistry{
    
    Registry r = new Registry();

	function joinP1()  returns (address JAddrP1){
          r.addParticipant(msg.sender);

	}

	function init( bytes32[] memory domainList ) public  returns (uint256 auctionId){

		return r.initAuction(domainList);

	}

	function checkAuctionOverP2(  ) public  returns (bool isOver2){

        return r.checkAuctionIsOver(); 

	}

	function offerP2( address oAddP2,bytes32 oDomainP2,uint256 oValueP2 ) public  returns (uint256 oCodeP2,bool oFailP2,string memory oMessageP2){

          return r.domainOffer(oValueP2,oDomainP2,oAddP2);

	}

	function joinP2( ) public  returns (address JAddrP2){
	        
          r.addParticipant(msg.sender);

	}

	function checkAuctionOverP1(  ) public  returns (bool isOver1){

		return r.checkAuctionIsOver(); 

	}

	function showSummary(  ) public  returns (uint[] memory amounts,bytes32[] memory itemsList,address[] memory participants){

		return r.getSummary();

	}

	function close(  ) public {


	}

	function showBids2(  ) public  returns (uint[] memory amounts,bytes32[] memory itemList,address[] memory participants){

		return r.getSummary();//stub generated -- insert here your code 

	}

	function start( uint256 duration ) public  returns (uint256 endTime){

		return r.startAuction(duration);

	}

	function offerP1( address oAddP1,bytes32 oDomainP1,uint256 oValueP1 ) public  returns (uint256 oCodeP1,bool oFailP1,string memory oMessageP1){

          return r.domainOffer(oValueP1,oDomainP1,oAddP1);

	}

	function showBids1(  ) public  returns (uint[] memory amounts,bytes32[] memory itemList,address[] memory participants){

		return r.getSummary();

	}

}//InterfaceImplementation End

