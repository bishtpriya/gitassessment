import java.io.*;
import java.nio.file.Paths;

public class App {

    public static class gitautomation {

        public static void main(String[] args) throws Exception {
            String repoUrl = "https://github.com/bishtpriya/technical-assessment.git";

            // Use a unique folder name by adding a timestamp
            String localDir = "cloned-repo-" + System.currentTimeMillis();

            // Use .java to avoid .gitignore issues with .txt
            String newFileName = "newfile.java";
            String newFileContent = "public class NewFile { public static void main(String[] args) { System.out.println(\"Hello from new file\"); } }";

            String existingFileName = "README.md";
            String contentToAppend = "\nAppended new content to this repo.";

            cloneRepo(repoUrl, localDir);
            addNewFile(localDir, newFileName, newFileContent); // Now also force-adds the file
            commitAndPush(localDir, "Add new file: " + newFileName);
            updateFile(localDir, existingFileName, contentToAppend);
            commitAndPush(localDir, "Update file: " + existingFileName);
        }

        static void runCommand(String command, File dir) throws IOException, InterruptedException {
            System.out.println("Running command: " + command);
            ProcessBuilder builder = new ProcessBuilder();
            if (System.getProperty("os.name").startsWith("Windows")) {
                builder.command("cmd.exe", "/c", command);
            } else {
                builder.command("bash", "-c", command);
            }
            builder.directory(dir);
            builder.redirectErrorStream(true);
            Process process = builder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                throw new RuntimeException("Command failed: " + command);
            }
        }

        static void cloneRepo(String repoUrl, String localDir) throws IOException, InterruptedException {
            runCommand("git clone " + repoUrl + " " + localDir, new File("."));
        }

        static void addNewFile(String dir, String fileName, String content) throws IOException, InterruptedException {
            File file = Paths.get(dir, fileName).toFile();
            file.getParentFile().mkdirs(); // Ensure directories exist
            System.out.println("Creating file at: " + file.getAbsolutePath());
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                writer.write(content);
            }
            runCommand("git add -f " + fileName, new File(dir)); // Force add to bypass .gitignore
        }

        static void updateFile(String dir, String fileName, String content) throws IOException, InterruptedException {
            File file = new File(dir + "/" + fileName);
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
                writer.write(content);
            }
            runCommand("git add " + fileName, new File(dir));
        }

        static void commitAndPush(String dir, String commitMessage) throws IOException, InterruptedException {
            runCommand("git status", new File(dir)); // Show what's going to commit
            runCommand("git commit -m \"" + commitMessage + "\"", new File(dir));
            runCommand("git push", new File(dir));
        }
    }
}
