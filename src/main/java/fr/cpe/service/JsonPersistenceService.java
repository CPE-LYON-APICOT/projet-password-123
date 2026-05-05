package fr.cpe.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import fr.cpe.model.User;
import fr.cpe.model.Vault;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

public class JsonPersistenceService {
    private static final String STORAGE_DIR = "storage";
    private static final String USERS_FILE = STORAGE_DIR + "/users.json";
    private final ObjectMapper mapper;
    public JsonPersistenceService() {
        this.mapper = new ObjectMapper();
        this.mapper.registerModule(new JavaTimeModule());
        this.mapper.enable(SerializationFeature.INDENT_OUTPUT);
        
        File dir = new File(STORAGE_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    public List<User> loadUsers() {
        File file = new File(USERS_FILE);
        if (!file.exists()) {
            return new ArrayList<>();
        }
        try {
            return mapper.readValue(file, new TypeReference<List<User>>() {});
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public void saveUsers(List<User> users) {
        try {
            mapper.writeValue(new File(USERS_FILE), users);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ObjectMapper getMapper() {
        return mapper;
    }

    public void saveUser(User user) {
        List<User> users = loadUsers();
        boolean found = false;
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getId().equals(user.getId())) {
                users.set(i, user);
                found = true;
                break;
            }
        }
        if (!found) {
            users.add(user);
        }
        saveUsers(users);
    }

    public Vault loadVault(UUID userId, byte[] key) {
        String vaultFile = STORAGE_DIR + "/vault_" + userId.toString() + ".json";
        File file = new File(vaultFile);
        if (!file.exists()) {
            return new Vault(userId);
        }
        try {
            User user = loadUsers().stream().filter(u -> u.getId().equals(userId)).findFirst().orElse(null);
            EncryptionStrategy strategy = user != null ? EncryptionStrategyFactory.getStrategy(user.getEncryptionType()) : new AES256Strategy();

            EncryptedVault wrapper = mapper.readValue(file, EncryptedVault.class);
            byte[] decryptedBytes = strategy.decrypt(Base64.getDecoder().decode(wrapper.encryptedData), key);
            return mapper.readValue(decryptedBytes, Vault.class);
        } catch (Exception e) {
            e.printStackTrace();
            return new Vault(userId);
        }
    }

    public void saveVault(Vault vault, byte[] key) {
        String vaultFile = STORAGE_DIR + "/vault_" + vault.getOwnerId().toString() + ".json";
        try {
            User user = loadUsers().stream().filter(u -> u.getId().equals(vault.getOwnerId())).findFirst().orElse(null);
            EncryptionStrategy strategy = user != null ? EncryptionStrategyFactory.getStrategy(user.getEncryptionType()) : new AES256Strategy();

            byte[] vaultBytes = mapper.writeValueAsBytes(vault);
            byte[] encryptedBytes = strategy.encrypt(vaultBytes, key);
            EncryptedVault wrapper = new EncryptedVault(Base64.getEncoder().encodeToString(encryptedBytes));
            mapper.writeValue(new File(vaultFile), wrapper);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static class EncryptedVault {
        public String encryptedData;

        public EncryptedVault() {}
        public EncryptedVault(String encryptedData) {
            this.encryptedData = encryptedData;
        }
    }
}
