package miage.reseau.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.util.HashMap;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

import javax.swing.text.StyledEditorKit.BoldAction;

public class Server {

	private static Logger LOG = Logger.getLogger(Server.class.getName());

	// Port a écouter alimenté par le fichier config(80 par défaut)
	private int port = 80;

	// Nombre de Thread d'écoute maximum
	private int nbThreads = 3;

	// Chemin absolu vers les ressources
	private String sourceDirectoryPath;

	// Correspondance entre domaines et dossiers
	private HashMap<String, String> domainDirectory;
	
	//  Listing des répertoires (si false pas de listing)
	private boolean listingDirectory;

	// Une intsance de Server est lancé à chaque lancement de l'application
	public static void main(String args[]) {
		try {
			new Server().start();
		} catch (Exception e) {
			LOG.severe("Erreur dans la configuration du serveur");
		}
	}

	// Le serveur lit la config et écoute sur le port spécifié
	public void start() throws IOException {
		getProperties();
		ServerSocket s = new ServerSocket(port);
		System.out.println("Serveur actif sur le port " + port + " (CTRL-C pour quitter)");
		// On limite le nombre de connexions client en fonction de la config
		ExecutorService executor = Executors.newFixedThreadPool(nbThreads);
		while (true) {
			executor.submit(new RequestListener(s.accept(), this));
		}
	}

	// Parcours de la config et alimentation des attributs du serveur
	private void getProperties() throws IOException {
		File file = new File("config.properties");
		Properties properties = new Properties();
		properties.load(new FileInputStream(file));
		port = Integer.parseInt(properties.getProperty("port", "80"));
		int nbThreadTmp = Integer.parseInt(properties.getProperty("nbThreads", "3"));
		nbThreads = nbThreadTmp > 0 ? nbThreadTmp : 3;
		sourceDirectoryPath = properties.getProperty("directory");
		domainDirectory = new HashMap<>();
		String[] domains = properties.getProperty("domains").split(",");
		for (String domain : domains) {
			domainDirectory.put(domain.split(":")[0], domain.split(":")[1]);
		}
		listingDirectory = Boolean.parseBoolean(properties.getProperty("listingDirectory", "false"));
	}

	public String getSourceDirectoryPath() {
		return sourceDirectoryPath;
	}

	// Renvoie le dossier correspondant au domaine en paramètre
	public String getDomainDirectory(String domain) {
		return domainDirectory.get(domain);
	}
	
	public boolean getListingDirectory() {
		return this.listingDirectory;
	}

}
