package fr.cpe.ui;

import fr.cpe.model.*;
import fr.cpe.service.PasswordGenerator;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.*;
import java.util.function.Consumer;

public class ItemDetailPanel extends VBox {
    
    private final Consumer<IVaultItem> onSave;
    private final Consumer<IVaultItem> onDelete;
    private final Consumer<IVaultItem> onCreate;

    public ItemDetailPanel(Consumer<IVaultItem> onSave, Consumer<IVaultItem> onDelete, Consumer<IVaultItem> onCreate) {
        this.onSave = onSave;
        this.onDelete = onDelete;
        this.onCreate = onCreate;
        getStyleClass().add("item-card");
        setPrefWidth(350);
        setAlignment(Pos.TOP_LEFT);
        setPadding(new Insets(20));
        setSpacing(20);
        showEmpty();
    }

    public void showEmpty() {
        getChildren().clear();
        Label msg = new Label("Sélectionnez un élément pour voir les détails");
        msg.getStyleClass().add("item-desc");
        getChildren().add(msg);
    }

    public void showDetails(IVaultItem item) {
        getChildren().clear();
        
        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER_LEFT);
        
        Label title = new Label(item.getName());
        title.getStyleClass().add("item-title");
        title.setStyle("-fx-font-size: 20;");
        HBox.setHgrow(title, Priority.ALWAYS);
        
        Button editBtn = new Button("✏");
        editBtn.setStyle("-fx-text-fill: #f9e2af; -fx-background-color: transparent; -fx-cursor: hand;");
        editBtn.setOnAction(e -> showEdit(item));

        Button deleteBtn = new Button("🗑");
        deleteBtn.setStyle("-fx-text-fill: #f38ba8; -fx-background-color: transparent; -fx-cursor: hand;");
        deleteBtn.setOnAction(e -> onDelete.accept(item));
        
        header.getChildren().addAll(title, editBtn, deleteBtn);
        
        Label desc = new Label(item.getDescription());
        desc.getStyleClass().add("item-desc");
        
        VBox fields = new VBox(15);
        fields.setPadding(new Insets(10, 0, 0, 0));

        if (item instanceof PasswordItem p) {
            addField(fields, "Identifiant", p.getUsername());
            addPasswordField(fields, "Mot de passe", p.getPassword());
        } else if (item instanceof CreditCardItem c) {
            addField(fields, "Numéro de carte", c.getCardNumber());
            addField(fields, "Expiration", c.getExpiryDate());
            addField(fields, "CVV", c.getCvv());
        } else if (item instanceof SshKeyItem s) {
            addField(fields, "Clé Publique", s.getPublicKey());
            addPasswordField(fields, "Clé Privée", s.getPrivateKey());
        }

