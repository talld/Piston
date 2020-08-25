package Engine;

import Engine.IO.Input;
import Engine.Renderer.RenderUtil;
import Engine.Renderer.Window;
import Game.Game;
import org.lwjgl.vulkan.VK10;

import static Engine.Renderer.RenderUtil.*;
import static org.lwjgl.vulkan.KHRSurface.vkDestroySurfaceKHR;

public class Boot {

    private Game game = new Game();
    public boolean end = false;

    public Boot(){
        init();
        gameLoop();
        cleanUp();
    }

    public void init() {
        //init
        Window.glfwPreInit();
        VkInit();
        VkInitDebug();
        //-------------
        return;
    }



    public void gameLoop() {
        while(!Window.isCloseRequested()){
            update();
            render();
        }
        return;
    }

    public void cleanUp(){
        vkCleanup();
    }

    public void update() {
        Input.update();
        game.update();
        return;
    }

    public void render() {
        Window.render();
        return;
    }

    public static void main(String[] args) {
        new Boot();
    }
}
