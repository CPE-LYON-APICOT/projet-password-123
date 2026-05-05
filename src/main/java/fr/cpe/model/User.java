package fr.cpe.model;
import java.util.UUID;


public class User {

    private UUID id;
    private String email;
    private String masterPasswordHash;
    private String salt;
    private String encryptionType = "AES-256";

    public User() {}

    public User(String email, String masterPasswordHash, String salt, String encryptionType) {
        this.id = UUID.randomUUID();
        this.email = email;
        this.masterPasswordHash = masterPasswordHash;
        this.salt = salt;
        this.encryptionType = encryptionType != null ? encryptionType : "AES-256";
    }

    public User(String email, String masterPasswordHash, String salt) {
        this(email, masterPasswordHash, salt, "AES-256");
    }

    public UUID getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getMasterPasswordHash() {
        return masterPasswordHash;
    }

    public String getSalt() {
        return salt;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setMasterPasswordHash(String masterPassword) {
        this.masterPasswordHash = masterPassword;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getEncryptionType() {
        return encryptionType;
    }

    public void setEncryptionType(String encryptionType) {
        this.encryptionType = encryptionType;
    }
}



