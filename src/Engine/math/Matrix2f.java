package Engine.math;

public class Matrix2f {

    private float[][] m = new float[2][2];

    public Matrix2f initIdentity(){
        m[0][0]=1;  m[0][1] = 0;
        m[1][0]=0;  m[1][1] = 1;
        return this;
    }

    public Matrix2f mul(Matrix2f b){

        Matrix2f res = new Matrix2f();

        for (int i = 0; i<2; i++){

            for (int j = 0; j<2; j++){
                b.setAt(i,j,m[i][0] * b.getAt(0,j) +
                                m[i][1] * b.getAt(1,j)
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
