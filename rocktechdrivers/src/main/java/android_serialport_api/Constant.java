package android_serialport_api;

import android.util.Log;

public class Constant {

    public final static int SCANNER_baudrate = 115200;
    public static String COM_SCANNER = "/dev/ttymxc2";
    public final static String COM_PRINTER = "/dev/ttymxc4";
    public final static int PRINTER_baudrate = 115200;

    public static void byteToHex(String string, int length, byte[] msg) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            byte b = msg[i];
            String str = Integer.toHexString(0xFF & b);
            if (str.length() == 1) {
                // str = " 0x0" + str;
                str = " 0" + str;
            } else {
                // str = " 0x" + str;
                str = " " + str;
            }
            sb.append(str);
        }
        Log.v("Constant", string + " " + sb.toString());
    }


    public static byte[] subBytes(byte[] src, int begin, int count) {
        byte[] bs = new byte[count];
        for (int i = begin; i < begin + count; i++) {
            bs[i - begin] = src[i];
        }
        return bs;
    }

    public static byte[] byteMerger(byte[] byte_1, byte[] byte_2) {
        byte[] byte_3 = new byte[byte_1.length + byte_2.length];
        System.arraycopy(byte_1, 0, byte_3, 0, byte_1.length);
        System.arraycopy(byte_2, 0, byte_3, byte_1.length, byte_2.length);
        return byte_3;
    }
}
