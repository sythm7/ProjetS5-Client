package client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import data.FilDeDiscussion;
import data.Message;
import enums.StatutEnum;
import networking.NetworkFilDeDiscussion;
import networking.NetworkHashMap;
import networking.NetworkMessage;

public class Client {

	private static final int CONNECTION_PORT = 6969;
	
	public static String lastAndFirstName;
	
	public static String userName;
	
	private ConnectionThread connectionThread;
	
	private InterfaceClient interfaceClient;
	
	private LinkedHashMap<String, LinkedHashSet<FilDeDiscussion>> groupTreeMap = new LinkedHashMap<>();
	
	private Socket socket = null;
	
	private ObjectOutputStream outputStream = null;
	
	private ObjectInputStream inputStream = null;
	
	private ArrayList<String> allGroupsList = null;
	
	/*Initialise les tests (c'est juste manuel, on ne l'utilisera plus car dès que la base de données sera liée au serveur et au client,
	* on ira récupérer les infos dessus.
	*/
	
	public void askForInitData() {
		String askForInit = new String("init");
		this.connectionThread.sendObject(askForInit);
	}
	
	public void send(Object obj) {
		this.connectionThread.sendObject(obj);
	}
	
	public boolean areIdentifiersValid(String[] identifiers) {
		
		String[] verification = null;
		
		boolean isValid = false;
		
		try {
			this.outputStream.writeUnshared(identifiers);
			this.outputStream.reset();
			Object obj = this.inputStream.readUnshared();
			verification = (String[]) obj;
			
		} catch (IOException e) {
			e.printStackTrace();
			
		} catch (ClassNotFoundException e) {
			System.out.println("Classe non trouvée");
			System.exit(4);
		}

		if(verification[0].equals("accepted")) {
			isValid = true;
			Client.lastAndFirstName = verification[1] + " " + verification[2];
		}
		
		return isValid;
	}
	
	public void updateTree(NetworkHashMap networkHashMap) {
		
		this.groupTreeMap.clear();
		
		ArrayList<String> receivedFddList = new ArrayList<>();
		receivedFddList.add("idF_Recu");
		
		LinkedHashMap<String, LinkedHashSet<NetworkFilDeDiscussion>> groupHashMap = networkHashMap.getGroupHashMap();
		
		for(String groupe : groupHashMap.keySet()) {
			
			this.groupTreeMap.put(groupe, new LinkedHashSet<FilDeDiscussion>());
			
			for(NetworkFilDeDiscussion networkFdd : groupHashMap.get(groupe)) {
				NetworkMessage networkFirstMessage = networkFdd.getListeMessages().iterator().next();
				Message first = new Message(networkFirstMessage.getContenu(), networkFirstMessage.getDate(), networkFirstMessage.getAuteur(), networkFirstMessage.getNomComplet());
				first.idM = networkFirstMessage.idM;
				first.setStatus(networkFirstMessage.getStatusMsg());
				
				FilDeDiscussion fdd = new FilDeDiscussion(networkFdd.getTitre(), networkFdd.getAuteur(), networkFdd.getNomGroupe());
				fdd.setStatusFdd(networkFdd.getStatusFdd());
				
				fdd.idF=networkFdd.idF;
				this.groupTreeMap.get(groupe).add(fdd);
				
				if(fdd.getStatusFdd().equals(StatutEnum.EN_ATTENTE))
					receivedFddList.add(String.valueOf(fdd.idF));
				
				for(NetworkMessage networkMessage : networkFdd.getListeMessages()) {
					Message m = new Message(networkMessage.getContenu(), networkMessage.getDate(), networkMessage.getAuteur(), networkMessage.getNomComplet());
					m.idM=networkMessage.idM;
					m.setStatus(networkMessage.getStatusMsg());
					fdd.addMsg(m);
				}
			}
		}
		
		this.interfaceClient.updateTree();
		
		int size = receivedFddList.size();
		
		String[] receivedFddArray = new String[size];
		
		for(int i = 0; i < size; i++)
			receivedFddArray[i] = receivedFddList.get(i);
			
		
		this.send(receivedFddArray);
	}
	
	public void sendFddConfirm(FilDeDiscussion fdd) {
		Message firstMsgInFdd = fdd.getListeMessages().iterator().next();
		NetworkFilDeDiscussion netFdd = new NetworkFilDeDiscussion(fdd.getTitre(), fdd.getAuteur(), fdd.getNomGroupe());
		NetworkMessage networkFirstMessage = new NetworkMessage(firstMsgInFdd.getContenu(), firstMsgInFdd.getDate(), firstMsgInFdd.getAuteur());
		networkFirstMessage.setNomComplet(Client.lastAndFirstName);
		netFdd.addMsg(networkFirstMessage);
		
		this.connectionThread.sendObject(netFdd);
	}

	public void sendFddConfirm(NetworkFilDeDiscussion networkFdd) {
		
		this.connectionThread.sendObject(networkFdd);
	}
	
	public void setInterfaceClient(InterfaceClient interfaceClient) {
		this.interfaceClient = interfaceClient;
	}
	
	public InterfaceClient getInterfaceClient() {
		return this.interfaceClient;
	}
	
	public LinkedHashMap<String, LinkedHashSet<FilDeDiscussion>> getGroupTreeMap() {
		return groupTreeMap;
	}
	
	public void connectToServer() {
		
		try {
			this.socket = new Socket("localhost", Client.CONNECTION_PORT);
			this.outputStream = new ObjectOutputStream(socket.getOutputStream());
			this.inputStream = new ObjectInputStream(socket.getInputStream());
		}
		catch(IOException ioException) {
			ioException.printStackTrace();
			JOptionPane.showMessageDialog(null, "Impossible de se connecter au serveur", "Connexion impossible", JOptionPane.ERROR_MESSAGE);
			System.exit(1);
		}
		
		System.out.println("Connecté au serveur");
	}
	
	public void disconnectFromServer() {
		this.connectionThread.interrupt();
		try {
			this.socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Déconnecté du serveur");
	}
	
	public ObjectOutputStream getObjectOutputStream() {
		return this.outputStream;
	}
	
	public ObjectInputStream getObjectInputStream() {
		return this.inputStream;
	}
	
	public Socket getSocket() {
		return this.socket;
	}
	
	public ConnectionThread getConnectionThread() {
		return this.connectionThread;
	}
	
	public ArrayList<String> getAllGroupsList() {
		return allGroupsList;
	}

	public void setAllGroupsList(ArrayList<String> allGroupsList) {
		this.allGroupsList = allGroupsList;
	}
	
	public void initConnectionThread() {
		this.connectionThread = new ConnectionThread(this);
	}
	
	/*Lance l'interface avec les test manuels (changer les tests manuels par les test dynamiques issus de la base de données lorsque cette
	* dernière sera opérationnelle.
	*/
	public static void main(String[] args) {

		Client client = new Client();
		
		InterfaceConnexion interfaceConnexion = new InterfaceConnexion(client);
		
		// Lancer l'interface
		// invokeLater() cree un thread dedie a l'affichage de l'interface graphique
		SwingUtilities.invokeLater(() -> interfaceConnexion.initialiser());
	}


}
