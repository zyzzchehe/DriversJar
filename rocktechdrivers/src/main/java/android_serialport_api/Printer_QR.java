package android_serialport_api;

import android.content.Intent;
import android.util.Log;

import com.comm.utils.CommonDataBean;
import com.comm.utils.CommonDataObservable;
import com.comm.utils.DataObserver;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class Printer_QR implements SerialPort.onDataReceivedListener {

    private static final String TAG = "Printer_QR";
    protected SerialPort printSerialPort = null;
    private static int n = -1;
    private final static String cmd = "READSTA " + "\r\n";
    private String data2Print;
    private QrCommand printer = new QrCommand();
    private CommonDataObservable commonDataObservable;
    int time = 3;//一般6s内会打印完成
    int width;
    int height;

    public Printer_QR(String portPath) throws Exception {
        printSerialPort = new SerialPort(new File(portPath), Constant.PRINTER_baudrate, 0, this);
        initHandler();
    }

    public Printer_QR(String portPath, int baudrate) throws Exception {
        printSerialPort = new SerialPort(new File(portPath), baudrate, 0, this);
        initHandler();
    }

    private void initHandler() {
        commonDataObservable = new CommonDataObservable();
    }

    public void close() {
        if (printSerialPort != null) {
            printSerialPort.stop();
        }
    }

    @Override
    public void onDataReceived(byte[] buffer, int size) {
        if (size == 1 && buffer[0] == 5) {
            return;
        }
        String str = null;
        try {
            str = new String(buffer, "gb2312");
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        }
        if (1 == n) {//是否有纸
            if (str != null) {
                String[] state = str.split("[ ,]");
                Intent intent = new Intent("android.intent.action.hal.printer.result.haspaper");
                if (state[1].equals("PAPEREND") || state[1].equals("PAPER")) {//有纸
                    commonDataObservable.setData(new CommonDataBean(true));
                } else if (state[1].equals("NOPAPER")) {//缺纸
                    commonDataObservable.setData(new CommonDataBean(false));
                }
            }
        } else if (2 == n) {
            if (str != null) {
                commonDataObservable.setData(new CommonDataBean(true));
            } else {
                commonDataObservable.setData(new CommonDataBean(false));
            }
        } else if (3 == n) {
            if (str != null) {
                if (printer.QiRui_PrinterState((FileInputStream) printSerialPort.getInputStream(), (FileOutputStream) printSerialPort.getOutputStream()) == 1) {
                    if (printer.getPaperState() == 0) {
                        return;
                    }
                    if (printer.IsBusy) {
                        checkState();
                        if (time == 0) {
                            return;
                        }
                    }
                }
                //Constant.writeLog("即将打印，打印内容 == "+data2Print);
                List<byte[]> data = printer.QiRui_ParserFCboxJsonToQR(width, height, data2Print);
                for (byte[] b : data) {
                    printSerialPort.sendData(b);
                }
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
                //再去轮询查打印机的状态 18s
                checkState();
                if (time == 0) {
                    commonDataObservable.setData(new CommonDataBean(false));
                    return;
                }
                n = 2;
                printSerialPort.start();
                try {
                    printSerialPort.sendData(cmd.getBytes("gb2312"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

            }
        } else if (n == 4) {
            if (str != null) {
                String[] state = str.split("[ ,]");
                if (state[1].equals("PAPEREND") || state[1].equals("NOPAPER")) {
                    commonDataObservable.setData(new CommonDataBean(true));
                } else {
                    commonDataObservable.setData(new CommonDataBean(false));
                }
            }
        } else {
            Log.d(TAG, "invalid state");
        }

    }


    void checkState() {
        while (time > 0) {
            printSerialPort.openSerialPort(new File(Constant.COM_PRINTER).getAbsolutePath(), Constant.PRINTER_baudrate, 0);
            if (printer.QiRui_PrinterState((FileInputStream) printSerialPort.getInputStream(), (FileOutputStream) printSerialPort.getOutputStream()) == 1) {
                if (printer.IsBusy) {
                    try {
                        Thread.sleep(3000);
                        time -= 1;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    continue;
                } else {
                    time = 6;
                    break;
                }
            } else {
                try {
                    Thread.sleep(3000);
                    time -= 1;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                continue;
            }
        }
    }


    public boolean hasPaper() {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        n = 1;
        printSerialPort.start();
        try {
            printSerialPort.sendData(cmd.getBytes("gb2312"));
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        }
        DataObserver dataObserver = new DataObserver();
        dataObserver.setiCommonDataObservable(new DataObserver.ICommonDataObservable() {
            @Override
            public void commonDataObservable(CommonDataObservable commonDataObservable) {
                countDownLatch.countDown();
            }
        });
        commonDataObservable.addObserver(dataObserver);
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        commonDataObservable.deleteObservers();
        return (boolean) commonDataObservable.getData().getO1();
    }

    public boolean hasPaperMore() {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        n = 4;
        printSerialPort.start();
        try {
            printSerialPort.sendData(cmd.getBytes("gb2312"));
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        }
        DataObserver dataObserver = new DataObserver();
        dataObserver.setiCommonDataObservable(new DataObserver.ICommonDataObservable() {
            @Override
            public void commonDataObservable(CommonDataObservable commonDataObservable) {
                countDownLatch.countDown();
            }
        });
        commonDataObservable.addObserver(dataObserver);
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        commonDataObservable.deleteObservers();
        return (boolean) commonDataObservable.getData().getO1();
    }

    public boolean print(int width, int height, String str) {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        n = 3;
        time = 3;
        data2Print = str;
        this.width = width;
        this.height = height;
        printSerialPort.start();
        try {
            printSerialPort.sendData(cmd.getBytes("gb2312"));
        } catch (UnsupportedEncodingException e2) {
            e2.printStackTrace();
        }
        DataObserver dataObserver = new DataObserver();
        dataObserver.setiCommonDataObservable(new DataObserver.ICommonDataObservable() {
            @Override
            public void commonDataObservable(CommonDataObservable commonDataObservable) {
                countDownLatch.countDown();
            }
        });
        commonDataObservable.addObserver(dataObserver);
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        commonDataObservable.deleteObservers();
        return (boolean) commonDataObservable.getData().getO1();
    }

    public int paperSize() {
        return 1;
    }
}
