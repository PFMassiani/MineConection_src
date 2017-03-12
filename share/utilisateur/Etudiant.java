package share.utilisateur;

import java.sql.*;

import exception.*;

import java.util.Iterator;
import java.util.Set;

public class Etudiant extends Utilisateur {

	// -----------------------------------------------------------------------------------------------------------------------
	// ATTRIBUTS -------------------------------------------------------------------------------------------------------------
	// -----------------------------------------------------------------------------------------------------------------------

	/**
	 * 
	 */
	private static final long serialVersionUID = 1028926935677583162L;
	private String nom, prenom, telephone;
	private int promo;
	//  Date naissance;
	Calendrier calendrier;

	//------------------------------------------------------------------------------------------------------------------------
	// CONSTRUCTEURS ---------------------------------------------------------------------------------------------------------
	// -----------------------------------------------------------------------------------------------------------------------

	//Constructeur utilisé pour créer un nouvel étudiant
	public Etudiant (int id, String n, String p, String t, int pr, Calendrier cal){    
		super(id);
		nom = n;
		prenom = p;
		promo = pr;
		telephone = t;
		calendrier = cal;

		if (IDENTIFIANT == -1) throw new InvalidIDException("");
	}

	//------------------------------------------------------------------------------------------------------------------------
	// ACCESSEURS ------------------------------------------------------------------------------------------------------------
	// -----------------------------------------------------------------------------------------------------------------------

	public String getNom(){
		return nom;
	}
	public String getPrenom(){
		return prenom;
	}
	public String getTelephone(){
		return telephone;
	}
	public int getPromo(){
		return promo;
	}
	public String getUpdate(){
		return "nom = '" + nom + "', prenom = '" + prenom + "', telephone = '" + telephone + "', promotion = " + promo;
	}
	public Calendrier getCalendrier() {
		return calendrier;
	}

	public void setNom(String n){
		nom = n;
	}
	public void setPrenom(String p){
		prenom = p;
	}
	public void setTelephone(String t){
		telephone = t;
	}
	public void setPromo(int p){
		promo = p;
	}

	//------------------------------------------------------------------------------------------------------------------------
	// UTILITAIRE ------------------------------------------------------------------------------------------------------------
	// -----------------------------------------------------------------------------------------------------------------------


	//------------------------------------------------------------------------------------------------------------------------
	// EVENEMENTS ------------------------------------------------------------------------------------------------------------
	// -----------------------------------------------------------------------------------------------------------------------

	// TODO Rejoindre un événement, en quitter un...
	
	@Override
	public String toString(){
		return "Étudiant n°" + IDENTIFIANT + ", " + prenom + " " + nom + " ( P " + promo + " )";
	}

	@Override
	public int hashCode() {
		return IDENTIFIANT;
	}
	@Override
	public boolean equals (Object o){
		if (!(o instanceof Etudiant)) return false;

		Etudiant e = (Etudiant) o;
		return e.getID() == IDENTIFIANT;
	}

}
