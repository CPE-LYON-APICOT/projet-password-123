# Fiche rendu projet

> Ce document est un bilan destiné au client. Présentez ce qui a été livré, ce qui fonctionne, et tournez habilement ce qui manque. Pas de jargon technique — on parle de fonctionnalités et de valeur perçue.

## Rappel du projet

**Password@123** devait être un coffre-fort numérique personnel : un seul mot de passe maître pour protéger tous vos accès, un stockage sécurisé par chiffrement, un générateur de mots de passe robustes, et la possibilité d'importer/exporter vos données depuis d'autres gestionnaires. L'objectif : offrir une alternative simple, locale et sécurisée.

## Ce qui a été livré

### Fonctionnalité 1 — Authentification par mot de passe maître


L'accès à l'application est protégé par un mot de passe maître unique. Lors de la première utilisation, un compte est créé automatiquement. Pour les connexions suivantes, le mot de passe est vérifié de manière sécurisée — sans jamais stocker le mot de passe en clair. Si les identifiants sont incorrects, l'accès est refusé.

### Fonctionnalité 2 — Coffre-fort chiffré avec choix de l'algorithme

Toutes vos données sont chiffrées avant d'être enregistrées sur le disque. Deux algorithmes de chiffrement reconnus sont disponibles au moment de la création du compte : **AES-256** (standard de l'industrie) et **Blowfish** (alternative rapide). Seul votre mot de passe maître peut déverrouiller le coffre.

### Fonctionnalité 3 — Stockage de plusieurs types d'éléments

Le coffre-fort accepte trois catégories d'éléments :
- **Mots de passe** : nom du service, identifiant, mot de passe.
- **Cartes bancaires** : numéro, date d'expiration, CVV.
- **Clés SSH** : clé publique et clé privée pour les développeurs et administrateurs systèmes.

Chaque élément est modifiable et supprimable à tout moment. Une interface de détail permet de consulter et éditer chaque entrée sans quitter la vue principale.

### Fonctionnalité 4 — Génération de mots de passe aléatoires

Un générateur intégré crée des mots de passe complexes et uniques en un clic. Il est entièrement configurable : longueur (de 4 à 64 caractères), inclusion de majuscules, de chiffres et de caractères spéciaux. Les mots de passe générés garantissent au moins un caractère de chaque type activé, puis complètent et mélangent aléatoirement le reste.

### Fonctionnalité 5 — Import et export multi-formats

L'application permet d'importer vos données depuis les gestionnaires les plus populaires du marché :
- **NordPass**, **1Password** et **ProtonPass** via leurs formats CSV spécifiques.
- Format **JSON** et **XML** pour les échanges techniques.
La détection du format est automatique à l'import.

### Fonctionnalité 6 — Recherche et filtrage par catégorie

La barre de recherche filtre les éléments en temps réel par nom. La barre latérale permet de basculer entre les catégories (Tous, Mots de passe, Cartes). Les deux filtres se combinent, ce qui permet de retrouver instantanément et ce même dans un coffre contenant de nombreux éléments.

## Ce qui n'a pas été livré (et pourquoi)

### Analyse des mots de passe faibles — Fondations posées, finition à venir

La feature était prévue dans le pitch : Les données nécessaires (le mot de passe) sont accessibles depuis le modèle, mais le module d'analyse — détection des mots de passe trop courts, réutilisés, ou issus de listes connues — n'a pas été implémenté dans le temps imparti. C'est une couche d'analyse à greffer sur l'existant.

### Type "Notes sécurisées" — Non implémenté

Le pitch évoquait la possibilité de stocker des notes confidentielles et des codes de récupération. L'architecture (via l'interface `IVaultItem` et la `VaultItemFactory`) est conçue pour accueillir de nouveaux types sans modification du reste de l'application. Ajouter un `NoteItem` est une extension possible au travail existant.

### Algorithme Argon2 / ChaCha20 — Remplacé par Blowfish

La conception initiale citait Argon2 et ChaCha20 comme stratégies de chiffrement. Ces algorithmes nécessitent des bibliothèques externes non disponibles dans la JDK standard. Blowfish, disponible nativement via `javax.crypto`, a été retenu à la place — il reste un algorithme solide et éprouvé pour ce cas d'usage.

## Perspectives

### Court terme

- **Analyse de force des mots de passe** : alerter l'utilisateur si un mot de passe est trop court, trop simple ou réutilisé dans plusieurs entrées.
- **Type "Notes sécurisées"** : ajouter une entrée libre pour stocker des codes de récupération, des PINs ou des informations sensibles non catégorisées.
- **Dérivation de clé renforcée** : remplacer la dérivation actuelle par PBKDF2 ou Argon2 pour résister aux attaques par force brute sur le mot de passe maître.

### Moyen terme

- **Générateur de codes 2FA (TOTP)** : intégrer un générateur de codes temporaires directement dans l'application pour ne plus dépendre d'une app tierce.
- **Verrouillage automatique par inactivité** : déconnecter automatiquement la session après une période d'inactivité configurable (le modèle `SessionManager` contient déjà le champ `lastActivity`).
- **Interface de gestion du profil** : permettre à l'utilisateur de changer son mot de passe maître ou son algorithme de chiffrement sans recréer son compte.

### Plus loin

- **Alias e-mail éphémères** : créer des adresses factices pour s'inscrire sur des sites sans exposer son adresse personnelle.
- **Partage sécurisé** : partager un mot de passe (Wi-Fi, abonnement familial) avec un proche via un lien chiffré à durée limitée.
- **Remplissage automatique** : extension navigateur qui reconnaît les sites et remplit les formulaires de connexion automatiquement.

