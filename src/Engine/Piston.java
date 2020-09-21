package Engine;

import Engine.IO.Input;
import Engine.Renderer.Renderer;
import Game.Game;
import org.lwjgl.system.MemoryStack;

public class Piston {

    private Game game;
    public boolean end = false;

    public Piston(){
        try(MemoryStack stack = MemoryStack.stackPush()) {
            init();
            Input.init();
            gameLoop();
            cleanUp();
        }
    }

    public void init() {
        Renderer renderer = new Renderer();
        game = new Game(renderer);
        Renderer.init();
    }



    public void gameLoop() {
        while(Renderer.running()){
            update();
            render();
        }
    }

    public void cleanUp()
    {
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

    public static void main(String[] args) {
        new Piston();
    }
}
