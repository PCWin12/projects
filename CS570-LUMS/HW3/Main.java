import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main {

	FileSystem fs;

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		new Main();

	}

	public Main() throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String vfs;

		while (true) {
			System.out.print("$>");
			vfs = br.readLine();
			String arr[] = vfs.split(" ");
			if (arr[0].equalsIgnoreCase("vfs")) {
				if (arr.length < 2) {
					System.out.print("No filename provided");
					continue;
				} else {
					fs = new FileSystem(arr[1]);
					break;
				}
			} else {
				System.out.print("invalid Command\n$>");
				continue;

			}
		}

		boolean done = false;
		while (!done) {
			Main.print("$>");

			vfs = br.readLine();
			String arr[] = vfs.split(" ");
			if (arr.length < 1) {
				System.out.print("\n$>");
				continue;
			} else if (arr[0].equalsIgnoreCase("dir")) {
				System.out.println("Current Directory: "+fs.currendir);
				Main.print("****Files and Folder in this directory****");
				fs.listfiles();
				//Main.print("$>");
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
				System.out.println("Check Point Created");
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
				if(arr.length ==2){
					fs.deleteFile(arr[1]);
				}else{
					System.out.println("Invalid delete Command\n$>");
				}
				
			} else if (arr[0].equalsIgnoreCase("script")) {
				if (arr.length == 2)
					new ScriptReader().run(arr[1], fs);
				else
					System.out.println("Invalid Script Command\n$>");

			} else if (arr[0].equalsIgnoreCase("exit")) {
				fs.file.close();
				done = true;
			} else {

				System.out.print("Type Again\n");
			}

		}

	}
	
	public static void print(String n){
		System.out.print(n);
	}

}
