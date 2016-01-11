import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;

import cs132.vapor.ast.VAddr;
import cs132.vapor.ast.VAssign;
import cs132.vapor.ast.VBranch;
import cs132.vapor.ast.VBuiltIn;
import cs132.vapor.ast.VCall;
import cs132.vapor.ast.VCodeLabel;
import cs132.vapor.ast.VFunction;
import cs132.vapor.ast.VGoto;
import cs132.vapor.ast.VInstr;
import cs132.vapor.ast.VMemRead;
import cs132.vapor.ast.VMemRef;
import cs132.vapor.ast.VMemWrite;
import cs132.vapor.ast.VOperand;
import cs132.vapor.ast.VReturn;
import cs132.vapor.ast.VVarRef;

public class FGraphBuilder extends VInstr.VisitorP<Node, Throwable> {

	// FlowGraphs
	HashMap<String, Node> graph = new HashMap<String, Node>();
	HashMap<VInstr, Node> mapper = new HashMap<VInstr, Node>();

	public void buildFlowGraph(VFunction[] vf) throws Throwable {
		for (VFunction func : vf) {
			// FGNode root;
			Node last = null;
			for (int i = 0; i < func.body.length; i++) {
				Node temp;
				if (i == 0) {
					temp = new Node(func.body[0].sourcePos.line);
					graph.put(func.ident, temp);
					mapper.put(func.body[0], temp);
				} else {
					temp = new Node(func.body[i].sourcePos.line);
					mapper.put(func.body[i], temp);
				}
				func.body[i].accept(temp, this);
				if (last != null) {
					last.children.add(temp);
				}
				last = temp;
			}
			for (int i = 0; i < func.body.length; i++) {
				Node temp = mapper.get(func.body[i]);
				if (func.body[i] instanceof VBranch) {
					VBranch branch = (VBranch) func.body[i];
					VInstr insttemp = func.body[branch.target.getTarget().instrIndex];
					Node nod = mapper.get(insttemp);
					temp.children.add(nod);

				} else if (func.body[i] instanceof VGoto) {
					VGoto go_to = (VGoto) func.body[i];
					if (go_to.target instanceof VAddr.Label<?>) {
						VAddr.Label<VCodeLabel> label = (VAddr.Label<VCodeLabel>) go_to.target;
						VInstr insttemp = func.body[label.label.getTarget().instrIndex];
						Node nod = mapper.get(insttemp);
						temp.children.add(nod);
					} else if (go_to.target instanceof VAddr.Var<?>) {
						for (VCodeLabel label : func.labels) {
							VInstr insttemp = func.body[label.instrIndex];
							Node nod = mapper.get(insttemp);
							temp.children.add(nod);
						}
					}
				}
			}

		}

	}

	HashMap<String, HashMap<String, LiveIntervals>> ranges = new HashMap<String, HashMap<String, LiveIntervals>>();
	HashSet<Node> visit = new HashSet<Node>();
	HashMap<String, LiveIntervals> localranges;

	public void liveIntervals(VFunction[] vf) {
		for (VFunction func : vf) {
			localranges = new HashMap<String, LiveIntervals>();

			Node root = graph.get(func.ident);
			getInternalIntervals(root);

			for (String inVar : root.in_set) {

				LiveIntervals li = localranges.get(inVar);
				if (li == null) {
					li = new LiveIntervals();
					localranges.put(inVar, li);
				}
				li.add(new Interval(0, root.linenumber));
			}
			ranges.put(func.ident, localranges);
			// localranges.clear();
		}
	}

	public void getInternalIntervals(Node node) {
		if (visit.contains(node)) {
			return;
		}
		for (String v : node.out_set) {
			for (Node child : node.children) {
				if (child.in_set.contains(v)) {
					LiveIntervals li = localranges.get(v);
					if (li == null) {
						li = new LiveIntervals();
						localranges.put(v, li);
					}

					li.add(new Interval(node.linenumber, child.linenumber));
				}
			}
		}
		for (Node child : node.children) {
			visit.add(node);
			getInternalIntervals(child);
		}

	}

	int maxargs = 0;

	public void maxArgs(VFunction[] vf) {
		int max = -1;
		for (VFunction func : vf) {
			int temp = func.params.length;
			if (temp > max)
				max = temp;
		}
		maxargs = max;
	}

	public void computeInOut(VFunction[] vf) {
		for (VFunction func : vf) {
			boolean done = true;
			while (done) {
				done = false;
				for (VInstr instr : func.body) {
					Node n = mapper.get(instr);
					HashSet<String> out_nodef = new HashSet<String>(
							n.out_set);
					out_nodef.removeAll(n.def);
					HashSet<String> in_def = new HashSet<String>(n.use);
					in_def.addAll(out_nodef);
					HashSet<String> out_def = new HashSet<String>();
					for (Node successor : n.children) {
						out_def.addAll(successor.in_set);
					}
					if (!equalLists(n.out_set, out_def)
							|| !equalLists(in_def, n.in_set)) {
						done = true;
						n.in_set = in_def;
						n.out_set = out_def;
					}
				}
			}

		}
	}

