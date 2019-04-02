package sample;



import javax.net.ServerSocketFactory;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSession;
import javax.net.SocketFactory;
import javax.net.ssl.SSLSocketFactory;
import java.io.IOException;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server extends Thread implements TCPConnectionListener{
    public ArrayList<TCPConnection> connections = new ArrayList<>();
    static int port=0;
    boolean start=true;
    static int userCount=2;
    boolean userCountAccept=false;
    boolean stop;
    boolean error;
    int user;


    ServerSocket serverSocket;

    @Override
    public void run() throws RuntimeException {


            System.out.println("Server running...");
            System.out.println(port);
            System.out.println(userCount);



            try {


                serverSocket = new ServerSocket(port);


                //while (!userCountAccept) {
                while (!userCountAccept) {
                    try {

                        if(!userCountAccept) {
                            user++;
                            System.out.println("new "+user);
                            new TCPConnection(this, serverSocket.accept());
                            if (user == userCount) {
                                System.out.println("ало закрываемся");
                                userCountAccept = true;

                                //serverSocket.close();
                            }
                       }
                    } catch (IOException e) {
                        System.out.println("TCPConnection exception: " + e);
                    }

                    while (userCountAccept) {
                        Thread.sleep(200);
                        System.out.println(connections.size());
                        if(connections.size()<user)
                            userCountAccept=false;

                    }
                }

            } catch (BindException e){
                System.out.println("it seems the port "+Server.port+" is busy");
                System.out.println("Restart server with other port");
                error=true;

            } catch (IOException e) {
                throw new RuntimeException(e);

            } catch (IllegalArgumentException e){
                error=true;
                System.out.println("Non-numeric port number");
                System.out.println("Restart server with other port");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }


    }


    public int getNclient(){
        return connections.size();
    }

    @Override
    public synchronized void onConnectionReady(TCPConnection tcpConnection) {
        connections.add(tcpConnection);
        //System.out.println("client connected "+tcpConnection+" "+connections.size());

        sendToAll("client connected "+tcpConnection);

    }

    @Override
    public synchronized void onRecieveReady(TCPConnection tcpConnection, String str) {
        //System.out.println(str);
        sendToAll(str);
    }

    @Override
    public synchronized void onDisconect(TCPConnection tcpConnection) {

        if(!stop) {
            System.out.println("dsads");
            connections.remove(tcpConnection);
        }
        sendToAll("client disconnected "+tcpConnection);
    }

    @Override
    public synchronized void onException(TCPConnection tcpConnection, Exception e) {
        System.out.println("TCP connection: "+e);
    }

    private void sendToAll(String str) {

        //System.out.println(str);

            if(!str.equals("null")) {
            int N = getNclient();
        

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

        }
    }

    @Override
    protected void finalize() {
        System.out.println("kill");
    }
}
