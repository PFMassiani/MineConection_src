package serveur.utilisateur;

import java.sql.*;
import java.util.Set;
import java.util.TreeSet;

import serveur.bdd.Connexion;
import serveur.bdd.DAO;
import serveur.bdd.DBModification;
import share.utilisateur.Calendrier;
import share.interaction.Evenement;

public class DACalendrier extends DAO<Calendrier>{
	
	public DACalendrier(){
		super("Etudiant");
	}
	
	@Override
	public int getNewID() {
		//TODO
		return -1;
	}
	@Override
	public String getUpdate(Calendrier c) {
		//TODO
		return "";
	}
	
	@Override
	public boolean supprimer(int id) {
		boolean reussi = false;
		synchronized(DBModification.getInstance()) {
			try{
				if (Connexion.getConnection().prepareStatement("DROP TABLE cal_" + id).executeUpdate() == 1)
				reussi = true;
			} catch (SQLException ex){
				System.out.println("SQLException: " + ex.getMessage());
				System.out.println("SQLState: " + ex.getSQLState());
				System.out.println("VendorError: " + ex.getErrorCode());
			}
		}
		return reussi;
	}
	
	@Override
	public boolean supprimer(Calendrier cal) {
		return supprimer(cal.getID());
	}
	
	
	@Override
	public boolean update(Calendrier cal) {
		try {
			String creationTable = "CREATE TABLE IF NOT EXISTS " + getTable(cal)
			+ " (id_evenement INT UNSIGNED NOT NULL,"
			+ "liste_principale BOOLEAN NOT NULL) "
			+ "CONSTRAINT fk_" + getTable(cal) + "FOREIGN KEY id_evenement REFERENCES Evenement(id)";
			String majListes = "UPDATE " + getTable(cal) + " SET liste_principale = ? WHERE id_evenement = ?";
			String nouveauxEvt = "INSERT INTO " + getTable(cal) + " VALUES (?,?)";
			String suppressionEvt = "DELETE FROM " + getTable(cal) + " WHERE id_evenement = ?";

			Connexion.getConnection().prepareStatement(creationTable).executeUpdate();

			Set<Evenement> maj = cal.getEvenementsClient();
			Set<Evenement> supprimes = chercher(cal.getID()).getEvenementsClient();
			Set<Evenement> nouveaux = new TreeSet<>(maj);
			nouveaux.removeAll(supprimes);
			supprimes.removeAll(maj);
			maj.removeAll(nouveaux);

			PreparedStatement stmtMaj = connexion.prepareStatement(majListes),
					stmtNouveaux = connexion.prepareStatement(nouveauxEvt),
					stmtSuppression = connexion.prepareStatement(suppressionEvt);

			for(Evenement e : maj) {
				stmtMaj.setBoolean(1, cal.getPrincipaleClient().contains(e));
				stmtMaj.setInt(2, e.getID());
				stmtMaj.executeUpdate();
			}
			for(Evenement e: supprimes) {
				stmtSuppression.setInt(1,e.getID());
				stmtSuppression.executeUpdate();
			}
			for(Evenement e : nouveaux) {
				stmtNouveaux.setInt(1, e.getID());
				stmtNouveaux.setBoolean(2,cal.getPrincipaleClient().contains(e));
			}
		} catch (SQLException ex){
			System.out.println("SQLException: " + ex.getMessage());
			System.out.println("SQLState: " + ex.getSQLState());
			System.out.println("VendorError: " + ex.getErrorCode());
		}
		return true;
	}
	
	@Override
	public Calendrier chercher(int id) {
		Calendrier cal = new Calendrier(id);
		try {
			ResultSet r = Connexion.getConnection().prepareStatement("SELECT * FROM cal_" + id).executeQuery();
			while (r.next()) {
				if(r.getBoolean("liste_principale")) cal.ajouterPrincipale(share.interaction.Evenement.chercher(r.getInt("id_evenement")));
				else cal.ajouterAttente(share.interaction.Evenement.chercher(r.getInt("id_evenement")));
			}
		} catch (SQLException ex){
			System.out.println("SQLException: " + ex.getMessage());
			System.out.println("SQLState: " + ex.getSQLState());
			System.out.println("VendorError: " + ex.getErrorCode());
		}
		return cal;
	}

	private String getTable(Calendrier cal) {
		return "cal_" + cal.getID();
	}

	@Override
	public Calendrier charger(ResultSet r) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}
}
