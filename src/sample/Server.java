package sample;

import javafx.concurrent.Task;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;

public class Server extends Task implements TCPConnectionListener{
    private ArrayList<TCPConnection> connections = new ArrayList<>();
    int port;
    boolean start=true;

    public Server(int port){
        this.port=port;

    }

    public Void call(){


        System.out.println("Server running...");
        System.out.println(port);


        try(ServerSocket serverSocket = new ServerSocket(port)){
            while (true){

                try {
                    new TCPConnection(this, serverSocket.accept());
                }catch (IOException e){
                    System.out.println("TCPConnection exception: "+e);
                }
            }
        }
        catch (IOException e){
            throw new RuntimeException(e);
        }
    }


    public int getNclient(){
        int N=0;
        for(TCPConnection o: connections){
            N++;
        }
        return N;
    }

    @Override
    public synchronized void onConnectionReady(TCPConnection tcpConnection) {
        connections.add(tcpConnection);
        System.out.println("client connected "+tcpConnection);
        sendToAll("client connected "+tcpConnection);

    }

    @Override
    public synchronized void onRecieveReady(TCPConnection tcpConnection, String str) {
        //System.out.println(str);
        sendToAll(str);
    }

    @Override
    public synchronized void onDisconect(TCPConnection tcpConnection) {
        connections.remove(tcpConnection);
        sendToAll("client disconnected "+tcpConnection);
    }

    @Override
    public synchronized void onException(TCPConnection tcpConnection, Exception e) {
        System.out.println("TCP connection: "+e);
    }

    private void sendToAll(String str) {

        //System.out.println(str);

            //if(!str.equals("null")) {
            int N = getNclient();
            System.out.println("n= " + N);
            if (N == 1) {
                connections.get(0).sendString("service:you first");
            }

            if (N == 2) {
                connections.get(0).sendString("service:you first");
                connections.get(1).sendString("service:you second");
                start=false;
            }
            if (N > 2) {
                int k = 1;
                for (TCPConnection o : connections) {
                    o.sendString("serviceMU:" + k + ">" + N);
                    k++;
                }
            }




            //System.out.println(str);
            for (TCPConnection connection : connections)
                connection.sendString(str);

        //}
    }
}
