package share.communication;

public enum TypeBackupable {
  ETUDIANT("utilisateur.Etudiant"),
  ASSOCIATION("utilisateur.Association"),
  EVENEMENT("interaction.Evenement"),
  PAPS("interaction.PAPS");
  
  private String nom;
  
  TypeBackupable(String nom){
    this.nom = nom;
  }
  
  public String getNomClasse(){
    return nom;
  }
}
