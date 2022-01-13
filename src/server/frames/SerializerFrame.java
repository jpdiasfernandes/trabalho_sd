package server.frames;

public class SerializerFrame {
    public short tag;
    public int opCode;
    public int size;
    public byte[] data;

    public String token;

    public SerializerFrame(short tag, int opCode, int size, byte[] data) {
        this.tag = tag;
        this.opCode = opCode;
        this.size = size;
        this.data = data;
    }
}
