package Engine.EngineUtilities;

public class Timer {

    public static final long SECOND = 1000000000l;

    private static double delta;

    public static long getTime(){
        return System.nanoTime();
    }

    public static double getDelta(){
        return delta;
    }

    public static void setDelta(double delta){
        Timer.delta = delta;
    }

}
