package serveur.utilisateur;

import java.util.*;
import java.sql.Date;
import share.interaction.Evenement;

public class Calendrier {
	SortedSet<Evenement> principale;
	SortedSet<Evenement> attente;
	
	public Calendrier() {
		ComparateurDates comp = new ComparateurDates();
		principale = new TreeSet<>(comp);
		attente = new TreeSet<>(comp);
	}
	
	public boolean ajouterPrincipale(Evenement evt) {
		Date date = evt.getDebut();
		SortedSet head = attente.headSet(evt);
		// TODO Ajouter l'élément à la liste principale, et afficher un message d'alerte s'il n'est pas compatible avec un autre, en complexité minimale
		return false;
	}
}

class ComparateurDates implements Comparator<Evenement> {

	@Override
	public int compare(Evenement e1, Evenement e2) {
		return (int) (e1.getDebut().getTime() - e2.getDebut().getTime());
	}
	
}