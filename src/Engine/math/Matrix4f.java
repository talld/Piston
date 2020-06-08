package Engine.math;

public class Matrix4f {

    private float[][] m = new float[4][4];

    public Matrix4f(){

    }

    public Matrix4f initIdentity(){
        m[0][0] = 1;    m[0][1] = 0; m[0][2] = 0; m[0][3] = 0;
        m[1][0] = 0;    m[1][1] = 1; m[1][2] = 0; m[1][3] = 0;
        m[2][0] = 0;    m[2][1] = 0; m[2][2] = 1; m[2][3] = 0;
        m[3][0] = 0;    m[3][1] = 0; m[3][2] = 0; m[3][3] = 1;

        return this;
    }

    public Matrix4f mul(Matrix4f b){

        Matrix4f res = new Matrix4f();

        for (int i = 0; i<4; i++){

            for (int j = 0; j<4; j++){
                b.setAt(i,j,m[i][0] * b.getAt(0,j) +
                                m[i][1] * b.getAt(1,j) +
                                m[i][2] * b.getAt(2,j) +
                                m[i][3] * b.getAt(3,j)
                );
            }

        }

        return res;
    }

    public float[][] getM() {
        return m;
    }

    public float getAt(int x, int y){
        return m[x][y];
    }

    public void setM(float[][] m) {
        this.m = m;
    }

    public void setAt(int x, int y,float val){
        this.m[x][y] = val;
    }

}
