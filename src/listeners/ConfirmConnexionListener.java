package listeners;

import javax.swing.*;

import client.Client;
import client.InterfaceClient;
import client.InterfaceConnexion;

import java.awt.event.*;

public class ConfirmConnexionListener implements ActionListener {

	private InterfaceConnexion interfaceConnexion;
	
	private InterfaceClient interfaceClient;
	
	private String errMsg;
	
	public ConfirmConnexionListener(InterfaceConnexion interfaceConnexion, InterfaceClient interfaceClient) {
		this.interfaceConnexion = interfaceConnexion;
		this.interfaceClient = interfaceClient;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		
		String id = this.interfaceConnexion.getIdTextFieldText();
		String password = this.interfaceConnexion.getPasswordTextFieldText();
		
		if(! isValid(id, password)) {
			JOptionPane.showMessageDialog(this.interfaceConnexion, this.errMsg, "Identifiants invalides", JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		Client client = this.interfaceClient.getClient();
		
		client.connectToServer();
		
		if(! client.areIdentifiersValid(new String[] {id, password})) {
			JOptionPane.showMessageDialog(this.interfaceConnexion, "L'identifiant ou le mot de passe est incorrect", "Identifiants invalides", JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		Client.userName = id;
		
		client.initConnectionThread();
		
		client.getConnectionThread().start();

		interfaceConnexion.dispose();
		
		this.interfaceClient.initialiser();
		
		client.setInterfaceClient(this.interfaceClient);
		
		client.askForInitData();
		
		this.interfaceClient.setVisible(true);
		
		
	}
	
	private boolean isValid(String id, String password) {
		if(id.isEmpty()) {
			this.errMsg = "Identifiant invalide : L'identifiant ne peut être vide ou composé seulement d'espaces";
			return false;
		}
		
		if(password.isEmpty()) {
			this.errMsg = "Mot de passe invalide : Le mot de passe ne peut être vide ou composé seulement d'espaces";
			return false;
		}
		return true;
	}
	
}