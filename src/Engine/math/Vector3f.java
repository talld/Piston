package Engine.math;

public class Vector3f {

    private float x;
    private float y;
    private float z;

    public Vector3f(){
        this.x = 0;
        this.y = 0;
        this.z = 0;
    }

    public Vector3f(float x,float y,float z){
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public String toString() {
        return "(" + x + "," + y + "," + z + ")";
    }

    public float length(){
        return (float)Math.sqrt(x*x+y*y+z*z);
    }

    public float dotProduct(Vector3f b){
        return (this.x * b.getX() + this.y * b.getY() + this.z * b.getZ());
    }

    public Vector3f crossProduct(Vector3f b){
        return new Vector3f(
                (y * b.getZ() - z * b.getY()),
                (z * b.getX() - x*b.getZ()),
                (x*b.getY()-y*b.getX())
        );
    }

    public Vector3f normalize(){
        float length = this.length();

        this.x /=length;
        this.y/=length;
        this.z/=length;

        return this;
    }

    public Vector2f rotate(float angle){

        return null;
    }

    public Vector3f add(Vector3f b){
        return new Vector3f(this.x+b.getX(),this.y+b.getY(),this.z+b.getZ());
    }

    public Vector3f add(float b){
        return new Vector3f(this.x+b,this.y+b,this.z+b);
    }

    public Vector3f sub(Vector3f b){
        return new Vector3f(this.x-b.getX(),this.y-b.getY(),this.z-b.getZ());
    }

    public Vector3f sub(float b){
        return new Vector3f(this.x-b,this.y-b,this.z-b);
    }

    public Vector3f mul(Vector3f b){
        return new Vector3f(this.x*b.getX(),this.y*b.getY(),this.z*b.getZ());
    }

    public Vector3f mul(float b){
        return new Vector3f(this.x*b,this.y*b,this.z*b);
    }

    public Vector3f div(Vector3f b){
        return new Vector3f(this.x/b.getX(),this.y/b.getY(),this.z/b.getZ());
    }

    public Vector3f div(float b){
        return new Vector3f(this.x/b,this.y/b,this.z/b);
    }


    public boolean equals(Vector3f b) {
        return (x == b.getX() && (y == b.getY()) && (z == b.getZ()));
    }

    public boolean equals(Vector2f b) {
        return (x == b.getX() && (y == b.getY()));
    }

    @Override
    public boolean equals(Object obj) {
        return false;
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
}
