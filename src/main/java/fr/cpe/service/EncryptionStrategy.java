package fr.cpe.service;

public interface EncryptionStrategy {
    byte[] encrypt(byte[] data, byte[] key) throws Exception;

    byte[] decrypt(byte[] data, byte[] key) throws Exception;

    String getName();
}