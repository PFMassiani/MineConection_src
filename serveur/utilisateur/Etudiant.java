package serveur.utilisateur;

import java.sql.*;

import exception.*;
import share.utilisateur.Calendrier;

import java.util.Iterator;
import java.util.Set;

public class Etudiant extends Utilisateur {

  // -----------------------------------------------------------------------------------------------------------------------
  // ATTRIBUTS -------------------------------------------------------------------------------------------------------------
  // -----------------------------------------------------------------------------------------------------------------------
  
  /**
   * 
   */
  private static final long serialVersionUID = 1028926935677583162L;
  private String nom, prenom, telephone;
  private int promo;
  Calendrier calendrier;
  
  private static volatile DAEtudiant dae = new DAEtudiant();
  
 //------------------------------------------------------------------------------------------------------------------------
 // CONSTRUCTEURS ---------------------------------------------------------------------------------------------------------
 // -----------------------------------------------------------------------------------------------------------------------
  
  //Constructeur utilisé pour créer un nouvel étudiant
  public Etudiant (String n, String p, String t, int pr){
    super((new DAEtudiant()).getNewID());
    
    nom = n;
    prenom = p;
    promo = pr;
    telephone = t;
    
    calendrier = new Calendrier(IDENTIFIANT);
    
    dae.update(this);
    if (IDENTIFIANT == -1) throw new InvalidIDException("");
  }
  
  //Charger un étudiant depuis la BDD
  public Etudiant (ResultSet r) throws SQLException{
    super(r.getInt("id"));
    try {
      ResultSetMetaData meta = r.getMetaData();
      if (meta.getTableName(1).compareTo("Etudiant") != 0) throw new InvalidResultException("Le résultat ne provient pas de la table Etudiant");

      nom = r.getString("nom");
      prenom = r.getString("prenom");
      telephone = r.getString("telephone");
      promo = r.getInt("promotion");
    } catch (InvalidResultException ex) {
      System.out.println("InvalidResultException : " + ex.getMessage());
    }
    
    calendrier = Calendrier.charger(IDENTIFIANT);
  }
  
  public Etudiant(share.utilisateur.Etudiant e) {
	  super(e.getID());
	  nom = e.getNom();
	  prenom = e.getPrenom();
	  telephone = e.getTelephone();
	  promo = e.getPromo();
	  
	  calendrier = e.getCalendrier();
  }
  
  //------------------------------------------------------------------------------------------------------------------------
  // ACCESSEURS ------------------------------------------------------------------------------------------------------------
  // -----------------------------------------------------------------------------------------------------------------------
  
  public String getNom(){
    return nom;
  }
  public String getPrenom(){
    return prenom;
  }
  public String getTelephone(){
    return telephone;
  }
  public int getPromo(){
    return promo;
  }
  public String getUpdate(){
    return "nom = '" + nom + "', prenom = '" + prenom + "', telephone = '" + telephone + "', promotion = " + promo;
  }
  
  public void setNom(String n){
    nom = n;
    dae.update(this);
  }
  public void setPrenom(String p){
    prenom = p;
    dae.update(this);
  }
  public void setTelephone(String t){
    telephone = t;
    dae.update(this);
  }
  public void setPromo(int p){
    promo = p;
    dae.update(this);
  }
  public boolean MAJ() {
	  return dae.update(this) & calendrier.push();
  }
  
  //------------------------------------------------------------------------------------------------------------------------
  // UTILITAIRE ------------------------------------------------------------------------------------------------------------
  // -----------------------------------------------------------------------------------------------------------------------
  
  public boolean supprimer() throws MissingObjectException{
    return dae.supprimer(this);
  }
  public static boolean supprimer (int id) throws MissingObjectException{
    return dae.supprimer(id);
  }
  public boolean update(){
    return dae.update(this);
  }
  public static Etudiant chercher(int id){
    return dae.chercher(id);
  }
//  public static int getIDMax(){
//    return dae.getIDMax();
//  }
  public static Set<Integer> ids(){
    return dae.ids();
  } 
  
