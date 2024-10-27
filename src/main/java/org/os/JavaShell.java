package org.os;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

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
    private void executeCommand(String command) throws IOException {
        String[] binarytokens = command.split("\\s+");

        // Handle cd as a special command
        if(binarytokens.length <= 2){
            switch (binarytokens[0]) {
                case "cd" -> CD(binarytokens[1]);
                case "pwd" -> PWD();
                case "ls" -> STDprintFunctionOutput(this::LS);
                case "touch" ->touch(binarytokens[1]);
                case "help" -> help();
                case "mkdir"->mkdirCommand(binarytokens);
            }

        } else { // more than two token [ | - >> - > ] tools
            String[] tokens = command.split("\\s+");
            if(command.contains("|")){
                Piping(tokens[0] , tokens[2]);
            } else if (command.contains(">>")) {
                RedirectingWithAppending(tokens[0] , tokens[2]);
            } else if (command.contains(">")) {
                RedirectingWithOverriding(tokens[0] , tokens[2]);
            }
        }
    }
    private void CD(String tokens){
        if (tokens.length() < 2) {
            System.out.println("cd: missing argument");
        } else {
            changeDirectory(tokens);
        }
    }
    private File PWD(){
        return currentDirectory;
    }
    private File[] LS(){
        File[]contents=currentDirectory.listFiles();
        if(contents == null) {
            System.out.println("Directory is inaccessible");
//            for (File content : contents) {
//                System.out.println(content.getName());
//            }
        }
        return contents;
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
    private static void printFileContent(File file) {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }
    }
    private void OutToFile(List<String> output , String filename){
        File file = new File(filename);

        // Use FileWriter in append mode
        try (FileWriter writer = new FileWriter(file, true)) {
            for (String line : output) { // Iterate over each string in the list
                writer.write(line + System.lineSeparator()); // Write to the file and append a new line
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private static void RedirectingWithOverriding(String cmd, String filePath) throws IOException {
        // Start the process
        Process process = new ProcessBuilder(cmd.split("\\s+")).start();

        // Read the output of the process
        InputStream inputStream = process.getInputStream();

        // Append the output to the specified file
        try (FileOutputStream fileOutput = new FileOutputStream(filePath, true)) {
            inputStream.transferTo(fileOutput);
        }
    }

    public static <T> void STDprintFunctionOutput(Supplier<T> function) {
        T result = function.get();  // Get the result

        if (result instanceof File[]) {
            File[] files = (File[]) result;
            Arrays.stream(files)
                    .map(File::getName)  // Get the name of each file
                    .forEach(System.out::println);  // Print each file name
        } else if (result instanceof String) {
            System.out.println((String) result);
        } else if (result instanceof File) {
            printFileContent((File) result);
        } else {
            System.out.println(result);
        }
    }
    //error here
    private void touch(String  name) {
        File newDir=new File(currentDirectory,name);
        if(newDir.mkdir()){
            System.out.println("Created A new Directory at path"+currentDirectory.getAbsolutePath());
        }
        else{
            System.out.println("file already exists");
        }
    }
    private void RedirectingWithAppending( String cmd ,String filePath){
        ProcessBuilder processBuilder = new ProcessBuilder(cmd.split("\\s+"));
        processBuilder.redirectErrorStream(true); // Combine error and output streams

        try {
            Process process = processBuilder.start();

            // Read the output of the command
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            List<String> output = new ArrayList<>();

            String line;
            while ((line = reader.readLine()) != null) {
                output.add(line);
            }
            OutToFile(output , filePath);

            // Wait for the process to complete
            int exitCode = process.waitFor();
            System.out.println("Command executed with exit code: " + exitCode);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
    private void Piping(String cmd1, String cmd2) throws IOException {
        executeCommand(cmd1);
        Process p1 = new ProcessBuilder(cmd1.split("\\s+")).start();

        // Capture the output of the first process
        InputStream inputStream = p1.getInputStream();

        // Start the second process with input from the first process
        Process p2 = new ProcessBuilder(cmd2.split("\\s+")).start();
        OutputStream outputStream = p2.getOutputStream();

        // Pipe the output of p1 to p2
        inputStream.transferTo(outputStream);

        // Close the output stream to indicate end of input
        outputStream.close();

        // Print the final output of the second process
        BufferedReader reader = new BufferedReader(new InputStreamReader(p2.getInputStream()));
        String line;
        while ((line = reader.readLine()) != null) {
            System.out.println(line);
        }
    }

}