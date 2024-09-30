import GameObjects.Pikachu;
import GameObjects.Platform;
import GameObjects.Player;
import GameObjects.PlayerFactory;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
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
    private boolean firstBackgroundScrollComplete = false;
    private int score = -1; // Keeps track of the player's score
    private Label scoreLabel; // Label to display the score
    private double platformTimer = 2;

    /**
     * The main entry point for the JavaFX application.
     * Initializes the game scene, sets up the score label, and displays the player selection screen.
     *
     * @param primaryStage The primary stage for this application, onto which the application scene can be set.
     */
    @Override
    public void start(Stage primaryStage) {
        // Initialize the game pane (the root node of our game scene)
        gamePane = new Pane();
        // Create and style the score label
        initializedScoreLabel();
        // Show player selection UI
        startScreenHandler(primaryStage);
    }

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

    private void initializedPlayersButton(Stage primaryStage, Pane selectionPane) {
        // Buttons for selecting players
        Button pikachuButton = new Button("Select Pikachu");
        Button bertButton = new Button("Select Bert");

        pikachuButton.setTranslateX(WINDOW_WIDTH / 3);
        pikachuButton.setTranslateY(100);
        bertButton.setTranslateX(WINDOW_WIDTH / 3 + 150);
        bertButton.setTranslateY(100);

        // Set button actions
        pikachuButton.setOnAction(event -> startGame("Pikachu", primaryStage));
        bertButton.setOnAction(event -> startGame("Bert", primaryStage));

        // Add components to the selection pane
        selectionPane.getChildren().addAll(pikachuButton, bertButton);
    }

    private void startGame(String playerType, Stage primaryStage) {
        // Remove player selection UI
        gamePane.getChildren().clear();
        initializedBackground();
        gamePane.getChildren().add(scoreLabel); // Retain score label

        // Create and add player based on selection
        PlayerFactory playerFactory = new PlayerFactory();
        player = playerFactory.createPlayer(playerType, (double) WINDOW_WIDTH / 2 - 25, groundHeight - 100, WINDOW_WIDTH);

        // Add player to the game pane
        gamePane.getChildren().add(player.getImageView());

        addInitialPlatforms();

        // Create a scene with the game pane
        gameScene = new Scene(gamePane, WINDOW_WIDTH, WINDOW_HEIGHT);

        // Set up keyboard controls
        keyPressingHandler();

        // Set the scene to the main game scene
        primaryStage.setScene(gameScene);
        primaryStage.setTitle("Ice Tower Game");
        primaryStage.show();

        // Start the game loop (handles continuous updates like movement, collision, etc.)
        startGameLoop();
    }

    private void initializedScoreLabel() {
        scoreLabel = new Label("Score: " + score);
        scoreLabel.setStyle("-fx-font-size: 32px; -fx-text-fill: black;"); // Set font size and color
        scoreLabel.setTranslateX(10);  // Position on the screen
        scoreLabel.setTranslateY(10);  // Position on the screen

        // Add the score label to the game pane
        gamePane.getChildren().add(scoreLabel);
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

        Image gameEndedImage = new Image("/util/game_ended.jpg");
        gameEndedImageView = new ImageView(gameEndedImage);
        gameEndedImageView.setFitWidth(WINDOW_WIDTH);
        gameEndedImageView.setFitHeight(WINDOW_HEIGHT);
        gameEndedImageView.setVisible(false); // Initially hidden
        gamePane.getChildren().addAll(startingBackground, skyBackground, gameEndedImageView);
    }

    private void keyPressingHandler() {
        gameScene.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case LEFT:
                    player.move(Constants.LEFT_DIRECTION);  // Move left by 10 pixels
                    break;
                case RIGHT:
                    player.move(Constants.RIGHT_DIRECTION);   // Move right by 10 pixels
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


    private Platform createRandomPlatform(double xPosition, double yPosition) {
        // Generate a random width between 150 and 250
        double platformWidth = randomDistance(150, 250);
        String imagePath = score > 50 ? "/util/red_platform.jpg" : "/util/plat.jpg";
        return new Platform(xPosition, yPosition, platformWidth, 20, imagePath, gamePane, platformTimer);
    }

    private void removeOffScreenPlatforms() {
        platforms.removeIf(platform -> platform.getImageView().getTranslateY() > WINDOW_HEIGHT);
    }

    private double randomDistance(double leftRange, double rightRange) {
        return Math.random() * (rightRange - leftRange) + leftRange;
    }

    private void addInitialPlatforms() {
        platforms = new ArrayList<>();
        // set initial big platform to represent the ground
        Platform groundPlatform = new Platform(0, groundHeight, WINDOW_WIDTH, 20, "/util/plat.jpg", gamePane, 100); // A full-width transparent ground
        groundPlatform.getImageView().setVisible(false);
        ; // Adjust opacity to make it semi-transparent

        platforms.add(groundPlatform);


        // Create some sample platforms at different positions
        Platform platform1 = new Platform(100, 450, 200, 20, "/util/plat.jpg", gamePane, platformTimer); // x, y, width, height
        Platform platform2 = new Platform(300, 300, 200, 20, "/util/plat.jpg", gamePane, platformTimer);
        Platform platform3 = new Platform(600, 150, 200, 20, "/util/plat.jpg", gamePane, platformTimer);

        platforms.add(platform1);
        platforms.add(platform2);
        platforms.add(platform3);

    }

    // This method will update the game state in each frame
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
                    score++;  // Increase the score by 1
                    scoreLabel.setText("Score: " + score);  // Update the score label
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

    private void update() {

        playerPlatformHandler();

        if (player.getImageView().getTranslateY() >= groundHeight + 20) {
            endGame(); // Call the method to end the game
        }

        // Check if the player has reached the middle of the screen (or some threshold)
        double playerScreenY = player.getImageView().getTranslateY();
        platformTimer = score > 50 ? 1 : 2;
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
        scoreLabel.setVisible(false);
        Label finalScoreLabel = new Label("Final score: " + score);
        finalScoreLabel.setStyle("-fx-font-size: 32px; -fx-text-fill: white;");
        finalScoreLabel.setTranslateX(WINDOW_WIDTH / 3 + 100);
        finalScoreLabel.setTranslateY(50);
        gamePane.getChildren().add(finalScoreLabel);
        // Optionally, you can stop the game loop or disable controls here
        // For example:
        // gameLoop.stop();
    }

    public static void main(String[] args) {
        launch(args);  // Launch the JavaFX application
    }
}
