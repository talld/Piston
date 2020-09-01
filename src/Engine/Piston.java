package Engine;

import Engine.Renderer.IO.Input;
import Engine.Renderer.Renderer;
import Game.Game;

public class Piston {

    private Game game = new Game();
    public boolean end = false;

    public Piston(){
        init();
        Input.init();
        gameLoop();
        cleanUp();
    }

    public void init() {
        Renderer renderer = new Renderer();
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
    }

    public static void main(String[] args) {
        new Piston();
    }
}
