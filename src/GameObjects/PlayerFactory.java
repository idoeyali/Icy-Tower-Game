package GameObjects;

public class PlayerFactory {

    public Player createPlayer(String playerType, double startX, double startY, double screenWidth) {
        switch (playerType.toLowerCase()) {
            case "pikachu":
                return new Pikachu(startX, startY, screenWidth);
            case "bert":
                return new Bert(startX, startY, screenWidth);
            case "eric":
                return new Eric(startX, startY, screenWidth);
            default:
                throw new IllegalArgumentException("Unknown player type: " + playerType);
        }
    }
}
