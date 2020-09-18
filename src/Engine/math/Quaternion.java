package Engine.math;

public class Quaternion {

    private float x;
    private float y;
    private float z;
    private float w;

    public Quaternion(float x,float y,float z,float w){

        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    public float length(){

        return (float) Math.sqrt(this.x*this.x+this.y*this.y+this.z*this.z+this.w*this.w);
    }

    public Quaternion normalise(){
        float length = this.length();

        this.x/=length;
        this.y/=length;
        this.z/=length;
        this.w/=length;

        return this;
    }

    public Quaternion conjugate(){
        return new Quaternion(-this.x,-this.y,-this.z,this.w);
    }

    public Quaternion mul(Quaternion b){
        return new Quaternion(
                x * b.getW() + w * b.getX() + y * b.getZ() - z * b.getY(),
                y * b.getW() + w * b.getY() + z * b.getX() - x * b.getZ(),
                z * b.getW() + w * b.getZ() + x * b.getY() - y * b.getX(),
                w * b.getW() - x * b.getX() - y * b.getY() - z * b.getZ()
        );
    }

    public Quaternion mul(Vector3f b){
        return new Quaternion(
        w * b.getX() + y * b.getZ() - z * b.getY(),
        w * b.getY() + z * b.getX() - x * b.getZ(),
        w * b.getZ() + x * b.getY() - y* b.getX(),
        -x * b.getX() - y * b.getY() - z * b.getZ());
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getZ() {
        return z;
    }

    public void setZ(float z) {
        this.z = z;
    }

    public float getW() {
        return w;
    }

    public void setW(float w) {
        this.w = w;
    }
}
