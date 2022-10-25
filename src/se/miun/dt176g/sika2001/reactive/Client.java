package se.miun.dt176g.sika2001.reactive;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * A client for writing and reading objects to and from a server. Acts as a wrapper for the given
 * socket.
 */
public class Client {

    private final Socket clientSocket;
    private final ObjectInputStream input;
    private final ObjectOutputStream output;

    /**
     * Create a new client with the given socket.
     *
     * @param clientSocket the socket to use
     * @throws IOException if an I/O error occurs when getting the socket's input and output streams
     */
    public Client(Socket clientSocket) throws IOException {
        this.clientSocket = clientSocket;

        // output stream needs to be initialized before input stream, or it will block forever
        this.output = new ObjectOutputStream(clientSocket.getOutputStream());
        this.input = new ObjectInputStream(clientSocket.getInputStream());
    }

    /**
     * Read an object from the connected server.
     *
     * @return the object
     * @throws IOException if an I/O error occurs when reading the object
     * @throws ClassNotFoundException if the class of the object being read can't be found
     */
    public Object read() throws IOException, ClassNotFoundException {
        return input.readObject();
    }

    /**
     * Write an object to the connected server.
     *
     * @param object the object to write
     * @throws IOException if an I/O error occurs when writing the object
     */
    public void write(Object object) throws IOException {
        output.writeObject(object);
    }

    public ObjectInputStream getInput() {
        return this.input;
    }

    public ObjectOutputStream getOutput() {
        return this.output;
    }

    /**
     * Close the socket of this client.
     *
     * @throws IOException if an I/O error occurs when closing the socket
     */
    public void shutdown() throws IOException {
        clientSocket.close();
    }

    /**
     * Check whether this client has been shutdown, i.e. its socket has been closed.
     *
     * @return whether the client is shutdown
     */
    public boolean isShutdown() {
        return clientSocket.isClosed();
    }
}
