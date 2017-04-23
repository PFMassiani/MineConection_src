package exception;

public class InvalidCommunicationException extends Exception{
	String type;
  public InvalidCommunicationException(String s){
    super(s);
    type = "";
  }
  public InvalidCommunicationException(String s, String type){
	    super(s);
	    this.type = type;
	  }
  public String getType() {
	  return type;
  }
}
