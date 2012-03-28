import java.io.File;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.ForkJoinPool;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;


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

   public static BufferedImage blur( BufferedImage srcImage ) {
      int w = srcImage.getWidth();
      int h = srcImage.getHeight();
 
      int[] src = srcImage.getRGB(0, 0, w, h, null, 0, w);
      int[] dst = new int[src.length];

      System.out.println( "Array size = " + src.length );
      System.out.println( "Threshold = " + sThreshold );

      int procs = Runtime.getRuntime().availableProcessors();
      System.out.println( Integer.toString(procs) + " procs are available" );

      ForkBlur fb = new ForkBlur( src, 0, src.length, dst );
      ForkJoinPool pool = new ForkJoinPool();
      
      long startTime = System.currentTimeMillis();
      pool.invoke(fb);
      long endTime = System.currentTimeMillis();

      System.out.println( "Image blue took " + Long.toString(endTime - startTime) + " millis." );
      
      BufferedImage dstImg = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
      dstImg.setRGB(0, 0, w, h, dst, 0, w);

      return dstImg;
   }

   public static void main( String[] args ) throws Exception {
      File imgFile = new File( "red-tulips.jpg" );
      BufferedImage image = ImageIO.read(imgFile);
      
      new ImageFrame( "Forkblur Original", image );
      BufferedImage blurredImg = blur( image );

      new ImageFrame( "Forkblur processed", blurredImg );
   }
}

class ImageFrame extends JFrame {
      public ImageFrame( String title, BufferedImage image ) {
	 super(title);
         setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
         setSize( image.getWidth(), image.getHeight() );
         add( new ImagePanel( image ) );
         setLocationByPlatform(true);
         setVisible( true ); 
      }
}

class ImagePanel extends JPanel {
      BufferedImage mImage;
      
      public ImagePanel( BufferedImage image ) {
	  mImage = image;
      }
 
      protected void paintComponent( Graphics g ) {
	 int x = (getWidth() - mImage.getWidth())/2;
         int y = (getHeight() - mImage.getHeight())/2;

         g.drawImage(mImage, x, y, this); 
      }  
}
