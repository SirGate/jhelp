/*
 * Server.java
 *
 */
package jhelp;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.sql.SQLException;


/**
 * This class sets a network connection between end client's objects of
 * {@link jhelp.Client} type and single {@link jhelp.ServerDb} object.
 *
 * author Tulupov Sergei
 * @see jhelp.Client
 * @see jhelp.ClientThread
 * @see jhelp.ServerDb
 */
public class Server implements JHelp {

    private ServerSocket serverSocket;
    private Socket clientSocket;
    private ClientThread[] clients;
    public ObjectInputStream inputdb;
    public ObjectOutputStream outputdb;

    public Server(int port, int dbPort) throws IOException {
        serverSocket = new ServerSocket(DEFAULT_SERVER_PORT);
        clients = new ClientThread[50];
    }

    public static void main(String[] args) throws SQLException, IOException {
        Server server = new Server(DEFAULT_SERVER_PORT, DEFAULT_DATABASE_PORT);
        server.run();
        server.disconnect();
    }

    private void run() {
        int i = 0;
        connect();
        System.out.println("SERVER: run");
        while (true) {
            Socket tmp = null;
            String s = "";
            try {
                tmp = serverSocket.accept();
                s = s + i;
                clients[i] = new ClientThread(this, tmp, s.trim());
                System.out.println("Client " + i + " connected");
                ++i;
                if (i > clients.length - 1) {
                    i = 0;
                }
                if (tmp.isClosed()) {
                    break;
                }
            } catch (IOException ex) {
                System.out.println("Error: can't make connection");
            }
        }
    }

    /**
     * The method sets connection to database ({@link jhelp.ServerDb} object)
     *
     * @return error code. The method returns {@link JHelp#OK} if streams are
     * successfully opened, otherwise the method returns {@link JHelp#ERROR}.
     */
    public int connect() {
        try {
            clientSocket = new Socket("localhost", JHelp.DEFAULT_DATABASE_PORT);
            outputdb = new ObjectOutputStream(clientSocket.getOutputStream());
            inputdb = new ObjectInputStream(clientSocket.getInputStream());
        } catch (UnknownHostException ex) {
            System.out.println("Error : Can't connection to Database");
        } catch (IOException ex) {
            System.out.println("Error:  Can't connection to Database");
        }
        System.out.println("Server connected to Database");
        return OK;
    }

    @Override
    public int connect(String[] args) {
        return OK;
    }

    /**
     * Transports initial {@link Data} object from {@link ClientThread} object
     * to {@link ServerDb} object and returns modified {@link Data} object to
     * {@link ClientThread} object.
     *
     * @param data Initial {@link Data} object which was obtained from client
     * application.
     * @return modified {@link Data} object
     */
    @Override
    synchronized public Data getData(Data data) {
        try {
            outputdb.writeObject(data);
            data = (Data) inputdb.readObject();
        } catch (IOException ex) {
            System.out.println("Error : Can't transfer data to Database");
        } catch (ClassNotFoundException ex) {
            System.out.println("Error : Can't transfer data to Database");
        }
        return data;
    }

    /**
     * The method closes connection with database.
     *
     * @return error code. The method returns {@link JHelp#OK} if a connection
     * with database ({@link ServerDb} object) closed successfully, otherwise
     * the method returns {@link JHelp#ERROR} or any error code.
     */
    public int disconnect() {
        try {
            inputdb.close();
            outputdb.close();
            clientSocket.close();
            serverSocket.close();
        } catch (IOException ex) {
            System.out.println("Error by closing connection to Database"
                    + " or output/input streams ");
            return ERROR;
        }
        System.out.println("SERVER: disconnected from Database");
        return OK;
    }
}
