package fr.cpe.ui;

import fr.cpe.model.*;
import fr.cpe.service.PasswordGenerator;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class AddVaultItemDialog extends Dialog<IVaultItem> {
    public AddVaultItemDialog(PasswordGenerator passwordGenerator, String styleSheet) {
        if (styleSheet != null && !styleSheet.isEmpty()) {
            getDialogPane().getStylesheets().add(styleSheet);
        }
        setTitle("Nouveau Vault Item");
        setHeaderText("Configuration de l'élément");

        ButtonType saveBtnType = new ButtonType("Ajouter", ButtonBar.ButtonData.OK_DONE);
        getDialogPane().getButtonTypes().addAll(saveBtnType, ButtonType.CANCEL);

        VBox content = new VBox(15);
        content.setPrefWidth(400);

        TextField nameF = new TextField();
        nameF.setPromptText("Nom (ex: Amazon)");
        TextField descF = new TextField();
        descF.setPromptText("Description (ex: Perso)");

        ComboBox<String> typeCombo = new ComboBox<>();
        typeCombo.getItems().addAll("Mot de passe", "Carte Bancaire", "Clé SSH");
        typeCombo.setValue("Mot de passe");

        VBox fieldsContainer = new VBox(10);

        VBox passInputs = new VBox(10);
        TextField userF = new TextField();
        userF.setPromptText("Identifiant");

        PasswordField passF = new PasswordField();
        passF.setPromptText("Mot de passe");
        TextField visiblePassF = new TextField();
        visiblePassF.setPromptText("Mot de passe");
        visiblePassF.setManaged(false);
        visiblePassF.setVisible(false);
        passF.textProperty().bindBidirectional(visiblePassF.textProperty());

        Button showPassBtn = new Button("👁");
        showPassBtn.setStyle("-fx-background-color: transparent; -fx-cursor: hand;");
        showPassBtn.setOnAction(e -> {
            if (passF.isVisible()) {
                passF.setVisible(false);
                passF.setManaged(false);
                visiblePassF.setVisible(true);
                visiblePassF.setManaged(true);
                showPassBtn.setText("🙈");
            } else {
                passF.setVisible(true);
                passF.setManaged(true);
                visiblePassF.setVisible(false);
                visiblePassF.setManaged(false);
                showPassBtn.setText("👁");
            }
        });

        HBox passFieldBox = new HBox(5, passF, visiblePassF, showPassBtn);
        HBox.setHgrow(passF, Priority.ALWAYS);
        HBox.setHgrow(visiblePassF, Priority.ALWAYS);

        VBox genSettings = new VBox(5);
        genSettings.setStyle("-fx-padding: 10; -fx-background-color: #1e1e2e; -fx-background-radius: 5;");
        Slider lenSlider = new Slider(4, 64, 16);
        lenSlider.setShowTickLabels(true);
        Label lenLabel = new Label("Longueur: 16");
        lenSlider.valueProperty().addListener((o, ov, nv) -> lenLabel.setText("Longueur: " + nv.intValue()));

        CheckBox upperCb = new CheckBox("A-Z");
        upperCb.setSelected(true);
        CheckBox digitsCb = new CheckBox("0-9");
        digitsCb.setSelected(true);
        CheckBox specialCb = new CheckBox("!@#");
        specialCb.setSelected(true);
        HBox cbBox = new HBox(10, upperCb, digitsCb, specialCb);

        Button genBtn = new Button("Générer via Builder");
        genBtn.getStyleClass().add("action-button");
        genBtn.setOnAction(e -> {
            PasswordGenerator gen = new PasswordGenerator.Builder()
                    .setLength((int) lenSlider.getValue())
                    .setUseUpper(upperCb.isSelected())
                    .setUseDigits(digitsCb.isSelected())
                    .setUseSpecial(specialCb.isSelected())
                    .build();
            passF.setText(gen.generate());
        });

        genSettings.getChildren().addAll(lenLabel, lenSlider, cbBox, genBtn);
        passInputs.getChildren().addAll(userF, passFieldBox, genSettings);

        VBox cardInputs = new VBox(10);
        TextField cardNumF = new TextField();
        cardNumF.setPromptText("Numéro");
        TextField expiryF = new TextField();
        expiryF.setPromptText("Expire (MM/YY)");
        TextField cvvF = new TextField();
        cvvF.setPromptText("CVV");
        cardInputs.getChildren().addAll(cardNumF, expiryF, cvvF);

        VBox sshInputs = new VBox(10);
        TextField pubKeyF = new TextField();
        pubKeyF.setPromptText("Clé Publique");
        TextArea privKeyF = new TextArea();
        privKeyF.setPromptText("Clé Privée");
        privKeyF.setPrefRowCount(3);
        sshInputs.getChildren().addAll(pubKeyF, privKeyF);

        fieldsContainer.getChildren().add(passInputs);

        typeCombo.setOnAction(e -> {
            fieldsContainer.getChildren().clear();
            int idx = typeCombo.getSelectionModel().getSelectedIndex();
            if (idx == 0)
                fieldsContainer.getChildren().add(passInputs);
            else if (idx == 1)
                fieldsContainer.getChildren().add(cardInputs);
            else
                fieldsContainer.getChildren().add(sshInputs);
        });

        content.getChildren().addAll(new Label("Catégorie :"), typeCombo, nameF, descF, new Separator(),
                fieldsContainer);
        getDialogPane().setContent(content);

        setResultConverter(btnType -> {
            if (btnType == saveBtnType) {
                int idx = typeCombo.getSelectionModel().getSelectedIndex();
                if (idx == 0)
                    return new PasswordItem(nameF.getText(), descF.getText(), userF.getText(), passF.getText());
                if (idx == 1)
                    return new CreditCardItem(nameF.getText(), descF.getText(), cardNumF.getText(), expiryF.getText(),
                            cvvF.getText());
                if (idx == 2)
                    return new SshKeyItem(nameF.getText(), descF.getText(), pubKeyF.getText(), privKeyF.getText());
            }
            return null;
        });
    }
}
