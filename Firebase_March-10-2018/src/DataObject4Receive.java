package src;

public class DataObject4Receive {
    protected DataObject4Receive() {
        Log.d(LOG_TAG, "<<constructor>> DataObject4Receive()");
    }
    //************************************************************************
    //*                 setObject
    //************************************************************************
    protected DataObject4Receive setObject(final Object object, final int idx) {
        //Log.d(LOG_TAG, ".setObject()");

        aObject[idx] = object;

        return this;
    }
    //************************************************************************
    //*                 getObject
    //************************************************************************
    protected Object getObject(final int idx) {
        //Log.d(LOG_TAG, ".getObject()");

        return aObject[idx];
    }
    //************************************************************************
    //*                 declare
    //************************************************************************
    private static final String LOG_TAG =
        "*** " + DataObject4Receive.class.getSimpleName();

    private static final int NOF_OBJECT = 3;

    private Object[] aObject = new Object[NOF_OBJECT];
}