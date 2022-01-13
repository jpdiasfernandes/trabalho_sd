package frames;

public class ReplySerializerFrame {
    public Short tag;
    public byte error;
    public int size;
    public byte[] data;

    public ReplySerializerFrame(Short tag, byte error, int size, byte[] data) {
        this.tag = tag;
        this.error = error;
        this.size = size;
        this.data = data;
    }
}
