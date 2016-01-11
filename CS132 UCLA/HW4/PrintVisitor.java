import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

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

public class PrintVisitor extends VInstr.VisitorPR<Integer, String, Throwable> {
	RegAlloc alloc;
	String[] vRegisters = new String[3];
	String[] aRegisters = new String[4];
	FGraphBuilder fg;

	public PrintVisitor(RegAlloc reg, FGraphBuilder f) {
		// TODO Auto-generated constructor stub
		alloc = reg;
		initiate();
		fg = f;
	}

	String currentMethod;
	private int spillstartsat;
	private int ofsetcalersaved;
	String out_fun = "";

	public String print(VFunction[] vf) throws Throwable {

		for (VFunction func : vf) {
			String temp_func = "";
			currentMethod = func.ident;

			HashMap<String, String> func_registers = alloc.regMapper
					.get(func.ident);
			ArrayList<String> savedbycallee = new ArrayList<String>();
			String saveCalleeSavedReg = recordCalleeSaved(func_registers,
					savedbycallee);
			int clr_saved_size = 0;

			for (Entry<String, String> e : alloc.regMapper.get(currentMethod)
					.entrySet()) {
				String reg = e.getValue();
				if (reg.contains("t")) {
					clr_saved_size++;
				}
			}

			int totaladdr = savedbycallee.size()
					+ alloc.spillageMapper.get(func.ident).size()
					+ clr_saved_size;

			if (func.ident.equalsIgnoreCase("main")) {
				temp_func += " [in 0 , out " + fg.maxargs + " , local "
						+ totaladdr + "]";
			} else {

				temp_func += " [in " + fg.maxargs + " , out " + fg.maxargs
						+ " , local " + totaladdr + "]";
			}

			temp_func = temp_func + "\n" + saveCalleeSavedReg;

			spillstartsat = savedbycallee.size();
			ofsetcalersaved = savedbycallee.size()
					+ alloc.spillageMapper.get(func.ident).size();
			String loadArgs = getRegistersarg(func.params);
			temp_func = temp_func + "\n" + loadArgs;

			for (int i = 0; i < func.body.length; i++) {

				String vaporMCode = func.body[i].accept(1, this);
				for (VCodeLabel l : func.labels) {
					if (l.instrIndex == i) {
						temp_func = temp_func + "\n" + l.ident + ":";
					}
				}
				temp_func = temp_func + "\n" + vaporMCode;

			}
			temp_func = temp_func + "\n" + loadbackCalleeSaved(savedbycallee);
			temp_func = "func " + func.ident + temp_func + "\n" + " ret";

			out_fun = out_fun + "\n" + temp_func;

		}
		System.out.println(out_fun);

		return out_fun;
	}

	public String loadbackCalleeSaved(ArrayList<String> calleeSavedRegisters) {
		String ret = "";
		for (int i = 0; i < calleeSavedRegisters.size(); i++) {
			String temp = " " + calleeSavedRegisters.get(i) + " = local[" + i
					+ "]";
			ret = ret + "\n" + temp;
		}
		return ret;
	}

	public String loadBackCallerSaved(
			ArrayList<String> callerSavedRegisters) {
		String ret = "";
		for (int i = 0; i < callerSavedRegisters.size(); i++) {
			String temp = getSpaces(1) + callerSavedRegisters.get(i)
					+ " = local[" + (i + ofsetcalersaved) + "]";
			ret = ret + "\n" + temp;
		}

		return ret;
	}

	public void initiate() {

		vRegisters[0] = "$v0";
		vRegisters[1] = "$v1";
		vRegisters[2] = "$t8";

		for (int i = 0; i < 4; i++) {
			aRegisters[i] = "$a" + i;
		}

	}

	public String getSpaces(int i) {
		String temp = "";
		for (int j = 0; j < i; j++) {
			temp = temp + "  ";
		}
		return temp;
	}

