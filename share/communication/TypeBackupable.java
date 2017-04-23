package share.communication;


public enum TypeBackupable {
  ETUDIANT("share.utilisateur.Etudiant","serveur.utilisateur.DAEtudiant"),
  ASSOCIATION("share.utilisateur.Association","serveur.utilisateur.DAAssociation"),
  EVENEMENT("share.interaction.Evenement","serveur.interaction.DAEvenement"),
  PAPS("share.interaction.PAPS","serveur.interaction.DAPAPS"),
  CALENDRIER("share.utilisateur.Calendrier","serveur.utilisateur.DACalendrier");
  
  private String nom, nomDao;
  
  TypeBackupable(String nom, String nomDao){
    this.nom = nom;
    this.nomDao = nomDao;
  }
  
  public String getNomClasse(){
    return nom;
  }
  public String getNomDAO() {
	  return nomDao;
  }
}
