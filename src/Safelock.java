import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.Random;

public class Safelock {
  static class Friend {
     private final String name;
     private final Lock lock = new ReentrantLock();
 
     public Friend(String name) {
	 this.name = name;
     }

     public String getName() {
	 return this.name;
     }

  }
}
