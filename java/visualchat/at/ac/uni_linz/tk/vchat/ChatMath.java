package at.ac.uni_linz.tk.vchat;

public class ChatMath {

	private static final int ENTRY_COUNT = 3600;
	private static final int FACTOR = ENTRY_COUNT / 360;

    private static double sinTable[] = new double[ENTRY_COUNT];
    private static double cosTable[] = new double[ENTRY_COUNT];

    public static final double PI_RAD = Math.PI / 180;

    static {
        for (int i = 0; i < ENTRY_COUNT; i++) {
            sinTable[i] = Double.MAX_VALUE;
            cosTable[i] = Double.MAX_VALUE;
        }
    }

    public static double getSin(double _angle) {
		int index = (int)(_angle * FACTOR);
        if (index < 0 || index >= ENTRY_COUNT) {
            index = (index + ENTRY_COUNT) % ENTRY_COUNT;
        }
        if (sinTable[index] == Double.MAX_VALUE) {
            sinTable[index] = Math.sin((double)_angle * PI_RAD);
        }
        return sinTable[index];
    }

    public static double getCos(double _angle) {
		int index = (int)(_angle * FACTOR);
        if (index < 0 || index >= ENTRY_COUNT) {
            index = (index + ENTRY_COUNT) % ENTRY_COUNT;
        }
        if (cosTable[index] == Double.MAX_VALUE) {
            cosTable[index] = Math.cos((double)_angle * PI_RAD);
        }
        return cosTable[index];
    }
}
