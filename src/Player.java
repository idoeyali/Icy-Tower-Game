import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public abstract class Player {
    protected ImageView playerImageView;  // Visual representation of the player
    private double velocityY;             // Vertical velocity for jumping/falling
    private double gravity;               // Gravity effect
    private double jumpStrength;          // Strength of the jump
    private boolean onGround;             // Flag to check if the player is on the ground
    private double velocityX;
    private final double screenWidth;// Horizontal velocity


    // Constructor
    public Player(double startX, double startY, double screenWidth) {
        this.screenWidth = screenWidth;
        this.gravity = 1.0;
        this.jumpStrength = -20.0;
        this.onGround = false;
        this.velocityY = 0;
        this.velocityX = 0;  // Initialize horizontal velocity
    }

    protected abstract void initializeImage(double startX, double startY);

    public ImageView getImageView() {
        return playerImageView;
    }

    public void stopVerticalMovement() {
        if (velocityY > 0) { // Stop movement only when falling (positive velocity)
            velocityY = 0;
        }
    }

    public void applyGravity() {
        if (!onGround) {
            velocityY += gravity;
        }
    }

    public void stop() {
        velocityY = 0;
    }

    public void update() {
        // Update the player's position based on vertical and horizontal velocities
        playerImageView.setTranslateY(playerImageView.getTranslateY() + velocityY);
        playerImageView.setTranslateX(playerImageView.getTranslateX() + velocityX);
        playerOutOfBoundsHandler();
    }

    private void playerOutOfBoundsHandler() {
        if (playerImageView.getTranslateX() < 0) {
            playerImageView.setTranslateX(0);
        }
        if (playerImageView.getTranslateX() > screenWidth) {
            playerImageView.setTranslateX(screenWidth);
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

    public boolean isOnGround() {
        return onGround;
    }

    public void setOnGround(boolean onGround) {
        this.onGround = onGround;
    }

    public double getVelocityY() {
        return velocityY;
    }
}
