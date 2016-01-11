import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;

public class MyProtocol implements Serializable{
	public int _magic;
	public 	int _id;
	public  int _cmd;
	public 	int _fd ;
	public 	int _mode;
	public int _len;
	
	//public 	byte[] _content = new byte[16*1024];
	String cn;

	public byte[] createPacket(int magic, int id, int cmd,int fd ,int mode,int len,
			 String content) throws IOException {

		ByteArrayOutputStream ba = new ByteArrayOutputStream(16*1024 + 36);
		DataOutputStream out = new DataOutputStream(ba);

		// byte[] data = null;
		out.writeInt(int2unsigned(magic));
		out.writeInt(int2unsigned(id));
		out.writeInt(int2unsigned(cmd));
		out.writeInt(int2unsigned(fd));
		out.writeInt(int2unsigned(mode));
		out.writeInt(int2unsigned(len));

		out.writeChars(content);
		out.flush();

		return ba.toByteArray();
	}
	public byte[] createPacket() throws IOException {

		ByteArrayOutputStream ba = new ByteArrayOutputStream(16*1024 + 36);
		DataOutputStream out = new DataOutputStream(ba);

		// byte[] data = null;
		out.writeInt(int2unsigned(this._magic));
		out.writeInt(int2unsigned(this._id));
		out.writeInt(int2unsigned(this._cmd));
		out.writeInt(int2unsigned(this._fd));
		out.writeInt(int2unsigned(this._mode));
		out.writeInt(int2unsigned(this._len));

		out.writeChars(cn);
		out.flush();

		return ba.toByteArray();
	}
	public int int2unsigned(int in) {

		return in;

	}
	public void decodePacket(byte[] pack) throws IOException {

		ByteArrayInputStream ba = new ByteArrayInputStream(pack);
		DataInputStream is = new DataInputStream(ba);
		
		// byte[] data = null;
		_magic = is.readInt();
		_id = is.readInt();
		_cmd = is.readInt();
		_fd = is.readInt();
		_mode = is.readInt();
		_len = is.readInt();
		
		
	cn=	byte2String(is, 16*1024);
//		is.read(_content , 0 ,16*1024);
		/*byte[] temp = null;
		is.read(temp,0,6);
		_start = String.valueOf(temp);
		byte[] temp2 = null;
		is.read(temp2,0,6);
		_end = String.valueOf(temp2);
		byte[] temp3 = null;
		is.read(temp3,0,24);
		_hash = String.valueOf(temp3);
		
	*/
	}
	
	public String byte2String(DataInputStream is , int len){
		char[] chr = new char[len];
		for(int i=0 ; i<len;i++){
			try {
				chr[i] =is.readChar();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
			}
			
		}
		return String.valueOf(chr);
		
	}
	
	public void print(){
		System.out.println("/"+_id+"/"+_cmd+"/"+_fd + "/"+ _mode+"/"+cn);
	}

}