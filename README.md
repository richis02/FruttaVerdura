Per comprendere al meglio Tensor Flow abbiamo scelto di sviluppare un’app che sfrutta la classificazione di immagini. In particolare abbiamo allenato un modello per il riconoscimento di alcuni tipi di frutta e verdura.
L’applicazione funziona solo se si garantiscono i permessi alla fotocamera. E' stata testata principalmente con Android 12, ma dovrebbe funzionare comunque anche con versioni più recenti.
Il modello è creato da noi e per svilupparlo abbiamo seguito questi passaggi:
1.    Per prima cosa abbiamo individuato un dataset di immagini di frutta da cui abbiamo scaricato le immagini grazie all’API di Kaggle.
2.    Il passo successivo è stato quello di creare uno script in python per processare i dati e allenare il modello.
        Abbiamo implementato il modello con la libreria Keras di Tensor Flow, utilizzando il transfer learning per adattare il modello MobileNetV2 al nostro caso.
3.    Abbiamo eseguito lo script su Google Colab sfruttando la GPU messa a disposizione dal runtime.

Il modello a volte può sbagliare in quanto è allenato su un dataset limitato e il numero di epoch di allenamento è limitato dal tempo di utilizzo gratuito di Colab.
Per ottenere, quindi, un modello più performante sarebbe necessario migliorare il dataset o modificare i parametri della rete ed eseguirla per un numero maggiore di epoch.

STRUTTURA DELL'APPLICAZIONE

L’applicazione ha tre funzioni principali.
a.    La prima funzione permette di visualizzare e gestire liste di frutta e verdura. 
b.    La seconda ha lo scopo di visualizzare i principali valori nutrizionali dei prodotti.
c.    Infine c’è la possibilità di riconoscere un frutto tramite la fotocamera per vederne i valori nutrizionali o per aggiungerlo alla lista corrente.

Per quanto riguarda la funzione a.

In questa sezione è possibile creare nuove liste di frutta e verdura. Dopo aver aggiunto una lista è possibile rinominarla (tramite il componente Dialog di Android X) o eliminarla.
Due o più liste inoltre possono avere lo stesso nome in quanto nel database Room sono univocamente determinate da un id numerico. 
Cliccando su una lista si possono vedere i prodotti al suo interno.
	
In questa schermata è possibile aggiungere elementi alla lista. Per farlo bisogna cliccare sul bottone “più” e successivamente sul simbolo “cerca”.
Si apre una schermata in cui è possibile scegliere un elemento e la rispettiva quantità da aggiungere.
Nel caso si scelga di aggiungere un frutto già presente la quantità viene sommata a quella che già c’era.
Tramite “Toast” viene segnalato se il frutto è stato aggiunto correttamente.
E’ possibile aggiungere un frutto alla lista anche cliccando su “fotocamera”.
Nella sezione fotocamera (verrà analizzata in seguito) che si aprirà basterà inquadrare un frutto e dopo il riconoscimento da parte del modello cliccare su “aggiungi”.
In questo verrà aggiunto 1 solo prodotto, la quantità sarà modificabile una volta tornati nella schermata della lista.

Dopo aver aggiunto un frutto (o una verdura) è possibile modificarne la quantità cliccando sull’elemento della lista e interagendo con il Dialog che compare.

E’ infine possibile eliminare un elemento dalla lista cliccando sul “cestino” che compare cliccando il “più”.


Per quanto riguarda la funzione b.

Se nella home page si seleziona “cerca” compare una schermata per ricercare un frutto o una verdura.
Una volta individuato il prodotto che si sta cercando è possibile cliccarlo per ottenere informazioni sui suoi valori.



Per quanto riguarda la funzione c.

Si accede a questa schermata cliccando su “foto” dalla home page o, come descritto in precedenza, nella fase di aggiunta di un frutto ad una lista.
Nel caso ci si acceda dalla home, dopo aver individuato un frutto viene richiamata l’activity corrispondente al frutto individuato.
Si tratta della parte principale della nostra app in quanto implementa il modello da noi creato con tensor flow. 
Dopo aver aperto la fotocamera il modello tenterà di riconoscere un frutto o una verdura.
Tramite un buffer contenente bitmap (che sono le immagini in tempo reale della fotocamera) viene chiesto periodicamente al modello di restituire una predizione.
Dalla distribuzione di probabilità ritornata dal modello viene scelto l’elemento con il valore maggiore.
Questo elemento viene preso in considerazione solo se supera il valore di threshold di 90%.
In caso affermativo viene mostrato il risultato e un bottone per compiere l’azione descritta qualche riga sopra.

Le icone dell’app di Fruitify sono state prese da Flat Icon, Freepik, Pexels.
