pragma experimental  ABIEncoderV2;
import "./Impl.sol";


contract AuctionSequenceFlow {
    
    struct StateVaribles{
        bytes32[] itemsList;
        uint auctionId;
        uint endTime;
        uint durationTime;
        address[] participants;
    }
    
    /* GLOBAL VARIABLES */
    StateVaribles state;
    
    //Business logic contracts
    Registry registry = new Registry();    



    /* DEBUGS EVENT */
    event CurrentState(StateVaribles state);
    event Result(string message,bool failure,uint code); //Log, rimuovere
    event TimeLog(uint now, uint endTime); //Log, rimuovere
    event AuctionOver(string message); //Log, rimuovere

    //SEQUENCE FLOW
    
    // Start -> enable (initAuction )
    
    function initAuction(bytes32[] memory _itemsList) public{
        state.itemsList = _itemsList;
        state.auctionId = registry.initAuction(_itemsList);
        emit CurrentState(state);
        
        /* enable(startAuction) */
    }
    
    function startAuction(uint _durationTime) public {
        state.durationTime = _durationTime;
        state.endTime = registry.startAuction(_durationTime);
        emit CurrentState(state);

        /* enable(addPartipant) */
    }
    
    
    function checkAuctionOver(uint _amount, bytes32 _item) public{
        string memory message;
        bool failure;
        uint code;
        emit TimeLog(now,state.endTime); 
        if (now < state.endTime){
            (message,failure,code) = registry.domainOffer(_amount,_item,msg.sender);
            emit Result(message,failure,code);
        } else {
            registry.close();
            emit AuctionOver("auction Over");
        }
        emit CurrentState(state);
    }
    
    
    function isParticipantExists (address _participant) public returns (bool){
        return registry.isParticipantExists(_participant);
    }
    
    
    function addParticipant(address _participant) public {
        registry.addParticipant(_participant);
        emit CurrentState(state);
        /* enable(checkAuctionOver) */
    }
    
    function subscribe(address _participant) public {
        
    }
    
    
    
    
    
}