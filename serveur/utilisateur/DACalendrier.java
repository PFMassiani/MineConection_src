package serveur.utilisateur;

import java.sql.*;
import java.util.Set;
import java.util.HashSet;

import serveur.bdd.Connexion;
import serveur.bdd.DAO;
import serveur.bdd.DBModification;
import share.utilisateur.Calendrier;

public class DACalendrier extends DAO<Calendrier>{

	public DACalendrier(){
		super("Etudiant");
	}

	@Override
	public int getNewID() {
		return -1;
	}
	@Override
	public String getUpdate(Calendrier c) {
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
			+ "liste_principale BOOLEAN NOT NULL, "
			+ "CONSTRAINT fk_" + getTable(cal) + " FOREIGN KEY (id_evenement) REFERENCES Evenement(id) ) ENGINE = INNODB";
			String majListes = "UPDATE " + getTable(cal) + " SET liste_principale = ? WHERE id_evenement = ?";
			String nouveauxEvt = "INSERT INTO " + getTable(cal) + " VALUES (?,?)";
			String suppressionEvt = "DELETE FROM " + getTable(cal) + " WHERE id_evenement = ?";

			Connexion.getConnection().prepareStatement(creationTable).executeUpdate();

			Set<Integer> maj = cal.getEvenementsClient();
			Set<Integer> supprimes = chercher(cal.getID()).getEvenementsClient();
			Set<Integer> nouveaux = new HashSet<>(maj);
			nouveaux.removeAll(supprimes);
			supprimes.removeAll(maj);
			maj.removeAll(nouveaux);

			PreparedStatement stmtMaj = connexion.prepareStatement(majListes),
					stmtNouveaux = connexion.prepareStatement(nouveauxEvt),
					stmtSuppression = connexion.prepareStatement(suppressionEvt);

			for(int i : maj) {
				stmtMaj.setBoolean(1, cal.getPrincipaleClient().contains(i));
				stmtMaj.setInt(2, i);
				stmtMaj.executeUpdate();
			}
			for(int i : supprimes) {
				stmtSuppression.setInt(1,i);
				stmtSuppression.executeUpdate();
			}
			for(int i : nouveaux) {
				stmtNouveaux.setInt(1, i);
				stmtNouveaux.setBoolean(2,cal.getPrincipaleClient().contains(i));
				stmtNouveaux.executeUpdate();
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
				if(r.getBoolean("liste_principale")) cal.getPrincipaleClient().add(r.getInt("id_evenement"));
				else cal.getAttenteClient().add(r.getInt("id_evenement"));
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

		return null;
	}

	@Override
	public Calendrier nouveau(Calendrier cal) {
		try {
			String creationTable = "CREATE TABLE IF NOT EXISTS " + getTable(cal)
			+ " (id_evenement INT UNSIGNED NOT NULL,"
			+ "liste_principale BOOLEAN NOT NULL, "
			+ "CONSTRAINT fk_" + getTable(cal) + " FOREIGN KEY (id_evenement) REFERENCES Evenement(id)"
			+ " ) "
			+ "ENGINE = INNODB";

			Connexion.getConnection().prepareStatement(creationTable).executeUpdate();
		} catch (SQLException ex) {
			System.out.println("SQLException: " + ex.getMessage());
			System.out.println("SQLState: " + ex.getSQLState());
			System.out.println("VendorError: " + ex.getErrorCode());
		}

		return cal;
	}

	public static DACalendrier getInstance() {
		return new DACalendrier();
	}
}
