package share.utilisateur;

import java.io.Serializable;
import serveur.bdd.Backupable;

public abstract class Utilisateur implements Serializable,Backupable {
	private static final long serialVersionUID = 4074056332090552166L;
	
	final protected int IDENTIFIANT;


	public Utilisateur (int id){
		IDENTIFIANT = id;
	}

	@Override
	public int getID(){
		return IDENTIFIANT;
	}
	public abstract boolean push();

}