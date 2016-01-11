
class BetterSorry implements State {
	private volatile byte[] value;
	private byte maxval;
	private Object[] locks;

	BetterSorry(byte[] v) {
		value = v;
		maxval = 127;
		

	}

	BetterSorry(byte[] v, byte m) {
		value = v;
		maxval = m;
		int l = v.length;
		locks = new Object[l];
		for (int i = 0; i < l; i++) {
			locks[i] = new Object();
		}

	}

	public int size() {
		return value.length;
	}

	public byte[] current() {
		return value;
	}

	public boolean swap(int i, int j) {

		if (value[i] <= 0 || value[j] >= maxval) {
			return false;
		}
		byte temp;
		synchronized (locks[i]) {
			 temp = (byte) (value[i] - 1);
			
		}
		value[i] = temp;
		
		byte temp1;
		synchronized (locks[j]) {
			 temp1= (byte) (value[j] + 1);
			
		}
		value[j] = temp1;
		return true;
	}
}
