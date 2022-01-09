package business;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class Flight {
    private String orig;
    private String dest;
    private Short capacity;

    public Flight(String orig, String dest) {
        this.orig = orig;
        this.dest = dest;
    }

    public Flight(String orig, String dest, Short capacity) {
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

    byte[] serialize(){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        try {
            dos.writeUTF(orig);
            dos.writeUTF(dest);
            if (capacity != null) {
                dos.writeShort(capacity);
            }
            dos.flush();
            return baos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    // deserialize only origin and dest
    static Flight deserialize(DataInputStream dis){
        try {
            String o = dis.readUTF();
            String d = dis.readUTF();

            return new Flight(o,d);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
