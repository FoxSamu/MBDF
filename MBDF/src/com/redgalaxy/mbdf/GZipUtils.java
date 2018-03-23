package com.redgalaxy.mbdf;

import java.io.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class GZipUtils {
    public static String compress(String str) throws IOException {
        if (str == null || str.length() == 0) {
            return str;
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        GZIPOutputStream gzip = new GZIPOutputStream(out);
//        String[] strs = str.split("\\n");
//        for( int i = 0; i<strs.length; i ++ ) {
//            String st = strs[i];
//            gzip.write(st.getBytes("ISO-8859-1"));
//        }
        gzip.write(str.getBytes("ISO-8859-1"));
        gzip.close();
        return out.toString("ISO-8859-1");
    }

    public static String decompress(String str) throws IOException {
        if (str == null || str.length() == 0) {
            return str;
        }
        GZIPInputStream gis = new GZIPInputStream(new ByteArrayInputStream(str.getBytes("ISO-8859-1")));
        BufferedReader bf = new BufferedReader(new InputStreamReader(gis, "ISO-8859-1"));
        StringBuilder outStr = new StringBuilder();
        String line;
        while ((line=bf.readLine())!=null) {
            outStr.append(line);
            outStr.append("\n");
        }
        outStr.deleteCharAt(outStr.length() - 1);
        return outStr.toString();
    }

//    public static String byteArrayToString(byte[] array) {
//        return new String( array );
//    }
//
//    public static byte[] stringToByteArray(String str) {
//        return str.getBytes();
//    }
}
