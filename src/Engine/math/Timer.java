package Engine.math;

public class Timer {

    public static final long SECOND = 1000000000l;

    private static double deltaTime;

    public static long getTime(){
         return System.nanoTime();
    }

    public static double getDeltaTime(){
        return deltaTime;
    }

    public static void setDeltaTime(double deltaTime){
        Timer.deltaTime = deltaTime;
    }
}
