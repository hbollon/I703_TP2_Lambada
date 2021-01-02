package fr.usmb.m1isc.compilation.tp;

import java.io.FileWriter;
import java.io.IOException;

public class Generator {
	private String filename; 
	
	public Generator(String filename) {
		this.filename = filename;
	}
	
	public void convertToAsm(Arbre a) throws IOException {
		if(a == null)
			return;
		
		FileWriter f = new FileWriter(filename + ".asm", false);
		f.write("DATA SEGMENT\n");
		for(String var : a.getAllVariables()) {
			f.write("	" + var + " DD\n");
		}
		f.write("DATA ENDS\n");
		
		f.write("CODE SEGMENT\n");
		for(String func : a.parseFunctions()) {
			f.write((func.contains(":") ? "" : "\t") + func + "\n");
		}
		f.write("CODE ENDS\n");
		
		f.close();
	}
}
