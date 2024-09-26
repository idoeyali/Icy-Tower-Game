package GameObjects;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public abstract class Player {
    protected static final double PLAYER_WIDTH =70;
    protected static final double PLAYER_HEIGHT =70;
    private ImageView playerImageView;  // Visual representation of the player
    private double velocityY;// Vertical velocity for jumping/falling
    private double speed;
    private final double gravity;               // Gravity effect
    private double jumpStrength;          // Strength of the jump
    private boolean onGround;             // Flag to check if the player is on the ground
    private double velocityX;
    private final double screenWidth;// Horizontal velocity


    // Constructor
    public Player(double startX, double startY, double speed, double jumpStrength, double screenWidth) {
        this.speed = speed;
        this.jumpStrength = jumpStrength;
        this.screenWidth = screenWidth;
        this.gravity = 1.0;
        this.onGround = false;
        this.velocityY = 0;
        this.velocityX = 0;  // Initialize horizontal velocity
    }

    protected void initializeImage(String imageName, double startX, double startY) {
        Image playerImage = new Image(getClass().getResourceAsStream("/util/" + imageName));
        playerImageView = new ImageView(playerImage);
        playerImageView.setFitWidth(PLAYER_WIDTH);
        playerImageView.setFitHeight(PLAYER_HEIGHT);
        playerImageView.setTranslateX(startX);
        playerImageView.setTranslateY(startY);
    }

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
        if (playerImageView.getTranslateX() + PLAYER_WIDTH > screenWidth) {
            playerImageView.setTranslateX(screenWidth-PLAYER_WIDTH);
        }

    }

    public void jump() {
        if (onGround) {
            velocityY = jumpStrength; // Set jump velocity
            onGround = false;          // GameObjects.Player is now in the air
        }
    }

    public void move(int direction){
        velocityX = direction*speed;
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
