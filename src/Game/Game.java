package Game;

import Engine.IO.Input;
import Engine.Renderer.Renderer;
import static org.lwjgl.glfw.GLFW.*;

public class Game {

    private Renderer renderer;

    private boolean resize = false;

    private float x = 0;
    private float y = 0;
    private float z = 0;

    private float xx = 0;
    private float yy = 0;
    private float zz = 0;

    public Game(Renderer renderer){
        this.renderer = renderer;
    }
        
    public void input(){

        if(Input.getPressed(GLFW_KEY_E) && !resize){
                Renderer.resizeWindow(1024,768);
            resize = true;
        }

        if(Input.getPressed(GLFW_KEY_D)){
            x-=0.01f;
        }

        if(Input.getPressed(GLFW_KEY_A)){
            x+=0.01f;
        }

        if(Input.getPressed(GLFW_KEY_W)){
            y-=0.01f;
        }

        if(Input.getPressed(GLFW_KEY_S)){
            y+=0.01f;
        }

        if(Input.getPressed(GLFW_KEY_UP)){
            xx+=1f;
        }

        if(Input.getPressed(GLFW_KEY_DOWN)){
            xx-=1f;
        }

        if(Input.getPressed(GLFW_KEY_RIGHT)){
            yy-=1f;
        }

        if(Input.getPressed(GLFW_KEY_LEFT)){
            yy+=1f;
        }

        if(Input.getPressed(GLFW_KEY_Z)){
            zz-=1f;
        }

        if(Input.getPressed(GLFW_KEY_X)){
            zz+=1f;
        }

        Renderer.setCamera(x,y,z,xx,yy,zz);

        x=0f;
        y=0f;
        z=0f;
        xx=0f;
        yy=0f;
        zz=0f;

        Input.update();
    }

    public void update(){
        input();
    }

    public void render(){

    }

}
