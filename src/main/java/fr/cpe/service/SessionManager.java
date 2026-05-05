package fr.cpe.service;

import javax.crypto.SecretKey;

import fr.cpe.model.User;

import java.time.LocalDateTime;

import com.google.inject.Singleton;

@Singleton
public class SessionManager {
    private User currentUser;
    private SecretKey decryptionKey; 
    private LocalDateTime lastActivity;

    public SessionManager() {}


    public SecretKey getDecryptionKey() {
        return this.decryptionKey;
    }

    public void login(User user, SecretKey decryptionKey) {
        this.currentUser = user;
        this.decryptionKey = decryptionKey;
        this.lastActivity = LocalDateTime.now();
    }

    public void logout() {
        this.currentUser = null;
        this.decryptionKey = null;
        this.lastActivity = null;
    }

    public boolean isLoggedIn() {
        return currentUser != null;
    }


    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    public User getCurrentUser() {
        return currentUser;
    }

}

