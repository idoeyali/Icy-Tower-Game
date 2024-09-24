import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.ArrayList;
import java.util.List;

public class IceTowerGame extends Application {

    private Pane gamePane;  // This is the main game area (where we'll add game elements like player and platforms)
    private Scene gameScene;
    private final int WINDOW_WIDTH = 1200;
    private final int WINDOW_HEIGHT = 600;

    private Player player;

    @Override
    public void start(Stage primaryStage) {
        // Initialize the game pane (the root node of our game scene)
        gamePane = new Pane();

        //Adding game background
        Image backgroundImage = new Image("/util/desert_background.jpg");
        ImageView background = new ImageView(backgroundImage);
        background.setFitWidth(WINDOW_WIDTH);
        background.setFitHeight(WINDOW_HEIGHT);
        gamePane.getChildren().add(background);  // Add background to the game pane

        addPlayer();
        addInitialPlatforms();
//        addInitialPlatforms();
//        Rectangle debugRectangle = new Rectangle(100, 100, Color.RED);
//        gamePane.getChildren().add(debugRectangle);  // Add a red rectangle for debugging

        // Create a scene with the game pane
        gameScene = new Scene(gamePane, WINDOW_WIDTH, WINDOW_HEIGHT);

        // Set up keyboard controls
        gameScene.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case LEFT:
                    player.move(-10);  // Move left by 10 pixels
                    break;
                case RIGHT:
                    player.move(10);   // Move right by 10 pixels
                    break;
                case SPACE:
                    player.jump();     // Make the player jump
                    break;
                default:
                    break;
            }
        });

        gameScene.setOnKeyReleased(event -> {
            switch (event.getCode()) {
                case LEFT:
                case RIGHT:
                    player.stopMoving(); // Stop horizontal movement when key is released
                    break;
                default:
                    break;
            }
        });

        // Set up the stage (the game window)
        primaryStage.setTitle("Ice Tower Game");
        primaryStage.setScene(gameScene);
        primaryStage.show();

        // Start the game loop (handles continuous updates like movement, collision, etc.)
        startGameLoop();
    }

    private void addPlayer() {
        player = new Pikachu((double) WINDOW_WIDTH / 2 - 25, WINDOW_HEIGHT - 100);
        gamePane.getChildren().add(player.getImageView());
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
    private List<Platform> platforms;

    private void addInitialPlatforms() {
        platforms = new ArrayList<>();

        // Create some sample platforms at different positions
        Platform platform1 = new Platform(100, 450, 200, 20,"/util/plat.jpg"); // x, y, width, height
        Platform platform2 = new Platform(300, 300, 200, 20,"/util/plat.jpg");
        Platform platform3 = new Platform(600, 150, 200, 20,"/util/plat.jpg");
        Platform platform4 = new Platform(600, 350, 200, 20,"/util/plat.jpg");

        platforms.add(platform1);
        platforms.add(platform2);
        platforms.add(platform3);
        platforms.add(platform4);

        // Add the platforms to the game pane
        for (Platform platform : platforms) {
            gamePane.getChildren().add(platform.getImageView());
        }
    }
    // This method will update the game state in each frame
    private void update() {
        // Here, we'll later handle player movement, platform updates, etc.

        boolean isPlayerOnPlatform = false;
        for (Platform platform : platforms) {
//            if (platform.isPlayerBeneathPlatform(player)){
//                player.stopVerticalMovement();
//            }
            if (platform.isPlayerOnPlatform(player)) {
                player.getImageView().setTranslateY(platform.getImageView().getTranslateY() - player.getImageView().getFitHeight());
                player.setOnGround(true); // Set the player to be on the ground (on the platform)
                isPlayerOnPlatform = true;
                player.stopVerticalMovement(); // Stop falling when on the platform
                break; // Stop downward movement
            }
        }
//        System.out.println(isPlayerOnPlatform);
        // If not on a platform, check for jumping into one
        if (!isPlayerOnPlatform) {
            for (Platform platform : platforms) {
                if (platform.isPlayerJumpingInto(player)) {
                    // If jumping into a platform, stop upward movement
//                    player.getImageView().setTranslateY(platform.getImageView().getTranslateY() - player.getImageView().getFitHeight());
                    player.stop(); // Prevent the player from moving upwards
                    player.setOnGround(false); // Set player to on ground (platform)
                    isPlayerOnPlatform = true; // Set flag
                    break; // Exit loop, player collided with platform
                }
            }
        }

        // Apply gravity if the player is not on any platform
        if (!isPlayerOnPlatform) {
            player.setOnGround(false);
            player.applyGravity(); // Apply gravity if falling
        }

        player.update();
    }

    public static void main(String[] args) {
        launch(args);  // Launch the JavaFX application
    }
}
