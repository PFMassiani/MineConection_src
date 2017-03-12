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
          case CHARGER:
            envoyerObjet(com.getType(), com.getID());
            break;
          case SAUVEGARDER:
            sauvegarderObjet(com.getObjet());
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
      c = Class.forName(type.getNomClasse());
      Method m = c.getDeclaredMethod("chercher", int.class);
      
      Object retour = m.invoke(null, id);
      
      oos.writeObject(c.cast(retour));
      
    } catch (ClassNotFoundException| IllegalAccessException | IllegalArgumentException | InvocationTargetException | IOException e){
      e.printStackTrace();
    } catch (NoSuchMethodException e){
      System.err.println("La classe " + c + " n'implémente pas la méthode ? chercher(int id)");
      e.printStackTrace();
    }
  }
  
  public boolean sauvegarderObjet(Object o){
    boolean reussi = false;
    
    Class<?> c = o.getClass();
    try {
    	
    	Method m = c.getDeclaredMethod("update");
    	m.invoke(c.cast(o));
    	
    } catch (NoSuchMethodException | 
    		IllegalAccessException | 
    		IllegalArgumentException | 
    		InvocationTargetException ex) {
    	ex.printStackTrace();
    }
    
    return reussi;
  }
  
  public boolean supprimer (Communication com){
	  boolean reussi = false;
	  System.out.println("Suppression en cours...");
	  Backupable o = com.getObjet();

	  
	  try {
		  // On récupère la classe de o
		  Class<?> c = Class.forName(com.getType().getNomClasse());
		  System.out.println("-----> Classe de l'objet :" + c);
		  // On récupère la méthode supprimer()
		  Method m;
		  if ( o != null) m = c.getDeclaredMethod("supprimer");
		  else m = c.getDeclaredMethod("supprimer", int.class);
		  System.out.println("-----> Méthode de suppression :" + m);


		  // Et si on la trouve, on l'applique
		  if (o != null) reussi = (boolean) m.invoke(c.cast(o));
		  else reussi = (boolean) m.invoke(null,com.getID());

		  System.out.println("-----> Réussite :" + reussi);

	  } catch (NoSuchMethodException | 
			  IllegalAccessException | 
			  IllegalArgumentException | 
			  InvocationTargetException | ClassNotFoundException e) {

		  e.printStackTrace();
	  }



	  return reussi;
  }
 

  public void envoyerIDs (TypeBackupable type){
    Class<?> c = null;
    try{

      c = Class.forName(type.getNomClasse());
      Method m = c.getDeclaredMethod("ids");
      Set<?> ids = (Set<?>) m.invoke(null);

      oos.writeObject(ids);

    } catch (ClassNotFoundException | 
        IllegalAccessException | 
        IllegalArgumentException | 
        InvocationTargetException e){
      e.printStackTrace();
    } catch (NoSuchMethodException e){
      System.err.println("Erreur: la classe " + c.getName() + " n'implémente pas la méthode static Set<Integer> supprimer()");
      e.printStackTrace();
    } catch (IOException e){
      e.printStackTrace();
    }
  }

  public void envoyerAll(TypeBackupable type){
    Class<?> c = null;
    try{

      c = Class.forName(type.getNomClasse());
      Method m = c.getDeclaredMethod("getAll");
      Set<?> all = (Set<?>) m.invoke(null);

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
