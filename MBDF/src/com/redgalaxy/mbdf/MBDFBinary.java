package com.redgalaxy.mbdf;

import java.io.IOException;

/**
 * Stores MBDF binary information as string.
 */
public class MBDFBinary {

    private Binary binary;
    public MBDFBinary(Binary binary) {
        this.binary = binary;
    }

    public Binary getBinary(){
        return binary;
    }

    public IMBDFType parse() throws MBDFInvalidException {
        // Read 3 bits for type
        EBinaryType type = EBinaryType.getFromBinaryPrefix(binary.subBit(0, 3));
        if(type == EBinaryType.INTEGER) {
            return MBDFLong.parseFromBinary(binary.subBit(3));
        }
        if(type == EBinaryType.FLOAT_32) {
            return MBDFFloat.parseFromBinary(binary.subBit(3));
        }
        if(type == EBinaryType.FLOAT_64) {
            return MBDFDouble.parseFromBinary(binary.subBit(3));
        }
        if(type == EBinaryType.BOOLEAN) {
            return MBDFBoolean.parseFromBinary(binary.subBit(3));
        }
        if(type == EBinaryType.NULL) {
            return MBDFNull.parseFromBinary(binary.subBit(3));
        }
        if(type == EBinaryType.STRING) {
            return MBDFString.parseFromBinary(binary.subBit(3));
        }
        if(type == EBinaryType.ARRAY) {
            return MBDFArray.parseFromBinary(binary.subBit(3));
        }
        if(type == EBinaryType.COMPOUND) {
            return MBDFCompound.parseFromBinary(binary.subBit(3));
        }

        // Not being thrown anyway, as every possible combination of three bits will lead to a valid type
        // Just for Java to complete return value
        throw new MBDFInvalidException("MBDF", binary, "Invalid type identifier: "+binary.subBit(0, 3));
    }

    public MBDF makeMBDF(boolean isCompressed) throws IOException {
        InfoTag tag = new InfoTag(isCompressed, binary.getByteArray().length * 8 - binary.getLen());
        String data = MBDF.get8BitFromBinary(binary);
        if(isCompressed) {
            data = GZipUtils.compress(data);
        }
        return new MBDF(data, tag);
    }

}
