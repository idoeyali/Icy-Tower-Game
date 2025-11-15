import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import java.util.List;
import GameObjects.Platform;

public class BackgroundManager {

    private ImageView startingBackground;
    private ImageView skyBackground;
    private ImageView gameEndedImageView;
    private Pane gamePane;
    private int windowWidth;
    private int windowHeight;
    private boolean firstBackgroundScrollComplete = false;

    public BackgroundManager(Pane gamePane, int windowWidth, int windowHeight) {
        this.gamePane = gamePane;
        this.windowWidth = windowWidth;
        this.windowHeight = windowHeight;
    }

    /**
     * Initializes the background images and adds them to the game pane.
     */
    public void initializeBackground() {
        Image backgroundImage = new Image(getClass().getResourceAsStream("/util/game_background.jpg"));
        startingBackground = new ImageView(backgroundImage);
        startingBackground.setFitWidth(windowWidth);
        startingBackground.setFitHeight(windowHeight);

        Image skyBackgroundImage = new Image(getClass().getResourceAsStream("/util/sky_background.jpg"));
        skyBackground = new ImageView(skyBackgroundImage);
        skyBackground.setFitWidth(windowWidth);
        skyBackground.setFitHeight(windowHeight);
        skyBackground.setTranslateY(-windowHeight);

        Image gameEndedImage = new Image(getClass().getResourceAsStream("/util/game_ended.jpg"));
        gameEndedImageView = new ImageView(gameEndedImage);
        gameEndedImageView.setFitWidth(windowWidth);
        gameEndedImageView.setFitHeight(windowHeight);
        gameEndedImageView.setVisible(false); // Initially hidden

        gamePane.getChildren().addAll(startingBackground, skyBackground, gameEndedImageView);
    }

    /**
     * Scrolls the background images and also moves the platforms.
     *
     * @param deltaY    The amount to scroll vertically.
     * @param platforms The list of platforms to scroll.
     */
    public void scrollBackground(double deltaY, List<Platform> platforms) {
        startingBackground.setTranslateY(startingBackground.getTranslateY() + deltaY);
        skyBackground.setTranslateY(skyBackground.getTranslateY() + deltaY);

        // Handle first scroll transition
        if (!firstBackgroundScrollComplete && startingBackground.getTranslateY() >= windowHeight) {
            firstBackgroundScrollComplete = true;
            startingBackground.setImage(new Image(getClass().getResourceAsStream("/util/sky_background.jpg")));
            startingBackground.setTranslateY(skyBackground.getTranslateY() - windowHeight);
        }

        // Reset background positions when they scroll off-screen
        if (skyBackground.getTranslateY() >= windowHeight) {
            skyBackground.setTranslateY(startingBackground.getTranslateY() - windowHeight);
        }
        if (startingBackground.getTranslateY() >= windowHeight) {
            startingBackground.setTranslateY(skyBackground.getTranslateY() - windowHeight);
        }

        // Scroll platforms along with the background
        for (Platform platform : platforms) {
            platform.getImageView().setTranslateY(platform.getImageView().getTranslateY() + deltaY);
        }
    }

    public void showGameOverScreen() {
        gameEndedImageView.setVisible(true);
    }

}
