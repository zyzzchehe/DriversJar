package android_serialport_api;

import android.util.Log;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class SPRTSerialPort {

    private static final String TAG = "SerialPort";

    private FileInputStream mFileInputStream = null;
    private FileOutputStream mFileOutputStream = null;
    private boolean isFileOutputStreamWriting = false;
    public ReadThread mReadThread;
    private onDataReceivedListener mListener = null;
    private boolean mIsSerialPortOpenSuccessed = false;

    private byte[] mData = new byte[50];
    private int mCount = 0;
    private int count;
    // 超时时间 单位ms
    private int overTime = 6000;

    public interface onDataReceivedListener {
        void onDataReceived(final byte[] buffer, final int size);
    }

    class ReadThread extends Thread {

        private boolean mRunning = false;

        public ReadThread() {
            mRunning = true;
        }

        public void cancel() {
            mRunning = false;
            interrupt();
        }

        @Override
        public void run() {
            super.run();
            Log.i(TAG, "run() ==> start !!!!! ");
            int cnt = 0, nCount = 0;
            try {
                while (mRunning && !isInterrupted()) {
                    if (mFileInputStream == null) {
                        return;
                    }

                    if (mFileInputStream.available() <= 0) {
                        if (0 == overTime) {
                            try {
                                Thread.sleep(10);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            continue;

                        } else {
                            if (nCount > (overTime / 10)) {
                                if (mListener != null) {
                                    Log.i(TAG, "receive  data timeout");
                                    mListener.onDataReceived(new byte[]{0x5}, 1);
                                }
                                break;
                            } else {
                                nCount++;
                                try {
                                    Thread.sleep(10);
                                } catch (InterruptedException e) {
                                    Thread.currentThread().interrupt();
                                }
                                continue;
                            }
                        }
                    }
                    cnt = mFileInputStream.read(mData);

                    if (cnt > 0) {
                        SPRTConstant.byteToHex(" Recv ", cnt, mData);
                        if (mListener != null && mData != null) {
                            mListener.onDataReceived(mData, cnt);
                        }
                    }
                    break;
                }
            } catch (IOException e) {
                Log.e(TAG, "run() ==> IOException");
                e.printStackTrace();
            }

            Log.e(TAG, "run() ==> stop !!!!! ");
        }
    }

    public SPRTSerialPort(File device, int baudrate, int flags, onDataReceivedListener listener) throws Exception {
        mIsSerialPortOpenSuccessed = false;
        mCount = 0;
        for (int i = 0; i < mData.length; i++) {
            mData[i] = 0x00;
        }

        /* Check access permission */
        if (!device.canRead() || !device.canWrite()) {
            Process su;
            su = Runtime.getRuntime().exec("su");
            String cmd = "chmod 666 " + device.getAbsolutePath() + "\n" + "exit\n";
            su.getOutputStream().write(cmd.getBytes());
            if ((su.waitFor() != 0) || !device.canRead() || !device.canWrite()) {
                Log.e(TAG, "permission get failed");
            }
        }
        openSerialPort(device.getAbsolutePath(), baudrate, flags);
        mListener = listener;
        mIsSerialPortOpenSuccessed = true;
    }

    public void openSerialPort(String serialPortPath, int baudrate, int flags) {
        FileDescriptor mFd = open(serialPortPath, baudrate, flags);
        mFileInputStream = new FileInputStream(mFd);
        mFileOutputStream = new FileOutputStream(mFd);
        if (mFd == null) {
            Log.e(TAG, "native open failed");
        }
    }

    public boolean isOpenedSuccessed() {
        return mIsSerialPortOpenSuccessed;
    }

    public void start() {
        /**
         * 延迟100ms保证接收到一包完整的数据
         */
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Log.e(TAG, "start");
        if (mReadThread != null) {
            mReadThread.cancel();
            mReadThread = null;
        }
        mReadThread = new ReadThread();
        mReadThread.start();
    }

    public void stop() {

        Log.e(TAG, "stop");

        if (mReadThread != null) {
            mReadThread.cancel();
            mReadThread = null;
        }

        try {
            if (mFileInputStream != null) {
                mFileInputStream.close();
                mFileInputStream = null;
            }

            if (mFileOutputStream != null && isFileOutputStreamWriting == false) {
                mFileOutputStream.close();
                mFileOutputStream = null;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        close();
    }

    public boolean sendData(byte[] data) {
        if (mIsSerialPortOpenSuccessed == false) {
            return false;
        }
        if (mFileOutputStream != null) {
            try {
                isFileOutputStreamWriting = true;
                mFileOutputStream.write(data);
                SPRTConstant.byteToHex(" Send ", data.length, data);
                mFileOutputStream.flush();
                isFileOutputStreamWriting = false;
                return true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return false;
    }

    public void setOverTime(int overTime) {
        this.overTime = overTime;
    }

    public InputStream getInputStream() {
        return this.mFileInputStream;
    }

    public OutputStream getOutputStream() {
        return this.mFileOutputStream;
    }

    // JNI
    private native static FileDescriptor open(String path, int baudrate, int flags);

    public native void close();

    static {
        System.loadLibrary("SPRTSerial");
    }
}