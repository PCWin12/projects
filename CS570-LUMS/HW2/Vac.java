import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

public class Vac {
	static ArrayList<byte[]> hashes;

	static String dir = "venti/dir/";
	static String rootHash;
	int size;
	static String filename = "sample2.txt";
	int dedup = 0;
	int total_blocks=0;

	public static void main(String[] args) throws Exception {
		// System.out.println(args.length);
		if (args.length != 2) {
			System.out
					.println("Invalid Arguments : java Vac <archival_directory> <file_to_arhive> ");
			return;
		}
		dir = args[0];
		if (!dir.endsWith("/")) {
			dir = dir + "/";
		}
		filename = args[1];
		File file = new File(dir);

		if (!file.exists()) {
			file.mkdirs();
		}
		hashes = new ArrayList<byte[]>();
		new Vac().read();
		

	}

	public String calculateHash(byte[] arr) throws NoSuchAlgorithmException {
		MessageDigest md = MessageDigest.getInstance("SHA-1");
		md.update(arr);

		byte byteData[] = md.digest();

		hashes.add(byteData);
		return Base32.encode(byteData);

	}

	public byte[] findHash(byte[] arr) throws Exception {
		MessageDigest md = MessageDigest.getInstance("SHA-1");
		md.update(arr);

		byte byteData[] = md.digest();

		return byteData;

	}

	public void read() throws Exception {
		String fileName = filename;

		try {
			byte[] buffer = new byte[100];

			FileInputStream inputStream = new FileInputStream(fileName);
			int total = 0;
			int nRead = 0;
			while ((nRead = inputStream.read(buffer)) != -1) {
				write(calculateHash(buffer), buffer);
				total += nRead;
			}
			inputStream.close();

			System.out.println("Read " + total + " bytes");
			size = total;
		} catch (FileNotFoundException ex) {
		} catch (IOException ex) {
		}

		saveHashes();
		String final_file = rootHash + "\n" + size + "\n" + dir;
		BufferedWriter writer = new BufferedWriter(new FileWriter(filename
				+ ".vac"));
		writer.write(final_file);
		writer.close();
		System.out.println("Vac done!!\nTotal blocks: "+total_blocks+"\nDeduplication in: "+dedup  + " Blocks \nFile Created  with name: " + filename
				+ ".vac");

	}

	public void saveHashes() throws Exception {

		ArrayList<byte[]> temp = new ArrayList<byte[]>();
		int len = hashes.size();
		System.out.println("Blocks of hashes at current level: "
				+ len);
		if (len == 1) {
			rootHash = Base32.encode(hashes.get(0));

			return;
		}
		int remain = len | 5;
		for (int i = 0; i < len; i = i + 5) {
			byte[] hash_temp = new byte[100];
			int c = 0;
			for (int j = 0; j < 5; j++) {
				if (i + j >= len) {
					// byte[] hash_temp1 = new byte[j*20];
					// for(int l =0 ; l<j*)
					break;

				}
				byte[] h = hashes.get(i + j);
				for (int k = 0; k < 20; k++) {
					hash_temp[c] = h[k];
					c++;
				}
			}
			byte[] byteHash = findHash(hash_temp);
			temp.add(byteHash);
			write(Base32.encode(byteHash), hash_temp);

		}
		// byte[] hash_temp = new byte[100];
		// int c = 0;
		// for (int i = len - remain; i < len; i++) {
		//
		// byte[] h = hashes.get(i);
		// for (int k = 0; k < 20; k++) {
		// hash_temp[c] = h[k];
		// c++;
		//
		// }
		// byte[] byteHash = findHash(hash_temp);
		// temp.add(byteHash);
		// write(Base32.encode(byteHash), hash_temp);
		//
		// }

		hashes.clear();
		hashes.addAll(temp);
		saveHashes();

	}

	public void write(String fileName, byte[] arr) {
		// System.out.println(fileName.getBytes().length);
		total_blocks++;
		File f = new File(dir + fileName);
		if (f.exists() && !f.isDirectory()) {
			// System.out.println(fileName + " : Block Exists");
			dedup++;
			return;
		}
		try {

			FileOutputStream outputStream = new FileOutputStream(dir + fileName);
			outputStream.write(arr);

			outputStream.close();

			// System.out.println("Wrote " + arr.length + " bytes");
		} catch (IOException ex) {
			ex.printStackTrace();
			System.out.println("Error writing file '" + fileName + "'");
		}

	}

}
