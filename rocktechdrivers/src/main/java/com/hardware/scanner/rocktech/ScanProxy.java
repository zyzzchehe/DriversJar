package com.hardware.scanner.rocktech;

import com.hardware.base.HardwareBaseResp;
import com.hardware.base.HardwareInfo;
import com.hardware.scanner.fc.IScanner;
import com.hardware.scanner.fc.ScannerResult;

import android_serialport_api.Scanner;

public class ScanProxy implements IScanner {

    private Scanner scanner;

    @Override
    public ScannerResult startScan(long l) {
        ScannerResult s = new ScannerResult();
        if (scanner == null) {
            s.setScanData("null");
            s.setRespCode(2002);
            s.setRespMsg("请初始化设备");
            return s;
        }
        scanner.setOverTime((int) l);
        return scanner.startScanner();
    }

    @Override
    public HardwareBaseResp canCelScan() {
        ScannerResult s = new ScannerResult();
        if (scanner == null) {
            s.setScanData("null");
            s.setRespCode(2002);
            s.setRespMsg("请初始化设备");
            return s;
        }
        scanner.cancelScanner();
        s.setRespCode(1001);
        s.setRespMsg("cancel成功");
        return s;
    }

    @Override
    public HardwareBaseResp init(String portPath) {
        HardwareBaseResp h = new HardwareBaseResp();
        try {
            scanner = new Scanner(portPath);
            h.setRespCode(1001);
            h.setRespMsg("初始化成功");
        } catch (Exception e) {
            h.setRespCode(2002);
            h.setRespMsg("初始化失败");
            e.printStackTrace();
        }
        return h;
    }

    @Override
    public HardwareBaseResp init(String portPath, int baudrate) {
        HardwareBaseResp h = new HardwareBaseResp();
        try {
            scanner = new Scanner(portPath, baudrate);
            h.setRespCode(1001);
            h.setRespMsg("初始化成功");
        } catch (Exception e) {
            h.setRespCode(2002);
            h.setRespMsg("初始化失败");
            e.printStackTrace();
        }
        return h;
    }

    @Override
    public HardwareInfo queryHardwareInfo() {
        return null;
    }

    @Override
    public HardwareBaseResp closePort() {
        ScannerResult s = new ScannerResult();
        if (scanner == null) {
            s.setScanData("null");
            s.setRespCode(2002);
            s.setRespMsg("请初始化设备");
            return s;
        }
        scanner.close();
        s.setRespCode(1001);
        s.setRespMsg("关闭成功");
        return s;
    }
}
