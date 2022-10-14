package se.miun.dt176g.sika2001.reactive;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Client {

    private final Socket clientSocket;
    private final ObjectInputStream input;
    private final ObjectOutputStream output;

    public Client(Socket clientSocket) throws IOException {
        this.clientSocket = clientSocket;

        // output stream needs to be initialized before input stream, or it will block forever
        this.output = new ObjectOutputStream(clientSocket.getOutputStream());
        this.input = new ObjectInputStream(clientSocket.getInputStream());
        System.out.println("Client class init finished!");
    }

    public Object read() throws IOException, ClassNotFoundException {
        return input.readObject();
    }

    public void write(Object object) throws IOException {
        output.writeObject(object);
    }

    public ObjectInputStream getInput() {
        return this.input;
    }

    public ObjectOutputStream getOutput() {
        return this.output;
    }
}
