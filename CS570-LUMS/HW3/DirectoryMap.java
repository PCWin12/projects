import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class DirectoryMap {

	int s_blk;
	int e_blk;

	ArrayList<InodeMap> imap = new ArrayList<>();

	// {dir: [/usr/localp.txt#23]}

	public DirectoryMap(int start, int end, RandomAccessFile file)
			throws IOException, ParseException {
		e_blk = end;
		s_blk = start;
		file.seek(start * 1024 - 1);
		byte b[] = new byte[(start - end + 1) * 1024];
		file.read(b);
		String data = new String(b, "UTF-8").trim();
		JSONParser parser = new JSONParser();
		//Main.print(data);
		JSONObject obj = (JSONObject) parser.parse(data);
		JSONArray arr = (JSONArray) obj.get("dir");
		Iterator<String> iterator = arr.iterator();
		while (iterator.hasNext()) {
			String str[] = iterator.next().split("#");
			InodeMap im = new InodeMap(str[0], str[1]);
			imap.add(im);
		}

	}

	InodeMap findInode(String path) {
		for (InodeMap im : imap) {
			if (im.dir.equalsIgnoreCase(FileSystem.currendir + path)) {

				return new InodeMap(im);
			}
		}

		System.out.print("File Not Found\n Creating...\n");
		return null;

	}

	void removeInode(String path) {
		int i = 0;
		for (InodeMap im : imap) {

			if (im.dir.equalsIgnoreCase(FileSystem.currendir + path)) {

				break;
			}
			i++;
		}
		imap.remove(i);

	}

	int getStart() {
		return s_blk;

	}

	int getEnd() {
		return e_blk;
	}

	public void write(int ver, String name, String path, int blk,
			RandomAccessFile file) {
		InodeMap im = findInode(name);
		if (im == null) {
			try {
				if (new Inode().createNewFile(file, path, name, blk)) {
					InodeMap new_im = new InodeMap(FileSystem.currendir+name, ""
							+ FileSystem.lastblock);
					imap.add(new_im);
					updateMap(file);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} else {
			removeInode(name);
			try {
				file.seek((Integer.parseInt(im.inode) * 1024));

				byte b[] = new byte[1024];
				file.read(b);
				Inode temp = new Inode(b);
				if (temp.write(file, path, blk)) {
					InodeMap new_im = new InodeMap(FileSystem.currendir+name, ""
							+ FileSystem.lastblock);
					imap.add(new_im);
					updateMap(file);
				}

			} catch (NumberFormatException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}

	void updateMap(RandomAccessFile file) throws IOException {

		file = FileSystem.getFile();
		JSONObject obj = new JSONObject();
		JSONArray arr = new JSONArray();
		for (InodeMap im : imap) {
			arr.add(im.dir + "#" + im.inode);
		}
		obj.put("dir", arr);
		byte b[] = obj.toJSONString().getBytes();
		int blk_sp = (int) Math.ceil((double) b.length / 1024);
		file.seek((FileSystem.lastblock+1) * 1024 );
		file.write(b);
		file.close();
		e_blk = FileSystem.lastblock + blk_sp;
		s_blk = FileSystem.lastblock + 1;
		FileSystem.lastblock = FileSystem.lastblock + blk_sp;
		file = FileSystem.getFile();

	}

	public void read(int ver, String name, String path, int blk,
			RandomAccessFile file) {
		InodeMap im = findInode(name);
		if (im == null) {

			System.out.print("File Not Found\n");
		} else {
			try {
				file.seek((Integer.parseInt(im.inode) * 1024));

				byte b[] = new byte[1024];
				file.read(b);
				Inode temp = new Inode(b);
				// new Inode()
				temp.read(file, path, blk);

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}

	void listFiles() {
		Set<String> set  = new HashSet<String>();
		for (InodeMap im : imap) {
			if (im.dir.startsWith(FileSystem.currendir)) {
				String str = im.dir
						.substring(FileSystem.currendir.length());
				set.add(str.split("/")[0]);
			}

		}
		Main.print("\n");
		for(String s:set){
			System.out.println(s);
		}
		
		Main.print("\n");
	}

	void changeDirectory(String dir) {
		if(dir.equals("..")){
			
			String c[] = FileSystem.currendir.split("/");
			if(c.length<1){
				Main.print("No Parent Directory\n");
			}else{
				String ndir = FileSystem.currendir.substring(0,c[c.length-1].length()-1);
				FileSystem.currendir = ndir;
				return;
			}
			
		}else if(dir.equals(".")){
			return;
		}
		
		
		
		for (InodeMap im : imap) {
			if (im.dir.startsWith(FileSystem.currendir + dir)) {
				FileSystem.currendir = FileSystem.currendir + dir + "/";
				System.out.println("Directory changed");
				return;
			}

		}
		System.out.println("Directory Not Found\n");

	}

	void copyfs(String path, RandomAccessFile file) throws IOException{
		for(InodeMap i : imap){
			String d[] = i.dir.split("/");
			int len = d[d.length-1].length();
			String mk_d = path+i.dir.substring(0, i.dir.length()-len-1);
			Main.print(mk_d + "\n");
			File f = new File(mk_d);
			f.mkdirs();
			int bn = Integer.parseInt(i.inode);
			file.seek(1024*bn);
			byte b[] = new byte[1024];
			file.read(b);
			new Inode(b).copy(mk_d+"/"+d[d.length-1], file);
		}
		
	}
	void deleteFile(RandomAccessFile file, String name){
		InodeMap im = findInode(name);
		if(im==null){
			System.out.println("File Not Found in the directory OR File does not exist \n");
		}else{
			removeInode(name);
			try {
				updateMap(file);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	
}
