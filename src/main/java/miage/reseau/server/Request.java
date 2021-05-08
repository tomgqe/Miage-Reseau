package miage.reseau.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

public class Request {

	private static Logger LOG = Logger.getLogger(Request.class.getName());

	// Liste des headers de la requete pour pouvoir y accéder si on ajoute des
	// fonctionnalité
	private HashMap<String, String> headers = new HashMap<String, String>();

	// contenu de la premiere ligne de la requete
	private String method;
	private String uri;
	private String version;

	// valeur du header Host:
	private String host;

	// Parcours du stream entrant, parsing de la premiere ligne,
	// recupération du host et ajout des autres headers dans headers
	public Request(InputStream is, Server server) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		String line = reader.readLine();
		parseRequestFirstLine(line);

		while (!line.equals("")) {
			if (line.contains("Host:")) {
				host = line.split(":")[1];
				LOG.info(line);
			}
			line = reader.readLine();
			parseRequest(line);
		}
		String directory = server.getSourceDirectoryPath() + '/' + server.getDomainDirectory(host);
		// Redirection sur index.html en cas d'absence de fichier cible
		if (uri.equals("/")) {
			LOG.info("Redirigée vers index.html");
			uri = "/index.html";
		}
		// conversion de l'url de la requete en un chemin vers le fichier cible
		this.uri = directory + uri;
	}

	// Parsing de la premeiere ligne de la requete
	private void parseRequestFirstLine(String str) {
		LOG.info(str);
		String[] requestSplit = str.split("\\s+");
		method = requestSplit[0];
		uri = requestSplit[1];
		version = requestSplit[2];
	}

	private void parseRequest(String str) {
		// LOG.info(str);
		headers.put(str.split(":")[0], str.split(":")[1]);
	}

	public HashMap<String, String> getHeaders() {
		return headers;
	}

	public String getMethod() {
		return method;
	}

	public String getUri() {
		return uri;
	}

	public String getVersion() {
		return version;
	}

	public String getHost() {
		return host;
	}

}