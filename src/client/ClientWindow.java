package client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.Socket;
import java.util.Scanner;


public class ClientWindow extends JFrame implements ActionListener {

    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 6666;
    private Socket clientSocket;
    private Scanner inMessage;
    private PrintWriter outMessage;

    private static final int WIDTH = 600;
    private static final int HEIGHT = 400;

    public static final JTextField name = new JTextField("");
    public static JTextArea log = new JTextArea(8,40);
    private JButton rock = new JButton(); // кнопка камня
    private JButton paper = new JButton(); // кнопка бумаги
    private JButton scissors = new JButton(); // кнопка ножниц

    public String clientName = "Игрок ";

    public ClientWindow() {
        try {
            clientSocket = new Socket(SERVER_HOST, SERVER_PORT);
            inMessage = new Scanner(clientSocket.getInputStream());
            outMessage = new PrintWriter(clientSocket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

        setSize(WIDTH, HEIGHT);
        setLocationRelativeTo(null);
        setTitle("КНБ");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        rock.setPreferredSize(new Dimension(120, 120));
        rock.setText("Камень");
        rock.setActionCommand("камень");
        paper.setPreferredSize(new Dimension(120, 120));
        scissors.setText("Ножницы");
        scissors.setActionCommand("ножницы");
        paper.setText("Бумага");
        paper.setActionCommand("бумага");
        scissors.setPreferredSize(new Dimension(120, 120));

        setLayout(new FlowLayout(FlowLayout.CENTER, 50, 30));

        add(rock);
        add(scissors);
        add(paper);
        rock.addActionListener(this);
        paper.addActionListener(this);
        scissors.addActionListener(this);

        add(log, BorderLayout.SOUTH);
        add(log, BorderLayout.SOUTH);
        log.setEditable(false);
        JScrollPane logScrollPane = new JScrollPane(log, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        add(logScrollPane, BorderLayout.CENTER);


        // обработчик события нажатия кнопки
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (true) {
                        if (inMessage.hasNext()) {
                            String inMes = inMessage.nextLine();
                            log.append(inMes + "\n");
                        }
                    }
                } catch (Exception e) {
                }
            }
        }).start();

        // обработчик события закрытия окна приложения
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                try {
                    if (!clientName.isEmpty()) {
                        outMessage.println(clientName + " покинул игру");
                    }
                    outMessage.flush();
                    outMessage.close();
                    inMessage.close();
                    clientSocket.close();
                } catch (IOException exc) {

                }
            }
        });
        setVisible(true);
    }

    public void sendMsg(String choice) {
        String msg = choice;
        outMessage.println(msg);
        outMessage.flush();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        clientName = name.getText();
        String choice = e.getActionCommand();
        sendMsg(choice);
    }
}