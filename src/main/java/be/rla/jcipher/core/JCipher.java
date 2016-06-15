package be.rla.jcipher.core;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.InvalidKeyException;
import java.security.Key;
import java.util.Arrays;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;

public class JCipher {

    private static JCipher instance = null;

    private static final String ALGO = "AES";

    private final static byte[] SIGN = new byte[]{1, 10, 19, 7, 7, 29, 4, 19, 8, 0, 28, 1, 20, 11, 6, 6, 20, 12};

    private Cipher cipher = null;
    private Key key = null;

    private JCipher() throws Exception {
        this.cipher = Cipher.getInstance(ALGO);
    }

    public static synchronized JCipher getInstance() throws Exception {
        if (instance == null) {
            instance = new JCipher();
        }
        return instance;
    }

    public byte[] crypt(byte[] phrase) throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        this.cipher.init(Cipher.ENCRYPT_MODE, this.key);
        // TODO use buffer for large file ???
        byte[] crypted = this.cipher.doFinal(phrase);
        byte[] content = new byte[SIGN.length + crypted.length];
        System.arraycopy(SIGN, 0, content, 0, SIGN.length);
        System.arraycopy(crypted, 0, content, SIGN.length, crypted.length);
        return Base64.getEncoder().encode(content);
    }

    public byte[] decrypt(byte[] raw) throws IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
        this.cipher.init(Cipher.DECRYPT_MODE, this.key);
        // TODO use buffer for large file !!! ???
        return this.cipher.doFinal(Arrays.copyOfRange(raw, SIGN.length, raw.length));
    }

    public void loadKey() throws Exception {
        File keyFile = new File(System.getProperty("user.home"), ".jckey");
        if (!keyFile.exists()) {
            Key secretKey = KeyGenerator.getInstance(ALGO).generateKey();
            try (BufferedOutputStream os = new BufferedOutputStream(new FileOutputStream(keyFile))) {
                os.write(Base64.getEncoder().encode(secretKey.getEncoded()));
            }
        }
        if (keyFile.isFile()) {
            try (BufferedInputStream is = new BufferedInputStream(new FileInputStream(keyFile))) {
                byte[] buffer = new byte[is.available()];
                is.read(buffer);
                this.key = new SecretKeySpec(Base64.getDecoder().decode(buffer), ALGO);
            }
        } else {
            throw new Exception("No de/crypt key !");
        }
    }

    public boolean isCryptContent(byte[] fromB64) {
        boolean isCryptContent = false;
        if (fromB64 != null && fromB64.length > SIGN.length) {
            isCryptContent = Arrays.equals(SIGN, Arrays.copyOfRange(fromB64, 0, SIGN.length));
        }
        return isCryptContent;
    }
}
