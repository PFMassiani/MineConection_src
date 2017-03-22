package share.communication;

import java.io.Serializable;

import exception.InvalidParameterException;
import serveur.bdd.Backupable;

public class Communication implements Serializable{
  private static final long serialVersionUID = 1L;
	
  private TypeBackupable type;
  private Action action;
  private Serializable o;
  private int id;

  public Communication(TypeBackupable type, Action action, Serializable o) throws InvalidParameterException{
    this.type = type;
    this.action = action;
    this.o = o;
    id = -1;
    switch (action){
    case SAUVEGARDER:
    case SUPPRIMER:
      if (o == null) throw new InvalidParameterException("L'action demandée requiert un objet de type Utilisateur ou Interaction");
      break;
    default:
      break;
    }
  }

  public Communication(TypeBackupable type, Action action, int id) throws InvalidParameterException{
    this.type = type;
    this.action = action;
    o = null;
    this.id = id;
    switch (action){
    case CHARGER:
    case SUPPRIMER:
      if (id < 0) throw new InvalidParameterException("L'action demandée requiert un identifiant positif ( id = " + id);
      break;
    default:
      break;
    }
  }

  public TypeBackupable getType(){
    return type;
  }
  public Action getAction(){
    return action;
  }
  public int getID(){
    return id;
  }
  public Serializable getObjet(){
    return o;
  }
  public Backupable getObjetBackupable() {
	  if (o instanceof Backupable) return (Backupable) o;
	  else return null;
  }
}
