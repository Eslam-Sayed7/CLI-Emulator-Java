package org.os;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class JavaShell {

    private File currentDirectory;

    public JavaShell() {
        // Set initial directory to the C: drive on Windows
        // String initialDir = "home/";

        // Validate the C: drive path
        File dir = new File(System.getProperty("user.dir"));
//        File dir = new File(initialDir);
        if (dir.exists() && dir.isDirectory()) {
            this.currentDirectory = dir;
            System.out.println("Initial directory set to: " + dir.getAbsolutePath());
        } else {
            System.err.println("Error: Unable to access C:\\ directory.");
            System.exit(1); // Exit if C:\ is not accessible
        }
    }


    public void runShell() {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String command;

        System.out.println("Welcome to JavaShell. Type 'exit' to quit.");

        while (true) {
            try {
                // Prompt for input
                System.out.print(currentDirectory+"> ");
                command = reader.readLine();

                // Exit condition
                if (command.trim().equalsIgnoreCase("exit")) {
                    System.out.println("Exiting JavaShell...");
                    break;
                }

                // Execute the command
                executeCommand(command);

            } catch (IOException e) {
                System.err.println("Error reading input: " + e.getMessage());
            }
        }
    }

    // Execute shell commands
    private void executeCommand(String command) {
        String[] tokens = command.split("\\s+");

        // Handle cd as a special command
        switch (tokens[0]) {
            case "cd" -> CD(tokens[1]);
            case "pwd" -> PWD();
            case "ls" -> LS();
            case "mkdir" ->mkdirCommand(tokens[1]);
        }

    }
    private void CD(String tokens){
        if (tokens.length() < 2) {
            System.out.println("cd: missing argument");
        } else {
            changeDirectory(tokens);
        }
    }
    private void PWD(){
        System.out.println(currentDirectory);
    }
    private void LS(){
        File[]contents=currentDirectory.listFiles();
        if(contents!=null) {
            for (File content : contents) {
                System.out.println(content.getName());
            }
        }
        else{
            System.out.println("Directory is inaccessible");
        }
    }
    private void mkdirCommand(String  name) {
        File newDir=new File(currentDirectory,name);
        try {
            if(newDir.createNewFile()){
                System.out.println("Created A new File at path"+currentDirectory.getAbsolutePath());
            }
            else{
                System.out.println("file already exists");
            }
        }
        catch (IOException e) {
            System.out.println("Error while creating the file: " + e.getMessage());
        } catch (SecurityException e) {
            System.out.println("Permission denied: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("An unexpected error occurred: " + e.getMessage());
        }

    }
    // Handle Changing of directory
    private void changeDirectory(String path) {
        File newDir = new File(currentDirectory, path);
        if(path.equals("..")){
            String parentPath = currentDirectory.getParent(); // Get the parent directory path

            if (parentPath != null) {
                currentDirectory = new File(parentPath);
                System.out.println("Directory changed to: " + currentDirectory.getAbsolutePath());

            } else {
                System.out.println("Already at the root directory.");
            }
            return;
        }
        if (newDir.exists() && newDir.isDirectory()) {
            currentDirectory = newDir;
            System.out.println("Directory changed to: " + currentDirectory.getAbsolutePath());
        } else {
            System.out.println("cd: no such directory: " + path);
        }
    }

}