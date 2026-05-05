package fr.cpe.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.cpe.model.Vault;
import java.io.File;

/**
 * Adapter JSON natif pour l'export/import.
 */
public class JsonVaultAdapter implements IVaultExportAdapter {

    private final ObjectMapper mapper;

    public JsonVaultAdapter(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public void exportVault(Vault vault, File file) throws Exception {
        mapper.writeValue(file, vault);
    }

    @Override
    public Vault importVault(File file) throws Exception {
        return mapper.readValue(file, Vault.class);
    }

    @Override
    public String getSupportedExtension() {
        return "json";
    }
}
