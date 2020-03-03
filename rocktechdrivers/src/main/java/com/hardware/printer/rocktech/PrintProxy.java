package com.hardware.printer.rocktech;

import com.hardware.base.HardwareBaseResp;
import com.hardware.base.HardwareInfo;
import com.hardware.printer.fc.HasPaperResult;
import com.hardware.printer.fc.IPrinter;
import com.hardware.printer.fc.NeedMoreResult;
import com.hardware.printer.fc.PaperSizeResult;
import com.hardware.printer.fc.PrintResult;


public class PrintProxy implements IPrinter {

    @Override
    public PaperSizeResult queryPaperSize() {

        return null;
    }

    @Override
    public HasPaperResult hasPaper() {

        return null;
    }

    @Override
    public NeedMoreResult needMore() {

        return null;
    }

    @Override
    public PrintResult print(int w, int h, String json) {

        return null;
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

        return null;
    }

    @Override
    public HardwareBaseResp init(String s, int i) {

        return null;
    }

    @Override
    public HardwareInfo queryHardwareInfo() {
        return null;
    }

    @Override
    public HardwareBaseResp closePort() {

        return null;
    }
}
