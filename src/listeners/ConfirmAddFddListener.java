package listeners;

import javax.swing.*;

import client.AddFddFrame;
import client.Client;
import client.InterfaceClient;
import data.FilDeDiscussion;
import networking.NetworkFilDeDiscussion;
import networking.NetworkMessage;

import java.awt.event.*;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;

public class ConfirmAddFddListener implements ActionListener {

	private AddFddFrame addFddFrame;
	
	private InterfaceClient interfaceClient;
	
	private String errMsg;
	
	private LinkedHashMap<String, LinkedHashSet<FilDeDiscussion>> groupTreeMap;
	
	public ConfirmAddFddListener(AddFddFrame addFddFrame, InterfaceClient interfaceClient) {
		this.addFddFrame = addFddFrame;
		this.interfaceClient = interfaceClient;
	}


	@Override
	public void actionPerformed(ActionEvent e) {
		
		this.groupTreeMap = this.interfaceClient.getGroupTreeMap();
		
		String groupName = addFddFrame.getGroupsListSelectedValue();
		String title = addFddFrame.getFddNameFieldText().trim();
		String content = addFddFrame.getContentEntryFieldText().trim();
		
		if(!isValid(groupName, title, content)) {
			JOptionPane.showMessageDialog(this.addFddFrame, this.errMsg, "Erreur d'ajout du fil de discussion", JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		NetworkFilDeDiscussion networkFdd = new NetworkFilDeDiscussion(title, Client.lastAndFirstName, groupName);
		networkFdd.addMsg(new NetworkMessage(content, LocalDateTime.now(), Client.lastAndFirstName));
		
		this.interfaceClient.getClient().sendFddConfirm(networkFdd);
		
		addFddFrame.dispose();
		this.interfaceClient.getButtonAddFdd().setEnabled(true);
	}

	private boolean isValid(String groupName, String title, String content) {
		
		if(groupName == null) {
			this.errMsg = "Groupe invalide : Veuillez sélectionner un groupe existant";
			return false;
		}
		
		for(String groupInKeySet : groupTreeMap.keySet()) {
			if(groupInKeySet.equals(groupName)) {
				for(FilDeDiscussion fdd : groupTreeMap.get(groupInKeySet)) {
					if(this.addFddFrame.getFddNameFieldText().equals(fdd.getTitre())) {
						this.errMsg = "Erreur : Ce fil de discussion existe déjà";
						return false;
					}
				}
			}
		}	

		if(title.isEmpty()) {
				this.errMsg = "Nom du fil de discussion invalide : Le fil de discussion ne peut être vide ou composé seulement d'espaces";
				return false;
		}
		
		if(content.isEmpty()) {
			this.errMsg = "Contenu invalide : Le contenu ne peut être vide ou composé seulement d'espaces";
			return false;
		}
		return true;
	}
	
}
