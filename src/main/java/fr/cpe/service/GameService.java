package fr.cpe.service;

import com.google.inject.Inject;
import fr.cpe.model.*;
import fr.cpe.ui.*;
import fr.cpe.ui.ImportChoiceDialog;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GameService {

    private final JsonPersistenceService persistenceService;
    private final SessionManager sessionManager;
    private final VaultExportService exportService;

    private Pane rootPane;
    private VBox itemListContainer;
    private ItemDetailPanel detailPanel;
    private TextField searchField;
    private List<IVaultItem> currentVaultItems = new ArrayList<>();
    private String currentCategory = "ALL";

    @Inject
    public GameService(JsonPersistenceService persistenceService, 
                       SessionManager sessionManager) {
        this.persistenceService = persistenceService;
        this.sessionManager = sessionManager;
        this.exportService = new VaultExportService(persistenceService.getMapper());
    }

    public void init(Pane gamePane) {
        this.rootPane = gamePane;
        var resource = getClass().getResource("/style.css");
        if (resource != null) {
            gamePane.getStylesheets().add(resource.toExternalForm());
        }
        
        updateView();
    }

    private void updateView() {
        if (!sessionManager.isLoggedIn()) {
            LoginScreen loginScreen = new LoginScreen(persistenceService, sessionManager, this::updateView);
            loginScreen.prefWidthProperty().bind(rootPane.widthProperty());
            loginScreen.prefHeightProperty().bind(rootPane.heightProperty());
            rootPane.getChildren().setAll(loginScreen);
        } else {
            showVaultView();
        }
    }

    private void showVaultView() {
        HBox layout = new HBox();
        layout.prefWidthProperty().bind(rootPane.widthProperty());
        layout.prefHeightProperty().bind(rootPane.heightProperty());

        VBox sidebar = new VBox(15);
        sidebar.getStyleClass().add("sidebar");
        sidebar.setPrefWidth(220);

        Label logo = new Label("SecureVault");
        logo.setStyle("-fx-text-fill: #89b4fa; -fx-font-size: 20; -fx-font-weight: bold; -fx-padding: 0 0 20 0;");

        Button allItemsBtn = new Button("Tous les items");
        allItemsBtn.getStyleClass().addAll("sidebar-button", "sidebar-button-active");
        
        Button passwordsBtn = new Button("Mots de passe");
        passwordsBtn.getStyleClass().add("sidebar-button");
        
        Button cardsBtn = new Button("Cartes bancaires");
        cardsBtn.getStyleClass().add("sidebar-button");

        allItemsBtn.setOnAction(e -> setCategory("ALL", allItemsBtn, passwordsBtn, cardsBtn));
        passwordsBtn.setOnAction(e -> setCategory("PASSWORDS", passwordsBtn, allItemsBtn, cardsBtn));
        cardsBtn.setOnAction(e -> setCategory("CARDS", cardsBtn, allItemsBtn, passwordsBtn));
 
        Separator sep = new Separator();
        sep.setPadding(new Insets(10, 0, 10, 0));
 
        Button importBtn = new Button("📥 Importer");
        importBtn.getStyleClass().add("sidebar-button");
        importBtn.setOnAction(e -> handleImport());
 
        Button exportBtn = new Button("📤 Exporter");
        exportBtn.getStyleClass().add("sidebar-button");
        exportBtn.setOnAction(e -> handleExport());

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        Button logoutBtn = new Button("Déconnexion");
        logoutBtn.getStyleClass().add("sidebar-button");
        logoutBtn.setOnAction(e -> {
            sessionManager.logout();
            updateView();
        });

        sidebar.getChildren().addAll(logo, allItemsBtn, passwordsBtn, cardsBtn, sep, importBtn, exportBtn, spacer, logoutBtn);

        VBox mainContent = new VBox(20);
        mainContent.setPadding(new Insets(30));
        HBox.setHgrow(mainContent, Priority.ALWAYS);

        HBox header = new HBox(20);
        header.setAlignment(Pos.CENTER_LEFT);

        searchField = new TextField();
        searchField.setPromptText("Recherche sécurisée...");
        searchField.getStyleClass().add("search-bar");
        HBox.setHgrow(searchField, Priority.ALWAYS);
        searchField.textProperty().addListener((obs, oldV, newV) -> filterItems(newV));

        Button addBtn = new Button("+ Ajouter");
        addBtn.getStyleClass().add("action-button");
        addBtn.setOnAction(e -> detailPanel.showCreate());

        header.getChildren().addAll(searchField, addBtn);

        HBox contentSplit = new HBox(20);
        VBox.setVgrow(contentSplit, Priority.ALWAYS);

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        HBox.setHgrow(scrollPane, Priority.ALWAYS);
        scrollPane.setPrefWidth(400);
        
        itemListContainer = new VBox(15);
        scrollPane.setContent(itemListContainer);

        detailPanel = new ItemDetailPanel(this::handleSave, this::handleDelete, this::handleCreate);

        contentSplit.getChildren().addAll(scrollPane, detailPanel);
        mainContent.getChildren().addAll(header, contentSplit);

        layout.getChildren().addAll(sidebar, mainContent);
        rootPane.getChildren().setAll(layout);

        refreshVault();
    }

    private void setCategory(String category, Button active, Button... others) {
        this.currentCategory = category;
        active.getStyleClass().add("sidebar-button-active");
        for (Button b : others) b.getStyleClass().remove("sidebar-button-active");
        filterItems(searchField.getText());
    }

    private void refreshVault() {
        Vault vault = persistenceService.loadVault(sessionManager.getCurrentUser().getId(), sessionManager.getDecryptionKey().getEncoded());
        currentVaultItems = vault.getItems();
        filterItems(searchField.getText());
    }

    private void filterItems(String query) {
        List<IVaultItem> filtered = currentVaultItems.stream()
                .filter(i -> {
                    if (currentCategory.equals("PASSWORDS")) return i instanceof PasswordItem;
                    if (currentCategory.equals("CARDS")) return i instanceof CreditCardItem;
                    return true;
                })
                .filter(i -> query == null || query.isEmpty() || 
                             i.getName().toLowerCase().contains(query.toLowerCase()) || 
                             i.getDescription().toLowerCase().contains(query.toLowerCase()))
                .collect(Collectors.toList());
        
        itemListContainer.getChildren().clear();
        for (IVaultItem item : filtered) {
            VBox card = new VBox(5);
            card.getStyleClass().add("item-card");
            card.setCursor(javafx.scene.Cursor.HAND);
            card.setOnMouseClicked(e -> detailPanel.showDetails(item));

            Label title = new Label(item.getName());
            title.getStyleClass().add("item-title");

            Label desc = new Label(item.getDescription());
            desc.getStyleClass().add("item-desc");

            card.getChildren().addAll(title, desc);
            itemListContainer.getChildren().add(card);
        }
    }

    private void handleImport() {
        var res = getClass().getResource("/style.css");
        String styleSheet = res != null ? res.toExternalForm() : "";
        
        ImportChoiceDialog choiceDialog = new ImportChoiceDialog(styleSheet);
        choiceDialog.showAndWait().ifPresent(strategy -> {
            javafx.stage.FileChooser fc = new javafx.stage.FileChooser();
            fc.setTitle("Importer un coffre (" + strategy + ")");
            fc.getExtensionFilters().add(new javafx.stage.FileChooser.ExtensionFilter("CSV Files", "*.csv"));
            
            File file = fc.showOpenDialog(rootPane.getScene().getWindow());
            if (file != null) {
                try {
                    Vault imported = exportService.importVault(file, strategy);
                    Vault current = persistenceService.loadVault(sessionManager.getCurrentUser().getId(), sessionManager.getDecryptionKey().getEncoded());
                    current.getItems().addAll(imported.getItems());
                    persistenceService.saveVault(current, sessionManager.getDecryptionKey().getEncoded());
                    refreshVault();
                    new Alert(Alert.AlertType.INFORMATION, "Importation réussie !").show();
                } catch (Exception ex) {
                    new Alert(Alert.AlertType.ERROR, "Erreur d'importation: " + ex.getMessage()).show();
                }
            }
        });
    }
 
    private void handleExport() {
        javafx.stage.FileChooser fc = new javafx.stage.FileChooser();
        fc.setTitle("Exporter le coffre");
        fc.getExtensionFilters().addAll(
            new javafx.stage.FileChooser.ExtensionFilter("CSV Files", "*.csv"),
            new javafx.stage.FileChooser.ExtensionFilter("JSON Files", "*.json"),
            new javafx.stage.FileChooser.ExtensionFilter("XML Files", "*.xml")
        );
        File file = fc.showSaveDialog(rootPane.getScene().getWindow());
        if (file != null) {
            try {
                Vault current = persistenceService.loadVault(sessionManager.getCurrentUser().getId(), sessionManager.getDecryptionKey().getEncoded());
                exportService.exportVault(current, file);
                new Alert(Alert.AlertType.INFORMATION, "Exportation réussie !").show();
            } catch (Exception ex) {
                new Alert(Alert.AlertType.ERROR, "Erreur d'exportation: " + ex.getMessage()).show();
            }
        }
    }
 
    private void handleCreate(IVaultItem item) {
        Vault vault = persistenceService.loadVault(sessionManager.getCurrentUser().getId(), sessionManager.getDecryptionKey().getEncoded());
        vault.addItem(item);
        persistenceService.saveVault(vault, sessionManager.getDecryptionKey().getEncoded());
        refreshVault();
        detailPanel.showDetails(item);
    }
 
    private void handleSave(IVaultItem item) {
        Vault vault = persistenceService.loadVault(sessionManager.getCurrentUser().getId(), sessionManager.getDecryptionKey().getEncoded());
        persistenceService.saveVault(vault, sessionManager.getDecryptionKey().getEncoded());
        refreshVault();
        detailPanel.showDetails(item);
    }

    private void handleDelete(IVaultItem item) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Voulez-vous vraiment supprimer cet élément ?", ButtonType.YES, ButtonType.NO);
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                Vault vault = persistenceService.loadVault(sessionManager.getCurrentUser().getId(), sessionManager.getDecryptionKey().getEncoded());
                vault.getItems().removeIf(i -> i.getName().equals(item.getName()) && i.getDescription().equals(item.getDescription()));
                persistenceService.saveVault(vault, sessionManager.getDecryptionKey().getEncoded());
                detailPanel.showEmpty();
                refreshVault();
            }
        });
    }

    public void update(double width, double height) {}
}
