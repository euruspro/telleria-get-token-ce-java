/*
 * InfoAjax.java
 * Creado el 16 de diciembre de 2006, 11:58 PM
 */
package cl.edinet.carpetaselectronicas.test.utils;

import java.io.PrintWriter;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Nefercarum
 */
public class ResultadoWS implements Serializable {

    private boolean exito = false;
    private String mensaje = null;
    private String shortDescripcion = null;
    private Throwable exception = null;
    private String delimitador = "&";

    public ResultadoWS(Throwable e) {
        this(false, e.getMessage());
        setException(exception);
    }

    public ResultadoWS(String msg) {
        this(true, msg);
    }

    public ResultadoWS(boolean exito, String mensaje) {
        this(exito, mensaje, "");
    }

    public ResultadoWS(boolean exito, String mensaje, String short_descripcion) {
        this.set(exito, mensaje, short_descripcion);
    }

    public ResultadoWS(boolean exito, String mensaje, String short_descripcion, Throwable e) {
        this.set(exito, mensaje, short_descripcion);
        this.setException(exception);
    }

    public static void responseMessage(PrintWriter out, ResultadoWS infoAjax) {
        out.println(infoAjax.toMonoStandard());
    }

    public static ResultadoWS createInfoAjax(String text, String delim) {

        ResultadoWS i = new ResultadoWS();

        String[] vec = text.split(delim);

        if (vec != null && vec.length == 1) {
            //Logger.getLogger(InfoAjax.class.getName()).log(Level.WARNING, "Ocurrio un problema al parsear un  texto InfoAjax desde un text: " + text + ", vec zero.");
            i.setExito(true);
            i.setMensaje(text);
            i.setShortDescripcion("");
            i.setDelimitador(delim);
            return i;
        }

        if ("ok".equals(vec[0])) {
            i.setExito(true);
        } else {
            i.setExito(false);
        }
        i.setMensaje(vec[1]);

        if (vec.length >= 2) {
            try {
                i.setShortDescripcion(vec[2]);
            } catch (Exception e) {
            }            
        }

        return i;

    }

    public ResultadoWS() {
        this(false, "", "");
    }

    public void setShortDescripcion(String str) {
        this.shortDescripcion = str;
    }

    public String getShortDescripcion() {
        return this.shortDescripcion;
    }

    public void set(boolean exito, String mensaje) {
        this.set(exito, mensaje, "");
    }

    public void set(boolean exito, String mensaje, String short_descripcion) {
        this.setExito(exito);
        this.setMensaje(mensaje);
        this.setShortDescripcion(short_descripcion);
    }

    public static void responseMessage(PrintWriter out, String msg) {
        out.println(responseMessage(msg));
    }

    public static void responseMessage(PrintWriter out, Throwable e) {
        checkException(e);
        out.println(responseMessage(e));
    }

    public static void responseMessage(PrintWriter out, Throwable e, String delim) {
        responseMessage(out, e, "", delim);
    }

    public static void responseMessage(PrintWriter out, Throwable e, String descripcion, String delim) {
        checkException(e);
        //out.println(responseMessage(e));
        ResultadoWS info = new ResultadoWS(false, e.getMessage(), descripcion);
        info.setDelimitador(delim);
        out.println(info.toMonoStandard());
    }

    private static void checkException(Throwable e) {
        if (e != null) {
            if (e.getClass().getName().equals("java.lang.Exception") || e.getClass().getName().equals("java.sql.SQLException")) {
                //System.out.println("La traza es: ");
                //e.printStackTrace();
            }
        }
    }

    public static String responseMessage(Throwable e) {
        checkException(e);
        return new ResultadoWS(e).toMonoStandard();
    }

    public static String responseMessage(String msg) {
        return new ResultadoWS(msg).toMonoStandard();
    }

    public static void responseMessage(PrintWriter out, String mensaje, String desc) {
        out.println(ResultadoWS.toMonoStandard(true, mensaje, desc));
    }

    public static void responseMessage(PrintWriter out, String mensaje, String desc, String delim) {
        ResultadoWS info = new ResultadoWS(true, mensaje, desc);
        info.setDelimitador(delim);
        out.println(info.toMonoStandard());
    }

    public static String responseMessage(String msg, String desc) {
        return new ResultadoWS(true, msg, desc).toMonoStandard();
    }

    public static String toMonoStandard(boolean exito, String mensaje) {
        return toMonoStandard(exito, mensaje, "");
    }

    public static String toMonoStandard(boolean exito, String mensaje, String short_descripcion) {
        return new ResultadoWS(exito, mensaje, short_descripcion).toMonoStandard();
    }

    public String toMonoStandard() {
        if (!isExito()) {
            return "error" + this.getDelimitador() + getMensaje() + this.getDelimitador() + getShortDescripcion();
        } else {
            return "ok" + this.getDelimitador() + getMensaje() + this.getDelimitador() + getShortDescripcion();
        }
    }

    public boolean isExito() {
        return exito;
    }

    public void setExito(boolean exito) {
        this.exito = exito;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public Throwable getException() {
        return exception;
    }

    public void setException(Throwable exception) {
        this.exception = exception;
    }

    public String getDescriptionException() {
        StringBuffer buf = new StringBuffer();
        if (this.getException() != null) {
            for (StackTraceElement s : getException().getStackTrace()) {
                buf.append(s.toString() + "<br>");
            }
        }
        return buf.toString();
    }

    public String getDelimitador() {
        return delimitador;
    }

    public void setDelimitador(String delimitador) {
        this.delimitador = delimitador;
    }
}
