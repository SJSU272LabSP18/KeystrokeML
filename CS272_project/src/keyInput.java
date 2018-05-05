import jdk.jshell.spi.ExecutionControl;

import java.awt.event.KeyEvent;
import java.lang.*;
public class KeyInput {
    //set keycode and return String value
    int keyCode;
    public void setKeyCode(int keyCode) {
        this.keyCode = keyCode;
    }
    public String getKeyStr(){
        return KeyEvent.getKeyText(keyCode);
    }

    //Get set keyUpTime
    long keyUpTime;
    public long getKeyUpTime() {
        return keyUpTime;
    }
    public void setKeyUpTime(long keyUpTime) {
        this.keyUpTime = keyUpTime;
    }

    //Get Set keyDownTime
    long keyDownTime;
    public long getKeyDownTime() {
        return keyDownTime;
    }
    public void setKeyDownTime(long keyDownTime) {
        this.keyDownTime = keyDownTime;
    }
}
