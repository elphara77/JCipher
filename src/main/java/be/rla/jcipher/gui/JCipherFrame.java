package be.rla.jcipher.gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JFrame;
import javax.swing.JLabel;

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

    private JCipherFrame() {
        super(JCipherApp.VERSION);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(0, 0, 1115, 340);
        GridBagLayout gridBagLayout = new GridBagLayout();
        gridBagLayout.columnWidths = new int[]{1115, 0};
        gridBagLayout.rowHeights = new int[]{318, 0};
        gridBagLayout.columnWeights = new double[]{1.0, Double.MIN_VALUE};
        gridBagLayout.rowWeights = new double[]{1.0, Double.MIN_VALUE};
        getContentPane().setLayout(gridBagLayout);

        JLabel lblDropAFile = new JLabel("Drop a file here to crypt or decrypt it !");
        GridBagConstraints gbc_lblDropAFile = new GridBagConstraints();
        gbc_lblDropAFile.gridx = 0;
        gbc_lblDropAFile.gridy = 0;
        getContentPane().add(lblDropAFile, gbc_lblDropAFile);

        fileDrop();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void fileDrop() {
        new FileDrop(this, new JCipherListener());
    }

}
