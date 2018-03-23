package com.redgalaxy.mbdf;

public class MBDFFloat implements IMBDFType<Float> {

    private float value;
    public static final EBinaryType TYPE = EBinaryType.FLOAT_32;

    public MBDFFloat(float value) {
        this.value = value;
    }

    @Override
    public Float getValue() {
        return value;
    }

    @Override
    public MBDFBinary encode() {
        return new MBDFBinary(
                new Binary.Builder(TYPE.binaryPrefix)
                        .append(addLeadingZeroes(Integer.toBinaryString(Float.floatToIntBits(value))))
                        .build()
        );
    }

    @Override
    public Binary encodeBinary() {
        return new Binary.Builder(TYPE.binaryPrefix)
                        .append(addLeadingZeroes(Integer.toBinaryString(Float.floatToIntBits(value))))
                        .build();
    }

    private static String addLeadingZeroes(String bin) {
        int len = bin.length();
        long amount = (long) 32 - len;

        // Create zeroes and append binary string
        StringBuilder zeroes = new StringBuilder();
        for( int i = 0; i < amount; i ++ ) {
            zeroes.append(0);
        }
        zeroes.append(bin);

        return zeroes.toString();
    }

    public static class MBDFFloat32InvalidException extends MBDFInvalidException {
        public MBDFFloat32InvalidException(Binary bin, String msg){
            super("float-32", bin, msg);
        }
    }

    public static MBDFFloat parseFromBinary(Binary binary) throws MBDFFloat32InvalidException {
        if(binary.getLen() < 32) throw new MBDFFloat32InvalidException(binary, "Binary is too short: Float-32 binaries are exactly 32 bits long.");
        if(binary.getLen() > 32) throw new MBDFFloat32InvalidException(binary, "Binary is too long: Float-32 binaries are exactly 32 bits long.");

        String str = binary.toString();
        return new MBDFFloat( Float.intBitsToFloat(Integer.parseInt((str.charAt(0) == '1' ? "-" : "") + str.substring(1), 2)) );
    }

    public String toString() {
        return "FLOAT_32 " + value;
    }
}
