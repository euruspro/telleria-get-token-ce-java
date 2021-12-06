/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cl.edinet.carpetaselectronicas.test.utils;

import java.io.File;
import java.io.FileInputStream;
import java.security.KeyStore;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

/**
 *
 * @author Cristian
 */
public class FirmaXML {

    public static String sign(File filePKCS12, String pinPKCS12, String xml) throws ValidationException {
        try {
            KeyStore keyStore = KeyStore.getInstance("PKCS12");

            keyStore.load(new FileInputStream(filePKCS12), pinPKCS12.toCharArray());

            String alias = "";
            Enumeration enu = keyStore.aliases();
            while (enu.hasMoreElements()) {
                alias = (String) enu.nextElement();
            }
            XMLSignConnector xmlSign = new XMLSignConnector(keyStore, pinPKCS12.toCharArray(), alias);

            Document doc = Utils.parseXml(xml.getBytes());
            NodeList securityHeader = doc.getElementsByTagName("Seed");

            xmlSign.signEnveloped(securityHeader.item(0), "", "ds");

            return new String(Utils.dumpSignedXML(doc));

        } catch (Exception ex) {
            Logger.getLogger(FirmaXML.class.getName()).log(Level.SEVERE, null, ex);
            throw new ValidationException("Error al firmar solicitud de autenticacion: " + ex.getMessage());
        }
    }
}
