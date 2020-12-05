
import java.math.BigInteger;


public class InsecureSharedValue {
    public static byte[] getValue() {
	String str = Config.getAsString("InsecureSharedValue");
	BigInteger bi = new BigInteger(str);
	return bi.toByteArray();
    }
}
