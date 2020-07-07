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
        postInit();
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

    public void postInit(){
        Window.createWindow(640,480,"title",0,1);
        VkPostInit();
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
        long[] imageViews = RenderUtil.getImageViews();
        for(int i = 0; i<imageViews.length-1;i++){
            VK10.vkDestroyImageView(getLogicDevice(),imageViews[i],null);
        }
        vkDestroySurfaceKHR(RenderUtil.getInstance(),Window.getSurface(),null);
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
