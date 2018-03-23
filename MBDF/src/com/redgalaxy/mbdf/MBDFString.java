package com.redgalaxy.mbdf;

import java.io.UnsupportedEncodingException;
import java.util.Base64;

public class MBDFString implements IMBDFType<String> {

    private String value;
    public static final EBinaryType TYPE = EBinaryType.STRING;

    public MBDFString(String value) {
        this.value = value;
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public MBDFBinary encode() {
        Binary.Builder binary = new Binary.Builder();
        for( int i = 0; i < value.length(); i ++ ) {
            binary.append(addLeadingZeroes(Integer.toBinaryString(value.charAt(i))));
        }
        Binary len = new MBDFInteger(value.length()).encodeBinaryRaw();
        binary.prepend(len).prepend(true).prepend(TYPE.binaryPrefix);
        return new MBDFBinary(binary.build());
    }

    @Override
    public Binary encodeBinary() {
        Binary.Builder binary = new Binary.Builder();
        for( int i = 0; i < value.length(); i ++ ) {
            binary.append(addLeadingZeroes(Integer.toBinaryString(value.charAt(i))));
        }
        Binary len = new MBDFInteger(value.length()).encodeBinaryRaw();
        binary.prepend(len).prepend(true).prepend(TYPE.binaryPrefix);
        return binary.build();
    }

    public Binary encodeBinaryRaw() {
        Binary.Builder binary = new Binary.Builder();
        for( int i = 0; i < value.length(); i ++ ) {
            binary.append(addLeadingZeroes(Integer.toBinaryString(value.charAt(i))));
        }
        Binary len = new MBDFInteger(value.length()).encodeBinaryRaw();
        binary.prepend(len).prepend(true);
        return binary.build();
    }

    private static String addLeadingZeroes(String bin) {
        int len = bin.length();
        long amount = (long) 16 - len;

        // Create zeroes and append binary string
        StringBuilder zeroes = new StringBuilder();
        for( int i = 0; i < amount; i ++ ) {
            zeroes.append(0);
        }
        zeroes.append(bin);

        return zeroes.toString();
    }

    public static class MBDFStringInvalidException extends MBDFInvalidException {
        public MBDFStringInvalidException(Binary bin, String msg){
            super("string", bin, msg);
        }
    }

    public static MBDFString parseFromBinary(Binary binary) throws MBDFStringInvalidException {
        // MBDF string binaries are at least five bits long, without binary prefix
        if(binary.getLen() < 5) throw new MBDFStringInvalidException(binary, "Binary is too short: String binaries are at least 5 bits long.");

        boolean is16bit = binary.getBit(0);
        Binary lenSize = binary.subBit(1, 4);
        int size = (int) Math.round(Math.pow(2, Integer.parseInt(lenSize.toString(), 2)));
        Binary lenInfo;
        try {
            lenInfo = binary.subBit(1, 4+size);
        } catch(IndexOutOfBoundsException err) {
            throw new MBDFStringInvalidException(binary, "Binary is too short: Could not completely find length information, because the length integer does not have it's specified length ("+size+").");
        }

        long len;
        try {
            len = MBDFLong.parseNonNegativeFromBinary(lenInfo);
        } catch(MBDFLong.MBDFIntegerInvalidException exception) {
            throw new MBDFStringInvalidException(binary, "Length integer reading failed, causing this error: "+exception.getMessage());
        }

        long actualLen = len * (is16bit ? 16 : 8);

        Binary stringdata = binary.subBit(4+size);
        if ( stringdata.getLen() != actualLen ) {
            throw new MBDFStringInvalidException(binary, "Amount of bits that should be read as characters does not compare to the found string length. (Read "+stringdata.getLen()+" bits, found "+actualLen+" bits). \n Tip: Amount of bits is always divisible by 8 (and by 16 if the string is 16 bits)");
        }

        if(actualLen == 0) {
            return new MBDFString( "" );
        }



        try {
            return new MBDFString(new String(stringdata.getByteArray(), is16bit ? "UTF-16" : "UTF-8"));
        } catch(UnsupportedEncodingException err) {
            // Not gonna happen because both 'UTF-8' and 'UTF-16' are supported
            throw new MBDFStringInvalidException(binary, "Not happening exception...");
        }

    }

    public String toString() {
        return "STRING '" + value + "'";
    }
}
