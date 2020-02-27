package android_serialport_api;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by zhangyazhou on 2019/2/22.
 */
public class QrCommand {


    public String Version = "20160722_Ver3.3";
    public String Author = "LJF-Studio.COM";

    private int PaperState = 0;
    private boolean Picked = false;
    public boolean IsBusy = true;
    private boolean CutState = false;

    private static final int OPT_CODE_NOP = 0;
    private static final int OPT_CODE_READY = 10;
    private static final int OPT_CODE_OVER = 11;
    private static final int OPT_CODE_TEXT = 12;
    private static final int OPT_CODE_LINE = 13;
    private static final int OPT_CODE_BARCODE = 14;
    private static final int OPT_CODE_QRCODE = 15;
    private static final int OPT_CODE_BITMAP = 16;


    public byte[] QiRui_CreatePage(int width, int height) {

        String cmd = "SIZE " + width + " mm," + height + " mm\r\n";

        try {
            return cmd.getBytes("gb2312");
        } catch (UnsupportedEncodingException e) {

            e.printStackTrace();
            return null;
        }
    }


    public byte[] QiRui_PrintPage(int count) {

        String cmd = "PRINT " + count + "\r\n";

        try {
            return cmd.getBytes("gb2312");
        } catch (UnsupportedEncodingException e) {

            e.printStackTrace();
            return null;
        }
    }


    public byte[] QiRui_Text(int x, int y, String font, int rotation, int xmulti, int ymulti, boolean isBold,
                             String content) {
        String cmd = null;
        String _content = content.replace("\"", "\\[\"]");
        if (isBold) {
            cmd = "TEXT " + x + "," + y + "," + "\"" + font + "\"" + "," + rotation + "," + xmulti + "," + ymulti
                    + ",B1," + "\"" + _content + "\"\r\n";
        } else {
            cmd = "TEXT " + x + "," + y + "," + "\"" + font + "\"" + "," + rotation + "," + xmulti + "," + ymulti + ","
                    + "\"" + _content + "\"\r\n";
        }
        try {
            return cmd.getBytes("gb2312");
        } catch (UnsupportedEncodingException e) {

            e.printStackTrace();
            return null;
        }
    }

    public byte[] QiRui_DrawLine(int start_x, int start_y, int end_x, int end_y, int width, int dottedType) {
        // LINE 320,460,600,780,8,M3
        String cmd = null;
        switch (dottedType) {
            case 0:
                cmd = "LINE " + start_x + "," + start_y + "," + end_x + "," + end_y + "," + width + "\r\n";
                break;
            case 1:
                cmd = "LINE " + start_x + "," + start_y + "," + end_x + "," + end_y + "," + width + ",M1\r\n";
                break;
            case 2:
                cmd = "LINE " + start_x + "," + start_y + "," + end_x + "," + end_y + "," + width + ",M2\r\n";
                break;
            case 3:
                cmd = "LINE " + start_x + "," + start_y + "," + end_x + "," + end_y + "," + width + ",M3\r\n";
                break;
            case 4:
                cmd = "LINE " + start_x + "," + start_y + "," + end_x + "," + end_y + "," + width + ",M4\r\n";
                break;

        }

        try {
            return cmd.getBytes("gb2312");
        } catch (UnsupportedEncodingException e) {

            e.printStackTrace();
            return null;
        }
    }


