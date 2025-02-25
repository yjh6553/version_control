package gitlet;

import java.io.Serializable;
import java.util.TreeMap;

public class Staging implements Serializable {
    /**Treemap of addition of staging area.*/
    private TreeMap<String, String> _addition;
    /**Treemap of removal of staging area.*/
    private TreeMap<String, String> _remove;

    public Staging() {
        _addition = new TreeMap<>();
        _remove = new TreeMap<>();
    }

    public TreeMap<String, String> getAdditionTree() {
        return _addition;
    }

    public TreeMap<String, String> getRemoveTree() {
        return _remove;
    }

    public void addToAddition(String key, String value) {
        _addition.put(key, value);
    }

    public void removeFromAddition(String key) {
        _addition.remove(key);
    }

    public void addToRemove(String key, String value) {
        _remove.put(key, value);
    }

    public void removeFromRemove(String key) {
        _remove.remove(key);
    }

    public void clear() {
        _addition = new TreeMap<>();
        _remove = new TreeMap<>();
    }
}
