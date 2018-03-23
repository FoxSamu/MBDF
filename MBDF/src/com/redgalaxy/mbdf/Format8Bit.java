package com.redgalaxy.mbdf;

import java.io.IOException;

public class Format8Bit {
    public static String getBinary(String encoded) {
        return null;
    }

    public static String getEncoded(String binary) throws IOException {
        return new String( getBytesFromBinary(binary), "UTF-8" );
    }

    private static byte[] getBytesFromBinary(String binary) {
        byte[] ret = new byte[binary.length() / 8];
        for (int i = 0; i < ret.length; i++) {
            String chunk = binary.substring(i * 8, i * 8 + 8);
            ret[i] = Byte.parseByte(chunk, 2);
        }
        return ret;
    }
}
