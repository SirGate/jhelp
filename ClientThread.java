/*
 * Class ClientThread.
 */
package jhelp;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * This class provides a network connection between end client of
 * {@link jhelp.Client} type and {@link jhelp.Server} object. Every object of
 * this class may work in separate thread.
 *
 * @author Tulupov Sergey
 * @see jhelp.Client
 * @see jhelp.Server
 */
public class ClientThread implements JHelp, Runnable {

    private static Server server;
    private Socket clientSocket;
    private ObjectInputStream input;
    private ObjectOutputStream output;
    private static String name; 
    Thread t;

    /**
     * Creates a new instance of Client
     *
     * @param server reference to {@link Server} object.
     * @param socket reference to {@link java.net.Socket} object for connection
     * with client application.
     */

    public ClientThread(Server server, Socket socket,String name) {
            t=new Thread(this,name);
            clientSocket=socket;
            ClientThread.server = server;
            ClientThread.name=name;
            try {
            input = new ObjectInputStream(clientSocket.getInputStream());
            output = new ObjectOutputStream(clientSocket.getOutputStream());
        } catch (IOException ex) {
             System.out.println("Can't connect with client " + name);
        }
         t.start();
    }

    /**
     * The method defines main job cycle for the object.
     */
    @Override
    public void run() {
    try (Socket socket = clientSocket){
        while (true){
           Data dt=(Data)input.readObject();
            dt=server.getData(dt);
            output.writeObject(dt);
           }
    }
           catch (IOException ex) {   
             System.out.println("Client "+name+" disconnected");
           }
           catch (ClassNotFoundException ex) {   
              System.out.println("Error: can't transfer Data");
           }
            }
   
    @Override
    public int connect() {
        return JHelp.OK;
    }

    @Override
    public int connect(String[] args) {
        System.out.println("MClient: connect");
        return JHelp.OK;
    }

    @Override
    public Data getData(Data data) {
        return data;
    }
    /**
     * The method closes connection with client application.
     * @return error code. The method returns {@link JHelp#OK} if input/output
     * streams and connection with client application was closed successfully,
     * otherwise the method returns {@link JHelp#ERROR}.
     */
    @Override
    public int disconnect() {
        try {
            input.close();
            output.close();
        } catch (IOException ex) {
            System.out.println("Error by closing input/output streams");
            return ERROR;
        }
        System.out.println("Client: disconnected");
        return OK;
    }
}
