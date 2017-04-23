package serveur;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
class Serveur {
	private static ServerSocket serv;
	private static int port = 10000;
	private static boolean stop = false, pause = false;
//	private static int clients = 0;
	
	public static void main(String[] args) {
		try {
			serv = new ServerSocket(port);
			serv.setReuseAddress(true);
			System.out.println("Serveur en ligne. En attente de connexion client.");
			Socket client;
			while (!stop) {
				if (!pause) {
					client = serv.accept();
					(new ServeurVirtuel(client,client.getInetAddress().toString())).start();
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
