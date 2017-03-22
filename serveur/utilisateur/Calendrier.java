package serveur.utilisateur;

import java.util.*;

import serveur.bdd.Backupable;
import share.interaction.Evenement;

public class Calendrier implements Backupable{
	SortedSet<Evenement> principale;
	SortedSet<Evenement> attente;
	
	public Calendrier() {
		ComparateurEvt comp = new ComparateurEvt();
		principale = new TreeSet<>(comp);
		attente = new TreeSet<>(comp);
	}
	
	public boolean ajouterPrincipale(Evenement evt) {
		// Possibilité pour détecter facilement les conflits: découper la journée en plage horaires, et à chaque plage horaire, 
		//	noter les événements qui l'occupent. Mais cela nécessite de repenser tout les 
		//	événements
		boolean reussi = false;
		for ( Evenement e : conflits(evt) ) 
			System.out.println("Attention : l'événement " + evt + " n'est pas compatible avec " + e);
		
		reussi = principale.add(evt);
		if (reussi && attente.remove(evt)) System.out.println("L'événement " + evt + " est passé de la liste d'attente à la liste principale");
		
		return reussi;
	}
	
	public boolean ajouterAttente(Evenement evt) {
		boolean reussi = false;
		
		for ( Evenement e : principale ) 
			if ( !e.equals(evt) && !evt.estCompatibleAvec(e) ) 
				System.out.println("Attention : l'événement " + evt + " n'est pas compatible avec " + e);
		
		reussi = attente.add(evt);
		if(reussi & principale.remove(evt)) System.out.println("L'événement " + evt + " est passé de la liste d'attente à la liste principale");
		
		return attente.add(evt);
	}
	
	public boolean surPrincipale(Evenement e) {
		return principale.contains(e);
	}
	
	public boolean surAttente(Evenement e) {
		return attente.contains(e);
	}
	
	public Set<Evenement> conflits(Evenement evt){
		Set<Evenement> conflits = new TreeSet<Evenement>();
		for ( Evenement e : principale ) if ( !e.equals(evt) && !e.estCompatibleAvec(evt))
			conflits.add(e);
		return conflits;
	}
	
	public boolean retirerPrincipale(Evenement evt) {
		return principale.remove(evt);
	}
	
	public boolean retirerAttente(Evenement evt) {
		return attente.remove(evt);
	}
	
	public boolean supprimer(Evenement evt) {
		return retirerPrincipale(evt) & !retirerAttente(evt);
	}
	
	public void afficher() {
		synchronized (System.out) {
			Iterator<Evenement> it = principale.iterator();
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

	@Override
	public int getID() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getUpdate() {
		// TODO Auto-generated method stub
		return null;
	}
}

class ComparateurEvt implements Comparator<Evenement> {

	@Override
	public int compare(Evenement e1, Evenement e2) {
		return (int) (e1.getDebut().getTime() - e2.getDebut().getTime());
	}
	
}