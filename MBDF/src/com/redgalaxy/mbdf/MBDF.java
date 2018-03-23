package com.redgalaxy.mbdf;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class MBDF {

    private String data;
    private InfoTag tag;

    public MBDF(String data) {
        this.tag = new InfoTag((byte) data.charAt(0));
        this.data = data.substring(1);
    }

    public MBDF(String data, InfoTag tag) {
        this.tag = tag;
        this.data = data;
    }

    public MBDFBinary getBinaryObject() throws IOException {
        String uncompressed = data;
        if (tag.isCompressed) {
            uncompressed = GZipUtils.decompress(data);
        }
        Binary binary = getBinaryFrom8Bit(uncompressed);
        return new MBDFBinary(binary.subBit(0, binary.getLen() - tag.trailing));
    }

    public static Binary getBinaryFrom8Bit(String s8bit) {
        try {
            byte[] bytes = s8bit.getBytes("ISO-8859-1");
            return new Binary(bytes, bytes.length * 8);
        } catch( UnsupportedEncodingException ignored ) {
            // This is not gonna happen because encoding 'ISO-8859-1' is always supported.
            return new Binary(new byte[0], 0);
        }
    }

    public static String get8BitFromBinary(Binary binary) {
        try {
            return new String(binary.getByteArray(), "ISO-8859-1");
        } catch( UnsupportedEncodingException ignored ) {
            // This is not gonna happen because encoding 'ISO-8859-1' is always supported.
            return "";
        }
    }

    /*
     * Adds leading zeroes to the binary string, so that the final amount of bits is 16
     */
    private static String addLeadingZeroes(String bin, boolean is16) {
        int len = bin.length();
        long amount = (long) (is16 ? 16 : 8) - len;

        // Create zeroes and append binary string
        StringBuilder zeroes = new StringBuilder();
        for( int i = 0; i < amount; i ++ ) {
            zeroes.append(0);
        }
        zeroes.append(bin);

        return zeroes.toString();
    }

    public String pack(){
        return tag.getFilePrefixChar() + data;
    }

    public String getData() {
        return data;
    }

    public InfoTag getTag() {
        return tag;
    }

}
