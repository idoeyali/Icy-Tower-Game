import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class IceTowerGame extends Application {

    private Pane gamePane;  // This is the main game area (where we'll add game elements like player and platforms)
    private Scene gameScene;
    private final int WINDOW_WIDTH = 800;
    private final int WINDOW_HEIGHT = 600;

    @Override
    public void start(Stage primaryStage) {
        // Initialize the game pane (the root node of our game scene)
        gamePane = new Pane();

        // Create a scene with the game pane
        gameScene = new Scene(gamePane, WINDOW_WIDTH, WINDOW_HEIGHT);

        // Set up the stage (the game window)
        primaryStage.setTitle("Ice Tower Game");
        primaryStage.setScene(gameScene);
        primaryStage.show();

        // Start the game loop (handles continuous updates like movement, collision, etc.)
        startGameLoop();
    }

    // Game loop for continuous updates
    private void startGameLoop() {
        AnimationTimer gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                // Update game state (this will be called about 60 times per second)
                update();
            }
        };
        gameLoop.start();  // Start the loop
    }

    // This method will update the game state in each frame
    private void update() {
        // Here, we'll later handle player movement, platform updates, etc.
    }

    public static void main(String[] args) {
        launch(args);  // Launch the JavaFX application
    }
}
