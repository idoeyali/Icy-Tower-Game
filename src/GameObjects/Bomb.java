package GameObjects;

import Collisions.CollisionStrategy;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * Bomb that is attached to a platform. Keeps offset relative to platform so it can move with it.
 */
public class Bomb {
    private final ImageView imageView;
    private final CollisionStrategy strategy;
    private final Platform platform;
    private final double offsetX; // itemX - platformX
    private final double offsetY; // itemY - platformY

    public Bomb(double itemX, double itemY, CollisionStrategy strategy, Platform platform) {
        this.strategy = strategy;
        this.platform = platform;
        this.offsetX = itemX - platform.getImageView().getTranslateX();
        this.offsetY = itemY - platform.getImageView().getTranslateY();

        Image bombImage = new Image(getClass().getResourceAsStream("/util/bomb.png"));
        this.imageView = new ImageView(bombImage);
        this.imageView.setFitWidth(40);
        this.imageView.setFitHeight(40);

        // initial placement relative to platform
        this.imageView.setTranslateX(platform.getImageView().getTranslateX() + offsetX);
        this.imageView.setTranslateY(platform.getImageView().getTranslateY() + offsetY);
    }

    public ImageView getImageView() {
        return imageView;
    }

    public CollisionStrategy getStrategy() {
        return strategy;
    }

    public Platform getPlatform() {
        return platform;
    }

    public double getOffsetX() {
        return offsetX;
    }

    public double getOffsetY() {
        return offsetY;
    }
}
