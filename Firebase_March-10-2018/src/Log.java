package src;

public class Log {
    //************************************************************************
    //*                 d
    //************************************************************************
    protected static synchronized void d(final String tag, final String msg) {
        if (debug)
            System.err.println(tag + msg);
    }
    //************************************************************************
    //*                 i
    //************************************************************************
    protected static synchronized void i(final String msg) {
        if (info)
            System.out.println(msg);
    }
    //************************************************************************
    //*                 declare
    //************************************************************************
    private static final boolean debug = true;
    private static final boolean info = true;
}