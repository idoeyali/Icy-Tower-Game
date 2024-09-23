import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Player {
    private ImageView playerImageView;  // Visual representation of the player
    private double velocityY;             // Vertical velocity for jumping/falling
    private double gravity;               // Gravity effect
    private double jumpStrength;          // Strength of the jump
    private boolean onGround;             // Flag to check if the player is on the ground
    private double velocityX;             // Horizontal velocity

    // Constructor
    public Player(double startX, double startY) {
        // Load the player image
        Image playerImage = new Image(getClass().getResourceAsStream("/util/pikachu_avatar.png")); // Adjust the path
        playerImageView = new ImageView(playerImage);
        playerImageView.setFitWidth(100);
        playerImageView.setFitHeight(100);
        playerImageView.setTranslateX(startX);
        playerImageView.setTranslateY(startY);

        this.gravity = 1.0;
        this.jumpStrength = -20.0;
        this.onGround = false;
        this.velocityY = 0;
        this.velocityX = 0;  // Initialize horizontal velocity
    }

    public ImageView getImageView() {
        return playerImageView;
    }

    public void update() {
        // Apply gravity if not on the ground
        if (!onGround) {
            velocityY += gravity; // Increase velocity downwards
        }

        // Update the player's position based on vertical and horizontal velocities
        playerImageView.setTranslateY(playerImageView.getTranslateY() + velocityY);
        playerImageView.setTranslateX(playerImageView.getTranslateX() + velocityX);

        // Check for ground collision (example)
        if (playerImageView.getTranslateY() >= 500) { // Adjust ground level as needed
            playerImageView.setTranslateY(500);
            onGround = true;  // Player is on the ground
            velocityY = 0;    // Reset vertical velocity
        }
    }

    public void jump() {
        if (onGround) {
            velocityY = jumpStrength; // Set jump velocity
            onGround = false;          // Player is now in the air
        }
    }

    public void move(double deltaX) {
        velocityX = deltaX; // Set the horizontal velocity
    }

    public void stopMoving() {
        velocityX = 0; // Stop horizontal movement
    }

    public void setOnGround(boolean onGround) {
        this.onGround = onGround;
    }
}
