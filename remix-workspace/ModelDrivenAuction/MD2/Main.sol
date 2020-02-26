pragma experimental  ABIEncoderV2;
import "./Registry.sol";


contract Main {
    
    address auctioneer = 0xCA35b7d915458EF540aDe6068dFe2F44E8fa733c;
    
    struct StateVaribles{
        address[] participantList; //serve per mantenere la multiple instance

        
        bytes32[] itemsList;
        uint auctionId;
        uint endTime;
        uint durationTime;
        address[] participants;
        bytes32[]  domains;
        address[]   bidder;
        uint[]  bids;
    }
    
    
    
    struct StateVariblesSubProcess{
        string message;
        bool failure;
        uint code;
        uint amount;
        bytes32 domain;
        bool isParticipantExists;
    }
    
    struct participantInfos{
        uint idx;                                  // id registrar
        bool exists;                               // viene impostato a true da addRegistrar
        StateVariblesSubProcess sb;
    }
    
    mapping(address => participantInfos) public participants;
    
    /* GLOBAL VARIABLES */
    StateVaribles state;
    

    
    //Business logic contracts
    Registry registry = new Registry();    

    /* DEBUGS EVENT */
    event CurrentState(StateVaribles state);
    event CurrentSbState(StateVariblesSubProcess state);
    event Result(string message,bool failure,uint code,uint amount, bytes32 domain); //Log, rimuovere
    event TimeLog(uint now, uint endTime); //Log, rimuovere
    event AuctionOver(string message); //Log, rimuovere
    event Summarize(bytes32[] domains, address[]  bidder, uint[] bids); //Log, rimuovere
    event AlreadyMDUser(string message,address participant);

    //SEQUENCE FLOW
    
    // Start -> enable (initAuction )
    
    function b_initAuction(bytes32[] memory _itemsList) public{
        state.itemsList = _itemsList;
        state.auctionId = registry.initAuction(_itemsList);
        emit CurrentState(state);
        
        /* enable(startAuction) */
    }
    
    function c_startAuction(uint _durationTime) public {
        state.durationTime = _durationTime;
        state.endTime = registry.startAuction(_durationTime);
        emit CurrentState(state);

        /* enable(addPartipant) */
    }
    
    
    function f_checkAuctionOver(uint _amount, bytes32 _item) public{

        bytes32 domain;
        address p = msg.sender;
        
        string memory message;
        // Come si gesticono i ritorni dell funzioni???
        
        emit TimeLog(now,state.endTime); 
        if (now < state.endTime){
            (message,participants[p].sb.failure,participants[p].sb.code,participants[p].sb.amount,participants[p].sb.domain) = registry.domainOffer(_amount,_item,p);
            participants[p].sb.message = message; 
            emit Result(participants[p].sb.message,participants[p].sb.failure,participants[p].sb.code,participants[p].sb.amount,participants[p].sb.domain);
            emit CurrentSbState(participants[p].sb);

        } else {
            registry.close();
            
            //Se qui devo farlo a mano??
            emit Result("Auction is over",true,507,_amount,_item);
        }
        emit CurrentState(state);
    }
    
    
    function d_isAuctionParticipantExists (address _participant) public returns (bool){
        return registry.isParticipantExists(_participant);
    }
    
    
    function e_addAuctionParticipant(address _participant) public {
        registry.addParticipant(_participant);
        emit CurrentState(state);
        /* enable(checkAuctionOver) */
    }
    
    function subscribe(address _participant) public {
        //notifica al Registro la volonta di partecipare all'asta
        
        //Come si implementa???
    }
    
    function getCurrentBids() public returns (bytes32[] memory domains, address[]  memory bidder, uint[] memory bids) {
        (domains,bidder,bids) = registry.getSummary();
        emit Summarize(domains,bidder,bids);
    }
    

    
    /***** MD STRUCUTRE *********/
    
    //Aggiunge un nuovo partecipante - server per logica interna 
    function addMDParticipant(address p) private{
        if (participants[p].exists==false) {
            participants[p].idx    = state.participantList.length; // Posizione nella lista dei partecipanti
            participants[p].exists = true;
            
            state.participantList.push(p);
        }
    }
    
    
    function isMDParticipantExists(address p) private returns (bool exists){ 
          return participants[p].exists;
    }
    
    
    function a_welcome() public{
        address p = msg.sender;
        if (p != auctioneer){ //Inserire una condizione utile a verificare che sia un nuovo address e non un mandatory o altro
            if (!isMDParticipantExists(p)){
                addMDParticipant(p);
                emit AlreadyMDUser("Aggiunto alla struttura MD",p);
            } else {
                emit AlreadyMDUser("User già dentro struttura MD",p);
            }
        } else {
            emit AlreadyMDUser("You are Auctioneer",p);
        }
    }
    
    

}