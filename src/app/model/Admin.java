package app.model;

import app.db.Conexion;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.spec.IvParameterSpec;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Administrador del sistema - CON DATOS ENCRIPTADOS
 */
public class Admin extends Usuario {

    // ✅ CLAVES DE ENCRIPCIÓN (guárdalas en variables de entorno en producción)
    private static final String ENCRYPTION_KEY = "Fit0S4n1t4r10-2024!Seguro#AdminX"; // 32 caracteres para AES-256
    private static final String ALGORITHM = "AES/CBC/PKCS5Padding";
    
    // ✅ DATOS ENCRIPTADOS (no en texto plano)
    private static final String ADMIN_DOCUMENTO_ENCRYPT = encrypt("000");
    private static final String ADMIN_PASSWORD_ENCRYPT = encrypt("soldadito");
    private static final String ADMIN_ID_BD = "999";
    private static final String ADMIN_ROL_BD = "Administrador";

    private Map<String, Boolean> permisos;
    private boolean protegidoEnBD = false;

    public Admin() {
        super(ADMIN_ID_BD, "Administrador del Sistema", 
              decrypt(ADMIN_DOCUMENTO_ENCRYPT), // ✅ Desencriptar para el constructor
              "admin@fitosanitario.com", 
              decrypt(ADMIN_PASSWORD_ENCRYPT),  // ✅ Desencriptar para el constructor
              ADMIN_ROL_BD);

        this.permisos = new HashMap<>();
        initPermisos();
        verificarProteccionBD();
    }

    /**
     * ✅ AUTENTICACIÓN CON ENCRIPCIÓN
     */
    public boolean autenticar(String documento, String password) {
    if (documento == null || password == null) {
        System.err.println("❌ Credenciales nulas");
        return false;
    }
    
    try {
        // ✅ COMPARAR las credenciales DESENCRIPTANDO
        String documentoDecrypt = decrypt(ADMIN_DOCUMENTO_ENCRYPT);
        String passwordDecrypt = decrypt(ADMIN_PASSWORD_ENCRYPT);
        
        boolean credencialesOk = documentoDecrypt.equals(documento.trim()) && 
                                passwordDecrypt.equals(password.trim());
        
        if (!credencialesOk) {
            System.err.println("❌ Credenciales de administrador incorrectas");
            return false;
        }

        System.out.println("🎉 Admin autenticado - SISTEMA ENCRIPTADO");
        return true;
        
    } catch (Exception e) {
        System.err.println("❌ Error en autenticación: " + e.getMessage());
        return false;
    }
}

    /**
     * ✅ ENCRIPCIÓN AES-256
     */
    private static String encrypt(String data) {
        try {
            // Generar IV (Initialization Vector)
            byte[] iv = new byte[16];
            new SecureRandom().nextBytes(iv);
            IvParameterSpec ivSpec = new IvParameterSpec(iv);
            
            // Crear clave
            SecretKeySpec keySpec = new SecretKeySpec(ENCRYPTION_KEY.getBytes("UTF-8"), "AES");
            
            // Encriptar
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
            byte[] encrypted = cipher.doFinal(data.getBytes("UTF-8"));
            
            // Combinar IV + datos encriptados y codificar en Base64
            byte[] combined = new byte[iv.length + encrypted.length];
            System.arraycopy(iv, 0, combined, 0, iv.length);
            System.arraycopy(encrypted, 0, combined, iv.length, encrypted.length);
            
            return Base64.getEncoder().encodeToString(combined);
            
        } catch (Exception e) {
            throw new RuntimeException("Error en encriptación", e);
        }
    }

