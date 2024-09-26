package GameObjects;

public class Pikachu extends Player {
    private static final double PIKACHU_SPEED = 12;
    private static final double PIKACHU_JUMP_STRENGTH = -20;

    public Pikachu(double startX, double startY, double screenWidth) {
        super(startX, startY, PIKACHU_SPEED, PIKACHU_JUMP_STRENGTH,screenWidth);
        initializeImage("pikachu_avatar.png", startX, startY);
    }

}
