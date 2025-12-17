# WordCrafter

Ce dépôt contient le code source du projet de programmation objet et algorithmique de deuxième année de licence. Il s'agit d'une application Java permettant de construire des mots à partir de morphèmes dans différents modes de jeu éducatifs.

## Lancement

Ce projet utilise Gradle comme système de build. Pour lancer le projet, vous pouvez utiliser différentes méthodes :

### Ligne de commande avec Gradle Wrapper (recommandé)

Sur Linux/macOS :
```bash
./gradlew run
```

Sur Windows :
```cmd
gradlew.bat run
```

### Compilation et création d'un JAR

Pour créer un JAR exécutable :
```bash
./gradlew build
```

Le JAR sera disponible dans `build/libs/wordcrafter-1.0-SNAPSHOT.jar`

Pour l'exécuter :
```bash
java -jar build/libs/wordcrafter-1.0-SNAPSHOT.jar
```

### Depuis votre IDE

Importez le projet en tant que projet Gradle, puis exécutez la classe principale `io.github.e_psi_lon.wordcrafter.WordCrafterApp`.

## Structure

Le projet est organisé selon une architecture MVC avec injection de dépendances :

- **[controller](/src/main/java/io/github/e_psi_lon/wordcrafter/controller)** : Contrôleurs pour la logique applicative
  - `AuthController` : Gestion de l'authentification et des comptes
  - `EditorController` : Contrôle du mode éditeur
  - `GameController` : Gestion de la logique de jeu
- **[database](/src/main/java/io/github/e_psi_lon/wordcrafter/database)** : Accès aux données via JDBC
  - `DatabaseManager` : Singleton gérant les connexions et opérations CRUD
- **[model](/src/main/java/io/github/e_psi_lon/wordcrafter/model)** : Entités métier
  - `User` (classe abstraite) : Utilisateur du système
  - `Player` : Joueur avec score
  - `Admin` : Administrateur avec droits d'édition
  - `Morpheme` : Unité linguistique de base
  - `Word` : Mot composé de morphèmes
  - `PlayerScore` : Score d'un joueur
- **[service](/src/main/java/io/github/e_psi_lon/wordcrafter/service)** : Services métier
  - `ServiceFactory` : Injection de dépendances
  - `AuthService` : Services d'authentification
  - `GameService` : Services de jeu
  - `PlayerService` : Services liés aux joueurs
  - `GameStateManager` : Gestion de l'état du jeu
- **[ui](/src/main/java/io/github/e_psi_lon/wordcrafter/ui)** : Interfaces graphiques Swing
  - `MainMenuFrame` : Menu principal
  - `LoginDialog` : Dialogue de connexion/inscription
  - `EditorFrame` : Interface d'édition des morphèmes et mots
  - `AccountSettingsFrame` : Paramètres du compte
  - `AppColors` : Palette de couleurs centralisée
  - **[game](/src/main/java/io/github/e_psi_lon/wordcrafter/ui/game)** : Modes de jeu
    - `GameFrame` (classe abstraite) : Base pour tous les modes
    - `MainGameFrame` : Mode principal avec grille de morphèmes
    - `FreeBuildFrame` : Mode construction libre
    - `PrefixMatcherFrame` : Mode préfixe-matcher (à venir)

## Base de données

Le projet utilise JDBC pour la connexion à la base de données et supporte deux SGBD :

### Configuration

La configuration se fait via variables d'environnement (voir `database.config.example`) :

- **Mode développement (par défaut)** : SQLite (`wordcrafter.db`)
- **Mode production** : MySQL/MariaDB

Variables d'environnement :
```bash
export DB_TYPE=SQLITE          # ou MYSQL
export DB_HOST=localhost       # pour MySQL
export DB_PORT=3306           # pour MySQL
export DB_NAME=wordcrafter    # pour MySQL
export DB_USER=wordcrafter    # pour MySQL
export DB_PASSWORD=motdepasse # pour MySQL
```

### Tables

| Table               | Description                           | Champs principaux                                                    |
|---------------------|---------------------------------------|----------------------------------------------------------------------|
| **`users`**         | Utilisateurs (joueurs et admins)      | `id`, `username`, `password_hash`, `role`, `score`                   |
| **`morphemes`**     | Morphèmes disponibles                 | `id`, `text`, `definition`                                           |
| **`words`**         | Mots valides                          | `id`, `text`, `points`, `definition`                                 |
| **`word_morphemes`**| Composition des mots                  | `word_id`, `morpheme_id`, `position`                                 |
| **`player_words`**  | Mots construits par chaque joueur     | `user_id`, `word_id`                                                 |

