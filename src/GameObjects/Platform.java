package GameObjects;

import GameObjects.Player;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Platform {
    private ImageView platformImageView;
    private boolean playerStep;

    public Platform(double x, double y, double width, double height, String imagePath) {
        Image playerImage = new Image(getClass().getResourceAsStream(imagePath)); // Adjust the path
        platformImageView = new ImageView(playerImage);
        platformImageView.setFitWidth(width);
        platformImageView.setFitHeight(height);
        platformImageView.setTranslateX(x);
        platformImageView.setTranslateY(y);
        playerStep = false;
    }

    public ImageView getImageView() {
        return platformImageView;
    }

    public boolean isPlayerOnPlatform(Player player) {
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
    }

    public boolean isPlayerStep() {
        return playerStep;
    }
}
