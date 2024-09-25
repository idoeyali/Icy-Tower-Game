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

    private final static int WINDOW_WIDTH = 1200;
    private final static int WINDOW_HEIGHT = 600;
    private final double groundHeight = 500;
    private Pane gamePane;  // This is the main game area (where we'll add game elements like player and platforms)
    private Scene gameScene;

    private Player player;
    private ImageView startingBackground;
    private ImageView skyBackground;
    private ImageView gameEndedImageView;

    private List<Platform> platforms;
    private boolean firstBackgroundScrollComplete = false; //TODO



    @Override
    public void start(Stage primaryStage) {
        // Initialize the game pane (the root node of our game scene)
        gamePane = new Pane();

        initializedBackground();

        addPlayer();
        addInitialPlatforms();

        // Create a scene with the game pane
        gameScene = new Scene(gamePane, WINDOW_WIDTH, WINDOW_HEIGHT);

        // Set up keyboard controls
        keyPressingHandler();

        // Set up the stage (the game window)
        primaryStage.setTitle("Ice Tower Game");
        primaryStage.setScene(gameScene);
        primaryStage.show();

        // Start the game loop (handles continuous updates like movement, collision, etc.)
        startGameLoop();
    }

    private void initializedBackground() {
        //Adding game background
        Image backgroundImage = new Image("/util/sky2.jpg");
        startingBackground = new ImageView(backgroundImage);
        startingBackground.setFitWidth(WINDOW_WIDTH);
        startingBackground.setFitHeight(WINDOW_HEIGHT);

        Image skyBackgroundImage = new Image("/util/sky22.jpg");
        skyBackground = new ImageView(skyBackgroundImage);
        skyBackground.setFitWidth(WINDOW_WIDTH);
        skyBackground.setFitHeight(WINDOW_HEIGHT);
        skyBackground.setTranslateY(-WINDOW_HEIGHT);
        gamePane.getChildren().addAll(startingBackground, skyBackground);  // Add background to the game pane

        Image gameEndedImage = new Image("/util/game_ended.jpg");
        gameEndedImageView = new ImageView(gameEndedImage);
        gameEndedImageView.setFitWidth(WINDOW_WIDTH);
        gameEndedImageView.setFitHeight(WINDOW_HEIGHT);
        gameEndedImageView.setVisible(false); // Initially hidden
        gamePane.getChildren().add(gameEndedImageView);
    }

    private void keyPressingHandler() {
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
    }

    private void addPlayer() {
        player = new Pikachu((double) WINDOW_WIDTH / 2 - 25, groundHeight - 100, WINDOW_WIDTH);
        gamePane.getChildren().add(player.getImageView());
    }


    private void scrollWorld(double deltaY) {
        // Scroll both backgrounds
        startingBackground.setTranslateY(startingBackground.getTranslateY() + deltaY);
        skyBackground.setTranslateY(skyBackground.getTranslateY() + deltaY);

        // When background1 scrolls off the screen for the first time
        if (!firstBackgroundScrollComplete && startingBackground.getTranslateY() >= WINDOW_HEIGHT) {
            firstBackgroundScrollComplete = true;

            // Set background1 to background2's image after the first scroll
            startingBackground.setImage(new Image("/util/sky22.jpg"));
            startingBackground.setTranslateY(skyBackground.getTranslateY() - WINDOW_HEIGHT);  // Reset position

            // Now continue scrolling both as background2
        }
        // When background2 scrolls off-screen, reset it below background1
        if (skyBackground.getTranslateY() >= WINDOW_HEIGHT) {
            skyBackground.setTranslateY(startingBackground.getTranslateY() - WINDOW_HEIGHT);
        }

        // When background1 scrolls off-screen after first transition, reset it below background2
        if (startingBackground.getTranslateY() >= WINDOW_HEIGHT) {
            startingBackground.setTranslateY(skyBackground.getTranslateY() - WINDOW_HEIGHT);
        }

        // Scroll the platforms along with the background
        for (Platform platform : platforms) {
            platform.getImageView().setTranslateY(platform.getImageView().getTranslateY() + deltaY);
        }
    }

    private void generatePlatforms() {
        // Find the highest platform (the one closest to the top of the screen)
        double highestPlatformY = platforms.stream()
                .mapToDouble(platform -> platform.getImageView().getTranslateY())
                .min()
                .orElse(WINDOW_HEIGHT); // Default to window height if no platforms exist

        // If the highest platform is below a certain threshold, generate new platforms
        if (highestPlatformY > (double) WINDOW_HEIGHT / 3) {
            // Generate a new y-position slightly above the highest platform
            double newYPosition = highestPlatformY - randomDistance();

            // Create a new platform with random x and calculated y position
            Platform newPlatform = createRandomPlatform(newYPosition);

            // Add the new platform to the list and the scene
            platforms.add(newPlatform);
            gamePane.getChildren().add(newPlatform.getImageView());
        }
    }

    private Platform createRandomPlatform(double y) {
        // Create a platform at a random horizontal position
        double xPosition = Math.random() * (WINDOW_WIDTH - 200);
        // Generate a random width between 150 and 250
        double platformWidth = Math.random() * (250 - 150 + 1) + 150;
        return new Platform(xPosition,y,platformWidth, 20, "/util/plat.jpg");
    }
    private void removeOffScreenPlatforms() {
        platforms.removeIf(platform -> platform.getImageView().getTranslateY() > WINDOW_HEIGHT);
    }

    private double randomDistance() {
        return Math.random() * 100 + 50; // Random distance between 50 and 150 pixels
    }

    private void addInitialPlatforms() {
        platforms = new ArrayList<>();
        // set initial big platform to represent the ground
        Platform groundPlatform = new Platform(0, groundHeight, WINDOW_WIDTH, 20, "/util/plat.jpg"); // A full-width transparent ground
        groundPlatform.getImageView().setOpacity(0.5); // Adjust opacity to make it semi-transparent
        platforms.add(groundPlatform);

        // Create some sample platforms at different positions
        Platform platform1 = new Platform(100, 450, 200, 20, "/util/plat.jpg"); // x, y, width, height
        Platform platform2 = new Platform(300, 300, 200, 20, "/util/plat.jpg");
        Platform platform3 = new Platform(600, 150, 200, 20, "/util/plat.jpg");

        platforms.add(platform1);
        platforms.add(platform2);
        platforms.add(platform3);

        // Add the platforms to the game pane
        for (Platform platform : platforms) {
            gamePane.getChildren().add(platform.getImageView());
        }
    }

    // This method will update the game state in each frame
    private void update() {

        playerPlatformHandler();

        if (player.getImageView().getTranslateY() >= groundHeight) {
            endGame(); // Call the method to end the game
        }

        // Check if the player has reached the middle of the screen (or some threshold)
        double playerScreenY = player.getImageView().getTranslateY();

        if (playerScreenY < (double) WINDOW_HEIGHT / 2) {
            // Scroll the world downward instead of moving the player higher
            scrollWorld(-player.getVelocityY()); // Move platforms down at the rate of player's upward velocity
            player.getImageView().setTranslateY((double) WINDOW_HEIGHT / 2); // Keep player in the middle
        }

        // Call generatePlatforms to create new platforms as needed
        generatePlatforms();

        // Remove platforms that are off-screen
        removeOffScreenPlatforms();

        // Update player's position and velocity
        player.update();
    }

    private void playerPlatformHandler() {
        boolean isPlayerOnPlatform = false;
        for (Platform platform : platforms) {
            if (platform.isPlayerOnPlatform(player)) {
                player.getImageView().setTranslateY(platform.getImageView().getTranslateY() - player.getImageView().getFitHeight());
                player.setOnGround(true); // Set the player to be on the ground (on the platform)
                isPlayerOnPlatform = true;
                player.stopVerticalMovement(); // Stop falling when on the platform
                break; // Stop downward movement
            }
        }
        for (Platform platform : platforms) {
            if (platform.isPlayerJumpingInto(player)) {
                player.stop(); // Prevent the player from moving upwards
                player.setOnGround(false); // Set player to on ground (platform)
                isPlayerOnPlatform = true; // Set flag
                break; // Exit loop, player collided with platform
            }
        }

        // Apply gravity if the player is not on any platform
        if (!isPlayerOnPlatform) {
            player.setOnGround(false);
            player.applyGravity(); // Apply gravity if falling
        }
    }

    // Game loop for continuous updates
    private void startGameLoop() {
        AnimationTimer gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                update();
            }
        };
        gameLoop.start();  // Start the loop
    }
    private void endGame() {
        gameEndedImageView.setVisible(true); // Show the game ended image
        // Hide all platforms
        for (Platform platform : platforms) {
            platform.getImageView().setVisible(false); // Hide platform images
        }
        // Optionally, you can stop the game loop or disable controls here
        // For example:
        // gameLoop.stop();
    }
    public static void main(String[] args) {
        launch(args);  // Launch the JavaFX application
    }
}