### Relations

Le schéma de données s'articule autour des relations suivantes :

- Un **utilisateur** (`users`) peut être un joueur (`PLAYER`) ou un administrateur (`ADMIN`) *(héritage via champ `role`)*
- Un **mot** (`words`) est composé de plusieurs **morphèmes** (`morphemes`) via `word_morphemes` *(relation N,M avec ordre)*
- Un **joueur** peut construire plusieurs **mots** via `player_words` *(relation N,M)*
- Les scores sont calculés dynamiquement en fonction des mots construits et stockés dans `users.score`

### Données par défaut

Au premier lancement, la base est automatiquement remplie avec :
- Un compte administrateur (`admin` / `admin`)
- Des morphèmes de base (préfixes, racines, suffixes)
- Quelques mots servant d'exemples

## Fonctionnalités

### Modes de jeu

- **Mode de jeu principal** : Grille de morphèmes à combiner pour former des mots valides
- **Mode construction libre** : Construisez des mots à partir de n'importe quels morphèmes, points basés sur la complexité
- **Mode préfixe-matcher** : *(Non implémenté - à venir)*

### Gestion des utilisateurs

- **Joueurs** : Inscription, connexion, suivi des scores et mots construits
- **Administrateurs** : Accès au mode éditeur pour gérer morphèmes et mots

### Mode éditeur (administrateurs uniquement)

- Ajout, modification et suppression de morphèmes
- Création de nouveaux mots avec sélection des morphèmes constitutifs
- Attribution de points et définitions

### Interface graphique

- Interface Swing avec thème pastel (rose nuageux)
- Layouts adaptatifs (BorderLayout, GridLayout, BoxLayout)
- Gestion de menus et dialogues

### Système de points

- **Mode principal** : Points définis par mot dans la base de données
- **Mode construction libre** : Points = nombre de morphèmes + (nombre de morphèmes - 1)²
  - Exemple : 3 morphèmes = 3 + 4 = 7 points

### Schéma de navigation

```
Menu Principal
├── Mode de Jeu Principal
│   └── Grille de morphèmes → Validation → Score
├── Mode Construction Libre
│   └── Liste de morphèmes → Construction → Score
├── Mode Préfixe-Matcher
│   └── (Non implémenté)
├── [Si non connecté]
│   └── Connexion/Inscription
│       ├── Nouveau compte (joueur)
│       └── Connexion (joueur/admin)
├── [Si connecté]
│   ├── Paramètres du compte
│   │   ├── Modifier le mot de passe
│   │   └── Voir les statistiques
│   ├── Déconnexion
│   └── [Si administrateur]
│       └── Mode éditeur
│           ├── Gestion des morphèmes
│           └── Gestion des mots
└── Quitter
```

## Technologies utilisées

- **Langage** : [Java 21](https://openjdk.org/projects/jdk/21/)
- **Build** : [Gradle 8.x](https://gradle.org/) avec Kotlin DSL
- **Base de données** : 
  - [SQLite JDBC 3.51.0.0](https://github.com/xerial/sqlite-jdbc) pour le développement
  - [MySQL Connector/J 9.5.0](https://dev.mysql.com/downloads/connector/j/) pour la production
- **Interface graphique** : [Swing](https://docs.oracle.com/javase/tutorial/uiswing/) (bibliothèque standard Java)
- **Tests** : [JUnit 5](https://junit.org/junit5/) (plateforme configurée)
- **Annotations** : [JetBrains Annotations](https://www.jetbrains.com/help/idea/annotating-source-code.html)

## Architecture

- **MVC** : Séparation modèle-vue-contrôleur
- **Objects** : `DatabaseManager`, `ServiceFactory`, `AppColors`
- **Factory** : `ServiceFactory` pour l'injection de dépendances
- **Écouteurs** : `GameStateListener` pour la mise à jour de l'UI
- **Héritage** : `User` -> `Player`/`Admin`, `GameFrame` → modes de jeu, `DatabaseEntity` -> classes de `model`

## Sécurité

- Hachage des mots de passe avec SHA-256
- Requêtes SQL préparées (protection contre les injections SQL)
- Séparation des privilèges (joueur/admin)

## Crédits

MAULNY Lilian & CABALLERO Simon, L2 Informatique, [Université de Tours](https://univ-tours.fr), antenne de Blois. Année 2024-2025.

## Licence

Ce projet est un projet universitaire sous licence GNU GPL v3.0 avec clauses additionnelles.
Voir [LICENSE](LICENSE) pour les détails.

