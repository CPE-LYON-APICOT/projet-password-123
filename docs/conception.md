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
@startuml
title Architecture Globale - Password@123

' --- Gestion Utilisateur & Session ---
class User {
    - userId: UUID
    - email: String
    - masterPasswordHash: String
    - salt: String
}

class SessionManager {
    - currentUser: User
    - decryptionKey: SecretKey
    - lastActivity: DateTime
    + login(email, password)
    + logout()
    + isAuthenticated(): Boolean
    + getDecryptionKey(): SecretKey
}

class Vault {
    - ownerId: UUID
    - items: List<VaultItem>
}

' --- Design Pattern: Factory (Vault Items) ---
interface VaultItem {
    + title: String
    + getDetails(): String
}

class PasswordItem implements VaultItem {
    + username: String
    + encryptedPassword: String
}

class CreditCardItem implements VaultItem {
    + cardNumber: String
    + expiryDate: String
}

class VaultItemFactory {
    + createItem(type: String): VaultItem
}

' --- Design Pattern: Strategy (Chiffrement) ---
interface EncryptionStrategy {
    + encrypt(data: String): String
    + decrypt(data: String): String
}

class AES256Strategy implements EncryptionStrategy {
    + encrypt(data: String): String
    + decrypt(data: String): String
}

class Argon2Strategy implements EncryptionStrategy {
    + encrypt(data: String): String
    + decrypt(data: String): String
}

' --- Design Pattern: Builder (Générateur) ---
class PasswordGenerator {
    - length: int
    - useSpecialChars: boolean
    + generate(): String
}

class Builder {
    + setLength(len: int): Builder
    + setSpecialChars(bool: boolean): Builder
    + build(): PasswordGenerator
}

' --- Relations Globales ---
SessionManager "1" o-- "1" User : gère
User "1" -- "1" Vault : possède
SessionManager ..> Vault : déverrouille
Vault "1" *-- "many" VaultItem : contient
VaultItemFactory ..> VaultItem : crée
VaultItem o-- EncryptionStrategy : utilise
PasswordGenerator +-- Builder

@enduml
```

### Diagramme 2 — *séquence*

```plantuml
@startuml
title Flux d'Authentification et d'Accès aux Données - Password@123

actor Utilisateur
participant "UI (Interface)" as UI
participant "SessionManager" as Session
participant "Persistence (DB)" as DB
participant "Vault" as Vault
participant "EncryptionStrategy" as Strategy

== Phase 1 : Authentification & Initialisation ==

Utilisateur -> UI : Saisit Email + Master Password
UI -> Session : login(email, password)
activate Session

Session -> DB : getUser(email)
activate DB
return UserData (Hash + Salt)

Session -> Session : Verify Password Hash
Session -> Session : Derive DecryptionKey (PBKDF2/Argon2)

Session --> UI : Auth Success
deactivate Session

== Phase 2 : Récupération du Coffre ==

UI -> DB : getEncryptedVault(userId)
activate DB
return EncryptedData
UI -> Vault : load(EncryptedData)

== Phase 3 : Consultation d'un Elément (Déchiffrement) ==

Utilisateur -> UI : Sélectionne un "PasswordItem"
UI -> Vault : getItemDetails(itemId)
activate Vault

Vault -> Session : getDecryptionKey()
activate Session
return DecryptionKey

Vault -> Strategy : decrypt(encryptedData, key)
activate Strategy
return PlainText Password
deactivate Strategy

Vault --> UI : Clear Text Data
deactivate Vault

UI -> Utilisateur : Affiche le mot de passe
@enduml
```


