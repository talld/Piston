package Engine;

import Engine.IO.Input;
import Engine.Renderer.RenderUtil;
import Engine.Renderer.Window;
import Game.Game;
import org.lwjgl.vulkan.VK10;

import static Engine.Renderer.RenderUtil.VKInit;
import static Engine.Renderer.RenderUtil.initRender;

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
        VKInit();
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

        VK10.vkDestroyInstance(RenderUtil.getInstance(),null);

        Window.dispose();
    }

    public void update() {
        //Input.update();
        //game.update();
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
