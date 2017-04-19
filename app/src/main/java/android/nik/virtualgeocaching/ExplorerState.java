package android.nik.virtualgeocaching;

/**
 * Created by Zsu on 2017. 04. 19..
 */

public class ExplorerState {

    private static ExplorerState explorerState;

    public static ExplorerState GetExplorerStateInstance()
    {
        if(explorerState==null)
            explorerState = new ExplorerState();
        return explorerState;
    }

    public boolean isButtonTouched() {
        return buttonTouched;
    }

    public void setButtonTouched(boolean buttonTouched) {
        this.buttonTouched = buttonTouched;
    }

    private boolean buttonTouched;

}
