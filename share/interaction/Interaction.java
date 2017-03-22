package share.interaction;

import java.io.Serializable;

import serveur.bdd.Backupable;
import share.utilisateur.Utilisateur;

// Classe mère de PAPS et de Evenement

public abstract class Interaction implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6376358620587929389L;
	// -----------------------------------------------------------------------------------------------------------------------
	// ATTRIBUTS -------------------------------------------------------------------------------------------------------------
	// -----------------------------------------------------------------------------------------------------------------------

	protected String nom, description;
	protected int places, placesRestantes;
	protected Utilisateur createur;
	protected boolean placesUpdate = false;
	//FileAttente attente;

	protected final int IDENTIFIANT;

	public final int FALSE = 0, TRUE = 1;

	public Interaction(int id, String n, String d, int p, Utilisateur c){
		IDENTIFIANT = id;
		nom = n;
		description = d;
		places = p;
		placesRestantes = p;
		createur = c;
	}

	public Interaction(int id, String n, int p, Utilisateur c){
		this(id,n,"",p,c);
	}

	public int getID(){
		return IDENTIFIANT;
	}
	public String getNom(){
		return nom;
	}
	public String getDescription(){
		return description;
	}
	public int getPlaces(){
		return places;
	}
	public int getPlacesRestantes(){
		return placesRestantes;
	}
	public Utilisateur getCreateur(){
		return createur;
	}
	public boolean estComplet(){
		return placesRestantes == 0;
	}
	public boolean setNom(String n){
		String oldNom = nom;
		nom = n;
		boolean reussi = push();
		if (!reussi) nom = oldNom;
		return reussi;
	}
	public boolean setDescription(String d){
		String oldDesc = description;
		description = d;
		boolean reussi = push();
		if (!reussi) nom = oldDesc;
		return reussi;  
	}
	public boolean setCreateur(Utilisateur c){
		Utilisateur oldCrea = createur;
		createur = c;
		boolean reussi = push();
		if (!reussi) createur = oldCrea;
		return reussi;  
	}
	protected abstract boolean push();

}
