package Util;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.sound.midi.SysexMessage;
import java.io.IOException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;

public class Authenticator {
    private PrivateKey privateKey;
    private PublicKey publicKey;
    private SecretKey aesKey;

    public Authenticator (PrivateKey privateKey, PublicKey publicKey) {
        this.privateKey = privateKey;
        this.publicKey = publicKey;
    }

    public String SendPublicKey() throws IOException {
        byte[] messageCripto = this.publicKey.getEncoded();
        String messageToClient = "CHAVE_PUBLICA " + Base64.getEncoder().encodeToString(messageCripto);
        return messageToClient;
    }

    public boolean DecryptSimetricKey(String simetricKey)  {
        try {
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, privateKey);

            byte[] authRequestBytes = Base64.getDecoder().decode(simetricKey);
            byte[] aesKeyBytes = cipher.doFinal(authRequestBytes);

            String sKeyMessage = new String(aesKeyBytes);
            String[] parsedKey = sKeyMessage.split(" ");

            if (parsedKey.length != 2) {
                return false;
            }

            if (!parsedKey[0].equals("CHAVE_SIMETRICA")) {
                return false;
            }

            byte[] key = parsedKey[1].getBytes();
            byte[] decodedKey = Base64.getDecoder().decode(key);
            this.aesKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
            return true;
        }catch(Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public String DecryptMessage(String message) throws Exception {
        try {
            byte[] messageBytes = Base64.getDecoder().decode(message);
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, aesKey);
            byte[] decryptedMessageBytes = cipher.doFinal(messageBytes);
            return new String(decryptedMessageBytes);
        } catch (Exception e) {
            e.printStackTrace();
            return "ERRO: Falha ao desencripitar a mensagem";
        }
    }

    public String EncryptMessage(String message) throws Exception {
        Cipher cif = Cipher.getInstance("AES");
        cif.init(Cipher.ENCRYPT_MODE, this.aesKey);

        byte[] buffer = cif.doFinal(message.getBytes());
        String messageToSend = Base64.getEncoder().encodeToString(buffer);
        return messageToSend;
    }
}
