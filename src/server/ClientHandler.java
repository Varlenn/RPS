package server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;


public class ClientHandler implements Runnable {

    private Server server;
    private PrintWriter outMessage;
    private Scanner inMessage;
    private static final String HOST = "localhost";
    private static final int PORT = 6666;
    private Socket clientSocket = null;
    public int num;
    public static int clients_count = 0;
    static Map<String,String> choiceStr = new HashMap<String,String>();


    public ClientHandler(int num, Socket socket, Server server) {
        try {
            clients_count++;
            this.num = num;
            this.server = server;
            this.clientSocket = socket;
            this.outMessage = new PrintWriter(socket.getOutputStream());
            this.inMessage = new Scanner(socket.getInputStream());
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        choiceStr.put("камень", " выбрал камень");
        choiceStr.put("ножницы", " выбрал ножницы");
        choiceStr.put("бумага", " выбрал бумагу");
    }


    @Override
    public void run() {

        try {
            String name = "Игрок " + num;
            while (true) {
                server.sendMessageToAllClients(name + " присоединился");
                break;
            }

            while (true) {
                if (inMessage.hasNext()) {
                    String clientChoice = inMessage.nextLine();

                    if (Server.choiceOne == "") {
                        Server.firstNum = num;
                        Server.choiceOne = clientChoice;
                        Server.firstStr = name + choiceStr.get(clientChoice);
                    }
                    else server.comparison(clientChoice, num);

                    String answer = choiceStr.get(clientChoice);
                    clientChoice = name + answer;

                    if (Server.result != "") {
                        server.sendMessageToAllClients(Server.firstStr);
                        server.sendMessageToAllClients(clientChoice);
                        server.printResult();
                    }
                }
                Thread.sleep(100);
            }
        }
        catch (InterruptedException ex) {
            ex.printStackTrace();
        }
        finally {
            this.close();
        }
    }

    public void sendMsg(String msg) {
        try {
            outMessage.println(msg);
            outMessage.flush();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void close() {
        server.removeClient(this);
        clients_count--;
        server.sendMessageToAllClients("Игрок" + num + " вышел");
    }
}
