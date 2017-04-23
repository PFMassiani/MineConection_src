package serveur;

import share.utilisateur.Calendrier;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.*;
import java.util.Set;
import java.io.*;

import exception.*;
import serveur.bdd.Backupable;
import serveur.bdd.DAO;
import share.communication.Action;
import share.communication.Communication;
import share.communication.TypeBackupable;

@SuppressWarnings("unchecked")
public class ServeurVirtuel extends Thread {

	@SuppressWarnings("unused")

	private Socket client;
	private InputStream is;
	private ObjectInputStream ois;
	private OutputStream os;
	private ObjectOutputStream oos;

	private PrintWriter log;

	private boolean fin = false, pause = false;

	public ServeurVirtuel(Socket client,String nomClient){
		//Connexion avec le client
		this.client = client;
		try {
			is = client.getInputStream();
			ois = new ObjectInputStream(is);
			os = client.getOutputStream();
			oos = new ObjectOutputStream(os);

			//Création du fichier de log
			File fichier = new File("../logs" + nomClient+"-client.log");
			fichier.createNewFile();
			log = new PrintWriter("../logs" + nomClient+"-client.log");

		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("Connexion client refusée");
		}
	}

	public void pause(){
		pause = true;
	}
	public void reprendre(){
		pause = false;
	}
	public void fin(){
		fin = true;
	}

	@Override
	public void run(){
		while (!fin){
			if (!pause){
				try{
					Object o = ois.readObject();
					if (!(o instanceof Communication)) throw new InvalidCommunicationException("La communication n'est pas du type Communication");
					Communication com = (Communication) o;
					Action action = com.getAction();

					System.out.println("Communication client reçue: \n" + com.getDescription());
					log.println("Communication client reçue: \n" + com.getDescription());
					log.flush();

					if (com.getObjet() instanceof Calendrier && com.getAction() == Action.SAUVEGARDER) {
						Calendrier cal = (Calendrier) com.getObjet();
						Set<Integer> principale = cal.getPrincipaleClient(), attente = cal.getAttenteClient();
						principale.clear();
						attente.clear();
						Set<Integer> s;
						o = ois.readObject();
						if (!(o instanceof Set)) throw new InvalidCommunicationException(""); 
						s = (Set<Integer>) o;
						for (int i : s) principale.add(i);
						o = ois.readObject();
						if (!(o instanceof Set)) throw new InvalidCommunicationException(""); 
						s = (Set<Integer>) o;
						for (int i : s) attente.add(i);
						com.setObjet(cal);
					}
					switch (action){

					case NOUVEAU:
						nouveau(com);
						break;
					case CHARGER:
						if(com.getType() == TypeBackupable.EVENEMENT) {
							System.out.println();
						}
						envoyerObjet(com.getType(), com.getID());
						break;
					case SAUVEGARDER:
						sauvegarderObjet(com.getObjet(),com.getType());
						break;
					case SUPPRIMER:
						supprimer(com);
						break;
					case GET_IDS:
						envoyerIDs(com.getType());
						break;
					case GET_ALL:
						envoyerAll(com.getType());
						break;
					default:
						break;
					}
					log.flush();

				} catch (ClassNotFoundException | IOException | InvalidCommunicationException e){
					System.err.println(e.getMessage());
					log.println("ERREUR : " + e.getMessage());
				}
			}
		}
	}

	public void envoyerObjet(TypeBackupable type, int id){
		DAO<? extends Backupable> dao = dao(type);
		Class<?> cDAO = null;

		try{
			cDAO = dao.getClass();
			Method m = cDAO.getMethod("chercher", int.class);

			Object retour = m.invoke(dao, id);

			oos.writeObject(Class.forName(type.getNomClasse()).cast(retour));
			System.out.println("-------> Objet envoyé");

		} catch (ClassNotFoundException| IllegalAccessException | IllegalArgumentException | InvocationTargetException | IOException | NoSuchMethodException e){
			System.err.println(e.getStackTrace());
			log.println("ERREUR : " + e.getStackTrace());
		}
	}

	public void sauvegarderObjet(Object o, TypeBackupable type){
		boolean reussi = false;
		DAO<? extends Backupable> dao = dao(type);
		Class<?> c = null;


		try {
			Class<?> cDAO = dao.getClass();
			c = Class.forName(type.getNomClasse());

			Method m = cDAO.getMethod("update",Class.forName("serveur.bdd.Backupable"));
			reussi = (boolean) m.invoke(dao,c.cast(o));

			oos.writeObject(reussi);
			if (reussi) {
				System.out.println("-------> Objet sauvegardé");
			}

		} catch (NoSuchMethodException | 
				IllegalAccessException | 
				IllegalArgumentException | 
				ClassNotFoundException |
				IOException |
				InvocationTargetException ex) {
			System.err.println(ex.getStackTrace());
			log.println("ERREUR : " + ex.getStackTrace());
		}
	}

