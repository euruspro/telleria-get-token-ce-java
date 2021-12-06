package cl.edinet.carpetaselectronicas.test.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * @author Cristian
 */
public class Utils {

    public static Document parseXml(byte[] xml) throws ParserConfigurationException, SAXException, IOException {

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        dbf.setIgnoringElementContentWhitespace(true);
        javax.xml.parsers.DocumentBuilder db;
        db = dbf.newDocumentBuilder();
        ByteArrayInputStream in = new ByteArrayInputStream(xml);
        Document document = null;
        try {
            document = db.parse(in);

            return document;
        } catch (Exception e) {
            Logger.getLogger(Utils.class.getName()).log(Level.INFO, "error al leer xml: " + new String(xml), e);
            return null;
        }
    }

    public static byte[] dumpSignedXML(Document doc) throws TransformerConfigurationException, TransformerException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer trans = tf.newTransformer();
        trans.setOutputProperty(OutputKeys.INDENT, "no");
        trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        trans.transform(new DOMSource(doc), new StreamResult(os));
        return os.toByteArray();
    }
}
