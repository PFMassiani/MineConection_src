import exception.*;
import java.util.*;

import java.sql.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.sql.Date;
import java.sql.ResultSet;

import serveur.bdd.Connexion;
import share.interaction.Evenement;
import share.utilisateur.Calendrier;
import share.utilisateur.Etudiant;
import share.communication.Action;
import share.communication.Communication;
import share.communication.ConnexionServeur;
import share.communication.TypeBackupable;

@SuppressWarnings("unused")
public class Test {
	private static int port = 10000;
	public static void main(String[] args0) throws DuplicateIdentifierException{
		
//		testServeur();
		viderBase();
		System.out.println("Done!");

	}
	
	public static void viderBase() {
		String drop = "DROP TABLE ?";
		String nom = "";
		String request = "SELECT TABLE_NAME FROM information_schema.tables WHERE TABLE_SCHEMA='mine_connection' AND TABLE_NAME NOT IN ('Association','Evenement','Etudiant')";
		try {
			ResultSet r = Connexion.getConnection().prepareStatement(request).executeQuery();
			PreparedStatement stmt = Connexion.getConnection().prepareStatement(drop);
			while (r.next()) {
				nom = r.getString("TABLE_NAME");
				Connexion.getConnection().prepareStatement("DROP TABLE " + nom).executeUpdate();
			}
		} catch(SQLException ex) {
			System.out.println("SQLException: " + ex.getMessage());
			System.out.println("SQLState: " + ex.getSQLState());
			System.out.println("VendorError: " + ex.getErrorCode());
		}
	}

	public static void remplirEtudiant(){
		String nom[] = {"Massiani", "Martinelli","Legrand","Li"};
		String prenom[] = {"Pierre-François", "Giacomo","Domitille","Nicolas"};
		String numeros[] = {"01","02","03","04"};
		int promo = 16;

		for (int i = 0; i < 4; i++){
			Etudiant.nouveau(nom[i],prenom[i],numeros[i],promo); 
		}
		System.out.println("Table remplie !");
	}

	public static void viderEtudiant(){
		Set<Integer> ids = Etudiant.ids();
		for (int i : ids) Etudiant.supprimer(i);
			
	}

	public static void afficherEtudiant(){
		Set<Integer> ids = Etudiant.ids();
		for (int i : ids){
//			System.out.println(Etudiant.chercher(i));
		}
	}

	public static void remplirEvenement(){
		String[] nom = {"Soirée Minception", "Amphi retap"};
		String[] description = {"La soirée de la meilleure liste", "Test"};
		int[] places = {10,20};
		java.sql.Date[] date = {new java.sql.Date((new java.util.Date()).getTime()), new java.sql.Date(1000000000)};
		int[] debutH = {1,2}, debutM = {30,40}, duree = {3,4};
		Etudiant[] e = {Etudiant.getRandomStudent(), Etudiant.getRandomStudent()};

		for (int i = 0; i < 2; i++) Evenement.nouveau(nom[i],description[i], places[i], date[i], debutH[i], debutM[i],duree[i],e[i]);
	}

	public static void viderEvenement(){
		Set<Integer> id = Evenement.ids();
		Iterator<Integer> it = id.iterator();	
		while (it.hasNext()) Evenement.supprimer(it.next());
	}

	public static Evenement evenementParticipants(){
////		Evenement evt = Evenement.nouveau("Événement test", "Test", 10, new java.sql.Date((new java.util.Date()).getTime()), 10,20,30,Etudiant.chercher(71));
//		Set<Integer> ids = Etudiant.ids();
//		for (int i : ids) evt.ajouter(Etudiant.chercher(i));
//		return evt;
		return null;
	}
	public static void afficherEvenement() {
		Set<Evenement> evts = Evenement.getAll();
		for(Evenement e : evts) System.out.println(e.toString());
	}

	public static void testerAntecedence(){
		Set<Integer> ids = Evenement.ids();
		Evenement e,f;
		for (int i : ids){
			for (int j : ids){
				e = Evenement.chercher(i);
				f = Evenement.chercher(j);
				System.out.println(e + " est avant " + f + " : " + e.estAvant(f));
			}
		}
	}

	public static void testerCompatibilite(){
		Set<Integer> ids = Evenement.ids();
		Evenement e,f;
		for (int i : ids){
			for (int j : ids){
				e = Evenement.chercher(i);
				f = Evenement.chercher(j);
				System.out.println(e + " est compatible avec " + f + " : " + e.estCompatibleAvec(f));
			}
		}
	}

