package cl.edinet.carpetaselectronicas.test.utils;

/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
import java.security.*;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.crypto.dsig.*;
import javax.xml.crypto.dsig.dom.DOMSignContext;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.keyinfo.KeyInfoFactory;
import javax.xml.crypto.dsig.keyinfo.X509Data;
import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec;
import javax.xml.crypto.dsig.spec.TransformParameterSpec;
import org.w3c.dom.Node;

/**
 * @author Cristian
 */
public class XMLSignConnector {

    private KeyStore ks;
    private char[] pin;
    private String alias;

    public XMLSignConnector(KeyStore ks, char[] pin, String alias) {
        this.ks = ks;
        this.pin = pin;
        this.alias = alias;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public KeyStore getKs() {
        return ks;
    }

    public void setKs(KeyStore ks) {
        this.ks = ks;
    }

    public char[] getPin() {
        return pin;
    }

    public void setPin(char[] pin) {
        this.pin = pin;
    }

    public void signEnveloped(Node securityHeader,
            String uri,
            String namespacePrefix
    ) throws Exception {
        try {
            XMLSignatureFactory fac = XMLSignatureFactory.getInstance("DOM");
            DigestMethod digestMethod = fac.newDigestMethod(DigestMethod.SHA1, null);
            C14NMethodParameterSpec spec = null;
            CanonicalizationMethod canolizationMethod_INCLUSIVE = fac.newCanonicalizationMethod(CanonicalizationMethod.INCLUSIVE, spec);
            SignatureMethod signatureMethod = fac.newSignatureMethod(SignatureMethod.RSA_SHA1, null);
            ArrayList<Transform> transformList = new ArrayList();
            TransformParameterSpec transformSpec = null;
            Transform envTransform = fac.newTransform(CanonicalizationMethod.ENVELOPED, transformSpec);
            transformList.add(envTransform);
            Reference ref = null;

            ref = fac.newReference("", digestMethod, transformList, null, null);

            ArrayList refList = new ArrayList();
            refList.add(ref);
            SignedInfo signedInfo = fac.newSignedInfo(canolizationMethod_INCLUSIVE, signatureMethod, refList);
            DOMSignContext signContext = null;
            KeyStore.PrivateKeyEntry keyEntry = (KeyStore.PrivateKeyEntry) ks.getEntry(alias, new KeyStore.PasswordProtection(pin));
            signContext = new DOMSignContext(keyEntry.getPrivateKey(), securityHeader);
            if (namespacePrefix != null) {
                signContext.setDefaultNamespacePrefix(namespacePrefix);
            }
            X509Certificate cert = (X509Certificate) keyEntry.getCertificate();

            KeyInfoFactory keyFactory = fac.getKeyInfoFactory();

            List x509Content = new ArrayList();

            x509Content.add(cert);
            X509Data xd = keyFactory.newX509Data(x509Content);
            ArrayList keyInfoList = new ArrayList();
            keyInfoList.add(keyFactory.newKeyValue(cert.getPublicKey()));
            keyInfoList.add(xd);

            XMLSignature signature = null;

            KeyInfo keyInfo = keyFactory.newKeyInfo(keyInfoList);

            signature = fac.newXMLSignature(signedInfo, keyInfo);

            signature.sign(signContext);

        } catch (UnrecoverableKeyException e) {
            throw new Exception("No fue posible abrir la firma digital (llave inrecuperable)");
        } catch (Exception ex) {
            Logger.getLogger(XMLSignConnector.class.getName()).log(Level.SEVERE, null, ex);
            throw new Exception("Error al procesar el certificado: " + ex.getMessage());
        }
    }
}
