package be.rla.jcipher.gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JProgressBar;

import be.rla.jcipher.core.JCipherListener;
import be.rla.jcipher.main.JCipherApp;
import net.iharder.dnd.FileDrop;

public class JCipherFrame extends JFrame {

    private static final long serialVersionUID = 1L;

    private static JCipherFrame instance = new JCipherFrame();

    public static JCipherFrame getInstance() {
        if (instance == null) {
            instance = new JCipherFrame();
        }
        return instance;
    }

    private JLabel label;

    private JProgressBar progressBar;

    private JCipherFrame() {
        super(JCipherApp.VERSION);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(0, 0, 1115, 340);// TODO large as screen wide ?
        GridBagLayout gridBagLayout = new GridBagLayout();
        gridBagLayout.columnWidths = new int[]{239, 0};
        gridBagLayout.rowHeights = new int[]{16, 0, 0};
        gridBagLayout.columnWeights = new double[]{1.0, Double.MIN_VALUE};
        gridBagLayout.rowWeights = new double[]{1.0, 1.0, Double.MIN_VALUE};
        getContentPane().setLayout(gridBagLayout);

        label = new JLabel("Drop a file here to crypt or decrypt it !");
        GridBagConstraints gbc_lblDropAFile = new GridBagConstraints();
        gbc_lblDropAFile.anchor = GridBagConstraints.SOUTH;
        gbc_lblDropAFile.insets = new Insets(0, 0, 5, 0);
        gbc_lblDropAFile.gridx = 0;
        gbc_lblDropAFile.gridy = 0;
        getContentPane().add(label, gbc_lblDropAFile);

        progressBar = new JProgressBar();
        progressBar.setVisible(false);
        GridBagConstraints gbc_progressBar = new GridBagConstraints();
        gbc_progressBar.anchor = GridBagConstraints.NORTH;
        gbc_progressBar.fill = GridBagConstraints.HORIZONTAL;
        gbc_progressBar.gridx = 0;
        gbc_progressBar.gridy = 1;
        getContentPane().add(progressBar, gbc_progressBar);

        fileDrop();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void fileDrop() {
        new FileDrop(this, JCipherListener.getInstance());
    }

    public JLabel getLabel() {
        return label;
    }

    public JProgressBar getProgressBar() {
        return progressBar;
    }

}
