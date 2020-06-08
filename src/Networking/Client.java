package Networking;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;

public class Client {

    private static Socket soc;

    public static void main(String[] args) {

        try{
            System.out.println("Client started");
            soc = new Socket();
            soc.setSoTimeout(35);
            InetAddress add = InetAddress.getByName("127.0.0.1");
            SocketAddress SSAdd = new InetSocketAddress(add,9806);
            soc.connect(SSAdd);
            PrintWriter out = new PrintWriter(soc.getOutputStream(),true);
            BufferedReader in = new BufferedReader(new InputStreamReader(soc.getInputStream()));
            String line;
            while(true){
                try {
                    if((line = in.readLine()) != null) {
                        //if data in
                    }
                    //loop
                }catch (Exception e){
                    //e.printStackTrace();
                }
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }
    private static void close() throws IOException {
        soc.close();
    }

    private static void sendPacket(){

    }
}
