package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;


public class Server {

    static final int PORT = 6666;
    private ArrayList<ClientHandler> clients = new ArrayList<ClientHandler>();

    static String choiceOne = "";
    static int firstNum;
    static String result = "";
    static String firstStr = "";

    public Server() {
        Socket clientSocket = null;
        ServerSocket serverSocket = null;
        try {
            int i = 1;
            serverSocket = new ServerSocket(PORT);
            System.out.println("Server is running...");
            while (true) {
                clientSocket = serverSocket.accept();
                ClientHandler client = new ClientHandler(i++, clientSocket, this);
                clients.add(client);
                new Thread(client).start();
            }
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
        finally {
            try {
                clientSocket.close();
                System.out.println("Server stopped");
                serverSocket.close();
            }
            catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public void sendMessageToAllClients(String msg) {
        for (ClientHandler o : clients) {
            o.sendMsg(msg);
        }
    }

    public void comparison(String choiceTwo, int secondNum) throws InterruptedException {
        if (choiceOne.equals(choiceTwo)) {
            result = "Ничья";
        }
        else if (choiceOne.equals("камень") && choiceTwo.equals("ножницы") || choiceOne.equals("ножницы") && choiceTwo.equals("бумага")
                || choiceOne.equals("бумага") && choiceTwo.equals("камень"))
            result = "Игрок " + firstNum + " победил";
        else
            result = "Игрок " + secondNum + " победил";
        choiceOne = "";
    }

    public void printResult() throws InterruptedException {
        if (result != "")
            sendMessageToAllClients(result);
        result = "";
    }

    public void removeClient(ClientHandler client) {
        clients.remove(client);
    }
}
