package com.dmj.cli.example;

import picocli.CommandLine;
import picocli.CommandLine.Option;

import java.util.concurrent.Callable;

public class Login implements Callable<Integer> {
    @Option(names = {"-u", "--user"}, description = "User name")
    String user;

    @Option(names = {"-p", "--password"}, description = "Passphrase", interactive = true,arity = "0..1")
    String password;

    @Option(names = {"-cp","--checkPassWord"},description = "Check Password",interactive = true,arity = "0..1")
    String checkPassWord;
    public Integer call() throws Exception {
        System.out.println("password = " + password);
        System.out.println("checkPassWord = " + checkPassWord);
        return 0;
    }

    public static void main(String[] args) {
        (new CommandLine(new Login()).addSubcommand(new ASCIIArt())).execute("-u", "user123", "-p","xxx","-cp","ASCIIArt","-s");
    }
}