package Collisions;
import Game.IceTowerGame;

// Increases the score by a specified amount
public class IncreaseScoreStrategy implements CollisionStrategy {
    private final int amount;

    public IncreaseScoreStrategy(int amount) {
        this.amount = amount;
    }

    @Override
    public void onCollision(IceTowerGame game) {
        game.addScore(amount);
    }
}