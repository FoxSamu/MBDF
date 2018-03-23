package com.redgalaxy.mbdf;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class MBDFFile {
    public static MBDF readMBDFFromFile(String filename) throws IOException {

//        FileInputStream is = new FileInputStream(filename);
//        InputStreamReader isr = new InputStreamReader(is, "ISO-8859-1");
//        BufferedReader br = new BufferedReader(isr);
//
//        StringBuilder builder = new StringBuilder();
//
//        String currentLine;
//
//        while ((currentLine = br.readLine()) != null) {
//            builder.append(currentLine);
//            builder.append("\n");
//        }
//
//        builder.deleteCharAt(builder.length() - 1);
//
//
//        br.close();

        Path path = Paths.get(filename);
        byte[] data = Files.readAllBytes(path);

        return new MBDF(new String(data, "ISO-8859-1"));
    }

    private static void writeToFile(String filename, byte[] txt) throws IOException {
//        BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
////        FileWriter writer = new FileWriter(filename);
//        writer.write(txt.getBytes("ISO-8859-1"));
//        writer.close();
//        PrintWriter pw = new PrintWriter(filename, "ISO-8859-1");
        FileOutputStream stream = new FileOutputStream(filename);
        stream.write(txt);
        stream.close();
    }

    public static void writeMBDFToFile(String filename, MBDF info) throws IOException {
        writeToFile(filename, info.pack().getBytes("ISO-8859-1"));
    }
}
