package fr.cpe.service;

import fr.cpe.model.*;
import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * Adapter CSV au format standard compatible avec Bitwarden (simplifié).
 */
public class CsvVaultAdapter implements IVaultExportAdapter {

    private static final String CSV_HEADER = "folder,favorite,type,name,notes,fields,reprompt,archivedDate,login_uri,login_username,login_password,login_totp";

    @Override
    public void exportVault(Vault vault, File file) throws Exception {
        try (PrintWriter writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))) {
            writer.println(CSV_HEADER);
            for (IVaultItem item : vault.getItems()) {
                String line = formatCsvLine(item);
                writer.println(line);
            }
        }
    }

    private String formatCsvLine(IVaultItem item) {
        String type = "login";
        String user = "";
        String pass = "";
        String notes = escapeCsv(item.getDescription());
        String name = escapeCsv(item.getName());

        if (item instanceof PasswordItem p) {
            user = escapeCsv(p.getUsername());
            pass = escapeCsv(p.getPassword());
        } else if (item instanceof CreditCardItem c) {
            type = "card";
            notes += " | Card: " + c.getCardNumber() + " | Exp: " + c.getExpiryDate() + " | CVV: " + c.getCvv();
        } else if (item instanceof SshKeyItem s) {
            type = "ssh";
            notes += " | PubKey: " + s.getPublicKey() + " | PrivKey: " + s.getPrivateKey();
        }

        // folder,favorite,type,name,notes,fields,reprompt,archivedDate,login_uri,login_username,login_password,login_totp
        return String.format(",false,%s,%s,%s,,,, ,%s,%s,", type, name, notes, user, pass);
    }

    @Override
    public Vault importVault(File file) throws Exception {
        Vault vault = new Vault(java.util.UUID.randomUUID());
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            reader.readLine(); // skip header
            String line;
            while ((line = reader.readLine()) != null) {
                String[] cols = CsvUtils.parseCsvLine(line);
                if (cols.length < 11) continue;

                String type = cols[2];
                String name = cols[3];
                String notes = cols[4];
                String url = (cols.length > 8) ? cols[8] : "";
                String user = (cols.length > 9) ? cols[9] : "";
                String pass = (cols.length > 10) ? cols[10] : "";

                if (type.equalsIgnoreCase("card")) {
                    vault.addItem(new CreditCardItem(name, notes, "Imported", "Imported", "000"));
                } else if (type.equalsIgnoreCase("ssh")) {
                    vault.addItem(new SshKeyItem(name, notes, "Imported", "Imported"));
                } else {
                    vault.addItem(new PasswordItem(name, notes + (url.isEmpty() ? "" : " (URL: " + url + ")"), user, pass));
                }
            }
        }
        return vault;
    }

    @Override
    public String getSupportedExtension() {
        return "csv";
    }

    private String escapeCsv(String s) {
        if (s == null) return "";
        return s.replace(",", " "); // Simplifié pour éviter les guillemets complexes
    }
}