    public byte[] QiRui_DrawPic(int x, int y, int width, int height, String octetStr) {
        String cmd = "BITMAP " + x + "," + y + "," + (width / 8) + "," + height + ",1,";
        byte[] pixel = new byte[octetStr.length() / 2];
        for (int i = 0; i < pixel.length; i++) {
            try {
                pixel[i] = (byte) ~(0xff & Integer.parseInt(octetStr.substring(i * 2, i * 2 + 2), 16));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        byte[] bcmd;
        try {
            bcmd = cmd.getBytes("gb2312");
        } catch (UnsupportedEncodingException e) {

            e.printStackTrace();
            bcmd = null;
        }
        byte[] totalcmd = new byte[bcmd.length + pixel.length];
        System.arraycopy(bcmd, 0, totalcmd, 0, bcmd.length);
        System.arraycopy(pixel, 0, totalcmd, bcmd.length, pixel.length);

        return totalcmd;

    }


    public byte[] QiRui_DrawBar(int x, int y, int type, int height, int hri, int rotation, int cellwidth,
                                String content) {
        String CodeType = "128";
        switch (type) {

            case 0:
                CodeType = "128";
                break;
            case 1:
                CodeType = "39";
                break;
            case 2:
                CodeType = "93";
                break;
            case 3:
                CodeType = "ITF";
                break;
            case 4:
                CodeType = "UPCA";
                break;
            case 5:
                CodeType = "UPCE";
                break;
            case 6:
                CodeType = "CODABAR";
                break;
            case 7:
                CodeType = "EAN8";
                break;
            case 8:
                CodeType = "EAN13";
                break;
            // case 9:
            // CodeType="128M";
            // break;

        }
        String cmd = "BARCODE " + x + "," + y + "," + "\"" + CodeType + "\"" + "," + height + "," + hri + "," + rotation
                + "," + cellwidth + "," + cellwidth + "," + "\"" + content + "\"\r\n";

        try {
            return cmd.getBytes("gb2312");
        } catch (UnsupportedEncodingException e) {

            e.printStackTrace();
            return null;
        }
    }


    public byte[] QiRui_DrawQRCode(int x, int y, int ecc, int rotation, int cellwidth, int Version, String content) {
        String strECC = "Q";
        if ((Version < 0) || (Version > 40)) {
            return null;
        }
        switch (ecc) {
            case 0:
                strECC = "L";
                break;
            case 1:
                strECC = "M";
                break;
            case 2:
                strECC = "Q";
                break;
            case 3:
                strECC = "H";
                break;

        }
        String cmd = "QRCODE " + x + "," + y + "," + strECC + "," + cellwidth + ",A," + rotation + ",M2,S7,V" + Version
                + "," + "\"" + content + "\"\r\n";

        try {
            return cmd.getBytes("gb2312");
        } catch (UnsupportedEncodingException e) {

            e.printStackTrace();
            return null;
        }
    }

    public byte[] QiRui_Cut(boolean isCut) {
        String cmd;
        if (isCut) {
            cmd = "SET CUTTER 1" + "\r\n";
            try {
                return cmd.getBytes("gb2312");
            } catch (UnsupportedEncodingException e) {

                e.printStackTrace();
                return null;
            }

        } else {
            cmd = "SET CUTTER OFF" + "\r\n";
            try {
                return cmd.getBytes("gb2312");
            } catch (UnsupportedEncodingException e) {

                e.printStackTrace();
                return null;
            }

        }

    }


    public byte[] QiRui_Direction(int direction, int mirror) {
        String cmd = "DIRECTION " + direction + "," + mirror + "\r\n";
        try {
            return cmd.getBytes("gb2312");
        } catch (UnsupportedEncodingException e) {

            e.printStackTrace();
            return null;
        }
    }

    public byte[] QiRui_SetGap(boolean isEnable) {
        String cmd;
        if (isEnable) {
            cmd = "SET GAP ON" + "\r\n";
            try {
                return cmd.getBytes("gb2312");
            } catch (UnsupportedEncodingException e) {

                e.printStackTrace();
                return null;
            }
        } else {
            cmd = "SET GAP OFF" + "\r\n";
            try {
                return cmd.getBytes("gb2312");
            } catch (UnsupportedEncodingException e) {

                e.printStackTrace();
                return null;
            }
        }

    }

    /**
     * 清除页面缓冲区
     *
     * @return 返回byte[]
     */
    public byte[] QiRui_Cls() {
        String cmd = "CLS" + "\r\n";
        try {
            return cmd.getBytes("gb2312");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 检查打印机是否为忙
     *
     * @return boolean类型： true表示忙，false表示none
     */
    public boolean QiRui_isBusy(FileInputStream mFileInputStream, FileOutputStream mFileOutputStream) {
        try {
            int _count = mFileInputStream.available();
            if (_count > 0) {
                byte[] _tt;
                _tt = new byte[_count];
                int _z;
                while ((_z = mFileInputStream.read(_tt, 0, _tt.length)) != -1) {
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        String cmd = "READSTA " + "\r\n";
        try {
            mFileOutputStream.write(cmd.getBytes("gb2312"));
            Thread.sleep(200);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try {
            int count = mFileInputStream.available();
            if (count > 0) {
                byte[] tt;
                tt = new byte[count];
                int z;
                while ((z = mFileInputStream.read(tt, 0, tt.length)) != -1) {

                    String str = new String(tt, "gb2312");

                    String[] state = str.split("[ ,]");

                    System.out.println("print state --> " + Arrays.toString(state));

                    if (state[3].equals("IDLE")) {
                        return false;
                    } else if (state[3].equals("BUSY")) {
                        return true;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }


    /**
     * 查询打印机状态
     *
     * @param mFileInputStream  输入流
     * @param mFileOutputStream 输出流
     * @return 0-查询失败 1-查询成功
     */
    public int QiRui_PrinterState(FileInputStream mFileInputStream, FileOutputStream mFileOutputStream) {
        try {
            int count = mFileInputStream.available();
            if (count > 0) {
                byte[] tt;
                tt = new byte[count];
                int z;
                while ((z = mFileInputStream.read(tt, 0, tt.length)) != -1) {
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.PaperState = 0;
        this.IsBusy = true;
        this.Picked = false;
        this.CutState = false;

        String cmd = "READSTA " + "\r\n";

        try {
            mFileOutputStream.write(cmd.getBytes("gb2312"));
            Thread.sleep(500);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try {
            int _count = mFileInputStream.available();
            if (_count > 0) {
                byte[] _tt;
                _tt = new byte[_count];
                int _z;
                while ((_z = mFileInputStream.read(_tt, 0, _tt.length)) != -1) {
                    String str = new String(_tt, "gb2312");
                    String[] state = str.split("[ ,]");
                    // 检查纸张

                    if (state[1].equals("LIBOPEN")) {
                        this.PaperState = 3;

                    } else if (state[1].equals("NOPAPER")) {
                        this.PaperState = 0;

                    } else if (state[1].equals("PAPEREND")) {
                        this.PaperState = 2;

                    } else if (state[1].equals("PAPER")) {
                        this.PaperState = 1;

                    } else if (state[1].equals("PAPERERR")) {
                        this.PaperState = 4;

                    }

                    // 检查拿取
                    if (state[2].equals("WAITPICK")) {
                        this.Picked = false;
                    } else if (state[2].equals("PICK")) {
                        this.Picked = true;
                    }
                    // 检查切刀
                    if (state[4].equals("CUTERERR")) {
                        this.CutState = false;
                    } else if (state[4].equals("CUTEROK")) {
                        this.CutState = true;
                    }
                    // 检查busy
                    if (state[3].equals("IDLE")) {
                        this.IsBusy = false;
                    } else if (state[3].equals("BUSY")) {
                        this.IsBusy = true;
                    }

                    return 1;
                }
            } else {
                return 0;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }


    public int getPaperState() {
        return PaperState;
    }

    public List<byte[]> QiRui_ParserFCboxJsonToQR(int w, int h, String jsonstring) {
        List<byte[]> dataList = new ArrayList<byte[]>();
        int LabelEndFlag = 0;
        try {
            JSONObject result = new JSONObject(jsonstring);
            JSONArray nameList = result.getJSONArray("opts");
            int length = nameList.length();
            int optCode, fontType, beginX, beginY, endX, endY, width, height, qrVersion, unitHeight, level;
            String content;
            boolean isBold;
            for (int i = 0; i < length; i++) {
                content = "";
                optCode = fontType = beginX = beginY = endX = endY = width = height = qrVersion = unitHeight = level = 0;
                isBold = false;
                JSONObject oj = nameList.getJSONObject(i);
                optCode = oj.getInt("optCode");
                switch (optCode) {
                    case OPT_CODE_NOP:
                        break;
                    case OPT_CODE_READY:
                        dataList.add(QiRui_CreatePage(w, h));
                        dataList.add(QiRui_Direction(0, 0));
                        dataList.add(QiRui_SetGap(true));
                        dataList.add(QiRui_Cut(true));
                        dataList.add(QiRui_Cls());//清除页面缓冲 yazhou add
                        break;
                    case OPT_CODE_OVER:
                        LabelEndFlag = 1;
                        dataList.add(QiRui_PrintPage(1));
                        break;
                    case OPT_CODE_TEXT:
                        beginX = oj.getInt("x");
                        beginY = oj.getInt("y");
                        fontType = oj.getInt("fontType");
                        isBold = oj.getBoolean("isBold");
                        content = oj.getString("content");
                        String font = "TSS24.BF2";
                        int xmulti = 0, ymulti = 0;
                        if (2 == fontType) {
                            font = "TSS24.BF2";
                        } else if (1 == fontType) {
                            font = "TSS16.BF2";
                        } else if (3 == fontType) {
                            font = "TSS24.BF2";
                            xmulti = 1;
                            ymulti = 2;
                        } else if (4 == fontType) {
                            font = "TSS32.BF2";
                        } else if (5 == fontType) {
                            font = "TSS24.BF2";
                            xmulti = 2;
                            ymulti = 2;
                        } else if (6 == fontType) {
                            font = "TSS24.BF2";
                            xmulti = 3;
                            ymulti = 3;
                        }
                        dataList.add(QiRui_Text(beginX, beginY, font, 0, xmulti, ymulti, isBold, content));
                        break;
                    case OPT_CODE_LINE:
                        beginX = oj.getInt("beginX");
                        beginY = oj.getInt("beginY");
                        endX = oj.getInt("endX");
                        endY = oj.getInt("endY");
                        width = oj.getInt("width");
                        dataList.add(QiRui_DrawLine(beginX, beginY, endX, endY, width + 1, 0));
                        break;
                    case OPT_CODE_BARCODE:
                        beginX = oj.getInt("x");
                        beginY = oj.getInt("y");
                        width = oj.getInt("width");
                        height = oj.getInt("height");
                        content = oj.getString("number");
                        dataList.add(QiRui_DrawBar(beginX, beginY, 0, height, 0, 0, 3, content));
                        break;
                    case OPT_CODE_QRCODE:
                        beginX = oj.getInt("x");
                        beginY = oj.getInt("y");
                        qrVersion = oj.getInt("qrVersion");
                        unitHeight = oj.getInt("unitHeight");
                        level = oj.getInt("level");
                        content = oj.getString("content");
                        dataList.add(QiRui_DrawQRCode(beginX, beginY, level, 0, unitHeight, qrVersion, content));
                        break;
                    case OPT_CODE_BITMAP:
                        beginX = oj.getInt("x");
                        beginY = oj.getInt("y");
                        width = oj.getInt("width");
                        height = oj.getInt("height");
                        content = oj.getString("octetStr");
                        dataList.add(QiRui_DrawPic(beginX, beginY, width, height, content));
                        break;
                    default:
                        break;
                }

            }

        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        if (LabelEndFlag == 1) {
            return dataList;
        } else {
            return null;
        }
    }
}
