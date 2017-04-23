package share.utilisateur;

import java.io.IOException;
import java.util.Date;
import java.util.Set;

import java.util.HashSet;

import exception.*;
import serveur.bdd.Backupable;
import share.communication.*;
import share.interaction.Evenement;

public class Calendrier implements Backupable{
	private static final long serialVersionUID = 1891686558829626168L;

	Set<Integer> principale;
	Set<Integer> attente;

	private final int IDENTIFIANT;

	public Calendrier(int id) {
		IDENTIFIANT = id;
		principale = new HashSet<>();
		attente = new HashSet<>();
	}

	public Set<Integer> getEvenementsClient(){
		Set<Integer> evts = new HashSet<>(principale);
		evts.addAll(attente);
		return evts;
	}
	public Set<Integer> getPrincipaleClient(){
		return principale;
	}
	public Set<Integer> getAttenteClient(){
		return attente;
	}

	public boolean ajouterPrincipale(int id) {
		// Possibilité pour détecter facilement les conflits: découper la journée en plage horaires, et à chaque plage horaire, 
		//	noter les événements qui l'occupent. Mais cela nécessite de repenser tout les événements
		pull();
		boolean reussi = false;

		reussi = principale.add(id);
		if (reussi && attente.remove(id)) System.out.println("L'événement " + id + " est passé de la liste d'attente à la liste principale");

		reussi &= push();

		return reussi;
	}

	public boolean ajouterAttente(int id) {
		pull();
		boolean reussi = attente.add(id);
		if(reussi & principale.remove(id)) System.out.println("L'événement " + id + " est passé de la liste principale à la liste d'attente");

		reussi &= push();

		return reussi;
	}

	public boolean surPrincipale(Evenement e) {
		pull();

		return IDENTIFIANT == e.getCreateur().getID() | principale.contains(e.getID());
	}

	public boolean surAttente(Evenement e) {
		pull();
		return !(IDENTIFIANT == e.getCreateur().getID()) & attente.contains(e.getID());
	}

	public Set<Evenement> conflits(Evenement evt){
		pull();
		Set<Evenement> conflits = new HashSet<Evenement>();
		Evenement e = null;
		for ( int i : principale ) {
			e = Evenement.chercher(i);
			if ( !e.equals(evt) && !e.estCompatibleAvec(evt)) conflits.add(e);
		}
		return conflits;
	}

	public boolean retirerPrincipale(Evenement evt) {
		pull();
		boolean reussi = principale.remove(evt.getID());
		if (reussi) reussi &= push();
		return reussi;
	}

	public boolean retirerAttente(Evenement evt) {
		pull();
		boolean reussi = attente.remove(evt.getID());
		if (reussi) reussi &= push();
		return reussi;
	}

	public boolean supprimerEvenement(Evenement evt) {
		pull();
		return retirerPrincipale(evt) & !retirerAttente(evt);
	}

	public void afficher() {
		pull();
		synchronized (System.out) {
			System.out.println("==================== SUR LISTE PRINCIPALE ====================");
			Date dateprec = null;
			Evenement evt = null;
			for(int i : principale ) {
				evt = Evenement.chercher(i);
				if (!evt.getDebut().equals(dateprec)) {
					dateprec = evt.getDebut();
					System.out.println("----------> " + dateprec);
				}
				evt.afficher();	
			}
			System.out.println("==================== SUR LISTE D'ATTENTE ====================");
			dateprec = null;
			for(int i : attente ) {
				evt = Evenement.chercher(i);
				if (!evt.getDebut().equals(dateprec)) {
					dateprec = evt.getDebut();
					System.out.println("----------> " + dateprec);
				}
				evt.afficher();	
			}

		}
	}

	public boolean push() {
		try {
			Communication com = new Communication(TypeBackupable.CALENDRIER, Action.SAUVEGARDER,this, "Sauvegarde du calendrier " + IDENTIFIANT);
			ConnexionServeur.getOOS().writeObject(com);
			ConnexionServeur.getOOS().writeObject(principale);
			ConnexionServeur.getOOS().writeObject(attente);
			Object o = ConnexionServeur.getIOS().readObject();
			if (!(o instanceof Boolean)) throw new InvalidCommunicationException("Le serveur n'a pas renvoyé un booléen");
			return (boolean) o;
		} catch (IOException | InvalidParameterException | ClassNotFoundException | InvalidCommunicationException ex) {
			System.out.println(ex.getMessage());
			ex.printStackTrace();
		}
		return false;
	}

	public void pull() {
		try {
			Communication com = new Communication(TypeBackupable.CALENDRIER, Action.CHARGER, IDENTIFIANT, "Chargement du calendrier " + IDENTIFIANT);
			ConnexionServeur.getOOS().writeObject(com);

			Object o = ConnexionServeur.getIOS().readObject();

			if (!(o instanceof Calendrier)) throw new InvalidCommunicationException("Le serveur n'a pas renvoyé un Calendrier");

			Calendrier c = (Calendrier) o;

			if (c.IDENTIFIANT != IDENTIFIANT) throw new InvalidIDException("L'identifiant du calendrier chargé est invalide");

			principale = c.principale;
			attente = c.attente;
		} catch (IOException | 
				InvalidCommunicationException | 
				InvalidIDException | 
				InvalidParameterException | 
				ClassNotFoundException ex) {
			System.err.println(ex.getMessage());
			ex.printStackTrace();
		}

	}

	@Override
	public int getID() {
		return IDENTIFIANT;
	}

	public static Calendrier charger(int id) {
		Calendrier cal = new Calendrier(id);
		cal.pull();
		return cal;
	}
	@Override
	public Calendrier setIdentifiant(int id){
		Calendrier cal = new Calendrier(id);
		cal.principale = principale;
		cal.attente = attente;
		return cal;
	}

	public static Calendrier nouveau(int id) {
		Calendrier cal = new Calendrier(id);

		try {
			Communication com = new Communication(TypeBackupable.CALENDRIER, Action.NOUVEAU, cal, "Création du calendrier " + cal.IDENTIFIANT);
			ConnexionServeur.getOOS().writeObject(com);
			Object o = ConnexionServeur.getIOS().readObject();
			if (!(o instanceof Calendrier)) throw new InvalidCommunicationException("Le serveur n'a pas renvoyé un calendrier");
			cal = (Calendrier) o;
		} catch (ClassNotFoundException | IOException | InvalidCommunicationException | InvalidParameterException ex) {
			ex.printStackTrace();
		}

		return cal;
	}

	public static boolean supprimer(int id) {
		boolean reussi = false;
		try {
			Communication com = new Communication(TypeBackupable.CALENDRIER, Action.SUPPRIMER,id, "Suppression du calendrier " + id);
			ConnexionServeur.getOOS().writeObject(com);
			Object o = ConnexionServeur.getIOS().readObject();
			if ( !(o instanceof Boolean )) throw new InvalidCommunicationException("Le serveur n'a pas renvoyé d'information sur la réussite de la suppression");
			reussi = (boolean) o;
		} catch (InvalidCommunicationException | 
				ClassNotFoundException |
				IOException |
				InvalidParameterException ex) {
			System.out.println(ex.getMessage());
			ex.printStackTrace();
		}
		return reussi;
	}
}
