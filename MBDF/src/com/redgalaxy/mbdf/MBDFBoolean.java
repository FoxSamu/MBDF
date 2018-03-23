package com.redgalaxy.mbdf;

public class MBDFBoolean implements IMBDFType<Boolean> {

    private boolean value;
    public static final EBinaryType TYPE = EBinaryType.BOOLEAN;

    public MBDFBoolean(boolean value) {
        this.value = value;
    }

    @Override
    public Boolean getValue() {
        return value;
    }

    @Override
    public MBDFBinary encode() {
        return new MBDFBinary( new Binary.Builder( value ).prepend(TYPE.binaryPrefix).build() );
    }

    @Override
    public Binary encodeBinary() {
        return new Binary.Builder( value ).prepend(TYPE.binaryPrefix).build();
    }

    public static class MBDFBooleanInvalidException extends MBDFInvalidException {
        public MBDFBooleanInvalidException(Binary bin, String msg){
            super("boolean", bin, msg);
        }
    }

    public static MBDFBoolean parseFromBinary(Binary binary) throws MBDFBooleanInvalidException {
        // MBDF boolean binaries are always 1 bit long, without binary prefix
        if(binary.getLen() < 1) throw new MBDFBooleanInvalidException(binary, "Binary is too short: Boolean binaries are exactly 1 bit long.");
        if(binary.getLen() > 1) throw new MBDFBooleanInvalidException(binary, "Binary is too long: Boolean binaries are exactly 1 bit long.");

        return new MBDFBoolean( binary.getBit(0) );
    }

    public String toString() {
        return "BOOLEAN " + value;
    }
}
