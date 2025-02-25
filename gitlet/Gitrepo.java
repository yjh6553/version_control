package gitlet;
import static gitlet.Utils.*;
import java.io.File;
import java.util.TreeMap;
import java.util.Set;
import java.util.ArrayList;
import java.util.Queue;
import java.util.List;
import java.util.LinkedList;

public class Gitrepo {

    /** Current Branch Name (HEAD branch NAME). */
    private String _currBranchName;
    /** Stage Class that has addition and removal.*/
    private Staging _stage;
    /** Path of the current working directory.*/
    private final File _cwd = new File(System.getProperty("user.dir"));
    /**Path of BLOB folder.*/
    private final String _BLOLBPATH = ".gitlet/blobs";
    /**Path of Commits folder.*/
    private final String _COMMITPATH = ".gitlet/commits";
    /**Path of branches folder.*/
    private final String _BRANCHESPATH = ".gitlet/branches";
    /**Path of stage folder.*/
    private final String _STAGEPATH = ".gitlet/staging";
    /**Treemap that has information of head commit of each branch.*/
    private TreeMap<String, String> _branches;


    public Gitrepo() {
        File stageLoc = new File(".gitlet/staging/stage.txt");
        if (stageLoc.exists()) {
            _stage = Utils.readObject(stageLoc, Staging.class);
        }
        File branchLoc = Utils.join(_BRANCHESPATH, "branches.txt");
        File branchName = Utils.join(_BRANCHESPATH, "currentName.txt");
        if (branchName.exists()) {
            _currBranchName = Utils.readContentsAsString(branchName);
        }

    }

    public void init() {
        File gitlet = new File(".gitlet");
        if (gitlet.exists()) {
            System.out.print("A Gitlet version-control system "
                    + "already exists in the current directory.");
        } else {
            new File(".gitlet").mkdir();
            new File(_BLOLBPATH).mkdirs();
            new File(_COMMITPATH).mkdirs();
            new File(_STAGEPATH).mkdirs();
            new File(_BRANCHESPATH).mkdirs();
            new File(_STAGEPATH).mkdirs();
            _currBranchName = "master";
            Commit initCommit = new Commit("initial commit",
                    null, new TreeMap<String, String>());
            Utils.writeObject(Utils.join(_COMMITPATH,
                    initCommit.cSha()), initCommit);
            _branches = new TreeMap<String, String>();
            _branches.put("master", initCommit.cSha());
            File branchFile = Utils.join(_BRANCHESPATH,
                    "branches.txt");
            Utils.writeObject(branchFile, _branches);
            Utils.writeContents(Utils.join(_BRANCHESPATH, "currentName.txt"),
                    _currBranchName);
            File headFile = Utils.join(".gitlet", "head.txt");
            Utils.writeContents(headFile, initCommit.cSha());
            _stage = new Staging();
            Utils.writeObject(Utils.join(_STAGEPATH, "stage.txt"), _stage);
        }
    }

    public void add(String fileName) {
        File newCon = new File(fileName);
        if (newCon.exists()) {
            String blob = Utils.readContentsAsString(newCon);
            String blobID = Utils.sha1(blob);
            File stageFile = Utils.join(_STAGEPATH, "stage.txt");
            if (getCurrCommit().getBlobs().get(fileName) != null
                    && getCurrCommit().getBlobs()
                    .get(fileName).equals(blobID)) {
                if (_stage.getRemoveTree().containsKey(fileName)) {
                    _stage.getRemoveTree().remove(fileName);
                    Utils.writeObject(stageFile, _stage);
                }
                return;
            }
            _stage.getRemoveTree().remove(fileName);
            Utils.writeContents(Utils.join(_BLOLBPATH, blobID), blob);
            _stage.addToAddition(fileName, blobID);
            Utils.writeObject(stageFile, _stage);
        } else {
            System.out.println("File does not exist.");
        }
    }

    public Commit getCurrCommit() {
        File headPointer = Utils.join(".gitlet", "head.txt");
        String commitID = Utils.readContentsAsString(headPointer);
        return Utils.readObject(Utils.join(_COMMITPATH, commitID),
                Commit.class);
    }

