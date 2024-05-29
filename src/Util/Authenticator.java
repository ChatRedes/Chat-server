package Util;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
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
            byte[] authRequestBytes = Base64.getDecoder().decode(simetricKey);
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, privateKey);

            byte[] aesKeyBytes = cipher.doFinal(authRequestBytes);
            String sKeyMessage = new String(aesKeyBytes);
            String[] parsedKey = sKeyMessage.split(" ");

            System.out.println(sKeyMessage);
            if (parsedKey.length != 2) {
                System.out.println("Tamanho incorreto: " + simetricKey);
                return false;
            }

            if (parsedKey[0] != "CHAVE_SIMETRICA") {
                System.err.println("Request incorreto");
                return false;
            }

            byte[] decodedKey = Base64.getDecoder().decode(parsedKey[1]);
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
}
