import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

public class Parser {

	String grammar = "Terminal,+,-,++,--,(,),$,EOF,0,1,2,3,4,5,6,7,8,9\nS,?,?,E,E,E,?,E,?,E,E,E,E,E,E,E,E,E,E\nE,?,?,P1#L,P1#L,P1#L,?,P1#L,?,P1#L,P1#L,P1#L,P1#L,P1#L,P1#L,P1#L,P1#L,P1#L,P1#L\nL,?,?,P1#L,P1#L,P1#L,?,P1#L,e,P1#L,P1#L,P1#L,P1#L,P1#L,P1#L,P1#L,P1#L,P1#L,P1#L\nP1,?,?,P2#L1,P2#L1,P2#L1,?,P2#L1,?,P2#L1,P2#L1,P2#L1,P2#L1,P2#L1,P2#L1,P2#L1,P2#L1,P2#L1,P2#L1\nL1,B#P2#L1,B#P2#L1,?,?,?,?,?,e,?,?,?,?,?,?,?,?,?,?\nP2,?,?,I#P2,I#P2,P3,?,P3,?,P3,P3,P3,P3,P3,P3,P3,P3,P3,P3\nP3,?,?,?,?,P4#L3,?,P4#L3,?,P4#L3,P4#L3,P4#L3,P4#L3,P4#L3,P4#L3,P4#L3,P4#L3,P4#L3,P4#L3\nL3,?,?,I#L3,I#L3,?,?,?,e,?,?,?,?,?,?,?,?,?,?\nP4,?,?,?,?,P5,?,$#P4,?,P5,P5,P5,P5,P5,P5,P5,P5,P5,P5\nP5,?,?,?,?,(#E#),(#E#),?,?,N,N,N,N,N,N,N,N,N,N\nB,+,-,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?\nI,?,?,++,--,?,?,?,?,?,?,?,?,?,?,?,?,?,?\nN,?,?,?,?,?,?,?,?,0,1,2,3,4,5,6,7,8,9\n";
	String num = "0123456789";
	ArrayList<Token> tokens = new ArrayList<Token>();
	HashMap<String, Production> parseTree = new HashMap<String, Production>();

	public static void main(String[] args) throws Exception {
		Parser p = new Parser();
		// p.run();
		p.init();

		// p.enrollTable();
		// p.parse();
	}


	// ============================================================================
	String token;
	String awk[];
	int pos = -1;
	int line = 0;

	void init() throws Exception {
		// FileReader fr = new FileReader("test.awk");
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		// BufferedReader br = new BufferedReader(fr);
		String s;
		String temp = "";
		while ((s = br.readLine()) != null) {
			// System.out.println(s);
			if (s.indexOf("#") != -1) {
				s = s.substring(0, s.indexOf("#"));

			}
			s=s.replaceAll("\\s+","");
			if (s.length() != 0) {
				// System.out.println(s);
				temp = temp + s + "\n";
			}
		}
		awk = temp.split("\n");

		// Start parsing

		token = nextTok();
		start();
		if (line >= awk.length) {
			System.out.println("Expression Parsed Successfully");

		} else if (pos + 1 >= awk[line].length()) {
			System.out.println("Expression Parsed Successfully");
		} else {
			System.out.println("Error");
			System.exit(0);
		}

	}

	String nextTok() {
		pos = pos + 1;
		if (pos >= awk[line].length()) {
			line++;
			pos = 0;
			if (line >= awk.length) {
				// System.out.println("EOF");
				return "";
			}
		}
		token = awk[line].charAt(pos) + "";
		if (token.equals("+") && pos + 1 < awk[line].length()) {
			if (awk[line].charAt(pos + 1) == '+') {
				token = "++";
				pos++;
			}
		} else if (token.equals("-") && pos + 1 < awk[line].length()) {
			if (awk[line].charAt(pos + 1) == '-') {
				token = "--";
				pos++;
			}
		}
	// System.out.println("Token new : "+token);
	 	//if(token.equals(""))
		return token;

	}

	void start() {
		if (containsString(new String[] { "+", "-", ")" }, token)) {
			System.out.println("Error on Line No. " + (line + 1));
		} else {
			e();
		}
	}

	void e() {
		if (containsString(new String[] { "+", "-", ")" }, token)) {

			System.out.println("Error on Line No. " + (line + 1));
			System.exit(0);
		} else {
			p1();
			l();
		}
	}

