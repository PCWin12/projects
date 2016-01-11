import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;

import cs132.util.ProblemException;
import cs132.vapor.ast.VBuiltIn.Op;
import cs132.vapor.ast.VDataSegment;
import cs132.vapor.ast.VOperand.Static;
import cs132.vapor.ast.VaporProgram;
import cs132.vapor.parser.VaporParser;

public class VM2M {
	public static void main(String args[]) throws Throwable {
		InputStream in = System.in;
		//in = new FileInputStream(
			//	"/Users/malig/Downloads/hw5-tester/SelfTestCases/LinkedList.vaporm");

		VaporProgram vp;
		try {
			vp = parseVapor(in, System.err);
			System.out.println(printHeader(vp.dataSegments));
			PrintVisitor p = new PrintVisitor(vp);
			p.initiateVisits();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	
	public static String printHeader(VDataSegment[] vd) {
		String head = "";
		head = head + ".data\n";
		for (VDataSegment ds : vd) {
			head = head + ds.ident + ":\n";
			for (Static v : ds.values) {
				String temp = v.toString().replaceFirst(":", "");

				head = head + " " + temp + "\n";
			}
			head = head + "\n";
		}
		return head;
	}

	public static VaporProgram parseVapor(InputStream in, PrintStream err)
			throws IOException {
		Op[] ops = { Op.Add, Op.Sub, Op.MulS, Op.Eq, Op.Lt, Op.LtS,
				Op.PrintIntS, Op.HeapAllocZ, Op.Error, };
		boolean allowLocals = false;
		String[] registers = { "v0", "v1", "a0", "a1", "a2", "a3", "t0", "t1",
				"t2", "t3", "t4", "t5", "t6", "t7", "s0", "s1", "s2", "s3",
				"s4", "s5", "s6", "s7", "t8", };
		boolean allowStack = true;

		VaporProgram program;
		try {
			program = VaporParser.run(new InputStreamReader(in), 1, 1,
					java.util.Arrays.asList(ops), allowLocals, registers,
					allowStack);
		} catch (ProblemException ex) {
			err.println(ex.getMessage());
			return null;
		}

		return program;
	}
}
