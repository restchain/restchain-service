pragma solidity >=0.4.22 <0.6.0;
import "./LotAuction.sol";

/*

Glossario

Auctioneer - banditore, avvia la vendita di un lotto
Bid - rilancio
Lotti d'asta (LotsAuction) - prodotti presenti nell'asta
HammerPrice - prezzo di aggiudicazione
Invenduto - non è stato raggiunto il prezzo minimo (base d'asta)
Lotto – Un oggetto singolo o un gruppo di oggetti che vengono offerti dalla casa d’aste come unica unità.
Prezzo di Riserva – É il prezzo minimo definito in via confidenziale tra la casa d’aste e il committente della vendita per l’aggiudicazione dell’opera.
Bidding - Base d'asta

=============================
Istruzioni per test su Remix
=============================

List di Domini  di prova in byte32 

domainList: 
["0x504950504f000000000000000000000000000000000000000000000000000000", "0x706c75746f000000000000000000000000000000000000000000000000000000","0x706c75744f000000000000000000000000000000000000000000000000000000"]


* STEP *
---------

1 - createAuction  (domainList)
2 - addPartecipant (copia e incolla un address preso dal selettore Account di remix)
3-  startAuction (in secondi es. 30)
4 - cambiare account tramite il selottore e verificare che sia lo stesso utilizzato al passo 2
5 - offer(amount, domain_in_bytes32)   - esegui un'offerta su di un elemnto preso da domaniList

*/

