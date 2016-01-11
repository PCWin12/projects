
class BetterSafe implements State {
	private volatile byte[] value;
	private byte maxval;
	private Object[] locks;

	BetterSafe(byte[] v) {
		value = v;
		maxval = 127;
		int l = v.length;
		locks = new Object[l];
		for (int i = 0; i < l; i++) {
			locks[i] = new Object();
		}
	}

	BetterSafe(byte[] v, byte m) {
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
		synchronized (locks[i]) {
			value[i]--;
		}
		synchronized (locks[j]) {
			value[j]++;
		}
		return true;
	}
}
