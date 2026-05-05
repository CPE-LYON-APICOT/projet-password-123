package fr.cpe.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Vault {
    private UUID ownerId;
    private List<IVaultItem> items = new ArrayList<>();

    public Vault() {}

    public Vault(UUID ownerId) { 
        this.ownerId = ownerId; 
    }

    public void addItem(IVaultItem item) { 
        items.add(item);
    }

    public List<IVaultItem> getItems() {
        return items;
    }
    
    public void setItems(List<IVaultItem> items) {
        this.items = items;
    }

    public UUID getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(UUID ownerId) {
        this.ownerId = ownerId;
    }

    public void loadAndDecryptAll(Byte[] encryptedData) {
        
    }
}