	public void supprimer (Communication com){
		boolean reussi = false;
		Backupable o = com.getObjetBackupable();
		DAO<? extends Backupable> dao = dao(com.getType());

		try {
			// On récupère la classe de o
			Class<?> cDAO = dao.getClass();
			// On récupère la méthode supprimer()
			Method m;
			if ( o != null) m = cDAO.getMethod("supprimer",Class.forName("serveur.bdd.Backupable"));
			else m = cDAO.getMethod("supprimer", int.class);


			// Et si on la trouve, on l'applique
			if (o != null) reussi = (boolean) m.invoke(dao,com.getObjet());
			else reussi = (boolean) m.invoke(dao,com.getID());

			oos.writeObject(reussi);
			if(reussi) {
				System.out.println("-------> Objet supprimé");
			}

		} catch (NoSuchMethodException | 
				IllegalAccessException | 
				IllegalArgumentException | 
				InvocationTargetException | ClassNotFoundException | IOException e) {
			System.err.println(e.getStackTrace());
			log.println("ERREUR : " + e.getStackTrace());

		}
	}

	public void nouveau (Communication com) {
		TypeBackupable type = com.getType();
		Class<?> cDAO = null, c = null;
		DAO<? extends Backupable> dao = dao(type);

		try {
			cDAO = dao.getClass();
			c = Class.forName(type.getNomClasse());

			Method m = cDAO.getMethod("nouveau", Class.forName("serveur.bdd.Backupable"));
			Object o = m.invoke(dao, c.cast(com.getObjet()));

			oos.writeObject(c.cast(o));
			System.out.println("-------> Objet créé");

		} catch (ClassNotFoundException | 
				IllegalAccessException | 
				IllegalArgumentException | 
				IOException |
				InvocationTargetException e){
			System.err.println(e.getStackTrace());
			log.println("ERREUR : " + e.getStackTrace());
		} catch (NoSuchMethodException e){
			System.err.println("Erreur: la classe " + c.getName() + " n'implémente pas la méthode getNewID()");
			System.err.println(e.getStackTrace());
			log.println("ERREUR : la classe " + c.getName() + " n'implémente pas la méthode getNewID()");
			log.println("ERREUR : " + e.getStackTrace());
		}
	}

	public void envoyerIDs (TypeBackupable type){
		Class<?> c = null;
		DAO<? extends Backupable> dao = dao(type);
		try{

			c = dao.getClass();
			Method m = c.getMethod("ids");
			Set<?> ids = (Set<?>) m.invoke(dao);

			oos.writeObject(ids);
			System.out.println("-------> IDs envoyé");


		} catch ( IllegalAccessException | 
				IllegalArgumentException | 
				InvocationTargetException e){
			e.printStackTrace();
		} catch (NoSuchMethodException e){
			System.err.println("Erreur: la classe " + c.getName() + " n'implémente pas la méthode static Set<Integer> ids()");
			System.err.println(e.getStackTrace());
			log.println("ERREUR : la classe " + c.getName() + " n'implémente pas la méthode static Set<Integer> ids()");
			log.println("ERREUR : " + e.getStackTrace());
		} catch (IOException e){
			System.err.println(e.getStackTrace());
			log.println("ERREUR : " + e.getStackTrace());
		}
	}

	public void envoyerAll(TypeBackupable type){
		Class<?> c = null;
		DAO<? extends Backupable> dao = dao(type);
		try{

			c = dao.getClass();
			Method m = c.getMethod("getAll");
			Set<?> all = (Set<?>) m.invoke(dao);

			oos.writeObject(all);
			System.out.println("-------> Objets envoyés");

		} catch (IllegalAccessException | 
				IllegalArgumentException | 
				InvocationTargetException e){
			System.err.println(e.getStackTrace());
		} catch (NoSuchMethodException e){
			System.err.println("Erreur: la classe " + c.getName() + " n'implémente pas la méthode static Set<?> getAll()");
			System.err.println(e.getStackTrace());
			log.println("ERREUR : la classe " + c.getName() + " n'implémente pas la méthode static Set<?> getAll()");
			log.println("ERREUR : " + e.getStackTrace());
		} catch (IOException e){
			System.err.println(e.getStackTrace());
			log.println("ERREUR : " + e.getStackTrace());
		}
	}
	private DAO<? extends Backupable> dao(TypeBackupable type){
		try {
			Class<?> c = Class.forName(type.getNomDAO());
			Method m = c.getMethod("getInstance");
			DAO<? extends Backupable> dao = (DAO<? extends Backupable>) m.invoke(null);
			return dao;

		} catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException | IllegalAccessException ex) {
			System.err.println(ex.getMessage());
			System.err.println(ex.getStackTrace());
			log.println("ERREUR : " + ex.getMessage());
			log.println("ERREUR : " + ex.getStackTrace());
		}
		return null;
	}
}
