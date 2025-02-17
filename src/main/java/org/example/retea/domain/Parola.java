package org.example.retea.domain;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.spec.KeySpec;
import java.util.Base64;
import java.util.Objects;

public class Parola extends Entity<Long> {

    String parola;
    String numeUtil;

    public Parola(String parola, String numeUtil) {
        this.parola = parola;
        this.numeUtil = numeUtil;
    }

    public String getParola() {
        return parola;
    }
    public void setParola(String parola) {
        this.parola = parola;
    }
    public String getNumeUtil() {
        return numeUtil;
    }
    public void setNumeUtil(String numeUtil) {
        this.numeUtil = numeUtil;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Parola parola1 = (Parola) o;
        return Objects.equals(parola, parola1.parola);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), parola);
    }

    public String criptare() {
        try {
            // generare cheie secreta
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            KeySpec specKey = new PBEKeySpec(parola.toCharArray(), "Salt".getBytes(), 65536, 256);
            SecretKey tmpry = factory.generateSecret(specKey);
            SecretKey secretKey = new SecretKeySpec(tmpry.getEncoded(), "AES");

            // criptare
            Cipher ciph = Cipher.getInstance("AES");
            ciph.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] encrypted = ciph.doFinal(parola.getBytes());

            // convertire in reprezentare base64
            return Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
