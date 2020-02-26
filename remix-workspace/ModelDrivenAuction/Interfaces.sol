pragma solidity ^0.5.0;




contract IRegistry{
    function initAuction(bytes32[] memory _itemsList) public returns (uint) ;
    function startAuction(uint _durationTime) public returns (uint);
    function isParticipantExists(address _participant) public returns (bool);
    function addParticipant(address _participant) public;
    function domainOffer(uint _amount, bytes32 _item,address _participant)  external returns (string memory message,bool failure,uint code); // ritorna (messaggio, fallimento {T|F} , codice di ritorno)
    function getCurrentBids() public;
    function terminate(uint _auctionId) public;
    function getSummary() public;
    function close() public;
    function subscribe(address _participant) public;
}


contract IParticipant {
}