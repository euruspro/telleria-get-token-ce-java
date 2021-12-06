/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cl.edinet.carpetaselectronicas.test.utils;

/**
 *
 * @author Cristian
 */
public class ValidationException extends Exception {

    /**
     * Creates a new instance of
     * <code>ValidationException</code> without detail message.
     */
    public ValidationException() {
    }

    /**
     * Constructs an instance of
     * <code>ValidationException</code> with the specified detail message.
     *
     * @param msg the detail message.
     */
    public ValidationException(String msg) {
        super(msg);
    }
}
