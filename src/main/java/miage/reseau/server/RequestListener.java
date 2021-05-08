package miage.reseau.server;

import java.io.OutputStream;
import java.net.Socket;
import java.util.logging.Logger;

public class RequestListener implements Runnable {

	private static Logger LOG = Logger.getLogger(RequestListener.class.getName());

	private Socket clientSocket;

	// Server dont est issue cette instance pour avoir avoir acces à ses propriétés
	private Server server;

	// Objet qui convertit la stream entrant en Request
	// puis créer la Response adéquate et l'envoie au client
	public RequestListener(Socket socket, Server server) {
		this.clientSocket = socket;
		this.server = server;
	}

	@Override
	public void run() {
		try {
			Request req = new Request(clientSocket, this.server);
			Response res = new Response(req);
			res.write(clientSocket.getOutputStream());
			clientSocket.close();
		} catch (Exception e) {
			LOG.severe("Runtime Error " + e.getMessage());
		}

	}

}
