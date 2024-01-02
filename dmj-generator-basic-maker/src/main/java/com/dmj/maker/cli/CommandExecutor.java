package com.dmj.maker.cli;

import com.dmj.maker.cli.command.ConfigCommand;
import com.dmj.maker.cli.command.GenerateCommand;
import com.dmj.maker.cli.command.ListCommand;
import picocli.CommandLine;
import picocli.CommandLine.Command;

@Command(name="dmj",mixinStandardHelpOptions = true)
public class CommandExecutor implements Runnable{
    private final CommandLine commandLine;
    {
        commandLine = new CommandLine(this)
                .addSubcommand(new ConfigCommand())
                .addSubcommand(new GenerateCommand())
                .addSubcommand(new ListCommand());
    }

    @Override
    public void run() {
        // 不输入命令时的友好提示
        System.out.println("请输入具体命令，或者输入 --help 查看命令提示");
    }

    public Integer doExecutor(String[] args){
        return commandLine.execute(args);
    }
}
