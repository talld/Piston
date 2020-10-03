package Engine;

import Engine.EngineUtilities.Timer;
import Engine.Geometry.Vertex;
import Engine.IO.Input;
import Engine.Renderer.Renderer;
import Game.Game;
import org.lwjgl.system.MemoryStack;
import org.joml.Vector3f;

public class Piston {

    private Game game;
    public boolean end = false;

    public Piston() throws Throwable {
        try(MemoryStack stack = MemoryStack.stackPush()) {
            init();
            gameLoop();
            cleanUp();
        }
    }

    public void init() {
        Renderer renderer = new Renderer();

        //Geometry
        Vertex.init();

        //Render
        Renderer.init();

        //IO
        Input.init();

        //Game
        game = new Game(renderer);
    }



    public void gameLoop() throws InterruptedException {

        int frames = 0;

        long time = Timer.getTime();
        long startTime = 0l;
        long deltaTime = 0l;

        long passedTime = 0l;

        while(Renderer.running()){

            startTime = Timer.getTime();
            deltaTime = startTime - time;
            time = Timer.getTime();
            passedTime+=deltaTime;
            if(passedTime >=Timer.SECOND){
                Renderer.getWindow().setTitle("FPS: " + frames + " " + passedTime/1000000000 + " ms");
                passedTime = 0;
                frames = 0;
            }else{
                frames++;
            }

            update();
            render();

        }
    }

    public void cleanUp(){
        Renderer.cleanUp();
    }

    public void update() {
        Input.update();
        Renderer.update();
        game.update();
    }

    public void render() {
        Renderer.render();
    }

    public static void main(String[] args) throws Throwable {

        new Piston();
    }
}
