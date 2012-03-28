import java.util.concurrent.RecursiveAction;

public class ForkBlur extends RecursiveAction {
   private int[] mSource;
   private int mStart;
   private int mLength;
   private int[] mDest;
   private int mBlurWidth = 15;

    public void ForkBlur( int[] src, int start, int length, int[] dst ) {
       mSource = src;
       mStart = start;
       mLength = length;
       mDest = dst;
   } 
}