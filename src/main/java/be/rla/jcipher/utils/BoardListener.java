package be.rla.jcipher.utils;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.Transferable;

class BoardListener extends Thread implements ClipboardOwner {
    Clipboard sysClip = Toolkit.getDefaultToolkit().getSystemClipboard();

    public void run() {
      Transferable trans = sysClip.getContents(this);
      regainOwnership(trans);
      System.out.println("Listening to board...");
      while(true) {}
    }

    public void lostOwnership(Clipboard c, Transferable t) {
      Transferable contents = sysClip.getContents(this); //EXCEPTION
      processContents(contents);
      regainOwnership(contents);
    }

    void processContents(Transferable t) {
      System.out.println("Processing: " + t);
    }

    void regainOwnership(Transferable t) {
      sysClip.setContents(t, this);
    }

    public static void main(String[] args) {
      BoardListener b = new BoardListener();
      b.start();
    }
  }