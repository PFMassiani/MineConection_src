package serveur.utilisateur;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Set;

import exception.InvalidResultException;
import exception.MissingObjectException;
import share.utilisateur.Etudiant;
import share.interaction.Evenement;
import share.utilisateur.Calendrier;
import serveur.bdd.DBModification;
import serveur.interaction.DAEvenement;
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
			  DACalendrier dac = new DACalendrier();
			  e = new Etudiant(r.getInt("id"),r.getString("nom"),r.getString("prenom"),r.getString("telephone"),r.getInt("promotion"),dac.chercher(r.getInt("id")));
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
  
  
  public static DAEtudiant getInstance() {
	  return new DAEtudiant();
  }
  @Override
  public boolean supprimer(int id) {
	  return supprimer(chercher(id));
  }
  
  @Override
  public boolean supprimer(Etudiant et) {
	  boolean reussi = false;
	  synchronized (DBModification.getInstance()) {
		  DAEvenement dae = new DAEvenement();
		  DACalendrier dac = new DACalendrier();
		  Calendrier cal = et.getCalendrier();
		  Set<Evenement> evts = dae.getAll();
		  for (Evenement evt : evts) {
			  try {
				  if (dae.inscrits(evt).contains(et)) {
					  cal.getPrincipaleClient().remove(evt);
					  cal.getAttenteClient().remove(evt);
					  dac.update(cal);
					  dae.supprimerInscrit(evt, et);
				  }
				  if(evt.getCreateur().equals(et)) dae.supprimer(evt);
			  } catch (MissingObjectException ex) {
				  System.out.println(ex.getMessage());
			  }
		  }
		  dac.supprimer(cal);
		  try {
			  return super.supprimer(et);
		  } catch (MissingObjectException ex) {
			  System.out.println(ex.getMessage());
		  }
		  return false;
	  }
  }
}