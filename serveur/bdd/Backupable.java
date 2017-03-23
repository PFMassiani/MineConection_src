package serveur.bdd;

import java.io.Serializable;

// Caractérise le fait qu'on puisse être stocké dans une BDD: on doit avoir un ID, et on doit pouvoir être mis à jour.

public interface Backupable extends Serializable{
  
  public int getID();
  public String getUpdate();
}
