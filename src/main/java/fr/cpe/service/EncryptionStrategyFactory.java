package fr.cpe.service;

public class EncryptionStrategyFactory {
    public static EncryptionStrategy getStrategy(String name) {
        if ("Blowfish".equals(name) || "BlowfishStrategy".equals(name)) {
            return new BlowfishStrategy();
        }
        // Default to AES-256
        return new AES256Strategy();
    }
}
