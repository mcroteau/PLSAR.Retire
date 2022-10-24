package io.amadeus.model;

public class MediaPartial {
    byte[] bytes;

    public MediaPartial(byte[] bytes){
        this.bytes = bytes;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }
}
