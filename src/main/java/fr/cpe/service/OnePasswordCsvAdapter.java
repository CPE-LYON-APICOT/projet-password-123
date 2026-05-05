package fr.cpe.service;

import fr.cpe.model.*;
import java.io.*;
import java.nio.charset.StandardCharsets;

public class OnePasswordCsvAdapter implements IVaultExportAdapter {
    @Override
    public void exportVault(Vault vault, File file) throws Exception {
        throw new UnsupportedOperationException("Export vers 1Password non supporté.");
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
                if (cols.length < 4) continue;
                
                String name = cols[0];
                String website = cols[1];
                String user = cols[2];
                String pass = cols[3];
                String note = (cols.length > 4) ? cols[4] : "";
                
                if (website != null && !website.isEmpty()) {
                    note = note + " (Site: " + website + ")";
                }
                
                vault.addItem(new PasswordItem(name, note, user, pass));
            }
        }
        return vault;
    }

    @Override
    public String getSupportedExtension() {
        return "1password.csv";
    }
}
