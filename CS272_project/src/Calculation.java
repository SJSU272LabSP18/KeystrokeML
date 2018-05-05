public class Calculation {
    private KeyInput key1, key2;
    private long H,DD,UD;
    public Calculation(KeyInput key1, KeyInput key2)
    {
        this.key1=key1;
        this.key2=key2;
        H=key1.getKeyUpTime()-key1.getKeyDownTime();
        DD=key2.getKeyDownTime()-key1.getKeyDownTime();
        UD=key2.getKeyDownTime()-key1.getKeyUpTime();
    }

    public long getH() {
        return H;
    }

    public long getDD() {
        return DD;
    }

    public long getUD() {
        return UD;
    }
}
