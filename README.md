Per far partire il progetto è sufficiente lanciare:

```
$ mvn spring-boot:run
```

Secondo le impostazioni presenti nel file di configurazione (src/main/resoruces/application.properties) si avrà che :

 - il servizio gira sulla porta 8085
 - la directory di upload dei file è /upload_dir
 - il db (per ora) è h1 con tutte le impostazioni (persistente su file)
  



Per testare gli endpoint in fase di sviluppo si consiglia l'utilizzo di ``httpie`` https://httpie.org qui abbreviato ad http.



Istruzioni per creare un utente su mysql;

```
create database chorchain;
create user 'chorchain'@'%' identified by 'chorchain'; -- Creates the user
grant all on chorchain.* to 'chorchain'@'%'; 
```


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
 
 
 
 
 
 


 