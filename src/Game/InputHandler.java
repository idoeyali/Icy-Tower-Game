package Game;

import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import GameObjects.Player;

public class InputHandler {
    /**
     * Sets up the keyboard controls for the player character.
     * Handles key press events for player movement (left, right, jump).
     */
    public InputHandler(Scene scene, Player player) {
        scene.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case LEFT:
                    player.move(Constants.LEFT_DIRECTION);
                    break;
                case RIGHT:
                    player.move(Constants.RIGHT_DIRECTION);
                    break;
                case SPACE:
                    player.jump();
                    break;
                default:
                    break;
            }
        });
        scene.setOnKeyReleased(event -> {
            if (event.getCode() == KeyCode.LEFT || event.getCode() == KeyCode.RIGHT) {
                player.stopMoving();
            }
        });
    }
}
