package Engine;

import Engine.IO.Input;
import Engine.Renderer.RenderUtil;
import Engine.Renderer.Window;
import Game.Game;
import org.lwjgl.vulkan.VK10;

import static Engine.Renderer.RenderUtil.*;

public class Boot {

    private Game game = new Game();
    public boolean end = false;

    public Boot(){
        init();
        postInit();
        gameLoop();
        cleanUp();
    }

    public void init() {
        //init
        VkInit();
        VkInitDebug();
        //-------------
        return;
    }

    public void postInit() {
        //post init
        Window.createWindow(640,480,"title",0,1);


        //----------
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
        Window.dispose();
        VK10.vkDestroyDevice(getLogicDevice(), null);
        VK10.vkDestroyInstance(RenderUtil.getInstance(),null);
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