    public void commit(String msg, Boolean merge, Commit mergeParent) {
        if (_stage.getAdditionTree().isEmpty() && _stage.getRemoveTree()
                .isEmpty()) {
            System.out.println("No changes added to the commit.");
            return;
        }
        if (msg.equals("")) {
            System.out.print("Please enter a commit message.");
            return;
        }
        @SuppressWarnings("unchecked")
        TreeMap<String, String> newBlob =
                (TreeMap<String, String>) getCurrCommit().getBlobs().clone();
        Set<String> addList = _stage.getAdditionTree().keySet();
        Set<String> removeList = _stage.getRemoveTree().keySet();
        for (String fileName : addList) {
            newBlob.put(fileName, _stage.getAdditionTree().get(fileName));
        }
        for (String fileName : removeList) {
            newBlob.remove(fileName);
        }
        Commit newCommit;
        if (!merge) {
            newCommit = new Commit(msg, getCurrCommit(), newBlob);
        } else {
            newCommit = new Commit(msg, getCurrCommit(), mergeParent,
                    newBlob);
        }
        Utils.writeObject(Utils.join(_COMMITPATH, newCommit.cSha()),
                newCommit);
        @SuppressWarnings("unchecked")
        TreeMap<String, String> branches = readObject(
                join(_BRANCHESPATH, "branches.txt"), TreeMap.class);
        branches.put(_currBranchName, newCommit.cSha());
        Utils.writeObject(Utils.join(_BRANCHESPATH, "branches.txt"),
                branches);
        Utils.writeContents(Utils.join(".gitlet", "head.txt"),
                newCommit.cSha());
        _stage.clear();
        Utils.writeObject(Utils.join(_STAGEPATH, "stage.txt"), _stage);
    }

    private void mergeLog(Commit cur) {
        System.out.println("===");
        System.out.println("commit " + cur.cSha());
        System.out.println("Merge: "
                + cur.getParent().cSha().substring(0, 7) + " "
                + cur.getMergeParent().cSha().substring(0, 7));
        System.out.println("Date: " + cur.getTime());
        System.out.println(cur.getMsg());
        System.out.println();
    }
    private void normalLog(Commit cur) {
        System.out.println("===");
        System.out.println("commit " + cur.cSha());
        System.out.println("Date: " + cur.getTime());
        System.out.println(cur.getMsg());
        System.out.println();
    }

    public void log() {
        Commit cur = getCurrCommit();
        while (cur != null) {
            if (cur.getMergeParent() != null) {
                mergeLog(cur);
            } else if (cur.getMergeParent() == null) {
                normalLog(cur);
            }
            if (cur.getParent() != null) {
                String parentID = cur.getParent().cSha();
                cur = Utils.readObject(Utils.join(_COMMITPATH, parentID),
                        Commit.class);
            } else {
                return;
            }
        }
    }

    public void checkout1(String... args) {
        String fileName = args[2];
        List<String> fileList = Utils.plainFilenamesIn(Utils.join(_cwd));
        if (!fileList.contains(fileName)) {
            System.out.println("File does not exist in that commit.");
            return;
        }
        String blobID = getCurrCommit().getBlobs().get(fileName);
        String contents = readContentsAsString(Utils.join(_BLOLBPATH,
                blobID));
        Utils.writeContents(Utils.join(_cwd, fileName), contents);
    }

