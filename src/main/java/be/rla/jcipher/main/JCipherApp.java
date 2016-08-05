package be.rla.jcipher.main;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import be.rla.jcipher.core.JCipher;
import be.rla.jcipher.core.JCipherListener;
import be.rla.jcipher.gui.JCipherFrame;

public class JCipherApp {

    public static final String VERSION = "JCipher v1.4 (c) R.Laporte";

    private static Timer timer = new Timer(true);
    private static TimerTask timerCloseTask;

    public static void main(String[] args) {
        if (args == null || args.length == 0) {
            JCipherFrame.getInstance();
            initApp();
        } else {
            System.out.println("ARGS TO TREAT MAY BE ;)");
            int count = -1;
            for (String arg : args) {
                System.out.println("arg #" + ++count + " : \"" + arg + "\"");
            }
        }
    }

    private static void initApp() {
        try {
            JCipher.getInstance().loadKey();
            initFrameAwake();
            resetTimer();
        } catch (Exception e) {
            JCipherFrame.getInstance().dispose();
        }
    }

    private static void initFrameAwake() {
        JCipherFrame.getInstance().addMouseListener(new MouseListener() {
            @Override
            public void mouseReleased(MouseEvent e) {
                resetTimer();
            }

            @Override
            public void mousePressed(MouseEvent e) {
                resetTimer();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                resetTimer();
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                resetTimer();
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                resetTimer();
            }
        });
    }

    private static void resetTimer() {
        if (timerCloseTask != null) {
            timerCloseTask.cancel();
            timer.purge();
        }
        timerCloseTask = new TimerTask() {
            @Override
            public void run() {
                if (JCipherListener.getInstance().isBusy()) {
                    resetTimer();
                } else {
                    JCipherFrame.getInstance().dispose();
                }
            }
        };
        timer.schedule(timerCloseTask, TimeUnit.MINUTES.toMillis(3L));
    }
}
