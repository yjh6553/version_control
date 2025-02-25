package gitlet;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TreeMap;
import java.io.Serializable;

public class Commit implements Serializable {
    /**Unix Time when commit executed.*/
    private String _time;
    /**Message of the commit.*/
    private String _message;
    /**Parent commit of the current commit.*/
    private Commit _parent;
    /**TreeMap that stores information about contents.
     * Key : file name
     * value : blobID
     */
    private TreeMap<String, String> _blob;
    /**Parent commit that made by merge command.*/
    private Commit _mergeParent = null;

    public Commit(String message, Commit parent,
                  TreeMap<String, String> blobMap) {
        _message = message;
        _parent = parent;
        SimpleDateFormat ft =
                new SimpleDateFormat("E MMM dd hh:mm:ss yyyy Z");
        Date currentTime = new Date();
        _time = ft.format(currentTime);
        _blob = blobMap;
    }

    public Commit(String message, Commit parent1,
                  Commit parent2, TreeMap<String, String> blobMap) {
        _message = message;
        _parent = parent1;
        SimpleDateFormat ft =
                new SimpleDateFormat("E MMM dd hh:mm:ss yyyy Z");
        Date currentTime = new Date();
        _time = ft.format(currentTime);
        _blob = blobMap;
        _mergeParent = parent2;
    }

    public String getTime() {
        return this._time;
    }

    public String getMsg() {
        return this._message;
    }

    public Commit getParent() {
        return this._parent;
    }

    public TreeMap<String, String> getBlobs() {
        return this._blob;
    }

    public void setMergeParent(Commit mergeParent) {
        _mergeParent = mergeParent;
    }

    public Commit getMergeParent() {
        return this._mergeParent;
    }

    public String cSha() {
        byte[] convert = Utils.serialize(this);
        return Utils.sha1(convert);
    }
}
