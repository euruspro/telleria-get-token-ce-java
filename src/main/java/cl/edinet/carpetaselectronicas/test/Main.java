/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cl.edinet.carpetaselectronicas.test;


import cl.edinet.carpetaselectronicas.test.utils.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.net.URL;

/**
 *
 * @author Cristian
 */
public class Main {

    private static final String URL_WS = "https://carpetaelectronica.editrade.cl/CarpetaElectronica-Services/IntegracionPublic";//url hacia el WS
    private String authUsuario = "";
    private String authPassword = "";
    private String rutAgencia;
    private String rutaPKCS12;
    private String pinPKCS12;
    private String despacho;
    private String tokenAutenticacion;
    private ClienteWS client = null;

    public Main(String rutAgencia, String rutaPKCS12, String pinPKCS12, String despacho, String usuarioAuth, String passwordAuth) {
        this.rutAgencia = rutAgencia;
        this.rutaPKCS12 = rutaPKCS12;
        this.pinPKCS12 = pinPKCS12;
        this.despacho = despacho;
        this.authUsuario = usuarioAuth;
        this.authPassword = passwordAuth;

    }

    public static void main(String[] args) {

        String rutAgencia = "88883000-6";
        String rutaPKCS12 = "tmp/firma_telleria.pfx";
        String pinPKCS12 = "Mdkdhsd8763w3";
        String despacho = "952829";

        String usuarioAutenticacionWS = "ws.telleria";
        String passwordAutenticacionWS = "sMviou8763wdf$dJjh282da";

        /*
          El siguiente programa realiza las siguientes operaciones 
            1) Obtiene un Token de autenticacion 
            2) Obtiene la llave de una carpeta 
            3) Obtiene la URL de una carpeta
         todos los resultados son vistos en la consola de Java
         */
        try {
            Main main = new Main(rutAgencia, rutaPKCS12, pinPKCS12, despacho, usuarioAutenticacionWS, passwordAutenticacionWS);
            main.procesar();
        } catch (Exception e) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void procesar() throws Exception {

        negociarToken();

        // String llaveCarpeta = getLlaveCarpeta();
        // System.out.println("La llave del despacho: " + despacho + " es: " + llaveCarpeta);

        // String urlCarpeta = getUrlCarpeta(llaveCarpeta);
        //System.out.println("la URL del despacho es: " + urlCarpeta);

        // String metadataCarpeta = getMetadataCarpeta(llaveCarpeta);
        // System.out.println("Metadata Carpeta despacho es: " + metadataCarpeta);
    }

    public void negociarToken() throws Exception {

        System.out.println("Negociando Token - V1.1");
        WSParameterUtils parametros = new WSParameterUtils();

        parametros.addParameter("rutAgencia", rutAgencia);

        ResultadoWS resultadoSeed = getWSClient().invoke("getSeed", parametros);

        String xmlSeed = null;

        if (resultadoSeed.isExito()) {
            xmlSeed = resultadoSeed.getMensaje();
        } else {
            throw new ValidationException("Error al intentar obtener semilla desde el servidor: " + resultadoSeed.getMensaje());
        }

        if (!DefaultValidador.validador.isValidParameter(xmlSeed)) {
            throw new ValidationException("Fallo al obtener semilla de autenticacion del servidor. Errr001");
        }
        if (xmlSeed.startsWith("<error>")) {
            throw new ValidationException("Fallo al obtener semilla de autenticacion del servidor. Errr002");
        }

        System.out.println("XML SEED: " + xmlSeed);
        String signedSeed = FirmaXML.sign(new File(rutaPKCS12), pinPKCS12, xmlSeed);

        //System.out.println("XML SEED SIGN: " + signedSeed);
        parametros.addParameter("xmlSolicitudToken", signedSeed);
        parametros.addParameter("rutAgencia", rutAgencia);

        ResultadoWS resultadoToken = getWSClient().invoke("getToken", parametros);

        if (resultadoToken.isExito()) {
            tokenAutenticacion = resultadoToken.getMensaje();

            if (tokenAutenticacion.startsWith("<error>")) {
                throw new ValidationException("Autenticacion rechazada por el servidor: Err1");
            }

        } else {
            throw new ValidationException("Error al obtener token de autenticacion: " + resultadoToken.getMensaje());
        }

        System.out.println("El token de autenticacion es: " + tokenAutenticacion);

        // URL url = new URL("https://us-central1-e-telleria.cloudfunctions.net/telleria_ce");
        URL url = new URL("https://tell-carpelect-http-settoken-os6ej4pxcq-uc.a.run.app");
        Map<String,String> params = new LinkedHashMap<>();
        params.put("key", tokenAutenticacion);

        StringBuilder postData = new StringBuilder();
        for (Map.Entry<String, String> p: params.entrySet()) {
            if (postData.length() != 0)  postData.append('&');

            postData.append(URLEncoder.encode(p.getKey(), "UTF-8"));
            postData.append('=');
            postData.append(URLEncoder.encode(p.getValue(), "UTF-8"));
        }

        byte[] postDataBytes = postData.toString().getBytes(StandardCharsets.UTF_8);

        HttpURLConnection conn = (HttpURLConnection)url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        conn.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
        conn.setDoOutput(true);
        conn.getOutputStream().write(postDataBytes);

        Reader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));

