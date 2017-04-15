package serveur.interaction;

import exception.MissingObjectException;
import serveur.bdd.DAO;
import serveur.bdd.DBModification;
import share.interaction.Evenement;
import share.utilisateur.Etudiant;
import serveur.utilisateur.DAEtudiant;

import java.sql.*;
import java.util.Set;
import java.util.HashSet;

public class DAEvenement extends DAO<Evenement> {
  
 //------------------------------------------------------------------------------------------------------------------------
 // CONSTRUCTEURS ---------------------------------------------------------------------------------------------------------
 // -----------------------------------------------------------------------------------------------------------------------
 
  public DAEvenement() {
    super("Evenement");
  }
  
 //-----------------------------------------------------------------------------------------------------------------------
 // METHODES --------------------------------------------------------------------------------------------------------------
 // -----------------------------------------------------------------------------------------------------------------------
  
  @Override
  public int getNewID(){
    int id = super.getNewID();
    synchronized (DBModification.getInstance()){
      try{
        if (id != -1){
          String stmt = "CREATE TABLE IF NOT EXISTS evt_" + id + " (" +
              "ordre_adhesion INT UNSIGNED AUTO_INCREMENT NOT NULL, " +
              "id_etudiant INT UNSIGNED NOT NULL, " +
              "date_adhesion DATE NOT NULL, " +
              "liste_principale BOOLEAN NOT NULL, " +
              "PRIMARY KEY(ordre_adhesion)," +
              "CONSTRAINT fk_id_etudiant_" + id + " FOREIGN KEY (id_etudiant) REFERENCES Etudiant(id)" +
              ") " +
              "ENGINE = INNODB";
          connexion.prepareStatement(stmt).executeUpdate();
        }
        else System.err.println("L'attribution d'un nouvel ID a échoué");

      } catch (SQLException ex){
        System.out.println("SQLException: " + ex.getMessage());
        System.out.println("SQLState: " + ex.getSQLState());
        System.out.println("VendorError: " + ex.getErrorCode());
      }
    }
    return id;
  }
  
  @Override
  public boolean supprimer(Evenement evt) throws MissingObjectException{
    boolean reussi = super.supprimer(evt);
    synchronized(DBModification.getInstance()){
      try{
        if (connexion.prepareStatement("DROP TABLE evt_" + evt.getID()).executeUpdate() == 1) reussi = true; 
      } catch (SQLException ex){
        System.out.println("SQLException: " + ex.getMessage());
        System.out.println("SQLState: " + ex.getSQLState());
        System.out.println("VendorError: " + ex.getErrorCode());
      }
    }
    return reussi;
  }
  
  @Override
  public boolean supprimer(int id) throws MissingObjectException{
    boolean reussi = super.supprimer(id);
    synchronized(DBModification.getInstance()){
      try{
        if (connexion.prepareStatement("DROP TABLE evt_" + id).executeUpdate() == 1) reussi = true; 
      } catch (SQLException ex){
        System.out.println("SQLException: " + ex.getMessage());
        System.out.println("SQLState: " + ex.getSQLState());
        System.out.println("VendorError: " + ex.getErrorCode());
      }
    }
    return reussi;
  }
  
  @Override
  public Evenement charger(ResultSet r) throws SQLException{
	  share.utilisateur.Utilisateur createur = null;
	  DAEtudiant dae = new DAEtudiant();
	  if (r.getBoolean("createur_est_etudiant") == true) createur = dae.chercher(r.getInt("createur_etudiant_id"));
//	  else createur = Association.chercher(r.getInt("createur_association_id"));

	  return new Evenement(r.getInt("id"),r.getString("nom"),r.getString("description"),r.getInt("places"),r.getDate("date"),r.getInt("debut_h"),r.getInt("debut_m"),r.getInt("duree"),createur);
  }

  public Set<share.utilisateur.Etudiant> updatePlaces(Evenement evt){
    Set<share.utilisateur.Etudiant> etudiantsRetires = new HashSet<share.utilisateur.Etudiant>();
    synchronized(DBModification.getInstance()){
      try{
        String requestSelection = "SELECT id_etudiant " +
            "FROM " + getTable(evt) + 
            " WHERE liste_principale = TRUE " +
            "ORDER BY ordre_adhesion DESC";
        String requestMAJ = "UPDATE " + getTable(evt) + " SET liste_principale = FALSE WHERE id_etudiant = ?";
        
        PreparedStatement stmtSelection = connexion.prepareStatement(requestSelection);
        PreparedStatement stmtMAJ = connexion.prepareStatement(requestMAJ);
        
        ResultSet r = null;
        
        int id = 0, n = - evt.getPlacesRestantes();
        DAEtudiant dae = new DAEtudiant();
        for (int i = 0; i < n; i++){
          r = stmtSelection.executeQuery();
          if (r.next()){
            //Forcément vérifié ssi evt.placesRestantes < 0, donc si on a au moins une personne
            //   sur liste principale car placesRestantes = places - taille liste principale et places >= 0
            id = r.getInt("id_etudiant");
            etudiantsRetires.add(dae.charger(r));
            
            stmtMAJ.setInt(1,id);
            stmtMAJ.executeUpdate();
          }
        }
      } catch (SQLException ex){
        System.out.println("SQLException: " + ex.getMessage());
        System.out.println("SQLState: " + ex.getSQLState());
        System.out.println("VendorError: " + ex.getErrorCode());
      }
    }
    return etudiantsRetires;
  }
  
