/*
 * ServerDb.java
 *
 */
package jhelp;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


/**
 * This class presents server directly working with database.
 *
 * author Tulupov Sergei
 */
public class ServerDb implements JHelp {

    private ServerSocket serverSocket;
    private Socket clientSocket;
    private ObjectInputStream input;
    private ObjectOutputStream output;
    private static Connection con;
    private static Statement st;
    public static Data dt;
    private static String str1;
    private static ResultSet rs;

    /**
     * Constructor creates new instance of <code>ServerDb</code>.
     *
     * @param port defines port for {@link java.net.ServerSocket} object.
     */
    public ServerDb(int port) {
        Socket tmp = null;
        String s1 = "jdbc:derby://localhost:1527/JGlossary,Sergey,admin";
        this.connect(s1.split(","));
        try (ServerSocket socket = serverSocket) {
            serverSocket = new ServerSocket(port);
            System.out.println("SERVERDB: run");
            tmp = serverSocket.accept();
            clientSocket = tmp;
            input = new ObjectInputStream(clientSocket.getInputStream());
            output = new ObjectOutputStream(clientSocket.getOutputStream());
            System.out.println("Server connected");
            while (true) {
                Data dt = (Data) input.readObject();
                dt = this.getData(dt);
                output.writeObject(dt);
            }
        } catch (IOException ex) {
            System.out.println("Server disconnected");
        } catch (ClassNotFoundException ex) {
            System.out.println("Error in data reading");
        }
        disconnect();
    }

    public static void main(String[] args) {
        ServerDb srv = new ServerDb(DEFAULT_DATABASE_PORT);
    }

    @Override
    public int connect() {
        System.out.println("SERVERDb: connect");
        return JHelp.READY;
    }

    /**
     * Method sets connection to database and create
     * {@link java.net.ServerSocket} object for waiting of client's connection
     * requests.
     *
     * @return error code. Method returns {@link jhelp.JHelp#READY} in success
     * case. Otherwise method return {@link jhelp.JHelp#ERROR} or error code.
     */
    public int connect(String[] args) {
        try {
            con = DriverManager.getConnection(
                    args[0].trim(), args[1].trim(), args[2].trim()
            );
        } catch (SQLException ex) {
            System.out.println("Error: Can't connected to Database");
        }
        System.out.println("SERVERDb: connected to Database");
        return JHelp.READY;
    }

