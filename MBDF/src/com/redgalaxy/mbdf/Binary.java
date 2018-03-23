package com.redgalaxy.mbdf;

import java.util.ArrayList;

public class Binary {
    private byte[] bin;
    private int len;
    public Binary(byte[] bin, int len) {
        this.bin = bin;
        this.len = len;
    }

    public boolean getBit(int index) {
        byte a = getByte(index);
        switch(index%8) {
            case 0: return (a & 128) > 0;
            case 1: return (a & 64) > 0;
            case 2: return (a & 32) > 0;
            case 3: return (a & 16) > 0;
            case 4: return (a & 8) > 0;
            case 5: return (a & 4) > 0;
            case 6: return (a & 2) > 0;
            case 7: return (a & 1) > 0;
            default: return false;
        }
    }

    private byte getByte(int index) {
        return bin[ Math.floorDiv( index, 8 ) ];
    }

    private void setByte(int index, byte b) {
        bin[ Math.floorDiv( index, 8 ) ] = b;
    }

    public int getLen() {
        return len;
    }

    public byte[] getByteArray() {
        return new Builder(this).build().bin;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        for( int i = 0; i < len; i++ ) {
            builder.append(getBit( i ) ? "1" : "0");
        }
        return builder.toString().substring(0, len);
    }

    public static byte getByteFromBinary(String binary){
        return (byte) Integer.parseInt(binary, 2);
    }

    public class BinariesDifferInLengthException extends Exception {
        private final String message;
        public BinariesDifferInLengthException(String operation, int len1, int len2) {
            message = "Could not do '"+operation+"' operation on two binaries with different lengths (length 1: "+len1+", length 2: "+len2+")";
        }

        @Override
        public String getMessage() {
            return message;
        }
    }

    public Binary or( Binary other ) throws BinariesDifferInLengthException {
        if( len != other.len ) {
            throw new BinariesDifferInLengthException("or", len, other.len);
        }

        byte[] newbin = new byte[len];

        for( int i = 0; i < bin.length; i++ ) {
            newbin[ i ] = (byte) (bin[i] | other.bin[i]);
        }

        return new Binary(newbin, len);
    }

    public Binary and( Binary other ) throws BinariesDifferInLengthException {
        if( len != other.len ) {
            throw new BinariesDifferInLengthException("and", len, other.len);
        }

        byte[] newbin = new byte[len];

        for( int i = 0; i < bin.length; i++ ) {
            newbin[ i ] = (byte) (bin[i] & other.bin[i]);
        }

        return new Binary(newbin, len);
    }

    public Binary xor( Binary other ) throws BinariesDifferInLengthException {
        if( len != other.len ) {
            throw new BinariesDifferInLengthException("xor", len, other.len);
        }

        byte[] newbin = new byte[len];

        for( int i = 0; i < bin.length; i++ ) {
            newbin[ i ] = (byte) (bin[i] ^ other.bin[i]);
        }

        return new Binary(newbin, len);
    }

    public Binary not() {

        byte[] newbin = new byte[len];

        for( int i = 0; i < bin.length; i++ ) {
            newbin[ i ] = (byte) (~bin[i]);
        }

        return new Binary(newbin, len);
    }

    public void setBit(int index, boolean value) {
        byte a = getByte(index);
        if( value ) {
            a = (byte) (a | (1 << 7 - (index % 8)));
        } else {
            a = (byte) (a & ~(1 << 7 - (index % 8)));
        }
        setByte(index, a);
    }

    public boolean equals(Binary other) {
        if(len != other.len) return false;
        for( int i = 0; i < bin.length; i ++ ) {
            if(bin[i] != other.bin[i]){
                return false;
            }
        }
        return true;
    }

    public Binary clone() {
        try{
            super.clone();
        } catch(CloneNotSupportedException ignored) {}
        return new Binary(bin.clone(), len);
    }

    public Binary subBit(int start) {
        if(start < 0 || start > len + 1) throw new IndexOutOfBoundsException("Binary index out of bounds: " + start);
        if(start == 0) return clone();
        if(start == len + 1) return new Binary(new byte[0], 0);
        byte[] bytes = new byte[Math.floorDiv(len - start, 8) + 1];
        Binary b = new Binary(bytes, len - start);
        for( int i = start; i < len; i ++ ) {
            b.setBit(i - start, getBit(i));
        }
        return b;
    }

    public Binary subBit(int start, int end) {
        if(start < 0 || start > len + 1) throw new IndexOutOfBoundsException("Binary index out of bounds: " + start);
        if(end < 0 || end > len + 1) throw new IndexOutOfBoundsException("Binary index out of bounds: " + end);
        if(start > end) throw new IndexOutOfBoundsException("Binary start index "+start+" is more than binary end index "+end+".");
        if(start == 0 && end == len) return clone();
        if(start == end) return new Binary(new byte[0], 0);
        byte[] bytes = new byte[Math.floorDiv(end - start, 8) + 1];
        Binary b = new Binary(bytes, end - start);
        for( int i = start; i < end; i ++ ) {
            b.setBit(i - start, getBit(i));
        }
        return b;
    }

    public Binary concat(Binary other) {
        byte[] bytes = new byte[Math.floorDiv(len + other.len, 8) + 1];
        Binary b = new Binary(bytes, len + other.len);
        for( int i = 0; i < len; i ++ ) {
            b.setBit(i, getBit(i));
        }
        for( int i = 0; i < other.len; i ++ ) {
            b.setBit(i + len, other.getBit(i));
        }
        return b;
    }

    public static class Builder {
        private ArrayList<Boolean> bools;
        public Builder() {
            bools = new ArrayList<>();
        }
        public Builder(boolean... booleans) {
            bools = new ArrayList<>();
            append( booleans );
        }
        public Builder(String bits) {
            bools = new ArrayList<>();
            append( bits );
        }
        public Builder(Binary bits) {
            bools = new ArrayList<>();
            append( bits );
        }
        public Builder append(boolean... bits) {
            for ( boolean b : bits ) {
                bools.add(b);
            }
            return this;
        }
        public Builder prepend(boolean... bits) {
            for ( int i = 0; i < bits.length; i ++ ) {
                bools.add(i, bits[ i ]);
            }
            return this;
        }
        public Builder append(String bits) {
            for ( int i = 0; i < bits.length(); i ++ ) {
                bools.add(bits.charAt( i ) == '1');
            }
            return this;
        }
        public Builder prepend(String bits) {
            for ( int i = 0; i < bits.length(); i ++ ) {
                bools.add(i, bits.charAt( i ) == '1');
            }
            return this;
        }
        public Builder append(Binary bits) {
            for ( int i = 0; i < bits.len; i ++ ) {
                bools.add(bits.getBit( i ));
            }
            return this;
        }
        public Builder prepend(Binary bits) {
            for ( int i = 0; i < bits.len; i ++ ) {
                bools.add(i, bits.getBit( i ));
            }
            return this;
        }
        public Binary build() {
            byte[] bytes = bools.size() % 8 == 0 ? new byte[Math.floorDiv(bools.size(), 8)] : new byte[Math.floorDiv(bools.size(), 8) + 1];
            Binary bin = new Binary(bytes, bools.size());
            for( int i = 0; i < bools.size(); i ++ ) {
                bin.setBit(i, bools.get( i ));
            }
            return bin;
        }
        public int length() {
            return bools.size();
        }
    }
}
