package com.redgalaxy.mbdf;

public class MBDFInteger implements IMBDFType<Integer> {

    public static final EBinaryType TYPE = EBinaryType.INTEGER;

    private int value;

    public MBDFInteger(int value){
        this.value = value;
    }

    @Override
    public Integer getValue(){
        return value;
    }

    @Override
    public MBDFBinary encode() {

        // Compute binary value
        Binary.Builder bin = new Binary.Builder(Integer.toBinaryString(Math.abs(value)));

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
        Binary.Builder bin = new Binary.Builder(Integer.toBinaryString(Math.abs(value)));

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
        Binary.Builder bin = new Binary.Builder(Integer.toBinaryString(Math.abs(value)));

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

    public String toString() {
        return "INTEGER " + value;
    }
}
