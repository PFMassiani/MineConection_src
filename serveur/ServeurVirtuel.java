package serveur;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.*;
import java.util.Set;
import java.io.*;

import exception.*;
import serveur.bdd.Backupable;
import share.communication.Action;
import share.communication.Communication;
import share.communication.TypeBackupable;

public class ServeurVirtuel extends Thread {
  
  @SuppressWarnings("unused")
  private Socket client;
  private InputStream is;
  private ObjectInputStream ois;
  private OutputStream os;
  private ObjectOutputStream oos;
  
  private boolean fin = false, pause = false;
  
  public ServeurVirtuel(Socket client){
    this.client = client;
    try {
      is = client.getInputStream();
      ois = new ObjectInputStream(is);
      os = client.getOutputStream();
      oos = new ObjectOutputStream(os);
      
      System.out.println("Connexion client établie");
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
	  System.out.println("Serveur Virtuel - Thread " + Thread.currentThread());
	  int tours = 0;
    while (!fin && tours < 1){
      if (!pause){
        try{
          Object o = ois.readObject();
          if (!(o instanceof Communication)) throw new InvalidCommunicationException("La communication n'est pas du type Communication");
          System.out.println("Récupération de la communication...");
          Communication com = (Communication) o;
          System.out.println("Récupération de l'action...");
          Action action = com.getAction();
          switch (action){

          case NOUVEAU:
        	  com.setID(nouveau(com)); 
        	  break;
          case CHARGER:
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
        } catch (ClassNotFoundException e){
          e.printStackTrace();
        } catch (IOException e){
        }catch (InvalidCommunicationException e){
          e.printStackTrace();
        }
//        tours ++;
      }
    }
  }
  
  public void envoyerObjet(TypeBackupable type, int id){
    Class<?> c = null;
    try{
      c = Class.forName(type.getNomDAO());
      Method m = c.getMethod("chercher", int.class);
      
      Object retour = m.invoke(type.getDAO(), id);
      
      oos.writeObject(Class.forName(type.getNomClasse()).cast(retour));
      
    } catch (ClassNotFoundException| IllegalAccessException | IllegalArgumentException | InvocationTargetException | IOException e){
      e.printStackTrace();
    } catch (NoSuchMethodException e){
      System.err.println("La classe " + c + " n'implémente pas la méthode chercher(int id)");
      e.printStackTrace();
    }
  }
  
  public void sauvegarderObjet(Object o, TypeBackupable type){
    boolean reussi = false;
    

    try {
        Class<?> cDAO = Class.forName(type.getNomDAO());
        Class<?> c = Class.forName(type.getNomClasse());

//        // TODO Accepter en argument un backupable
    	Method m = cDAO.getMethod("update",Class.forName("serveur.bdd.Backupable"));
    	reussi = (boolean) m.invoke(type.getDAO(),c.cast(o));
    	
    	oos.writeObject(reussi);
    	
    } catch (NoSuchMethodException | 
    		IllegalAccessException | 
    		IllegalArgumentException | 
    		ClassNotFoundException |
    		IOException |
    		InvocationTargetException ex) {
    	System.out.println("EXCEPTION LANCÉE");
		System.out.println(ex.getClass());
		System.out.println(ex.getMessage());
    }
  }
  
  public void supprimer (Communication com){
	  boolean reussi = false;
	  System.out.println("Suppression en cours...");
	  Backupable o = com.getObjetBackupable();

	  
	  try {
		  // On récupère la classe de o
		  Class<?> cDAO = Class.forName(com.getType().getNomDAO());
		  Class<?> c = Class.forName(com.getType().getNomClasse());
		  System.out.println("-----> Classe du DAO :" + c);
		  System.out.println("-----> Objet : " + o);
		  System.out.println("-----> ID de l'objet : " + com.getID());
		  // On récupère la méthode supprimer()
		  Method m;
		  if ( o != null) m = cDAO.getMethod("supprimer",Class.forName("serveur.bdd.Backupable"));
		  else m = cDAO.getMethod("supprimer", int.class);
		  System.out.println("-----> Méthode de suppression :" + m);


		  // Et si on la trouve, on l'applique
		  if (o != null) reussi = (boolean) m.invoke(com.getType().getDAO(),com.getObjet());
		  else reussi = (boolean) m.invoke(com.getType().getDAO(),com.getID());

		  System.out.println("-----> Réussite :" + reussi);
		  oos.writeObject(reussi);

	  } catch (NoSuchMethodException | 
			  IllegalAccessException | 
			  IllegalArgumentException | 
			  InvocationTargetException | ClassNotFoundException | IOException e) {

		  e.printStackTrace();
	  }
  }
 
  public int nouveau (Communication com) {
	  int id = -1;
	  TypeBackupable type = com.getType();
	  Class<?> cDAO = null, c = null;

	  try {
		  cDAO = Class.forName(type.getNomDAO());
		  c = Class.forName(type.getNomClasse());
		  Method m = cDAO.getDeclaredMethod("getNewID");
		  id = (int) m.invoke(type.getDAO());
		  
		  m = c.getMethod("setIdentifiant", int.class);
		  Object o = m.invoke(c.cast(com.getObjet()), id);
		  
		  m = cDAO.getMethod("update",c);
		  m.invoke(type.getDAO(), c.cast(o));
		  
		  oos.writeObject(c.cast(o));
		  
	  } catch (ClassNotFoundException | 
			  IllegalAccessException | 
			  IllegalArgumentException | 
			  IOException |
			  InvocationTargetException e){
		  e.printStackTrace();
	  } catch (NoSuchMethodException e){
		  System.err.println("Erreur: la classe " + c.getName() + " n'implémente pas la méthode getNewID()");
		  e.printStackTrace();
	  }

	  return id;
  }

  public void envoyerIDs (TypeBackupable type){
    Class<?> c = null;
    try{

      c = Class.forName(type.getNomDAO());
      Method m = c.getMethod("ids");
      Set<?> ids = (Set<?>) m.invoke(type.getDAO());

      oos.writeObject(ids);

    } catch (ClassNotFoundException | 
        IllegalAccessException | 
        IllegalArgumentException | 
        InvocationTargetException e){
      e.printStackTrace();
    } catch (NoSuchMethodException e){
      System.err.println("Erreur: la classe " + c.getName() + " n'implémente pas la méthode static Set<Integer> ids()");
      e.printStackTrace();
    } catch (IOException e){
      e.printStackTrace();
    }
  }

  public void envoyerAll(TypeBackupable type){
    Class<?> c = null;
    try{

      c = Class.forName(type.getNomDAO());
      Method m = c.getMethod("getAll");
      Set<?> all = (Set<?>) m.invoke(type.getDAO());

      oos.writeObject(all);

    } catch (ClassNotFoundException | 
        IllegalAccessException | 
        IllegalArgumentException | 
        InvocationTargetException e){
      e.printStackTrace();
    } catch (NoSuchMethodException e){
      System.err.println("Erreur: la classe " + c.getName() + " n'implémente pas la méthode static Set<?> getAll()");
      e.printStackTrace();
    } catch (IOException e){
      e.printStackTrace();
    }
  }
}
