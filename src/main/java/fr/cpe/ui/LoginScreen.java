package fr.cpe.ui;

import fr.cpe.model.User;
import fr.cpe.service.JsonPersistenceService;
import fr.cpe.service.SessionManager;
import fr.cpe.utils.SecurityUtils;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import javax.crypto.spec.SecretKeySpec;
import java.util.List;

public class LoginScreen extends VBox {
    private final JsonPersistenceService persistenceService;
    private final SessionManager sessionManager;
    private final Runnable onLoginSuccess;

    public LoginScreen(JsonPersistenceService persistenceService, SessionManager sessionManager,
            Runnable onLoginSuccess) {
        this.persistenceService = persistenceService;
        this.sessionManager = sessionManager;
        this.onLoginSuccess = onLoginSuccess;

        setAlignment(Pos.CENTER);
        getStyleClass().add("login-container");

        Label title = new Label("Password@123");
        title.getStyleClass().add("login-title");

        TextField emailField = new TextField();
        emailField.setPromptText("Email");
        emailField.getStyleClass().add("form-field");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Master Password");
        passwordField.getStyleClass().add("form-field");

        Label strategyLabel = new Label("Stratégie de chiffrement (Nouveau compte) :");
        strategyLabel.setStyle("-fx-text-fill: #6c7086; -fx-font-size: 12;");

        ComboBox<String> strategyCombo = new ComboBox<>();
        strategyCombo.getItems().addAll("AES-256", "Blowfish");
        strategyCombo.setValue("AES-256");
        strategyCombo.getStyleClass().add("combo-box");

        Button loginButton = new Button("Accès au Coffre");
        loginButton.getStyleClass().add("action-button");
        loginButton
                .setOnAction(e -> handleLogin(emailField.getText(), passwordField.getText(), strategyCombo.getValue()));

        getChildren().addAll(title, emailField, passwordField, strategyLabel, strategyCombo, loginButton);
    }

    private void handleLogin(String email, String password, String strategyName) {
        if (email.isEmpty() || password.isEmpty())
            return;

        String passwordHash = SecurityUtils.hashSHA512(password);
        List<User> users = persistenceService.loadUsers();
        User user = users.stream().filter(u -> u.getEmail().equals(email)).findFirst().orElse(null);

        if (user == null) {
            user = new User(email, passwordHash, "salt_unused", strategyName);
            persistenceService.saveUser(user);
        } else if (!user.getMasterPasswordHash().equals(passwordHash)) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Mot de passe erroné");
            alert.show();
            return;
        }

        byte[] key = new byte[16];
        System.arraycopy(passwordHash.getBytes(), 0, key, 0, 16);

        SecretKeySpec secretKey = new SecretKeySpec(key, "AES");
        sessionManager.login(user, secretKey);

        onLoginSuccess.run();
    }
}
