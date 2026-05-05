package fr.cpe.service;

import fr.cpe.model.*;
import java.io.*;
import java.nio.charset.StandardCharsets;

public class NordPassCsvAdapter implements IVaultExportAdapter {
    @Override
    public void exportVault(Vault vault, File file) throws Exception {
        throw new UnsupportedOperationException("Export vers NordPass non supporté.");
    }

    @Override
    public Vault importVault(File file) throws Exception {
        Vault vault = new Vault(java.util.UUID.randomUUID());
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            String header = reader.readLine();
            if (header == null) return vault;
            
            String line;
            while ((line = reader.readLine()) != null) {
                String[] cols = CsvUtils.parseCsvLine(line);
                if (cols.length < 5) continue; // Minimum pour un login basique
                
                String name = cols[0];
                String url = (cols.length > 1) ? cols[1] : "";
                String username = (cols.length > 3) ? cols[3] : "";
                String password = (cols.length > 4) ? cols[4] : "";
                String note = (cols.length > 5) ? cols[5] : "";
                String email = (cols.length > 16) ? cols[16] : "";
                String type = (cols.length > 22) ? cols[22] : "login";
                
                // Si l'identifiant est vide, on tente d'utiliser l'email
                String identifiant = (username == null || username.isEmpty()) ? email : username;

                if ("card".equalsIgnoreCase(type) || (cols.length > 7 && !cols[7].isEmpty())) {
                    String cardNumber = (cols.length > 7) ? cols[7] : "";
                    String exp = (cols.length > 10) ? cols[10] : "";
                    String cvc = (cols.length > 8) ? cols[8] : "";
                    vault.addItem(new CreditCardItem(name, note, cardNumber, exp, cvc));
                } else if (!name.isEmpty() || !password.isEmpty()) {
                    vault.addItem(new PasswordItem(name, note + (url.isEmpty() ? "" : " (URL: " + url + ")"), identifiant, password));
                }
            }
        }
        return vault;
    }

    @Override
    public String getSupportedExtension() {
        return "nordpass.csv";
    }
}
