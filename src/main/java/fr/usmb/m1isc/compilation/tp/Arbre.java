package fr.usmb.m1isc.compilation.tp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class Arbre {
	
	public enum NodeType {
		NAME, PLUS, MOINS, MOINS_UNAIRE, MUL, DIV, MOD, NOT, OR, AND, PAR_G, PAR_D, SEMI, POINT, LET, INPUT, OUTPUT, IF, THEN, ELSE, WHILE, DO, EGAL, GT, GTE, NIL, ERROR, ENTIER, IDENT
	}
	
	private NodeType type;
	private Object value;
	private Arbre fg, fd;
	private char format = 0;
	public Arbre(NodeType type, Object value, Arbre fg, Arbre fd) {
		this.type = type;
		this.value = value;
		this.fg = fg;
		this.fd = fd;
	}
	public Arbre(NodeType type, Object value) {
		this(type, value, null, null);
	}
	public Arbre(NodeType type) {
		this(type, null, null, null);
	}
	public NodeType getType() {
		return type;
	}
	public void setType(NodeType type) {
		this.type = type;
	}
	public Object getValue() {
		return value;
	}
	public void setValue(Object value) {
		this.value = value;
	}
	public Arbre getFg() {
		return fg;
	}
	public void setFg(Arbre fg) {
		this.fg = fg;
	}
	public Arbre getFd() {
		return fd;
	}
	public void setFd(Arbre fd) {
		this.fd = fd;
	}
	public char getFormat() {
		return format;
	}
	public void setFormat(char format) {
		this.format = format;
	}
	
	public String[] getAllVariables() {
		ArrayList<String> vars = new ArrayList<String>();
		if(type.equals(NodeType.LET))
			vars.add((String) fg.value);
		if(fg != null)
			Collections.addAll(vars, fg.getAllVariables());
		if(fd != null)
			Collections.addAll(vars, fd.getAllVariables());
		
		String[] out = new String[vars.size()];
		out = vars.toArray(out);
		return out;
	}
	
	private String[] getCode(Arbre a, String r) throws IOException {
		if(a.fg == null && a.fd == null) {
			return new String[] {"mov " + r + ", " + (String)a.value};
		}
		
		ArrayList<String> vars = new ArrayList<String>();
		Collections.addAll(vars, a.parseFunctions());
		vars.add("pop " + r);
		
		String[] out = new String[vars.size()];
		out = vars.toArray(out);
		return out;
	}
	
	public String[] parseFunctions() throws IOException {
		ArrayList<String> funcs = new ArrayList<String>();
		
		switch (type) {
		case LET:
			Collections.addAll(funcs, getCode(fd, "eax"));
			funcs.add("mov " + fg.value + ", eax");
			funcs.add("push eax");
			break;
			
		case PLUS:
			Collections.addAll(funcs, getCode(fg, "eax"));
			Collections.addAll(funcs, getCode(fd, "ebx"));
			funcs.add("add eax, ebx");
			funcs.add("push eax");
			break;
			
		case MOINS:
			Collections.addAll(funcs, getCode(fg, "eax"));
			Collections.addAll(funcs, getCode(fd, "ebx"));
			funcs.add("sub eax, ebx");
			funcs.add("push eax");
			break;
			
		case MUL:
			Collections.addAll(funcs, getCode(fg, "eax"));
			Collections.addAll(funcs, getCode(fd, "ebx"));
			funcs.add("mul eax, ebx");
			funcs.add("push eax");
			break;
			
		case DIV:
			Collections.addAll(funcs, getCode(fg, "eax"));
			Collections.addAll(funcs, getCode(fd, "ebx"));
			funcs.add("div eax, ebx");
			funcs.add("push eax");
			break;
			
		case SEMI:
			Collections.addAll(funcs, getCode(fg, "eax"));
			Collections.addAll(funcs, fd.parseFunctions());
			break;
			
		default:
			break;
		}
		
		String[] out = new String[funcs.size()];
		out = funcs.toArray(out);
		return out;
	}
	
	public String toString() {
		StringBuilder builder = new StringBuilder();
		
		if (fg == null && fd == null)
			return (String)value + " ";
		
	    if (fg != null)
	    	if (fd != null)
	    		builder.append("(" + value + " " + fg.toString() + " " + fd.toString() + ")");
	    	else
	    		builder.append("(" + value + " " + fg.toString() + ")");
	    else if (fd != null)
	    	builder.append("(" + value + " . " + fd.toString() + ")");

	    	
	    return builder.toString();
	  }
}