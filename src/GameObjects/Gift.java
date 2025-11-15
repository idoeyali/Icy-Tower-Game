package GameObjects;

import Collisions.CollisionStrategy;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * Gift attached to a platform (offset relative to platform).
 */
public class Gift {
    private final ImageView imageView;
    private final CollisionStrategy strategy;
    private final Platform platform;
    private final double offsetX;
    private final double offsetY;

    public Gift(double itemX, double itemY, CollisionStrategy strategy, Platform platform) {
        this.strategy = strategy;
        this.platform = platform;
        this.offsetX = itemX - platform.getImageView().getTranslateX();
        this.offsetY = itemY - platform.getImageView().getTranslateY();

        Image giftImage = new Image(getClass().getResourceAsStream("/util/gift.png"));
        this.imageView = new ImageView(giftImage);
        this.imageView.setFitWidth(40);
        this.imageView.setFitHeight(40);
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
