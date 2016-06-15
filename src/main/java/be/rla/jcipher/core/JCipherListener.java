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

    @Override
    public void filesDropped(final File[] files) {
        if (!busy) {
            synchronized (JCipherListener.class) {
                busy = true;
                try {
                    if (files != null && files.length == 1 && files[0].isFile()) {
                        JCipherFrame.getInstance().setAlwaysOnTop(true);

                        try {
                            JCipher.getInstance().loadKey();
                        } catch (Exception e2) {
                            e2.printStackTrace();
                            JCipherFrame.getInstance().dispose();
                        }

                        byte[] buffer = null;
                        byte[] fromB64 = null;
                        try (InputStream is = new BufferedInputStream(new FileInputStream(files[0]))) {
                            buffer = new byte[is.available()];
                            IOUtils.read(is, buffer);
                            try {
                                fromB64 = Base64.getDecoder().decode(buffer);
                                if (!JCipher.getInstance().isCryptContent(fromB64)) {
                                    fromB64 = null;
                                }
                            } catch (IllegalArgumentException e) {
                                // is not Base64
                            }
                        } catch (Exception e1) {
                            e1.printStackTrace();
                        }
                        if (fromB64 == null && buffer != null && buffer.length > 0) {
                            int resp = JOptionPane.showConfirmDialog(JCipherFrame.getInstance(), "Do you REALLY want to crypt this file ?");
                            boolean ok = false;
                            if (resp == JOptionPane.OK_OPTION) {
                                try (OutputStream os = new BufferedOutputStream(new FileOutputStream(files[0]))) {
                                    IOUtils.write(JCipher.getInstance().crypt(buffer), os);
                                    ok = true;
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                            if (ok) {
                                JOptionPane.showMessageDialog(JCipherFrame.getInstance(), "The file has been successfully encrypted :-)", "JCipher",
                                        JOptionPane.INFORMATION_MESSAGE);
                            } else {
                                JOptionPane.showMessageDialog(JCipherFrame.getInstance(), "The file has been NOT encrypted !", "JCipher", JOptionPane.WARNING_MESSAGE);
                            }
                        } else {
                            byte[] datas = null;
                            try {
                                datas = JCipher.getInstance().decrypt(fromB64);
                            } catch (Exception e) {
                                JOptionPane.showMessageDialog(JCipherFrame.getInstance(), "Cannot decrypt this file ! Sorry :(", "JCipher Error", JOptionPane.ERROR_MESSAGE);
                            }
                            if (datas != null) {
                                String filename = files[0].getName();
                                String name = filename.substring(0, filename.lastIndexOf("."));
                                String ext = filename.substring(filename.lastIndexOf("."));
                                try {
                                    File outputFile = File.createTempFile(name + "_", ext);
                                    try (OutputStream os = new BufferedOutputStream(new FileOutputStream(outputFile))) {
                                        IOUtils.write(datas, os);
                                    }
                                    outputFile.deleteOnExit();
                                    switch (ext) {
                                        case ".txt":
                                        case ".zip":
                                        default:
                                            Desktop.getDesktop().open(outputFile);
                                            break;
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                } finally {
                    busy = false;
                    JCipherFrame.getInstance().setAlwaysOnTop(false);
                }
            }
        }
    }
}
