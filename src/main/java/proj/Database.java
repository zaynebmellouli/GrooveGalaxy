package proj;

import javax.net.SocketFactory;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.*;
import java.util.Scanner;

public class Database {
    public static void startClient(String host, int port) throws IOException {

        SocketFactory factory = SSLSocketFactory.getDefault();
        try (SSLSocket socket = (SSLSocket) factory.createSocket(host, port)) {

            socket.setEnabledCipherSuites(new String[] { "TLS_AES_128_GCM_SHA256" });
            socket.setEnabledProtocols(new String[] { "TLSv1.3" });

            String message = "This is a secure channel!";
            System.out.println("sending message: " + message);
            OutputStream os = new BufferedOutputStream(socket.getOutputStream());
            os.write(message.getBytes());
            os.flush();

            InputStream is   = new BufferedInputStream(socket.getInputStream());
            byte[]      data = new byte[2048];
            int len = is.read(data);
            System.out.printf("client received %d bytes: %s%n", len, new String(data, 0, len));

            String line = "";
            try (Scanner scanner = new Scanner(System.in)) {
                System.out.println("Type: 'Exit' to close the connection");

                while (!line.equals("Exit")) {
                    try {
                        line = scanner.nextLine();
                        ;
                        System.out.println("sending message: " + line);
                        os = new BufferedOutputStream(socket.getOutputStream());
                        os.write(line.getBytes());
                        os.flush();

                        is = new BufferedInputStream(socket.getInputStream());
                        data = new byte[2048];
                        len = is.read(data);
                        System.out.printf("client received %d bytes: %s%n", len, new String(data, 0, len));
                    } catch (IOException i) {
                        System.out.println(i);
                        return;
                    }
                }
                try {
                    scanner.close();
                    is.close();
                    os.close();
                    socket.close();
                } catch (IOException i) {
                    System.out.println(i);
                    return;
                }
            }

        }
    }

    public static void main(String args[]) throws IOException {
        System.setProperty("javax.net.ssl.keyStore", "https_cert/user.p12");
        System.setProperty("javax.net.ssl.keyStorePassword", "changeme");
        System.setProperty("javax.net.ssl.trustStore", "https_cert/usertruststore.jks");
        System.setProperty("javax.net.ssl.trustStorePassword", "changeme");
        if (args.length == 2) {
            String host = args[0];
            int port = Integer.parseInt(args[1]);
            startClient(host, port);
        } else {
            System.out.println("Usage: java Database <host> <port>");
            return;
        }

    }
}
