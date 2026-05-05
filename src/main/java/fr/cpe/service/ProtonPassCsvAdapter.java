package fr.cpe.service;

import fr.cpe.model.*;
import java.io.*;
import java.nio.charset.StandardCharsets;

public class ProtonPassCsvAdapter implements IVaultExportAdapter {
    @Override
    public void exportVault(Vault vault, File file) throws Exception {
        throw new UnsupportedOperationException("Export vers Proton Pass non supporté.");
    }

    @Override
    public Vault importVault(File file) throws Exception {
        Vault vault = new Vault(java.util.UUID.randomUUID());
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            reader.readLine(); // Skip header
            String line;
            while ((line = reader.readLine()) != null) {
                String[] cols = CsvUtils.parseCsvLine(line);
                if (cols.length < 7) continue;
                
                String type = cols[0];
                String name = cols[1];
                String url = cols[2];
                String email = cols[3];
                String username = cols[4];
                String password = cols[5];
                String note = cols[6];
                
                // Si l'identifiant est vide, on tente d'utiliser l'email
                String identifiant = (username == null || username.isEmpty()) ? email : username;
                
                // Si le mot de passe est vide mais présent dans la note
                if ((password == null || password.isEmpty()) && "login".equalsIgnoreCase(type) && !note.isEmpty()) {
                    password = note;
                }

                if ("card".equalsIgnoreCase(type)) {
                    vault.addItem(new CreditCardItem(name, note, "Imported", "Imported", "000"));
                } else if (!name.isEmpty() || !password.isEmpty()) {
                    vault.addItem(new PasswordItem(name, note + (url.isEmpty() ? "" : " (URL: " + url + ")"), identifiant, password));
                }
            }
        }
        return vault;
    }

    @Override
    public String getSupportedExtension() {
        return "protonpass.csv";
    }
}
