package client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

import networking.NetworkHashMap;

public class ConnectionThread extends Thread {
	
	private Client client;
	
	private ObjectOutputStream outputStream;
	
	private ObjectInputStream inputStream;
	
	private Socket socket;

	public ConnectionThread(Client client) {
		this.client = client;
		this.socket = this.client.getSocket();
		this.outputStream = this.client.getObjectOutputStream();
		this.inputStream = this.client.getObjectInputStream();
	}
	
	public void sendObject(Object obj) {
		try {
			this.outputStream.writeUnshared(obj);
			this.outputStream.reset();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void getAllGroupsList(Object obj) {
		@SuppressWarnings("unchecked")
		ArrayList<String> allGroupsList = (ArrayList<String>) obj;
		this.client.setAllGroupsList(allGroupsList);
		
		synchronized(this) {
			this.notifyAll();
		}
	}

	@Override
	public void run() {
		
		System.out.println("ConnectionThread lancé");
		
		while(socket.isConnected()) {
			
			try {
				Object obj = this.inputStream.readUnshared();
				System.out.println(obj.getClass() + " recu");
				
				if(obj instanceof NetworkHashMap) {
					NetworkHashMap networkHashMap = (NetworkHashMap) obj;
					this.client.updateTree(networkHashMap);
				}

				if(obj instanceof ArrayList<?>) {
					this.getAllGroupsList(obj);
				}
				
			}
			catch (IOException e) {
				try {
					socket.close();
					System.out.println("Socket fermé");
					socket.setKeepAlive(false);
				} catch (IOException e1) {
					System.exit(2);
				}
			}
			catch (ClassNotFoundException e) {
				System.out.println("Classe non trouvée");
				System.exit(3);
			}
		}
	}
}
