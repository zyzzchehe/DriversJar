package com.hardware.printer.rocktech;

import android.util.Log;
import com.hardware.base.HardwareBaseResp;
import com.hardware.base.HardwareInfo;
import com.hardware.printer.fc.HasPaperResult;
import com.hardware.printer.fc.IPrinter;
import com.hardware.printer.fc.NeedMoreResult;
import com.hardware.printer.fc.PaperSizeResult;
import com.hardware.printer.fc.PrintResult;

import android_serialport_api.Printer_SPRT;


public class PrintProxy implements IPrinter {

    private Printer_SPRT printerSprt;
    @Override
    public PaperSizeResult queryPaperSize() {
        PaperSizeResult paperSizeResult = new PaperSizeResult();
        paperSizeResult.setPaperSize(0);
        paperSizeResult.setRespCode(1001);
        paperSizeResult.setRespMsg("获取纸张尺寸成功");
        return paperSizeResult;
    }

    @Override
    public HasPaperResult hasPaper() {
        HasPaperResult hasPaperResult = new HasPaperResult();
        if(printerSprt == null){
            hasPaperResult.setRespCode(2002);
            hasPaperResult.setRespMsg("请初始化设备");
        }else {
            hasPaperResult.setHasPaper(printerSprt.hasPaper());
            hasPaperResult.setRespCode(1001);
            hasPaperResult.setRespMsg(printerSprt.hasPaper() ? "打印机有纸" : "打印机已无纸");
        }
        return hasPaperResult;
    }

    @Override
    public NeedMoreResult needMore() {
        NeedMoreResult needMoreResult = new NeedMoreResult();
        if(printerSprt == null){
            needMoreResult.setRespCode(2002);
            needMoreResult.setRespMsg("请初始化设备");
        }else {
            needMoreResult.setNeedMore(printerSprt.hasPaperMore());
            needMoreResult.setRespCode(1001);
            needMoreResult.setRespMsg(printerSprt.hasPaperMore() ? "打印机纸充足" : "打印机纸将尽");
        }
        return needMoreResult;
    }

    @Override
    public PrintResult print(int i, int i1, String s) {
        return null;
    }

    @Override
    public PrintResult print(int i, int i1, String s, int i2) {
        PrintResult printResult = new PrintResult();
        if(i2 != 1){
            printResult.setRespCode(2002);
            printResult.setRespMsg("打印机类型错误");
        }else{
            if(printerSprt == null) {
                printResult.setRespCode(2002);
                printResult.setRespMsg("请初始化设备");
            }else {
                boolean status = printerSprt.print(s);
                printResult.setStatus(status);
                printResult.setRespCode(1001);
                printResult.setRespMsg(status ? "打印成功" : "打印失败");
            }
        }
        return printResult;
    }

    @Override
    public HardwareBaseResp downloadBmpToLabelFlash(int i, String s) {
        return null;
    }

    @Override
    public HardwareBaseResp cleanBmpFromLabelFlash(int i) {
        return null;
    }

    /**
     * 串口地址和波特率都要指定
     * @param s
     * @return
     */
    @Override
    public HardwareBaseResp init(String s) {
        return null;
    }

    @Override
    public HardwareBaseResp init(String s, int i) {
        HardwareBaseResp h = new HardwareBaseResp();
        try {
            printerSprt = new Printer_SPRT(s, i);
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
        HardwareBaseResp resp = new HardwareBaseResp();
        printerSprt.closeSerialPort();
        resp.setRespCode(1001);
        resp.setRespMsg("串口关闭成功");
        return resp;
    }
}
