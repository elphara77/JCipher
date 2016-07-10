import static org.junit.Assert.fail;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.util.Base64;

import org.junit.After;
import org.junit.Test;

import be.rla.jcipher.core.JCipher;

public class CipherTestCase {

    @After
    public void after() {
        System.out.println();
        System.out.println();
    }

    @Test
    public void testRaw() {
        String toCryptString = "Raphaël est un chef ;)";
        System.out.println("to crypt: \"" + toCryptString + "\"");
        ByteBuffer toCrypt = ByteBuffer.wrap(toCryptString.getBytes());
        try {
            JCipher.getInstance().loadKey();
            ByteBuffer crypted = JCipher.getInstance().crypt(toCrypt);
            System.out.println(crypted.toString());
            System.out.println("crypted: \"" + new String(crypted.array()) + "\"");
            ByteBuffer decrypted = JCipher.getInstance().decrypt(Base64.getDecoder().decode(crypted));
            System.out.println(decrypted.toString());
            System.out.println("decrypted: \"" + new String(decrypted.array()) + "\"");
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void testFile() {
        try (InputStream in = new BufferedInputStream(CipherTestCase.class.getClassLoader().getResourceAsStream("test.jpeg"));) {
            ByteBuffer read = ByteBuffer.allocate(in.available());
            Channels.newChannel(in).read(read);

            JCipher.getInstance().loadKey();
            ByteBuffer crypted = JCipher.getInstance().crypt(read);
            System.out.println("crypted: " + crypted);
            ByteBuffer decrypted = JCipher.getInstance().decrypt(Base64.getDecoder().decode(crypted));
            System.out.println(decrypted);
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }

    }

}
