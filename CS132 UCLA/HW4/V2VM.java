import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;

import cs132.util.ProblemException;
import cs132.vapor.ast.VBuiltIn.Op;
import cs132.vapor.ast.VDataSegment;
import cs132.vapor.ast.VOperand;
import cs132.vapor.ast.VaporProgram;
import cs132.vapor.parser.VaporParser;

public class V2VM {

	public static void main(String[] args) throws Throwable {
		// TODO Auto-generated method stub
		InputStream in = System.in;
		//stream = new FileInputStream("/Users/malig/Downloads/compilershw4-master/hw4-tester/SelfTestCases/LinkedList.vapor"); 
		
		VaporProgram vp;
		try {
			vp = parseVapor(in,System.err);
			System.out.println(printHeader(vp.dataSegments));
			parse(vp);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	private static void parse(VaporProgram vp) throws Throwable {
		// TODO Auto-generated method stub
		FGraphBuilder fg = new FGraphBuilder();
			fg.buildFlowGraph(vp.functions);
			//System.out.println("Pass G builder");
			fg.computeInOut(vp.functions);
			//System.out.println("Pass IN/OUT builder");
			fg.liveIntervals(vp.functions);
			//System.out.println("Pass Live builder");
			fg.maxArgs(vp.functions);
			//System.out.println("Pass FG builder");
			RegAlloc regal = new RegAlloc(fg);
			
			regal.allocateRegisters();
			//System.out.println("Pass Allocator");
			PrintVisitor p = new PrintVisitor(regal, fg);
			p.print(vp.functions);
	}
	public static String printHeader(VDataSegment[] vd){
		String space = " ";
		String cnst = "const";
		String ret = "";
		for(VDataSegment ds : vd)
		{
			ret = ret+ cnst + space + ds.ident+"\n";
			for(VOperand op : ds.values)
			{
				ret = ret+space + op.toString() + "\n";
			}
			
		}
		return ret;
	}

	public static VaporProgram parseVapor(InputStream in, PrintStream err)
			throws IOException {
		Op[] ops = { Op.Add, Op.Sub, Op.MulS, Op.Eq, Op.Lt, Op.LtS,
				Op.PrintIntS, Op.HeapAllocZ, Op.Error, };
		boolean allowLocals = true;
		String[] registers = null;
		boolean allowStack = false;

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
