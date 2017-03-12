package serveur.bdd;

// Caractérise le fait qu'on puisse être stocké dans une BDD: on doit avoir un ID, et on doit pouvoir être mis à jour.

public interface Backupable {
  
  public int getID();
  public String getUpdate();
}
