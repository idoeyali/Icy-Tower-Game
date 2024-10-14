package GameObjects;

public class Eric extends Player{
    private static final double ERIC_SPEED = 8;
    private static final double ERIC_JUMP_STRENGTH = -22;

    public Eric(double startX, double startY, double screenWidth) {
        super(startX, startY, ERIC_SPEED, ERIC_JUMP_STRENGTH,screenWidth);
        initializeImage("southpark_avatar.png", startX, startY);
    }
}
