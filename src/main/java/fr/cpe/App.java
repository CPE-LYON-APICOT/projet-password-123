package fr.cpe;

// ╔══════════════════════════════════════════════════════════════════════════════╗
// ║                                                                            ║
// ║   🔒  FICHIER INTERDIT — NE PAS MODIFIER CE FICHIER  🔒                    ║
// ║                                                                            ║
// ║   Ce fichier fait partie du socle technique du projet.                      ║
// ║   Il gère le démarrage de l'application JavaFX et l'initialisation          ║
// ║   de l'injecteur Guice.                                                    ║
// ║                                                                            ║
// ║   Vous n'avez PAS besoin de le modifier. Si vous le cassez, plus rien      ║
// ║   ne démarre. Toute votre logique va dans GameService et vos propres        ║
// ║   classes.                                                                 ║
// ║                                                                            ║
// ║   Voir CONTRIBUTING.md pour savoir quels fichiers modifier.                ║
// ║                                                                            ║
// ╚══════════════════════════════════════════════════════════════════════════════╝

import com.google.inject.Guice;
import com.google.inject.Injector;
import fr.cpe.engine.GameEngine;
import fr.cpe.engine.InputService;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

/**
 * Point d'entrée de l'application JavaFX.
 *
 * <h2>Ce que fait cette classe :</h2>
 * <ol>
 * <li>Crée l'injecteur Guice avec {@link AppModule} (vos bindings)</li>
 * <li>Demande à Guice de construire le {@link GameEngine} avec toutes ses
 * dépendances</li>
 * <li>Crée la fenêtre JavaFX avec un Pane de 800×600</li>
 * <li>Lance la boucle de jeu via {@code engine.start(gamePane)}</li>
 * </ol>
 *
 * <h2>Flux d'injection Guice :</h2>
 * 
 * <pre>
 *   App  →  Guice.createInjector(AppModule)
 *        →  injector.getInstance(GameEngine)
 *              └── GameEngine(@Inject GameService)
 *                      └── GameService(@Inject PhysicsService)
 * </pre>
 *
 * @see AppModule pour déclarer vos bindings interface → implémentation
 * @see GameEngine pour la boucle de jeu (update à chaque frame)
 */
public class App extends Application {

    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;

    private GameEngine engine;

    @Override
    public void start(Stage stage) {
        // Création de l'injecteur Guice avec notre module de configuration
        Injector injector = Guice.createInjector(new AppModule());

        // Guice construit le GameEngine et injecte automatiquement les services
        engine = injector.getInstance(GameEngine.class);
        InputService inputService = injector.getInstance(InputService.class);

        Pane gamePane = new Pane();
        gamePane.setStyle("-fx-background-color: #1e1e2e;");
        Scene scene = new Scene(gamePane, WIDTH, HEIGHT);

        // Capture des événements clavier → InputService
        scene.setOnKeyPressed(e -> inputService.handleKeyPressed(e.getCode()));
        scene.setOnKeyReleased(e -> inputService.handleKeyReleased(e.getCode()));

        stage.setTitle("Password@123");
        stage.setScene(scene);
        stage.show();

        // Lancement de la boucle de jeu
        engine.start(gamePane);
    }

    @Override
    public void stop() {
        if (engine != null) {
            engine.stop();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
