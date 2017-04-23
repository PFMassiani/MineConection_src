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
	private String description;

	public Communication(TypeBackupable type, Action action, Serializable o, String desc) throws InvalidParameterException{
		this.type = type;
		this.action = action;
		this.o = o;
		id = -1;
		description = desc;
		switch (action){
		case NOUVEAU:
		case SAUVEGARDER:
		case SUPPRIMER:
			if (o == null) throw new InvalidParameterException("L'action demandée requiert un objet de type Backupable");
			break;
		default:
			break;
		}
	}

	public Communication(TypeBackupable type, Action action, int id, String desc) throws InvalidParameterException{
		this.type = type;
		this.action = action;
		o = null;
		this.id = id;
		description = desc;
		switch (action){
		case CHARGER:
		case SUPPRIMER:
			if (id < 0) throw new InvalidParameterException("L'action demandée requiert un identifiant positif ( id = " + id + " )");
			break;
		default:
			break;
		}
	}

	public Communication(TypeBackupable type, Action action, String desc) throws InvalidParameterException{
		this.type = type;
		this.action = action;
		o = null;
		id = -1;
		description = desc;
		switch (action) {
		case GET_IDS:
		case GET_ALL:
			break;
		default:
			throw new InvalidParameterException("L'action est invalide ( action = " + action + " )");
		}
	}

	public void setID(int id) {
		this.id = id;
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
	public void setObjet(Serializable o) {
		this.o = o;
	}
	public String getDescription() {
		return description;
	}
}
