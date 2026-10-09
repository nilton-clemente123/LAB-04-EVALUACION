package com.tecsup.demo_01.config;

/**
 * Guarda el usuario de la petición actual en un ThreadLocal para que la
 * auditoría pueda registrar "quién" realizó cada operación.
 */
public final class UsuarioActualHolder {

    private static final ThreadLocal<String> USUARIO = new ThreadLocal<>();

    private UsuarioActualHolder() {
    }

    public static void set(String usuario) {
        USUARIO.set(usuario);
    }

    public static String get() {
        return USUARIO.get();
    }

    public static void clear() {
        USUARIO.remove();
    }
}