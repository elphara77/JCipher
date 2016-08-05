package be.rla.jcipher.core;

import java.awt.Desktop;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Base64;

import javax.swing.JOptionPane;

import org.apache.commons.io.IOUtils;

import be.rla.jcipher.gui.JCipherFrame;
import net.iharder.dnd.FileDrop.Listener;

public class JCipherListener implements Listener {

    private boolean busy = false;

    private File outputFile = null;

    private static JCipherListener instance = new JCipherListener();

    private JCipherListener() {
    }

    public static JCipherListener getInstance() {
        if (instance == null) {
            instance = new JCipherListener();
        }
        return instance;
    }

    @Override
    public void filesDropped(final File[] files) {
        if (!busy) {
            synchronized (JCipherListener.class) {
                busy = true;
                try {
                    if (files != null && files.length == 1 && files[0].isFile()) {

                        JCipherFrame.getInstance().setAlwaysOnTop(true);
                        JCipherFrame.getInstance().getLabel().setText("Treating file " + files[0].getName());
                        JCipherFrame.getInstance().getProgressBar().setVisible(true);
                        JCipherFrame.getInstance().getProgressBar().setMinimum(0);
                        JCipherFrame.getInstance().getProgressBar().setMaximum(100);

                        try {
                            JCipher.getInstance().loadKey();
                            JCipherFrame.getInstance().getProgressBar().setValue(10);
                        } catch (Exception e2) {
                            e2.printStackTrace();
                            JCipherFrame.getInstance().dispose();
                        }

                        byte[] buffer = null;
                        byte[] fromB64 = null;
                        try (InputStream is = new BufferedInputStream(new FileInputStream(files[0]))) {
                            buffer = new byte[is.available()];
                            JCipherFrame.getInstance().getProgressBar().setValue(20);
                            IOUtils.read(is, buffer);
                            JCipherFrame.getInstance().getProgressBar().setValue(30);
                            try {
                                fromB64 = Base64.getDecoder().decode(buffer);
                                JCipherFrame.getInstance().getProgressBar().setValue(40);
                                if (!JCipher.getInstance().isCryptContent(fromB64)) {
                                    fromB64 = null;
                                }
                                JCipherFrame.getInstance().getProgressBar().setValue(50);
                            } catch (IllegalArgumentException e) {
                                // is not Base64
                            }
                        } catch (Exception e1) {
                            e1.printStackTrace();
                        }
                        if (fromB64 == null && buffer != null && buffer.length > 0) {
                            int resp = JOptionPane.showConfirmDialog(JCipherFrame.getInstance(), "Do you REALLY want to crypt this ?");
                            boolean ok = false;
                            if (resp == JOptionPane.OK_OPTION) {
                                try (OutputStream os = new BufferedOutputStream(new FileOutputStream(files[0]))) {
                                    JCipherFrame.getInstance().getLabel().setText("Encrypting " + files[0].getName() + " please wait!");
                                    JCipherFrame.getInstance().getProgressBar().setValue(70);
                                    IOUtils.write(JCipher.getInstance().crypt(buffer), os);
                                    JCipherFrame.getInstance().getProgressBar().setValue(100);
                                    ok = true;
                                } catch (Throwable e) {
                                    e.printStackTrace();
                                    System.gc();
                                }
                            }
                            if (ok) {
                                JOptionPane.showMessageDialog(JCipherFrame.getInstance(), "This has been successfully encrypted :-)", "JCipher", JOptionPane.INFORMATION_MESSAGE);
                            } else {
                                JOptionPane.showMessageDialog(JCipherFrame.getInstance(), "This has been NOT encrypted !", "JCipher", JOptionPane.WARNING_MESSAGE);
                            }
                        } else {
                            byte[] datas = null;
                            try {
                                JCipherFrame.getInstance().getLabel().setText("Decrypting " + files[0].getName() + " please wait");
                                JCipherFrame.getInstance().getProgressBar().setValue(60);
                                datas = JCipher.getInstance().decrypt(fromB64);
                                JCipherFrame.getInstance().getProgressBar().setValue(70);

                            } catch (Throwable e) {
                                if (e instanceof Error) {
                                    System.gc();
                                    JOptionPane.showMessageDialog(JCipherFrame.getInstance(), "Unable to decrypt : " + e.getMessage(), "JCipher Error", JOptionPane.ERROR_MESSAGE);
                                } else {
                                    JOptionPane.showMessageDialog(JCipherFrame.getInstance(), "Cannot decrypt this file ! Sorry :(", "JCipher Error", JOptionPane.ERROR_MESSAGE);
                                }
                            }
                            if (datas != null) {
                                String filename = files[0].getName();
                                String name = filename.substring(0, filename.lastIndexOf("."));
                                String ext = filename.substring(filename.lastIndexOf("."));
                                setOutputFileContent(datas, name, ext);
                            }
                        }
                    }
                } finally {
                    JCipherFrame.getInstance().getProgressBar().setVisible(false);
                    JCipherFrame.getInstance().getLabel().setText("Drop a file here to crypt or decrypt it !");
                    JCipherFrame.getInstance().setAlwaysOnTop(false);
                    System.gc();
                    busy = false;
                }
            }
        }
    }

