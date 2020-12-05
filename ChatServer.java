
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import java.io.IOException;

public class ChatServer {
  public static final int portNum = Config.getAsInt("ServerPortNum");

  // activeSenders is the list of clients that are currently active.
  private final Set activeSenders = Collections.synchronizedSet(new HashSet());

  public ChatServer() {
    // This constructor never returns, unless there is an error.
    try {
      ServerSocket ss;
      ss = new ServerSocket(portNum);
      for (;;) {
        // wait for a new client to connect, then hook it up properly
        final Socket sock = ss.accept();
        final InputStream in = sock.getInputStream();
        final OutputStream out = sock.getOutputStream();
        System.err.println("Got connection");
        final SenderThread sender = new SenderThread(out);
        new ReceiverThread(in, sender);
      }
    } catch (final IOException x) {
      System.err.println("Dying: IOException");
    }
  }

  public static void main(final String[] argv) {
    new ChatServer();
  }

  class SenderThread extends Thread {
    // forwards messages to a client
    // messages are queued when somebody calls queueForSending
    // we take them from the queue and send them along

    private final OutputStream out;
    private final Queue queue;

    SenderThread(final OutputStream outStream) {
      out = outStream;
      queue = new Queue();
      activeSenders.add(this);
      start();
    }

    public void queueForSending(final byte[] message) {
      // Queue a message, to be sent as soon as possible.
      // We queue messages, rather than sending them immediately, because
      // sending immediately would cause us to block, if the client
      // had fallen behind in processing his incoming messages. If we
      // blocked, the processing of incoming messages would be frozen,
      // which would be Very Bad. By queueing messages, we can ensure that
      // the processing of incoming messages never stalls, no matter how
      // badly clients behave.

      queue.put(message);
    }

    public void run() {
      // suck messages out of the queue and send them out
      try {
        for (;;) {
          final Object o = queue.get();
          final byte[] barr = (byte[]) o;
          out.write(barr);
          out.flush();
        }
      } catch (final IOException x) {
        // unexpected exception -- stop relaying messages
        x.printStackTrace();
        try {
          out.close();
        } catch (final IOException x2) {
        }
      }
      activeSenders.remove(this);
    }
  }

  class ReceiverThread extends Thread {
    // receives messages from a client, and forwards them to everybody else

    private final InputStream in;
    private final SenderThread me;
    private byte[] userNameBytes = null;

    ReceiverThread(final InputStream inStream, final SenderThread mySenderThread) {
      in = inStream;
      me = mySenderThread;
      start();
    }

    public void run() {
      // get first line, which is the client's username
      ByteArrayOutputStream baos = getOneLine(false);

      String name = new String(baos.toByteArray());
      name = name.substring(0, name.length() - 1); // trim trailing carriage return
      name = "[" + name + "] ";
      userNameBytes = name.getBytes();
      // get subsequent lines, and sent them to the other clients
      for (;;) {
        baos = getOneLine(true);
        // printLog("encrypted text = " + new String(baos.toByteArray()));
        sendToOthers(baos);
        baos.reset();
      }
    }

    private ByteArrayOutputStream getOneLine(final boolean prependUserName) {
      final ByteArrayOutputStream baos = new ByteArrayOutputStream();
      // read in a message, terminated by carriage-return
      // buffer the message in baos, until we see EOF or carriage-return
      // return the message we got
      // if prependUserName is true, then prepend the username of the
      // sender to the message before we return it
      try {
        if (prependUserName)
          baos.write(userNameBytes);
        int c;
        do {
          c = in.read();
          if (c == -1) {
            // got EOF -- return what we have, then quit
            return (baos);
          }
          baos.write(c);
        } while (c != '\n');
        // return what we have -- note: this includes a final CR
        return (baos);
      } catch (final IOException x) {
        // return what we have, then quit
        return (baos);
      }
    }

    // stArr is a dummy variable, used to make toArray happy below
    private final SenderThread[] stArr = new SenderThread[1];

    private void sendToOthers(final ByteArrayOutputStream baos) {
      // extract the contents of baos, and queue them for sending to all
      // other clients (but not to ourself);
      // also, reset baos so it is empty and can be reused

      final byte[] message = baos.toByteArray();
      baos.reset();

      final SenderThread[] guys = (SenderThread[]) (activeSenders.toArray(stArr));
      for (int i = 0; i < guys.length; ++i) {
        final SenderThread st = guys[i];
        if (st != me)
          st.queueForSending(message);
      }
    }
  }

  private void printLog(String msg) {
    System.out.println("> Server Log: " + msg);
  }
}
