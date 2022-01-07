package client.business;

public class Flight {
    private String orig;
    private String dest;

    public Flight(String orig, String dest) {
        this.orig = orig;
        this.dest = dest;
    }

    public String getOrig() {
        return orig;
    }

    public void setOrig(String orig) {
        this.orig = orig;
    }

    public String getDest() {
        return dest;
    }

    public void setDest(String dest) {
        this.dest = dest;
    }

    public Flight clone(){
        return new Flight(this.orig,this.dest);
    }
}
