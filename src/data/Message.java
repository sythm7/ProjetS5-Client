package data;

import java.time.LocalDate;
import java.time.LocalDateTime;

import enums.StatutEnum;

public class Message {

	private String contenu;
	
	private LocalDateTime date;
	
	private String auteur;
	public int idM;
	
	private String nomComplet;
	
	private StatutEnum statusMsg = StatutEnum.EN_ATTENTE;
	
	public Message(String contenu, LocalDateTime date, String auteur, String nomComplet) {
		this.auteur = auteur;
		this.contenu = contenu;
		this.date = date;
		this.nomComplet = nomComplet;
	}
	
	public Message(String contenu, LocalDateTime date, String auteur, String nomComplet, StatutEnum statusMsg) {
		this.auteur = auteur;
		this.contenu = contenu;
		this.date = date;
		this.nomComplet = nomComplet;
		this.statusMsg = statusMsg;
	}

	public String toString() {
		
		String jour = String.valueOf(date.getDayOfMonth());
		
		String mois = String.valueOf(date.getMonthValue());
		
		String minute = String.valueOf(date.getMinute());
		
		String heure = String.valueOf(date.getHour());
		
		String dateFormat = createDateFormat(jour, mois, minute, heure);
		
		return "<html><b STYLE=\"color: rgb(70, 230, 180); font-size: 1.4em;\">" + this.nomComplet + "</b> " + "<i STYLE=\"color: rgb(150, 230, 255); font-size: 1.2em;\">" + dateFormat + "</i><br><h3 STYLE=\"padding:0 0 0 20px; font-size: 1.3em; width: %1spx;\">" + contenu + "</h3></html>";
	}
	
	private String createDateFormat(String jour, String mois, String minute, String heure) {
		
		LocalDate now = LocalDate.now();
		
		if(date.getDayOfMonth() == now.getDayOfMonth() && date.getMonth() == now.getMonth() && date.getYear() == now.getYear()) {
			
				if(Integer.valueOf(heure) < 10)
					heure = "0" + heure;
				if(Integer.valueOf(minute) < 10)
					minute = "0" + minute;
				
				return "Aujourd'hui à " + heure + ":" + minute;
		}
			
		if(Integer.valueOf(jour) < 10)
			jour = "0" + jour;
		if(Integer.valueOf(mois) < 10)
			mois = "0" + mois;

		return jour + "/" + mois + "/" + date.getYear();
	}
	
	public String getContenu() {
		return contenu;
	}

	public LocalDateTime getDate() {
		return date;
	}

	public String getAuteur() {
		return auteur;
	}
	
	public String getNomComplet() {
		return nomComplet;
	}
	
	public StatutEnum getStatus() {
		return this.statusMsg;
	}
	
	public void setStatus(StatutEnum statusMsg) {
		this.statusMsg = statusMsg;
	}
	
}
