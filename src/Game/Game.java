package Game;

import Engine.IO.Input;
import Engine.Renderer.Renderer;
import static org.lwjgl.glfw.GLFW.*;

public class Game {

    Renderer renderer;

    boolean resize = false;

    public Game(Renderer renderer){
        this.renderer = renderer;
    }
        
    public void input(){

        if(Input.getPressed(GLFW_KEY_E) && !resize){
            renderer.resizeWindow(1024,768);
            resize = false;
        }
        Input.update();
    }

    public void update(){
        input();
    }

    public void render(){

    }

}
