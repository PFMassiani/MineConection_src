package share.utilisateur;

import java.io.IOException;
import java.sql.Date;
import java.util.*;

import exception.*;
import share.communication.Action;
import share.communication.Communication;
import share.communication.ConnexionServeur;
import share.communication.TypeBackupable;
import share.interaction.*;

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
			
			Object reussi = ConnexionServeur.getIOS().readObject();
			if (!(reussi instanceof Boolean)) throw new InvalidCommunicationException("La communication n'a pas renvoyé un booléen");
			return (boolean) reussi;
		} catch(IOException | InvalidParameterException | ClassNotFoundException | InvalidCommunicationException ex) {
			System.out.println(ex.getMessage());
		}
		return false;
	}
	
	public void pull() {
		try {
			Communication com = new Communication(TypeBackupable.ETUDIANT, Action.CHARGER, IDENTIFIANT);
			ConnexionServeur.getOOS().writeObject(com);
			Object o = ConnexionServeur.getIOS().readObject();
			if (!(o instanceof Etudiant)) throw new InvalidCommunicationException("Le serveur n'a pas renvoyé la mise à jour");
			Etudiant e = (Etudiant) o;
			nom = e.nom;
			prenom = e.prenom;
			telephone = e.telephone;
			promo = e.promo;
			calendrier.pull();
		} catch(IOException | InvalidParameterException | ClassNotFoundException | InvalidCommunicationException ex) {
			System.out.println(ex.getMessage());
		}
	}
	
	public static Etudiant chercher(int id) {
		try {
			  Communication com = new Communication(TypeBackupable.ETUDIANT, Action.CHARGER,id);
			  ConnexionServeur.getOOS().writeObject(com);
			  Object o =  ConnexionServeur.getIOS().readObject();
			  if (!(o instanceof Etudiant)) throw new InvalidCommunicationException("La communication n'a pas renvoyé un Etudiant");
			  return (Etudiant) o;
		  } catch (InvalidCommunicationException | IOException | ClassNotFoundException | InvalidParameterException ex) {
			  System.out.println(ex.getMessage());
		  }
		  
		return null;
	}
	public static Etudiant nouveau(String n, String p, String t, int pr) {
		Etudiant e = null;
		
		try {
			Communication com = new Communication(TypeBackupable.ETUDIANT, Action.NOUVEAU);
			ConnexionServeur.getOOS().writeObject(com);
			Object o = ConnexionServeur.getIOS().readObject();
			if (!(o instanceof Etudiant)) throw new InvalidCommunicationException("La communication n'a pas renvoyé un Etudiant");
			e = (Etudiant) o;
		} catch (InvalidCommunicationException | IOException | ClassNotFoundException | InvalidParameterException ex) {
			  System.out.println(ex.getMessage());
		}
		e.setNom(n);
		e.setPrenom(p);
		e.setTelephone(t);
		e.setPromo(pr);
		e.calendrier = Calendrier.charger(e.getID());
		return e;
	}
	
	public static Set<Integer> ids(){
		Set<Integer> ids = new HashSet<>();
		
		try {
			Communication com = new Communication (TypeBackupable.ETUDIANT, Action.GET_IDS);
			ConnexionServeur.getOOS().writeObject(com);
			Object o = ConnexionServeur.getIOS().readObject();
			if (!(o instanceof Set)) throw new InvalidCommunicationException("La communication n'a pas renvoyé un ensemble");
			Set s = (Set) o;
			if (!s.isEmpty()) {
				o = s.toArray()[0];
				if (!(o instanceof Integer)) throw new InvalidCommunicationException("La communication n'a pas renvoyé l'ensemble des identifiants");
				ids = (Set<Integer>) s;
			}
		} catch (InvalidCommunicationException | IOException | ClassNotFoundException | InvalidParameterException ex) {
			  System.out.println(ex.getMessage());
		}
		return ids;
	}
	
	public static boolean supprimer(int id) {
		boolean reussi = false;
		try {
			Communication com = new Communication(TypeBackupable.ETUDIANT, Action.SUPPRIMER,id);
			ConnexionServeur.getOOS().writeObject(com);
			Object o = ConnexionServeur.getIOS().readObject();
			if ( !(o instanceof Boolean )) throw new InvalidCommunicationException("La communication n'a pas renvoyé d'information sur la réussite de la suppression");
			reussi = (boolean) o;
		} catch (InvalidCommunicationException | 
				ClassNotFoundException |
				IOException |
				InvalidParameterException ex) {
			System.out.println(ex.getMessage());
		}
		return reussi;
		
	}
	public static Etudiant getRandomStudent() {
		Set<Integer> ids = ids();
		if ( ids.size() == 0) return null;
		int i = ((int) (Math.random() * ids.size()));
		Iterator<Integer> it = ids.iterator();
		for (int j = 0; j < i-1; j++) it.next();
		
		return chercher(it.next());
		
	}
	

@Override
public Etudiant setIdentifiant(int id) {
	Calendrier cal = calendrier.setIdentifiant(id);
	return new Etudiant(id, nom,prenom,telephone,promo,cal);
}
}
