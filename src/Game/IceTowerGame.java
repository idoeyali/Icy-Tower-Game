package Game;

import GameObjects.Bomb;
import GameObjects.Gift;
import GameObjects.Platform;
import GameObjects.Player;
import GameObjects.PlayerFactory;
import Collisions.EndGameStrategy;
import Collisions.IncreaseScoreStrategy;
import Collisions.DecreaseScoreStrategy;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.scene.image.Image;

import java.util.ArrayList;
import java.util.List;

public class IceTowerGame extends Application {

    private final double groundHeight = 500;

    private Pane gamePane;
    private Scene gameScene;
    private AnimationTimer gameLoop;

    private Player player;
    private BackgroundManager backgroundManager;
    private Stage primaryStageRef;

    private List<Platform> platforms;
    private int score = 0;
    private Label scoreLabel;
    private double platformTimer = 70;

    private List<Bomb> bombs;
    private List<Gift> gifts;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStageRef = primaryStage;
        gamePane = new Pane();
        startScreenHandler(primaryStage);
    }

    private void startScreenHandler(Stage primaryStage) {
        Pane selectionPane = new Pane();

        Image backgroundImage = new Image(getClass().getResourceAsStream("/util/welcome_screen.jpg"));
        ImageView backgroundImageView = new ImageView(backgroundImage);
        backgroundImageView.setFitWidth(Constants.WINDOW_WIDTH);
        backgroundImageView.setFitHeight(Constants.WINDOW_HEIGHT);
        selectionPane.getChildren().add(backgroundImageView);

        Label selectLabel = new Label("Select Your Player:");
        selectLabel.setStyle("-fx-font-size: 32px; -fx-text-fill: white;");
        selectLabel.setTranslateX(Constants.WINDOW_WIDTH / 3);
        selectLabel.setTranslateY(50);
        selectionPane.getChildren().add(selectLabel);

        initializedPlayersButton(primaryStage, selectionPane);

        Scene selectionScene = new Scene(selectionPane, Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);
        primaryStage.setScene(selectionScene);
        primaryStage.setTitle("Select Your Player");
        primaryStage.show();
    }

    private void initializedPlayersButton(Stage primaryStage, Pane selectionPane) {
        Button pikachuButton = new Button("Select Pikachu");
        Button bertButton = new Button("Select Bert");
        Button ericButton = new Button("Select Eric");

        pikachuButton.setTranslateX(Constants.WINDOW_WIDTH / 3);
        pikachuButton.setTranslateY(100);
        bertButton.setTranslateX(Constants.WINDOW_WIDTH / 3 + 150);
        bertButton.setTranslateY(100);
        ericButton.setTranslateX(Constants.WINDOW_WIDTH / 3 + 300);
        ericButton.setTranslateY(100);

        pikachuButton.setOnAction(event -> startGame("Pikachu", primaryStage));
        bertButton.setOnAction(event -> startGame("Bert", primaryStage));
        ericButton.setOnAction(event -> startGame("Eric", primaryStage));

        selectionPane.getChildren().addAll(pikachuButton, bertButton, ericButton);
    }

    private void startGame(String playerType, Stage primaryStage) {
        gamePane = new Pane();
        platforms = new ArrayList<>();
        bombs = new ArrayList<>();
        gifts = new ArrayList<>();
        score = 0;

        backgroundManager = new BackgroundManager(gamePane, Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);
        backgroundManager.initializeBackground();
        initializedScoreLabel();

        PlayerFactory playerFactory = new PlayerFactory();
        player = playerFactory.createPlayer(playerType, (double) Constants.WINDOW_WIDTH / 2, groundHeight - 100, Constants.WINDOW_WIDTH);
        gamePane.getChildren().add(player.getImageView());

        addInitialPlatforms();

        gameScene = new Scene(gamePane, Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);
        new InputHandler(gameScene, player);

        primaryStage.setScene(gameScene);
        primaryStage.setTitle("IceTower Game");
        primaryStage.show();

        startGameLoop(primaryStage);
    }

    private void initializedScoreLabel() {
        scoreLabel = new Label("Score: " + score);
        scoreLabel.setStyle("-fx-font-size: 32px; -fx-text-fill: black;");
        scoreLabel.setTranslateX(10);
        scoreLabel.setTranslateY(10);
        gamePane.getChildren().add(scoreLabel);
    }

    private void addInitialPlatforms() {
        platforms = new ArrayList<>();
        Platform groundPlatform = new Platform(0, groundHeight, Constants.WINDOW_WIDTH, 20, "/util/black_platform.jpg", gamePane, 100);
        groundPlatform.getImageView().setVisible(false);
        platforms.add(groundPlatform);

        generatePlatforms();
        generatePlatforms();
        generatePlatforms();
    }

    private void generatePlatforms() {
        Platform highestPlatform = platforms.stream()
                .min((p1, p2) -> Double.compare(p1.getImageView().getTranslateY(), p2.getImageView().getTranslateY()))
                .orElse(null);

        if (highestPlatform != null) {
            double highestPlatformY = highestPlatform.getImageView().getTranslateY();
            double highestPlatformX = highestPlatform.getImageView().getTranslateX();
            if (highestPlatformY > (double) Constants.WINDOW_HEIGHT / 3) {
                double newXPosition = generateNewXPosition(highestPlatformX);
                newXPosition = Math.max(50, Math.min(Constants.WINDOW_WIDTH - 250, newXPosition));
                double newYPosition = highestPlatformY - randomDistance(125, 150);

                Platform newPlatform = createRandomPlatform(newXPosition, newYPosition);
                platforms.add(newPlatform);

                // spawn item on platform
                addRandomBombOrGiftOnPlatform(newPlatform);
            }
        }
    }

    private double generateNewXPosition(double highestPlatformX) {
        double distance = randomDistance(150, 400);
        if (highestPlatformX < 100) return highestPlatformX + distance;
        if (highestPlatformX > Constants.WINDOW_WIDTH - 300) return highestPlatformX - distance;
        int randomSign = Math.random() < 0.5 ? Constants.LEFT_DIRECTION : Constants.RIGHT_DIRECTION;
        return highestPlatformX + distance * randomSign;
    }

    private Platform createRandomPlatform(double xPosition, double yPosition) {
        double platformWidth = randomDistance(150, 250);
        String imagePath = score < 50 ? "/util/black_platform.jpg" : "/util/red_platform.jpg";
        imagePath = score > 100 ? "/util/green_platform.jpg" : imagePath;
        return new Platform(xPosition, yPosition, platformWidth, 20, imagePath, gamePane, platformTimer);
    }

    private double randomDistance(double leftRange, double rightRange) {
        return Math.random() * (rightRange - leftRange) + leftRange;
    }

    private void playerPlatformHandler() {
        boolean isPlayerOnPlatform = false;
        for (Platform platform : platforms) {
            if (platform.isPlayerOnPlatform(player)) {
                player.getImageView().setTranslateY(platform.getImageView().getTranslateY() - player.getImageView().getFitHeight());
                player.setOnGround(true);
                isPlayerOnPlatform = true;
                player.stopVerticalMovement();
                if (!platform.isPlayerStep()) {
                    score++;
                    platform.setPlayerStep(true);
                }
                break;
            }
        }
        for (Platform platform : platforms) {
            if (platform.isPlayerJumpingInto(player)) {
                player.stop();
                player.setOnGround(false);
                isPlayerOnPlatform = true;
                break;
            }
        }
        if (!isPlayerOnPlatform) {
            player.setOnGround(false);
            player.applyGravity();
        }
    }

    private void addRandomBombOrGiftOnPlatform(Platform platform) {
        double x = platform.getImageView().getTranslateX() + platform.getImageView().getFitWidth() / 2 - 20;
        double y = platform.getImageView().getTranslateY() - 50;

        double r = Math.random();
        if (platform.getImageView().getFitWidth()> 220 && r < Constants.BOMB_CHANCES) {
            Bomb bomb = new Bomb(x, y, new EndGameStrategy(), platform);
            bombs.add(bomb);
            gamePane.getChildren().add(bomb.getImageView());
        } else if (r < Constants.BAD_GIFT_CHANCES) {
            Gift gift = new Gift(x, y, new DecreaseScoreStrategy(10), platform);
            gifts.add(gift);
            gamePane.getChildren().add(gift.getImageView());
        } else if (r < Constants.GOOD_GIFT_CHANCES) {
            Gift gift = new Gift(x, y, new IncreaseScoreStrategy(10), platform);
            gifts.add(gift);
            gamePane.getChildren().add(gift.getImageView());
        }
    }

    /**
     * Move all items to follow their platform each frame.
     */
    private void updateItemsPosition() {
        for (Bomb b : bombs) {
            Platform p = b.getPlatform();
            if (p != null) {
                double newX = p.getImageView().getTranslateX() + b.getOffsetX();
                double newY = p.getImageView().getTranslateY() + b.getOffsetY();
                b.getImageView().setTranslateX(newX);
                b.getImageView().setTranslateY(newY);
            }
        }
        for (Gift g : gifts) {
            Platform p = g.getPlatform();
            if (p != null) {
                double newX = p.getImageView().getTranslateX() + g.getOffsetX();
                double newY = p.getImageView().getTranslateY() + g.getOffsetY();
                g.getImageView().setTranslateX(newX);
                g.getImageView().setTranslateY(newY);
            }
        }
    }

    private void removeOffScreenPlatforms() {
        List<Platform> removedPlatforms = new ArrayList<>();
        platforms.removeIf(platform -> {
            if (platform.getImageView().getTranslateY() > Constants.WINDOW_HEIGHT) {
                removedPlatforms.add(platform);
                platform.getImageView().setVisible(false);
                return true;
            }
            return false;
        });

        bombs.removeIf(b -> {
            if (removedPlatforms.contains(b.getPlatform())) {
                gamePane.getChildren().remove(b.getImageView());
                return true;
            }
            // also remove bombs that fell off-screen for any other reason
            return b.getImageView().getTranslateY() > Constants.WINDOW_HEIGHT;
        });

        gifts.removeIf(g -> {
            if (removedPlatforms.contains(g.getPlatform())) {
                gamePane.getChildren().remove(g.getImageView());
                return true;
            }
            return g.getImageView().getTranslateY() > Constants.WINDOW_HEIGHT;
        });
    }

    private void handleBombGiftCollisions() {
        bombs.removeIf(b -> {
            ImageView iv = b.getImageView();
            if (player.getImageView().getBoundsInParent().intersects(iv.getBoundsInParent())) {
                b.getStrategy().onCollision(this);
                gamePane.getChildren().remove(iv);
                return true;
            }
            return false;
        });

        gifts.removeIf(g -> {
            ImageView iv = g.getImageView();
            if (player.getImageView().getBoundsInParent().intersects(iv.getBoundsInParent())) {
                g.getStrategy().onCollision(this);
                gamePane.getChildren().remove(iv);
                return true;
            }
            return false;
        });
    }

    public void addScore(int amount) {
        score += amount;
        score = Math.max(score,0);
    }

    public Stage getPrimaryStage() {
        return primaryStageRef;
    }

    private void updateGameState(Stage primaryStage) {
        playerPlatformHandler();

        // update item positions to follow their platforms
        updateItemsPosition();

        // handle collisions after updating positions
        handleBombGiftCollisions();

        platformTimer = score > 100 ? 1 : 2;
        platformTimer = score < 50 ? 3 : platformTimer;

        if (player.getImageView().getTranslateY() >= groundHeight + 30) {
            endGame(primaryStage);
            return;
        }

        generatePlatforms();
        removeOffScreenPlatforms();
        player.update();
    }

    private void render() {
        double playerScreenY = player.getImageView().getTranslateY();
        if (playerScreenY < (double) Constants.WINDOW_HEIGHT / 2) {
            backgroundManager.scrollBackground(-player.getVelocityY(), platforms);
            player.getImageView().setTranslateY((double) Constants.WINDOW_HEIGHT / 2);
        }
        scoreLabel.setText("Score: " + score);
    }

    private void startGameLoop(Stage primaryStage) {
        if (gameLoop != null) gameLoop.stop();

        gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                updateGameState(primaryStage);
                render();
            }
        };
        gameLoop.start();
    }

    public void endGame(Stage primaryStage) {
        // show game over
        backgroundManager.showGameOverScreen();

        // hide player
        player.getImageView().setVisible(false);

        // hide all platforms
        for (Platform platform : platforms) platform.getImageView().setVisible(false);

        // hide item visuals
        for (Bomb bomb : bombs) if (bomb.getImageView() != null) bomb.getImageView().setVisible(false);
        for (Gift gift : gifts) if (gift.getImageView() != null) gift.getImageView().setVisible(false);

        // hide score label
        scoreLabel.setVisible(false);

        Label finalScoreLabel = new Label("Final score: " + score + "\nPress R to Restart The Game");
        finalScoreLabel.setStyle("-fx-font-size: 32px; -fx-text-fill: white;");
        finalScoreLabel.setTranslateX(Constants.WINDOW_WIDTH / 3);
        finalScoreLabel.setTranslateY(50);
        gamePane.getChildren().add(finalScoreLabel);

        gameScene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.R) startScreenHandler(primaryStage);
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}
