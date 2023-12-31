package com.dmj.cli.pattern;

/**
 * 命令模式
 * 命令具体的实现
 */
public class TurnOffCommand implements Command{
    Device device;
    public TurnOffCommand(Device device) {
        this.device = device;
    }

    @Override
    public void execute() {
        device.turnOff();
    }
}
