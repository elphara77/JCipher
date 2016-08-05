package be.rla.jcipher.utils;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.io.Reader;

public class ClipboardHelper {

    private static Clipboard clipboard = null;

    static {
        clipboard = Toolkit.getDefaultToolkit().getSystemSelection();
        if (clipboard == null) {
            // on Windows
            clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        }
    }

    public static void setToClipboard(String str) {
        StringSelection selection = new StringSelection(str);
        clipboard.setContents(selection, selection);
    }

    public static void main(String[] args) {
        System.out.println(getFromClipboard());
    }

    public static String getFromClipboard() {
        String ret = "";

        final Transferable data = clipboard.getContents(null);
        try {
            if (data != null && data.isDataFlavorSupported(DataFlavor.getTextPlainUnicodeFlavor())) {
                final DataFlavor df = DataFlavor.getTextPlainUnicodeFlavor();

                try (Reader r = df.getReaderForText(data)) {
                    StringBuffer buf = new StringBuffer();
                    int c;
                    while ((c = r.read()) != -1) {
                        buf.append((char) c);
                    }
                    ret = buf.toString();
                }
            }
        } catch (Exception ex) {
            ret = data.toString();
        }
        return ret;
    }
}
