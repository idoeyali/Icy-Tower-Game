package GameObjects;

public class Pikachu extends Player {
    private static final double PIKACHU_SPEED = 13;
    private static final double PIKACHU_JUMP_STRENGTH = -19;

    public Pikachu(double startX, double startY, double screenWidth) {
        super(startX, startY, PIKACHU_SPEED, PIKACHU_JUMP_STRENGTH,screenWidth);
        initializeImage("pikachu_avatar.png", startX, startY);
    }

}
