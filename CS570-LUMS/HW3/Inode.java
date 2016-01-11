import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class Inode {

	String file_name;
	ArrayList<Integer> blocks = new ArrayList<Integer>();

	public Inode(byte block[]) {

		try {
			String s = new String(block, "UTF-8").trim();
			//Main.print(s);
			JSONParser parser = new JSONParser();

			JSONObject obj = (JSONObject) parser.parse(s);

			String lb = (String) obj.get("filename");
			file_name = lb;
			JSONArray arr = (JSONArray) obj.get("blocks");
			Iterator<String> iterator = arr.iterator();
			while (iterator.hasNext()) {
				blocks.add(Integer.parseInt(iterator.next()));
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public Inode() {
	}

	boolean write(RandomAccessFile file, String input, int blk)
			throws IOException {

		int bn = FileSystem.lastblock;

		file.seek((bn + 1) * 1024);
		RandomAccessFile inp = new RandomAccessFile(input, "r");

		byte in[] = new byte[1024];
		inp.read(in);

		if (blk - 1 > blocks.size()) {
			System.out.print("Segmentation fault on Block\n");
			return false;
		}
		file.write(in);
		if (blk == blocks.size()) {

		} else {
			blocks.remove(blk);
		}
		blocks.add(blk, bn + 1);

		// blocks.add(FileSystem.lastblock);
		FileSystem.lastblock++;
		file.close();
		file = FileSystem.getFile();

		// create Inode and write on file;

		JSONObject obj = new JSONObject();
		JSONArray jar = new JSONArray();
		for (int i : blocks) {
			jar.add(i + "");
		}
		obj.put("filename", file_name);
		obj.put("blocks", jar);
		file.seek((FileSystem.lastblock + 1) * 1024);
		file.write(obj.toJSONString().getBytes());
		FileSystem.lastblock++;
		file.close();
		file = FileSystem.getFile();

		return true;
	}

	public boolean createNewFile(RandomAccessFile file, String input,
			String name, int blk) throws IOException {

		int bn = FileSystem.lastblock;

		file.seek((bn + 1) * 1024);
		RandomAccessFile inp = new RandomAccessFile(input, "r");

		byte in[] = new byte[1024];
		inp.read(in);
		if (blk - 1 > blocks.size()) {
			System.out.print("Segmentation fault on Block\n");
			return false;
		}
		file.write(in);
		blocks.add(FileSystem.lastblock + 1);
		FileSystem.lastblock++;
		file.close();
		file = FileSystem.getFile();

		// create Inode and write on file;

		JSONObject obj = new JSONObject();
		JSONArray jar = new JSONArray();
		for (int i : blocks) {
			jar.add(i + "");
		}
		String fname = name.split("/")[name.split("/").length - 1];

		obj.put("filename", fname);
		obj.put("blocks", jar);
		file.seek((FileSystem.lastblock + 1) * 1024);
		file.write(obj.toJSONString().getBytes());
		FileSystem.lastblock++;
		file.close();
		file = FileSystem.getFile();
		return true;
	}

	void read(RandomAccessFile file, String input, int blk) throws IOException {
		if (blk > blocks.size() - 1) {
			System.out.print("Block Does Not Exist\n");
			return;
		}
		int bn = blocks.get(blk);
		file.seek(1024 * bn);
		byte b[] = new byte[1024];
		file.read(b);
		RandomAccessFile in = new RandomAccessFile(input, "rw");
		in.write(b);
		in.close();

	}

	void copy(String path, RandomAccessFile file) {
		String str = "";
		Main.print(path+"\n");
		try {
			for (int bn : blocks) {

				file.seek(1024 * bn);

				byte b[] = new byte[1024];
				file.read(b);
				str = str + new String(b, "UTF-8");
			}
		
			RandomAccessFile in = new RandomAccessFile(path, "rw");
			in.write(str.getBytes());
			in.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
