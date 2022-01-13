package business;

public class Auth {
    private String token;
    private boolean isAdmin;

    public String getToken() {
        return token;
    }
    public boolean isAdmin(){return isAdmin;};

    public void setToken(String token){
        this.token = token;
    }
    public void setIsAdmin(boolean isAdmin){
        this.isAdmin = isAdmin;
    }

    private static Auth single_instance = null;

    public static Auth getInstance() {
        if (single_instance == null)
            single_instance = new Auth();

        return single_instance;
    }
}
