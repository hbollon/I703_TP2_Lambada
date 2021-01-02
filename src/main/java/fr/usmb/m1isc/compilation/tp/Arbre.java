package fr.usmb.m1isc.compilation.tp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class Arbre {
	
	public enum NodeType {
		NAME, PLUS, MOINS, MOINS_UNAIRE, MUL, DIV, MOD, NOT, OR, AND, PAR_G, PAR_D, SEMI, POINT, LET, INPUT, OUTPUT, IF, THEN, ELSE, WHILE, DO, EGAL, GT, GTE, NIL, ERROR, ENTIER, IDENT
	}
	
	private NodeType type;
	private Object value;
	private Arbre fg, fd;
	private char format = 0;
	private int id;
	public Arbre(NodeType type, Object value, Arbre fg, Arbre fd) {
		this.type = type;
		this.value = value;
		this.fg = fg;
		this.fd = fd;
		this.id = 1;
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
		Set<String> vars = new HashSet<String>();
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
	
	public String[] parseFunctions() throws IOException {
		ArrayList<String> funcs = new ArrayList<String>();
		int condId;
		
		switch (type) {
		case ENTIER :
			funcs.add("mov eax, " + value);
            break;

        case IDENT:
        	funcs.add("mov eax, " + value);
            break;
            
		case LET:
			Collections.addAll(funcs, fd.parseFunctions());
			funcs.add("mov " + fg.value + ", eax");
			break;
			
		case PLUS:
			Collections.addAll(funcs, fg.parseFunctions());
			funcs.add("push eax");
			Collections.addAll(funcs, fd.parseFunctions());
			funcs.add("pop ebx");
			funcs.add("add eax, ebx");
			break;
			
		case MOINS:
			Collections.addAll(funcs, fg.parseFunctions());
			funcs.add("push eax");
			Collections.addAll(funcs, fd.parseFunctions());
			funcs.add("pop ebx");
			funcs.add("sub eax, ebx");
			break;
			
		case MUL:
			Collections.addAll(funcs, fg.parseFunctions());
			funcs.add("push eax");
			Collections.addAll(funcs, fd.parseFunctions());
			funcs.add("pop ebx");
			funcs.add("mul eax, ebx");
			break;
			
		case DIV:
			Collections.addAll(funcs, fg.parseFunctions());
			funcs.add("push eax");
			Collections.addAll(funcs, fd.parseFunctions());
			funcs.add("pop ebx");
			funcs.add("div eax, ebx");
			break;
			
		case MOD:
			Collections.addAll(funcs, fd.parseFunctions());
			funcs.add("push eax");
            Collections.addAll(funcs, fg.parseFunctions());
            funcs.add("pop ebx");
            funcs.add("mov ecx, eax");
            funcs.add("div ecx, ebx");
            funcs.add("mul ecx, ebx");
            funcs.add("sub eax, ecx");
            break;
			
		case SEMI:
			Collections.addAll(funcs, fg.parseFunctions());
			Collections.addAll(funcs, fd.parseFunctions());
			break;
			
		case GT:
			condId = getCondId();
            Collections.addAll(funcs, fg.parseFunctions());
            funcs.add("push eax");
            Collections.addAll(funcs, fd.parseFunctions());
            funcs.add("pop ebx");
            funcs.add("sub eax, ebx");
            funcs.add("jle FALSE_GT_" + condId);
            funcs.add("mov eax, 1");
            funcs.add("jmp END_GT_" + condId);
            funcs.add("FALSE_GT_" + condId + ":");
            funcs.add("mov eax, 0");
            funcs.add("END_GT_" + condId + ":");
            break;
            
		case GTE:
            condId = getCondId();
            Collections.addAll(funcs, fg.parseFunctions());
            funcs.add("push eax");
            Collections.addAll(funcs, fd.parseFunctions());
            funcs.add("pop ebx");
            funcs.add("sub eax, ebx");
            funcs.add("jle FALSE_GTE_" + condId);
            funcs.add("mov eax, 1");
            funcs.add("jmp END_GTE_" + condId);
            funcs.add("FALSE_GTE_" + condId + ":");
            funcs.add("mov eax, 0");
            funcs.add("END_GTE_" + condId + ":");
            break;
			
		case WHILE:
			condId = getCondId();
			funcs.add("START_WHILE_" + condId + ":");
			Collections.addAll(funcs, fg.parseFunctions());
			funcs.add("jz END_WHILE_" + condId);
			if(fd.getFg() != null)
				Collections.addAll(funcs, fd.getFg().parseFunctions());
			funcs.add("jmp START_WHILE_" + condId);
			funcs.add("END_WHILE_" + condId + ":");
			break;
			
		case IF:
			condId = getCondId();
			Collections.addAll(funcs, fg.parseFunctions());
            funcs.add("jz ELSE_" + condId);
            if(fd.getFg() != null)
            	Collections.addAll(funcs, fd.getFg().parseFunctions());
            funcs.add("jmp END_IF_" + condId);
            funcs.add("ELSE_" + condId + ":");
            if(fd.getFd().getFg() != null)
            	Collections.addAll(funcs, fd.getFd().getFg().parseFunctions());
            funcs.add("END_IF_" + condId + ":");
            break;

        case AND:
        	condId = getCondId();
        	Collections.addAll(funcs, fg.parseFunctions());
            funcs.add("jz END_AND_" + condId);
            Collections.addAll(funcs, fd.parseFunctions());
            funcs.add("END_AND_" + condId+ ":");
            break;

        case OR:
        	condId = getCondId();
        	Collections.addAll(funcs, fg.parseFunctions());
            funcs.add("jnz END_OR_" + condId);
            Collections.addAll(funcs, fd.parseFunctions());
            funcs.add("END_OR_" + condId + ":");
            break;

        case EGAL:
        	condId = getCondId();
            Collections.addAll(funcs, fg.parseFunctions());
            funcs.add("push eax");
            Collections.addAll(funcs, fd.parseFunctions());
            funcs.add("pop ebx");
            funcs.add("sub eax, ebx");
            funcs.add("jnz FALSE_EGAL_" + condId);
            funcs.add("mov eax, 1");
            funcs.add("jmp END_EGAL" + condId);
            funcs.add("FALSE_EGAL_" + condId + ":");
            funcs.add("mov eax, 0");
            funcs.add("END_EGAL" + condId + ":");
            break;

        case NOT:
        	condId = getCondId();
        	Collections.addAll(funcs, fg.parseFunctions());
            funcs.add("jz TRUE_NOT_" + condId);
            funcs.add("mov eax, 0");
            funcs.add("jmp END_NOT_" + condId);
            funcs.add("TRUE_NOT_" + condId + ":");
            funcs.add("mov eax, 1");
            funcs.add("END_NOT_" + condId + ":");
            break;
            
        case INPUT:
            funcs.add("in eax");
            break;

        case OUTPUT:
        	Collections.addAll(funcs, fg.parseFunctions());
            funcs.add("out eax");
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
	
	public int getCondId() {
		return id++;
	}
}