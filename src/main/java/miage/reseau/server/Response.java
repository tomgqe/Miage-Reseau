package miage.reseau.server;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import miage.reseau.commonEnums.ContentType;
import miage.reseau.commonEnums.Status;

public class Response {

	private static Logger LOG = Logger.getLogger(Response.class.getName());

	// la version de la reponse
	public static final String VERSION = "HTTP/1.0";

	// headers transmis par la réponse
	private List<String> headers = new ArrayList<String>();

	byte[] body;

	// construction de la reponse :
	// un cas par méthode HTTP gérée
	public Response(Request req) throws IOException {

		switch (req.getMethod()) {
		case "GET":
			// On recherche le fichier cible de la requete
			// si trouvé on le retourne avec les headers adéquats
			// sinon on retourne un Statut 404
			try {
				File file = new File(req.getUri());
				if (file.exists()) {
					fillHeaders(Status._200);
					setContentType(req.getUri(), headers);
					fillResponse(getBytes(file));
				} else {
					LOG.info("File not found:" + req.getUri());
					fillHeaders(Status._404);
					fillResponse(Status._404.toString());
				}
			} catch (Exception e) {
				LOG.severe("Response Error");
				fillHeaders(Status._400);
				fillResponse(Status._400.toString());
			}

			break;
		default:
			// Statut : not yet implemented
			fillHeaders(Status._501);
			fillResponse(Status._501.toString());
		}
	}

	// Conversion du fichier en byte
	private byte[] getBytes(File file) throws IOException {
		int length = (int) file.length();
		byte[] array = new byte[length];
		InputStream in = new FileInputStream(file);
		int offset = 0;
		while (offset < length) {
			int count = in.read(array, offset, (length - offset));
			offset += count;
		}
		in.close();
		return array;
	}

	// ajout des headers fixes
	private void fillHeaders(Status status) {
		headers.add(Response.VERSION + " " + status.toString());
		headers.add("Connection: close");
		headers.add("Server: MiageReseauServer");
	}

	// Deux méthodes fillResponse
	// pour retourner sous forme de tableau de bytes le fichier cible peu importe
	// son format
	private void fillResponse(String response) {
		body = response.getBytes();
	}

	private void fillResponse(byte[] response) {
		body = response;
	}
	// Ecriture de la reponse dans le stream de sortie
	// Mis au bon format en ajoutant des retours à ligne
	public void write(OutputStream os) throws IOException {
		DataOutputStream output = new DataOutputStream(os);
		for (String header : headers) {
			output.writeBytes(header + "\r\n");
		}
		output.writeBytes("\r\n");
		if (body != null) {
			output.write(body);
		}
		output.writeBytes("\r\n");
		output.flush();
	}

	// Recupère l'extension du fichier et appel à l'enum Status pour spécifier le content-type
	private void setContentType(String uri, List<String> list) {
		try {
			String ext = uri.substring(uri.indexOf(".") + 1);
			// L'Enum Content-Type est en majuscule pour eviter les probleme de casse donc
			// on passe l'extension en majuscule aussi
			list.add(ContentType.valueOf(ext.toUpperCase()).toString());
		} catch (Exception e) {
			LOG.severe("ContentType non géré : " + e);
		}
	}
}