  private String getTable(Evenement evt) {
	return "evt_" + evt.getID();
}

public Set<share.utilisateur.Etudiant> participants(Evenement evt){
	    Set<share.utilisateur.Etudiant> participants = new HashSet<>();

	    try{
	      String query = "SELECT * FROM Etudiant WHERE id IN " +
	      		"( SELECT id_etudiant FROM " + getTable(evt) + " WHERE liste_principale = TRUE )";
	      ResultSet r = connexion.prepareStatement(query).executeQuery();
	      DAEtudiant dae = new DAEtudiant();
	      while (r.next()) participants.add(dae.charger(r));
	      
	    } catch (SQLException ex){
	      System.out.println("SQLException: " + ex.getMessage());
	      System.out.println("SQLState: " + ex.getSQLState());
	      System.out.println("VendorError: " + ex.getErrorCode());
	    }

	    return participants;
	  }
  public Set<share.utilisateur.Etudiant> attente(Evenement evt){
	    Set<share.utilisateur.Etudiant> attente = new HashSet<>();

	    try{
	      String query = "SELECT * FROM Etudiant WHERE id IN " +
	      		"( SELECT id_etudiant FROM " + getTable(evt) + " WHERE liste_principale = FALSE )";
	      ResultSet r = connexion.prepareStatement(query).executeQuery();
	      DAEtudiant dae = new DAEtudiant();

	      while (r.next()) attente.add(dae.charger(r));
	      
	    } catch (SQLException ex){
	      System.out.println("SQLException: " + ex.getMessage());
	      System.out.println("SQLState: " + ex.getSQLState());
	      System.out.println("VendorError: " + ex.getErrorCode());
	    }

	    return attente;
	  }
  
  public Set<share.utilisateur.Etudiant> inscrits(Evenement evt){
    Set<share.utilisateur.Etudiant> inscrits = new HashSet<>();

    try{
      String query = "SELECT * FROM Etudiant WHERE id IN " +
          "( SELECT id_etudiant FROM " + getTable(evt) +" )";
      ResultSet r = connexion.prepareStatement(query).executeQuery();
      DAEtudiant dae = new DAEtudiant();

      while (r.next()) inscrits.add(dae.charger(r));
      
    } catch (SQLException ex){
      System.out.println("SQLException: " + ex.getMessage());
      System.out.println("SQLState: " + ex.getSQLState());
      System.out.println("VendorError: " + ex.getErrorCode());
    }

    return inscrits;
  }
  
  public boolean ajouterPrincipale(Evenement evt, share.utilisateur.Etudiant e){
    boolean reussi = false;
    synchronized(DBModification.getInstance()){
      try{
        if (evt.getPlacesRestantes() > 0 && !evt.principale().contains(e)){
        	String query;
        	if (!evt.attente().contains(e)) {
        		java.sql.Date maintenant = new java.sql.Date((new java.util.Date()).getTime());
        		query = "INSERT INTO " + getTable(evt) + " (id_etudiant,date_adhesion,liste_principale) VALUES " +
        				"( " + e.getID() + 
        				", '" + maintenant +
        				"', TRUE)";
        		
        	}
        	else query = "UPDATE " + getTable(evt) + " SET liste_principale = TRUE WHERE id_etudiant = " + e.getID();
        	
        	if (connexion.prepareStatement(query).executeUpdate() == 1 ) {
    			ResultSet r = connexion.prepareStatement("SELECT places_restantes FROM Evenement WHERE id = "+ evt.getID()).executeQuery();
    			int restantes = 0;
    			if (r.next()) restantes = r.getInt("places_restantes");
    			connexion.prepareStatement("UPDATE Evenement SET places_restantes = " + (restantes-1));
    			reussi = true;
        	}
        }
      } catch (SQLException ex){
        System.out.println("SQLException: " + ex.getMessage());
        System.out.println("SQLState: " + ex.getSQLState());
        System.out.println("VendorError: " + ex.getErrorCode());
      }
      return reussi;
    }
  }
  
