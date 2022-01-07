package client.business.Connection;

public class Frame {
    private Short tag;
    private int size;
    private byte[] data;

    // construtor sem Tag serve apenas para o Stub que não precisa se preocupar com a gestão de TAG's
    // depois no send() será passado como parâmetro a TAG na qual esse frame será identificado
    public Frame(int size, byte[] data) {
        this.size = size;
        this.data = data;
    }

    //construtor com Tag serve apenas para poder coletar a TAG recebida na reply
    public Frame(Short tag, int size, byte[] data) {
        this.tag = tag;
        this.size = size;
        this.data = data;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public Short getTag() {
        return tag;
    }

    public void setTag(Short tag) {
        this.tag = tag;
    }
}
