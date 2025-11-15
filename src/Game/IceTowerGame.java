
import GameObjects.Platform;
import GameObjects.Player;
import GameObjects.PlayerFactory;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
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
    private AnimationTimer gameLoop; // Store the timer reference

    private Player player;
    private BackgroundManager backgroundManager;

    private List<Platform> platforms;
    private int score = 0; // Keeps track of the player's score
    private Label scoreLabel; // Label to display the score
    private double platformTimer = 70;

    /**
     * The main entry point for the JavaFX application.
     * Initializes the game scene, and displays the player selection screen.
     *
     * @param primaryStage The primary stage for this application, onto which the application scene can be set.
     */
    @Override
    public void start(Stage primaryStage) {
        // Initialize the game pane (the root node of our game scene)
        gamePane = new Pane();
        // Show player selection UI
        startScreenHandler(primaryStage);
    }

    /**
     * Sets up the start screen for player selection.
     * Displays player selection options and sets the primary stage with the selection scene.
     *
     * @param primaryStage The primary stage used to display the selection screen.
     */
    private void startScreenHandler(Stage primaryStage) {
        // Create a new pane for the player selection
        Pane selectionPane = new Pane();

        // Initialize and add the background for the selection screen
        Image backgroundImage = new Image(getClass().getResourceAsStream("/util/welcome_screen.jpg"));
        ImageView backgroundImageView = new ImageView(backgroundImage);
        backgroundImageView.setFitWidth(WINDOW_WIDTH);  // Set to your window width
        backgroundImageView.setFitHeight(WINDOW_HEIGHT); // Set to your window height
        selectionPane.getChildren().add(backgroundImageView); // Add background to selection pane

        Label selectLabel = new Label("Select Your Player:");
        selectLabel.setStyle("-fx-font-size: 32px; -fx-text-fill: white;");
        selectLabel.setTranslateX(WINDOW_WIDTH / 3);
        selectLabel.setTranslateY(50);
        selectionPane.getChildren().add(selectLabel);

        initializedPlayersButton(primaryStage, selectionPane);

        // Create a scene with the selection pane
        Scene selectionScene = new Scene(selectionPane, WINDOW_WIDTH, WINDOW_HEIGHT);
        primaryStage.setScene(selectionScene);
        primaryStage.setTitle("Select Your Player");
        primaryStage.show();
    }

    /**
     * Initializes the player selection buttons for Pikachu and Bert.
     * Adds event listeners to handle the start of the game based on the player's choice.
     *
     * @param primaryStage  The primary stage used for displaying the game scene.
     * @param selectionPane The Pane to which the buttons for player selection are added.
     */
    private void initializedPlayersButton(Stage primaryStage, Pane selectionPane) {
        // Buttons for selecting players
        Button pikachuButton = new Button("Select Pikachu");
        Button bertButton = new Button("Select Bert");
        Button ericButton = new Button("Select Eric");

        pikachuButton.setTranslateX(WINDOW_WIDTH / 3);
        pikachuButton.setTranslateY(100);
        bertButton.setTranslateX(WINDOW_WIDTH / 3 + 150);
        bertButton.setTranslateY(100);
        ericButton.setTranslateX(WINDOW_WIDTH / 3 + 300);
        ericButton.setTranslateY(100);

        // Set button actions
        pikachuButton.setOnAction(event -> startGame("Pikachu", primaryStage));
        bertButton.setOnAction(event -> startGame("Bert", primaryStage));
        ericButton.setOnAction(event -> startGame("Eric", primaryStage));

        // Add components to the selection pane
        selectionPane.getChildren().addAll(pikachuButton, bertButton, ericButton);
    }

    /**
     * Starts the game based on the selected player type (Pikachu or Bert).
     * Initializes the background, player, platforms, and key controls, then switches to the game scene.
     *
     * @param playerType   The type of player selected (either "Pikachu" or "Bert").
     * @param primaryStage The primary stage used for displaying the game scene.
     */
    private void startGame(String playerType, Stage primaryStage) {
        gamePane = new Pane(); // Reinitialize game pane
        platforms = new ArrayList<>(); // Initialize the platforms list
        score = 0; // Reset score

        backgroundManager = new BackgroundManager(gamePane, WINDOW_WIDTH, WINDOW_HEIGHT);
        backgroundManager.initializeBackground();
        initializedScoreLabel();

        // Create and add player based on selection
        PlayerFactory playerFactory = new PlayerFactory();
        player = playerFactory.createPlayer(playerType, (double) WINDOW_WIDTH / 2, groundHeight - 100, WINDOW_WIDTH);
        gamePane.getChildren().add(player.getImageView()); // Ensure player is added before InputHandler

        addInitialPlatforms();

        gameScene = new Scene(gamePane, WINDOW_WIDTH, WINDOW_HEIGHT);
        new InputHandler(gameScene, player); // Set up input after player is added

        primaryStage.setScene(gameScene);
        primaryStage.setTitle("Ice Tower Game");
        primaryStage.show();

        startGameLoop(primaryStage); // Start the game loop
    }


    /**
     * Initializes the score label displayed in the top left corner of the game scene.
     * Sets its initial style and position on the game pane.
     */
    private void initializedScoreLabel() {
        scoreLabel = new Label("Score: " + score);
        scoreLabel.setStyle("-fx-font-size: 32px; -fx-text-fill: black;"); // Set font size and color
        scoreLabel.setTranslateX(10);  // Position on the screen
        scoreLabel.setTranslateY(10);  // Position on the screen

        // Add the score label to the game pane
        gamePane.getChildren().add(scoreLabel);
    }


    /**
     * Generates new platforms if the highest platform on the screen is below a certain threshold.
     * Ensures new platforms are created above the player's current position.
     */
    private void generatePlatforms() {
        // Find the highest platform (the one closest to the top of the screen)
        Platform highestPlatform = platforms.stream()
                .min((p1, p2) -> Double.compare(p1.getImageView().getTranslateY(), p2.getImageView().getTranslateY()))
                .orElse(null); // Default to null if no platforms exist

        if (highestPlatform != null) {
            // Get the x and y of the highest platform
            double highestPlatformY = highestPlatform.getImageView().getTranslateY();
            double highestPlatformX = highestPlatform.getImageView().getTranslateX();
            // If the highest platform is below a certain threshold, generate new platforms
            if (highestPlatformY > (double) WINDOW_HEIGHT / 3) {
                // Generate a random x-position within ±[100,200] pixels of the highest platform's x-position
                double newXPosition = generateNewXPosition(highestPlatformX);
                // Make sure the new platform's x-position stays within the screen bounds
                newXPosition = Math.max(50, Math.min(WINDOW_WIDTH - 250, newXPosition)); // Keep within window width

                // Generate a new y-position slightly above the highest platform
                double newYPosition = highestPlatformY - randomDistance(125, 150);

                // Create a new platform with random x and calculated y position
                Platform newPlatform = createRandomPlatform(newXPosition, newYPosition);
                // Add the new platform to the list and the scene
                platforms.add(newPlatform);
            }
        }
    }

    /**
     * Generates a random X position for new platforms within the bounds of the window.
     * Ensures the new platform isn't too far from the last generated platform.
     *
     * @param highestPlatformX The X position of the highest platform.
     * @return A new X position for the next platform.
     */
    private double generateNewXPosition(double highestPlatformX) {
        double distance = randomDistance(150, 400);
        if (highestPlatformX < 100) {
            //If the highest GameObjects.Platform close to left side of screen set newX to be on right to highestPlatform
            return highestPlatformX + distance;
        } else if (highestPlatformX > WINDOW_WIDTH - 300) {
            //If the highest GameObjects.Platform close to right side of screen set newX to be on left to highestPlatform
            return highestPlatformX - distance;
        } else {
            // set newX to be randomly left/right to highestPlatform
            int randomSign = Math.random() < 0.5 ? Constants.LEFT_DIRECTION : Constants.RIGHT_DIRECTION;
            return highestPlatformX + distance * randomSign;
        }
    }

    /**
     * Creates a new platform with random width and at the specified position.
     * Adjusts platform's image based on the current score.
     *
     * @param xPosition The X coordinate of the new platform.
     * @param yPosition The Y coordinate of the new platform.
     * @return The newly created platform.
     */
    private Platform createRandomPlatform(double xPosition, double yPosition) {
        // Generate a random width between 150 and 250
        double platformWidth = randomDistance(150, 250);
        String imagePath = score < 50 ? "/util/black_platform.jpg" : "/util/red_platform.jpg";
        imagePath = score > 100 ? "/util/green_platform.jpg" : imagePath;
        return new Platform(xPosition, yPosition, platformWidth, 20, imagePath, gamePane, platformTimer);
    }

    /**
     * Removes platforms that are off-screen (i.e., platforms that have scrolled past the bottom of the game window).
     */
    private void removeOffScreenPlatforms() {
        platforms.removeIf(platform -> platform.getImageView().getTranslateY() > WINDOW_HEIGHT);
    }

    /**
     * Generates a random distance within a specified range.
     *
     * @param leftRange  The minimum value for the random distance.
     * @param rightRange The maximum value for the random distance.
     * @return A random double value between leftRange and rightRange.
     */
    private double randomDistance(double leftRange, double rightRange) {
        return Math.random() * (rightRange - leftRange) + leftRange;
    }

    /**
     * Adds the initial platforms to the game pane.
     * Creates and positions the starting platforms for the player to jump on.
     */
    private void addInitialPlatforms() {
        platforms = new ArrayList<>();
        // set initial big platform to represent the ground
        Platform groundPlatform = new Platform(0, groundHeight, WINDOW_WIDTH, 20, "/util/black_platform.jpg", gamePane, 100); // A full-width transparent ground
        groundPlatform.getImageView().setVisible(false);
        platforms.add(groundPlatform);
        generatePlatforms();
        generatePlatforms();
        generatePlatforms();
    }

    /**
     * Handles collision detection between the player and the platforms.
     * Checks whether the player is on a platform or is jumping into one, applying gravity if necessary.
     */
    private void playerPlatformHandler() {
        boolean isPlayerOnPlatform = false;
        for (Platform platform : platforms) {
            if (platform.isPlayerOnPlatform(player)) {
                player.getImageView().setTranslateY(platform.getImageView().getTranslateY() - player.getImageView().getFitHeight());
                player.setOnGround(true); // Set the player to be on the ground (on the platform)
                isPlayerOnPlatform = true;
                player.stopVerticalMovement();
                // Check if the platform hasn't already given score
                if (!platform.isPlayerStep()) {
                    score++;
                    platform.setPlayerStep(true);  // Mark the platform as scored
                }// Stop falling when on the platform
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

    public void addScore(int amount){
        score += amount;
    }

    /**
     * Updates the game state in each frame.
     * This includes handling collisions, generating new platforms, updating player position, and managing the score.
     */
    private void updateGameState(Stage primaryStage) {
        // Handle collision and jumping of the player and platforms
        playerPlatformHandler();

        // Adjust platform timer based on score: if score 0-50 3 sec, if score 50-100 2 sec, and 1 sec otherwise
        platformTimer = score > 100 ? 1 : 2;
        platformTimer = score < 50 ? 3 : platformTimer;
        if (player.getImageView().getTranslateY() >= groundHeight + 30) {
            endGame(primaryStage); // Call the method to end the game
        }

        // Call generatePlatforms to create new platforms as needed
        generatePlatforms();

        // Remove platforms that are off-screen
        removeOffScreenPlatforms();

        // Update player's position and velocity
        player.update();
    }

    /**
     * Renders the game visuals in each frame.
     * This includes handling background scrolling, keeping the player centered, and updating the score label.
     */
    private void render() {
        double playerScreenY = player.getImageView().getTranslateY();
        if (playerScreenY < (double) WINDOW_HEIGHT / 2) {
            // Use the background manager to handle scrolling
            backgroundManager.scrollBackground(-player.getVelocityY(), platforms);
            player.getImageView().setTranslateY((double) WINDOW_HEIGHT / 2);
        }
        scoreLabel.setText("Score: " + score);
    }


    /**
     * Starts the game loop that continuously updates the game state and renders the visuals.
     * The loop runs at every frame, ensuring smooth gameplay.
     */
    private void startGameLoop(Stage primaryStage) {
        if (gameLoop != null) {
            gameLoop.stop(); // Stop any previous game loop
        }

        gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                updateGameState(primaryStage);  // Update all game-related logic
                render();           // Draw everything to the screen
            }
        };
        gameLoop.start();  // Start the loop
    }


    /**
     * Ends the game when the player falls below the ground.
     * Displays the game over screen, hides platforms, and shows the final score.
     */
    private void endGame(Stage primaryStage) {
        // Show game over background via BackgroundManager
        backgroundManager.showGameOverScreen();

        // Hide the player's image view so it "disappears" from the screen
        player.getImageView().setVisible(false);

        // Hide all platform images
        for (Platform platform : platforms) {
            platform.getImageView().setVisible(false);
        }

        // Hide the score label
        scoreLabel.setVisible(false);

        // Optionally, display a final score label
        Label finalScoreLabel = new Label("Final score: " + score + "\nPress R to Restart The Game");
        finalScoreLabel.setStyle("-fx-font-size: 32px; -fx-text-fill: white;");
        finalScoreLabel.setTranslateX(WINDOW_WIDTH / 3);
        finalScoreLabel.setTranslateY(50);
        gamePane.getChildren().add(finalScoreLabel);
        gameScene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.R) {
                startScreenHandler(primaryStage); // Restart the game
            }
        });
    }


    public static void main(String[] args) {
        launch(args);  // Launch the JavaFX application
    }
}
