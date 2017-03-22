package share.interaction;

import java.io.IOException;
import java.io.Serializable;
import java.sql.*;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import exception.*;
//import serveur.utilisateur.Etudiant;
import share.utilisateur.Etudiant;
import share.utilisateur.Utilisateur;
import share.communication.Action;
import share.communication.Communication;
import share.communication.ConnexionServeur;
import share.communication.TypeBackupable;

public class Evenement extends share.interaction.Interaction {

  //------------------------------------------------------------------------------------------------------------------------
  // ATTRIBUTS ---------------------------------------------------------------------------------------------------------
  // -----------------------------------------------------------------------------------------------------------------------
  
  private static final long serialVersionUID = -1977854013851011478L;
  
  private Date date;
  private int debutH, debutM, duree;
  
  private static Set<Integer> ids;
  private Set<Etudiant> principale,attente;
  
  //------------------------------------------------------------------------------------------------------------------------
  // CONSTRUCTEURS ---------------------------------------------------------------------------------------------------------
  // -----------------------------------------------------------------------------------------------------------------------
  
  public Evenement(int id,String nom, String description, int places, Date date, int debutH, int debutM, int duree, Utilisateur createur) {
    super(id,nom,description,places,createur);

    this.debutH = debutH + ((int) debutM / 60);
    this.debutM = debutM % 60;
    int nbJours = (int) (debutH / 24);
    debutH %= 24;
    this.date = date;
    this.date.setTime(this.date.getTime() + nbJours * 24 * 60 * 60 * 10000);
    this.duree = duree;
    if (IDENTIFIANT == -1) throw new InvalidIDException("");
  }
  
  //------------------------------------------------------------------------------------------------------------------------
  // ACCESSEURS / SETTERS ---------------------------------------------------------------------------------------------------------
  // -----------------------------------------------------------------------------------------------------------------------
  
  public Date getDebut(){
    return date;
  }
  public int getDuree(){
    return duree;
  }
  public String getDureeHM(){
    int h = (int) (duree / 60);
    int m = (int) duree - 60 * h;
    String s = "";
    if (h != 0) s += h + "h ";
    s += m + "min";
    return s;
  }
  public long getFin(){
    return date.getTime() + ( ( debutH * 60 ) + debutM ) * 60 * 1000 ;
  }
  public static Set<Integer> ids(){
    return ids;
  }
  public Set<Etudiant> principale(){
    return principale;
  }
  public Set<Etudiant> inscrits(){
	Set<Etudiant> inscrits = new HashSet<>(principale);
	inscrits.addAll(attente);
    return inscrits;
  }
  public Set<Etudiant> attente(){
	  return attente;
  }
  
  @Override
  public String toString(){
//   return "Événement n°" + IDENTIFIANT + " : " + nom + 
//          ". Début : " + date + " à " + debutH + "h " + debutM + "min. " +
//          "Durée : " + getDureeHM() + ". Créé par " + createur; 
    return nom + " ( " + date + " : " + debutH + " ) ";
  }
  
  @Override
  public boolean equals(Object o){
    if (!(o instanceof Evenement)) return false;
    Evenement evt = (Evenement) o;
    return evt.getID() == IDENTIFIANT;
  }
  
  //------------------------------------------------------------------------------------------------------------------------
  // MÉTIER ---------------------------------------------------------------------------------------------------------
  // -----------------------------------------------------------------------------------------------------------------------
  
  // TODO Recherche par nom
  
  public boolean estCompatibleAvec(Evenement evt){
    Evenement premier, deuxieme;
    if (date.before(evt.date)){
      premier = this;
      deuxieme = evt;
    }
    else {
      premier = evt;
      deuxieme = this;
    }
    return premier.getFin() <= deuxieme.date.getTime();
  }
  
  public boolean estAvant(Evenement evt){
    return date.getTime() <= evt.date.getTime();
  }
  
  public boolean participe(Etudiant e){
    return principale().contains(e);
  }
  
  public boolean estInscrit(Etudiant e){
    return inscrits().contains(e);
  }
  
  public void setPlaces(int places){
    this.places = places;
    placesRestantes = places - ids().size();
    placesUpdate = true;
  }
  
  // TODO Reprendre la manière de faire des modifications à un objet:
  //	- On modifie l'objet / la liste des participants depuis la classe client, et on update tout
  // 	- On fait remonter la communication depuis la classe client jusqu'au serveur, et c'est lui qui s'occupe de tout
  // 				---> À FAIRE
  
  public boolean ajouterPrincipale(Etudiant e){
	  if (push(e)) {
		  attente.remove(e);
		  principale.add(e);
		  placesRestantes--;
		  
		  return true;
	  }
	  else return false;
  }
  
  public boolean ajouterAttente(Etudiant e){
	  if (push(e)) {
		  if (principale.remove(e)) placesRestantes++;
		  attente.add(e);
		  return true;
	  }
	  else return false;
  }
  
  // Renvoie 0 si ok, 1 si attente, 2 si déjà dedans
  public boolean ajouter(Etudiant e){
	  boolean reussi = false;
    if (!(reussi = ajouterPrincipale(e))) reussi = ajouterAttente(e);
    return reussi;
  }
  
  public void supprimerInscrit(Etudiant e){
	  if(!principale.remove(e)) attente.remove(e);
  }
  
  public static Evenement getRandomEvent() {
	  // TODO
	  return null;
  }

  public void afficher() {
	  // TODO Affichage d'un événement
  }
  
  private boolean push(Serializable b) {
	  try{
		  Communication com = new Communication(TypeBackupable.EVENEMENT, Action.SAUVEGARDER,this);
		  ConnexionServeur.getOOS().writeObject(com);
		  return ConnexionServeur.getIOS().readBoolean();
	  } catch (InvalidParameterException | IOException ex) {
		  System.out.println(ex.getMessage());
	  }
	  return false;
  }

  public int getDebutH() {
	  return debutH;
  }
  public int getDebutM() {
	  return debutM;
  }
  public Date getDate() {
	  return date;
  }
}