package com.dmj;

import com.dmj.maker.cli.CommandExecutor;

public class Main {
    public static void main(String[] args) {
        CommandExecutor commandExecutor = new CommandExecutor();
        commandExecutor.doExecutor(args);
    }
}