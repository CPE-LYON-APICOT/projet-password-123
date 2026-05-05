package fr.cpe;

import fr.cpe.model.*;
import fr.cpe.service.*;
import org.junit.jupiter.api.Test;

import javax.crypto.spec.SecretKeySpec;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class AppTest {

    // =========================================================================
    // TESTS DES MODELES (fr.cpe.model)
    // =========================================================================

    @Test
    void testUser() {
        User user = new User("test@cpe.fr", "hash123", "salt456");
        System.out.println("--- Test User ---");
        System.out.println("Email: " + user.getEmail());
        System.out.println("ID: " + user.getId());

        assertEquals("test@cpe.fr", user.getEmail());
        assertEquals("hash123", user.getMasterPasswordHash());
        assertEquals("salt456", user.getSalt());
        assertNotNull(user.getId());

        user.setEmail("new@cpe.fr");
        System.out.println("Nouveau Email: " + user.getEmail());
        assertEquals("new@cpe.fr", user.getEmail());
    }

    @Test
    void testPasswordItem() {
        PasswordItem item = new PasswordItem("GMAIL", "Compte principal", "user123", "secretPass");
        System.out.println("--- Test PasswordItem ---");
        System.out.println("Item: " + item.getName() + " | Login: " + item.getUsername() + " | Pass: " + item.getPassword());

        assertEquals("GMAIL", item.getName());
        assertEquals("user123", item.getUsername());
        assertEquals("secretPass", item.getPassword());
    }

    @Test
    void testCreditCardItem() {
        CreditCardItem card = new CreditCardItem("Ma Carte", "Perso", "1234-5678", "12/25", "999");
        System.out.println("--- Test CreditCardItem ---");
        System.out.println("Carte: " + card.getName() + " | Num: " + card.getCardNumber());

        assertEquals("1234-5678", card.getCardNumber());
        assertEquals("12/25", card.getExpiryDate());
    }

    @Test
    void testVault() {
        UUID ownerId = UUID.randomUUID();
        Vault vault = new Vault(ownerId);
        PasswordItem item = new PasswordItem("Site", "desc", "u", "p");

        vault.addItem(item);

        assertEquals(ownerId, vault.getOwnerId());
        assertEquals(1, vault.getItems().size());
        assertEquals(item, vault.getItems().get(0));
    }

    @Test
    void testVaultItemFactory() {
        // Test creation Password
        IVaultItem pass = VaultItemFactory.createPassword("N", "D", "U", "P");
        assertTrue(pass instanceof PasswordItem);

        // Test creation via Map
        Map<String, String> data = new HashMap<>();
        data.put("name", "Visa");
        data.put("description", "Bleue");
        data.put("number", "0000");
        data.put("expiry", "01/01");
        data.put("cvv", "123");

        IVaultItem card = VaultItemFactory.createItem("CARD", data);
        assertTrue(card instanceof CreditCardItem);
        assertEquals("Visa", card.getName());

        // Test erreur type inconnu
        assertThrows(IllegalArgumentException.class, () -> VaultItemFactory.createItem("INCONNU", data));
    }

    // =========================================================================
    // TESTS DES SERVICES (fr.cpe.service)
    // =========================================================================

    @Test
    void testAES256Strategy() throws Exception {
        AES256Strategy strategy = new AES256Strategy();
        byte[] key = "12345678901234567890123456789012".getBytes(); // 32 octets pour AES-256
        String secret = "Message Secret";

        System.out.println("--- Test AES-256 ---");
        byte[] encrypted = strategy.encrypt(secret.getBytes(), key);
        System.out.println("Message chiffre (octets): " + java.util.Arrays.toString(encrypted));

        byte[] decrypted = strategy.decrypt(encrypted, key);
        System.out.println("Message dechiffre: " + new String(decrypted));

        assertEquals(secret, new String(decrypted));
        assertEquals("AES-256", strategy.getName());
    }

    @Test
    void testBlowfishStrategy() throws Exception {
        BlowfishStrategy strategy = new BlowfishStrategy();
        byte[] key = "12345678".getBytes(); // Blowfish accepte des clés plus courtes (8-56 octets)
        String secret = "Message Secret Blowfish";

        System.out.println("--- Test Blowfish ---");
        byte[] encrypted = strategy.encrypt(secret.getBytes(), key);
        System.out.println("Message chiffre (octets): " + java.util.Arrays.toString(encrypted));

        byte[] decrypted = strategy.decrypt(encrypted, key);
        System.out.println("Message dechiffre: " + new String(decrypted));

        assertEquals(secret, new String(decrypted));
        assertEquals("Blowfish", strategy.getName());
    }

    @Test
    void testPasswordGeneratorBuilder() {
        System.out.println("--- Test PasswordGenerator ---");
        // Cas 1 : Longueur 10 avec chiffres
        PasswordGenerator gen1 = new PasswordGenerator.Builder().setLength(10).setUseUpper(false).setUseSpecial(false)
                .build();
        String p1 = gen1.generate();
        System.out.println("MDP 1 (len 10, no upper): " + p1);
        assertEquals(10, p1.length());

        // Cas 2 : Longueur 25 avec tout
        PasswordGenerator gen2 = new PasswordGenerator.Builder().setLength(25).build();
        String p2 = gen2.generate();
        System.out.println("MDP 2 (len 25, all): " + p2);
        assertEquals(25, p2.length());

        // Cas 3 : Defauts
        PasswordGenerator gen3 = new PasswordGenerator.Builder().build();
        String p3 = gen3.generate();
        System.out.println("MDP 3 (defaut): " + p3);
        assertNotNull(p3);
    }

    @Test
    void testSessionManager() {
        SessionManager session = new SessionManager();
        assertNotNull(session);

        User user = new User("u@cpe.fr", "hash", "salt");
        SecretKeySpec key = new SecretKeySpec(new byte[32], "AES");

        session.login(user, key);
        assertTrue(session.isLoggedIn());
        assertEquals(user, session.getCurrentUser());
        assertEquals(key, session.getDecryptionKey());

        session.logout();
        assertFalse(session.isLoggedIn());
        assertNull(session.getCurrentUser());
    }
}
