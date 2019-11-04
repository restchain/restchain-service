Per testare gli endpoint in fase di sviluppo si consiglia l'utilizzo di ``httpie`` https://httpie.org

Suggerimenti per l'utilizzo di httpie:

Es.
 
Endpoint di Login /login

`$ http --form --session blockchain POST localhost:8085/login address=mioaddress password=password
`

--form : permette di passare i parametri in form `form-encoded  (multipart/form-data )`

--session: crea una ``named session`` che mantiene i valori di sessione e permette di continuare la navigazione tra i vari endpoint 'prottetti' dopo l'autenticazione

--follow: segue l'eventuale redirezione se c'è (quella nel location)

-v: verboso




http --session a :8085/modelz @yarn-error.log
@ : serve per postare un file 

