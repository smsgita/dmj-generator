package com.dmj.cli.pattern;

public class Client {
    public static void main(String[] args) {
        Device one = new Device("one");
        Device two = new Device("two");

        TurnOffCommand offCommand = new TurnOffCommand(one);
        TurnOnCommand onCommand = new TurnOnCommand(two);

        RemoteControl remoteControl = new RemoteControl();

        remoteControl.setCommand(onCommand);
        remoteControl.pressButton();

        remoteControl.setCommand(offCommand);
        remoteControl.pressButton();


    }
}
