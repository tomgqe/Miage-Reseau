package miage.reseau.server;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Scanner;
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
	public Response(Request req, boolean listingDirectory) throws IOException {

		switch (req.getMethod()) {
		case "GET":
			// On recherche le fichier cible de la requete
			// si c'est un dossier et que le listing est activé on le gère
			// si trouvé et authorisé on le retourne avec les headers adéquats
			// si pas authorisé 403 Forbidden
			// si non trouvé on retourne un Statut 404
			// si exception : 400 bad request
			try {
				File file = new File(req.getUri());
				if (file.isDirectory() && listingDirectory) {
					if (checkAuthorization(req)) {
						listingDirectory(file, req.getFullUrl());
					}
				} else if (file.exists()) {
					if (checkAuthorization(req)) {
						fillHeaders(Status._200);
						setContentType(req.getUri(), headers);
						fillResponse(getBytes(file));
					}
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
			// Statut : not yet implemented si la methode n'est pas reconnue
			fillHeaders(Status._501);
			fillResponse(Status._501.toString());
		}
	}

	private void listingDirectory(File folder, String url) {
		String pathString;
		String html = "<ul>";
		html += "<li><a href=\"http://" + url.replace("/"+folder.getName(), "") + "\">.</a></li>";
		for (File file : folder.listFiles()) {
			
			html += "<li><a href=\"http://" + url+"/"+file.getName() + "\">"
                    + file.getName() + "</a></li>";
		}
		html += "</ul>";
		fillHeaders(Status._200);
		setContentType(ContentType.HTML, headers);
		fillResponse(html.getBytes());

	}

	// parcours tous les dossiers de l'uri à la recherche de .htpasswd
	// si trouvé on verifie l'authorization
	private boolean checkAuthorization(Request req) {
		String[] splittedUri = req.getUri().split("/");
		String testedUri = "";
		for (int i = 1; i < splittedUri.length - 1; i++) {
			File htpasswd = new File(testedUri + "/" + splittedUri[i] + "/.htpasswd");
			testedUri = testedUri + "/" + splittedUri[i];
			if (htpasswd.exists()) {
				if (!verifyAuthorization(htpasswd, req)) {
					return false;
				}
			}
		}
		return true;
	}

	private boolean verifyAuthorization(File htpasswd, Request req) {
		String header = req.getHeaders("Authorization");
		// si header authorization non renseigné on lève statut 401
		if (header == null) {
			LOG.info("Unauthorized access to" + req.getUri());
			fillHeaders(Status._401);
			fillResponse(Status._401.toString());
			return false;
		} else {
			// on split sur " " pour ne pas prendre le type d'authorization
			String id = header.split(" ").length > 0 ? header.split(" ")[1] : header;
			// on décode l'identifiant du client pour avoir le couple id/mdp
			id = new String(Base64.getDecoder().decode(id));
			try {
				// On parcourt le fichier htpasswd
				Scanner sc = new Scanner(htpasswd);
				boolean authorised = false;
				while (sc.hasNextLine()) {
					String line = sc.nextLine();
					// si on trouve une correspondance avec l'identifiant client on hash en md5 le
					// mdp client et on compare les mdp
					if (line.split(":")[0].equals(id.split(":")[0])) {
						MessageDigest md = MessageDigest.getInstance("MD5");
						md.update(id.split(":")[1].getBytes());
						byte[] digest = md.digest();
						String pwdMd5 = new BigInteger(1, digest).toString(16);
						if (line.split(":")[1].equals(pwdMd5)) {
							authorised = true;
						}
					}
				}
				sc.close();
				// si pas d'authorization, on ne va pas plus loin et on leve le statut 403
				if (!authorised) {
					LOG.info("Forbidden acces to" + req.getUri());
					fillHeaders(Status._403);
					fillResponse(Status._403.toString());
					return false;
				}
			} catch (Exception e) {
				System.out.println(e.getMessage());
				return false;
			}
		}
		return true;

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

	// Recupère l'extension du fichier et appel à l'enum Status pour spécifier le
	// content-type
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
	
	private void setContentType(ContentType contentType, List<String> list) {
		try {
			// L'Enum Content-Type est en majuscule pour eviter les probleme de casse donc
			// on passe l'extension en majuscule aussi
			list.add(contentType.toString());
		} catch (Exception e) {
			LOG.severe("ContentType non géré : " + e);
		}
	}
}