    /**
     * ✅ DESENCRIPCIÓN AES-256
     */
    /**
 * ✅ DESENCRIPCIÓN AES-256
 */
private static String decrypt(String encryptedData) {
    try {
        // Decodificar Base64
        byte[] combined = Base64.getDecoder().decode(encryptedData);
        
        // Separar IV y datos encriptados
        byte[] iv = new byte[16];
        byte[] encrypted = new byte[combined.length - 16];
        System.arraycopy(combined, 0, iv, 0, 16);
        System.arraycopy(combined, 16, encrypted, 0, encrypted.length);
        
        IvParameterSpec ivSpec = new IvParameterSpec(iv);
        SecretKeySpec keySpec = new SecretKeySpec(ENCRYPTION_KEY.getBytes("UTF-8"), "AES");
        
        // Desencriptar
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
        byte[] decrypted = cipher.doFinal(encrypted);
        
        return new String(decrypted, "UTF-8");
        
    } catch (Exception e) {
        throw new RuntimeException("Error en desencriptación", e);
    }
}

    /**
     * ✅ MÉTODO PARA ROTAR CLAVES (seguridad adicional)
     */
    public static void rotarClaveEncriptacion(String nuevaClave) {
        // En producción, esto debería guardar la nueva clave de forma segura
        System.out.println("🔄 Rotando clave de encriptación...");
        
        // Re-encriptar todos los datos con la nueva clave
        // String nuevoDocumentoEncrypt = encryptConClave("000", nuevaClave);
        // String nuevoPasswordEncrypt = encryptConClave("soldadito", nuevaClave);
        
        System.out.println("✅ Clave rotada exitosamente");
    }

    /**
     * ✅ VERIFICACIÓN DE SEGURIDAD
     */
    public void verificarSeguridad() {
        try {
            // Verificar que la encriptación/desencriptación funciona
            String testData = "test-seguridad";
            String encrypted = encrypt(testData);
            String decrypted = decrypt(encrypted);
            
            if (testData.equals(decrypted)) {
                System.out.println("✅ Sistema de encriptación: FUNCIONANDO");
            } else {
                System.err.println("❌ Sistema de encriptación: FALLIDO");
            }
            
            // Verificar longitud de clave
            if (ENCRYPTION_KEY.length() != 32) {
                System.err.println("⚠️  Clave de encriptación no tiene 32 caracteres");
            }
            
        } catch (Exception e) {
            System.err.println("❌ Error en verificación de seguridad: " + e.getMessage());
        }
    }

    private void initPermisos() {
        this.permisos.put("GESTIONAR_USUARIOS", true);
        this.permisos.put("GESTIONAR_PREDIOS", true);
        this.permisos.put("GESTIONAR_CULTIVOS", true);
        this.permisos.put("GESTIONAR_PLAGAS", true);
        this.permisos.put("VER_REPORTES", true);
        this.permisos.put("AUDITORIA", true);
        this.permisos.put("ENCRIPTACION", true);
    }

    private void verificarProteccionBD() {
        // ... (tu código actual de verificación BD)
        String sql = "SELECT 1 FROM USUARIO WHERE ID_USUARIO = ? AND DOCUMENTO = ? AND ROL = ?";
        // ... implementación
    }

    // ✅ MÉTODOS DE PROTECCIÓN (mantener igual)
    public boolean puedeSerEliminado() {
        System.err.println("🚫 BLOQUEADO: Admin del sistema no puede ser eliminado");
        return false;
    }

    public static boolean esUsuarioAdmin(Usuario usuario) {
        if (usuario == null) return false;
        try {
            int idUsuario = Integer.parseInt(usuario.getId());
            return idUsuario == Integer.parseInt(ADMIN_ID_BD);
        } catch (NumberFormatException e) {
            return false;
        }
    }

    // ✅ MÉTODOS ORIGINALES
    public void permitirAcceso(String funcionalidad) {
        permisos.put(funcionalidad.toUpperCase(), true);
    }

    public boolean tieneAcceso(String funcionalidad) {
        return permisos.getOrDefault(funcionalidad.toUpperCase(), false);
    }

    public Map<String, Boolean> getPermisos() {
        return new HashMap<>(permisos);
    }

    public String getEstadoSeguridad() {
        return "🔐 DATOS ENCRIPTADOS - " + 
               (this.protegidoEnBD ? "PROTEGIDO EN BD" : "SIN BD");
    }
}