package be.rla.jcipher.core;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.ShortBufferException;
import javax.crypto.spec.SecretKeySpec;

public class JCipher {

    private static JCipher instance = null;

    private static final String ALGO = "AES";

    private final static byte[] SIGN = new byte[]{1, 10, 19, 7, 7, 29, 4, 19, 8, 0, 28, 1, 20, 11, 6, 6, 20, 12};

    private Cipher cipher = null;
    private Key key = null;

    private JCipher() throws NoSuchAlgorithmException, NoSuchPaddingException {
        this.cipher = Cipher.getInstance(ALGO);
    }

    public static synchronized JCipher getInstance() throws NoSuchAlgorithmException, NoSuchPaddingException {
        if (instance == null) {
            instance = new JCipher();
        }
        return instance;
    }

    public ByteBuffer crypt(ByteBuffer buffer) throws InvalidKeyException, ShortBufferException, IllegalBlockSizeException, BadPaddingException {
        this.cipher.init(Cipher.ENCRYPT_MODE, this.key);
        ByteBuffer out = ByteBuffer.allocate(this.cipher.getOutputSize(buffer.limit()) + SIGN.length);
        analyze("out", out);
        out.position(SIGN.length);
        analyze("pos. SIGN", out);
        this.cipher.doFinal(buffer, out);
        analyze("do final", out);
        out.rewind();
        analyze("rewind", out);
        out.put(SIGN);
        analyze("put SIGN", out);
        ByteBuffer encoded = Base64.getEncoder().encode((ByteBuffer) out.rewind());
        analyze("encoded", encoded);
        return encoded;
    }

    public ByteBuffer decrypt(ByteBuffer fromB64) throws InvalidKeyException, ShortBufferException, IllegalBlockSizeException, BadPaddingException {
        this.cipher.init(Cipher.DECRYPT_MODE, this.key);
        analyze("to decrypt", fromB64);
        fromB64.position(SIGN.length);
        analyze("pass sign", fromB64);
        int outputSize = this.cipher.getOutputSize(fromB64.limit());
        ByteBuffer out = ByteBuffer.allocate(outputSize);
        analyze("out", out);
        this.cipher.doFinal(fromB64, out);
        analyze("do final out", out);
        return out;
    }

    public static void main(String[] args) {
        ByteBuffer bb = ByteBuffer.allocate(10);
        analyze(bb);
        bb.put("A".getBytes());
        analyze(bb);
        bb.put("B".getBytes());
        analyze(bb);
        bb.put("B".getBytes());
        bb.put("B".getBytes());
        bb.put("B".getBytes());
        bb.put("B".getBytes());
        bb.put("B".getBytes());
        bb.put("B".getBytes());
        bb.position(5);
        analyze(bb);
        bb.put("C".getBytes());
        analyze(bb);
        // bb.clear();
        // analyze(bb);
        System.out.println("MARK");
        // bb.mark();
        // bb.reset();
        analyze(bb);
        bb.rewind();
        analyze(bb);
        bb.limit(4);
        analyze(bb);
    }

    public static void analyze(ByteBuffer buffer) {
        analyze("", buffer);
    }

    public static void analyze(String comment, ByteBuffer buffer) {
        if (buffer.array().length < 100) {
            System.out.println(String.format(comment + "%n%3d@%3d/%3d : \"%s\" - remains %d", buffer.position(), buffer.limit(), buffer.capacity(),
                    Arrays.deepToString(Arrays.asList(buffer.array()).toArray()), buffer.remaining()));
        } else {
            System.out.println(String.format(comment + "%n%3d@%3d/%3d : \"%s\" - remains %d", buffer.position(), buffer.limit(), buffer.capacity(),
                    "to long", buffer.remaining()));
        }
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

    public boolean isCryptContent(ByteBuffer fromB64) {
        boolean isCryptContent = false;
        if (fromB64 != null && fromB64.limit() > SIGN.length) {
            byte[] toTest = new byte[SIGN.length];
            fromB64.get(toTest);
            isCryptContent = Arrays.equals(SIGN, toTest);
        }
        return isCryptContent;
    }
}
