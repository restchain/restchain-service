# Configurazione
## Ganache
E' una blockchain di sviluppo che viene installata in locale e serve a simulare il comportamento della blockchain. E' completamente configurabile.
 
Installare globalmente ``ganache-cli`` :
```
yarn global add ganache-cli
```

 
# Avvio 
## Avvio di Ganache
Il servizio API necessità di una blockchain, nella fase di sviluppo e demo, tipicamente, se non diversamente specificato nel file di configurazione si impiega la blockchain di sviluppo ``ganache``.

Per avviare tale blockchain è stato predisposto all'interno del progetto un file eseguibile che si occupa di configurare ed eseguire la nostra istanza locale di ganache.

Per l'avvio dell'istanza locale di ganache sarà sufficiente lanciare il comando:

```
$ ./ganache-chorchain.sh
```

## Avvio servizio API
Per far partire il progetto è sufficiente lanciare:

```
$ mvn spring-boot:run
```

Secondo le impostazioni presenti nel file di configurazione (src/main/resoruces/application.properties) si avrà che :

 - il servizio gira sulla porta 8085
 - la directory di upload dei file è /upload_dir
 - il db (per ora) è h1 con tutte le impostazioni (persistente su file)
  



Per testare gli endpoint in fase di sviluppo si consiglia l'utilizzo di ``httpie`` https://httpie.org qui abbreviato ad http.

> NOTA: Verificare che la versione del pacchetto `solidity` installato in locale corrisponda alla versione utilizzata all'interno del `servizio` e quindi quella che praticamente viene scritta nello smarta contract alla prima riga
>
>```
>pragma solidity ^0.5.17;
>```
>
>Se le due versioni non coincidono il sistema genera un errore
>``` 
>Error: Source file requires different compiler version (current compiler is 0.8.0+commit.c7dfd78e.Darwin.appleclang) - note that nightly builds are considered to be strictly less than the released version
>```
>
>Nel caso di MAC per installare ed usare la versione `solidity 5` (`solc`) nella versione `0.5.17`, installare `solidity tramite `brew` e il pacchetto` `solidity@5` tramite il comando:
>```
>brew install solidity@5
>brew link solidity@5
>```

# WIKI

Istruzioni per creare un utente su mysql;

```
create database chorchain;
create user 'chorchain'@'%' identified by 'chorchain'; -- Creates the user
grant all on chorchain.* to 'chorchain'@'%'; 
```

### ganache users
master account : 

account[1] = 0x535CCa8697F29DaC037a734D6984eeD7EA943A85
account[2] = 0x9515365F4cB7463E7d0B9A12De7706dE6EB62709
account[3] = 0x901D7C8d516a5c97bFeE31a781A1101D10BBc8e9
account[4] = 0x84FdF08A7317c58AfBb9342636Ce1496C9Eb3B60
account[5] = 0x07ED3d24A545f85B04bFC5Cc26De59Dde920f9Fe

Login credentials:

username : account[x]
password : test  


**comandi utilizzati spesso**

http  POST :8085/signin address=aa password=aa
http --form --session a POST :8085/login address=0x535CCa8697F29DaC037a734D6984eeD7EA943A85 password=test

http --session a POST :8085/model description="aaa" data=@bpmn/OnlinePurchase.bpmn name="test" extension="bpmn"
http --session a :8085/model/1
http --session a POST :8085/instance modelId=1 mandatoryParticipants:='["1","2"]' visibleAt:='["null"]'




## Endpoints

Per utilizzare alcuni gli endpoint presenti è necessario eseguire prima il *login* perchè prottetti da permessi di sicurezza. Il login se fatto da linea di comando va fatto con il parametro `--form` con associata un sessione (`--session nome_sessione`) richiamabile successivamente per garantire la connessione.



 ###Note per il login
  
 #### /login
  
 `$ http --form --session blockchain POST localhost:8085/login address=mioaddress password=password
 `
 
 `--form` : permette di passare i parametri in form `form-encoded  (multipart/form-data )`
 
 `--session`: crea una ``named session`` che mantiene i valori di sessione e permette di continuare la navigazione tra i vari endpoint 'prottetti' dopo l'autenticazione
 
 `--follow`: segue l'eventuale redirezione se c'è (quella nel location)
 
 `-v:` verboso
 
 #### /signin

Permette di creare un nuovo utente 
 
 
`http POST :8085/users/signin address=io password=io`
 
  #### /signin
 
 Permette di creare un nuovo utente 
  
  
 `http POST :8085/users/signin address=io password=io`
 
#### /users/id

Permette di ottenere le info utente tramite id 
 
`http GET :8085/users/1 `
 
#### /users/adress
 
Permette di ottenere le info utente tramite address 
  
`http GET :8085/users/address `
 
 
 
#### /model
 
 GET  - Permette di ottenere la lista di coreografie presenti 

 POST - Permette di aggiungere una nuova coregrafia 
   es. 
   ```
   http --session blockchain :8085/model @file
   ```
 @ : serve per postare un file  
 
 
 DELETE - /model/id -  Permette di eliminare la coregrafia id 
 
 GET - /model/id -  Permette di ottenere la coregrafia id 
 
 
 
 
 
 


 