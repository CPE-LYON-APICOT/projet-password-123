package fr.cpe.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.cpe.model.Vault;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class VaultExportService {
    private final Map<String, IVaultExportAdapter> adapters = new HashMap<>();

    public VaultExportService(ObjectMapper mapper) {
        registerAdapter(new CsvVaultAdapter());
        registerAdapter(new JsonVaultAdapter(mapper));
        registerAdapter(new XmlVaultAdapter());
    }

    public void registerAdapter(IVaultExportAdapter adapter) {
        adapters.put(adapter.getSupportedExtension().toLowerCase(), adapter);
    }

    public void exportVault(Vault vault, File file) throws Exception {
        String ext = getExtension(file.getName());
        IVaultExportAdapter adapter = adapters.get(ext);
        if (adapter == null) throw new IllegalArgumentException("Format non supporté: " + ext);
        adapter.exportVault(vault, file);
    }

    public Vault importVault(File file) throws Exception {
        String ext = getExtension(file.getName());
        if ("csv".equals(ext)) return detectAndImportCsv(file);
        
        IVaultExportAdapter adapter = adapters.get(ext);
        if (adapter == null) throw new IllegalArgumentException("Format non supporté: " + ext);
        return adapter.importVault(file);
    }

    public Vault importVault(File file, String strategy) throws Exception {
        IVaultExportAdapter adapter = null;
        switch (strategy.toLowerCase()) {
            case "nordpass" -> adapter = new NordPassCsvAdapter();
            case "protonpass" -> adapter = new ProtonPassCsvAdapter();
            case "1password" -> adapter = new OnePasswordCsvAdapter();
            default -> adapter = new CsvVaultAdapter();
        }
        return adapter.importVault(file);
    }

    private Vault detectAndImportCsv(File file) throws Exception {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            String header = reader.readLine();
            if (header == null) throw new IOException("Fichier CSV vide");

            IVaultExportAdapter selected = new CsvVaultAdapter(); // Default

            if (header.contains("cardholdername")) {
                selected = new NordPassCsvAdapter();
            } else if (header.contains("totp") && header.contains("vault")) {
                selected = new ProtonPassCsvAdapter();
            } else if (header.startsWith("title") && header.contains("website")) {
                selected = new OnePasswordCsvAdapter();
            } else if (header.contains("login_password")) {
                selected = new CsvVaultAdapter(); // Notre format Bitwarden-style
            }
            
            return selected.importVault(file);
        }
    }

    private String getExtension(String fileName) {
        int idx = fileName.lastIndexOf('.');
        if (idx == -1) return "";
        return fileName.substring(idx + 1).toLowerCase();
    }
}
