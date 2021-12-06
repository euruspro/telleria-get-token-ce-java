package cl.edinet.carpetaselectronicas.test.utils;

import java.util.Vector;
import org.apache.soap.rpc.Parameter;

/**
 * @author Cristian
 */
public class WSParameterUtils {

    private Vector<Parameter> vector = null;

    public WSParameterUtils() {
        vector = new Vector<Parameter>();
    }

    public void addParameter(String key, String value) {
        vector.add(new Parameter(key, String.class, value, null));
    }

    public Vector<Parameter> getVector() {
        return vector;
    }

    public void setVector(Vector<Parameter> vector) {
        this.vector = vector;
    }
}
