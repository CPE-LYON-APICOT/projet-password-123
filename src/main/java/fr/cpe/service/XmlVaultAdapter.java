package fr.cpe.service;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import fr.cpe.model.Vault;
import java.io.File;

/**
 * Adapter XML pour l'export/import.
 */
public class XmlVaultAdapter implements IVaultExportAdapter {

    private final XmlMapper mapper;

    public XmlVaultAdapter() {
        this.mapper = new XmlMapper();
        this.mapper.registerModule(new JavaTimeModule());
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
        return "xml";
    }
}
