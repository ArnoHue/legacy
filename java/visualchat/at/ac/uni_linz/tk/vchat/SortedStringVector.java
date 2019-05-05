package at.ac.uni_linz.tk.vchat;

import java.util.*;
import java.text.*;
import java.io.*;

public class SortedStringVector implements Serializable {

  private Vector vec;

	private static transient Collator col = Collator.getInstance();

	private static boolean isGT(String s, String t) {
	  return col.compare(s, t) > 0;
	}

	private static boolean isGTE(String s, String t) {
	  return col.compare(s, t) >= 0;
	}

	private static boolean isLS(String s, String t) {
	  return col.compare(s, t) < 0;
	}

	private static boolean isLSE(String s, String t) {
	  return col.compare(s, t) <= 0;
	}

	private static boolean isEqu(String s, String t) {
	  return col.compare(s, t) == 0;
	}


  public SortedStringVector(int size) {
    vec = new Vector(size);
  }

  public void addElement(String str) {
    vec.addElement(str);
  }

  public boolean contains(String str) {
    return vec.contains(str);
  }

  public boolean removeElement(String str) {
    return vec.removeElement(str);
  }

  public String elementAt(int index) {
    return (String)vec.elementAt(index);
  }

  public void sort() {
	String[] arr = new String[vec.size()];
	vec.copyInto(arr);
    quicksort(arr, 0, arr.length - 1);
    vec = new Vector(arr.length);
    for (int i = 0; i < arr.length; i++) {
		vec.addElement(arr[i]);
	}
  }

  public int size() {
    return vec.size();
  }

  private static void quicksort(String[] arr, int leftBound, int rightBound) {

    if (arr.length < 2)
      return;
    if (leftBound >= rightBound)
      return;

    String pivot = arr[rightBound];

    int leftIndex = leftBound;
    int rightIndex = rightBound - 1;

    while(leftIndex <= rightIndex) {
      while ((leftIndex <= rightIndex) && (isLSE(arr[leftIndex], pivot)))
	    leftIndex++;
      while ((rightIndex >= leftIndex) && (isGTE(arr[rightIndex], pivot)))
	    rightIndex--;
      if (leftIndex < rightIndex)
	    swap(arr, leftIndex, rightIndex);
    }
    swap(arr, leftIndex, rightBound);
    quicksort(arr, leftBound, leftIndex - 1);
    quicksort(arr, leftIndex + 1, rightBound);
  }

  private static void swap(String[] arr, int a, int b) {
    String temp = arr[b];
    arr[b] = arr[a];
    arr[a] = temp;
  }

}
