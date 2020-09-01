package Engine.Renderer.math;

public class Vector2f {

    private float x;
    private float y;
    public Vector2f(){
        this.x = 0f;
        this.y = 0f;
    }

    public Vector2f(float x,float y){
        this.x = x;
        this.y = y;
    }

    @Override
    public String toString() {
        return "(" + x + "," + y + ")";
    }

    public float length(){
        return (float)Math.sqrt(x*x+y*y);
    }

    public float dotProduct(Vector2f b){
        return (x * b.getX() + y * b.getY());
    }

    public Vector2f normalize(){
        float length = this.length();

        this.x /=length;
        this.y/=length;

        return this;
    }

    public Vector2f rotate(float angle){
        float rad = (float) Math.toRadians(angle);
        float cos = (float) Math.cos(rad);
        float sin = (float) Math.sin(rad);

        return new Vector2f(x*cos - y*cos , x*sin + y*cos);
    }

    public Vector2f add(Vector2f b){
        return new Vector2f(this.x+b.getX(),this.y+b.getY());
    }

    public Vector2f add(float b){
        return new Vector2f(this.x+b,this.y+b);
    }

    public Vector2f sub(Vector2f b){
        return new Vector2f(this.x-b.getX(),this.y-b.getY());
    }

    public Vector2f sub(float b){
        return new Vector2f(this.x-b,this.y-b);
    }

    public Vector2f mul(Vector2f b){
        return new Vector2f(this.x*b.getX(),this.y*b.getY());
    }

    public Vector2f mul(float b){
        return new Vector2f(this.x*b,this.y*b);
    }

    public Vector2f div(Vector2f b){
        return new Vector2f(this.x/b.getX(),this.y/b.getY());
    }

    public Vector2f div(float b){
        return new Vector2f(this.x/b,this.y/b);
    }

    public boolean equals(Vector2f b) {
        return (x == b.getX() && (y == b.getY()));
    }

    public boolean equals(Vector3f b) {
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

    public float getZ() {
        return 0;
    }

    public void setZ() {
        return;
    }

    public void setY(float y) {
        this.y = y;
    }

    public int compareXTo(Vector2f o) {
        return 0;
    }

    public int compareYTo(Vector2f o) {
        return 0;
    }
}