    public void checkout2(String... args) {
        if (!args[2].equals("--")) {
            System.out.println("Incorrect operands.");
            return;
        }
        String commitID = args[1];
        List<String> commitIDs = plainFilenamesIn(_COMMITPATH);
        if (commitID.length() < 10) {
            for (String id : commitIDs) {
                if (id.contains(commitID)) {
                    commitID = id;
                    break;
                }
            }
        }
        List<String> cfileList = Utils.plainFilenamesIn(Utils.
                join(_COMMITPATH));
        if (!cfileList.contains(commitID)) {
            System.out.println("No commit with that id exists.");
            return;
        }
        Commit targetCommit = readObject(join(_COMMITPATH, commitID),
                Commit.class);
        String fileName = args[3];
        Set<String> fileList = targetCommit.getBlobs().keySet();
        if (!fileList.contains(fileName)) {
            System.out.println("File does not exist in that commit.");
            return;
        }
        String blobID = targetCommit.getBlobs().get(fileName);
        String contents = readContentsAsString(join(_BLOLBPATH,
                blobID));
        writeContents(join(_cwd, fileName), contents);
    }

    public void checkout3(String... args) {
        String bName = args[1];
        TreeMap<String, String> branches = getBranchMap();
        String commitID = branches.get(bName);
        Set<String> branchList = branches.keySet();
        if (!branchList.contains(bName)) {
            System.out.println("No such branch exists.");
            return;
        }
        if (commitID.equals(readContentsAsString(join(".gitlet",
                "head.txt")))
                && bName.equals(_currBranchName)) {
            System.out.println("No need to checkout the current branch.");
            return;
        }
        Commit targetCommit = readObject(join(_COMMITPATH, commitID),
                Commit.class);
        Commit curCommit = getCurrCommit();
        List<String> cwdFileList = plainFilenamesIn(_cwd);
        for (String file : cwdFileList) {
            if (!curCommit.getBlobs().keySet().contains(file)
                    && targetCommit.getBlobs().keySet().contains(file)) {
                System.out.println("There is an untracked file in the way; "
                        + "delete it, or add and commit it first.");
                return;
            }
        }
        for (String file : cwdFileList) {
            if (curCommit.getBlobs().keySet().contains(file)
                    && !targetCommit.getBlobs().keySet().contains(file)) {
                restrictedDelete(file);
            }
        }
        TreeMap<String, String> blobMap = targetCommit.getBlobs();
        Set<String> targetCommitFiles = targetCommit.getBlobs().keySet();
        for (String file : targetCommitFiles) {
            String contents = readContentsAsString(join(_BLOLBPATH,
                    blobMap.get(file)));
            writeContents(join(_cwd, file), contents);
        }
        _stage.clear();
        branches.put(bName, commitID);
        writeObject(join(_BRANCHESPATH, "branches.txt"), branches);
        writeContents(join(".gitlet", "head.txt"), commitID);
        writeObject(join(_STAGEPATH, "stage.txt"), _stage);
        writeContents(join(_BRANCHESPATH, "currentName.txt"), bName);
    }


    public void rm(String fileName) {
        boolean stageChecker = false;
        boolean commitChecker = false;
        Set<String> addTree = _stage.getAdditionTree().keySet();
        for (String sKey : addTree) {
            if (sKey.equals(fileName)) {
                stageChecker = true;
            }
        }
        Commit cur = getCurrCommit();
        Set<String> blobMap = cur.getBlobs().keySet();
        for (String cKey : blobMap) {
            if (cKey.equals(fileName)) {
                commitChecker = true;
            }
        }
        if (commitChecker) {
            restrictedDelete(join(_cwd, fileName));
            String blobID = cur.getBlobs().get(fileName);
            _stage.addToRemove(fileName, blobID);
            if (stageChecker) {
                _stage.removeFromAddition(fileName);
            }
            writeObject(join(_STAGEPATH, "stage.txt"), _stage);
        } else if (stageChecker) {
            _stage.getAdditionTree().remove(fileName);
            writeObject(join(_STAGEPATH, "stage.txt"), _stage);
        } else {
            System.out.println("No reason to remove the file.");
        }
    }

    public void globalLog() {
        List<String> fileList = plainFilenamesIn(_COMMITPATH);
        for (String id : fileList) {
            Commit cur = readObject(join(_COMMITPATH, id), Commit.class);
            System.out.println("===");
            System.out.println("commit " + cur.cSha());
            System.out.println("Date: " + cur.getTime());
            System.out.println(cur.getMsg());
            System.out.println();
        }
    }

