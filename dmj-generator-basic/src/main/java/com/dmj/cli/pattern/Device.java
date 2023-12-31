package com.dmj.cli.pattern;

/**
 * 被控制的对象
 */
public class Device {
    String name;
    public Device(String name){
        this.name = name;
    }
    public void turnOff(){
        System.out.println(name+"设备关闭");
    }
    public void turnOn(){
        System.out.println(name+"设备打开");
    }
}
