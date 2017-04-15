package serveur.utilisateur;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import exception.InvalidResultException;
import share.utilisateur.Etudiant;
import share.utilisateur.Calendrier;

import serveur.bdd.DAO;

public class DAEtudiant extends DAO<Etudiant> {

  // -----------------------------------------------------------------------------------------------------------------------
  // CONSTRUCTEURS ---------------------------------------------------------------------------------------------------------
  // -----------------------------------------------------------------------------------------------------------------------

  public DAEtudiant() {
    super("Etudiant");
  }

  // -----------------------------------------------------------------------------------------------------------------------
  // METHODES --------------------------------------------------------------------------------------------------------------
  // -----------------------------------------------------------------------------------------------------------------------
  
  @Override
  public Etudiant charger(ResultSet r) throws SQLException{
	  Etudiant e = null;
	  if (r.getRow() != 0) {
		  try {
			  ResultSetMetaData rMeta = r.getMetaData();
			  if (rMeta.getTableName(1).compareTo("Etudiant") != 0) throw new InvalidResultException("Le résultat ne contient pas un étudiant");
			  e = new Etudiant(r.getInt("id"),r.getString("nom"),r.getString("prenom"),r.getString("telephone"),r.getInt("promotion"),Calendrier.charger(r.getInt("id")));
		  } catch (InvalidResultException ex) {
			  System.err.println(ex.getMessage());
		  }
	  }
	  return e;
  }

  @Override
  public String getUpdate(Etudiant e) {
	  return "nom = '" + e.getNom() + "', prenom = '" + e.getPrenom() + "', telephone = '" + e.getTelephone() + "', promotion = " + e.getPromo();  
  }
}