    /**
     * Method returns result of client request to a database.
     *
     * @param data object of {@link jhelp.Data} type with request to database.
     * @return object of {@link jhelp.Data} type with results of request to a
     * database.
     * @see Data
     * @since 1.0
     */
    @Override
    public Data getData(Data data) {
        try {
            st = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            String str = new String(data.getKey().getItem()).trim();
            str = str.toLowerCase();
            int i = 0;
            if (data.getOperation() == JHelp.SELECT) {
                rs = st.executeQuery("SELECT * FROM TBLTERMS "
                        + "LEFT JOIN TBLDEFINITIONS"
                        + " ON TBLTERMS.ID = TBLDEFINITIONS.TERM_ID");
                while (rs.next()) {
                    String str2 = rs.getString(2).toLowerCase();
                    if (str2.equals(str)) {
                        i++;
                    }
                }
                rs.beforeFirst();
                Item[] val = new Item[i];
                int i1 = 0;
                while (rs.next()) {
                    String str2 = rs.getString(2).toLowerCase();
                    if (str2.equals(str)) {
                        Item dt1 = new Item(rs.getString(4).trim());
                        val[i1] = dt1;
                        val[i1].setItem(rs.getString(4).trim());
                        i1++;
                    }
                }
                Data dt2 = new Data(data.getOperation(), data.getKey(), val);
                data = dt2;
            }
            if (data.getOperation() == JHelp.NEXT) {
                boolean b = false;
                rs = st.executeQuery("SELECT * FROM TBLTERMS "
                        + "LEFT JOIN TBLDEFINITIONS"
                        + " ON TBLTERMS.ID = TBLDEFINITIONS.TERM_ID");
                while (rs.next()) {
                    String str2 = rs.getString(2).toLowerCase();
                    if (str2.equals(str) & (!(rs.isLast()))) {
                        rs.next();
                        str = rs.getString(2).toLowerCase();
                        b = true;
                    }
                }
                if (b = true) {
                    rs.beforeFirst();
                    while (rs.next()) {
                        String str2 = rs.getString(2).toLowerCase();
                        if (str2.equals(str)) {
                            i++;
                        }
                    }
                    rs.beforeFirst();
                    Item[] val = new Item[i];
                    int i1 = 0;
                    while (rs.next()) {
                        String str2 = rs.getString(2).toLowerCase();
                        if (str2.equals(str)) {
                            Item dt1 = new Item(rs.getString(4).trim());
                            val[i1] = dt1;
                            val[i1].setItem(rs.getString(4).trim());
                            i1++;
                        }
                    }
                    Item it = new Item(str);
                    Data dt2 = new Data(data.getOperation(), it, val);
                    data = dt2;
                }
            }
            if (data.getOperation() == JHelp.PREVIOUS) {
                boolean b = false;
                rs = st.executeQuery("SELECT * FROM TBLTERMS "
                        + "LEFT JOIN TBLDEFINITIONS"
                        + " ON TBLTERMS.ID = TBLDEFINITIONS.TERM_ID");
                while (rs.next()) {
                    String str2 = rs.getString(2).toLowerCase();
                    if (str2.equals(str) & (!(rs.isFirst()))) {
                        rs.previous();
                        str = rs.getString(2).toLowerCase();
                        b = true;
                    }
                }
                if (b = true) {
                    rs.beforeFirst();
                    while (rs.next()) {
                        String str2 = rs.getString(2).toLowerCase();
                        if (str2.equals(str)) {
                            i++;
                        }
                    }
                    rs.beforeFirst();
                    Item[] val = new Item[i];
                    int i1 = 0;
                    while (rs.next()) {
                        String str2 = rs.getString(2).toLowerCase();
                        if (str2.equals(str)) {
                            Item dt1 = new Item(rs.getString(4).trim());
                            val[i1] = dt1;
                            val[i1].setItem(rs.getString(4).trim());
                            i1++;
                        }
                    }
                    Item it = new Item(str);
                    Data dt2 = new Data(data.getOperation(), it, val);
                    data = dt2;
                }
            }
            if (data.getOperation() == JHelp.DELETE) {
                st.execute("DELETE FROM TBLTERMS "
                        + "WHERE TBLTERMS.TERM='"
                        + str.trim() + "'"
                );
                Data dt2 = new Data(data.getOperation(), data.getKey(), null);
                data = dt2;
            }
            if (data.getOperation() == JHelp.INSERT) {
                int i1 = -1;
                int i2 = 0;
                rs = st.executeQuery("SELECT * FROM TBLTERMS");
                while (rs.next()) {
                    String str2 = rs.getString(2).toLowerCase();
                    if (str2.equals(str)) {
                        i1 = rs.getInt(1);
                    }
                }
                if (i1 > -1) {
                    st.execute("DELETE FROM TBLDEFINITIONS "
                            + "WHERE TERM_ID="
                            + i1);
                } else if (i1 == -1) {
                    rs = st.executeQuery("SELECT * FROM TBLTERMS");
                    if (rs.last()) {
                        i1 = rs.getInt(1) + 1;
                        st.execute("insert into tblterms values(" + i1 + ",'" + str.trim() + "')");
                    }
                }
                rs = st.executeQuery("SELECT * FROM TBLDEFINITIONS");
                if (rs.last()) {
                    i2 = rs.getInt(1) + 1;
                }
                i = 0;
                String str2 = "";
                while (i < data.getValues().length) {
                    if (data.getValue(i).getItem() != "") {
                        str2 = data.getValue(i).getItem().trim();
                        st.execute("insert into tbldefinitions values(" + i2 + ",'"
                                + str2.trim() + "'," + i1 + ")");
                    }
                    i++;
                    i2++;
                }
            }
            st.close();
        } catch (SQLException e) {
            System.out.println("Error in data reading from Database");
        }
        return data;
    }

    /**
     * Method disconnects <code>ServerDb</code> object from a database and
     * closes {@link java.net.ServerSocket} object.
     *
     * @return disconnect result. Method returns {@link #DISCONNECT} value, if
     * the process ends successfully. Othewise the method returns error code,
     * for example {@link #ERROR}.
     * @see jhelp.JHelp#DISCONNECT
     * 
     */
    public int disconnect() {
        try {
            input.close();
            output.close();
            clientSocket.close();
        } catch (IOException ex) {
            System.out.println("Error occured by closing connection");
            return ERROR;
        }
        System.out.println("SERVERDb: disconnected");
        return JHelp.DISCONNECT;
    }

}
