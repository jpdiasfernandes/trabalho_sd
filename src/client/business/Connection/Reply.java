package client.business.Connection;

public class Reply {
    private Short tag;
    private byte error;
    private int dataSize;
    private byte[] data;

    public Reply(Short tag, byte error, int dataSize, byte[] data) {
        this.tag = tag;
        this.error = error;
        this.dataSize = dataSize;
        this.data = data;
    }

    public Short getTag() {
        return tag;
    }

    public byte getError() {
        return error;
    }

    public int getDataSize() {
        return dataSize;
    }

    public byte[] getData() {
        return data;
    }
}
