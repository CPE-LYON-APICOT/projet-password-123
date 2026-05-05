package fr.cpe.model;

public class PasswordItem implements IVaultItem {
    private String name;
    private String description;
    private String username;
    private String password;

    public PasswordItem() {}

    public PasswordItem(String name, String description, String username, String password) {
        this.name = name;
        this.description = description;
        this.username = username;
        this.password = password;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