	public boolean equalLists(HashSet<String> one, HashSet<String> two) {

		return one.equals(two);
	}

	public boolean equalLists(List<String> one, List<String> two) {
		if (one == null && two == null) {
			return true;
		}

		if ((one == null && two != null) || one != null && two == null
				|| one.size() != two.size()) {
			return false;
		}

		one = new ArrayList<String>(one);
		two = new ArrayList<String>(two);

		Collections.sort(one);
		Collections.sort(two);
		return one.equals(two);
	}

	@Override
	public void visit(Node arg0, VAssign arg1) throws Throwable {
		// TODO Auto-generated method stub
		if (arg1.source instanceof VVarRef.Local) {
			arg0.use.add(arg1.source.toString());
		}
		arg0.def.add(arg1.dest.toString());

	}

	@Override
	public void visit(Node arg0, VCall arg1) throws Throwable {
		// TODO Auto-generated method stub

		for (VOperand op : arg1.args) {
			if (op instanceof VVarRef.Local) {
				arg0.use.add(op.toString());
			}
		}

		if (arg1.dest != null) {
			arg0.def.add(arg1.dest.toString());
		}
		String name = null;
		if (arg1.addr instanceof VAddr.Var<?>) {
			VAddr.Var v = (VAddr.Var) arg1.addr;
			name = v.var.toString();
		}
		if (name != null) {
			arg0.use.add(name);
		}
	}

	@Override
	public void visit(Node arg0, VBuiltIn arg1) throws Throwable {
		// TODO Auto-generated method stub
		for (VOperand op : arg1.args) {
			if (op instanceof VVarRef.Local) {
				arg0.use.add(op.toString());
			}
		}
		if (arg1.dest != null) {
			arg0.def.add(arg1.dest.toString());
		}
	}

	@Override
	public void visit(Node arg0, VMemWrite arg1) throws Throwable {
		// TODO Auto-generated method stub
		if (arg1.source instanceof VVarRef.Local) {
			arg0.use.add(arg1.source.toString());
		}
		if (arg1.dest instanceof VMemRef.Global) {
			VMemRef.Global ref = (VMemRef.Global) arg1.dest;
			String name = null;
			if (ref.base instanceof VAddr.Var<?>) {
				VAddr.Var v = (VAddr.Var) ref.base;
				name = v.var.toString();
			}
			if (name != null) {
				arg0.use.add(name);
			}
		}

	}

	@Override
	public void visit(Node arg0, VMemRead arg1) throws Throwable {
		// TODO Auto-generated method stub
		if (arg1.source instanceof VMemRef.Global) {
			VMemRef.Global ref = (VMemRef.Global) arg1.source;

			String name = null;
			if (ref.base instanceof VAddr.Var<?>) {
				VAddr.Var v = (VAddr.Var) ref.base;
				name = v.var.toString();
			}

			if (name != null) {
				arg0.use.add(name);
			}
		}

		arg0.def.add(arg1.dest.toString());

	}

	@Override
	public void visit(Node arg0, VBranch arg1) throws Throwable {
		// TODO Auto-generated method stub
		if (arg1.value instanceof VVarRef.Local) {
			arg0.use.add(arg1.value.toString());
		}
	}

	@Override
	public void visit(Node arg0, VGoto arg1) throws Throwable {
		// TODO Auto-generated method stub
		String name = null;
		if (arg1.target instanceof VAddr.Var<?>) {
			VAddr.Var v = (VAddr.Var) arg1.target;
			name = v.var.toString();
		}
		if (name != null) {
			arg0.use.add(name);
		}
	}

	@Override
	public void visit(Node arg0, VReturn arg1) throws Throwable {
		// TODO Auto-generated method stub
		if (arg1.value instanceof VVarRef.Local) {
			arg0.use.add(arg1.value.toString());
		}
	}

}

class Interval {
	public int start;
	public int end;

	public Interval(int s, int e) {
		start = s;
		end = e;
	}
}

class LiveIntervals {
	public LinkedHashSet<Interval> intervals = new LinkedHashSet<Interval>();

	public void add(Interval i) {
		intervals.add(i);
	}

	public boolean isOverlapping(LiveIntervals li) {
		for (Interval i : li.intervals) {
			for (Interval j : intervals) {
				if (i.start >= j.start && j.start <= i.end) {
					return true;
				} else if (i.start >= j.end && j.end <= i.end) {
					return true;
				} else if (j.start >= i.start && j.end <= i.end) {
					return true;

				}
			}
		}
		return false;

	}
}

class Node {

	ArrayList<String> use = new ArrayList<String>();
	ArrayList<String> def = new ArrayList<String>();

	int linenumber;

	ArrayList<Node> children = new ArrayList<Node>();

	public Node(int line) {
		linenumber = line;
	}

	HashSet<String> in_set = new HashSet<String>();
	HashSet<String> out_set = new HashSet<String>();

}