import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Unvac {

	static String fileName = "ample.txt.vac";
	String rootHash;
	int size;
	static String dir;
	static ArrayList<String> hashes;

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		if(args.length!=1) {
			System.out.println("Invalid Arguments : java Unvac <file_to_arhive>.vac ");
			return ;
		}else if(!args[0].endsWith(".vac")){
			System.out.println("Not a Vac File");
		}
		fileName = args[0];
		try{
		new Unvac().readFile();
		}catch(Exception e){
			System.out.println("Invalid Hashes or Blocks removed or invalid vac file");
		}
	}

	public void readFile() throws Exception {
		BufferedReader br = new BufferedReader(new FileReader(fileName));

		StringBuilder sb = new StringBuilder();
		rootHash = br.readLine();
		size = Integer.parseInt(br.readLine());
		dir = br.readLine();
		hashes = new ArrayList<String>();
		hashes.add(rootHash);
		
		expandHash();
		System.out.println("File UnVac-ed"+"\nFile Recovered from archive with name: "+fileName.replaceAll(".vac", ""));
	}

	boolean isHash(byte[] arr) {

		for (int i = 0; i < 20; i++) {
			if (arr[i] != 0) {
				return true;
			}
		}
		return false;

	}

	public void expandHash() throws Exception {
		int len = hashes.size();
		System.out.println("Current hashes: "+len);

		if (len * 100 > size) {
			System.out.println("Data Blocks Reached");
			// write to file
			writeToFile();

			return;
		}
		ArrayList<String> temp = new ArrayList<String>();
		for (int i = 0; i < len; i++) {
		//	System.out.println(hashes.get(i));
			byte[] file_hashes = getfileData(hashes.get(i));
			for (int j = 0; j < 5; j++) {

				byte[] temp_hash = new byte[20];
				for (int k = 0; k < 20; k++) {
					temp_hash[k] = file_hashes[j * 20 + k];
				}

				if (isHash(temp_hash)) {
					//System.out.println(Base32.encode(temp_hash));
					temp.add((Base32.encode(temp_hash)));
				}
			}

		}

		hashes.clear();
		hashes.addAll(temp);
		expandHash();

	}

	private void writeToFile() throws Exception {
		// TODO Auto-generated method stub
		byte[] comp_data = new byte[size];
		int count = 0;
		for (String str : hashes) {

			File f = new File(dir + str);
			if (!f.exists() || f.isDirectory()) {
				 System.out.println(fileName + " : Block does not exist");
				return;
			}

			byte[] data = getfileData(str);
			for (int i = 0; i < 100; i++) {
				if (count == size) {
					break;
				}
				comp_data[count] = data[i];
				count++;
			}

		}
		try {

			FileOutputStream outputStream = new FileOutputStream(
					fileName.replaceAll(".vac", ""));
			outputStream.write(comp_data);

			outputStream.close();

		} catch (IOException ex) {
		}

	}

	private byte[] getfileData(String name) throws Exception {
		// TODO Auto-generated method stub

		byte[] buffer = new byte[100];

		FileInputStream inputStream = new FileInputStream(dir+name);
		inputStream.read(buffer);
		inputStream.close();
		return buffer;

	}
}
