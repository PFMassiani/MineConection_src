package share.utilisateur;

import java.io.IOException;
import java.sql.*;

import exception.*;
import share.communication.Action;
import share.communication.Communication;
import share.communication.ConnexionServeur;
import share.communication.TypeBackupable;
import share.interaction.*;

import java.util.Iterator;
import java.util.Set;

public class Etudiant extends Utilisateur {

	// -----------------------------------------------------------------------------------------------------------------------
	// ATTRIBUTS -------------------------------------------------------------------------------------------------------------
	// -----------------------------------------------------------------------------------------------------------------------

	private static final long serialVersionUID = 1028926935677583162L;
	private String nom, prenom, telephone;
	private int promo;
	private Calendrier calendrier;

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

	public boolean setNom(String n){
		String oldNom = nom;
		nom = n;
		boolean reussi = push();
		if (!reussi) nom = oldNom;
		return reussi;
	}
	public boolean setPrenom(String p){
		String oldPrenom = prenom;
		prenom = p;
		boolean reussi = push();
		if (!reussi) prenom = oldPrenom;
		return reussi;
	}
	public boolean setTelephone(String t){
		String oldTel = telephone;
		telephone = t;
		boolean reussi = push();
		if (!reussi) telephone = oldTel;
		return reussi;
		}
	public boolean setPromo(int p){
		int oldPromo = promo;
		promo = p;
		boolean reussi = push();
		if (!reussi) promo = oldPromo;
		return reussi;	}

	//------------------------------------------------------------------------------------------------------------------------
	// UTILITAIRE ------------------------------------------------------------------------------------------------------------
	// -----------------------------------------------------------------------------------------------------------------------


	//------------------------------------------------------------------------------------------------------------------------
	// EVENEMENTS ------------------------------------------------------------------------------------------------------------
	// -----------------------------------------------------------------------------------------------------------------------

	
	public boolean participer(Evenement e) {
		boolean surPrincipale = e.ajouter(this);
		if (surPrincipale) calendrier.ajouterPrincipale(e);
		else calendrier.ajouterAttente(e);
		push();
		return surPrincipale;
	}
	public void quitter(Evenement e) {
		e.supprimerInscrit(this);
		calendrier.supprimer(e);
	}
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

	@Override
	public boolean push() {
		try {
			Communication com = new Communication(TypeBackupable.ETUDIANT, Action.SAUVEGARDER, this);
			ConnexionServeur.getOOS().writeObject(com);
			
			return ConnexionServeur.getIOS().readBoolean();
		} catch(IOException | InvalidParameterException ex) {
			System.out.println(ex.getMessage());
		}
		return false;
	}

}
