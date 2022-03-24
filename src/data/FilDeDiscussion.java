package data;

import java.util.Iterator;
import java.util.LinkedHashSet;

import enums.StatutEnum;

public class FilDeDiscussion implements Iterable<Message> {

	private String titre;
	
	private String auteur;
	
	private String nomGroupe;
	public int idF;
	public int index=0;
	
	private StatutEnum statusFdd;
	
	private LinkedHashSet<Message> listeMessages = new LinkedHashSet<>();
	
	public FilDeDiscussion(String titre, String auteur, String nomGroupe, Message premierMsg) {
		this.titre = titre;
		this.auteur = auteur;
		this.nomGroupe = nomGroupe;
		this.listeMessages.add(premierMsg);
		this.index=premierMsg.idM;
	}
	
	public FilDeDiscussion(String titre, String auteur, String nomGroupe) {
		this.titre = titre;
		this.auteur = auteur;
		this.nomGroupe = nomGroupe;
	}
	
	public void setPremierMessage(Message premierMsg) {
		this.index = premierMsg.idM;
	}
	
	public String toString() {
		return titre;
	}
	
	public void addMsg(Message msg) {
		listeMessages.add(msg);
		if (this.index<msg.idM)
			this.index=msg.idM;
	}

	public String getTitre() {
		return titre;
	}

	public String getAuteur() {
		return auteur;
	}

	public String getNomGroupe() {
		return nomGroupe;
	}
	
	public StatutEnum getStatusFdd() {
		return statusFdd;
	}

	public void setStatusFdd(StatutEnum statusFdd) {
		this.statusFdd = statusFdd;
	}
	
	public LinkedHashSet<Message> getListeMessages(){
		return listeMessages;
	}

	@Override
	public Iterator<Message> iterator() {
		return listeMessages.iterator();
	}
	
	public boolean equals(Object obj) {
		if(! (obj instanceof FilDeDiscussion))
			return false;
		
		FilDeDiscussion fdd = (FilDeDiscussion) obj;
		
		return this.idF == fdd.idF;
	}
	
	@Override
	public int hashCode() {
		return (this.titre.hashCode() + this.idF + this.index + this.nomGroupe.hashCode()) * 31;
	}
}
