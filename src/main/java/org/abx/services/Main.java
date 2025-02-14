package org.abx.services;

import org.abx.util.StreamUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;

public class Main {


    public static void startService(JSONObject service) {
        // Get the path to the current Java executable
        String javaHome = System.getProperty("java.home");
        String javaBin = Paths.get(javaHome, "bin", "java").toString();

        // Path to the JAR file of the subsequent process
        JSONArray jsonJarPath =service.getJSONArray("classpath");
        StringBuilder jarPath = new StringBuilder();
        for (int i = 0; i < jsonJarPath.length();++i){
            jarPath.append(jsonJarPath.getString(i)).append(File.pathSeparator);
        }

        // Main class of the JAR (optional if defined in the JAR manifest)
        String mainClass = service.getString("class");

        JSONArray params = service.getJSONArray("params");
        String[] args = new String[4+params.length()];
        args[0]=javaBin;
        args[1]="-cp";
        args[2]=jarPath.toString();
        args[3]=mainClass;
        for (int i = 0; i< params.length();++i){
            args[4+i]=params.getString(i);
        }
        // Construct the command to launch the process
        ProcessBuilder processBuilder = new ProcessBuilder(args
        ).directory(new File(service.getString("path")));
        System.out.println(Arrays.toString(args));
        // Redirect output and error streams
        processBuilder.inheritIO();
        try {
            // Start the process
            Process process = processBuilder.start();

            // Wait for the process to finish (optional)
            int exitCode = process.waitFor();
            System.out.println("Process exited with code: " + exitCode);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) throws Exception {
        String config = StreamUtils.readStream(new FileInputStream("settings.json"));
        JSONObject jsonConfig = new JSONObject(config);
        System.out.println("Starting services ");
        JSONArray jsonServices = jsonConfig.getJSONArray("services");
        for (int i = 0; i < jsonServices.length(); ++i) {
            startService(jsonServices.getJSONObject(i));
        }
    }
}