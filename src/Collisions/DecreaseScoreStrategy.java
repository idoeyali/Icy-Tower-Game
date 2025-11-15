package Collisions;
import Game.IceTowerGame;

// Decreases the score by a specified amount
public class DecreaseScoreStrategy implements CollisionStrategy {
    private final int amount;

    public DecreaseScoreStrategy(int amount) {
        this.amount = amount;
    }

    @Override
    public void onCollision(IceTowerGame game) {
        game.addScore(-amount);
    }
}