contract Auctioneer {
    
    
    constructor() public{
        owner = msg.sender;
    }
    
    address payable public owner;
    
    
    /* STRUCTS */
    //Fare con traudtture
    
    struct auctionStruct{
        bool active;                    //flag che indica se l'asta è in corso o meno
        uint id;                        //Id dell'asta (progressivo)
        uint endTime;                   //Timestamp
        uint startBlock;                //block number
        uint bidding;                   //base d'asta
        uint maxBidValue;               //massimo valore spendibile
        uint maxLots;                   //massimo numero di lotti a cui partecipare
        address[] partecipants;         //Lista dei partecipanti    TODO capire se necessaria
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
    
    
    /* EVENTS */
    
    //Fare con traudtture
    
    event AuctionCreated(uint id,bytes32[] lots);
    event AuctionStarted(bytes32[] lots,uint endTime, uint startBlock);
    event AuctionClosed(uint id, uint startTime, uint endTime);
    event SubscribeToAuction(uint time,address partecipantAddress);
    event FailedOffer(uint time,address sender,uint amount,bytes32 lot, string reason);
    event AddedPartecipant(address _partecipantAddress);
    event LogUint(uint value,string logMessage);
    event LogBool(bool value,string logMessage);
    
    struct StateVaribles{
        auctionStruct auction;
    }
    
    /* ISTANCES */
    StateVaribles state;  //rappresenta lo stato globale
    
    
    
    /* MODIFIERS*/
    //Fare con traudtture doppio click e associ i contsraints al task, valutare se mostrarli nel modello
    
    //verifica che il chiamente sia l'Auctioneer

    
    modifier OnlyPartecipant(address _participant){
        require(state.auction.participantsMap[_participant].exists==true,'Partecipant doesn\'t exists');        //Controlla che il partecipante esista
        _;
    }
    
    //Esempio interessante "Modificatore con emissione di eventi"
    //https://ethereum.stackexchange.com/questions/57160/can-i-emit-event-in-function-modifier
    
    //Verifica che l'asta si attiva ma scaduta
    modifier AuctionIsOver(){
       require(state.auction.endTime < now, 'Auction still active'); //check that auction is over
       _;
    }
    
    modifier LotExists(bytes32 _item){
       require(state.auction.lotsMap[_item].item != 0,'Partecipant doesn\'t exists'); //check that auction is over
       _;
    }
    
    modifier AuctionIsActive(){
        require(state.auction.active == true, 'Auction should be active'); //Check  that auction is still active   
        _;
    }
    
    modifier PartecipantNotExsist(address _partecipant){
        require(state.auction.participantsMap[_partecipant].exists==false,'Partecipant already exists');        //Controlla che il partecipante non sia già inserito
        _;
    }
   
    
    /* TASKS */
    
    function  createAuction(bytes32[] memory _itemsList) public OnlyAuctioneer {
        _crateAuction(_itemsList);
        _buildLotAuction(_itemsList);
    }
    
     modifier OnlyAuctioneer {
        require(msg.sender == owner, 'Not the Auctioneer');
        _;
    }
    
    function addParticipant(address _partecipant) public OnlyAuctioneer PartecipantNotExsist(_partecipant){
        _addPartecipant(_partecipant);
    }
    
    //Avvia l'asta
    function startAuction(uint _durationTime) public OnlyAuctioneer {
        _startAuction(_durationTime); //imposta il flag 'asta attiva'
        notifyAuctionStarted();
    }
    
    /* END TASK */
    
    /* Preset functions */
    function _crateAuction(bytes32[] memory _itemsList) private {
        state.auction.lots=_itemsList; //domains list
        state.auction.endTime=0; //not yet started , default endTime
        state.auction.startBlock=block.number;
        state.auction.maxBidValue=100;
        state.auction.maxLots=2;
        state.auction.id =state.auction.id+1;
    }
    

    function _buildLotAuction(bytes32[] memory _itemsList) private {
        for(uint i = 0; i <_itemsList.length; i++){
            bytes32 _item = _itemsList[i];
            _createLotAuction(_item,state.auction.bidding);
        }
    }
    
    //crea l'asta per il lotto
    function _createLotAuction(bytes32 _item,uint _bidding) private{
            LotAuction lotA=new LotAuction(_bidding);
            state.auction.lotsMap[_item] = lot(_item,lotA); //aggiungo una nuova coppia lotto,istanzaAsta
    }

    //Emette un evento di Asta Creata riportando gli Item dell'asta
    function notifyAuctionInvitation() public{
        emit AuctionCreated(state.auction.id,state.auction.lots);
    }
    
    //Emette un evento di richiesta sottoscrizione all'asta
    function notifySubscribeToAuction() public{
        emit SubscribeToAuction(now,msg.sender);
    }
    
    //Emette notifica di Asta avviata
    function notifyAuctionStarted() public{
        emit AuctionStarted(state.auction.lots,state.auction.endTime,state.auction.startBlock);
    }
    
    // Aggiunge un partecipante al record dei partecipanti
    /* implementare il pattern suggerito per gestire la ricerca dell'indirizzo ed evitare una duplicazione */
    //https://ethereum.stackexchange.com/questions/27510/solidity-list-contains
    function _addPartecipant(address _partecipant) private{
        state.auction.participantsMap[_partecipant].id   = state.auction.partecipants.length;   // Posizione nella lista dei partecipanti
        state.auction.participantsMap[_partecipant].exists = true;                              //Prova dell'esistenza
        state.auction.partecipants.push(_partecipant);
    }
    
    //Avvia l'asta
    function _startAuction(uint _durationTime) public OnlyAuctioneer {
        state.auction.endTime=now+_durationTime; //Imposta la data di scadenza
        state.auction.active = true; //imposta il flag 'asta attiva'
    }

    //Procedi con l'offerta
    function lotOffer(uint _amount, bytes32 _item) public OnlyPartecipant(msg.sender) AuctionIsActive LotExists(_item) {
        canOffer(_amount,_item,msg.sender);
    }
    
    function canOffer(uint _amount, bytes32 _item, address _partecipant) private AuctionIsActive OnlyPartecipant(_partecipant) LotExists(_item) {
        if (state.auction.endTime < now){ //Controlla che l'asta non è giunta al termine
            emit FailedOffer(now,msg.sender,_amount,_item,'AuctionIsOver');
        } else {
            if (( state.auction.participantsMap[_partecipant].lotsListBids[_item] == 0 && //se non ho mai fatto puntate su Item
                (state.auction.participantsMap[_partecipant].currentLots.length+1) > state.auction.maxLots //allora controlla che aggiungendo virtualmente un lotto tra le puntate, il numero dei lotto a cui partecipare sia ok
                 ) || ( state.auction.participantsMap[_partecipant].currentLots.length > state.auction.maxLots) // se ho già puntato quel lotto verifico che il limete dei lotto sia ok
                ){
                  emit FailedOffer(now,msg.sender,_amount,_item,'MaxLotsExceeded ');    
                } else {
                    if (state.auction.participantsMap[msg.sender].currentCumulativeBiddedValue - state.auction.participantsMap[msg.sender].lotsListBids[_item] + _amount <= state.auction.maxBidValue){ //Se la somma delle puntate non eccede i limiti
                        if (state.auction.participantsMap[_partecipant].lotsListBids[_item] == 0 ){
                            state.auction.participantsMap[msg.sender].currentLots.push(_item);
                        }
                        //Puoi Puntare su uno specifico lotto -TAsk offerta presente nel contratto Auction
                        LotAuction lotA  = state.auction.lotsMap[_item].lotA;
                        lotA.offer(_amount,msg.sender);          
                    } else {
                        emit FailedOffer(now,msg.sender,_amount,_item,'AmountExceeded'); 
                    }        
                } 
        }
    }
    
    
    
    
    //Chiude l'asta in corso
    function f_closeAuction() public AuctionIsOver OnlyAuctioneer{
        for(uint i = 0; i<state.auction.lots.length; i++){
            if(state.auction.lots[i] != 0)
                closeLotAuction(i);
        }
        emit AuctionClosed(state.auction.id,state.auction.endTime,state.auction.startBlock);
        state.auction.endTime=0;
        state.auction.active=false;        
        state.auction.lots =new bytes32[](0);
    } 
    
    //Chiude e pulisce i lotti d'asta
    function closeLotAuction(uint index) private{
        LotAuction(state.auction.lotsMap[state.auction.lots[index]].lotA).close(); //Chiude un lotto d'asta
        delete state.auction.lotsMap[state.auction.lots[index]];  //rimuove il lott d'asta dal mapping dei lotti
        delete state.auction.lots[index]; //rimuove l'item dal vettore degli item correnti
    }
}



