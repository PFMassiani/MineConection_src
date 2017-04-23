package client;

import java.io.*;
import java.util.*;

import exception.InvalidCommunicationException;
import share.interaction.Evenement;
import share.utilisateur.Etudiant;

public class IHMClient {
	Scanner sc;
	
	public IHMClient() {
		sc = new Scanner(System.in);
	}
	public int menuPrincipal() {
		boolean fin = false, refaire = false;
		int choixMax = 13, choix;
		Map<Integer,String> menu = new HashMap<>();
		
		menu.put(0, "Quitter");
		menu.put(1, "Ajouter un étudiant");
		menu.put(2, "Supprimer un étudiant");
		menu.put(3, "Afficher un étudiant");
		menu.put(4, "Afficher tous les étudiants");
		menu.put(5, "Ajouter un événement");
		menu.put(6, "Supprimer un événement");
		menu.put(7, "Afficher un événement");
		menu.put(8, "Afficher tous les événements");
		menu.put(9, "Inscrire un étudiant sur la liste principale d'un événement ( dans la limite des places disponibles )");
		menu.put(10, "Inscrire un étudiant sur la liste d'attente d'un événement");
		menu.put(11, "Supprimer un étudiant d'un événement");
		menu.put(12, "Supprimer tous les évémements");
		menu.put(13, "Supprimer tous les étudiants ( supprime aussi les événements )");
		
		menu.put(42, "Voir un remake de Star Wars Episode IV : A New Hope en ASCII");
				
		
		while (!fin)
		{
			refaire = false;
			do {
				System.out.println("================= Mine Connection =================");
				System.out.println("---------------------------------------------------");
				System.out.println();
				System.out.println("Bienvenue dans Mine Connection (alpha), la ( future ? ) plateforme de gestion des événements des Mines.");

				for ( int i = 0; i < 14; i++) System.out.println(i + ". " + menu.get(i));
				
				System.out.println();
				System.out.println("\n Que voulez vous faire ?");
				
				try {
					String s = sc.nextLine();
					choix = Integer.parseInt(s);
				} catch (NumberFormatException ex) {
					choix = -1;
				}
				
				if(! menu.keySet().contains(choix)) System.out.println("Choix invalide. Merci de choisir "
						+ "un nombre entre 0 et " + choixMax); 
			} while (refaire);
			switch(choix) {
			case 0:
				fin = true;
				System.out.println("The program has been terminated");
				break;
			case 1:
				ajouterEtudiant();
				break;
			case 2:
				supprimerEtudiant();
				break;
			case 3:
				afficherEtudiant();
				break;
			case 4:
				afficherTousEtudiants();
				break;
			case 5:
				ajouterEvenement();
				break;
			case 6:
				supprimerEvenement();
				break;
			case 7:
				afficherEvenement();
				break;
			case 8:
				afficherTousEvenements();
				break;
			case 9:
				surListePrincipale();
				break;
			case 10:
				surListeAttente();
				break;
			case 11:
				supprimerInscrit();
				break;
			case 12:
				supprimerTousEvenements();
				break;
			case 13:
				supprimerTousEtudiants();
				break;
//			case 42:
//				starWars();
//				break;
				default:
					break;
			}
			try {
				Thread.sleep(3);
			} catch (InterruptedException ex) {
				System.out.println(ex.getMessage());
				ex.printStackTrace();
			}
		}
		
		return 0;
	}
	
	public void ajouterEtudiant() {
		System.out.println();
		System.out.println("Menu de création d'étudiant");
		System.out.println();
		System.out.println("L'action peut être interrompue à tout moment en rentrant la valeur -1");
		System.out.println("Veuillez remplir les informations suivantes.");
		try {
		System.out.println("Nom ? ");
		String nom = sc.nextLine();
		arret(nom);
		System.out.println("Prénom ? ");
		String prenom = sc.nextLine();
		arret(prenom);
		System.out.println("Téléphone ? ");
		String telephone = sc.nextLine();
		arret(telephone);
		int promo = -1;
		boolean fin;
		String s;
		do {
			try {
				System.out.println("Numéro de promotion ? ");
				s = sc.nextLine();
				arret(s);
				promo = Integer.parseInt(s);
				fin = true;
			} catch (Exception e) {
				fin = false;
			}
		} while (!fin);
		
		Etudiant e = Etudiant.nouveau(nom, prenom, telephone, promo);
		if ( e != null) System.out.println("L'étudiant a été correctement ajouté à la base de données");
		else System.out.println("Échec de l'ajout de l'étudiant. L'administrateur système espère sincèrement que M. Hermant ne verra cette ligne qu'en examinant le code.");
		
		} catch (ArretException ex) {
			System.out.println("Procédure interrompue. Retour au menu");
		}
	}
	
