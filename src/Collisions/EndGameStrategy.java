package Collisions;

import Game.IceTowerGame;
// Ends the game
public class EndGameStrategy implements CollisionStrategy {
    @Override
    public void onCollision(IceTowerGame game) {
        game.endGame(game.getPrimaryStage());
    }
}