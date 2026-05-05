package fr.cpe.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class ImportChoiceDialog extends Dialog<String> {

    public ImportChoiceDialog(String styleSheet) {
        setTitle("Importer un coffre");
        setHeaderText("Sélectionnez la source de l'importation");
        
        if (!styleSheet.isEmpty()) {
            getDialogPane().getStylesheets().add(styleSheet);
        }
        getDialogPane().getStyleClass().add("dialog-pane");

        ButtonType importBtn = new ButtonType("Importer", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelBtn = new ButtonType("Annuler", ButtonBar.ButtonData.CANCEL_CLOSE);
        getDialogPane().getButtonTypes().addAll(importBtn, cancelBtn);

        VBox content = new VBox(15);
        content.setPadding(new Insets(20));
        content.setAlignment(Pos.CENTER);

        ToggleGroup group = new ToggleGroup();
        RadioButton rbStandard = new RadioButton("Standard (CSV / Bitwarden)");
        rbStandard.setUserData("standard");
        RadioButton rbNord = new RadioButton("NordPass (CSV)");
        rbNord.setUserData("nordpass");
        RadioButton rbProton = new RadioButton("Proton Pass (CSV)");
        rbProton.setUserData("protonpass");
        RadioButton rbOne = new RadioButton("1Password (CSV)");
        rbOne.setUserData("1password");

        rbStandard.setToggleGroup(group);
        rbNord.setToggleGroup(group);
        rbProton.setToggleGroup(group);
        rbOne.setToggleGroup(group);
        rbStandard.setSelected(true);

        // Styling for dark mode
        content.getChildren().addAll(rbStandard, rbNord, rbProton, rbOne);
        content.getChildren().forEach(n -> {
            if (n instanceof RadioButton rb) rb.setStyle("-fx-text-fill: #cdd6f4;");
        });

        getDialogPane().setContent(content);

        setResultConverter(dialogButton -> {
            if (dialogButton == importBtn) {
                return (String) group.getSelectedToggle().getUserData();
            }
            return null;
        });
    }
}