    public void find(String msg) {
        List<String> fileList = plainFilenamesIn(_COMMITPATH);
        int counter = 0;
        for (String id : fileList) {
            Commit cur = readObject(join(_COMMITPATH, id), Commit.class);
            if (cur.getMsg().equals(msg)) {
                counter++;
                System.out.println(id);
            }
        }
        if (counter == 0) {
            System.out.println("Found no commit with that message.");
        }
    }

    public void status() {
        File check = new File(".gitlet");
        if (!check.exists()) {
            System.out.println("Not in an initialized Gitlet directory.");
            return;
        }
        Commit curCommit = getCurrCommit();
        System.out.println("=== Branches ===");
        TreeMap<String, String> branches = getBranchMap();
        Set<String> branchList = branches.keySet();
        for (String branch : branchList) {
            String branchID = branches.get(branch);
            if (curCommit.cSha().equals(branchID)) {
                System.out.println("*" + branch);
            } else {
                System.out.println(branch);
            }
        }
        System.out.println();

        System.out.println("=== Staged Files ===");
        Staging stage = readObject(join(_STAGEPATH, "stage.txt"),
                Staging.class);
        Set<String> addList = stage.getAdditionTree().keySet();
        for (String fileName : addList) {
            System.out.println(fileName);
        }
        System.out.println();

        System.out.println("=== Removed Files ===");
        Set<String> removeList = stage.getRemoveTree().keySet();
        for (String fileName : removeList) {
            System.out.println(fileName);
        }
        System.out.println();
        System.out.println("=== Modifications Not Staged For Commit ===");
        TreeMap<String, String> blobMap = curCommit.getBlobs();
        List<String> result = new ArrayList<>();
        Set<String> fileInCommit = blobMap.keySet();
        List<String> cwdList = plainFilenamesIn(_cwd);
        System.out.println();
        System.out.println("=== Untracked Files ===");
        System.out.println();
    }

    private Staging getStage() {
        Staging curStage = readObject(join(_STAGEPATH, "stage.txt"),
                Staging.class);
        return curStage;
    }

    public void branch(String bName) {
        TreeMap<String, String> treeMap = getBranchMap();
        Set<String> check = treeMap.keySet();
        if (check.contains(bName)) {
            System.out.println("A branch with that name already exists.");
        } else {
            String blobID = readContentsAsString(join(".gitlet",
                    "head.txt"));
            treeMap.put(bName, blobID);
            writeObject(join(_BRANCHESPATH, "branches.txt"), treeMap);
        }
    }

    public void rmBranch(String bName) {
        TreeMap<String, String> treeMap = getBranchMap();
        Set<String> check = treeMap.keySet();
        String headID = readContentsAsString(join(".gitlet",
                "head.txt"));
        if (!check.contains(bName)) {
            System.out.println("A branch with that name does not exists.");
        } else if (_currBranchName.equals(bName)) {
            System.out.println("Cannot remove the current branch.");
        } else {
            treeMap.remove(bName);
            writeObject(join(_BRANCHESPATH, "branches.txt"), treeMap);
        }
    }

    public void reset(String commitID) {
        List<String> commitsList = plainFilenamesIn(join(_COMMITPATH));
        if (!commitsList.contains(commitID)) {
            System.out.println("No commit with that id exists.");
            return;
        }
        Commit targetCommit = readObject(join(_COMMITPATH, commitID),
                Commit.class);
        Commit curCommit = getCurrCommit();
        List<String> cwdFiles = plainFilenamesIn(join(_cwd));
        for (String file : cwdFiles) {
            if (!curCommit.getBlobs().keySet().contains(file)
                    && targetCommit.getBlobs().keySet().contains(file)) {
                System.out.println("There is an untracked file in the way; "
                        + "delete it, or add and commit it first.");
                return;
            }
        }
        for (String file :cwdFiles) {
            if (curCommit.getBlobs().keySet().contains(file)
                    && !targetCommit.getBlobs().keySet().contains(file)) {
                restrictedDelete(join(_cwd, file));
            }
        }
        Set<String> targetFiles = targetCommit.getBlobs().keySet();
        for (String updateFile : targetFiles) {
            String contents = readContentsAsString(
                    join(_BLOLBPATH, targetCommit.getBlobs().get(updateFile)));
            writeContents(join(_cwd, updateFile), contents);
        }
        _stage.clear();
        String headCommitID = readContentsAsString(join(".gitlet",
                "head.txt"));
        TreeMap<String, String> branches = getBranchMap();
        Set<String> bNames = branches.keySet();
        branches.put(_currBranchName, commitID);
        writeObject(join(_BRANCHESPATH, "branches.txt"), branches);
        writeObject(join(_STAGEPATH, "stage.txt"), _stage);
        writeContents(join(".gitlet", "head.txt"), commitID);
    }

