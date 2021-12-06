package cl.edinet.carpetaselectronicas.test.utils;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.soap.Fault;
import org.apache.soap.SOAPException;
import org.apache.soap.encoding.SOAPMappingRegistry;
import org.apache.soap.encoding.soapenc.StringDeserializer;
import org.apache.soap.rpc.Call;
import org.apache.soap.rpc.Response;
import org.apache.soap.transport.http.SOAPHTTPConnection;
import org.apache.soap.util.xml.QName;

/**
 * @author Cristian
 */
public class ClienteWS {

    private URL url = null;
    private Call call = null;
    private AuthWSClient autenticacion = null;
    private String targetURI = null;
    public boolean DEBUG = false;
    private Integer timeout = null;

    public ClienteWS(String url, String targetURI, AuthWSClient auth) throws MalformedURLException {
        if (!targetURI.endsWith("/")) {
            targetURI = targetURI + "/";
        }
        this.targetURI = targetURI;
        this.autenticacion = auth;
        this.url = new URL(url);
        initCall();
    }

    private void initCall() {
        this.call = new Call();
        this.call.setTargetObjectURI(targetURI);
        this.call.setEncodingStyleURI("http://schemas.xmlsoap.org/soap/encoding/");
        SOAPMappingRegistry smr = new SOAPMappingRegistry();
        smr.mapTypes(org.apache.soap.Constants.NS_URI_SOAP_ENC, new QName("", "return"), null, null, new StringDeserializer());
        this.call.setSOAPMappingRegistry(smr);
    }

    public String invokeAndReturnOnlySuccess(String methodName) throws Exception {
        return invokeAndReturnOnlySuccess(methodName, new WSParameterUtils());
    }

    public String invokeAndReturnOnlySuccess(String methodName, WSParameterUtils wsParameterUtils) throws Exception {
        try {
            ResultadoWS resultado = invoke(methodName, wsParameterUtils);

            if (!resultado.isExito()) {
                throw new Exception("Ocurrio un error al ejecutar el metodo ws. " + resultado.getMensaje());
            }
            return resultado.getMensaje();

        } catch (Exception e) {
            throw e;
        }
    }

    public ResultadoWS invoke(String methodName) throws WSClientException {
        return invoke(methodName, new WSParameterUtils());
    }

    public ResultadoWS invoke(String methodName, WSParameterUtils parameterUtils) throws WSClientException {
        return invoke(methodName, parameterUtils.getVector());
    }

    public ResultadoWS invoke(String methodName, Vector vectorParametros) throws WSClientException {
        try {

            this.initCall();
            this.call.setMethodName(methodName);
            this.call.setParams(vectorParametros);

            SOAPHTTPConnection st = new SOAPHTTPConnection();
            st.setUserName(autenticacion.getNik());
            st.setPassword(autenticacion.getPassword());
            this.call.setSOAPTransport(st);

            if (timeout != null) {
                this.call.setTimeout(timeout);
            }

            Response response = this.call.invoke(url, methodName);

            if (response.generatedFault()) {
                Fault fault = response.getFault();
                return new ResultadoWS(false, fault.getFaultString());
            } else {
                String xml = response.getReturnValue().getValue().toString();
                if (DEBUG) {
                    System.out.println("DefaultWSClient.xml response: " + xml);
                }

                if (xml == null) {
                    new WSClientException("No se encontro ningun resultado desde el WS");
                }

                if (xml.startsWith("<error>")) {
                    xml = xml.replaceAll("<error>", "");
                    if (xml != null) {
                        xml = xml.replaceAll("</error>", "");
                    }
                    if (xml == null) {
                        xml = "error desconocido";
                    }
                    //System.out.println("DefaultWSClient.xml response-error: " + xml);
                    throw new WSClientException(xml, xml);
                } else {
                    return new ResultadoWS(true, response.getReturnValue().getValue().toString());
                }
            }

        } catch (WSClientException e) {
            Logger.getLogger(ClienteWS.class.getName()).log(Level.SEVERE, "message", e);
            throw e;
        } catch (SOAPException e) {
            if (DEBUG) {
                Logger.getLogger(ClienteWS.class.getName()).log(Level.INFO, "DefaultWSClient.invoke. SOAPException: ", e);
            }
            throw new WSClientException(e.getMessage());
        } catch (Exception e) {
            Logger.getLogger(ClienteWS.class.getName()).log(Level.SEVERE, "message", e);
            return new ResultadoWS(e);
        } finally {
        }
    }

    public URL getUrl() {
        return url;
    }

    public void setUrl(URL url) {
        this.url = url;
    }

    public void setAuth(String username, String password) {
        this.autenticacion = new AuthWSClient(username, password);
    }

    public boolean isDEBUG() {
        return DEBUG;
    }

    public void setDEBUG(boolean DEBUG) {
        this.DEBUG = DEBUG;
    }

    public AuthWSClient getAutenticacion() {
        return autenticacion;
    }

    public void setAutenticacion(AuthWSClient autenticacion) {
        this.autenticacion = autenticacion;
    }

    public Integer getTimeout() {
        return timeout;
    }

    public void setTimeout(Integer timeout) {
        this.timeout = timeout;
    }
}
