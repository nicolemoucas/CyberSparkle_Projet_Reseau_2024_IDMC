# Cyber Sparkle 💎
Cyber Sparkle est un système de délégation de tâches qui permet de trouver un _hash_ qui commence par _n_ zéros en cherchant le _nonce_ correct, méthode appelée _Proof Of Work_ utilisée par le Bitcoin.

## Table des matières
- [Installation](#installation)
  - [À partir du dépôt Git](#a-partir-du-depot-git)
  - [À partir des fichiers .jar](#a-partir-des-fichiers-jar)
- [Utilisation](#utilisation)
  - [Miner 101](#miner-101)
  - [Miner avec plusieurs machines](#miner-plusieurs-machines)
- [Auteurs](#auteurs)

## [Installation](#installation)
Vous aurez besoin du [JDK Java 21](https://www.oracle.com/java/technologies/javase/jdk21-archive-downloads.html) et de [Maven](https://maven.apache.org/download.cgi).

### [À partir du dépôt Git](#a-partir-du-depot-git)
Ouvrez une interface en ligne de commandes (CLI) et déplacez vous dans le dossier où vous voulez cloner le dépôt de Cyber Sparkle. Vous pouvez utiliser la CLI d'une IDE tel que Visual Studio Code ou IntelliJ IDEA.
1. Clonez le dépôt Git de [Cyber Sparkle](https://github.com/weiss54/S8_reseau.git) :
```
git clone https://github.com/weiss54/S8_reseau.git
```
2. Pour builder le projet il suffit d'exécuter la commande `mvn clean install` à la racine du projet.

### [À partir des fichiers .jar](#a-partir-des-fichiers-jar)
Vous aurez besoin des fichiers `S8_projet_reseau-1.0-SNAPSHOT-client.jar` et `S8_projet_reseau-1.0-SNAPSHOT-serveur.jar`.

## [Utilisation](#utilisation)
### [Miner 101](#miner-101)
1. Si vous avez récupéré le projet du dépôt Git vous devez d'abord le _builder_ pour générer les fichiers -jar avec les commandes suivantes :
```
mvn clean
mvn install
```

2. Ouvrez deux fenêtres de terminal ou CLI et naviguez dans le dossier où se trouvent les fichiers .jar. Veuillez bien identifier une fenêtre "serveur" et une fenêtre "client" et respecter l'ordre des commandes afin de ne pas avoir des problèmes.

3. Lancez la commande suivante dans la fenêtre serveur :
```
java -jar S8_projet_reseau-1.0-SNAPSHOT-serveur.jar
```
Le serveur va commencer à écouter les connexions au port 1337.

4. Lancez la commande suivante dans la fenêtre client :
```
java -jar S8_projet_reseau-1.0-SNAPSHOT-client.jar
```
Une série d'instructions seront exécutées pour vérifier la connexion entre le serveur et le client.

Vous pouvez arrêter à tout moment l'exécution du programme avec les touches `CTRL + C`.

5. Vous pouvez taper `help` dans la fenêtre serveur pour afficher un menu avec les commandes disponibles. Pour résoudre une tâche avec une difficulté _d_ exécutez la commande suivante en remplaçant _d_ par le niveau de difficulté (entier de 1 à 32) :
```
solve <d>
```
La résolution de la tâche commencera et vous verez sur la fenêtre client que des instructions pour la résolution sont reçues. Si la tâche a déjà été résolue ou lorsque la solution est trouvée, elle s'affichera sur la fenêtre serveur.

6. Vous pouvez demander le statut de la tâche lors de son exécution avec la commande dans la fenêtre serveur :
```
status
```

7. Pour quitter le programme, exécutez la commande suivante dans la fenêtre serveur :
```
quit
```

### [Miner avec plusieurs machines](#miner-plusieurs-machines)
Lorsque la difficulté des tâches augmente, le temps de résolution augmente aussi, c'est pourquoi il est possible de connecter plusieurs machines pour accélérer le processus (comme de vrais mineurs de Bitcoin !). Pour ce faire vous aurez besoin de plusieurs ordinateurs (2+), du fichier `.jar` pour le serveur dans un ordinateur et des images Docker dans chacune des autres machines (qui seront les clients ou _workers_).

Pour simplifier ce processus, nous avons mis en place Docker pour générer une image qui peut être réutilisée. Si vous avez déjà cette image vous pouvez vous avancer à l'étape 3.

Pour générer l'image vous avez besoin de Docker que vous pouvez installer avec [Homebrew](https://formulae.brew.sh/formula/docker), et de [Docker Desktop](https://docs.docker.com/desktop/).
1. Lancez Docker Desktop dans la machine serveur.
2. Ouvrez une fenêtre du terminal ou CLI et déplacez vous dans le dossier racine du projet. Exécutez les commandes suivantes pour _builder_ le projet, générer une image Docker et l'enregistrer :
```
mvn clean
mvn install
docker image build -t client .
docker image save -o target/client client
```
Un fichier intitulé "client" devrait apparaître maintenant dans le dossier target du projet. Envoyez cette image à toutes les machines client.

3. Vérifiez l'adresse IP locale du serveur, vous pouvez suivre [ce guide](https://www.avg.com/en/signal/find-ip-address).

4. Naviguez dans le dossier avec le fichier `S8_projet_reseau-1.0-SNAPSHOT-serveur.jar` et exécutez la commande suivante pour lancer le serveur :
```
java -jar S8_projet_reseau-1.0-SNAPSHOT-serveur.jar
```

5. Démarrez Docker Desktop sur le ou les ordinateurs qui seront les clients pour démarrer automatiquement Docker daemon, vous pouvez le vérifier avec la commande :
```
docker info
```
Si Docker daemon est actif, vous devriez voir des informations en commençant par "Client [...]" ainsi que la version de Docker utilisée.

6. Ouvrez une fenêtre du terminal ou CLI dans le.s ordinateur.s client.s, déplacez vous dans le dossier qui contient le fichier "client" et exécutez les commandes :
```
docker image load -i client
docker run client:latest <adresse_ip_serveur>
```
Vous pouvez vérifier que l'image s'exécute avec la commande suivante depuis une autre fenêtre du terminal :
```
docker ps
```

7. Vous pouvez maintenant intéragir avec le serveur comme quand il y avait une seule machine. Vous pouvez utiliser `help` pour afficher les options disponibles et `solve <d>` pour résoudre une tâche de difficulté _d_. Vous verez dans le terminal des clients que la valeur de l'incrément (`inc`) correspond au nombre de clients (ordinateurs).

## [Auteurs](#auteurs)
- CASTRO MOUCHERON Nicole
- FAYNOT Marine
- FRASELLE Nadège
- WEISS Lucas