    private boolean mergeConditionCheck(String bName) {
        Staging stage = readObject(join(_STAGEPATH, "stage.txt"),
                Staging.class);
        TreeMap<String, String> branches = getBranchMap();
        TreeMap<String, String> addTree = stage.getAdditionTree();
        TreeMap<String, String> removeTree = stage.getRemoveTree();
        if (!addTree.isEmpty() || !removeTree.isEmpty()) {
            System.out.println("You have uncommitted changes.");
            return false;
        } else if (!branches.containsKey(bName)) {
            System.out.println("A branch with that name does not exist.");
            return false;
        } else if (branches.get(bName).equals(
                readContentsAsString(join(".gitlet",
                        "head.txt")))) {
            System.out.println("Cannot merge a branch with itself");
            return false;
        } else {
            return true;
        }
    }

    public String bfs(Commit cbHead, Commit gbHead) {
        List<String> track = new ArrayList<>();
        Queue<Commit> q = new LinkedList<Commit>();
        q.add(gbHead);
        q.add(cbHead);
        while (q.peek() != null) {
            Commit temp = q.poll();
            if (track.contains(temp.cSha())) {
                return temp.cSha();
            }
            track.add(temp.cSha());
            if (temp.getParent() != null) {
                q.add(temp.getParent());
            }
            if (temp.getMergeParent() != null) {
                q.add(temp.getMergeParent());
            }
        }
        return null;
    }

    public List<String> bfs2(Commit cbHead) {
        List<String> track = new ArrayList<>();
        Queue<Commit> q = new LinkedList<Commit>();
        q.add(cbHead);
        while (q.peek() != null) {
            Commit temp = q.poll();
            track.add(temp.cSha());
            if (temp.getMergeParent() != null) {
                q.add(temp.getMergeParent());
            }
            if (temp.getParent() != null) {
                q.add(temp.getParent());
            }
        }
        return track;
    }

    private Commit findSplitPoint(Commit cbHead, Commit gbHead) {
        String gbID = bfs(cbHead, gbHead);
        return readObject(join(_COMMITPATH, gbID), Commit.class);
    }

    private boolean conflictContents(String curBlobID, String givenBlobID,
                                     String fName) {
        String result;
        if (curBlobID == null) {
            String givenContents = readContentsAsString(join(_BLOLBPATH,
                    givenBlobID));
            result = "<<<<<<< HEAD" + "\n";
            result = result + "=======\n";
            result = result + givenContents;
            result = result + ">>>>>>>";
        } else if (givenBlobID == null) {
            String curContents = readContentsAsString(join(_BLOLBPATH,
                    curBlobID));
            result = "<<<<<<< HEAD" + "\n";
            result = result + curContents;
            result = result + "=======\n";
            result = result + ">>>>>>>\n";
        } else {
            String curContents = readContentsAsString(join(_BLOLBPATH,
                    curBlobID));
            String givenContents = readContentsAsString(join(_BLOLBPATH,
                    givenBlobID));
            result = "<<<<<<< HEAD" + "\n";
            result = result + curContents;
            result = result + "=======\n";
            result = result + givenContents;
            result = result + ">>>>>>>\n";
        }
        writeContents(join(_cwd, fName), result);
        add(fName);
        return true;
    }

