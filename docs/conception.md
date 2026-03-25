# Conception technique

> Ce document décrit l'architecture technique de votre projet. Vous êtes dans le rôle du lead-dev / architecte. C'est un document technique destiné à des développeurs.

## Vue d'ensemble
L'application est structurée selon une architecture en couches garantissant le découplage entre l'interface utilisateur, la logique métier et la sécurité.
1. Présentation (UI) — Interface utilisateur
2. Security (Moteur de Chiffrement) — chiffrement et déchiffrement les données
3. Persistance (Stockage) — Ne stocke que des données chiffrées

```
UI  →  Security (CryptoService)  →  Persistance 
```


## Design Patterns

### DP 1 — *Adapter, Wrapper*

**Feature associée** : import / export des données

**Justification** : import / export des données en différents format (CSV, json, XML)

**Intégration** : 
Création d'une interface DataExporter
L'Adapter encapsule les bibliothèques pour les faire correspondre à notre interface.

### DP 2 — *Builder*

**Feature associée** : génération du mot de passe

**Justification** : ajouter des charactères spéciaux, longeur 

**Intégration** :
Implémentation d'une classe PasswordGenerator.Builder() qui permet d'enchaîner les appels de configuration

### DP 3 — *Factory*

**Feature associée** : Stackage 

**Justification** : permet de stocker différent elements dans le coffre (mdp, notes, CB, clef ssh).
permet d'ajouter facilement de nouveaux types d'éléments à l'avenir

**Intégration** :
Création d'une interface VaultItem. 
Une classe VaultItemFactory se charge d'instancier (PasswordItem, CreditCardItem, ...) en fonction des données fournies par l'utilisateur.
### DP 4 — *Strategy*

**Feature associée** : Chiffrement des mot de passe

**Justification** : Pouvoir interchanger l'algorithme de chiffrement dynamiquement (AES-256, ChaCha20, Argon2) selon les besoins de sécurité

**Intégration** :
Création d'une interface EncryptionStrategy avec une méthode encrypt(data).
## Diagrammes UML

### Diagramme 1 — *Classe*

    


```plantuml

```

### Diagramme 2 — *séquence*

```plantuml

```


