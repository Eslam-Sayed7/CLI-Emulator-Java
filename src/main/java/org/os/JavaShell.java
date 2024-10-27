package org.os;

import java.io.*;
import java.util.Objects;
import java.util.stream.StreamSupport;

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
            case "help"->help();
            case "mkdir" ->mkdirCommand(tokens);
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
    private void mkdirCommand(String[]  name) {
        for (int i=1 ;  i < name.length ; ++i) {
            File newDir = new File(currentDirectory, name[i]);
            if (newDir.mkdir()) {
                System.out.println("Created A new Directory at path" + currentDirectory.getAbsolutePath());
            } else {
                System.out.println("file already exists");
            }
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
    private void help(){
        String file="help.txt";
        System.out.println(file);
        try(BufferedReader br=new BufferedReader(new FileReader(file))){
            String line;
            while((line=br.readLine())!=null){
                System.out.println(line);
            }
        }
        catch (IOException e){
            System.out.println("Error While reading the file");
        }
    }

}