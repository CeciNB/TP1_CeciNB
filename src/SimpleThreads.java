/*The following example brings together some of the concepts of this section.
SimpleThreads consists of two threads. The first is the main thread that every Java application has.
The main thread creates a new thread from the Runnable object, MessageLoop, and waits for it to finish.
If the MessageLoop thread takes too long to finish, the main thread interrupts it.*/

/*The MessageLoop thread prints out a series of messages.
If interrupted before it has printed all its messages, the MessageLoop thread prints a message and exits.*/


import java.util.Random;

public class SimpleThreads {

    // Display a message, preceded by
    // the name of the current thread
    static void threadMessage(String message) {
        String threadName = Thread.currentThread().getName();
        System.out.format("%s: %s%n", threadName, message);
    }

    private static class MessageLoop implements Runnable {
        private int numberOfMessages = 0;

        public int getNumberOfMessages() {
            return numberOfMessages;
        }

        public void run() {
            String[] importantInfo = {
                    "Mares eat oats",
                    "Does eat oats",
                    "Little lambs eat ivy",
                    "A kid will eat ivy too"
            };
            try {
                for (int i = 0; i < importantInfo.length; i++) {
                    // Pause for 4 seconds
                    Random r = new Random();
                    int j = r.nextInt(4) + 1;
                    Thread.sleep(1000 * j);
                    // Print a message
                    threadMessage(importantInfo[i]);
                    //
                    numberOfMessages++;
                }
            } catch (InterruptedException e) {
                threadMessage("I wasn't done!");
            }
        }
    }

    public static void main(String args[]) throws InterruptedException {

        // Delay, in milliseconds before
        // we interrupt MessageLoop
        // thread (default one hour).
        long patience = 1000 * 60 * 60;

        // If command line argument
        // present, gives patience
        // in seconds.
        /*if (args.length > 0) { //manual input
            try {
                patience = Long.parseLong(args[0]) * 1000;
            } catch (NumberFormatException e) {
                System.err.println("Argument must be an integer.");
                System.exit(1);
            }
        }*/
        MessageLoop loopForT = new MessageLoop();
        MessageLoop loopForU = new MessageLoop();

        threadMessage("Starting MessageLoop thread");
        long startTime = System.currentTimeMillis();
        Thread t = new Thread(loopForT);
        t.setName("Thread-T");
        t.start();
        Thread u = new Thread(loopForU);
        u.setName("Thread-U");
        u.start();

        threadMessage("Waiting for MessageLoop thread to finish");
        // loop until MessageLoop
        // thread exits
        while (t.isAlive()||u.isAlive()) {
            threadMessage("Still waiting...");
            // Wait maximum of 1 second
            // for MessageLoop thread
            // to finish.
            t.join(1000);
            u.join(1000);
            if ( ( (System.currentTimeMillis() - startTime) > patience) && t.isAlive() ) {
                threadMessage("Tired of waiting!");
                t.interrupt();
                // Shouldn't be long now
                // -- wait indefinitely
                t.join();
            }
            else if ( ( (System.currentTimeMillis() - startTime) > patience) && u.isAlive() ) {
                threadMessage("Tired of waiting!");
                u.interrupt();
                // Shouldn't be long now
                // -- wait indefinitely
                u.join();
            }
            if ( ( (System.currentTimeMillis() - startTime) > 3000) && (t.isAlive() && u.isAlive()) ) {
                int uMessages = loopForU.getNumberOfMessages();
                int tMessages = loopForT.getNumberOfMessages();
                if(uMessages > tMessages){
                    u.interrupt();
                    System.out.println("T is the envy thread");
                    System.out.println("T number: " + tMessages + " U number: " + uMessages);
                }
                else if(uMessages < tMessages){
                    t.interrupt();
                    System.out.println("U is the envy thread");
                    System.out.println("T number: " + tMessages + " U number: " + uMessages);
                }
                else {
                    System.out.println("They are equally fast");
                    System.out.println("T number: " + tMessages + " U number: " + uMessages);
                }
            }
        }
        threadMessage("Finally!");
    }
}