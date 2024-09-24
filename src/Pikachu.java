import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Pikachu extends Player{
    public Pikachu(double startX, double startY) {
        super(startX, startY);
        initializeImage(startX, startY);
    }

    @Override
    protected void initializeImage(double startX, double startY) {
        // Load the player image
        Image playerImage = new Image(getClass().getResourceAsStream("/util/pikachu_avatar.png")); // Adjust the path
        playerImageView = new ImageView(playerImage);
        playerImageView.setFitWidth(80);
        playerImageView.setFitHeight(80);
        playerImageView.setTranslateX(startX);
        playerImageView.setTranslateY(startY);
    }
}
