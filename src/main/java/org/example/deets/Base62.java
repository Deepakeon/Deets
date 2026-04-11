package org.example.deets;

import java.math.BigInteger;
import java.util.UUID;

public class Base62 {
    private static final String ALPHABET =
            "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

    public static String encodeUuid(UUID uuid) {
        BigInteger big = new BigInteger(1, uuidToBytes(uuid));
        return encodeBase62(big);
    }

    private static byte[] uuidToBytes(UUID uuid) {
        byte[] bytes = new byte[16];
        long msb = uuid.getMostSignificantBits();
        long lsb = uuid.getLeastSignificantBits();
        for (int i = 0; i < 8; i++) {
            bytes[i] = (byte)(msb >>> (8 * (7 - i)));
        }
        for (int i = 0; i < 8; i++) {
            bytes[8 + i] = (byte)(lsb >>> (8 * (7 - i)));
        }
        return bytes;
    }

    private static String encodeBase62(BigInteger num) {
        if (num.equals(BigInteger.ZERO)) {
            return "0";
        }
        StringBuilder sb = new StringBuilder();
        BigInteger base = BigInteger.valueOf(62);
        while (num.compareTo(BigInteger.ZERO) > 0) {
            BigInteger[] qr = num.divideAndRemainder(base);
            int remainder = qr[1].intValue();
            sb.append(ALPHABET.charAt(remainder));
            num = qr[0];
        }
        return sb.reverse().toString();
    }
}
