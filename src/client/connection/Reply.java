package connection;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

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

    // Only for tests
    public byte[] deserialize(){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        try {
            dos.writeShort(tag);
            dos.writeByte(error);
            dos.writeInt(dataSize);

            if (dataSize != 0) {
                dos.write(data);
            }
            return baos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