	void p1() {
		if (containsString(new String[] { "+", "-", ")" }, token)) {

			System.out.println("Error on Line No. " + (line + 1));
			System.exit(0);
		} else {
			p2();
			l1();
		}

	}

	void l() {
		if (containsString(new String[] { "++", "--", "(", "$", "0", "1", "2",
				"3", "4", "5", "6", "7", "8", "9" }, token)) {
			p1();
			l();
		}
	}

	void p2() {

		if (containsString(new String[] { "(", "$", "0", "1", "2", "3", "4",
				"5", "6", "7", "8", "9" }, token)) {
			p3();
		} else if (containsString(new String[] { "++", "--" }, token)) {
			i();
			p2();
		} else {
			System.out.println("Error on Line No. " + (line + 1));
			System.exit(0);
		}
	}

	void l1() {
		if (containsString(new String[] { "+", "-" }, token)) {
			b();
			p2();
			l1();
		}
	}

	void p3() {
		if (containsString(new String[] { "(", "$", "0", "1", "2", "3", "4",
				"5", "6", "7", "8", "9" }, token)) {
			p4();
			l3();

		} else {
			System.out.println("Error on Line No. " + (line + 1));
			System.exit(0);
		}
	}

	void i() {
		if (containsString(new String[] { "++", "--" }, token)) {
			token = nextTok();

		} else {
			System.out.println("Error on Line No. " + (line + 1));
			System.exit(0);
		}
	}

	void b() {

		if (containsString(new String[] { "+", "-" }, token)) {
			token = nextTok();

		} else {
			System.out.println("Error on Line No. " + (line + 1));
			System.exit(0);
		}
	}

	void p4() {
		if (token.equals("(")) {
			p5();
		} else if (token.equals("$")) {
			token = nextTok();
			//p4();
			e();
		} else if (containsString(new String[] { "0", "1", "2", "3", "4", "5",
				"6", "7", "8", "9" }, token)) {
			p5();
		} else {
			System.out.println("Error on Line No. " + (line + 1));
			System.exit(0);
		}
	}

	void l3() {
		if (containsString(new String[] { "++", "--" }, token)) {
			i();
			l3();
		}
	}

	void p5() {
		if (token.equals("(")) {
			token = nextTok();
			e();
			if (!token.equals(")")) {
				System.out.println("Error on Line No. " + (line + 1));
				System.exit(0);
			}
			token = nextTok();
		} else if (containsString(new String[] { "0", "1", "2", "3", "4", "5",
				"6", "7", "8", "9" }, token)) {
			token = nextTok();

		} else {
			System.out.println("Error on Line No. " + (line + 1));
			System.exit(0);
		}
	}

	boolean containsString(String arr[], String str) {
		for (String temp : arr) {
			if (temp.equals(str)) {
				return true;
			}
		}
		return false;
	}
	// ============================================================================
}

class Production {
	public HashMap<String, String> h = new HashMap<String, String>();
}

class Token {
	public String tk;
	public int typ;

	public Token(String token, int type) {
		tk = token;
		typ = type;
	}
}



// Non-Recursive Implementation 

