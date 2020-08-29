package Engine;

import Engine.IO.Input;
import Engine.Renderer.VRenderer;
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
        VRenderer renderer = new VRenderer();
        VRenderer.init();
    }



    public void gameLoop() {
        while(VRenderer.running()){
            update();
            render();
        }
    }

    public void cleanUp()
    {
        VRenderer.cleanUp();
    }

    public void update() {
        Input.update();
        VRenderer.update();
        game.update();
    }

    public void render() {
    }

    public static void main(String[] args) {
        new Piston();
    }
}