	// 1 calee saved
	// 2 caller saved
	public String recordCalleeSaved(HashMap<String, String> func_register,
			ArrayList<String> savedbycalee) {
		String ret = "";
		for (Entry<String, String> e : func_register.entrySet()) {
			String reg = e.getValue();
			if (reg.contains("s")) {
				if (!contains(savedbycalee, reg)) {
					savedbycalee.add(reg);
				}
			}
		}
		for (int i = 0; i < savedbycalee.size(); i++) {
			ret = ret + "\n local[" + i + "] = " + savedbycalee.get(i);
		}

		return ret;
	}

	public String applySpill(String reg, String var, Integer spaces) {
		String ret = "";
		if (contains(alloc.spillageMapper.get(currentMethod), var)) {
			ret = getSpaces(spaces)
					+ reg
					+ " = local["
					+ (alloc.spillageMapper.get(currentMethod).indexOf(var) + spillstartsat)
					+ "]";
		} else {
			var = alloc.regMapper.get(currentMethod).get(var);
			if (var == null)
				var = vRegisters[2];
			ret = getSpaces(spaces) + reg + " = " + var;
		}

		return ret;
	}

	@Override
	public String visit(Integer arg0, VAssign arg1) throws Throwable {
		// TODO Auto-generated method stub

		String dest = arg1.dest.toString();
		String val = arg1.source.toString();
		String assign = "";

		if (arg1.source instanceof VVarRef.Local) {
			assign = applySpill(vRegisters[0], val, arg0);
			val = vRegisters[0];
		}

		if (contains(alloc.spillageMapper.get(currentMethod), dest)) {
			assign = assign + "\n"
					+ saveInLocal(currentMethod, dest, val, arg0);
		} else {
			dest = alloc.regMapper.get(currentMethod).get(dest);
			if (dest == null)
				dest = vRegisters[2];
			assign = assign + "\n" + getSpaces(arg0) + dest + " = " + val;
		}

		return assign;
	}

	@Override
	public String visit(Integer arg0, VCall arg1) throws Throwable {
		// TODO Auto-generated method stub
		String functionCall = "";
		ArrayList<String> registers_caller = new ArrayList<String>();
		String savingRegisters = callersavingReg(registers_caller);
		String loadingbackRegisters = loadBackCallerSaved(registers_caller);

		int i = 0;
		String func_args1 = "";
		for (VOperand opr : arg1.args) {
			String currentArgReg = "";
			if (opr instanceof VVarRef.Local) {
				String s = applySpill(vRegisters[0], opr.toString(), arg0);
				func_args1 = func_args1 + "\n" + s;
				currentArgReg = vRegisters[0];
			} else {
				currentArgReg = opr.toString();
			}

			if (i < 4) {
				String destReg = aRegisters[i];
				func_args1 = func_args1 + "\n" + getSpaces(arg0) + destReg
						+ " = " + currentArgReg;
			} else {
				func_args1 = func_args1 + "\n" + getSpaces(arg0) + "out["
						+ (i - 4) + "]" + " = " + currentArgReg;
			}

			i++;
		}

		String create_call = "";
		String call_addr = null;
		if (arg1.addr instanceof VAddr.Var<?>) {
			VAddr.Var v = (VAddr.Var) arg1.addr;
			call_addr = v.var.toString();

		}

		if (call_addr != null) {
			create_call = functionCall + "\n"
					+ applySpill(vRegisters[0], call_addr, arg0);
			call_addr = vRegisters[0];
		} else {
			call_addr = arg1.addr.toString();
		}
		create_call = create_call + "\n" + getSpaces(arg0) + "call "
				+ call_addr;
		String create_ret = "";
		if (arg1.dest != null) {
			String dest = arg1.dest.toString();

			if (contains(alloc.spillageMapper.get(currentMethod), dest)) {
				create_ret = create_ret + "\n"
						+ saveInLocal(currentMethod, dest, "$v0", arg0);
			} else {
				dest = alloc.regMapper.get(currentMethod).get(dest);
				if (dest == null)
					dest = vRegisters[2];
				create_ret = create_ret + "\n" + getSpaces(arg0) + dest
						+ " = $v0";
			}
		}

		functionCall = savingRegisters + "\n" + func_args1 + "\n" + create_call
				+ "\n" + loadingbackRegisters + "\n" + create_ret;
		return functionCall;
	}

