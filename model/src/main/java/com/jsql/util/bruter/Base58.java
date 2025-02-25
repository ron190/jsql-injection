package com.jsql.util.bruter;

import org.apache.commons.lang3.StringUtils;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class Base58 {

    private static final char[] ALPHABET = "123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz".toCharArray();
    private static final int BASE_58 = Base58.ALPHABET.length;
    private static final int BASE_256 = 256;

    private static final int[] INDEXES = new int[128];
    
    static {
        Arrays.fill(Base58.INDEXES, -1);
        for (int i = 0; i < Base58.ALPHABET.length; i++) {
            Base58.INDEXES[Base58.ALPHABET[i]] = i;
        }
    }

    private Base58() {
        // Utility class
    }

    public static String encode(byte[] input) {
        if (input.length == 0) {
            // paying with the same coin
            return StringUtils.EMPTY;
        }
        // Make a copy of the input since we are going to modify it.
        byte[] copyInput = Base58.copyOfRange(input, 0, input.length);
        // Count leading zeroes
        var zeroCount = 0;
        while (zeroCount < copyInput.length && copyInput[zeroCount] == 0) {
            ++zeroCount;
        }
        // The actual encoding
        var temp = new byte[copyInput.length * 2];
        int j = temp.length;
        int startAt = zeroCount;
        while (startAt < copyInput.length) {
            byte mod = Base58.divmod58(copyInput, startAt);
            if (copyInput[startAt] == 0) {
                ++startAt;
            }
            temp[--j] = (byte) Base58.ALPHABET[mod];
        }
        // Strip extra '1' if any
        while (j < temp.length && temp[j] == Base58.ALPHABET[0]) {
            ++j;
        }
        // Add as many leading '1' as there were leading zeros.
        while (--zeroCount >= 0) {
            temp[--j] = (byte) Base58.ALPHABET[0];
        }
        byte[] output = Base58.copyOfRange(temp, j, temp.length);
        return new String(output, StandardCharsets.UTF_8);
    }

    public static byte[] decode(String input) {
        if (input.isEmpty()) {
            // paying with the same coin
            return new byte[0];
        }
        var input58 = new byte[input.length()];
        // Transform the String to a base58 byte sequence
        for (var i = 0; i < input.length(); ++i) {
            var c = input.charAt(i);
            int digit58 = -1;
            if (c >= 0 && c < 128) {
                digit58 = Base58.INDEXES[c];
            }
            if (digit58 < 0) {
                throw new IllegalArgumentException("Not a Base58 input: " + input);
            }
            input58[i] = (byte) digit58;
        }

        // Count leading zeroes
        var zeroCount = 0;
        while (zeroCount < input58.length && input58[zeroCount] == 0) {
            ++zeroCount;
        }

        // The encoding
        var temp = new byte[input.length()];
        int j = temp.length;

        int startAt = zeroCount;
        while (startAt < input58.length) {
            byte mod = Base58.divmod256(input58, startAt);
            if (input58[startAt] == 0) {
                ++startAt;
            }
            temp[--j] = mod;
        }
        // Do no add extra leading zeroes, move j to first non-null byte.
        while (j < temp.length && temp[j] == 0) {
            ++j;
        }
        return Base58.copyOfRange(temp, j - zeroCount, temp.length);
    }

    private static byte divmod58(byte[] number, int startAt) {
        var remainder = 0;
        for (int i = startAt; i < number.length; i++) {
            int digit256 = number[i] & 0xFF;
            int temp = remainder * Base58.BASE_256 + digit256;
            number[i] = (byte) (temp / Base58.BASE_58);
            remainder = temp % Base58.BASE_58;
        }
        return (byte) remainder;
    }

    private static byte divmod256(byte[] number58, int startAt) {
        var remainder = 0;
        for (int i = startAt; i < number58.length; i++) {
            int digit58 = number58[i] & 0xFF;
            int temp = remainder * Base58.BASE_58 + digit58;
            number58[i] = (byte) (temp / Base58.BASE_256);
            remainder = temp % Base58.BASE_256;
        }
        return (byte) remainder;
    }

    private static byte[] copyOfRange(byte[] source, int from, int to) {
        var range = new byte[to - from];
        System.arraycopy(source, from, range, 0, range.length);
        return range;
    }
}