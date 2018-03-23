package com.redgalaxy.mbdf;

public enum EBinaryType {
    INTEGER(0, "000"),
    FLOAT_32(1, "001"),
    FLOAT_64(2, "010"),
    STRING(3, "011"),
    BOOLEAN(4, "100"),
    ARRAY(5, "101"),
    COMPOUND(6, "110"),
    NULL(7, "111");

    public final int dataType;
    public final String literalPrefix;
    public final Binary binaryPrefix;

    EBinaryType(int dtype, String bprefix) {
        dataType = dtype;
        literalPrefix = bprefix;
        binaryPrefix = new Binary.Builder(bprefix).build();
    }

    public static class BinaryPrefixNotFoundException extends RuntimeException {
        public BinaryPrefixNotFoundException(String str){
            super(str);
        }
    }

    public static EBinaryType getFromBinaryPrefix(Binary binaryPrefix) {
        if( binaryPrefix.equals(INTEGER.binaryPrefix) ) return INTEGER;
        if( binaryPrefix.equals(FLOAT_32.binaryPrefix) ) return FLOAT_32;
        if( binaryPrefix.equals(FLOAT_64.binaryPrefix) ) return FLOAT_64;
        if( binaryPrefix.equals(STRING.binaryPrefix) ) return STRING;
        if( binaryPrefix.equals(BOOLEAN.binaryPrefix) ) return BOOLEAN;
        if( binaryPrefix.equals(ARRAY.binaryPrefix) ) return ARRAY;
        if( binaryPrefix.equals(COMPOUND.binaryPrefix) ) return COMPOUND;
        if( binaryPrefix.equals(NULL.binaryPrefix) ) return NULL;
        throw new BinaryPrefixNotFoundException("Could not find type from binary prefix " + binaryPrefix);
    }
}