        char[] array = new char[100];
        in.read(array);
        System.out.println(array);
        in.close();
    }

    public ClienteWS getWSClient() throws MalformedURLException {
        if (client == null) {
            client = new ClienteWS(URL_WS, "http://ws.services.sadcom.edinet.cl/", new AuthWSClient(authUsuario, authPassword));
        }
        return client;
    }

    public String getUrlCarpeta(String llaveCarpeta) throws Exception {
        String xml = createXMLComando("GET_URL",
                new KeyValue("despacho", despacho),
                new KeyValue("llave", llaveCarpeta));

        System.out.println("XML:::");
        System.out.println(xml);
        ResultadoWS resultado = enviarOperacionWS(xml);

        if (!resultado.isExito()) {
            throw new ValidationException("Error al obtener la url de la carpeta: " + resultado.getMensaje());
        }
        String url = resultado.getMensaje();
        return url;
    }

    public String getMetadataCarpeta(String llaveCarpeta) throws Exception {
        String xml = createXMLComando("GET_CARPETA",
                new KeyValue("despacho", despacho),
                new KeyValue("llave", llaveCarpeta));

        System.out.println("XML:::");
        System.out.println(xml);
        ResultadoWS resultado = enviarOperacionWS(xml);

        if (!resultado.isExito()) {
            throw new ValidationException("Error al obtener la url de la carpeta: " + resultado.getMensaje());
        }
        String url = resultado.getMensaje();
        return url;
    }

    public String getLlaveCarpeta() throws Exception {

        String xml = createXMLComando("GET_KEY", new KeyValue("despacho", despacho));

        ResultadoWS resultado = enviarOperacionWS(xml);

        if (!resultado.isExito()) {
            throw new ValidationException("Error al obtener llave de carpeta: " + resultado.getMensaje());
        }
        String llave = resultado.getMensaje();
        return llave;
    }

    private ResultadoWS enviarOperacionWS(String xmlComando) throws Exception {

        WSParameterUtils parametros = new WSParameterUtils();
        parametros.addParameter("xmlOperation", xmlComando);
        parametros.addParameter("token", tokenAutenticacion);
        parametros.addParameter("rutAgencia", rutAgencia);

        ResultadoWS res = getWSClient().invoke("publicOperation", parametros);

        return res;
    }

    private String createXMLComando(String nombreComando, KeyValue... parameter) throws Exception {

        StringBuilder builder = new StringBuilder();

        builder.append("<CarpetaElectronicaConnector>");
        builder.append("<operation name='" + nombreComando + "'>");
        for (KeyValue kv : parameter) {
            builder.append("<parameter id='" + kv.getKey() + "'>" + kv.getValue() + "</parameter>");
        }
        builder.append("</operation>");
        builder.append("</CarpetaElectronicaConnector>");

        String xml = builder.toString();
        return xml;
    }
}
