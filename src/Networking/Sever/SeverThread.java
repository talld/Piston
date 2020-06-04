package Networking.Sever;

import java.net.Socket;

public class SeverThread extends Thread {

    public SeverThread(Socket accept) {
    }

    public void run(){
        System.out.println("never sever");
    }

}
