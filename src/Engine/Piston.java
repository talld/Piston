package Engine;

import Engine.IO.Input;
import Engine.Renderer.VRenderer;
import Engine.Renderer.Window;
import Game.Game;

import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwTerminate;

public class Piston {

    private Game game = new Game();
    private static Window window;
    public boolean end = false;

    public static long getWindowPointer() {
        return window.getWindow();
    }

    public static Window getWindow() {
        return window;
    }

    public Piston(){
        init();
        Input.init();
        gameLoop();
        cleanUp();
    }

    public void init() {
        glfwInit();
        window = new Window(640,480,"Window");
        window.create();
        VRenderer renderer = new VRenderer();
        VRenderer.init();
    }



    public void gameLoop() {
        while(!window.isCloseRequested()){
            update();
            render();
        }
    }

    public void cleanUp()
    {
        VRenderer.cleanUp();
        window.destroy();
        glfwTerminate();
    }

    public void update() {
        Input.update();
        window.update();
        game.update();
        return;
    }

    public void render() {
    }

    public static void main(String[] args) {
        new Piston();
    }
}
