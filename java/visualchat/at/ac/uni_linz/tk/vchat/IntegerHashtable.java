package at.ac.uni_linz.tk.vchat;

import java.util.*;
import java.io.*;


/**
 * An IntegerHashtable is a HashTable that uses only integers as HashTable keys.
 *
 * @author      Arno Huetter
 * (C)opyright 1997/98 by the Institute for Computer Science, Telecooperation Department, University of Linz
 */

public class IntegerHashtable extends Hashtable implements Serializable {

  static final long serialVersionUID = -3984602671554438960L;


/**
 * Maps the specified key to the specified value in the IntegerHashtable.
 *
 * @param key        the key in the IntegerHashtable
 * @param value      the value to be stored
 */

  public synchronized Object put(int key, Object value) {
    return super.put(new Integer(key), value);
  }


/**
 * Returns the value to which the specified key is mapped in the IntegerHashtable.
 *
 * @param key      the key in the IntegerHashtable
 */

  public synchronized Object get(int key) {
    return super.get(new Integer(key));
  }


/**
 * Removes the key (and its corresponding value) from the IntegerHashtable.
 *
 * @param key      the key that needs to be removed
 */

  public synchronized Object remove(int key) {
    return super.remove(new Integer(key));
  }


/**
 * Tests if the specified object is a key in the IntegerHashtable.
 *
 * @param key      the possible key
 */

  public synchronized boolean containsKey(int key) {
    return super.containsKey(new Integer(key));
  }


/**
 * Maps the next available key to the specified value in the IntegerHashtable.
 *
 * @param value      the value to be stored
 */

  public synchronized Object put(Object value) {
    return super.put(new Integer(getMaxKey() + 1), value);
  }


/**
 * Returns the value to which the last key is mapped in the IntegerHashtable.
 */

  public synchronized Object getLast() {
    return get(getMaxKey());
  }


/**
 * Returns the the first key that is mapped to a specified value.
 *
 * @param value      the value whose key is to be returned
 */

  public synchronized int getKey(Object value) {
    Enumeration keyEnum;
    int key;
    keyEnum = keys();

    while(keyEnum.hasMoreElements()) {
      key = ((Integer)keyEnum.nextElement()).intValue();
      if (get(key).equals(value))
        return key;
    }
    return -1;
  }


/**
 * Returns the last key in the IntegerHashtable.
 */

  public synchronized int getMaxKey() {
    Enumeration keyEnum;
    int key;

    keyEnum = keys();
    key = 0;

    while(keyEnum.hasMoreElements())
      key = Math.max(((Integer)keyEnum.nextElement()).intValue(), key);

    return key;
  }

}
