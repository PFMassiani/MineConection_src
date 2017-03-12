package serveur;
import java.io.IOException;
import java.net.ServerSocket;

public class Serveur {
	private static ServerSocket serv;
	private static int port = 10000;
	private static boolean stop = false, pause = false;
	private static int clients = 0;
	
	public static void main(String[] args) {
		try {
			serv = new ServerSocket(port);
			serv.setReuseAddress(true);
			System.out.println("Server: online");
			while (!stop) {
				if (!pause) {
					(new ServeurVirtuel(serv.accept())).start();
					clients++;
					System.out.println("Nombre de clients: " + clients);
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