	@Override
	public String visit(Integer arg0, VBuiltIn arg1) throws Throwable {
		// TODO Auto-generated method stub
		String ret = "";
		int[] temp = { 0, 0 };
		String[] argument_reg = new String[2];
		for (int i = 0; i < arg1.args.length; i++) {
			VOperand op = arg1.args[i];
			if (op instanceof VVarRef.Local) {
				String spil = vRegisters[i];
				ret = ret + "\n" + applySpill(spil, op.toString(), arg0);
				argument_reg[i] = spil;
				temp[i] = 1;
			}
		}

		String func_inputs = "";
		for (int i = 0; i < arg1.args.length; i++) {
			if (temp[i] == 0)
				func_inputs = func_inputs + " " + arg1.args[i].toString();
			else
				func_inputs = func_inputs + " " + argument_reg[i];
		}

		String _actual = arg1.op.name + "(" + func_inputs + ")";
		if (arg1.dest == null) {
			ret = ret + "\n" + getSpaces(arg0) + _actual;
		} else {
			String nam = arg1.dest.toString();
			if (contains(alloc.spillageMapper.get(currentMethod), nam)) {
				String spil = vRegisters[2];
				ret = ret + "\n" + getSpaces(arg0) + spil + " = " + _actual;
				ret = ret + "\n" + saveInLocal(currentMethod, nam, spil, arg0);
			} else {
				nam = alloc.regMapper.get(currentMethod).get(nam);
				if (nam == null)
					nam = vRegisters[2];
				ret = ret + "\n" + getSpaces(arg0) + nam + " = " + _actual;
			}

		}

		return ret;

	}

	@Override
	public String visit(Integer arg0, VMemWrite arg1) throws Throwable {
		// TODO Auto-generated method stub
		String temp = "";
		String var = arg1.source.toString();

		if (arg1.source instanceof VVarRef.Local) {
			String spil = vRegisters[0];
			temp = applySpill(spil, var, arg0);
			var = spil;
		}

		if (arg1.dest instanceof VMemRef.Global) {
			VMemRef.Global ref = (VMemRef.Global) arg1.dest;

			String base = null;
			if (ref.base instanceof VAddr.Var<?>) {
				VAddr.Var v = (VAddr.Var) ref.base;
				base = v.var.toString();

			}

			if (base != null) {
				String reg = vRegisters[1];
				temp = temp + "\n" + applySpill(reg, base, arg0) + "\n"
						+ getSpaces(arg0) + "[" + reg + "+" + ref.byteOffset
						+ "] = " + var;
			} else {
				temp = temp + "\n" + getSpaces(arg0) + "["
						+ ref.base.toString() + "+" + ref.byteOffset + "] = "
						+ var;
			}
		}

		return temp;
	}

	@Override
	public String visit(Integer arg0, VMemRead arg1) throws Throwable {
		// TODO Auto-generated method stub
		String temp = "";
		String destVar = arg1.dest.toString();

		String reg_ret = "";
		if (arg1.source instanceof VMemRef.Global) {
			VMemRef.Global ref = (VMemRef.Global) arg1.source;

			String base = null;
			if (ref.base instanceof VAddr.Var<?>) {
				VAddr.Var v = (VAddr.Var) ref.base;
				base = v.var.toString();

			}

			reg_ret = vRegisters[0];
			if (base != null) {
				temp = temp + "\n" + applySpill(reg_ret, base, arg0) + "\n"
						+ getSpaces(arg0) + reg_ret + " = [" + reg_ret + "+"
						+ ref.byteOffset + "]";
			} else {
				temp = temp + "\n" + getSpaces(arg0) + reg_ret + " = ["
						+ ref.base.toString() + "+" + ref.byteOffset + "]";
			}
		}

		if (contains(alloc.spillageMapper.get(currentMethod), destVar)) {
			temp = temp + "\n"
					+ saveInLocal(currentMethod, destVar, reg_ret, arg0);
		} else {
			String reg = alloc.regMapper.get(currentMethod).get(destVar);
			if (reg == null)
				reg = vRegisters[2];
			temp = temp + "\n" + getSpaces(arg0) + reg + " = " + reg_ret;
		}

		return temp;
	}