  public boolean ajouterAttente(Evenement evt, share.utilisateur.Etudiant e){
    boolean reussi = false;
    synchronized(DBModification.getInstance()){
    	try{
    		java.sql.Date maintenant = new java.sql.Date((new java.util.Date()).getTime());
    		String query;
    		if (evt.principale().contains(e)) query = "UPDATE " + getTable(evt) + " SET liste_principale = FALSE WHERE id_etudiant = " + e.getID();
    		else {
    			query = "INSERT INTO " + getTable(evt) + " (id_etudiant,date_adhesion,liste_principale) VALUES " +
    					"( " + e.getID() + 
    					", '" + maintenant +
    					"', FALSE)";
    		}
    		if (connexion.prepareStatement(query).executeUpdate() == 1 )
    			if (query.compareTo("UPDATE " + getTable(evt) + " SET liste_principale = FALSE WHERE id_etudiant = " + e.getID()) == 0) {
    				ResultSet r = connexion.prepareStatement("SELECT places_restantes FROM Evenement WHERE id = "+ evt.getID()).executeQuery();
        			int restantes = 0;
        			if (r.next()) restantes = r.getInt("places_restantes");
        			connexion.prepareStatement("UPDATE Evenement SET places_restantes = " + (restantes+1));	
    			}
    			reussi = true;

      } catch (SQLException ex){
        System.out.println("SQLException: " + ex.getMessage());
        System.out.println("SQLState: " + ex.getSQLState());
        System.out.println("VendorError: " + ex.getErrorCode());
      }
    }
    return reussi;
  }
  
  public int placesRestantes(Evenement evt){
    int restantes = 0;
    try{
      ResultSet r = connexion.prepareStatement("SELECT places_restantes FROM Evenement WHERE id = " + evt.getID()).executeQuery();
      if(r.next()) restantes = r.getInt("places_restantes");
    } catch (SQLException ex){
      System.out.println("SQLException: " + ex.getMessage());
      System.out.println("SQLState: " + ex.getSQLState());
      System.out.println("VendorError: " + ex.getErrorCode());
    }
    
    return restantes;
  }
  
  public boolean supprimerInscrit (Evenement evt, share.utilisateur.Etudiant e){
    if (!evt.estInscrit(e)) return false;
    
    try {
      connexion.prepareStatement("DELETE FROM " + getTable(evt) + " WHERE id_etudiant = " + e.getID()).executeUpdate();
    } catch (SQLException ex) {
      System.out.println("SQLException: " + ex.getMessage());
      System.out.println("SQLState: " + ex.getSQLState());
      System.out.println("VendorError: " + ex.getErrorCode());
    }
    return true;
  }
  
  @Override
  public String getUpdate(Evenement evt) {
		String update = "nom = '" + evt.getNomClient() + 
				"', description = '" + evt.getDescriptionClient() + 
				"', places = " + evt.getPlacesClient() + 
				", places_restantes = " + evt.getPlacesRestantesClient() + 
				", date = '" + evt.getDateClient() + 
				"', duree = " + evt.getDureeClient() +
				", debut_h = " + evt.getDebutHClient() +
				", debut_m = " + evt.getDebutMClient();
		if (evt.getCreateurClient() instanceof share.utilisateur.Etudiant) update += " , createur_est_etudiant = TRUE , " +
				"createur_etudiant_id = " + evt.getCreateurClient().getID() + 
				" , createur_association_id = NULL";
		else update += " , createur_est_etudiant = FALSE , " +
				"createur_etudiant_id = NULL , " +
				"createur_association_id = " + evt.getCreateurClient().getID();
		return update;
	}
  
  @Override
  public boolean update(Evenement evt) {
	  boolean reussi = super.update(evt);
	  Set<share.utilisateur.Etudiant> maj = evt.inscritsClient();
	  Set<share.utilisateur.Etudiant> nouveaux = evt.inscritsClient();
	  Set<share.utilisateur.Etudiant> supprimes = participants(evt);
	  nouveaux.removeAll(supprimes); // On obtient les nouveaux inscrits
	  supprimes.removeAll(maj);
	  maj.removeAll(nouveaux);

	  for (share.utilisateur.Etudiant e : supprimes ) evt.supprimerInscrit(e);
	  for (share.utilisateur.Etudiant e : maj )
		  if (evt.principaleClient().contains(e) && 
				  !(reussi = ajouterPrincipale(evt,e)))
			  ajouterAttente(evt,e);
	  for (share.utilisateur.Etudiant e : nouveaux) 
		  if (!(reussi = ajouterPrincipale(evt,e)))
			  reussi = ajouterAttente(evt,e);

	  return reussi;
  }
}
