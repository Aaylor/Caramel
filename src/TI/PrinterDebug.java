package TI;

public class PrinterDebug {
    public final static boolean DISPLAYCOLOR  = System.console() != null;
    public final static String BLUE  = "\033[0;1;94m";
    public final static String FLUSH = "\033[0m";
    public final static String GREEN = "\033[0;1;92m";

    public static boolean udpDebugPrint = false;
    public static boolean tcpDebugPrint = false;



    public static void printUDP(String msg) {
        if (udpDebugPrint) {
            if (DISPLAYCOLOR) {
                msg = BLUE + msg + FLUSH;
            }

            System.out.println(msg);
        }
    }

    public static void printTCP(String msg) {
        if (tcpDebugPrint) {
            if (DISPLAYCOLOR) {
                msg = GREEN + msg + FLUSH;
            }

            System.out.println(msg);
        }
    }
}
