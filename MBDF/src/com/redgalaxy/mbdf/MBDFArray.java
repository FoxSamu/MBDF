package com.redgalaxy.mbdf;


public class MBDFArray implements IMBDFType<TypeArray> {

    private TypeArray value;

    public static final EBinaryType TYPE = EBinaryType.ARRAY;

    public MBDFArray(){
        value = new TypeArray();
    }

    public MBDFArray(TypeArray array){
        value = array;
    }

    public MBDFArray(IMBDFType[] types){
        value = new TypeArray(types);
    }

    @Override
    public TypeArray getValue() {
        return value;
    }

    @Override
    public MBDFBinary encode() {
        Binary len = new MBDFInteger(value.length()).encodeBinaryRaw();
        Binary.Builder encodedEntries = new Binary.Builder();
        for( int i = 0; i < value.length(); i ++ ) {
            encodedEntries.append(value.get( i ).encodeBinary());
        }
        encodedEntries.prepend(len).prepend(TYPE.binaryPrefix);
        return new MBDFBinary( encodedEntries.build() );
    }

    @Override
    public Binary encodeBinary() {
        Binary len = new MBDFInteger(value.length()).encodeBinaryRaw();
        Binary.Builder encodedEntries = new Binary.Builder();
        for( int i = 0; i < value.length(); i ++ ) {
            encodedEntries.append(value.get( i ).encodeBinary());
        }
        encodedEntries.prepend(len).prepend(TYPE.binaryPrefix);
        return encodedEntries.build();
    }

    public static class MBDFArrayInvalidException extends MBDFInvalidException {
        public MBDFArrayInvalidException(Binary bin, String msg){
            super("array", bin, msg);
        }
    }

    private static int getLengthFromIntegerData(Binary id) {
        return (int) Math.floor(Math.pow(2, Integer.parseInt(id.subBit(1, 4).toString(), 2))) + 4;
    }

    private static int getLengthFromFloat32Data() {
        return 32;
    }

    private static int getLengthFromFloat64Data() {
        return 64;
    }

    private static int getLengthFromBooleanData() {
        return 1;
    }

    private static int getLengthFromNullData() {
        return 0;
    }

    public static int getLengthFromStringData(Binary binary) throws MBDFArrayInvalidException {
        // MBDF string binaries are at least five bits long, without binary prefix
        if(binary.getLen() < 5) return binary.getLen();

        boolean is16bit = binary.getBit(0);
        Binary lenSize = binary.subBit(1, 4);
        int size = (int) Math.round(Math.pow(2, Integer.parseInt(lenSize.toString(), 2)));
        Binary lenInfo;
        try {
            lenInfo = binary.subBit(1, 4+size);
        } catch(IndexOutOfBoundsException err) {
            return binary.getLen();
        }

        long len;
        try {
            len = MBDFLong.parseNonNegativeFromBinary(lenInfo);
        } catch(MBDFLong.MBDFIntegerInvalidException exception) {
            throw new MBDFArrayInvalidException(binary, "Length integer reading failed, causing this error: "+exception.getMessage());
        }

        long actualLen = len * (is16bit ? 16 : 8);

        return (int) (4 + size + actualLen);
    }

    private static int getLengthFromArrayData(Binary binary) throws MBDFArrayInvalidException {
        Binary lenSize = binary.subBit(0, 3);
        int size = (int) Math.round(Math.pow(2, Integer.parseInt(lenSize.toString(), 2)));
        Binary lenInfo;
        try {
            lenInfo = binary.subBit(0, 3+size);
        } catch(IndexOutOfBoundsException err) {
            return binary.getLen();
        }

        long len;
        try {
            len = MBDFLong.parseNonNegativeFromBinary(lenInfo);
        } catch(MBDFLong.MBDFIntegerInvalidException exception) {
            throw new MBDFArrayInvalidException(binary, "Length integer reading failed, causing this error: "+exception.getMessage());
        }

        try {
            int start = 3+size;
            for( int i = 0; i < len; i ++ ) {
                // Read 3 bits for type
                EBinaryType type = EBinaryType.getFromBinaryPrefix(binary.subBit(start, start + 3));
                if(type == EBinaryType.INTEGER) {
                    start += getLengthFromIntegerData(binary.subBit(start + 3));
                }
                if(type == EBinaryType.FLOAT_32) {
                    start += getLengthFromFloat32Data();
                }
                if(type == EBinaryType.FLOAT_64) {
                    start += getLengthFromFloat64Data();
                }
                if(type == EBinaryType.BOOLEAN) {
                    start += getLengthFromBooleanData();
                }
                if(type == EBinaryType.NULL) {
                    start += getLengthFromNullData();
                }
                if(type == EBinaryType.STRING) {
                    start += getLengthFromStringData(binary.subBit(start + 3));
                }
                if(type == EBinaryType.ARRAY) {
                    start += getLengthFromArrayData(binary.subBit(start + 3));
                }
                if(type == EBinaryType.COMPOUND) {
                    start += getLengthFromCompoundData(binary.subBit(start + 3));
                }
                start += 3;
            }
            return start;
        } catch(IndexOutOfBoundsException err ) {
            return binary.getLen();
        }
    }

