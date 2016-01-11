import java.util.Arrays;

public class PassCeacker {

	private char[] chars; // Character Set
	private char[] cend; // Current Guess

	public PassCeacker(char[] chSet, char[] gth) {
		chars = chSet;
		cend = gth;
	//	Arrays.fill(cg, cs[0]);
		//Arrays.binarySearch(cs, cg[5]) + 1;
		//Arrays.bin
	}

	public void increment() {
		int index = cend.length - 1;
		while (index >= 0) {
			if (cend[index] == chars[chars.length - 1]) {
				if (index == 0) {
					cend = new char[cend.length + 1];
					Arrays.fill(cend, chars[0]);
					break;
				} else {
					cend[index] = chars[0];
					index--;
				}
			} else {
			
				cend[index] = chars[search(chars, cend[index]) + 1];
				break;
			}
		}
	}
	
	public int search(char[] a  , char b){
		for (int i=0;i<a.length ; i++){
			if(a[i] == b)
				return i;
		}
		return 0;
		
	}

	@Override
	public String toString() {
		return String.valueOf(cend);
	}
}