	public void supprimerEtudiant() {
		System.out.println();
		System.out.println("Menu de suppression d'étudiant");
		System.out.println();
		System.out.println("L'action peut être interrompue à tout moment en rentrant la valeur -1");
		System.out.println("Quel est l'identifiant de l'étudiant à supprimer ? ");
		String s = sc.nextLine();
		try{
			arret(s);
			int id = Integer.parseInt(s);
			boolean reussi = Etudiant.supprimer(id);
			if (reussi) System.out.println("L´étudiant a été supprimé avec succès !");
			else System.out.println("La suppression a échoué.");
		} catch (ArretException ex) {
			System.out.println("Procédure interrompue. Retour au menu");
		}
	}
	
	public void afficherEtudiant() {
		System.out.println();
		System.out.println("Menu d'affichage d'étudiant");
		System.out.println();
		System.out.println("L'action peut être interrompue à tout moment en rentrant la valeur -1");
		System.out.println("Quel est l'identifiant de l'étudiant à afficher ? ");
		String s = sc.nextLine();
		try{
			arret(s);
			int id = Integer.parseInt(s);
			Etudiant e = Etudiant.chercher(id);
			if (e != null) e.afficher();
			else System.out.println("L'étudiant n'a pas pu être chargé");
		} catch (ArretException ex) {
			System.out.println("Procédure interrompue. Retour au menu");
		} catch (InvalidCommunicationException ex) {
			System.out.println("L'identifiant renseigné ne correspond à aucun Étudiant. Merci de renseigner un identifiant valide.");
		}
	}
	
	public void afficherTousEtudiants() {
		System.out.println();
		Set<Etudiant> ets = Etudiant.getAll();
		for (Etudiant e : ets) {
			if ( e == null) System.out.println("L'étudiant n'a pas pu être chargé");
			else System.out.println(e.toString());
		}
		System.out.println("Entrez n'importe quoi pour continuer");
		sc.nextLine();
	}
	
