package it.polimi.modaclouds.space4cloud.milp;

public class MILPException extends Exception {
	public MILPException(String string) {
		super(string);
	}
	
	public MILPException(String string, Throwable t) {
		super(string, t);
	}

	private static final long serialVersionUID = -531589505799916253L;
}
