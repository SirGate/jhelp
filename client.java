package jhelp;



/*
 * author Tulupov Sergei
 */




import java.awt.Color;
import java.awt.Container;
import java.awt.Label;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class client extends JFrame
        implements ActionListener, JHelp {

    private JFrame frame, helpframe;
    private JTextField term;
    public static JTextArea define, help;
    private Label head, head1;
    private JLabel definition;
    private JButton exit, find, save, prev, next, delete;
    private String str2;
    private int iter;
    private Socket clientSocket;
    public static Data dd;
    public Item dd1;
    private String[] str3;
    private ObjectInputStream input;
    private ObjectOutputStream output;
    private boolean isconnected;

    public client() {
        isconnected = false;
        frame = this;
        setTitle("JHelp Client");
        setLocation(150, 100);
        setSize(640, 480);
        setResizable(false);
        setLayout(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        Container cp = getContentPane();
        cp.setBackground(Color.LIGHT_GRAY);
        head = new Label("Definitions:");
        cp.add(head);
        head1 = new Label("Term:");
        cp.add(head1);
        term = new JTextField(30);
        cp.add(term);
        define = new JTextArea();
        cp.add(define);
        define.setLineWrap(true);
        definition = new JLabel();
        cp.add(definition);
        find = new JButton("Find");
        cp.add(find);
        exit = new JButton("Exit");
        cp.add(exit);
        save = new JButton("Save");
        cp.add(save);
        prev = new JButton("Previous");
        cp.add(prev);
        next = new JButton("Next");
        cp.add(next);
        delete = new JButton("Delete");
        cp.add(delete);
        setDesign();
        createMenu();
        setVisible(true);
        helpframe = new JFrame("About programm");
        helpframe.setSize(600, 400);
        helpframe.setLocation(350, 100);
        helpframe.setResizable(false);
        helpframe.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        help = new JTextArea();
        help.setLineWrap(true);
        help.setText("    This program is client part of information-reference "
                + "system JHelp,\n"
                + "intended for searching definition by entered term.\n"
                + "   For connection with server is necessary to enter in point of menu\"Do\" and choose point \"Connect to Server\" .\n"
                + "   Disconnection  with server makes automatically at the exit from programm.\n"
                + "    Term entered in the field \"Term\" You can enter in both registers.\n "
                + "   Definition printed  in the field \"Definitions\".\n"
                + "    After the term entered, for searching push button \"Find\".\n"
                + "    By the buttons \"Previous\" and \"Next\" you could see"
                + " previous and next records \n"
                + "    You could  edit fields \"Term\" and \"Definitions\"."
                + "    To save the record \n push button \"Save\"."
                + "   If record exist in DB,"
                + " then old definition will be deleted from DB , and new definition will be saved."
                + "If record don't exist,\n then it will be added to DB .\n "
                + "  Every definition must ended by symbol \"#\" .\n"
                + "This sumbol is restricted to used in the definitions.\n"
                + "    By button \"Delete\" you could delete record  from DB,"
                + " and , the key field for deletion \n is field \"Term\".");
        helpframe.add(help);
        help.setEditable(false);
    }

    public static void main(String[] args) {
        new client();
    }

    private void setDesign() {
        head.setBounds(10, 70, 250, 28);
        head1.setBounds(30, 35, 50, 20);
        term.setBounds(80, 30, 380, 31);
        define.setBounds(10, 100, 450, 320);
        definition.setBounds(460, 160, 160, 48);
        find.setBounds(500, 33, 100, 25);
        exit.setBounds(500, 380, 100, 25);
        prev.setBounds(500, 210, 100, 25);
        next.setBounds(500, 245, 100, 25);
        save.setBounds(500, 110, 100, 25);
        delete.setBounds(500, 145, 100, 25);
        find.setActionCommand("Find");
        find.addActionListener(new ActionListener() {
            @Override
            public void
                    actionPerformed(ActionEvent ae) {
                String cmd = ae.getActionCommand();
                if (cmd.equals("Find")) {
                    if ((!term.getText().equals("")) & isconnected) {
                        dd1 = new Item(term.getText());
                        dd = new Data(JHelp.SELECT, dd1, JHelp.DEFAULT_VALUES);
                        dd = getData(dd);
                        iter = 0;
                        str2 = "";
                        if (iter >= dd.getValues().length) {
                            define.setText("Searching definition doesn't exist");
                        }
                        while (iter < dd.getValues().length) {
                            if (iter == 0 & dd.getValue(iter).getItem() != "") {
                                str2 = (dd.getValue(iter).getItem()).trim() + "\n";
                                define.setText(str2 + "#" + "\n");
                            } else {
                                str2 = (dd.getValue(iter).getItem()).trim() + "\n";
                                define.append(str2 + "#" + "\n");
                            }
                            iter++;
                        }
                    }
                }
            }
        });
        save.addActionListener(new ActionListener() {
            @Override
            public void
                    actionPerformed(ActionEvent ae) {
                String cmd = ae.getActionCommand();
                if ((cmd.equals("Save"))) {
                    str2 = "";
                    if (!term.getText().equals("") & !define.getText().equals("")
                            & isconnected) {
                        dd1 = new Item(term.getText());
                        str2 = define.getText();
                        str3 = str2.trim().split("#");
                        iter = 0;
                        Item[] val = new Item[str3.length];
                        while (iter < str3.length) {
                            Item dt1 = new Item(str3[iter]);
                            val[iter] = dt1;
                            val[iter].setItem(str3[iter]);
                            iter++;
                        }
                        dd = new Data(JHelp.INSERT, dd1, val);
                        getData(dd);
                    } else {
                        define.setText("Please enter term and definition");
                    }
                }
            }
        });
        prev.addActionListener(new ActionListener() {
            @Override
            public void
                    actionPerformed(ActionEvent ae) {
                String cmd = ae.getActionCommand();
                if ((cmd.equals("Previous"))) {
                    if (!term.getText().equals("") & isconnected) {
                        String str3 = term.getText().trim();
                        dd1 = new Item(term.getText());
                        dd = new Data(JHelp.PREVIOUS, dd1, JHelp.DEFAULT_VALUES);
                        dd = getData(dd);
                        iter = 0;
                        str2 = "";
                        if (iter >= dd.getValues().length) {
                            define.setText("Searching definition doesn't exist");
                        }
                        if (!(str3.equals(dd.getKey().getItem().trim()))
                                & (iter < dd.getValues().length)) {
                            term.setText(dd.getKey().getItem().trim());
                            while (iter < dd.getValues().length) {
                                if (iter == 0 & dd.getValue(iter).getItem() != "") {
                                    str2 = (dd.getValue(iter).getItem()).trim() + "\n";
                                    define.setText(str2 + "#" + "\n");
                                } else {
                                    str2 = (dd.getValue(iter).getItem()).trim() + "\n";
                                    define.append(str2 + "#" + "\n");
                                }
                                iter++;
                            }
                        }
                    }
                }
            }
        });
        next.addActionListener(new ActionListener() {
            @Override
            public void
                    actionPerformed(ActionEvent ae) {
                String cmd = ae.getActionCommand();
                if ((cmd.equals("Next"))) {
                    if (!term.getText().equals("") & isconnected) {
                        dd1 = new Item(term.getText());
                        dd = new Data(JHelp.NEXT, dd1, JHelp.DEFAULT_VALUES);
                        dd = getData(dd);
                        iter = 0;
                        str2 = "";
                        if (iter >= dd.getValues().length) {
                            define.setText("Searching definition doesn't exist");
                        } else {
                            term.setText(dd.getKey().getItem().trim());
                        }
                        while (iter < dd.getValues().length) {
                            if (iter == 0 & dd.getValue(iter).getItem() != "") {
                                str2 = (dd.getValue(iter).getItem()).trim() + "\n";
                                define.setText(str2 + "#" + "\n");
                            } else {
                                str2 = (dd.getValue(iter).getItem()).trim() + "\n";
                                define.append(str2 + "#" + "\n");
                            }
                            iter++;
                        }
                    }
                }
            }
        });
        delete.addActionListener(new ActionListener() {
            @Override
            public void
                    actionPerformed(ActionEvent ae) {
                String cmd = ae.getActionCommand();
                if ((cmd.equals("Delete"))) {
                    if (!term.getText().equals("") & isconnected) {
                        dd1 = new Item(term.getText());
                        dd = new Data(JHelp.DELETE, dd1, JHelp.DEFAULT_VALUES);
                        getData(dd);
                        iter = 0;
                        str2 = "";
                        term.setText("");
                        define.setText("");
                    }
                }
            }
        });
        exit.addActionListener(new ActionListener() {
            @Override
            public void
                    actionPerformed(ActionEvent ae) {
                String cmd = ae.getActionCommand();
                if (cmd.equals("Exit")) {
                    if (isconnected) {
                        disconnect();
                    }
                    frame.dispose();
                }
            }
        });
    }

    private void createMenu() {
        JMenuItem quit = new JMenuItem(
                "Exit");
        quit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(
                    ActionEvent e) {
                if (isconnected) {
                    disconnect();
                }
                frame.dispose();
            }
        });

        JMenuItem conn = new JMenuItem(
                "Connect to Server");
        conn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(
                    ActionEvent e) {
                connect();
            }
        });
        JMenuItem first = new JMenuItem("About programm");
        first.addActionListener(this);
        JMenu file = new JMenu("Do");
        file.add(conn);
        file.addSeparator();
        file.add(quit);
        JMenu view = new JMenu("Help");
        view.add(first);
        JMenuBar bar = new JMenuBar();
        bar.add(file);
        bar.add(view);
        setJMenuBar(bar);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();
        if (cmd.equals("About programm")) {
            helpframe.setVisible(true);
        }
    }

    @Override
    public int connect() {
        try {
            clientSocket = new Socket("localhost", JHelp.DEFAULT_SERVER_PORT);
            output = new ObjectOutputStream(clientSocket.getOutputStream());
            input = new ObjectInputStream(clientSocket.getInputStream());
            isconnected = true;
            define.setText("Client: connected");
        } catch (IOException ex) {
            define.setText("Error : Can't connect to Server");
        }
        return JHelp.OK;
    }

    @Override
    public int connect(String[] args) {
        return JHelp.ERROR;
    }

    /**
     * Method gets data from data source
     *
     * @param data initial object (template)
     * @return new object
     */
    @Override
    public Data getData(Data data) {
        try {
            output.writeObject(data);
            data = (Data) input.readObject();
        } catch (IOException ex) {
            System.out.println("Error : Can't transfer data to/from Server");
        } catch (ClassNotFoundException ex) {
            System.out.println("Error : Can't transfer data to/from Server");

        }
        return data;
    }

    /**
     * Method disconnects client and server
     *
     * @return error code
     */
    public int disconnect() {
        try {
            input.close();
            output.close();
            clientSocket.close();
        } catch (IOException ex) {
            System.out.println("Error by disconnection from Server");
        }
        System.out.println("Client: disconnect");
        return JHelp.ERROR;
    }

}