	public void ajouterEvenement() {
		System.out.println();
		System.out.println("Menu d'ajout d'événement");
		System.out.println();
		System.out.println("L'action peut être interrompue à tout moment en rentrant la valeur -1");
		System.out.println("Veuillez remplir les informations suivantes.");
		try {
			System.out.println("Quel est le nom de l'événement ? ");
			String nom = sc.nextLine();
			arret(nom);
			System.out.println("Quelle est la description de l'événement ? (Retours à la ligne autorisés, finir la description par FIN)");
			String desc = "", append = "";
			String end = "";
			while(end.compareTo("FIN") != 0) {
				append = sc.nextLine();
				arret(append);
				if (append.length() >= 3) end = append.substring(append.length() - 3);
				else end = "";
				desc += append;
			}
			desc = desc.substring(0, desc.length() - 3);
			System.out.println("Combien y a-t-il de places ? ");
			String s = sc.nextLine();
			arret(s);
			int places = Integer.parseInt(s);
			System.out.println("Quelle est la date de début ? (JJ/MM/AAAA)");
			String str_date = sc.nextLine();
			arret(str_date);
			java.sql.Date date = parserDate(str_date);
			System.out.println("À quelle heure débute l'événement ? (HH:MM)");
			String str_heure = sc.nextLine();
			arret(str_heure);
			int debutH = parserH(str_heure);
			int debutM = parserM(str_heure);
			System.out.println("Combien de temps dure l'événement (min) ?");
			String str_duree = sc.nextLine();
			arret(str_duree);
			int duree = Integer.parseInt(str_duree);
			System.out.println("Quel est l'identifiant du créateur ?");
			String str_idCrea = sc.nextLine();
			int idCrea = Integer.parseInt(str_idCrea);
			
			System.out.println();
			Evenement e = Evenement.nouveau(nom, desc, places, date, debutH, debutM, duree, Etudiant.chercher(idCrea));
			if ( e != null ) System.out.println("L'événement a été créé avec succès");
			else System.err.println("Échec de la création de l'événement. Au risque de me répêter, cette phrase ne doit pas être visible à la soutenance...");
		} catch(ArretException ex) {
			System.out.println("Procédure interrompue. Retour au menu.");
		} catch (NumberFormatException | StringIndexOutOfBoundsException ex) {
			System.err.println("La donnée saisie est invalide. Merci de respecter le format indiqué");
		} catch (InvalidCommunicationException ex) {
			System.err.println("L'identifiant renseigné ne correspond à aucun Étudiant. Merci de rentrer un identifiant valide.");
		}
	}
	public void supprimerEvenement() {
		System.out.println();
		System.out.println("Menu de suppression d'un événement");
		System.out.println();
		System.out.println("L'action peut être interrompue à tout moment en rentrant la valeur -1");
		System.out.println("Quel est l'identifiant de l'événement à supprimer ?");
		try {
			String str_id = sc.nextLine();
			arret(str_id);
			int idSuppr = Integer.parseInt(str_id);
			boolean reussi = Evenement.supprimer(idSuppr);
			if ( reussi ) System.out.println("L'événement a été supprimé avec succès.");
			else System.err.println("Échec de la suppression de l'événement.");
		} catch(ArretException ex) {
			System.out.println("Procédure interrompue. Retour au menu.");
		} catch (NumberFormatException ex) {
			System.out.println("La donnée saisie est invalide. Merci de respecter le format indiqué");
		}
	}
	public void afficherEvenement() {
		System.out.println();
		System.out.println("Menu d'affichage d'événement");
		System.out.println();
		System.out.println("L'action peut être interrompue à tout moment en rentrant la valeur -1");
		System.out.println("Quel est l'identifiant de l'événement à afficher ? ");
		String s = sc.nextLine();
		try{
			arret(s);
			int id = Integer.parseInt(s);
			Evenement e = Evenement.chercher(id);
			if (e != null) e.afficher();
			else System.out.println("L'événement n'a pas pu être chargé");
		} catch (ArretException ex) {
			System.out.println("Procédure interrompue. Retour au menu");
		} 
	}
	public void afficherTousEvenements() {
		System.out.println();
		Set<Evenement> evts = Evenement.getAll();
		for (Evenement e : evts) {
			if ( e == null) System.out.println("L'événement n'a pas pu être chargé");
			e.afficher();
		}
		System.out.println("Entrez n'importe quoi pour continuer");
		sc.nextLine();
	}
	public void surListePrincipale() {
		System.out.println();
		System.out.println("Menu d'inscription à la liste principale un événement");
		System.out.println();
		System.out.println("La procédure peut être interrompue à tout moment en rentrant la valeur -1");
		System.out.println("À quel événement l'inscription doit-elle être faite ? ");
		try {
			String str_idEvt = sc.nextLine();
			arret(str_idEvt);
			int idEvt = Integer.parseInt(str_idEvt);
			
			Evenement evt = Evenement.chercher(idEvt);
			if(evt.estComplet()) System.out.println("Cet événement est complet. L'inscription sera faite sur liste d'attente");
			System.out.println("Quel est l'étudiant à inscrire ?");
			String str_idEtu = sc.nextLine();
			arret(str_idEtu);
			int idEtu = Integer.parseInt(str_idEtu);
			Etudiant et = Etudiant.chercher(idEtu);
			boolean attente = false, reussi = et.ajouterPrincipale(evt);
			if (!reussi) attente = et.ajouterAttente(evt);
			
			if (reussi) System.out.println("L'étudiant a été inscrit sur la liste principale avec succès");
			else if (!reussi & attente) System.out.println("L'étudiant a été inscrit sur la liste d'attente avec succès");
			else System.out.println("Échec de l'opération");
		} catch(ArretException ex) {
			System.out.println("Procédure interrompue. Retour au menu.");
		} catch (NumberFormatException ex) {
			System.out.println("Merci de rentrer un nombre.");
		} catch (InvalidCommunicationException ex) {
			System.out.println("L'identifiant choisi ne correspond à aucun " + ex.getType() + ". Merci de choisir un identifiant valide.");
		}
	}
	public void surListeAttente() {
		System.out.println();
		System.out.println("Menu d'inscription à la liste d'attente un événement");
		System.out.println();
		System.out.println("La procédure peut être interrompue à tout moment en rentrant la valeur -1");
		System.out.println("À quel événement l'inscription doit-elle être faite ? ");
		try {
			String str_idEvt = sc.nextLine();
			arret(str_idEvt);
			int idEvt = Integer.parseInt(str_idEvt);
			
			Evenement evt = Evenement.chercher(idEvt);
			System.out.println("Quel est l'étudiant à inscrire ?");
			String str_idEtu = sc.nextLine();
			arret(str_idEtu);
			int idEtu = Integer.parseInt(str_idEtu);
			Etudiant et = Etudiant.chercher(idEtu);
			boolean surPrincipale = evt.principale().contains(et);
			boolean reussi = et.ajouterAttente(evt);
			
			if (reussi & !surPrincipale) System.out.println("L'étudiant a été inscrit sur la liste d'attente avec succès !	");
			else if (reussi & surPrincipale) System.out.println("L'étudiant a été transféré de la liste principale à la liste d'attente avec succès !");
			else System.out.println("Échec de l'opération");
		} catch(ArretException ex) {
			System.out.println("Procédure interrompue. Retour au menu.");
		} catch (NumberFormatException ex) {
			System.out.println("Merci de rentrer un nombre.");
		} catch (InvalidCommunicationException ex) {
			System.out.println("L'identifiant choisi ne correspond à aucun " + ex.getType() + ". Merci de choisir un identifiant valide.");
		}
	}
	public void supprimerInscrit() {
		System.out.println();
		System.out.println("Menu de suppression d'un étudiant de la liste des inscrits à un événement");
		System.out.println();
		System.out.println("La procédure peut être interrompue à tout moment en rentrant la valeur -1");
		System.out.println("De quel événement la supression doit-elle être faite ? ");
		
		try {
			String str_idEvt = sc.nextLine();
			arret(str_idEvt);
			int idEvt = Integer.parseInt(str_idEvt);
			
			Evenement evt = Evenement.chercher(idEvt);
			System.out.println("Quel est l'étudiant concerné ?");
			String str_idEtu = sc.nextLine();
			arret(str_idEtu);
			int idEtu = Integer.parseInt(str_idEtu);
			Etudiant et = Etudiant.chercher(idEtu);
			
			boolean reussi = et.supprimerInscription(evt);
			
			if (reussi) System.out.println("Suppression réussie");
			else System.out.println("Échec de la suppression");
		} catch (ArretException ex) {
			System.out.println("Procédure interrompue. Retour au menu.");
		} catch (NumberFormatException ex) {
			System.out.println("Merci de rentrer un nombre.");
		} catch (InvalidCommunicationException ex) {
			System.out.println("L'identifiant choisi ne correspond à aucun " + ex.getType() + ". Merci de choisir un identifiant valide.");
		}
	}
	public void supprimerTousEvenements() {
		System.out.println();
		Set<Evenement> evts = Evenement.getAll();
		boolean reussi = true;
		for (Evenement evt : evts) reussi &= evt.supprimer();
		
		if (reussi) System.out.println("Tous les événements ont été supprimés.");
		else System.out.println("Certains événements n'ont pas pu être supprimés");
	}
	public void supprimerTousEtudiants() {
		System.out.println();
		Set<Etudiant> etus = Etudiant.getAll();
		boolean reussi = true;
		for (Etudiant et : etus) reussi &= Etudiant.supprimer(et.getID());

		if (reussi) System.out.println("Tous les étudiants ont été supprimés.");
		else System.out.println("Certains étudiants n'ont pas pu être supprimés");
	}
//	public void starWars() {
//		try {
//			System.out.println("Star Wars");
//			Process p = Runtime.getRuntime().exec("starWars.sh");
//		} catch (Exception ex) {
//			System.out.println("Erreur d'exécution");
//			ex.printStackTrace();
//		}
//	}
	// TODO Ne donne pas la bonne date
	private java.sql.Date parserDate(String sDate) throws NumberFormatException{
		String sY = sDate.substring(6,10);
		String sM = sDate.substring(3,5);
		String sD = sDate.substring(0,2);
		int y = Integer.parseInt(sY), m = Integer.parseInt(sM), d = Integer.parseInt(sD);
		int anneeBissextile = 1972, nBiss = 0;
		while (anneeBissextile <= y) {
			nBiss ++;
			anneeBissextile += 4;
		}
		long ms = (y - 1970 + nBiss) * 1000 * 3600 * 24 * 365;
		int nJours = 0;
		for (int i = 1; i < m; i++) {
			if (i == 2) nJours = 28;
			else if (i % 2 == 1) {
				if (i <= 7 ) nJours = 31;
				else nJours = 30;
			}
			else {
				if (i <= 7) nJours = 30;
				else nJours = 31;
			}
			ms += nJours * 24 * 3600 * 1000;
		}
		ms += d * 24 * 3600 * 1000;
		
		return new java.sql.Date(ms);
//		return new java.sql.Date(y,m,d);
	}
	private int parserH(String s) throws NumberFormatException{
		return Integer.parseInt(s.substring(0,2));
	}
	private int parserM(String s) throws NumberFormatException{
		return Integer.parseInt(s.substring(3,5));
	}
	private void arret(String s) throws ArretException {
		try {
			if (Integer.parseInt(s) == -1) throw new ArretException();
		} catch (NumberFormatException ex) {}
	}
	
	public static void main(String[] args) {
		IHMClient ihm  = new IHMClient();
		ihm.menuPrincipal();
	}
}

class ArretException extends Exception {
	public ArretException() {super("");}
}
