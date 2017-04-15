package share.communication;

import serveur.bdd.DAO;
import serveur.interaction.DAEvenement;
import serveur.utilisateur.*;

public enum TypeBackupable {
  ETUDIANT("share.utilisateur.Etudiant","serveur.utilisateur.DAEtudiant", new DAEtudiant()),
  ASSOCIATION("share.utilisateur.Association","serveur.utilisateur.DAAssociation",null),
  EVENEMENT("share.interaction.Evenement","serveur.interaction.DAEvenement",new DAEvenement()),
  PAPS("share.interaction.PAPS","serveur.interaction.DAPAPS",null),
  CALENDRIER("share.utilisateur.Calendrier","serveur.utilisateur.DACalendrier",new DACalendrier());
  
  private String nom, nomDao;
  private DAO<?> dao;
  
  TypeBackupable(String nom, String nomDao, DAO<?> dao){
    this.nom = nom;
    this.nomDao = nomDao;
    this.dao = dao;
  }
  
  public String getNomClasse(){
    return nom;
  }
  public String getNomDAO() {
	  return nomDao;
  }
  public DAO<?> getDAO(){
	  return dao;
  }
}
