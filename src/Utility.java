import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class Utility
{
    private static SecretKeySpec secretKey = new SecretKeySpec("5u8x/A?D(G+KbPeS".getBytes(), "AES");
    Cipher cipher = null;

    public Utility () {

    }

    public String GetResponse(Boolean encrypted, DataInputStream receive) {
        String response = null;
        try {
            if (encrypted) {
                response = decrypt(receive.readUTF());
            } else {
                response = receive.readUTF();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        System.out.println(response);
        return response;

    }

    public String decrypt(String s) {
        String plaintextstring = null;

        try
        {
            cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            plaintextstring = new String(cipher.doFinal(Base64.getDecoder().decode(s)));
        } catch (NoSuchPaddingException | NoSuchAlgorithmException | IllegalBlockSizeException
                | BadPaddingException | InvalidKeyException ex) {
            ex.printStackTrace();
        }

        return plaintextstring;
    }

    public String EncryptAndEncodeToBase64String(String s) {
        String secretstring = null;
        try
        {
            cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            secretstring = Base64.getEncoder().encodeToString(cipher.doFinal(s.getBytes()));
        } catch (NoSuchPaddingException | NoSuchAlgorithmException | IllegalBlockSizeException
                | BadPaddingException | InvalidKeyException ex) {
            ex.printStackTrace();
        }
        return secretstring;
    }

    public void EncryptAndSend(String msg, DataOutputStream send) {
        try
        {
            send.writeUTF(EncryptAndEncodeToBase64String(msg));
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

    }

    public String ReadFile(String filename) throws IOException
    {
        return new String(Files.readAllBytes(Paths.get(filename)));
    }

    public void Send(String msg, DataOutputStream send) {
        try
        {
            send.writeUTF(msg);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

    }

}
