package share.communication;

public enum TypeBackupable {
  ETUDIANT("share.utilisateur.Etudiant"),
  ASSOCIATION("share.utilisateur.Association"),
  EVENEMENT("share.interaction.Evenement"),
  PAPS("share.interaction.PAPS"),
  CALENDRIER("share.utilisateur.Calendrier");
  
  private String nom;
  
  TypeBackupable(String nom){
    this.nom = nom;
  }
  
  public String getNomClasse(){
    return nom;
  }
}
