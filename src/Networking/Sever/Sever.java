package Networking.Sever;
import java.net.*;
import java.util.ArrayList;
import java.util.concurrent.TimeoutException;

public class Sever {

    private static ServerSocket SS;
    private static int cons=0;
    private static int maxCons = 2;
    private static SocketAddress SSAdd;

    public static void main(String[] args) {

        try{
            System.out.println("Waiting for connections");
            SS = new ServerSocket();
            SS.setSoTimeout(50);
            InetAddress add = InetAddress.getByName("127.0.0.1");
            SSAdd = new InetSocketAddress(add,9806);
            SS.bind(SSAdd);
            System.out.println(SS.isBound());
            System.out.println(SSAdd);
            System.out.println();
            while (true) {

                if(cons<maxCons){
                    try {
                        SeverThread t = new SeverThread(SS.accept());
                        t.start();
                    }catch (SocketTimeoutException e){

                    }

                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

}
