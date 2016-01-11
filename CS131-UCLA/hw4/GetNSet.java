
import java.util.concurrent.atomic.AtomicIntegerArray;

public class GetNSet implements State {
	private AtomicIntegerArray value;
	private byte maxval;

	public GetNSet(byte[] v) {
		int l = v.length;
		int temp[] = new int[l];
		for (int i = 0; i < l; i++) {
			temp[i] = (int) v[i];
		}
		value = new AtomicIntegerArray(temp);
		maxval = 127;

	}

	public GetNSet(byte[] v, byte m) {
		int l = v.length;
		int temp[] = new int[l];
		for (int i = 0; i < l; i++) {
			temp[i] = (int) v[i];
		}
		value = new AtomicIntegerArray(temp);
		maxval = m;

	}

	@Override
	public int size() {
		// TODO Auto-generated method stub
		return value.length();
	}

	@Override
	public byte[] current() {
		// TODO Auto-generated method stub
		byte b[] = new byte[value.length()];
		for (int i = 0; i < value.length(); i++) {
			b[i] = (byte) value.get(i);
		}

		return b;
	}

	@Override
	public boolean swap(int i, int j) {
		// TODO Auto-generated method stub
		
		if (value.get(i) <= 0 || value.get(j) >= maxval) {
			return false;
		}
		int temp1 = value.get(i);
		temp1--;
		value.set(i,temp1);
		int temp2 = value.get(j);
		temp2++;
		value.set(j,temp2);
		return true;
	}

}
