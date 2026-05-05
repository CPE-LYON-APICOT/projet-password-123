package fr.cpe.service;

import fr.cpe.model.CreditCardItem;
import fr.cpe.model.PasswordItem;
import fr.cpe.model.User;
import fr.cpe.model.Vault;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class JsonPersistenceServiceTest {

    private JsonPersistenceService service;

    @BeforeEach
    void setUp() {
        service = new JsonPersistenceService();
    }

    @Test
    void testSaveAndLoadUsers() {
        User user = new User("test@example.com", "hash", "salt");
        service.saveUser(user);

        List<User> users = service.loadUsers();
        assertFalse(users.isEmpty());
        
        User loadedUser = users.stream()
                .filter(u -> u.getId().equals(user.getId()))
                .findFirst()
                .orElse(null);
        
        assertNotNull(loadedUser);
        assertEquals(user.getEmail(), loadedUser.getEmail());
        assertEquals(user.getMasterPasswordHash(), loadedUser.getMasterPasswordHash());
        assertEquals(user.getSalt(), loadedUser.getSalt());
    }

    @Test
    void testSaveAndLoadVault() {
        UUID userId = UUID.randomUUID();
        Vault vault = new Vault(userId);
        byte[] dummyKey = new byte[16]; // 128-bit key for AES
        
        PasswordItem pwd = new PasswordItem("Github", "My github pwd", "user", "secret");
        CreditCardItem card = new CreditCardItem("Visa", "Main card", "1234", "12/25", "123");
        
        vault.addItem(pwd);
        vault.addItem(card);
        
        service.saveVault(vault, dummyKey);
        
        Vault loadedVault = service.loadVault(userId, dummyKey);
        assertNotNull(loadedVault);
        assertEquals(userId, loadedVault.getOwnerId());
        assertEquals(2, loadedVault.getItems().size());
        
        assertTrue(loadedVault.getItems().get(0) instanceof PasswordItem);
        assertTrue(loadedVault.getItems().get(1) instanceof CreditCardItem);
        
        PasswordItem loadedPwd = (PasswordItem) loadedVault.getItems().get(0);
        assertEquals("Github", loadedPwd.getName());
        assertEquals("secret", loadedPwd.getPassword());
        
        CreditCardItem loadedCard = (CreditCardItem) loadedVault.getItems().get(1);
        assertEquals("Visa", loadedCard.getName());
        assertEquals("1234", loadedCard.getCardNumber());
    }
}