        getChildren().addAll(header, desc, new Separator(), fields);
    }

    private void showEdit(IVaultItem item) {
        getChildren().clear();
        Label title = new Label("Modifier : " + item.getName());
        title.getStyleClass().add("item-title");

        VBox fields = new VBox(10);
        TextField nameF = new TextField(item.getName()); nameF.getStyleClass().add("search-bar");
        TextField descF = new TextField(item.getDescription()); descF.getStyleClass().add("search-bar");
        
        fields.getChildren().addAll(new Label("Nom"), nameF, new Label("Description"), descF);

        if (item instanceof PasswordItem p) {
            TextField userF = new TextField(p.getUsername()); userF.getStyleClass().add("search-bar");
            TextField passF = new TextField(p.getPassword()); passF.getStyleClass().add("search-bar");
            fields.getChildren().addAll(new Label("Utilisateur"), userF, new Label("Mot de passe"), passF);
            
            Button saveBtn = createSaveButton(e -> {
                p.setName(nameF.getText());
                p.setDescription(descF.getText());
                p.setUsername(userF.getText());
                p.setPassword(passF.getText());
                onSave.accept(item);
            });
            fields.getChildren().add(saveBtn);
        } else if (item instanceof CreditCardItem c) {
            TextField cardF = new TextField(c.getCardNumber()); cardF.getStyleClass().add("search-bar");
            TextField expF = new TextField(c.getExpiryDate()); expF.getStyleClass().add("search-bar");
            TextField cvvF = new TextField(c.getCvv()); cvvF.getStyleClass().add("search-bar");
            fields.getChildren().addAll(new Label("Numéro"), cardF, new Label("Expiration"), expF, new Label("CVV"), cvvF);
            
            Button saveBtn = createSaveButton(e -> {
                c.setName(nameF.getText());
                c.setDescription(descF.getText());
                c.setCardNumber(cardF.getText());
                c.setExpiryDate(expF.getText());
                c.setCvv(cvvF.getText());
                onSave.accept(item);
            });
            fields.getChildren().add(saveBtn);
        } else if (item instanceof SshKeyItem s) {
            TextField pubF = new TextField(s.getPublicKey()); pubF.getStyleClass().add("search-bar");
            TextArea privF = new TextArea(s.getPrivateKey()); privF.getStyleClass().add("search-bar");
            fields.getChildren().addAll(new Label("Clé Publique"), pubF, new Label("Clé Privée"), privF);
            
            Button saveBtn = createSaveButton(e -> {
                s.setName(nameF.getText());
                s.setDescription(descF.getText());
                s.setPublicKey(pubF.getText());
                s.setPrivateKey(privF.getText());
                onSave.accept(item);
            });
            fields.getChildren().add(saveBtn);
        }

        Button cancelBtn = new Button("Annuler");
        cancelBtn.getStyleClass().add("sidebar-button");
        cancelBtn.setOnAction(e -> showDetails(item));
        fields.getChildren().add(cancelBtn);

        getChildren().addAll(title, new Separator(), fields);
    }

    public void showCreate() {
        getChildren().clear();
        Label title = new Label("Nouveau Secret");
        title.getStyleClass().add("item-title");

        ComboBox<String> typeCombo = new ComboBox<>();
        typeCombo.getItems().addAll("Mot de passe", "Carte Bancaire", "Clé SSH");
        typeCombo.setValue("Mot de passe");
        typeCombo.getStyleClass().add("search-bar");
        typeCombo.setMaxWidth(Double.MAX_VALUE);

        VBox fieldsContainer = new VBox(15);
        TextField nameF = new TextField(); nameF.setPromptText("Nom (ex: Amazon)"); nameF.getStyleClass().add("search-bar");
        TextField descF = new TextField(); descF.setPromptText("Description (ex: Perso)"); descF.getStyleClass().add("search-bar");
        
        VBox dynamicFields = new VBox(10);
        
        // --- PASSWORDS ---
        VBox passInputs = new VBox(10);
        TextField userF = new TextField(); userF.setPromptText("Identifiant"); userF.getStyleClass().add("search-bar");
        PasswordField passF = new PasswordField(); passF.setPromptText("Mot de passe"); passF.getStyleClass().add("search-bar");
        
        VBox genSettings = new VBox(5);
        genSettings.setStyle("-fx-padding: 10; -fx-background-color: #181825; -fx-background-radius: 5;");
        Slider lenSlider = new Slider(4, 64, 16);
        Label lenLabel = new Label("Longueur: 16");
        lenLabel.setStyle("-fx-text-fill: #cdd6f4;");
        lenSlider.valueProperty().addListener((o, ov, nv) -> lenLabel.setText("Longueur: " + nv.intValue()));
        
        CheckBox upperCb = new CheckBox("A-Z"); upperCb.setSelected(true);
        CheckBox digitsCb = new CheckBox("0-9"); digitsCb.setSelected(true);
        CheckBox specialCb = new CheckBox("!@#"); specialCb.setSelected(true);
        HBox cbBox = new HBox(10, upperCb, digitsCb, specialCb);
        
        Button genBtn = new Button("Générer");
        genBtn.getStyleClass().add("sidebar-button");
        genBtn.setOnAction(e -> {
            PasswordGenerator gen = new PasswordGenerator.Builder()
                    .setLength((int)lenSlider.getValue())
                    .setUseUpper(upperCb.isSelected())
                    .setUseDigits(digitsCb.isSelected())
                    .setUseSpecial(specialCb.isSelected())
                    .build();
            passF.setText(gen.generate());
        });
        genSettings.getChildren().addAll(lenLabel, lenSlider, cbBox, genBtn);
        passInputs.getChildren().addAll(userF, passF, genSettings);

        // --- CARDS ---
        VBox cardInputs = new VBox(10);
        TextField cardNumF = new TextField(); cardNumF.setPromptText("Numéro"); cardNumF.getStyleClass().add("search-bar");
        TextField expiryF = new TextField(); expiryF.setPromptText("Expire (MM/YY)"); expiryF.getStyleClass().add("search-bar");
        TextField cvvF = new TextField(); cvvF.setPromptText("CVV"); cvvF.getStyleClass().add("search-bar");
        cardInputs.getChildren().addAll(cardNumF, expiryF, cvvF);

        // --- SSH ---
        VBox sshInputs = new VBox(10);
        TextField pubKeyF = new TextField(); pubKeyF.setPromptText("Clé Publique"); pubKeyF.getStyleClass().add("search-bar");
        TextArea privKeyF = new TextArea(); privKeyF.setPromptText("Clé Privée"); privKeyF.getStyleClass().add("search-bar");
        privKeyF.setPrefRowCount(3);
        sshInputs.getChildren().addAll(pubKeyF, privKeyF);

        dynamicFields.getChildren().add(passInputs);

        typeCombo.setOnAction(e -> {
            dynamicFields.getChildren().clear();
            int idx = typeCombo.getSelectionModel().getSelectedIndex();
            if (idx == 0) dynamicFields.getChildren().add(passInputs);
            else if (idx == 1) dynamicFields.getChildren().add(cardInputs);
            else dynamicFields.getChildren().add(sshInputs);
        });

        Button createBtn = new Button("Créer l'élément");
        createBtn.getStyleClass().add("action-button");
        createBtn.setMaxWidth(Double.MAX_VALUE);
        createBtn.setOnAction(e -> {
            IVaultItem newItem = null;
            int idx = typeCombo.getSelectionModel().getSelectedIndex();
            if (idx == 0) newItem = new PasswordItem(nameF.getText(), descF.getText(), userF.getText(), passF.getText());
            else if (idx == 1) newItem = new CreditCardItem(nameF.getText(), descF.getText(), cardNumF.getText(), expiryF.getText(), cvvF.getText());
            else if (idx == 2) newItem = new SshKeyItem(nameF.getText(), descF.getText(), pubKeyF.getText(), privKeyF.getText());
            
            if (newItem != null) {
                onCreate.accept(newItem);
            }
        });

        Button cancelBtn = new Button("Annuler");
        cancelBtn.getStyleClass().add("sidebar-button");
        cancelBtn.setOnAction(e -> showEmpty());

        fieldsContainer.getChildren().addAll(new Label("Type de secret"), typeCombo, new Label("Nom"), nameF, new Label("Description"), descF, new Separator(), dynamicFields, createBtn, cancelBtn);
        getChildren().addAll(title, new Separator(), fieldsContainer);
    }

    private Button createSaveButton(javafx.event.EventHandler<javafx.event.ActionEvent> action) {
        Button saveBtn = new Button("Sauvegarder");
        saveBtn.getStyleClass().add("action-button");
        saveBtn.setOnAction(action);
        return saveBtn;
    }

    private void addField(VBox container, String labelStr, String value) {
        VBox box = new VBox(5);
        Label label = new Label(labelStr);
        label.setStyle("-fx-text-fill: #6c7086; -fx-font-size: 11;");
        
        HBox row = new HBox(10);
        TextField field = new TextField(value);
        field.setEditable(false);
        field.getStyleClass().add("search-bar");
        field.setStyle("-fx-background-color: #1e1e2e;");
        HBox.setHgrow(field, Priority.ALWAYS);
        
        Button copyBtn = new Button("📋");
        copyBtn.getStyleClass().add("sidebar-button");
        copyBtn.setOnAction(e -> copyToClipboard(value));
        
        row.getChildren().addAll(field, copyBtn);
        box.getChildren().addAll(label, row);
        container.getChildren().add(box);
    }

    private void addPasswordField(VBox container, String labelStr, String value) {
        VBox box = new VBox(5);
        Label label = new Label(labelStr);
        label.setStyle("-fx-text-fill: #6c7086; -fx-font-size: 11;");
        
        HBox row = new HBox(10);
        PasswordField field = new PasswordField();
        field.setText(value);
        field.setEditable(false);
        field.getStyleClass().add("search-bar");
        field.setStyle("-fx-background-color: #1e1e2e;");
        HBox.setHgrow(field, Priority.ALWAYS);
        
        Button showBtn = new Button("👁");
        showBtn.getStyleClass().add("sidebar-button");
        
        TextField visibleField = new TextField(value);
        visibleField.setEditable(false);
        visibleField.getStyleClass().add("search-bar");
        visibleField.setStyle("-fx-background-color: #1e1e2e;");
        HBox.setHgrow(visibleField, Priority.ALWAYS);
        
        showBtn.setOnAction(e -> {
            if (row.getChildren().contains(field)) {
                row.getChildren().set(0, visibleField);
                showBtn.setText("🙈");
            } else {
                row.getChildren().set(0, field);
                showBtn.setText("👁");
            }
        });

        Button copyBtn = new Button("📋");
        copyBtn.getStyleClass().add("sidebar-button");
        copyBtn.setOnAction(e -> copyToClipboard(value));
        
        row.getChildren().addAll(field, showBtn, copyBtn);
        box.getChildren().addAll(label, row);
        container.getChildren().add(box);
    }

    private void copyToClipboard(String text) {
        ClipboardContent content = new ClipboardContent();
        content.putString(text);
        Clipboard.getSystemClipboard().setContent(content);
    }
}
