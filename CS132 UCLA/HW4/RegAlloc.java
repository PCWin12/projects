import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

public class RegAlloc {
	FGraphBuilder fgb;
	public HashMap<String, ArrayList<String>> spillageMapper  = new HashMap<String, ArrayList<String>>();
	public HashMap<String, HashMap<String, String>> regMapper = new HashMap<String, HashMap<String,String>>();
	private String[] callee  = new String[8];
	private String[] caller = new String[8];

	public RegAlloc(FGraphBuilder f) {
		fgb = f;
		initiateRegisters();
	}
	private void initiateRegisters() {
		// TODO Auto-generated method stu
		for(int i = 0; i < callee.length; i++) {
			callee[i] = "$s" + i;
			caller[i] = "$t" + (i);
		}
	}
	ArrayList<String> localspillage= new ArrayList<String>();
	HashMap<String,LiveIntervals> localrange;
	
	public void allocateRegisters(){
		//System.out.println("In Alloc");
		for(Entry<String, HashMap<String,LiveIntervals>> entry:fgb.ranges.entrySet()) {
			localrange = entry.getValue();
			localspillage= new ArrayList<String>();
			HashMap<String,String> assignments = setRegister(localrange, localspillage);
			spillageMapper.put(entry.getKey(), localspillage);
			regMapper.put(entry.getKey(), assignments);
			//System.out.println(entry.getKey());
		}
	}
	
	
	public void getRegisters(HashMap<String,ArrayList<String>> mapper, ArrayList<String> registers){  
		  for(String s:caller) {
			  registers.add(s);
			  mapper.put(s, new ArrayList<String>());
		  }
		  for(String s:callee) {
			  registers.add(s);
			  mapper.put(s, new ArrayList<String>());
		  }
	}
	 public HashMap<String,String> setRegister(HashMap<String,LiveIntervals> r, ArrayList<String> localspillage) {
		  ArrayList<String> registers = new ArrayList<String>();
		  HashMap<String,String> inUse= new HashMap<String,String>();
		  HashMap<String,ArrayList<String>> mapper =  new HashMap<String,ArrayList<String>>();
		
		  			getRegisters(mapper, registers);
	
		  for(Entry<String, LiveIntervals> entry:r.entrySet()) {
			  String var = entry.getKey();
			  if(registers.size()>0) {
				  String avail = registers.remove(0);
				  inUse.put(var, avail);
				  mapper.get(avail).add(var);
			  } else {
				  boolean availRegister = false;
				  for(Entry<String, ArrayList<String>> e:mapper.entrySet()) {
					  String regname = e.getKey();
					  ArrayList<String> variables = e.getValue();
					  boolean overlap = true;
					  for(String variable:variables) {
						  if(r.get(variable).isOverlapping( r.get(var))) 
							  overlap = false;  
					  }
					  
					  if(overlap) {							
						  availRegister = true;
					     inUse.put(var, regname);
						 mapper.get(regname).add(var);
					     break;
				      }
			       }

				  if(!availRegister) {
					  int maxSize = 0;
					  String maxVar = "";
					  for(Entry<String, LiveIntervals> e:r.entrySet()) {
				          LiveIntervals l = e.getValue();
						  int size = l.intervals.size();
						  
						  if(maxVar.isEmpty() || (size > maxSize) ) {
							  maxVar = e.getKey();
							  maxSize = size;
						  }
					  }
					  localspillage.add(maxVar);
					  r.remove(maxVar);
					  inUse = setRegister(r, localspillage);
					  return inUse;
				  }
			  }
			  
		  }
		  
		  return inUse;
		  
	  }

}