  public static Etudiant getRandomStudent() {
	  Set<Integer> ids = ids();
	  int randI = (int) ( Math.random() * ids.size() );
	  Iterator<Integer> it = ids.iterator();
	  int i = 0;
	  while (it.hasNext() && randI >= 0) {
		  i = it.next();
		  randI --;
	  }
	  
	  return Etudiant.chercher(i);
  }
  
  public static Set<Etudiant> getAll() {
	  return dae.getAll();
  }
  
//  @Override
//  public byte[] encoder(){
//    List<Byte> id_b_128 = new LinkedList<>();
//    int id = IDENTIFIANT;
//    
//    // On décompose IDENTIFIANT en base 128, pour être sûr de pouvoir le transmettre sous forme de byte.
//    while (id > 127){
//      id_b_128.add((byte) (id%128));
//      id -= (int) ((id - (id%128))/128);
//    }
//    int n = 1 + id_b_128.size() + 4;
//    byte[] id_code = new byte[ 1 + id_b_128.size() + 4 ];
//    Iterator<Byte> it = id_b_128.iterator();
//    
//
//    for (int i = 1; i <= id_b_128.size(); i++) id_code[i] = it.next();
//    
//    Charset utf8 = StandardCharsets.UTF_8;
//    
//    byte[] prenom_code = prenom.getBytes(utf8), nom_code = nom.getBytes(utf8), tel_code =  telephone.getBytes(utf8), promo_code =  {((byte) promo)};
//    n = id_code.length + nom_code.length + tel_code.length + tel_code.length + promo_code.length;
//    int k = 0;
//    
//    byte[] code = new byte[n + 6];
//    code[0] = 0;
//    for (int i = 0; i < id_code.length; i++) code[k + i] = id_code[i];
//    
//    k += id_code.length;
//    code[k] = 56; // . est le caractère de séparation
//    k += 1;
//    for (int i = 0; i < nom_code.length; i++) code[k + i] = nom_code[i];
//    
//    k += nom_code.length;
//    code[k] = 56; // . est le caractère de séparation
//    k += 1;
//    for (int i = 0; i < prenom_code.length; i++) code[k + i] = prenom_code[i];
//
//    k += prenom_code.length;
//    code[k] = 56; // . est le caractère de séparation
//    k += 1;
//    for (int i = 0; i < tel_code.length; i++) code[k + i] = tel_code[i];
//    
//    k += tel_code.length;
//    code[k] = 56; // . est le caractère de séparation
//    k += 1;
//    for (int i = 0; i < promo_code.length; i++) code[k + i] = promo_code[i];
//    
//    
//    
//    
//    return code;
//  }
//  
//  @Override
//  @SuppressWarnings("unchecked")
//  public Etudiant decoder(){
//    
//    return null;
//  }
  
  //------------------------------------------------------------------------------------------------------------------------
  // EVENEMENTS ------------------------------------------------------------------------------------------------------------
  // -----------------------------------------------------------------------------------------------------------------------
  
//  Evenement organiserEvenement(String nom, String lieu, int places, Date debut, Time duree, String description){
//    return new Evenement(nom, lieu, places, debut, duree, description, this);
//  }
//  
//  Evenement organiserEvenementDansAsso(Association asso, String nom, String lieu, int places, Date debut, Time duree, String description)
//                                          throws NotInAssociationException{
//    if (!asso.estDansAssociation(this)) throw new NotInAssociationException(this.toString() + asso);
//    return new Evenement(nom, lieu, places, debut, duree, description, asso);
//  }
  
   public String toString(){
     return "Étudiant n°" + IDENTIFIANT + ", " + prenom + " " + nom + " ( P " + promo + " )";
   }
   
   public boolean equals (Object o){
     if (!(o instanceof Etudiant)) return false;
     
     Etudiant e = (Etudiant) o;
     return e.getID() == IDENTIFIANT;
   }

@Override
public share.utilisateur.Etudiant getObjetClient() {
	return new share.utilisateur.Etudiant(IDENTIFIANT,nom,prenom,telephone,promo,calendrier);
}

}
