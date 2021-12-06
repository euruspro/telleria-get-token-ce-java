/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cl.edinet.carpetaselectronicas.test.utils;

/**
 *
 * @author Cristian
 */
public class WSClientException extends Exception {

    private String xmlError = null;
    private int type = 0;
    private static int ERROR_ANY = 0;
    public static int ERROR_CONNECTION_REFUSED = 1;
    public static int ERROR_UNSUPPORTED_RESPONSE = 2;
    public static int ERROR_REQUIRES_HTTP_AUTHENTICATION = 3;
    public static int ERROR_AUTHENTICATION_FAIL = 4;
    public static int ERROR_SSL_HANDSHAKE = 5;

    /**
     * Creates a new instance of
     * <code>WSClientException</code> without detail message.
     */
    public WSClientException() {
    }

    public WSClientException(String msg) {
        this(msg, ERROR_ANY);
    }

    public WSClientException(String msg, int type) {
        super(msg);
        this.type = type;
    }

    public WSClientException(Throwable throwable) {
        super(throwable);
        this.type = ERROR_ANY;
    }

    public WSClientException(String msg, String xmlError) {
        super(msg);
        setXmlError(xmlError);
        this.type = ERROR_ANY;
    }

    public String getXmlError() {
        return xmlError;
    }

    public void setXmlError(String xmlError) {
        this.xmlError = xmlError;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
