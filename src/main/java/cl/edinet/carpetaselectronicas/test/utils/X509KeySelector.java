/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cl.edinet.carpetaselectronicas.test.utils;

import java.security.Key;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.util.Iterator;
import javax.xml.crypto.AlgorithmMethod;
import javax.xml.crypto.KeySelector;
import javax.xml.crypto.KeySelectorException;
import javax.xml.crypto.KeySelectorResult;
import javax.xml.crypto.XMLCryptoContext;
import javax.xml.crypto.XMLStructure;
import javax.xml.crypto.dsig.SignatureMethod;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.keyinfo.X509Data;

/**
 *
 * @author zulo
 */
public class X509KeySelector extends KeySelector {

    private X509Certificate certificadoSeleccionado = null;

    @Override
    public KeySelectorResult select(KeyInfo keyInfo,
            KeySelector.Purpose purpose,
            AlgorithmMethod method,
            XMLCryptoContext context)
            throws KeySelectorException {
        if (keyInfo == null) {
            throw new KeySelectorException("No se encontro ningun certificado para varificar.");
        }
        Iterator ki = keyInfo.getContent().iterator();
        while (ki.hasNext()) {
            XMLStructure info = (XMLStructure) ki.next();
            if (!(info instanceof X509Data)) {
                continue;
            }
            X509Data x509Data = (X509Data) info;
            Iterator xi = x509Data.getContent().iterator();
            while (xi.hasNext()) {
                final Object o = xi.next();
                if (!(o instanceof X509Certificate)) {
                    continue;
                }
                X509Certificate cert = (X509Certificate) o;
                //System.out.println("cert: " + cert);
                final PublicKey key = cert.getPublicKey();
                // Make sure the algorithm is compatible
                // with the method.
                if (algEquals(method.getAlgorithm(), key.getAlgorithm())) {
                    return new KeySelectorResult() {

                        public Key getKey() {
                            certificadoSeleccionado = (X509Certificate) o;
                            return key;
                        }
                    };
                }
            }
        }
        throw new KeySelectorException("Llave de validacion no encontrada.");
    }

    static boolean algEquals(String algURI, String algName) {
        if ((algName.equalsIgnoreCase("DSA")
                && algURI.equalsIgnoreCase(SignatureMethod.DSA_SHA1))
                || (algName.equalsIgnoreCase("RSA")
                && algURI.equalsIgnoreCase(SignatureMethod.RSA_SHA1))) {
            return true;
        } else {
            return false;
        }
    }

    public X509Certificate getCertificadoSeleccionado() {
        return certificadoSeleccionado;
    }

    public void setCertificadoSeleccionado(X509Certificate certificadoSeleccionado) {
        this.certificadoSeleccionado = certificadoSeleccionado;
    }
}
