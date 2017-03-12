package serveur.interaction;

import java.sql.*;
import java.util.Iterator;
import java.util.Set;

import exception.*;
import serveur.utilisateur.Etudiant;
import serveur.utilisateur.Utilisateur;

public class Evenement extends Interaction {

  //------------------------------------------------------------------------------------------------------------------------
  // ATTRIBUTS ---------------------------------------------------------------------------------------------------------
  // -----------------------------------------------------------------------------------------------------------------------
  
  /**
   * 
   */
  private static final long serialVersionUID = -1977854013851011478L;

  private static DAEvenement dae = new DAEvenement();
  
  private Date date;
  private int debutH, debutM, duree;
  
  //------------------------------------------------------------------------------------------------------------------------
  // CONSTRUCTEURS ---------------------------------------------------------------------------------------------------------
  // -----------------------------------------------------------------------------------------------------------------------
  
  public Evenement(String nom, String description, int places, Date date, int debutH, int debutM, int duree, Utilisateur createur){
    super(dae.getNewID(),nom,description,places,createur);

    this.debutH = debutH + ((int) debutM / 60);
    this.debutM = debutM % 60;
    int nbJours = (int) (debutH / 24);
    debutH %= 24;
    this.date = date;
    this.date.setTime(this.date.getTime() + nbJours * 24 * 60 * 60 * 10000);
    this.duree = duree;
    
    dae.update(this);
    if (IDENTIFIANT == -1) throw new InvalidIDException("");
  }
  
  public Evenement(ResultSet r) throws SQLException{
    super(r.getInt("id"), "", "", 0, null);
    try {
      ResultSetMetaData meta = r.getMetaData();
      if (meta.getTableName(1).compareTo("Evenement") != 0) throw new InvalidResultException("Le résultat ne provient pas de la table Evenement");

      nom = r.getString("nom");
      description = r.getString("description");
      places = r.getInt("places");
      date = r.getDate("date");
      debutH = r.getInt("debut_h");
      debutM = r.getInt("debut_m");
      duree = r.getInt("duree");
      
      if (r.getInt("createur_est_etudiant") == TRUE) createur = Etudiant.chercher(r.getInt("createur_etudiant_id"));
      // else createur = Association.chercher(r.getInt("createur_assciation_id"));
      
    } catch (InvalidResultException ex) {
      System.out.println("InvalidResultException : " + ex.getMessage());
    }
  }
  
  //------------------------------------------------------------------------------------------------------------------------
  // ACCESSEURS / SETTERS ---------------------------------------------------------------------------------------------------------
  // -----------------------------------------------------------------------------------------------------------------------
  
  public Date getDebut(){
    return date;
  }
  public long getDuree(){
    return duree;
  }
  public String getDureeHM(){
    int h = (int) (duree / 60);
    int m = duree - 60 * h;
    String s = "";
    if (h != 0) s += h + "h ";
    s += m + "min";
    return s;
  }
  public long getFin(){
    return date.getTime() + ( ( debutH * 60 ) + debutM ) * 60 * 1000 ;
  }
  
  @Override
  public String getUpdate() {
    String update = "nom = '" + nom + 
        "', description = '" + description + 
        "', places = " + places + 
        ", places_restantes = " + placesRestantes + 
        ", date = '" + date + 
        "', duree = " + duree +
        ", debut_h = " + debutH +
        ", debut_m = " + debutM;
    if (createur instanceof Etudiant) update += " , createur_est_etudiant = TRUE , " +
    		                                      "createur_etudiant_id = " + createur.getID() + 
    		                                      " , createur_association_id = NULL";
    else update += " , createur_est_etudiant = FALSE , " +
    		           "createur_etudiant_id = NULL , " +
    		           "createur_association_id = " + createur.getID();
    return update;
  }
  public String getTable(){
    return "evt_" + IDENTIFIANT;
  }
  public static Set<Integer> ids(){
    return dae.ids();
  }
  public Set<Etudiant> participants(){
    return dae.participants(this);
  }
  public Set<Etudiant> inscrits(){
    return dae.inscrits(this);
  }
  //------------------------------------------------------------------------------------------------------------------------
  // UTILITAIRE ---------------------------------------------------------------------------------------------------------
  // -----------------------------------------------------------------------------------------------------------------------
  
  public boolean supprimer() throws MissingObjectException{
    return dae.supprimer(this);
  }
  
  public static boolean supprimer(int id) throws MissingObjectException{
    return dae.supprimer(id);
  }
  
  public boolean update(){
    boolean reussi = dae.update(this);
    if (reussi && placesUpdate) reussi &= dae.updateEvenement(this).isEmpty();
    if (reussi) placesUpdate = false;
    return reussi;
  }
  
  public static Evenement chercher(int id){
    return dae.chercher(id);
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
    return participants().contains(e);
  }
  
  public boolean estInscrit(Etudiant e){
    return inscrits().contains(e);
  }
  
  public void setPlaces(int places){
    this.places = places;
    placesRestantes = places - ids().size();
    placesUpdate = true;
  }
  
  public boolean ajouterPrincipale(Etudiant e){
    boolean reussi = dae.ajouterPrincipale(this, e);
    if (reussi) placesRestantes--;
    update();
    return reussi;
  }
  
  public boolean ajouterAttente(Etudiant e){
    return dae.ajouterAttente(this, e);
  }
  
  // Renvoie 0 si ok, 1 si attente, 2 si déjà dedans
  public int ajouter(Etudiant e){
    if (placesRestantes > 0 ) {
      ajouterPrincipale(e);
      return 0;
    }
    
    else if (participe(e)) return 2;
    
    else {
      ajouterAttente(e);
      return 0;
    }
  }
  
  public boolean supprimerInscrit(Etudiant e){
    boolean participe = participe(e), reussi = dae.supprimerInscrit(this, e);
    if (reussi && participe) {
      placesRestantes ++;
      update();
    }
    return reussi;
  }
  
  public static Evenement getRandomEvent() {
	  Set<Integer> ids = ids();
	  int randI = (int) ( Math.random() * ids.size() );
	  Iterator<Integer> it = ids.iterator();
	  int i = 0;
	  while (it.hasNext() && randI >= 0) {
		  i = it.next();
		  randI --;
	  }
	  
	  return Evenement.chercher(i);
  }
}
