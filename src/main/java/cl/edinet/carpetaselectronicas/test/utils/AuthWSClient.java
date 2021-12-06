/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cl.edinet.carpetaselectronicas.test.utils;

/**
 *
 * @author Cristian
 */
public class AuthWSClient {

    private String nik = null;
    private String password = null;

    public AuthWSClient() {
    }

    public AuthWSClient(String nik, String password) {
        this.nik = nik;
        this.password = password;
    }

    public String getNik() {
        return nik;
    }

    public void setNik(String nik) {
        this.nik = nik;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
