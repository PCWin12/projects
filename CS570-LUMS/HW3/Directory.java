import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Directory {

	String path;
	ArrayList<String> ch_dir = new ArrayList<>();
	ArrayList<String> files = new ArrayList<>();

	public Directory(byte block[]) throws ParseException {
		try {
			String s = new String(block, "UTF-8");

			JSONParser parser = new JSONParser();

			JSONObject obj = (JSONObject) parser.parse(s);

			String lb = (String) obj.get("path");
			path = lb;
			JSONArray arr = (JSONArray) obj.get("files");
			Iterator<String> iterator = arr.iterator();
			while (iterator.hasNext()) {
				files.add(iterator.next());
			}
			arr = (JSONArray) obj.get("child");
			iterator = arr.iterator();
			while (iterator.hasNext()) {
				ch_dir.add(iterator.next());
			}

		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	boolean getFile(String name, RandomAccessFile file, String input, int blk)
			throws IOException, ParseException {
		for (String s : files) {
			String arr[] = s.split("#");
			if (arr[0].equalsIgnoreCase(name.split("/")[1])) {
				int bn_inode = Integer.parseInt(arr[1]);
				file.seek(bn_inode * 1024 - 1);
				byte block_inode[] = new byte[1024];
				file.read(block_inode);
				if (new Inode(block_inode).write(file, input, blk)) {
					// update the inode

					return true;
				}

			}
		}
		if (name.split("/").length == 2) {
			System.out.print("File not found\nCreating new file...\n$>");
			if (new Inode().createNewFile(file, input, name, blk)) {
				files.add(name+"#" + (FileSystem.lastblock));
				JSONObject obj1 = new JSONObject();
				JSONArray dir = new JSONArray();
				for (String s : ch_dir) {
					dir.add(s);
				}
				JSONArray fil = new JSONArray();
				for (String s : files) {
					fil.add(s);
				}

				obj1.put("path", "/");
				obj1.put("child", dir);
				obj1.put("files", fil);

				file.seek(FileSystem.lastblock * 1024 - 1);
				file.write(obj1.toJSONString().getBytes());
				file.close();
				file = FileSystem.getFile();

				return true;
			}else if(name.split("/").length<2){
				
				System.out.print("Invalid Filename\n$>");
			}
		}else {
			String new_path="",str[];
			str = name.split("/");
			String dir = str[1];
			for(int i=2 ; i<str.length;i++){
				new_path = new_path+"/"+str[i];
				
			}
			for (String s : ch_dir) {
				String arr[] = s.split("#");
				if (arr[0].equalsIgnoreCase(dir)) {
					int bn_inode = Integer.parseInt(arr[1]);
					byte b[] = new byte[1024];
					int bn = Integer.parseInt(arr[1]);
					file.seek(bn * 1024 - 1);
					file.read(b);
					if (new Directory(b).getFile(name, file, input, blk)) {
						// updatesuper.
						ch_dir.add(dir+"#" + (FileSystem.lastblock));
						JSONObject obj1 = new JSONObject();
						JSONArray dir1 = new JSONArray();
						for (String s1 : ch_dir) {
							dir1.add(s1);
						}
						JSONArray fil = new JSONArray();
						for (String ss2 : files) {
							fil.add(ss2);
						}

						obj1.put("path", "/");
						obj1.put("child", dir);
						obj1.put("files", fil);

						file.seek(FileSystem.lastblock * 1024 - 1);
						file.write(obj1.toJSONString().getBytes());
						file.close();
						file = FileSystem.getFile();
						return true;
					}
				
				
			}			
		}
			
		
		
		}
			
			
			
		// files.add(name+"#"+String.valueof(FileSystem.lastblock+1));

		return false;

	}

}
