/*
 * This class is the main class.
 *
 */
package assignment1;

/**
 * This class is for initializing PollProcess class and execute method.
 *
 * @author Qing Ge Phoenix
 */
public class ManagePollTable {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        PollProcess db = new PollProcess(); 
        db.connect();

    }
}
