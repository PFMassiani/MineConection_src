package share.utilisateur;

import java.io.Serializable;
import java.io.IOException;
import java.util.Comparator;
import java.util.Date;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import exception.*;
import serveur.bdd.Backupable;
import share.communication.*;
import share.interaction.Evenement;

public class Calendrier implements Backupable{
	private static final long serialVersionUID = 1891686558829626168L;

	SortedSet<Evenement> principale;
	SortedSet<Evenement> attente;

	private final int IDENTIFIANT;

	public Calendrier(int id) {
		IDENTIFIANT = id;
		ComparateurEvt comp = new ComparateurEvt();
		principale = new TreeSet<>(comp);
		attente = new TreeSet<>(comp);
	}

	public Set<Evenement> getEvenementsClient(){
		Set<Evenement> evts = new TreeSet<>(principale);
		evts.addAll(attente);
		return evts;
	}
	public Set<Evenement> getPrincipaleClient(){
		return principale;
	}
	public Set<Evenement> getAttenteClient(){
		return attente;
	}
	
	public boolean ajouterPrincipale(Evenement evt) {
		// Possibilité pour détecter facilement les conflits: découper la journée en plage horaires, et à chaque plage horaire, 
		//	noter les événements qui l'occupent. Mais cela nécessite de repenser tout les événements
		pull();
		
		boolean reussi = false;
		for ( Evenement e : conflits(evt) ) 
			System.out.println("Attention : l'événement " + evt + " n'est pas compatible avec " + e);

		reussi = principale.add(evt);
		if (reussi && attente.remove(evt)) System.out.println("L'événement " + evt + " est passé de la liste d'attente à la liste principale");
		
		push();

		return reussi;
	}

	public boolean ajouterAttente(Evenement evt) {
		pull();
		
		boolean reussi = false;

		for ( Evenement e : principale ) 
			if ( !e.equals(evt) && !evt.estCompatibleAvec(e) ) 
				System.out.println("Attention : l'événement " + evt + " n'est pas compatible avec " + e);

		reussi = attente.add(evt);
		if(reussi & principale.remove(evt)) System.out.println("L'événement " + evt + " est passé de la liste d'attente à la liste principale");
		
		push();

		return attente.add(evt);
	}

	public boolean surPrincipale(Evenement e) {
		pull();
		return principale.contains(e);
	}

	public boolean surAttente(Evenement e) {
		pull();
		return attente.contains(e);
	}

	public Set<Evenement> conflits(Evenement evt){
		pull();
		Set<Evenement> conflits = new TreeSet<Evenement>();
		for ( Evenement e : principale ) if ( !e.equals(evt) && !e.estCompatibleAvec(evt))
			conflits.add(e);
		return conflits;
	}

	public boolean retirerPrincipale(Evenement evt) {
		pull();
		boolean reussi = principale.remove(evt);
		if (reussi) reussi &= push();
		return reussi;
	}

	public boolean retirerAttente(Evenement evt) {
		pull();
		boolean reussi = attente.remove(evt);
		if (reussi) reussi &= push();
		return reussi;
	}

	public boolean supprimer(Evenement evt) {
		pull();
		return retirerPrincipale(evt) & !retirerAttente(evt);
	}

	public void afficher() {
		pull();
		synchronized (System.out) {
			System.out.println("==================== SUR LISTE PRINCIPALE ====================");
			Date dateprec = null;
			for(Evenement evt : principale ) {
				if (!evt.getDebut().equals(dateprec)) {
					dateprec = evt.getDebut();
					System.out.println("----------> " + dateprec);
				}
				evt.afficher();	
			}
			System.out.println("==================== SUR LISTE D'ATTENTE ====================");
			dateprec = null;
			for(Evenement evt : attente ) {
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
			Communication com = new Communication(TypeBackupable.CALENDRIER, Action.SAUVEGARDER,this);
			ConnexionServeur.getOOS().writeObject(com);

			return ConnexionServeur.getIOS().readBoolean();

		} catch (IOException | InvalidParameterException ex) {
			System.out.println(ex.getMessage());
		}
		return false;
	}

	public void pull() {
		try {
			Communication com = new Communication(TypeBackupable.CALENDRIER, Action.CHARGER, IDENTIFIANT);
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
			Communication com = new Communication(TypeBackupable.CALENDRIER, Action.NOUVEAU, cal);
			ConnexionServeur.getOOS().writeObject(com);
			Object o = ConnexionServeur.getIOS().readObject();
			if (!(o instanceof Calendrier)) throw new InvalidCommunicationException("La communication n'a pas renvoyé un calendrier");
			cal = (Calendrier) o;
		} catch (ClassNotFoundException | IOException | InvalidCommunicationException | InvalidParameterException ex) {
			ex.printStackTrace();
		}
		
		return cal;
	}
}

class ComparateurEvt implements Comparator<Evenement>,Serializable {

	@Override
	public int compare(Evenement e1, Evenement e2) {
		return (int) (e1.getDebut().getTime() - e2.getDebut().getTime());
	}
	
	
}
