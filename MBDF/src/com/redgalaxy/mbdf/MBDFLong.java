package com.redgalaxy.mbdf;

public class MBDFLong implements IMBDFType<Long> {

    public static final EBinaryType TYPE = EBinaryType.INTEGER;

    private long value;

    public MBDFLong(long value){
        this.value = value;
    }

    @Override
    public Long getValue(){
        return value;
    }

    @Override
    public MBDFBinary encode() {

        // Compute binary value
        Binary.Builder bin = new Binary.Builder(Long.toBinaryString(Math.abs(value)));

        // Apply leading zeroes
        int bestPowOf2 = getBestPowerOfTwo(bin.length());
        bin = addLeadingZeroes(bin, Math.round(Math.pow(2, bestPowOf2)));

        // Make the negative flag
        boolean isNegative = value < 0;

        bin.prepend(addLeadingZeroes(Integer.toBinaryString(bestPowOf2)));
        bin.prepend(isNegative);
        bin.prepend(EBinaryType.INTEGER.binaryPrefix);

        return new MBDFBinary(bin.build());
    }

    @Override
    public Binary encodeBinary() {

        // Compute binary value
        Binary.Builder bin = new Binary.Builder(Long.toBinaryString(Math.abs(value)));

        // Apply leading zeroes
        int bestPowOf2 = getBestPowerOfTwo(bin.length());
        bin = addLeadingZeroes(bin, Math.round(Math.pow(2, bestPowOf2)));

        // Make the negative flag
        boolean isNegative = value < 0;

        bin.prepend(addLeadingZeroes(Integer.toBinaryString(bestPowOf2)));
        bin.prepend(isNegative);
        bin.prepend(EBinaryType.INTEGER.binaryPrefix);

        return bin.build();
    }

    public Binary encodeBinaryRaw() {

        // Compute binary value
        Binary.Builder bin = new Binary.Builder(Long.toBinaryString(Math.abs(value)));

        // Apply leading zeroes
        int bestPowOf2 = getBestPowerOfTwo(bin.length());
        bin = addLeadingZeroes(bin, Math.round(Math.pow(2, bestPowOf2)));

        bin.prepend(addLeadingZeroes(Integer.toBinaryString(bestPowOf2)));

        return bin.build();
    }


    /*
     * Finds the best power of two for an integer to store it in
     */
    private static int getBestPowerOfTwo(int i) {
        boolean isPowOf2 = i > 0 && ((i & (i - 1)) == 0);
        return i == 0 ? 0 : isPowOf2 ? 31 - Integer.numberOfLeadingZeros(i) : 32 - Integer.numberOfLeadingZeros(i);
    }

    private static Binary.Builder addLeadingZeroes(Binary.Builder bin, long a) {
        int len = bin.length();
        long amount = a - len;

        // Create zeroes and append binary string
        StringBuilder zeroes = new StringBuilder();
        for( int i = 0; i < amount; i ++ ) {
            zeroes.append(0);
        }
        bin.prepend(zeroes.toString());

        return bin;
    }

    private static String addLeadingZeroes(String bin) {
        int len = bin.length();
        long amount = 3 - len;

        // Create zeroes and append binary string
        StringBuilder zeroes = new StringBuilder();
        for( int i = 0; i < amount; i ++ ) {
            zeroes.append(0);
        }
        zeroes.append(bin);

        return zeroes.toString();
    }

    public static class MBDFIntegerInvalidException extends MBDFInvalidException {
        public MBDFIntegerInvalidException(Binary bin, String msg){
            super("integer", bin, msg);
        }
    }

    public static MBDFLong parseFromBinary(Binary binary) throws MBDFIntegerInvalidException {
        // MBDF integer binaries are at least five bits long, without binary prefix:
        // A negative flag, three bits indicating size and at least one bit representing the value
        if(binary.getLen() < 5) throw new MBDFIntegerInvalidException(binary, "Binary is too short: Integer binaries are at least 5 bits long.");

        boolean negative = binary.getBit(0);

        Binary sizeIndicator = binary.subBit(1, 4);
        int size = (int) Math.round(Math.pow(2, Integer.parseInt(sizeIndicator.toString(), 2)));

        Binary bits = binary.subBit(4);
        if(bits.getLen() != size){
            throw new MBDFIntegerInvalidException(binary, "Actual binary length does not compare to given binary length (Read "+size+", found "+bits.getLen()+").");
        }

        return new MBDFLong( (negative ? -1 : 1) * Long.parseLong(bits.toString(), 2) );
    }

    public static long parseNonNegativeFromBinary(Binary binary) throws MBDFIntegerInvalidException {
        // MBDF non-negative integer binaries are at least four bits long, without binary prefix:
        // Three bits indicating size and at least one bit representing the value
        if(binary.getLen() < 4) throw new MBDFIntegerInvalidException(binary, "Binary is too short: Integer binaries are at least 5 bits long.");

        Binary sizeIndicator = binary.subBit(0, 3);
        int size = (int) Math.round(Math.pow(2, Integer.parseInt(sizeIndicator.toString(), 2)));

        Binary bits = binary.subBit(3);
        if(bits.getLen() != size){
            throw new MBDFIntegerInvalidException(binary, "Actual binary length does not compare to given binary length (Read "+size+", found "+bits.getLen()+").");
        }

        return Long.parseLong(bits.toString(), 2);
    }

    public String toString() {
        return "INTEGER " + value;
    }
}
