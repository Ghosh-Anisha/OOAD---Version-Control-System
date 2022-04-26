import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.io.File;
import java.io.IOException;

public class Repo {
    private String HEAD = "master";
    private StagingArea stage;
    private File workingDir;

    public Repo() {
        workingDir = new File(System.getProperty("user.dir"));
        String pathToHead = ".agile/branches/HEAD.txt";
        if (new File(pathToHead).exists()) {
            HEAD = SerializeUtils.readStringFromFile(pathToHead);
        }
        String pathToStage = ".agile/staging/stage.txt";
        if (new File(pathToStage).exists()) {
            stage = SerializeUtils.deserialize(pathToStage, StagingArea.class);
        }
    }

    public void init() {
        File git = new File(".agile");
        if (git.exists()) {
            System.out.println("A version-control system"
                    + " already exists in the current directory.");
        } else {
            new File(".agile").mkdirs();
            new File(".agile/blobs").mkdirs();
            new File(".agile/branches").mkdirs();
            new File(".agile/commits").mkdirs();
            new File(".agile/staging").mkdirs();
            new File(".agile/global-log").mkdirs();

            // Initializes default commit saved to /commits directory with SHA1 as name.
            Commit initialCommit = new Commit("initial commit", new HashMap<>(), null);
            SerializeUtils.storeObjectToFile(initialCommit,
                    ".agile/commits/" + initialCommit.getOwnHash() + ".txt");

            // Makes a master branch file in /branches with initial commit SHA1 String as contents.
            String pathToMaster = ".agile/branches/master.txt";
            new File(pathToMaster);
            SerializeUtils.writeStringToFile(initialCommit.getOwnHash(), pathToMaster, false);

            // Makes a HEAD text file in /branches, with the name of branch as contents.
            String pathToHead = ".agile/branches/HEAD.txt";
            new File(pathToHead);
            SerializeUtils.writeStringToFile("master", pathToHead, false);

            // Makes a StagingArea Object with an empty HashMap of added and changed files,
            // as well as an empty ArrayList of removed files.
            stage = new StagingArea();
            new File(".agile/staging/stage.txt");
            SerializeUtils.storeObjectToFile(stage, ".agile/staging/stage.txt");
        }
    }

    public String getHEAD() {
        return HEAD;
    }

    public StagingArea getStage() {
        return stage;
    }

    public void add(String fileName) {
        File toAdd = new File(fileName);
        File findFile = findFile(fileName, workingDir);
        if (toAdd.exists()) {
            byte[] blob = Utils.readContents(toAdd);
            String blobHash = Utils.sha1(blob);
            if (getCurrentCommit().getBlobs().get(fileName) != null
                    && getCurrentCommit().getBlobs().get(fileName).equals(blobHash)) {
                if (stage.getRemovedFiles().contains(fileName)) {
                    stage.getRemovedFiles().remove(fileName);
                    SerializeUtils.storeObjectToFile(stage, ".agile/staging/stage.txt");
                }
                return;
            }
            if (stage.getRemovedFiles().contains(fileName)) {
                stage.getRemovedFiles().remove(fileName);
            }
            Utils.writeContents(new File(".agile/blobs/" + blobHash + ".txt"), blob);
            stage.add(fileName, blobHash);
            SerializeUtils.storeObjectToFile(stage, ".agile/staging/stage.txt");
            System.out.println("File added successfully!");
        } else {
            System.out.print("File does not exist.");
        }
    }

    public void commitment(String msg) {
        if (stage.getAddedFiles().isEmpty() && stage.getRemovedFiles().isEmpty()) {
            System.out.print("No changes added to the commit.");
            return;
        } else if (msg.equals("")) {
            System.out.print("Please enter a commit message.");
            return;
        }
        Commit curr = getCurrentCommit();
        HashMap<String, String> copiedBlobs = (HashMap) curr.getBlobs().clone();
        ArrayList<String> filesToAdd = new ArrayList<>(stage.getAddedFiles().keySet());
        for (String fileName : filesToAdd) {
            copiedBlobs.put(fileName, stage.getAddedFiles().get(fileName));
        }
        for (String fileToRemove : stage.getRemovedFiles()) {
            copiedBlobs.remove(fileToRemove);
        }
        Commit newC = new Commit(msg, copiedBlobs, curr.getOwnHash());
        SerializeUtils.writeStringToFile(newC.getOwnHash(),
                ".agile/branches/" + HEAD + ".txt", false);
        SerializeUtils.storeObjectToFile(newC,
                ".agile/commits/" + newC.getOwnHash() + ".txt");
        stage.clear();
        SerializeUtils.storeObjectToFile(stage, ".agile/staging/stage.txt");
    }

    public void rm(String fileName) {
        boolean isStaged = stage.getAddedFiles().containsKey(fileName);
        Commit curr = getCurrentCommit();
        boolean isTracked = false;
        ArrayList<String> committedFiles = new ArrayList<>(curr.getBlobs().keySet());
        for (String f : committedFiles) {
            if (f.equals(fileName)) {
                isTracked = true;
            }
        }
        if (isTracked) {
            Utils.restrictedDelete(fileName);
            stage.addToRemovedFiles(fileName);
            if (isStaged) {
                stage.getAddedFiles().remove(fileName);
            }
            SerializeUtils.storeObjectToFile(stage, ".agile/staging/stage.txt");
        } else if (isStaged) {
            stage.getAddedFiles().remove(fileName);
            SerializeUtils.storeObjectToFile(stage, ".agile/staging/stage.txt");
        } else {
            System.out.print("No reason to remove the file.");
        }
    }

    public void log() {
        Commit curr = getCurrentCommit();
        while (curr != null) {
            System.out.println("===");
            System.out.println("Commit " + curr.getOwnHash());
            System.out.println(curr.getDatetime());
            System.out.println(curr.getMessage());
            System.out.println();
            if (curr.getParentHash() != null) {
                curr = SerializeUtils.deserialize(".agile/commits/"
                        + curr.getParentHash() + ".txt", Commit.class);
            } else {
                break;
            }
        }
    }

    public void global() {
        File gl = new File(".agile/global-log/gl.txt");
        try (BufferedReader br = new BufferedReader(new FileReader(gl))) {
            String line = null;
            while ((line = br.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException excp) {
            return;
        }
    }

    public void branch(String branchName) {
        File branchFile = new File(".agile/branches/" + branchName + ".txt");
        if (branchFile.exists()) {
            System.out.print("A branch with that name already exists.");
            return;
        }
        String sha1 = SerializeUtils.readStringFromFile(".agile/branches/" + HEAD + ".txt");
        SerializeUtils.writeStringToFile(sha1,
                ".agile/branches/" + branchName + ".txt",
                false);
    }

    public void rmb(String branchName) {
        if (branchName.equals(SerializeUtils.readStringFromFile(".agile/branches/HEAD.txt"))) {
            System.out.print("Cannot remove the current branch.");
            return;
        }
        File branchFile = new File(".agile/branches/" + branchName + ".txt");
        if (!branchFile.delete()) {
            System.out.print("A branch with that name does not exist.");
        }
    }
    
    public File findFile(String fileName, File dir) throws IllegalArgumentException {
        File[] fileList = dir.listFiles();
        for (File f : fileList) {
            if (f.getName().equals(fileName)) {
                return f;
            }
        }
        return null;
    }

    public Commit getCurrentCommit() {
        String hash = SerializeUtils.readStringFromFile(".agile/branches/" + HEAD + ".txt");
        return SerializeUtils.deserialize(".agile/commits/" + hash + ".txt", Commit.class);
    }
}