import java.util.Arrays;

public class SecProto {
  private int SHA1_SIZE = 20;
  private int RND_SIZE = RandomSeed.NumBytes;
  private byte[] mKey;
  private HashFunction mHashFunction;
  private BlockCipher mBCGenerator;

  public SecProto() {
    init();
  }

  public void init() {
    mKey = InsecureSharedValue.getValue();
    mHashFunction = new HashFunction();
    mBCGenerator = new BlockCipher(mKey);
  }
  public byte[] pack(byte[] message) {
    byte[] temp = Arrays.copyOf(message, message.length - 2);
    byte[] msg = addPadding(temp);
    byte[] random = RandomSeed.getArray();
    byte[] concatArray = ConCatByteArr(msg, random);
    mHashFunction.update(concatArray);
    byte[] hashedArray = mHashFunction.digest();
    byte[] feed = ConCatByteArr(concatArray, hashedArray);
    int len = feed.length;
    byte[] result = new byte[len];
    int maxLen = len / 8;
    int offset = 0;
    for (int i = 0; i < maxLen; i++) {
      mBCGenerator.encrypt(feed, offset, result, offset);
      offset += 8;
    }
    return result;
  }

  public SecProData unpack(byte[] cipher) {
    SecProData result;
    int len = cipher.length;
    int maxLen = len / 8;

    byte[] decrypted = new byte[len];
    int offset = 0;

    // decrypt data
    for (int i = 0; i < maxLen; i++) {
      mBCGenerator.decrypt(cipher, offset, decrypted, offset);
      offset += 8;
    }
    len = decrypted.length;
    int a = len - SHA1_SIZE;
    int b = a - RND_SIZE;
    byte[] hashed = Arrays.copyOfRange(decrypted, a, len);
    byte[] random = Arrays.copyOfRange(decrypted, b, a);
    byte[] data = Arrays.copyOfRange(decrypted, 0, b);
    mHashFunction.reset();
    mHashFunction.update(ConCatByteArr(data, random));
    byte[] newHashed = mHashFunction.digest();

    if (Arrays.equals(hashed, newHashed)) {
      result = new SecProData(data, random);
      return result;
    } else {
      data = new String("!!WRONG!!\n").getBytes();
      result = new SecProData(data, random);
      return result;
    }

  }

  private byte[] addPadding(byte[] message) {
    int len = message.length + (SHA1_SIZE + RND_SIZE);
    if ((len % 8) != 0) {
      int over = 8 - (len % 8);
      byte[] temp = Arrays.copyOf(message, (message.length + over));
      temp[temp.length - 1] = (byte) '\r';
      return temp;
    } else {
      return message;
    }
  }

  public static byte[] ConCatByteArr(byte[] a, byte[] b) {
    byte[] c = new byte[a.length + b.length];
    System.arraycopy(a, 0, c, 0, a.length);
    System.arraycopy(b, 0, c, a.length, b.length);
    return c;
  }

  private void printLog(String msg) {
    System.out.println("> SecProto Log: " + msg);
  }

  public SecProEnc splitUserName(byte[] data) {
    // [name] data
    int i = findCharIndexoOf(data, ']');
    byte[] userName = Arrays.copyOfRange(data, 0, i + 2);
    byte[] encData = Arrays.copyOfRange(data, i + 2, data.length - 1);
    SecProEnc res = new SecProEnc(encData, userName);
    return res;
  }

  public int findCharIndexoOf(byte[] data, char ch) {
    for (int i = 0; i < data.length; i++) {
      if (data[i] == (byte) ch)
        return i;
    }
    return -1;
  }

  public class SecProEnc {
    private byte[] mData;
    private byte[] mName;

    public SecProEnc(byte[] data, byte[] name) {
      this.mData = data;
      this.mName = name;
    }

    public byte[] getData() {
      return this.mData;
    }

    public byte[] getUserName() {
      return this.mName;
    }
  }

  public class SecProData {
    private byte[] mData;
    private byte[] mRandom;

    public SecProData(byte[] data, byte[] random) {
      this.mData = data;
      this.mRandom = random;
    }

    public byte[] getData() {
      return this.mData;
    }

    public byte[] getRandom() {
      return this.mRandom;
    }
  }
}