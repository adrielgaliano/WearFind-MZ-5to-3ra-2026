package com.example.WearFind.Service;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

// Guarda las contraseñas con hash (PBKDF2 + sal), nunca en texto plano
public class ClaveUtil {

    private static final int ITERACIONES = 120000;
    private static final int LONGITUD_BITS = 256;

    public static String encriptar(String clave) {
        byte[] sal = new byte[16];
        new SecureRandom().nextBytes(sal);
        byte[] hash = derivar(clave, sal);
        return Base64.getEncoder().encodeToString(sal) + ":" + Base64.getEncoder().encodeToString(hash);
    }

    public static boolean verificar(String clave, String guardada) {
        if (clave == null || guardada == null || !guardada.contains(":")) {
            return false;
        }
        String[] partes = guardada.split(":");
        byte[] sal = Base64.getDecoder().decode(partes[0]);
        byte[] esperado = Base64.getDecoder().decode(partes[1]);
        return MessageDigest.isEqual(esperado, derivar(clave, sal));
    }

    private static byte[] derivar(String clave, byte[] sal) {
        try {
            PBEKeySpec especificacion = new PBEKeySpec(clave.toCharArray(), sal, ITERACIONES, LONGITUD_BITS);
            return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(especificacion).getEncoded();
        } catch (Exception e) {
            throw new IllegalStateException("No se pudo procesar la contraseña", e);
        }
    }
}
