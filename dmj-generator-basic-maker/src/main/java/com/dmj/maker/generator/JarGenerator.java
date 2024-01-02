package com.dmj.maker.generator;

import java.io.*;

public class JarGenerator {
    public static void doGenerator(String projectDir) throws IOException, InterruptedException {
        // 调用 Process 类执行 Maven打包命令
        String winMavenCommand = "cmd /c mvn clean package -DskipTests=true";
        String otherMavenCommand = "mvn clean package -DskipTests=true";
        String mavenCommand = winMavenCommand;

        ProcessBuilder processBuilder = new ProcessBuilder(mavenCommand.split(" "));
        processBuilder.directory(new File(projectDir));

        Process process = processBuilder.start();

        // 读取命令的输出
        InputStream inputStream = process.getInputStream();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        while((line = bufferedReader.readLine()) != null) {
            System.out.println(line);
        }
        int exitCode = process.waitFor();
        System.out.println("退出码：" + exitCode);
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        doGenerator("D:\\local-project\\project\\dmj-generator\\dmj-generator-basic");
    }
}
