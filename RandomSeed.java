
import java.security.SecureRandom;
import java.util.Random;

public class RandomSeed {
    // This class provides you with a random number seed that you can use
    // as a source of randomness in your programs. Use it like this:
    //
    // byte[] seed = RandomSeed.getArray();
    //
    // Afterward, seed will point to an array of RandomSeed.NumBytes
    // bytes, which you can assume are random.
    //
    // If you call getArray more than once, it will return the
    // same array (unchanged) every time, so don't try to use this as
    // a random number generator by calling it repeatedly. Instead, use it
    // as the initial seed of a generator that you create.

    public static final int NumBytes = 16;

    private static byte[] randBytes = null;

    public static byte[] getArray() {
        // if (randBytes == null) {
        Random rand = new SecureRandom();
        randBytes = new byte[NumBytes];
        rand.nextBytes(randBytes);
        // }
        return randBytes;
    }
}
