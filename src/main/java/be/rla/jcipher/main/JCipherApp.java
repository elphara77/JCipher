package be.rla.jcipher.main;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import be.rla.jcipher.core.JCipher;
import be.rla.jcipher.gui.JCipherFrame;

public class JCipherApp {

    public static final String VERSION = "JCipher v1.1 (c) R.Laporte";

    private static Timer timer = new Timer(true);

    public static void main(String[] args) {
        final JCipherFrame frame = JCipherFrame.getInstance();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        initApp();
    }

    private static void initApp() {
        try {
            JCipher.getInstance();
            resetTimer();
        } catch (Exception e) {
            JCipherFrame.getInstance().dispose();
        }
    }

    private static void resetTimer() {
        timer.purge();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                JCipherFrame.getInstance().dispose();
            }
        }, TimeUnit.MINUTES.toMillis(5L));
    }
}
