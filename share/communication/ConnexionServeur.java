package share.communication;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ConnexionServeur {
	private static Socket serveur;
	private static int port = 10000;
	private static ObjectOutputStream oos;
	private static ObjectInputStream ios;
	
	{
		new ConnexionServeur();
	}
	
	private ConnexionServeur() {
		try{
			serveur = new Socket("localhost", port);
			oos = new ObjectOutputStream(serveur.getOutputStream());
			ios = new ObjectInputStream(serveur.getInputStream());
		} catch (IOException ex) {
			System.out.println(ex.getMessage());
		}
	}
	public static Socket getInstance() {
		if (serveur == null) new ConnexionServeur();
		return serveur;
	}
	
	public static ObjectOutputStream getOOS() {
		if( serveur == null ) new ConnexionServeur();
		return oos;
	}
	
	public static ObjectInputStream getIOS() {
		if( serveur == null ) new ConnexionServeur();
		return ios;
	}
}
