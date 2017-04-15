package serveur.bdd;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Set;
import java.util.HashSet;

import exception.MissingObjectException;

public abstract class DAO<T extends Backupable> {
  
  // -----------------------------------------------------------------------------------------------------------------------
  // ATTRIBUTS -------------------------------------------------------------------------------------------------------------
  // -----------------------------------------------------------------------------------------------------------------------
  
  protected Connection connexion = Connexion.getConnection();
  protected String table, champPrimaire = "id";
  
  // -----------------------------------------------------------------------------------------------------------------------
  // CONSTRUCTEURS ---------------------------------------------------------------------------------------------------------
  // -----------------------------------------------------------------------------------------------------------------------
  
  public DAO(String t){
    table = t;
  }
  
  // -----------------------------------------------------------------------------------------------------------------------
  // METHODES --------------------------------------------------------------------------------------------------------------
  // -----------------------------------------------------------------------------------------------------------------------
  
  public Set<Integer> ids(){
    Set<Integer> ids = new HashSet<Integer>();
    try{
      ResultSet r = connexion.prepareStatement("SELECT " + champPrimaire + " FROM " + table).executeQuery();
      
      while (r.next()) ids.add(r.getInt("id"));
    } catch (SQLException ex) {
      System.out.println("SQLException: " + ex.getMessage());
      System.out.println("SQLState: " + ex.getSQLState());
      System.out.println("VendorError: " + ex.getErrorCode());
    }
    
    return ids;
  }
  
  public int getNewID(){
    int id = -1, idMax = 0;
    synchronized(DBModification.getInstance()){
      try{
        
        idMax = getIDMax();

        connexion.prepareStatement("INSERT INTO " + table + " VALUES ()").executeUpdate();

        ResultSet r = connexion.prepareStatement("SELECT " + champPrimaire + " FROM " + table + " WHERE id > " + idMax).executeQuery();
        
        if (r.next()) id = r.getInt("id");
        
      } catch (SQLException ex){
        System.out.println("SQLException: " + ex.getMessage());
        System.out.println("SQLState: " + ex.getSQLState());
        System.out.println("VendorError: " + ex.getErrorCode());
      }
    }
    return id;
  }
  public int getIDMax(){
    int idMax = 0;
    try{
      ResultSet r = connexion.prepareStatement("SELECT MAX(" + champPrimaire + ") FROM " + table).executeQuery();
      if (r.next()) idMax = r.getInt("MAX(" + champPrimaire + ")");
    } catch (SQLException ex){
      System.out.println("SQLException: " + ex.getMessage());
      System.out.println("SQLState: " + ex.getSQLState());
      System.out.println("VendorError: " + ex.getErrorCode());
    }
    return idMax;
  }
  
  public boolean supprimer(T obj) throws MissingObjectException{
    boolean reussi = false;
    synchronized(DBModification.getInstance()){
      try{
    	if ( chercher(obj.getID()) == null) throw new MissingObjectException("L'objet " + obj + " n'a pas pu être trouvé dans la table " + table);
        if( connexion.prepareStatement("DELETE FROM " + table + " WHERE " + champPrimaire + " = " + obj.getID()).executeUpdate() == 1) reussi = true;
        
      } catch (SQLException ex){
        System.out.println("SQLException: " + ex.getMessage());
        System.out.println("SQLState: " + ex.getSQLState());
        System.out.println("VendorError: " + ex.getErrorCode());
      }
      
    }
    return reussi;
  }
  public boolean supprimer(int id) throws MissingObjectException {
    boolean reussi = false;
    synchronized(DBModification.getInstance()){
      try{
    	if ( chercher(id) == null) throw new MissingObjectException("L'objet d'ID " + id + " n'a pas pu être trouvé dans la table " + table);
        if (connexion.prepareStatement("DELETE FROM " + table + " WHERE " + champPrimaire + " = " + id).executeUpdate() == 1 ) reussi = true;
      } catch (SQLException ex){
        System.out.println("SQLException: " + ex.getMessage());
        System.out.println("SQLState: " + ex.getSQLState());
        System.out.println("VendorError: " + ex.getErrorCode());
      
      }
      
    }
    return reussi;
  }
  public boolean update(T obj) {
    boolean reussi = false;
    synchronized(DBModification.getInstance()){
      try{
        ResultSet r = connexion.createStatement().executeQuery("SELECT * FROM " + table + " WHERE " + champPrimaire + " = " + obj.getID());
        
        if (r.next()){
          String query = "UPDATE " + table + " SET " + getUpdate(obj) + " WHERE " + champPrimaire + " = " + obj.getID() ;
          connexion.prepareStatement(query).executeUpdate();
          
          reussi = true;
        }
        
      } catch (SQLException ex){
        System.out.println("SQLException: " + ex.getMessage());
        System.out.println("SQLState: " + ex.getSQLState());
        System.out.println("VendorError: " + ex.getErrorCode());
      }
    }
    return reussi;
  }
  
  public T chercher(int id) {
	  T t = null;
	    
	    try{
	      ResultSet r = connexion.prepareStatement("SELECT * FROM " + table + " WHERE + " + champPrimaire + " = " + id).executeQuery();
	      if (!r.next()) System.out.println("Aucun résultat");
	      else t = charger(r);
	      
	    } catch (SQLException ex){
	      System.out.println("SQLException: " + ex.getMessage());
	      System.out.println("SQLState: " + ex.getSQLState());
	      System.out.println("VendorError: " + ex.getErrorCode());
	    }
	    return t;
  }
  
  protected void setChampPrimaire(String s){
    champPrimaire = s;
  }
  
  public Set<T> getAll(){
	  Set<T> all = new HashSet<>();
	  try {
		  ResultSet r = connexion.prepareStatement("SELECT * FROM " + table).executeQuery();
		  while(r.next()) all.add(charger(r));
	  } catch (SQLException ex) {
		  System.out.println("SQLException: " + ex.getMessage());
	      System.out.println("SQLState: " + ex.getSQLState());
	      System.out.println("VendorError: " + ex.getErrorCode());
	  }
	  return all;
  }
  
  public abstract T charger(ResultSet r) throws SQLException;
  public abstract String getUpdate(T t);
  
}
