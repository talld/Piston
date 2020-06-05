package Networking.Sever;

import java.io.*;
import java.net.Socket;

public class SeverThread extends Thread {

    private Socket soc;

    public SeverThread(Socket soc) {
        this.soc = soc;
    }

    public void run(){
        System.out.println("new sever");
        try {
            //connection setup
            BufferedReader in = new BufferedReader(new InputStreamReader(soc.getInputStream()));
            PrintWriter out = new PrintWriter(soc.getOutputStream(),true);
            String line;
            //connection loop
            while (true){
                line = in.readLine();
                try {
                    if(line != null){
                        //if data in
                    }
            }catch (Exception e)
                {

                }
            }
            //end
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
