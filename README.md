# Miage-Reseau
Le projet de Réseau de la MIAGE

## Fonctionnalités
Le projet est un serveur web from scratch. 
Les fonctionnalité présentes sont :
- Traitement des requêtes GET 
- Gestion des statuts courants
- Gestion multisite
- Gestion de plusieurs connexions en parallèle (avec paramétrage du nombre maximum)
- Affichage de log sur stdout : ip de l'appelant + requête

### Fonctionnalité prévues
- Protection de ressource par une authentification basique
- Gestion du listing des répertoires (paramétrage dans la config).

## Généralités

Projet réalisé par :

- Tom GHESQUIERE
- Alexi JACQUET
- Tristan LUC

pour le projet de Réseaux Informatiques avec Jérôme Bertrand en M1 MIAGE (2020/2021)

## Utilisation

### Installation
Cloner le projet.
Le projet est prêt à être lancé.
Si vous souhaitez recharger le projet maven : `mvn clean install`.

### Configurer le serveur

Le fichier config.properties se trouve dans le dossier bindist/bin

Il permet le paramétrage du :
- port écouté par le serveur : port
- chemin absolu vers le dossier qui contient les ressources : directory
- lien entre noms de domaines et dossiers de ressources : domains

			exemple : domains = verti.att:verti
	
			les ressources du dommaine verti.att sont dans le dossier verti
			

#### Pensez à configurer le etc/hosts

Paramétrer le fichier /etc/hosts pour que les domaines configuré plus tôt soit redirigé sur 127.0.0.1			
			
			exemple : 
			
			127.0.0.1	verti.att
			

### Lancer le serveur

* Windows

Exécuter le fichier situé dans : bindist/bin/run.bat


* MacOs / Unix

```bash
./bindist/bin/run 
```