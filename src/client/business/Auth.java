package client.business;

public class Auth {
    private String token;

    public String getToken() {
        return token;
    }

    public void setToken(String token){
        this.token = token;
    }

    private static Auth single_instance = null;

    public static Auth getInstance() {
        if (single_instance == null)
            single_instance = new Auth();

        return single_instance;
    }
}
