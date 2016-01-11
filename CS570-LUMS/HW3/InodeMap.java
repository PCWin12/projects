
public class InodeMap {

	public String dir;
	public String inode;
	
 public InodeMap(String d , String i){
	 dir = d;
	 inode = i;
	 
	 
 }
 public InodeMap(InodeMap im){
	 dir = im.dir;
	 inode = im.inode;
	 
	 
 }
}
