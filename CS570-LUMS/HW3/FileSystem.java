import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

public class FileSystem {

	public static int lastblock;
	static String filename;
	int blocksize = 1024;
	BufferedReader reader;
	BufferedWriter buffwriter;
	static RandomAccessFile file;
	private SuperBlock superBlock;
	private int version;
	public static String currendir;
	
	public FileSystem(String name) {
		filename = name;
		File f = new File(filename);
		if (f.exists() && !f.isDirectory()) {
			System.out.println("File System Exists\n Reading...");
			// try {
			// reader = new BufferedReader(new FileReader(filename));
			// FileWriter fw = new FileWriter(filename);
			// buffwriter = new BufferedWriter(fw);
			// } catch (IOException e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// }
			try {
				file = new RandomAccessFile(filename, "rw");
				loadFS();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			try {
				System.out.println("File System Does Not Exist\n Creating...\n");
				f.createNewFile();
				file = new RandomAccessFile(filename, "rw");
				// reader = new BufferedReader(new FileReader(filename));
				// FileWriter fw = new FileWriter(filename);
				// buffwriter = new BufferedWriter(fw);
				initializeFS();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}


	int getBlockstart(int bn) {

		return bn * blocksize;
	}

	@SuppressWarnings({ "unchecked" })
	void initializeFS() {
		try {
			JSONObject obj = new JSONObject();
			JSONArray jar = new JSONArray();
			jar.add("0 1#1");
			obj.put("version", jar);
			obj.put("lastblock", "1");
			version=0;
			file.write(obj.toJSONString().getBytes());
			
			superBlock = new SuperBlock(obj.toJSONString().getBytes());
			// Done super block
			lastblock = 1;
			file.seek(1024);
			// root direc
			JSONObject obj1 = new JSONObject();
			JSONArray ch_dir = new JSONArray();
			obj1.put("dir", ch_dir);
			file.write(obj1.toJSONString().getBytes());
			file.close();
		

			getFile();
			superBlock.loadDMap(version, file);

		} catch (IOException | ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	int write(String name, int bn, String name_input)  {

		
		superBlock.write(version, name, name_input, bn, file);
		return 1;
	}

	int getBlockend(int bn) {
		return bn * blocksize + blocksize - 1;
	}

	public static RandomAccessFile getFile() {
		try {
			file = new RandomAccessFile(filename, "rw");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return file;

	}

	void switchVersion(int ver){
		try {
			superBlock.loadDMap(ver, file);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	void pwd(){
		
		System.out.println(currendir+"");
	}
	
	void listfiles(){
		superBlock.listfile();
	}
	void createCheckPoint(){
		superBlock = superBlock.writeCheckPoint();
		version++;
		System.out.println("Version : "+version);
		
		
		
	}
	void read(String name, String input, int blk){
		superBlock.read(version, name, input, blk, file);
	}
	void cd(String dr){
		superBlock.changedr(dr);
	}
	void loadFS() throws Exception {
		byte sb[] = new byte[1024];
		file.seek(0);
		file.read(sb);
		superBlock = new SuperBlock(sb);
		superBlock.loadDMap(superBlock.getLatestVerison(), file);
		lastblock = superBlock.lastblock;
		version = superBlock.getLatestVerison();
		
		Main.print("\nVersion Loaded:  "+version+"\n");

	}
	void copyfs(int ver, String outputdir){
		try {
			superBlock.copyfs(ver, outputdir, file);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	void deleteFile(String name){
		superBlock.deleteFile(name, file);
	}
}
