package businesslogic;

public enum Mode {
    IS(0), IX(1), S(2), X(3);

    private int id;
    Mode(int id) {
        this.id = id;
    }

    public int getID() {
        return id;
    }
}
