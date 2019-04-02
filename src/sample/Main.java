package sample;

import java.io.IOException;
import java.net.BindException;
import java.util.Scanner;

public class Main {

    static Server server;
    static int p=0;
    static int c=0;


    public static void main(String[] args) {

        Scanner s = new Scanner(System.in);
        if(args.length==2) {
            Server.port= Integer.parseInt(args[0]);
            Server.userCount = Integer.parseInt(args[1]);
            start();
        }else {
            iniPort(s);
            iniUser(s);

            start();

        }
    }

    public static void iniPort(Scanner s){
        s=new Scanner(System.in);
        System.out.print("input server port : ");
        try {
            Server.port = s.nextInt();
            if(Server.port>65536){
                System.out.println("ЕБЛАН ЧТОЛИ?");
                Server.port=0;
                iniPort(s);
            }
        }catch (Exception e){
            System.out.println();
            System.out.println("input number, not letters");
            iniPort(s);
        }
    }
    public static void iniUser(Scanner s){
        s=new Scanner(System.in);
        System.out.print("input user count : ");
        try {
            Server.userCount = s.nextInt();
        }catch (Exception e){
            System.out.println();
            System.out.println("input number, not letters");
            iniUser(s);
        }
    }

    public static void commandRead(){
        Scanner s = new Scanner(System.in);
        String userInput;
        while (true) {
            userInput = s.nextLine();
            System.out.println(userInput);
            boolean command =false;

            if(userInput.equals("stop")){
                if(server!=null)
                    stop();
                else
                    System.out.println("Server has already been shut down");

                command=true;
            }
            if(userInput.equals("start")){
                if(Server.port!=0&&Server.userCount!=0) {
                    if (server == null)
                        start(p, c);
                    else
                        System.out.println("Server is already on");
                }else {
                    iniPort(s);
                    iniUser(s);
                    start();
                }

                command=true;
            }
            if(userInput.equals("status")){
                getRam();
                command = true;
            }
            if(userInput.equals("restart")){
                if(server!=null) {
                    restart();
                }else {
                    if(Server.port==0&&Server.userCount==0){
                        iniPort(s);
                        iniUser(s);
                        start();
                    }else{
                        start(p,c);
                    }
                }
                command=true;
            }
            if(userInput.equals("restart null")){
                if(server!=null) {
                    stop();
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    iniPort(s);
                    iniUser(s);
                    start();

                }else {
                    iniPort(s);
                    iniUser(s);
                    start();
                }
                command = true;
            }
            if(userInput.equals("help")){
                help();
                command=true;
            }
            if(userInput.contains("port")) {
                if(server==null) {
                    if(userInput.equals("port")) {
                        iniPort(s);
                    }else {
                        try {
                            //System.out.println(">"+userInput.substring(5,userInput.length())+"<");
                            Server.port=Integer.parseInt(userInput.substring(5,userInput.length()));
                            if(Server.port>65536){
                                System.out.println("КОНч?");
                                Server.port=0;
                            }
                        }catch (Exception e){
                            System.out.println("Совсем дэб?");
                        }
                    }

                    p = Server.port;
                }
                else
                    System.out.println("server RUNNING!");

                command=true;
            }
            if(userInput.contains("user")) {



                if(server==null) {
                    if(userInput.equals("user")) {
                        iniUser(s);
                    }else {
                        try {
                            //System.out.println(">"+userInput.substring(5,userInput.length())+"<");
                            Server.userCount=Integer.parseInt(userInput.substring(5,userInput.length()));
                        }catch (Exception e){
                            System.out.println("Совсем дэб?");
                        }
                    }
                    c=Server.userCount;

                }
                else
                    System.out.println("server RUNNING!");

                command=true;
            }
            if(userInput.equals("null")){
                if(server!=null) {
                    Null();
                }else {
                    Server.port=0;
                    Server.userCount=0;
                }

                command = true;
            }
            if(!command){
                System.out.println("command not found use help");
            }
        }
    }

    public static void start(){

        server = new Server();
        server.setDaemon(true);
        server.start();
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if(server.error){
            System.out.println("Server start error");
            Null();
        }

        commandRead();
    }
    public static void start(int p,int c){
        try {
            Server.port = p;
            Server.userCount = c;
            server = new Server();
            server.setDaemon(true);
            server.start();
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if(server.error){
                System.out.println("Server start error");
                Null();
            }
        }catch (RuntimeException e){
            System.out.println("it seems the port "+Server.port+" is busy");
        }

    }
    public static void getRam(){
        int mb = 1024 * 1024;
        Runtime instance = Runtime.getRuntime();
        System.out.println("***** Heap utilization statistics [MB] *****\n");
        System.out.println("Total Memory: " + instance.totalMemory() / mb);
        //System.out.println("Free Memory: " + instance.freeMemory() / mb);
        System.out.println("Used Memory: "
                + (instance.totalMemory() - instance.freeMemory()) / mb);
        if(server!=null) {
            System.out.println("Port: "+Server.port);
            System.out.println("User count: " + Server.userCount+"/"+server.getNclient());
            System.out.println("Server run?: " + !server.stop);
        }else {
            System.out.println("Port: 0");
            System.out.println("User count: 0/0");
            System.out.println("Server run?: " + false);
        }
    }
    public static void stop(){

        server.stop=true;
        server.userCountAccept=true;
        if (server.connections.size() != 0) {
            for (TCPConnection t : server.connections)
                t.disconnect();
            server.connections.clear();
        }

        System.out.println(server.serverSocket.isClosed());
        if(!server.serverSocket.isClosed()) {
            try {
                server.serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        p=Server.port;
        c=Server.userCount;
        System.out.println(">"+server.getNclient());
        server=null;
        System.gc();

    }
    public static void restart(){
        stop();
        start(p,c);
    }
    public static void help(){
        System.out.println("======HELP======");
        System.out.println("start");
        System.out.println("stop");
        System.out.println("status");
        System.out.println("restart");
        System.out.println("restart null");
        System.out.println("port");
        System.out.println("user");
        System.out.println("null");

    }
    public static void Null(){

        /*server.stop=true;
        if(server.connections.size()!=0) {
            for (TCPConnection t : server.connections)
                t.disconnect();
        }

        server.connections.clear();
        if(!server.serverSocket.isClosed()) {
            try {
                server.serverSocket.close();
                server.stop = true;
            } catch (Exception e) {

            }
        }*/
        stop();

        Server.port=0;
        Server.userCount=0;
        server=null;
        System.gc();
    }
}
