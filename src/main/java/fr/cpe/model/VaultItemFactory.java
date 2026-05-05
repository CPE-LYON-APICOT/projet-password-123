package fr.cpe.model;

import java.util.Map;

public class VaultItemFactory {
    // Méthodes de création d'items spécifiques
    public static IVaultItem createPassword(String name, String desc, String user, String pass) {
        return new PasswordItem(name, desc, user, pass);
    }

    public static IVaultItem createCreditCard(String name, String desc, String num, String date, String cvv) {
        return new CreditCardItem(name, desc, num, date, cvv);
    }
    
    // Méthode générique pour créer un item à partir d'un type et d'une map de données
    public static IVaultItem createItem(String type, Map<String, String> data) {
        String name = data.get("name");
        String desc = data.get("description");

        return switch (type.toUpperCase()) {
            case "PASSWORD" -> new PasswordItem(name, desc, data.get("username"), data.get("password"));
            case "CARD" -> new CreditCardItem(name, desc, data.get("number"), data.get("expiry"), data.get("cvv"));
            default -> throw new IllegalArgumentException("Type d'item inconnu : " + type);
        };
    }
}