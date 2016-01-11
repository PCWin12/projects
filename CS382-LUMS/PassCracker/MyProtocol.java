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
	public 	String _start;
	public 	String _end;
	public 	byte[] _hash = new byte[16];

	public byte[] createPacket(int magic, int id, int cmd, String start,
			String end, byte[] hashval) throws IOException {

		ByteArrayOutputStream ba = new ByteArrayOutputStream(50);
		DataOutputStream out = new DataOutputStream(ba);

		// byte[] data = null;
		out.writeInt(int2unsigned(magic));
		out.writeInt(int2unsigned(id));
		out.writeInt(int2unsigned(cmd));
		out.writeChars(start);
		out.writeChars(end);
		out.write(hashval);
		out.flush();

		return ba.toByteArray();
	}

	public int int2unsigned(int in) {

		return Math.abs(in);

	}
	public void decodePacket(byte[] pack) throws IOException {

		ByteArrayInputStream ba = new ByteArrayInputStream(pack);
		DataInputStream is = new DataInputStream(ba);
		
		// byte[] data = null;
		_magic = is.readInt();
		_id = is.readInt();
		_cmd = is.readInt();
		_start = byte2String(is, 6);
		_end = byte2String(is, 6);
		is.read(_hash , 0 ,16);
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
		System.out.println(_magic+"/"+_id+"/"+_cmd+"/"+_start + "/"+ _end+"/"+_hash);
	}

}