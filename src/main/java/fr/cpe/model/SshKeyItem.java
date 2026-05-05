package fr.cpe.model;

public class SshKeyItem implements IVaultItem {
    private String name;
    private String description;
    private String publicKey;
    private String privateKey;

    public SshKeyItem() {}

    public SshKeyItem(String name, String description, String publicKey, String privateKey) {
        this.name = name;
        this.description = description;
        this.publicKey = publicKey;
        this.privateKey = privateKey;
    }

    @Override
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    @Override
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getPublicKey() { return publicKey; }
    public void setPublicKey(String publicKey) { this.publicKey = publicKey; }

    public String getPrivateKey() { return privateKey; }
    public void setPrivateKey(String privateKey) { this.privateKey = privateKey; }
}
