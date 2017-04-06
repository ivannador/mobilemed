package com.nador.mobilemed.data.utils;

import java.io.UnsupportedEncodingException;
import java.security.InvalidParameterException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by nador on 13/06/16.
 */
public class ByteUtils {

    private ByteUtils() {}

    /**
     * Eg. "ff" -> 0xff
     *
     * @param s string to convert
     * @return byte array from string
     */
    public static byte[] hexStringToByteArray(final String s) throws InvalidParameterException {
        int len = s.length();
        if (len % 2 != 0) {
            throw new InvalidParameterException("Invalid string length, must be even!");
        }

        byte[] data = new byte[len / 2];
        for (int i = 0; i < len - 1; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }

    /**
     * Eg. 0xff -> "ff"
     *
     * @param bytes byte array to convert
     * @return string from byte array
     */
    public static String byteArrayToHexString(final byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }
    final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();

    /**
     * Truncate a byte array to the desired length
     *
     * @param bytes The byte array to be truncated
     * @param length Desired length
     * @return Byte array with new length
     */
    public static byte[] truncate(final byte[] bytes, final int length) {
        if (bytes.length < length) {
            return bytes;
        }

        byte[] truncatedBytes = new byte[length];
        System.arraycopy(bytes, 0, truncatedBytes, 0, length);
        return truncatedBytes;
    }

    /**
     * Convert byte array to it's long value
     *
     * @param bytes input byte array
     * @return long value
     */
    public static long byteArrayToInt(final byte[] bytes) {
        long value = 0;
        for (int i = 0; i < bytes.length; i++) {
            value = (value << 8) + (bytes[i] & 0xFF);
        }
        return value;
    }

    public static long byteArrayToIntTC(final byte[] bytes) {
        long value = 0;
        for (int i = 0; i < bytes.length; i++) {
            value = (value << 8) + (bytes[i]);
        }
        return value;
    }

    public static int fromTwoComplement(int value, int bitSize) {
        int shift = Integer.SIZE - bitSize;
        // shift sign into position
        int result  = value << shift;
        // Java right shift uses sign extension, but only works on integers or longs
        result = result >> shift;
        return result;
    }

    public static byte[] applyBitmask(final byte[] bytes, final byte[] bitMask) {
        byte[] result = new byte[bitMask.length];
        for (int i = 0; i < bitMask.length; i++) {
            result[i] = (byte) (bytes[i] & bitMask[i]);
        }
        return result;
    }

    /**
     * Apply bitmask and return only the values in indexes affected by bitmask
     * If bitmask is all zeroes, an empty array is returned
     *
     * @param bytes
     * @param bitMask
     * @return
     */
    public static byte[] cropToBitmask(final byte[] bytes, final byte[] bitMask) {
        byte[] bitmaskedBytes = applyBitmask(bytes, bitMask);
        byte[] resultUntruncated = new byte[bitmaskedBytes.length];
        int length = 0;
        for (int i = 0; i < bitMask.length; i++) {
            if (bitMask[i] != 0) {
                resultUntruncated[length] = bitmaskedBytes[i];
                length++;
            }
        }
        return truncate(resultUntruncated, length);
    }

    /**
     * Encode string with MD5
     *
     * @param s
     * @return
     */
    public static final String MD5(final String s) {
        final String MD5 = "MD5";
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest
                    .getInstance(MD5);
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuilder hexString = new StringBuilder();
            for (byte aMessageDigest : messageDigest) {
                String h = Integer.toHexString(0xFF & aMessageDigest);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * Returns the SHA1 hash for the provided String
     *
     * @param text
     * @return the SHA1 hash or null if an error occurs
     */
    public static String SHA1(String text) {

        try {
            MessageDigest md;
            md = MessageDigest.getInstance("SHA-1");
            md.update(text.getBytes("UTF-8"), 0, text.length());
            byte[] sha1hash = md.digest();

            return toHex(sha1hash);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static String toHex(byte[] buf) {

        if (buf == null) {
            return "";
        }

        int l = buf.length;
        StringBuffer result = new StringBuffer(2 * l);

        for (int i = 0; i < buf.length; i++) {
            appendHex(result, buf[i]);
        }

        return result.toString();

    }

    private final static String HEX = "0123456789ABCDEF";

    private static void appendHex(StringBuffer sb, byte b) {
        sb.append(HEX.charAt((b >> 4) & 0x0f))
                .append(HEX.charAt(b & 0x0f));
    }
}