//	public void parse() throws Exception {
//		ArrayList<String> stack = new ArrayList<String>();
//		FileReader fr = new FileReader("test.awk");
//		BufferedReader br = new BufferedReader(fr);
//		String awk = "";
//		String s;
//		stack.add("S");
//		String currentToken = "", nextToken = "";
//		int j = 1;
//		while ((s = br.readLine()) != null) {
//			if (s.indexOf("#") != -1) {
//				s = s.substring(0, s.indexOf("#"));
//			}
//			int i = 0;
//
//			while (i < s.length()) {
//
//				currentToken = s.charAt(i) + "";
//				if (currentToken.equals(" ")) {
//					i++;
//					continue;
//				}
//				if (currentToken.equals("+") && i + 1 < s.length()) {
//					if (s.charAt(i + 1) == '+') {
//						currentToken = "++";
//						i++;
//					}
//				} else if (currentToken.equals("-") && i + 1 < s.length()) {
//					if (s.charAt(i + 1) == '-') {
//						currentToken = "--";
//						i++;
//					}
//				}
//
//				while (!stack.get(0).equals(currentToken)) {
//					String top = stack.remove(0);
//					// printStack(stack);
//					// System.out.println("							"+top + "  ->  " +
//					// currentToken);
//					Production p = parseTree.get(top.trim());
//					if (p.h.containsKey(currentToken)) {
//						String nextPro[] = p.h.get(currentToken).split("#");
//						for (int k = nextPro.length - 1; k >= 0; k--) {
//							stack.add(0, nextPro[k].trim());
//						}
//					} else {
//						if (p.h.containsKey("EOF")) {
//							// stack.remove(0);
//						} else {
//							System.out.println("Error at Line No. " + j);
//							System.exit(0);
//						}
//					}
//
//					if (stack.size() == 0) {
//						System.out.println("Error at Line No. " + j);
//						System.exit(0);
//					}
//					if (stack.get(0).equals(currentToken)) {
//						stack.remove(0);
//						// System.out.println("token parsed :  " +
//						// currentToken);
//						break;
//					}
//				}
//
//				i++;
//
//				// System.out.println(currentToken);
//			}
//
//			// System.out.println(s);
//			j++;
//		}
//		// Check
//		while (stack.size() != 0) {
//			String top = stack.remove(0);
//			// System.out.println(top);
//			if (!parseTree.containsKey(top)) {
//				System.out.println("Error at Line No. " + j);
//				System.exit(0);
//			}
//			if (!parseTree.get(top).h.containsKey("EOF")) {
//				System.out.println("Error at Line No. " + j);
//				System.exit(0);
//			}
//		}
//		System.out.println("Expression parsed successfully");
//
//	}
//
//	public void printStack(ArrayList<String> st) {
//		System.out.println("******Stack*******");
//		for (String t : st) {
//			System.out.print("   " + t);
//		}
//		System.out.println("\n******StackEnd*******");
//	}
//
//	public void run() throws Exception {
//		// TODO Auto-generated method stub
//		FileReader fr = new FileReader("test.awk");
//		BufferedReader br = new BufferedReader(fr);
//		String awk = "";
//		String s;
//		while ((s = br.readLine()) != null) {
//			// System.out.println(s);
//			if (s.indexOf("#") != -1) {
//				s = s.substring(0, s.indexOf("#"));
//				// System.out.println(s);
//			}
//			awk = awk + s;
//		}
//		// awk.replace("\n", "");
//
//		System.out.println(awk);
//		int i = 0;
//		StringBuilder tok = new StringBuilder();
//		int typ = -1;
//		boolean cont = false;
//
//		while (i < awk.length()) {
//			if (num.indexOf(awk.charAt(i)) != -1) {
//				tokens.add(new Token("" + awk.charAt(i), 1));
//				System.out.print(tok);
//
//			} else if (awk.charAt(i) == '+') {
//				if (i + 1 < awk.length()) {
//					if (awk.charAt(i + 1) == '+') {
//						tokens.add(new Token("++", 2));
//						i = i + 2;
//						continue;
//
//					}
//				}
//				tokens.add(new Token("+", 4));
//			} else if (awk.charAt(i) == '-') {
//				if (i + 1 < awk.length()) {
//					if (awk.charAt(i + 1) == '-') {
//						tokens.add(new Token("--", 3));
//						i = i + 2;
//						continue;
//
//					}
//				}
//				tokens.add(new Token("-", 5));
//			} else if (awk.charAt(i) == '$') {
//				tokens.add(new Token("$", 6));
//			} else if (awk.charAt(i) == '(') {
//				tokens.add(new Token("(", 7));
//
//			} else if (awk.charAt(i) == ')') {
//				tokens.add(new Token(")", 8));
//			}
//
//			i++;
//		}
//
//		fr.close();
//		for (Token t : tokens) {
//			System.out.println(t.tk);
//		}
//	}
//
//	public void enrollTable() throws Exception {
//		String s[] = grammar.split("\n");
//		String title[] = s[0].split(",");
//		for (int i = 1; i < s.length; i++) {
//			String temp[] = s[i].split(",");
//			Production p = new Production();
//			for (int j = 1; j < title.length; j++) {
//
//				if (!temp[j].equalsIgnoreCase("?")) {
//					p.h.put(title[j].trim(), temp[j].trim());
//					// System.out.println("~~~~~~~~" + title[j] + "-----"
//					// + temp[j]);
//				}
//			}
//			// System.out.println("UP -> " + temp[0]);
//			parseTree.put(temp[0].trim(), p);
//		}
//		// System.out.println("Parse Tree enrolled");
//	}
