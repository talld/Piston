package Engine;

import Engine.IO.Input;
import Game.Game;

public class MainComponent {

    private static double frameCap = 5000.0;
    private boolean isRunning;
    private Game game;

    public MainComponent(){
        isRunning = false;
        game = new Game();
    }

    public void start(){
        if(isRunning){
            return;
        }else {
            run();
        }

    }

    public void stop(){
        if(!isRunning){
            return;
        }else{
            isRunning=false;
        }
    }

    private void run(){

        int frames = 0;
        long frameCounter = 0;
        boolean render = false;
        isRunning=true;
        long lastTime = Timer.getTime();
        long startTime = lastTime;
        long passedTime = startTime-lastTime;
        double unprocessedTime =0d;
        final double frameTime = 1.0/frameCap;
        while(isRunning){

            startTime = Timer.getTime();
            passedTime = startTime-lastTime;
            lastTime = startTime;

            unprocessedTime+=passedTime / (double) Timer.SECOND;
            frameCounter+=passedTime;

            while(unprocessedTime > frameTime){
                render=true;
                unprocessedTime -=frameTime;

                if(Window.isCloseRequested()){
                    stop();
                }

                Timer.setDeltaTime(frameTime);
                Input.update();
                game.input();
                game.update();
                if(frameCounter >= Timer.SECOND){
                    Window.setTile("FPS: " + frames);
                    frames=0;
                    frameCounter = 0;
                }

            }
            if(render) {
                render();
                frames++;
                render = false;
            }else{
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void render(){
        Window.render();
        game.render();
    }

    private void cleanUp(){
        Window.dispose();
    }


    public static void main(String[] args) {
        Window.createWindow(640,480,"3d",0,1);
        Window.setCurrent();
        MainComponent game = new MainComponent();
        game.start();
    }

}
