package com.redgalaxy.mbdf;

public class MBDFDouble implements IMBDFType<Double> {

    private double value;
    public static final EBinaryType TYPE = EBinaryType.FLOAT_64;

    public MBDFDouble(double value) {
        this.value = value;
    }

    @Override
    public Double getValue() {
        return value;
    }

    @Override
    public MBDFBinary encode() {
        return new MBDFBinary(
                new Binary.Builder(TYPE.binaryPrefix)
                        .append(addLeadingZeroes(Long.toBinaryString(Double.doubleToLongBits(value))))
                        .build()
        );
    }

    @Override
    public Binary encodeBinary() {
        return new Binary.Builder(TYPE.binaryPrefix)
                .append(addLeadingZeroes(Long.toBinaryString(Double.doubleToLongBits(value))))
                .build();
    }

    private static String addLeadingZeroes(String bin) {
        int len = bin.length();
        long amount = (long) 64 - len;

        // Create zeroes and append binary string
        StringBuilder zeroes = new StringBuilder();
        for( int i = 0; i < amount; i ++ ) {
            zeroes.append(0);
        }
        zeroes.append(bin);

        return zeroes.toString();
    }

    public static class MBDFFloat64InvalidException extends MBDFInvalidException {
        public MBDFFloat64InvalidException(Binary bin, String msg){
            super("float-64", bin, msg);
        }
    }

    public static MBDFDouble parseFromBinary(Binary binary) throws MBDFFloat64InvalidException {
        if(binary.getLen() < 64) throw new MBDFFloat64InvalidException(binary, "Binary is too short: Float-64 binaries are exactly 64 bits long.");
        if(binary.getLen() > 64) throw new MBDFFloat64InvalidException(binary, "Binary is too long: Float-64 binaries are exactly 64 bits long.");

        String str = binary.toString();
        return new MBDFDouble( Double.longBitsToDouble(Long.parseLong((str.charAt(0) == '1' ? "-" : "") + str.substring(1), 2)) );
    }

    public String toString() {
        return "FLOAT_64 " + value;
    }
}