	public static void testServeur() {
		
		Communication com;
		Etudiant et;
		Evenement evt;
		Set<Etudiant> allStudents;
		Set<Integer> allIDs;
		int id = 0;
		try {
			Socket serv = new Socket ("localhost", port);
			ObjectOutputStream oos = new ObjectOutputStream(serv.getOutputStream());
			ObjectInputStream ois = new ObjectInputStream(serv.getInputStream());
			
			// CONTRE EXEMPLE MINIMAL
//			viderEvenement();
//			remplirEvenement();
//			evt = Evenement.getRandomEvent();
//			System.out.println("Événement CEM : " + evt);
//			boolean reussi = evt.setNom("CEM");
//			System.out.println("Changement nom CEM réussi : " + reussi);
//			Set<Integer> idsEtudiants = Etudiant.ids();
//			System.out.println("Ids étudiants : " + idsEtudiants);
			
//			viderEvenement();
//			System.out.println("Événements vidés");
//			remplirEvenement();
//			System.out.println("Événements remplis");
//			
//			
//			// TESTS EVENEMENT
//
//			evt = Evenement.getRandomEvent();
//			System.out.println(evt + "");
//			com = new Communication (TypeBackupable.EVENEMENT, Action.CHARGER, evt.getID());
//			oos.writeObject(com);
//			evt = (Evenement) ois.readObject();
//			System.out.println("Événement chargé : " + evt);
//			
//			// Modification Evenement
//			evt = Evenement.getRandomEvent();
//
//			System.out.println("Nom modifié : " + evt.setNom("Nom modifié"));
//			System.out.println("Événement modifié. Chargement de la modification...");
//			com = new Communication(TypeBackupable.EVENEMENT, Action.CHARGER, evt.getID());
//			oos.writeObject(com);
//			evt = (Evenement) ois.readObject();
//			System.out.println("Événement modifié: " + evt);
//
//			
//			// Suppression Evenement
//			com = new Communication (TypeBackupable.EVENEMENT, Action.SUPPRIMER, evt);
//			oos.writeObject(com);
//			System.out.println("Événement supprimé");
//			
//			// TESTS ETUDIANT
//			System.out.println(ois.readObject());
//
//			Etudiant etu = Etudiant.getRandomStudent();
//			com = new Communication (TypeBackupable.ETUDIANT,Action.CHARGER,etu.getID());
//			oos.writeObject(com);
//			et = (Etudiant) ois.readObject();
//			System.out.println("Étudiant chargé: " + et);
//			
//			et.setNom("Nom changééé");
//			com = new Communication(TypeBackupable.ETUDIANT,Action.CHARGER,et.getID());
//			oos.writeObject(com);
//			et = (Etudiant) ois.readObject();
//			System.out.println("Étudiant modifié: " + et);
//			
//			com = new Communication(TypeBackupable.ETUDIANT, Action.GET_ALL,null);
//			oos.writeObject(com);
//			allStudents = (Set<Etudiant>) ois.readObject();
//			
//			com = new Communication(TypeBackupable.ETUDIANT, Action.GET_IDS,null);
//			oos.writeObject(com);
//			allIDs = (Set<Integer>) ois.readObject();
//
//			System.out.println(allStudents + "");
//			System.out.println(allIDs + "");
//			
//			com = new Communication (TypeBackupable.ETUDIANT, Action.SUPPRIMER, et);
//			oos.writeObject(com);
//			System.out.println("Étudiant supprimé");
//			
//			et = Etudiant.nouveau("Lemon", "John", "0102030405", 16);
//			System.out.println("Bienvenue à " + et);
//			System.out.println("Son calendrier est :");



			viderEvenement();
			viderEtudiant();
			viderEvenement();
			viderEtudiant();

			remplirEtudiant();
			remplirEvenement();
			afficherEtudiant();
			afficherEvenement();
			
			et = Etudiant.getRandomStudent();
			evt = Evenement.getRandomEvent();
			
			System.out.println("Étudiant choisi : " + et);
			System.out.println("Événement choisi : " + evt);
			
			et.ajouterPrincipale(evt);
			et.getCalendrier().afficher();
			
			//CEM
//			Etudiant.nouveau("Lemon","John" , "06", 16);
//			et = Etudiant.chercher(289);
//			System.out.println(et);
//
//			evt = Evenement.getRandomEvent();
//			System.out.println(evt);
//			Calendrier cal = et.getCalendrier();
//			cal.ajouterPrincipale(evt.getID());
//			cal.afficher();
			
			
			serv.close();
		} catch (IOException e) {
			e.printStackTrace();
//		} catch (InvalidParameterException e) {
//			e.printStackTrace();
//		} catch (ClassNotFoundException e) {
//			e.printStackTrace();
		}
	}
}
