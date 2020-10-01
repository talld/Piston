package Engine.Objects.Camera;

import org.joml.Matrix4f;

public class Camera {

    private Matrix4f view;
    private Matrix4f projection;

    public Camera(Matrix4f view, Matrix4f projection){
        this.view = view;
        this.projection = projection;
    }
}
