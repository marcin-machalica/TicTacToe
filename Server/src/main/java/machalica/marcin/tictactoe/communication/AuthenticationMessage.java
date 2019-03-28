package machalica.marcin.tictactoe.communication;

public class AuthenticationMessage extends Message {
    private String login;
    private char[] password;

    public AuthenticationMessage(String login, char[] password) {
        this.login = login;
        this.password = password;
    }

    public String getLogin() {
        return login;
    }

    public char[] getPassword() {
        return password;
    }
}