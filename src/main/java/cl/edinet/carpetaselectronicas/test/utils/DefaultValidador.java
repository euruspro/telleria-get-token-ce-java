package cl.edinet.carpetaselectronicas.test.utils;

/*
 * DefaultValidador.java Created on 22 de septiembre de 2007, 09:35 AM
 */
/**
 * @author Nefercarum
 */
public class DefaultValidador {

    public static DefaultValidador validador = new DefaultValidador();

    public DefaultValidador() {
    }

    public boolean isValidParameter(String str, Integer caracteresMinimos, Integer caracteresMaximos, String type) {
        if (str == null) {
            return false;
        }
        if (str.equals("") || str.trim().length() == 0) {
            return false;
        }
        if (caracteresMinimos > 0) {
            if (str.length() < caracteresMinimos) {
                return false;
            }
        }
        if (caracteresMaximos > 0) {
            if (str.length() > caracteresMaximos) {
                return false;
            }
        }
        return true;
    }

    public boolean isValidParameter(String str) {
        return isValidParameter(str, 0, -1, "text");
    }
}
