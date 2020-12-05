
import java.math.BigInteger;


public class DHParams {
  // These parameters are suitable for using in the Diffie-Hellman key
  // exchange protocol.
  public static final BigInteger g = BigInteger.valueOf(5);
  public static final BigInteger p;

  private static final String pStr = 
  "72a925f760b2f954ed287f1b0953f3e6aef92e456172f9fe86fdd8822241b9c9788"+
  "fbc289982743efbcd2ccf062b242d7a567ba8bbb40d79bca7b8e0b6c05f835a5b93"+
  "8d985816bc648985adcff5402aa76756b36c845a840a1d059ce02707e19cf47af0b"+
  "5a882f32315c19d1b86a56c5389c5e9bee16b65fde7b1a8d74a7675de9b707d4c5a"+
  "4633c0290c95ff30a605aeb7ae864ff48370f13cf01d49adb9f23d19a439f753ee7"+
  "703cf342d87f431105c843c78ca4df639931f3458fae8a94d1687e99a76ed99d0ba"+
  "87189f42fd31ad8262c54a8cf5914ae6c28c540d714a5f6087a171fb74f4814c6f9"+
  "68d72386ef356a05180c3bec7ddd5ef6fe76bfd8717";

  static {
    p = new BigInteger(pStr, 16);
  }
}
