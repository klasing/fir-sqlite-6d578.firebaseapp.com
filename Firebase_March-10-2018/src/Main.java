package src;

public class Main {
    public static void main(String[] args) {
        Log.d(LOG_TAG, ".main()");

        // create Control instance
        new Control();
    }
    //************************************************************************
    //*                 exit
    //************************************************************************
    protected static void exit(final int exit_code) {
        Log.d(LOG_TAG, ".exit()");

        if (exit_verbose) {
            String verbose = null;
            switch (exit_code) {
                case 0:
                    verbose = "normal exit";
                    break;
                case 1:
                    verbose = "authorize Firebase failed";
                    break;
                case 2:
                    verbose = "authorize Google failed";
                    break;
                case 3:
                    verbose = "database error";
                    break;
                default:
                    verbose = "" + exit_code;
            } // eof switch
            System.out.println("exit_code: " + verbose);
        } else {
            System.out.println("exit_code: " + exit_code);
        }

        System.exit(exit_code);
    }
    //************************************************************************
    //*                 declare
    //************************************************************************
    private static final String LOG_TAG =
        "*** " + Main.class.getSimpleName();

    private static final boolean exit_verbose = true;
}