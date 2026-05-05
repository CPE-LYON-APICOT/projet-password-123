package fr.cpe.service;

import fr.cpe.model.Vault;
import java.io.File;

public interface IVaultExportAdapter {
    /**
     * Exporte un coffre vers un fichier.
     */
    void exportVault(Vault vault, File file) throws Exception;

    /**
     * Importe un coffre depuis un fichier.
     */
    Vault importVault(File file) throws Exception;

    /**
     * Retourne l'extension supportée (ex: "csv", "json").
     */
    String getSupportedExtension();
}
