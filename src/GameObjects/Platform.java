package GameObjects;

import GameObjects.Player;
import javafx.animation.PauseTransition;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

import java.security.cert.PolicyNode;

public class Platform {
    private ImageView platformImageView;
    private boolean playerStep;
    private Pane gamePane;
    private double liveTime;

    public Platform(double x, double y, double width, double height, String imagePath, Pane gamePane, double liveTime) {
        Image platformImage = new Image(getClass().getResourceAsStream(imagePath)); // Adjust the path
        platformImageView = new ImageView(platformImage);
        platformImageView.setFitWidth(width);
        platformImageView.setFitHeight(height);
        platformImageView.setTranslateX(x);
        platformImageView.setTranslateY(y);
        this.playerStep = false;
        this.gamePane = gamePane;
        this.liveTime = liveTime;
        gamePane.getChildren().add(platformImageView);
    }

    public ImageView getImageView() {
        return platformImageView;
    }

    public boolean isPlayerOnPlatform(Player player) {
        if (liveTime == 0){
            return false;
        }
        double playerBottomY = player.getImageView().getTranslateY() + player.getImageView().getFitHeight();
        // Get platform's top Y position
        double platformTopY = platformImageView.getTranslateY();
        // If the player height equal to platform height
        if (playerBottomY >= platformTopY - 15 && playerBottomY <= platformTopY + 5) {
            return player.getImageView().getTranslateX() + 20 <= platformImageView.getTranslateX() + platformImageView.getFitWidth() && player.getImageView().getTranslateX() + player.getImageView().getFitWidth() - 20 >= platformImageView.getTranslateX();
        }
        return false;
    }
    public boolean isPlayerBeneathPlatform(Player player) {
        double playerTopY = player.getImageView().getTranslateY();
        // Get platform's top Y position
        double platformTopY = platformImageView.getTranslateY() + platformImageView.getFitHeight();
        // If the player height equal to platform height
        if (platformTopY >= playerTopY - 15 && platformTopY <= playerTopY + 5) {
            return player.getImageView().getTranslateX() + 20 <= platformImageView.getTranslateX() + platformImageView.getFitWidth()
                    && player.getImageView().getTranslateX() + player.getImageView().getFitWidth() - 20 >= platformImageView.getTranslateX();
        }
        return false;
    }
    public boolean isPlayerJumpingInto(Player player) {
        // Get player's top Y position
        double playerTopY = player.getImageView().getTranslateY();

        // Get platform's bottom Y position (the top is where the player is landing)
        double platformBottomY = platformImageView.getTranslateY() + platformImageView.getFitHeight();

        // Check if the player's top Y is within the platform's vertical range during a jump
        if (playerTopY <= platformBottomY && playerTopY >= platformBottomY-15 && player.getVelocityY() < 0) {
            return player.getImageView().getTranslateX() + 20 <= platformImageView.getTranslateX() + platformImageView.getFitWidth()
                    && player.getImageView().getTranslateX() + player.getImageView().getFitWidth() - 20 >= platformImageView.getTranslateX();
        }
        return false;
    }

    public void setPlayerStep(boolean playerStep) {
        this.playerStep = playerStep;
        startDisappearTimer();
    }
    private void startDisappearTimer() {
        // Create a pause transition to delay for 10 seconds
        PauseTransition pause = new PauseTransition(Duration.seconds(liveTime));

        // After 10 seconds, remove the platform from the game pane
        pause.setOnFinished(event -> {
            gamePane.getChildren().remove(platformImageView);  // Remove platform
            liveTime=0;  // Update the field when platform disappears
        });
        // Start the timer
        pause.play();
    }
    public boolean isPlayerStep() {
        return playerStep;
    }
}
