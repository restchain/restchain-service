pragma solidity ^0.5.0;
import "./tt3.sol";
import "./LotAuction.sol";

contract Registry is IRegistry {
    
    uint public startingBidValue=4;
    
    struct auctionStruct{
        bool active;                    //flag che indica se l'asta è in corso o meno
        uint id;                        //Id dell'asta (progressivo)
        uint endTime;                   //Timestamp
        uint startBlock;                //block number
        uint bidding;                   //base d'asta
        uint maxBidValue;               //massimo valore spendibile
        uint maxLots;                   //massimo numero di lotti a cui partecipare
        address[] participants;         //Lista dei partecipanti    TODO capire se necessaria
        bytes32[] lots;                 //Lista dei lotti
        mapping(bytes32 => lot)  lotsMap;  //Mapping dei lotti d'asta, contiene coppia chiave valore (item in byte32,istanzaAsta)
        mapping(address => participant)  participantsMap; //Mapping dei lotti d'asta, contiene coppia chiave valore (item in byte32,istanzaAsta)
    }
   
    // *link al contratto Asta*
    struct lot{
        bytes32 item; //usato come proof 
        LotAuction lotA;
    }

    //partecipante contiene info:
    struct participant{
        uint id;                                   // id del partecipant
        uint currentCumulativeBiddedValue;         // somma corrente delle offerte
        bytes32[] currentLots;                     // item a cui sta concorrendo
        bool exists;                               // viene impostato a true da addPartecipant, meccanismo usato per evitare di inserire duplicati
        mapping(bytes32 => uint) lotsListBids; // Liste delle offerte fatte per ogni lotto
    }
    
    auctionStruct auction;


    /* EVENTS */
    event AddedParticipant(address participant); //Log, rimuovere
    event ParticipantExists(bool exists,address participant); //Log, rimuovere


    /* ABSTRACT METHODS */
    function initAuction(bytes32[] memory _itemsList) public returns (uint){
        _createAuction(_itemsList);
        _buildLotAuction(_itemsList);
        return auction.id;
    }
    
    function addParticipant(address _participant) public {
        _addParticipant(_participant);
    }
    
    function isParticipantExists(address _participant) public returns (bool){
        return _getRegistrarsExists(_participant);
    }
    
    //Avvia l'asta
    function startAuction(uint _durationTime) public returns (uint endTime) {
        return _startAuction(_durationTime);
    }

    // ritorna (messaggio, fallimento {T|F} , codice di ritorno)
    function domainOffer(uint _amount, bytes32 _item, address _participant)  external returns (string memory message,bool failure,uint code) {
   
        (message,failure,code) = _canOffer(_amount,_item, _participant);
        return (message,failure,code);
    }
    
    function getCurrentBids() public {}
    
    function terminate(uint _auctionId) public {}
    function close() public { }
    
    function getSummary() public {}
    function subscribe(address _participant) public {}


    /********************* INTERNAL *******************************************/
    function _createAuction(bytes32[] memory _itemsList) private {
        auction.lots=_itemsList; //domains list
        auction.endTime=0; //not yet started , default endTime
        auction.startBlock=block.number;
        auction.maxBidValue=100;
        auction.maxLots=2;
        auction.id =auction.id+1;
    }
    

    function _buildLotAuction(bytes32[] memory _itemsList) private {
        for(uint i = 0; i <_itemsList.length; i++){
            bytes32 _item = _itemsList[i];
            _createLotAuction(_item,auction.bidding);
        }
    }
    
    //crea l'asta per il lotto
    function _createLotAuction(bytes32 _item,uint _bidding) private{
            LotAuction lotA=new LotAuction(_bidding);
            auction.lotsMap[_item] = lot(_item,lotA); //aggiungo una nuova coppia lotto,istanzaAsta
    }
    
    // Aggiunge un partecipante al record dei partecipanti
    /* implementare il pattern suggerito per gestire la ricerca dell'indirizzo ed evitare una duplicazione */
    //https://ethereum.stackexchange.com/questions/27510/solidity-list-contains
    function _addParticipant(address _participant) private{
        auction.participantsMap[_participant].id   = auction.participants.length;   // Posizione nella lista dei partecipanti
        auction.participantsMap[_participant].exists = true;                              //Prova dell'esistenza
        auction.participants.push(_participant);
        emit AddedParticipant(_participant);
    }
    
        
    //Avvia l'asta
    function _startAuction(uint _durationTime) private returns (uint) {
        auction.endTime=now+_durationTime; //Imposta la data di scadenza
        auction.active = true; //imposta il flag 'asta attiva'
        return auction.endTime;
    }

    function _getRegistrarsExists(address _participant) private  returns(bool) {
        emit ParticipantExists(auction.participantsMap[_participant].exists,_participant);
        return auction.participantsMap[_participant].exists;
    }


    //Procedi con l'offerta
    function _canOffer(uint _amount, bytes32 _item, address _participant)  private  returns (string memory message,bool failure,uint code)  {
        if(_getRegistrarsExists(_participant) == true){
            if (auction.endTime < now){ //Controlla che l'asta non è giunta al termine
                //emit FailedOffer(now,msg.sender,_amount,_item,'AuctionIsOver') ;
                return ("Asta terminata",true,500);
            } else {
                if (( auction.participantsMap[_participant].lotsListBids[_item] == 0 && //se non ho mai fatto puntate su Item
                    (auction.participantsMap[_participant].currentLots.length+1) > auction.maxLots //allora controlla che aggiungendo virtualmente un lotto tra le puntate, il numero dei lotto a cui partecipare sia ok
                     ) || ( auction.participantsMap[_participant].currentLots.length > auction.maxLots) // se ho già puntato quel lotto verifico che il limete dei lotto sia ok
                    ){
                      //emit FailedOffer(now,msg.sender,_amount,_item,'MaxLotsExceeded ');    
                      return ("Troppi domini",true,501);
                    } else {
                        if (auction.participantsMap[_participant].currentCumulativeBiddedValue - auction.participantsMap[_participant].lotsListBids[_item] + _amount <= auction.maxBidValue){ //Se la somma delle puntate non eccede i limiti
                            if (auction.participantsMap[_participant].lotsListBids[_item] == 0 ){
                                auction.participantsMap[_participant].currentLots.push(_item);
                            }
                            //Puoi Puntare su uno specifico lotto -TAsk offerta presente nel contratto Auction
                            LotAuction lotA  = auction.lotsMap[_item].lotA;
                            
                            return lotA.offer(_amount,_participant);       
                        } else {
                            //emit FailedOffer(now,msg.sender,_amount,_item,'AmountExceeded'); 
                            return ("Token finiti",true,502);
                        }        
                    } 
            } 
      } else {
          return("Partecipant non iscritto",false,503);
     }
    
    }
}