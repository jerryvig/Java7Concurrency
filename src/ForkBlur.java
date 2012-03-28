import java.util.concurrent.RecursiveAction;

public class ForkBlur extends RecursiveAction {
   private int[] mSource;
   private int mStart;
   private int mLength;
   private int[] mDest;
   private int mBlurWidth = 15;

   protected static int sThreshold = 100000;

    public ForkBlur( int[] src, int start, int length, int[] dst ) {
       mSource = src;
       mStart = start;
       mLength = length;
       mDest = dst;
   }

   protected void computeDirectly() {
      int sidePixels = (mBlurWidth - 1) / 2;
      for (int index = mStart; index < mStart + mLength; index++) {
	  // Calculate average.
	  float rt = 0, gt = 0, bt = 0;
	  for (int mi = -sidePixels; mi <= sidePixels; mi++) {
	      int mindex = Math.min(Math.max(mi + index, 0),
                                    mSource.length - 1);
	      int pixel = mSource[mindex];
	      rt += (float)((pixel & 0x00ff0000) >> 16)
		  / mBlurWidth;
	      gt += (float)((pixel & 0x0000ff00) >>  8)
		  / mBlurWidth;
	      bt += (float)((pixel & 0x000000ff) >>  0)
		  / mBlurWidth;
	  }
          
	  // Re-assemble destination pixel.
	  int dpixel = (0xff000000     ) |
	      (((int)rt) << 16) |
	      (((int)gt) <<  8) |
	      (((int)bt) <<  0);
	  mDest[index] = dpixel;
      }
   }

   public void compute() {
      if ( mLength < sThreshold ) {
	  computeDirectly();
          return; 
      }
 
      int split = mLength/2;
      invokeAll( new ForkBlur( mSource, mStart, split, mDest ), new ForkBlur( mSource, mStart+split, mLength-split, mDest ) );

   }

   public static void main( String[] args ) throws Exception {
      File imgFile = new File( "red-tulips.jpg" );
      BufferedImage image = ImageIO.read(imgFile);
      
      new ImageFrame( "Forkblur Original", image );
      BufferedImage blurredImg = blur( image );

      new ImageFrame( "Forkblur processed", blurredImage );
   }
}