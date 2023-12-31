package com.dmj.cli.pattern;

/**
 * 设计模式
 * 命令装配
 */
public class RemoteControl {
    Command command;

    public void setCommand(Command command) {
        this.command = command;
    }

    public void pressButton(){
        command.execute();
    }
}
