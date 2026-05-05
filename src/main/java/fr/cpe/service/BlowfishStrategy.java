package fr.cpe.service;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class BlowfishStrategy implements EncryptionStrategy {

    private static final String ALGORITHM = "Blowfish";

    @Override
    public byte[] encrypt(byte[] data, byte[] key) throws Exception {
        SecretKeySpec secretKey = new SecretKeySpec(key, ALGORITHM);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        return cipher.doFinal(data);
    }

    @Override
    public byte[] decrypt(byte[] data, byte[] key) throws Exception {
        SecretKeySpec secretKey = new SecretKeySpec(key, ALGORITHM);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        return cipher.doFinal(data);
    }

    @Override
    public String getName() {
        return "Blowfish";
    }
}