    private static int getLengthFromCompoundData(Binary binary) throws MBDFArrayInvalidException {
        Binary lenSize = binary.subBit(0, 3);
        int size = (int) Math.round(Math.pow(2, Integer.parseInt(lenSize.toString(), 2)));
        Binary lenInfo;
        try {
            lenInfo = binary.subBit(0, 3+size);
        } catch(IndexOutOfBoundsException err) {
            return binary.getLen();
        }

        long len;
        try {
            len = MBDFLong.parseNonNegativeFromBinary(lenInfo);
        } catch(MBDFLong.MBDFIntegerInvalidException exception) {
            throw new MBDFArrayInvalidException(binary, "Length integer reading failed, causing this error: "+exception.getMessage());
        }

        try {
            int start = 3 + size;
            for (int i = 0; i < len; i++) {
                // Key
                start += getLengthFromStringData(binary.subBit(start));
                // Read 3 bits for type
                EBinaryType type = EBinaryType.getFromBinaryPrefix(binary.subBit(start, start + 3));
                if (type == EBinaryType.INTEGER) {
                    start += getLengthFromIntegerData(binary.subBit(start + 3));
                }
                if (type == EBinaryType.FLOAT_32) {
                    start += getLengthFromFloat32Data();
                }
                if (type == EBinaryType.FLOAT_64) {
                    start += getLengthFromFloat64Data();
                }
                if (type == EBinaryType.BOOLEAN) {
                    start += getLengthFromBooleanData();
                }
                if (type == EBinaryType.NULL) {
                    start += getLengthFromNullData();
                }
                if (type == EBinaryType.STRING) {
                    start += getLengthFromStringData(binary.subBit(start + 3));
                }
                if (type == EBinaryType.ARRAY) {
                    start += getLengthFromArrayData(binary.subBit(start + 3));
                }
                if (type == EBinaryType.COMPOUND) {
                    start += getLengthFromCompoundData(binary.subBit(start + 3));
                }
                start += 3;
            }

            return start;
        } catch(IndexOutOfBoundsException err ) {
            return binary.getLen();
        }
    }

    public static MBDFArray parseFromBinary(Binary binary) throws MBDFInvalidException {
        // MBDF array binaries are at least four bits long, without binary prefix
        if(binary.getLen() < 4) throw new MBDFArrayInvalidException(binary, "Binary is too short: Array binaries are at least 4 bits long.");

        Binary lenSize = binary.subBit(0, 3);
        int size = (int) Math.round(Math.pow(2, Integer.parseInt(lenSize.toString(), 2)));
        Binary lenInfo;
        try {
            lenInfo = binary.subBit(0, 3+size);
        } catch(IndexOutOfBoundsException err) {
            throw new MBDFArrayInvalidException(binary, "Binary is too short: Could not completely find length information, because the length integer does not have it's specified length ("+size+").");
        }

        long len;
        try {
            len = MBDFLong.parseNonNegativeFromBinary(lenInfo);
        } catch(MBDFLong.MBDFIntegerInvalidException exception) {
            throw new MBDFArrayInvalidException(binary, "Length integer reading failed, causing this error: "+exception.getMessage());
        }

        int start = size+3;
        TypeArray parsed = new TypeArray();
        for( int i = 0; i < len; i ++ ) {

            // Read 3 bits for type
            EBinaryType type = EBinaryType.getFromBinaryPrefix(binary.subBit(start, start + 3));
//            start += 3;
            if(type == EBinaryType.INTEGER) {
                int inc = start + getLengthFromIntegerData(binary.subBit(start + 3)) + 3;
                MBDFLong mbdftype = MBDFLong.parseFromBinary(binary.subBit(start + 3, inc));
                parsed.add( mbdftype );
                start = inc;
            }
            if(type == EBinaryType.FLOAT_32) {
                int inc = start + getLengthFromFloat32Data() + 3;
                MBDFFloat mbdftype = MBDFFloat.parseFromBinary(binary.subBit(start + 3, inc));
                parsed.add( mbdftype );
                start = inc;
            }
            if(type == EBinaryType.FLOAT_64) {
                int inc = start + getLengthFromFloat64Data() + 3;
                MBDFDouble mbdftype = MBDFDouble.parseFromBinary(binary.subBit(start + 3, inc));
                parsed.add( mbdftype );
                start = inc;
            }
            if(type == EBinaryType.BOOLEAN) {
                int inc = start + getLengthFromBooleanData() + 3;
                MBDFBoolean mbdftype = MBDFBoolean.parseFromBinary(binary.subBit(start + 3, inc));
                parsed.add( mbdftype );
                start = inc;
            }
            if(type == EBinaryType.NULL) {
                int inc = start + getLengthFromNullData() + 3;
                MBDFNull mbdftype = MBDFNull.parseFromBinary(binary.subBit(start + 3, inc));
                parsed.add( mbdftype );
                start = inc;
            }
            if(type == EBinaryType.STRING) {
                int inc = start + getLengthFromStringData(binary.subBit(start + 3)) + 3;
                MBDFString mbdftype = MBDFString.parseFromBinary(binary.subBit(start + 3, inc));
                parsed.add( mbdftype );
                start = inc;
            }
            if(type == EBinaryType.ARRAY) {
                int inc = start + getLengthFromArrayData(binary.subBit(start + 3)) + 3;
                MBDFArray mbdftype = parseFromBinary(binary.subBit(start + 3, inc));
                parsed.add( mbdftype );
                start = inc;
            }
            if(type == EBinaryType.COMPOUND) {
                int inc = start + getLengthFromCompoundData(binary.subBit(start + 3)) + 3;
                MBDFCompound mbdftype = MBDFCompound.parseFromBinary(binary.subBit(start + 3, inc));
                parsed.add( mbdftype );
                start = inc;
            }
        }

        return new MBDFArray( parsed );
    }

    public String toString() {
        return "ARRAY " + value.toString();
    }

}
