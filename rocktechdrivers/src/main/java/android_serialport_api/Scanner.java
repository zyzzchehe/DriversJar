package android_serialport_api;

import android.os.Handler;
import android.util.Log;

import com.comm.utils.CommonDataBean;
import com.comm.utils.CommonDataObservable;
import com.comm.utils.DataObserver;
import com.comm.utils.HandlerThread;
import com.hardware.scanner.fc.ScannerResult;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.CountDownLatch;

public class Scanner implements SerialPort.onDataReceivedListener {

    private static final String TAG = "Scanner";
    private SerialPort scanSerialPort;
    private static byte[] start = null;
    private static byte[] stop = null;
    private static byte[] LISHIZHI = null;
    private Handler mHandler;
    private CommonDataObservable commonDataObservable;
    private HandlerThread handlerThread;


    public Scanner(String portPath, int baudrate) throws Exception {
        start = new byte[]{0x1a, 0x54, 0x0d};
        stop = new byte[]{0x1a, 0x55, 0x0d};
        scanSerialPort = new SerialPort(new File(portPath), baudrate, 0, this);
        initHandler();
    }

    public Scanner(String portPath) throws Exception {
        start = new byte[]{0x1a, 0x54, 0x0d};
        stop = new byte[]{0x1a, 0x55, 0x0d};
        scanSerialPort = new SerialPort(new File(portPath), Constant.SCANNER_baudrate, 0, this);
        initHandler();
    }

    @Override
    public void onDataReceived(byte[] buffer, int size) {
        if (LISHIZHI == null) {
            LISHIZHI = Constant.subBytes(buffer, 0, size);
            // 0.3s之后反馈结果
            mHandler.sendEmptyMessageDelayed(1, 400);
        } else {
            LISHIZHI = Constant.byteMerger(LISHIZHI, Constant.subBytes(buffer, 0, size));
        }
        if (LISHIZHI.length == 1 && LISHIZHI[0] == 5) {
            LISHIZHI = null;
            return;
        }
        // 结尾不等于回车,继续接收
        if (LISHIZHI[LISHIZHI.length - 1] != 13) {
            mHandler.sendEmptyMessage(2);
        } else {
            // 结尾等于回车、删除回车
            LISHIZHI = Constant.subBytes(LISHIZHI, 0, LISHIZHI.length - 1);
            mHandler.removeMessages(1);
            mHandler.sendEmptyMessage(1);
        }
    }


    private void initHandler() {
        commonDataObservable = new CommonDataObservable();
        handlerThread = new HandlerThread();
        handlerThread.start();
        mHandler = new Handler(handlerThread.getLooper()) {

            public void handleMessage(android.os.Message msg) {
                switch (msg.what) {
                    case 1:
                        try {
                            String res = new String(LISHIZHI, "GBK");
                            commonDataObservable.setData(new CommonDataBean(res));
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        LISHIZHI = null;
                        // 关闭扫描
                        cancelScanner();
                        break;
                    case 2:
                        scanSerialPort.start();
                        break;
                    default:
                        break;
                }
            }
        };
    }


    public void setOverTime(int overTime) {
        scanSerialPort.setOverTime(overTime);
    }

    public void cancelScanner() {
        LISHIZHI = null;
        Log.v(TAG, "===停止发光，取消扫码====");
        if (scanSerialPort.mReadThread != null) {
            scanSerialPort.mReadThread.cancel();
        }
        scanSerialPort.sendData(stop);
        return;
    }

    public void close() {
        LISHIZHI = null;
        Log.v(TAG, "====关闭扫秒抢====");
        scanSerialPort.sendData(stop);
        scanSerialPort.stop();
        return;
    }

    public ScannerResult startScanner() {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        scanSerialPort.sendData(start);
        scanSerialPort.start();
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
        ScannerResult scannerResult = new ScannerResult();
        scannerResult.setScanData((String) commonDataObservable.getData().getO1());
        scannerResult.setRespCode(1001);
        commonDataObservable = null;
        handlerThread.stop();
        handlerThread = null;
        return scannerResult;
    }
}
