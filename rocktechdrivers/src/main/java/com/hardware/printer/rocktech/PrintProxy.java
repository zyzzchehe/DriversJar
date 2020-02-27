package com.hardware.printer.rocktech;

import com.hardware.base.HardwareBaseResp;
import com.hardware.base.HardwareInfo;
import com.hardware.printer.fc.HasPaperResult;
import com.hardware.printer.fc.IPrinter;
import com.hardware.printer.fc.NeedMoreResult;
import com.hardware.printer.fc.PaperSizeResult;
import com.hardware.printer.fc.PrintResult;

import android_serialport_api.Printer_QR;

public class PrintProxy implements IPrinter {
    private Printer_QR printerQr;

    @Override
    public PaperSizeResult queryPaperSize() {
        PaperSizeResult p = new PaperSizeResult();
        if (printerQr == null) {
            p.setPaperSize(-1);
            p.setRespCode(2002);
            p.setRespMsg("请初始化设备");
            return p;
        }
        p.setPaperSize(printerQr.paperSize());
        p.setRespCode(1001);
        p.setRespMsg("查询成功");
        return p;
    }

    @Override
    public HasPaperResult hasPaper() {
        HasPaperResult h = new HasPaperResult();
        if (printerQr == null) {
            h.setHasPaper(false);
            h.setRespCode(2002);
            h.setRespMsg("请初始化设备");
            return h;
        }
        h.setHasPaper(printerQr.hasPaper());
        h.setRespCode(1001);
        return h;
    }

    @Override
    public NeedMoreResult needMore() {
        NeedMoreResult n = new NeedMoreResult();
        if (printerQr == null) {
            n.setNeedMore(false);
            n.setRespCode(2002);
            n.setRespMsg("请初始化设备");
            return n;
        }
        n.setNeedMore(printerQr.hasPaperMore());
        n.setRespCode(1001);
        return n;
    }

    @Override
    public PrintResult print(int w, int h, String json) {
        PrintResult p = new PrintResult();
        if (printerQr == null) {
            p.setStatus(false);
            p.setRespCode(2002);
            p.setRespMsg("请初始化设备");
            return p;
        }
        p.setStatus(printerQr.print(w, h, json));
        p.setRespCode(1001);
        p.setRespMsg("打印成功");
        return p;
    }

    @Override
    public HardwareBaseResp downloadBmpToLabelFlash(int i, String s) {
        return null;
    }

    @Override
    public HardwareBaseResp cleanBmpFromLabelFlash(int i) {
        return null;
    }

    @Override
    public HardwareBaseResp init(String s) {
        HardwareBaseResp h = new HardwareBaseResp();
        try {
            printerQr = new Printer_QR(s);
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
    public HardwareBaseResp init(String s, int i) {
        HardwareBaseResp h = new HardwareBaseResp();
        try {
            printerQr = new Printer_QR(s, i);
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
        HardwareBaseResp h = new HardwareBaseResp();
        if (printerQr == null) {
            h.setRespCode(2002);
            h.setRespMsg("请初始化设备");
            return h;
        }
        printerQr.close();
        h.setRespCode(1001);
        h.setRespMsg("关闭成功");
        return h;
    }
}
