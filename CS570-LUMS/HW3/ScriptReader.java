import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;


public class ScriptReader {

	void run(String file, FileSystem fs){
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(file));
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		String vfs;

			try {
				while((vfs = br.readLine())!=null){
				String arr[] = vfs.split(" ");
				if (arr.length < 1) {
					System.out.print("\n$>");
					continue;
				} else if (arr[0].equalsIgnoreCase("dir")) {
					fs.listfiles();
				} else if (arr[0].equals("cd")) {
					if (arr[1] != null) {
						fs.cd(arr[1]);
					}else
					{
						System.out.print("Invalid Directory\n$>");
					}
				} else if (arr[0].equalsIgnoreCase("read")) {
					if (arr.length == 4) {
						fs.read(arr[1], arr[3], Integer.parseInt(arr[2]));
					} else {
						System.out.println("Invalid read command\n$>");
					}

				} else if (arr[0].equalsIgnoreCase("write")) {
					if (arr.length == 4) {
						fs.write(arr[1], Integer.parseInt(arr[2]), arr[3]);
					} else {
						System.out.println("Invalid write command\n$>");
					}
				} else if (arr[0].equalsIgnoreCase("checkpoint")) {
					fs.createCheckPoint();
					System.out.println("Check Point Created\n$>");
				} else if (arr[0].equalsIgnoreCase("switch")) {
					if (arr.length == 2)
						fs.switchVersion(Integer.parseInt(arr[1]));
					else
						System.out.println("Invalid Version Command\n$>");
				} else if (arr[0].equalsIgnoreCase("copyfs")) {
					if (arr.length == 3)
						fs.copyfs(Integer.parseInt(arr[1]), arr[2]);
					else
						System.out.println("Invalid copyfs command");
				} else if (arr[0].equalsIgnoreCase("delete")) {
						// delete
					if(arr.length ==2){
						fs.deleteFile(arr[1]);
					}else{
						System.out.println("Invalid delete Command\n$>");
					}
				} else if (arr[0].equalsIgnoreCase("script")) {
				} else if (arr[0].equalsIgnoreCase("exit")) {
				} else {

					System.out.print("Type Again \n $>");
				}

}
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
	}	

