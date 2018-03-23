package com.redgalaxy.mbdf;

public class MBDFNull implements IMBDFType<Object> {

    public static final EBinaryType TYPE = EBinaryType.NULL;

    @Override
    public MBDFBinary encode() {
        return new MBDFBinary(TYPE.binaryPrefix.clone());
    }

    @Override
    public Binary encodeBinary() {
        return TYPE.binaryPrefix.clone();
    }

    @Override
    public Object getValue() {
        return null;
    }



    public static class MBDFNullInvalidException extends MBDFInvalidException {
        public MBDFNullInvalidException(Binary bin, String msg){
            super("null", bin, msg);
        }
    }

    public static MBDFNull parseFromBinary(Binary binary) throws MBDFNullInvalidException {
        // MBDF boolean binaries are always 1 bit long, without binary prefix
        if(binary.getLen() > 0) throw new MBDFNullInvalidException(binary, "Binary is too long: Null binaries are exactly 0 bits long.");

        return new MBDFNull();
    }

    public String toString() {
        return "NULL null";
    }
}
