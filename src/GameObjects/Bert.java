package GameObjects;

public class Bert extends Player {
    private static final double BERT_SPEED = 12;
    private static final double BERT_JUMP_STRENGTH = -20;

    public Bert(double startX, double startY, double screenWidth) {
        super(startX, startY, BERT_SPEED, BERT_JUMP_STRENGTH,screenWidth);
        initializeImage("bert_avatar.png", startX, startY);
    }

}