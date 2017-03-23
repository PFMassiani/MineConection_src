package serveur.utilisateur;

import java.io.Serializable;

import serveur.bdd.Backupable;

/* Classe mère de tout ce qui peut participer à une Interaction. */

public abstract class Utilisateur implements Backupable, Serializable {
  /**
   * 
   */
  private static final long serialVersionUID = 4074056332090552166L;
  final protected int IDENTIFIANT;
  

  public Utilisateur (int id){
    IDENTIFIANT = id;
  }

  public int getID(){
    return IDENTIFIANT;
  }
  
  public abstract share.utilisateur.Utilisateur getObjetClient();
  
}