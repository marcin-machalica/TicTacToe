package machalica.marcin.tictactoe.communication.game;

public class AssignGameTableMessage extends GameMessage {
    private int gameTableId;

    public AssignGameTableMessage(int gameTableId) {
        this.gameTableId = gameTableId;
    }

    public int getGameTableId() {
        return gameTableId;
    }
}