    public void textDropped(String text) {
        if (!busy) {
            synchronized (JCipherListener.class) {
                busy = true;
                try {
                    if (!text.isEmpty()) {

                        try {
                            JCipher.getInstance().loadKey();
                        } catch (Exception e) {
                            e.printStackTrace();
                            JCipherFrame.getInstance().dispose();
                        }

                        byte[] buffer = text.getBytes();
                        byte[] fromB64 = null;
                        try {
                            fromB64 = Base64.getDecoder().decode(buffer);
                            try {
                                if (!JCipher.getInstance().isCryptContent(fromB64)) {
                                    fromB64 = null;
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } catch (IllegalArgumentException e) {
                            // is not Base64
                        }
                        if (fromB64 == null && buffer != null && buffer.length > 0) {
                            int resp = JOptionPane.showConfirmDialog(JCipherFrame.getInstance(), "Do you REALLY want to crypt this ?");
                            boolean ok = false;
                            if (resp == JOptionPane.OK_OPTION) {
                                try {
                                    byte[] crypted = JCipher.getInstance().crypt(buffer);
                                    setOutputFileContent(crypted, "Clipboard encrypted", ".txt");
                                    ok = true;
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                            if (!ok) {
                                JOptionPane.showMessageDialog(JCipherFrame.getInstance(), "This has been NOT encrypted !", "JCipher", JOptionPane.WARNING_MESSAGE);
                            }
                        } else {
                            byte[] datas = null;
                            try {
                                datas = JCipher.getInstance().decrypt(fromB64);
                            } catch (Throwable e) {
                                if (e instanceof Error) {
                                    System.gc();
                                    JOptionPane.showMessageDialog(JCipherFrame.getInstance(), "Unable to decrypt : " + e.getMessage(), "JCipher Error", JOptionPane.ERROR_MESSAGE);
                                } else {
                                    JOptionPane.showMessageDialog(JCipherFrame.getInstance(), "Cannot decrypt this ! Sorry :(", "JCipher Error", JOptionPane.ERROR_MESSAGE);
                                }
                            }
                            if (datas != null) {
                                setOutputFileContent(datas, "Clipboard decrypted", ".txt");
                            }
                        }
                    }
                } finally {
                    System.gc();
                    busy = false;
                }
            }
        }
    }

    private void setOutputFileContent(byte[] datas, String filenamePrefix, String filenameSuffix) {
        try {
            setOutputFile(File.createTempFile(filenamePrefix + "_", filenameSuffix));

            try (OutputStream os = new BufferedOutputStream(new FileOutputStream(getOutputFile()))) {
                IOUtils.write(datas, os);
            }

            Desktop.getDesktop().open(getOutputFile());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (getOutputFile() != null && getOutputFile().exists()) {
                getOutputFile().deleteOnExit();
            }
        }
    }

    private File getOutputFile() {
        return outputFile;
    }

    private void setOutputFile(File outputFile) {
        this.outputFile = outputFile;
    }

    public boolean isBusy() {
        return busy;
    }
}
