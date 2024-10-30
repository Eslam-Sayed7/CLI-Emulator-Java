package org.os;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.lang.*;
import java.util.List;
import java.util.function.Supplier;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;


public class JavaShell {

    private File currentDirectory;

    public JavaShell() throws IOException {
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

    private void CD(String tokens){
        if (tokens.length() < 2) {
            System.out.println("cd: missing argument");
        } else {
            changeDirectory(tokens);
        }
    }
    private String PWD(){
        return currentDirectory.toString();
    }
    private File[] LS(){
        File[]contents = currentDirectory.listFiles();
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
    public static void RedirectingWithOverriding(String cmd, String filePath) throws IOException {
        // Start the process
        Process process = new ProcessBuilder(cmd.split("\\s+")).start();

        // Read the output of the process
        InputStream inputStream = process.getInputStream();

        // Append the output to the specified file
        try (FileOutputStream fileOutput = new FileOutputStream(filePath, false)) {
            inputStream.transferTo(fileOutput);
        }
    }

    public void touch(String  name) {
        File newDir=new File(currentDirectory,name);
        try{
            if(newDir.createNewFile()){
                System.out.println("Created A new Directory at path"+currentDirectory.getAbsolutePath());
            }
            else{
                System.out.println("file already exists");
            }
        }
        catch (IOException e){
            System.out.println("Error while Making the file");
        }

    }
    private void rm(String name) {
        File file = new File(currentDirectory, name);
        if (file.isFile() && file.delete()) {
            System.out.println("Deleted file: " + file.getAbsolutePath());
        } else {
            System.out.println("File does not exist or failed to delete");
        }
    }
    private void rmdir(String name) {
        File dir = new File(currentDirectory, name);
        if (dir.isDirectory() && dir.delete()) {
            System.out.println("Deleted directory: " + dir.getAbsolutePath());
        } else {
            System.out.println("Directory does not exist, is not empty, or failed to delete");
        }
    }

    private void mv(String sourceName, String targetName) {
        File source = new File(currentDirectory, sourceName);
        File target = new File(currentDirectory, targetName);
        if (source.exists() && source.renameTo(target)) {
            System.out.println("Moved/Renamed to: " + target.getAbsolutePath());
        } else {
            System.out.println("Failed to move/rename file or directory");
        }
    }
    public void RedirectingWithAppending( String cmd ,String filePath){
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
    public void Piping(String cmd1, String cmd2) throws IOException {
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

    public void Cat(String fileName) {
        File file = new File(currentDirectory, fileName);
        if (file.exists() && file.isFile()) {
            printFileContent(file);
        } else {
            System.out.println("cat: " + fileName + ": No such file");
        }
    }

    private File[] LS(boolean showAll, boolean reverseOrder) {
        File[] contents = currentDirectory.listFiles();

        if (contents == null) {
            System.out.println("Directory is inaccessible");
            return new File[0];
        }

        // Filter out hidden files if showAll is false
        if (!showAll) {
            contents = Arrays.stream(contents)
                    .filter(file -> !file.isHidden())
                    .toArray(File[]::new);
        }

        // Reverse order if reverseOrder is true
        if (reverseOrder) {
            Arrays.sort(contents, (f1, f2) -> f2.getName().compareTo(f1.getName()));
        } else {
            Arrays.sort(contents, (f1, f2) -> f1.getName().compareTo(f2.getName()));
        }

        return contents;
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
            if(!command.contains("|") && !command.contains(">>") && !command.contains(">")){
                switch (binarytokens[0]) {
                    case "cd" -> CD(binarytokens[1]);
                    case "pwd" -> STDprintFunctionOutput(this::PWD);
                    case "ls" -> STDprintFunctionOutput(this::LS);
                    case "touch" ->touch(binarytokens[1]);
                    case "rm" -> rm(binarytokens[1]);
                    case "rmdir" -> rmdir(binarytokens[1]);
                    case "mv" -> mv(binarytokens[1], binarytokens[2]);
                    case "help" -> help();
                    case "mkdir"->mkdirCommand(binarytokens);
                    case "cat" -> Cat(binarytokens[1]);
                    case "ls-a" -> STDprintFunctionOutput(() -> LS(true, false));
                    case "ls-r" -> STDprintFunctionOutput(() -> LS(false, true));
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
}