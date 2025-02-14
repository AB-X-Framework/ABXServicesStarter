package org.abx.services;

import org.abx.util.StreamUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Properties;

public class Main {


    public static void start(JSONObject service) {
        // Get the path to the current Java executable
        String javaHome = System.getProperty("java.home");
        String javaBin = Paths.get(javaHome, "bin", "java").toString();

        // Path to the JAR file of the subsequent process
        String jarPath = service.getString("file");

        // Main class of the JAR (optional if defined in the JAR manifest)
        String mainClass = service.getString("class");

        // Construct the command to launch the process
        ProcessBuilder processBuilder = new ProcessBuilder(
                javaBin, "-cp", jarPath, mainClass
        ).directory(new File(service.getString("path")));
        System.out.println(processBuilder.command());

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
        for (int i = 0; i< jsonServices.length();++i){
            start(jsonServices.getJSONObject(i));
        }
    }
}