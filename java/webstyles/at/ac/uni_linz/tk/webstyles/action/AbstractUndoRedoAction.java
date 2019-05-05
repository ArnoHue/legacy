package at.ac.uni_linz.tk.webstyles.action;

import java.io.*;
import java.util.*;
import java.awt.event.*;
import javax.swing.*;

import at.ac.uni_linz.tk.webstyles.*;


public abstract class AbstractUndoRedoAction extends AbstractAction implements Observer {
    
    public static class Snapshot {
        public byte[] buffer;
        public String name;
        
        public Snapshot(byte[] bufferParam, String nameParam) {
            buffer = bufferParam;
            name = nameParam;
        }
    }
    
    public static class SnapshotBuffer extends Observable {
  
        private Vector snapshots;
        private int snapshotIndex;
        private int capacity;
        
        public SnapshotBuffer(int capacityParam) {
            snapshots = new Vector(capacityParam);
            capacity = capacityParam;
            snapshotIndex = -1;
        }
   
        public synchronized void addSnapshot(Snapshot snapshot) {
            for (int i = snapshots.size() - 1; i > snapshotIndex; i--) {
                snapshots.removeElementAt(i);
            }
            if (snapshots.size() == capacity) {
                snapshots.removeElementAt(0);
                snapshots.insertElementAt(snapshot, snapshotIndex);
            }
            else {
                snapshots.insertElementAt(snapshot, ++snapshotIndex);
            }
            setChanged();
            notifyObservers();
        }
   
        public synchronized Snapshot moveToNextSnapshot() {
            if (snapshotIndex + 1 < snapshots.size()) {
                Snapshot snap = (Snapshot)snapshots.elementAt(++snapshotIndex);
                setChanged();
                notifyObservers();
                return snap;
            }
            else {
                return null;
            }
        }
   
        public synchronized Snapshot moveToPreviousSnapshot() {
            if (snapshotIndex > 0) {
                Snapshot snap = (Snapshot)snapshots.elementAt(--snapshotIndex);
                setChanged();
                notifyObservers();
                return snap;
            }
            else {
                return null;
            }
        }

        public synchronized String getNextSnapshotName() {
            if (snapshotIndex + 1 < snapshots.size()) {
                return ((Snapshot)snapshots.elementAt(snapshotIndex + 1)).name;
            }
            else {
                return null;
            }
        }
   
        public synchronized String getSnapshotName() {
            if (snapshotIndex >= 0) {
                return ((Snapshot)snapshots.elementAt(snapshotIndex)).name;
            }
            else {
                return null;
            }
        }
        
        public int getSize() {
            return snapshots.size();
        }
        
        public int getIndex() {
            return snapshotIndex;
        }
    }
    
    public static final int UNDO_HISTORY_SIZE = 10;
    
    protected static SnapshotBuffer snapshotBuffer;
    static {
        snapshotBuffer = new SnapshotBuffer(UNDO_HISTORY_SIZE);
    }
    
    public AbstractUndoRedoAction(String name, Icon icon) {
        super(name, icon);
    }
    
    public static void createSnapshot(String name) {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream(8192);
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(WebStyles.getApplication().getController());
            oos.close();
            snapshotBuffer.addSnapshot(new Snapshot(bos.toByteArray(), name));
        }
        catch (Exception excpt) {
            excpt.printStackTrace();
            // JOptionPane.showMessageDialog(WebStyles.getApplication().getFrame(), excpt.toString(), "Snapshot Error", JOptionPane.ERROR_MESSAGE, null);
        }
    }
    
    public void update(Observable o, Object arg) {
    }

}