    private void co2Add(String gbHeadID, String file) {
        checkout2("checkout", gbHeadID, "--", file);
        add(file);
    }

    private TreeMap<String, String> getBMap() {
        File branchLoc = Utils.join(_BRANCHESPATH, "branches.txt");
        @SuppressWarnings("unchecked")
        TreeMap<String, String> branches = readObject(branchLoc, TreeMap.class);
        return branches;
    }

    private boolean mergeCond1(Commit cbHead, Commit gbHead,
                               String bName, Commit splitCommit) {
        for (String file : plainFilenamesIn(_cwd)) {
            if (!cbHead.getBlobs().containsKey(file) && gbHead.getBlobs().
                    containsKey(file)) {
                System.out.println("There is an untracked file in the way; "
                        + "delete it, or add and commit it first.");
                return true;
            }
        }
        if (splitCommit.cSha().equals(cbHead.cSha())) {
            checkout3("checkout", bName);
            System.out.println("Current branch fast-forwarded.");
            return true;
        } else if (bfs2(cbHead).contains(gbHead.cSha())) {
            System.out.println("Given branch is an ancestor"
                    + "of the current branch.");
            return true;
        } else {
            return false;
        }
    }

    public void merge(String bName) {
        if (!mergeConditionCheck(bName)) {
            return;
        }
        Commit cbHead = getCurrCommit();
        Commit gbHead = readObject(join(_COMMITPATH, getBMap().get(bName)),
                Commit.class);
        Commit splitCommit = findSplitPoint(cbHead, gbHead);
        if (mergeCond1(cbHead, gbHead, bName, splitCommit)) {
            return;
        }
        TreeMap<String, String> spB = splitCommit.getBlobs();
        TreeMap<String, String> cB = cbHead.getBlobs();
        TreeMap<String, String> gB = gbHead.getBlobs();
        boolean conflict  = false;
        for (String sF : spB.keySet()) {
            if (cB.containsKey(sF) && gB.containsKey(sF)) {
                if (cB.get(sF).equals(spB.get(sF))
                        && !cB.get(sF).equals(gB.get(sF))) {
                    co2Add(gbHead.cSha(), sF);
                } else if (!spB.get(sF).equals(gB.get(sF)) && !spB.get(sF).
                        equals(cB.get(sF)) && !cB.get(sF).equals(gB.get(sF))) {
                    conflict = conflictContents(cB.get(sF), gB.get(sF), sF);
                }
            }
            if (!gB.containsKey(sF)) {
                if (spB.get(sF).equals(cB.get(sF))) {
                    rm(sF);
                } else if (!spB.get(sF).equals(cB.get(sF))
                        && cB.containsKey(sF)) {
                    conflict = conflictContents(cB.get(sF), gB.get(sF), sF);
                }
            }
            if (!cB.containsKey(sF)) {
                if (!spB.get(sF).equals(gB.get(sF)) && gB.containsKey(sF)) {
                    conflict = conflictContents(cB.get(sF), gB.get(sF), sF);
                }
            }
        }
        for (String f : gB.keySet()) {
            if (!spB.containsKey(f) && !cB.containsKey(f)) {
                co2Add(gbHead.cSha(), f);
            }
            if (!spB.containsKey(f) && cB.containsKey(f)
                    && !gB.get(f).equals(cB.get(f))) {
                conflict = conflictContents(cB.get(f), gB.get(f), f);
            }
        }
        mergeResult(bName, gbHead, conflict);
    }

    private void mergeResult(String bName, Commit gbHead, Boolean conflict) {
        if (conflict) {
            System.out.println("Encountered a merge conflict.");
        }
        commit("Merged " + bName + " into "
                + _currBranchName + ".", true, gbHead);
    }

    public TreeMap<String, String> getBranchMap() {
        @SuppressWarnings("unchecked")
        TreeMap<String, String> branches = readObject(
                join(_BRANCHESPATH, "branches.txt"), TreeMap.class);
        return branches;
    }
}