	@Override
	public String visit(Integer arg0, VBranch arg1) throws Throwable {
		// TODO Auto-generated method stub
		String cond = "";
		if (arg1.value instanceof VVarRef.Local) {
			String var = arg1.value.toString();
			cond = applySpill(vRegisters[0], var, arg0);
		} else {
			cond = getSpaces(arg0) + vRegisters[0] + " = "
					+ arg1.value.toString();
		}
		String reg = vRegisters[0];

		if (!arg1.positive) {
			return cond + "\n" + getSpaces(arg0) + "if0 " + reg + " goto "
					+ arg1.target.toString();
		} else {
			return cond + "\n" + getSpaces(arg0) + "if " + reg + " goto "
					+ arg1.target.toString();
		}

	}

	@Override
	public String visit(Integer arg0, VGoto arg1) throws Throwable {
		// TODO Auto-generated method stub
		String base = null;
		if (arg1.target instanceof VAddr.Var<?>) {
			VAddr.Var v = (VAddr.Var) arg1.target;
			base = v.var.toString();

		}

		if (base == null) {
			return "goto " + arg1.target.toString();
		} else {
			return applySpill(vRegisters[0], base, arg0) + "\n"
					+ getSpaces(arg0) + "goto " + vRegisters[0];

		}
	}

	@Override
	public String visit(Integer arg0, VReturn arg1) throws Throwable {
		// TODO Auto-generated method stub
		if (arg1.value == null) {
			return "";
		}
		if (arg1.value instanceof VVarRef.Local) {
			return applySpill("$v0", arg1.value.toString(), arg0);
		} else {
			return getSpaces(arg0) + "$v0 = " + arg1.value.toString();
		}

	}

	public String getRegistersarg(VVarRef.Local[] params) {
		String loadArguments = "";
		for (int i = 0; i < params.length; i++) {
			String paramVal = "";
			if (i < 4) {
				paramVal = aRegisters[i];
			} else {
				String assignment = " " + vRegisters[0] + " = in[ " + (i - 4)
						+ " ]";
				loadArguments = loadArguments + "\n" + assignment;
				paramVal = vRegisters[0];
			}
			if (contains(alloc.spillageMapper.get(currentMethod),
					params[i].toString())) {
				loadArguments = loadArguments
						+ "\n"
						+ saveInLocal(currentMethod, params[i].toString(),
								paramVal, 1);
			} else {
				String temp = alloc.regMapper.get(currentMethod).get(
						params[i].toString());
				if (temp == null)
					temp = vRegisters[2];
				loadArguments = loadArguments + "\n " + temp + " = " + paramVal;
			}

		}

		return loadArguments;
	}

	boolean contains(ArrayList<String> l, String str) {
		for (String s : l) {
			if (s.equals(str)) {
				return true;
			}
		}
		return false;

	}

	public String callersavingReg(ArrayList<String> reg_caller) {
		String ret = "";
		for (Entry<String, String> e : alloc.regMapper.get(currentMethod)
				.entrySet()) {
			String reg = e.getValue();
			if (reg.contains("t")) {
				if (!contains(reg_caller, reg)) {
					reg_caller.add(reg);
				}
			}
		}
		for (int i = 0; i < reg_caller.size(); i++) {
			String temp = " local[" + String.valueOf(i + ofsetcalersaved)
					+ "] = " + reg_caller.get(i);
			ret = ret + "\n" + temp;
		}

		return ret;
	}

	public String saveInLocal(String funName, String variable,
			String sourceVal, Integer indentation) {
		int memoryOffset = alloc.spillageMapper.get(funName).indexOf(variable)
				+ spillstartsat;
		return getSpaces(indentation) + "local[" + memoryOffset + "]" + " = "
				+ sourceVal;
	}

}
