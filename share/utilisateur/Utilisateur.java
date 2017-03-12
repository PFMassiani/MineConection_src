package share.utilisateur;

import java.io.Serializable;

public abstract class Utilisateur implements Serializable {
	  /**
	   * 
	   */
	  private static final long serialVersionUID = 4074056332090552166L;
	  final protected int IDENTIFIANT;
	  

	  public Utilisateur (int id){
	    IDENTIFIANT = id;
	  }

	  public int getID(){
	    return IDENTIFIANT;
	  }
	  
	}