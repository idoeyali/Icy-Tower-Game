package Collisions;

import Game.IceTowerGame;

/**
 * Represents a strategy for handling collisions between GameObjects.
 */
public interface CollisionStrategy {
    void onCollision(IceTowerGame game);
}

