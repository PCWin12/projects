import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Iterator;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class SuperBlock {
	public int lastblock;
	ArrayList<String> version = new ArrayList<>();

	// JSONObject obj;
	DirectoryMap dmap;

	public SuperBlock(byte[] block) {
		try {
			String s = new String(block, "UTF-8").trim();

			JSONParser parser = new JSONParser();
		//	Main.print(s);
			JSONObject obj = (JSONObject) parser.parse(s);

			String lb = (String) obj.get("lastblock");
			lastblock = Integer.parseInt(lb);
			JSONArray arr = (JSONArray) obj.get("version");
			Iterator<String> iterator = arr.iterator();
			while (iterator.hasNext()) {
				version.add(iterator.next());
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	void changedr(String dr){
		dmap.changeDirectory(dr);
	}
	void listfile(){
		dmap.listFiles();
	}

	void printVersions() {
		for (String s : version) {
			String arr[] = s.split(" ");
			System.out.println("Version No. : " + arr[0]);

		}
	}

	int getLatestVerison() {
		return Integer.parseInt(version.get(version.size() - 1).split(" ")[0]);
	}

	void rootBlock(int ver, RandomAccessFile file, String name, String path_in,
			int blk) throws Exception {

		for (String s : version) {
			String arr[] = s.split(" ");
			if (Integer.parseInt(arr[0]) == ver) {
				byte b[] = new byte[1024];
				int bn = Integer.parseInt(arr[1]);
				file.seek(bn * 1024);
				file.read(b);

				if (new Directory(b).getFile(name, file, path_in, blk)) {
					// updatesuper.

				}
				;
			}
		}

		String arr[] = version.get(version.size() - 1).split(" ");
		byte b[] = new byte[1024];
		int bn = Integer.parseInt(arr[1]);
		file.seek(bn * 1024 );
		file.read(b);
		if (new Directory(b).getFile(name, file, path_in, blk)) {
			// updatesuper.
		}
		;

	}

	public SuperBlock writeCheckPoint() {
		// TODO Auto-generated method stub

		int ver = getLatestVerison();
		version.add((ver + 1) + " " + (dmap.s_blk) + "#" + dmap.e_blk);
		lastblock = FileSystem.lastblock;
		JSONObject obj = new JSONObject();
		JSONArray jar = new JSONArray();
		for (String str : version) {
			jar.add(str);
		}
		RandomAccessFile file = FileSystem.getFile();
		obj.put("version", jar);
		obj.put("lastblock", ""+lastblock);
		try {
			file.seek(0);
			file.write(obj.toJSONString().getBytes());

			file.close();
			file = FileSystem.getFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return this;
	}

	void loadDMap(int ver, RandomAccessFile file) throws IOException,
			ParseException {
		FileSystem.currendir = "/";
		for (String s : version) {
			String arr[] = s.split(" ");
			if (Integer.parseInt(arr[0]) == ver) {
				
				int s_bn = Integer.parseInt(arr[1].split("#")[0]);

				int e_bn = Integer.parseInt(arr[1].split("#")[1]);
				dmap = new DirectoryMap(s_bn, e_bn, file);
				return;
			}
		}

		System.out.print("Invalid Version Number\n");
	}

	void write(int ver, String name, String path, int blk, RandomAccessFile file) {

		dmap.write(ver, name, path, blk, file);

	}

	void read(int ver, String name, String path, int blk, RandomAccessFile file) {

		dmap.read(ver, name, path, blk, file);

	}
void copyfs(int ver, String outputdir, RandomAccessFile file) throws IOException, ParseException{
	DirectoryMap cmap ;
	FileSystem.currendir = "/";
	for (String s : version) {
		String arr[] = s.split(" ");
		if (Integer.parseInt(arr[0]) == ver) {
			
			int s_bn = Integer.parseInt(arr[1].split("#")[0]);

			int e_bn = Integer.parseInt(arr[1].split("#")[1]);
			cmap = new DirectoryMap(s_bn, e_bn, file);
			cmap.copyfs(outputdir , file);		}
	}	
}
	
	void listVersions() {
		for(String str: version){
			System.out.println(str.split(" ")[0]);
			
			
		}
	}
	
	void deleteFile(String name, RandomAccessFile file){
		dmap.deleteFile(file, name);
	}



}
