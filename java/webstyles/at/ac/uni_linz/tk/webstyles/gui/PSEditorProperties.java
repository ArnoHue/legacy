package at.ac.uni_linz.tk.webstyles.gui;

import java.io.*;
import java.util.*;

public class PSEditorProperties extends Properties {
    
    private static String PROPERTIES_FILE = "webstyles.properties";
    private static PSEditorProperties properties;
    
    public static String[] PROPERTY_FILE_HISTORY = { "FILE_HISTORY_1", "FILE_HISTORY_2", "FILE_HISTORY_3", "FILE_HISTORY_4" };
    public static String PROPERTY_SAVE_FOLDER = "SAVE_FOLDER";
    public static String PROPERTY_GENERIC_CONTENT = "GENERIC_CONTENT";
    public static String PROPERTY_SUFFIX = "_";
    public static String FILE_HISTORY_EMPTY = "<< empty >>";
    
    static {
        properties = new PSEditorProperties();
    }
    
    public static PSEditorProperties getProperties() {
        return properties;
    }
    
    private PSEditorProperties() {
        put(PROPERTY_FILE_HISTORY[0], FILE_HISTORY_EMPTY);
        put(PROPERTY_FILE_HISTORY[1], FILE_HISTORY_EMPTY);
        put(PROPERTY_FILE_HISTORY[2], FILE_HISTORY_EMPTY);
        put(PROPERTY_FILE_HISTORY[3], FILE_HISTORY_EMPTY);
    }
    
    public synchronized void load() {
        try {
            super.load(new FileInputStream(PROPERTIES_FILE));
        }
        catch (IOException excpt) {
            excpt.printStackTrace();
        }
    }
    
    public synchronized void save() {
        try {
            super.save(new FileOutputStream(PROPERTIES_FILE), "WebStyles Properties");
        }
        catch (IOException excpt) {
            excpt.printStackTrace();
        }
    }
}