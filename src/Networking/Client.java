package Networking;

import java.net.*;

public class Client {

    public static void main(String[] args) {

        try{
            System.out.println("Client started");
            Socket soc = new Socket();
            InetAddress add = InetAddress.getByName("127.0.0.1");
            SocketAddress SSAdd = new InetSocketAddress(add,9806);
            try{
                soc.connect(SSAdd);
                while(true){

                }
            }catch (SocketTimeoutException e){
                System.out.println("connection timed out");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
