package client.business.Connection;

public class Request {
    private Short tag;
    private byte opcode;
    private int dataSize;
    private byte[] data;

    // construtor sem Tag serve apenas para o Stub que não precisa se preocupar com a gestão de TAG's
    // depois no send() será passado como parâmetro a TAG na qual esse frame será identificado
    public Request(byte opcode, int dataSize, byte[] data) {
        this.opcode = opcode;
        this.dataSize = dataSize;
        this.data = data;
    }

    public Short getTag() {
        return tag;
    }

    public byte getOpcode() {
        return opcode;
    }

    public int getSize() {
        return dataSize;
    }

    public byte[] getData() {
        return data;
    }
}
