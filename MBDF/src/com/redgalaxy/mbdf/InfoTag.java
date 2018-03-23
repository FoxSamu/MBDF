package com.redgalaxy.mbdf;

public class InfoTag {
    public final boolean isCompressed;
    public final int trailing;

    public InfoTag(byte info){

        isCompressed = ((int) info & 8) > 0;
        boolean b1 = ((int) info & 4) > 0;
        boolean b2 = ((int) info & 2) > 0;
        boolean b3 = ((int) info & 1) > 0;
        trailing = (b1 ? 4 : 0) + (b2 ? 2 : 0) + (b3 ? 1 : 0);
    }

    public InfoTag(boolean compress, int t){

        isCompressed = compress;
        trailing = t;
    }

    public String getFilePrefixChar() {
        Binary.Builder b = new Binary.Builder();
        b.append(false, false, false, false);
        b.append(isCompressed);
        boolean b1 = (trailing & 4) > 0;
        boolean b2 = (trailing & 2) > 0;
        boolean b3 = (trailing & 1) > 0;
        b.append(b1, b2, b3);
        return MBDF.get8BitFromBinary(b.build());
    }
}
