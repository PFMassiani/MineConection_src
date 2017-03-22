package serveur.interaction;

import java.io.Serializable;
import java.util.Set;
import java.util.HashSet;

import serveur.bdd.Backupable;
import serveur.utilisateur.Utilisateur;

// Classe mère de PAPS et de Evenement

public abstract class Interaction implements Backupable, Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = 6376358620587929389L;
  // -----------------------------------------------------------------------------------------------------------------------
  // ATTRIBUTS -------------------------------------------------------------------------------------------------------------
  // -----------------------------------------------------------------------------------------------------------------------
  
  protected String nom, description;
  protected int places, placesRestantes;
  protected Utilisateur createur;
  protected boolean placesUpdate = false;
  
  protected Set<serveur.utilisateur.Etudiant> principale,attente;
//FileAttente attente;
  
  protected final int IDENTIFIANT;
  
  public final int FALSE = 0, TRUE = 1;
  
  protected Interaction(int id, String n, String d, int p, Utilisateur c){
    IDENTIFIANT = id;
    nom = n;
    description = d;
    places = p;
    placesRestantes = p;
    createur = c;
    
    principale = new HashSet<>();
    attente = new HashSet<>();
  }
  
  public Interaction(int id, String n, int p, Utilisateur c){
    this(id,n,"",p,c);
  }
  public Interaction(share.interaction.Interaction inter) {
	  this(inter.getID(),inter.getNom(),inter.getDescription(),inter.getPlaces(),inter.getCreateur());
  }
  
  public int getID(){
    return IDENTIFIANT;
  }
  public String getNom(){
    return nom;
  }
  public String getDescription(){
    return description;
  }
  public int getPlaces(){
    return places;
  }
  public int getPlacesRestantes(){
    return placesRestantes;
  }
  public Utilisateur getCreateur(){
    return createur;
  }
  public boolean estComplet(){
    return placesRestantes == 0;
  }
  public void setNom(String n){
    nom = n;
  }
  public void setDescription(String d){
    description = d;
  }
  public void setCreateur(Utilisateur c){
    createur = c;
  }
  
}
