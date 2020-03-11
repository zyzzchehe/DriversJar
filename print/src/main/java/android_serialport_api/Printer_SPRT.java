package android_serialport_api;

import android.util.Log;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

public class Printer_SPRT implements SPRTSerialPort.onDataReceivedListener {
    private final String TAG = "Printer_SPRT";
    private SPRTSerialPort printer;
    private byte[] cmd = {0x10, 0x4, 0x4};
    private boolean status = false;

    private static boolean flag = false;
    private static boolean result = false;
    /**
     * 需要一个计时器
     */
    private Timer timer;
    private TimerTask timerTask = new TimerTask() {
        @Override
        public void run() {
            Log.i(TAG, "run: time out, PrintState is "+ SPRTConstant.PrinterState.HANDLE);
            flag = true;
            result = false;
            status = false;
        }
    };

    private void cancelTimer(){
        if(timer != null){
            timer.cancel();
            timer = null;
        }
        if(timerTask != null){
            timerTask.cancel();
            timerTask = null;
        }
    }

    public Printer_SPRT(String portPath, int baudrate) throws Exception {
        timer = new Timer();
        printer = new SPRTSerialPort(new File(portPath), baudrate, 0, this);
    }

    @Override
    public void onDataReceived(byte[] buffer, int size) {
        if (size == 1 && buffer[0] == 9) {
            return;
        }
        byte[] strData = new byte[size];
        for (int i = 0; i < size; i++) {
            strData[i] = buffer[i];
        }
        /**
         * 是否有纸
         */
        if (SPRTConstant.PrinterState.HANDLE == SPRTConstant.PrinterState.HASPAPER) {
            flag = true;
            cancelTimer();
            if ((getByte(strData[0], 5)) == 3) {
                // 发送没纸广播
               result = false;
            }
            if ((getByte(strData[0], 5)) == 0) {
                // 发送有纸广播
               result = true;
            }
        }
        /**
         * 是否纸将尽
         */
        if (SPRTConstant.PrinterState.HANDLE == SPRTConstant.PrinterState.NEEDMORE) {
            flag = true;
            cancelTimer();
            if (getByte(strData[0], 2) == 3) {
                // 发送纸将近广播
                result = false;
            }
            if (getByte(strData[0], 2) == 0) {
                // 不发生纸将近广播
                result = true;
            }
        }
        /**
         * 打印纸张结果
         */
        if (SPRTConstant.PrinterState.HANDLE == SPRTConstant.PrinterState.DATA2PRINT) {
            flag = true;
            cancelTimer();
            if ((1 & (strData[0] >> 4)) == 0) {//打印成功
                status = true;
            }
        }
    }

    private byte getByte(byte paramByte, int paramInt) {
        return (byte) (0x3 & (paramByte >> paramInt));
    }

    public boolean hasPaper() {
        timer.schedule(timerTask,1000);
        flag = false;
        SPRTConstant.PrinterState.HANDLE = SPRTConstant.PrinterState.HASPAPER;
        printer.start();
        printer.sendData(cmd);
        while (!flag){
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    public boolean hasPaperMore() {
        timer.schedule(timerTask,1000);
        flag = false;
        SPRTConstant.PrinterState.HANDLE = SPRTConstant.PrinterState.NEEDMORE;
        printer.start();
        printer.sendData(cmd);
        while (!flag){
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    public boolean print(final String str) {
        timer.schedule(timerTask,4000);
        flag = false;
        SPRTConstant.PrinterState.HANDLE = SPRTConstant.PrinterState.DATA2PRINT;
        final byte[] query = {16, 6, 1};
        printer.start();
        printer.sendData(query);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
                if (status) {
                    byte[] b;
                    byte[] bnew;
                    try {
                        b = str.getBytes("GBK");
                        if (b[b.length - 3] == b[b.length - 1]) {
                            bnew = Arrays.copyOfRange(b, 0, b.length - 2);
                        } else {
                            bnew = b;
                        }
                    } catch (UnsupportedEncodingException e1) {
                        return;
                    }
                    status = false;
                    printer.sendData(bnew);
                    try {
                        Thread.sleep(3000);
                        printer.start();
                        printer.sendData(query);
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
//                    if (status) {
//                        Intent intent = new Intent("android.intent.action.hal.printer.result.status");
//                        intent.putExtra("status", true);
//                        context.sendBroadcast(intent);
//                    } else {
//                        Intent intent = new Intent("android.intent.action.hal.printer.result.status");
//                        intent.putExtra("status", false);
//                        context.sendBroadcast(intent);
//                    }
                }
            }
        }).start();
        while (!flag){
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return status;
    }

    public int paperSize() {
//        Intent intent = new Intent("android.intent.action.hal.printer.supportsize.result");
//        intent.putExtra("papersize", 0); // 0-3寸，1-4寸
//        context.sendBroadcast(intent);
        return 0;
    }

    public void closeSerialPort() {
        printer.close();
    }

}
