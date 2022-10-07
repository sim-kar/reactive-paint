package se.miun.dt176g.sika2001.reactive;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * A server that listens for client {@link Socket}s on the same port. Start the server with
 * {@link Server#start()}, and stop it by disposing of the Observable returned by
 * {@link Server#start()}.
 */
public class Server {

    private final int port;
    private final ServerSocket serverSocket;

    /**
     * Create a new Server using the given port number.
     *
     * @param port the port number to use
     * @throws IOException if an I/O error occurs when opening the socket
     */
    public Server(int port) throws IOException {
        this.port = port;
        this.serverSocket = new ServerSocket(port);
    }

    /**
     * Starts listening for clients. Returns an Observable of all connected clients
     * (as {@link Socket}s). Dispose of the Observable to stop the server.
     *
     * @return an Observable of connected clients as Sockets
     */
    public Observable<Socket> start() {
        return Observable.<Socket>create(emitter -> {
            while (!emitter.isDisposed()) { // keep server active until disposed
                emitter.onNext(serverSocket.accept());
            }
        }).subscribeOn(Schedulers.io());
    }

    /**
     * Get the port number that this Server is bound to.
     *
     * @return the port number
     */
    public int getPort() {
        return port;
    }
}
