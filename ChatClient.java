
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import java.io.IOException;

public class ChatClient {
  public static byte EndStreamDelimiter = (byte) '\n';
  private Set randoms = Collections.synchronizedSet(new HashSet<Byte>());

  public ChatClient(String username, String serverHost, int serverPort) throws IOException {
    Socket sock = new Socket(serverHost, serverPort);
    OutputStream out = sock.getOutputStream();
    new ReceiverThread(sock.getInputStream());
    // Send out username to the server.
    // Add Carriage Return (CR) first; receiver will have to strip CR
    // CR is needed because receiver takes input one line at a time;
    // so we need to put the username onto its own line.
    out.write((username).getBytes());
    out.write(EndStreamDelimiter);
    out.flush();

    // new code added from here
    SecProto mSecProto = new SecProto();
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    // Read in what the user types, and send it to the server.
    for (;;) {
      int c = System.in.read();
      if (c == -1)
        break;
      baos.write(c);
      if (c == '\n') {
        if (baos.size() != 0) {
          byte[] buffer = baos.toByteArray();
          byte[] encData = mSecProto.pack(buffer);
          out.write(encData);
          out.write(EndStreamDelimiter);
          out.flush();
        } else {
          System.out.println("Empty Buffer!");
          break;
        }
        baos.reset();
      }
    }
    baos.close();
    out.close();
    sock.close();
  }

  public static void main(String[] argv) {
    String username = argv[0];
    String hostname = (argv.length <= 1) ? "localhost" : argv[1];
    try {
      new ChatClient(username, hostname, ChatServer.portNum);
    } catch (IOException x) {
      x.printStackTrace();
    }
  }

  class ReceiverThread extends Thread {
    // This is a thread that waits for bytes to arrive from the server.
    // When a whole line of text has arrived (or when the connection from
    // the server is broken, it prints the line of incoming text.
    //
    // We put this in a separate thread so that the printing of incoming
    // text can proceed concurrently with the entry and sending of new
    // text.

    private InputStream in;
    SecProto mSecProto;

    ReceiverThread(InputStream inStream) {
      in = inStream;
      mSecProto = new SecProto();
      start();
    }

    public void run() {
      try {
        ByteArrayOutputStream baos; // queues up stuff until carriage-return
        baos = new ByteArrayOutputStream();
        for (;;) {
          int c = in.read();
          if (c == -1) {
            // connection from server was broken; output what we have
            spew(baos);
            break;
          }
          baos.write(c);
          if (c == '\n') {
            mspew(baos); // got end of line; output what we have
          }
        }
      } catch (IOException x) {
      }
    }

    private void spew(ByteArrayOutputStream baos) throws IOException {
      // Output the contents of baos; then reset (to empty) baos.
      byte[] message = baos.toByteArray();
      baos.reset();
      System.out.write(message);
    }

    private void mspew(ByteArrayOutputStream baos) throws IOException {
      SecProto.SecProEnc temp = mSecProto.splitUserName(baos.toByteArray());
      SecProto.SecProData decryptedData = mSecProto.unpack(temp.getData());
      // attach end stream delimiter to the end of plain text
      byte[] plain = new byte[decryptedData.getData().length + 1];
      System.arraycopy(decryptedData.getData(), 0, plain, 0, decryptedData.getData().length);
      plain[decryptedData.getData().length] = EndStreamDelimiter;
      // check randomness of message
      byte[] rnd = decryptedData.getRandom();
      byte[] msg;
      if (!randoms.contains(rnd)) {
        randoms.add(rnd);
        msg = SecProto.ConCatByteArr(temp.getUserName(), plain);
      } else {
        msg = "!!REPLAY!!\r\n".getBytes();
      }
      baos.reset();
      System.out.write(msg);
    }

  }
}
