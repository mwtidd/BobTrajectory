package com.team319.pid;

public class Pid {
	private double p;
	private double i;
	private double d;

	public Pid(){

	}

	public Pid(double p, double i, double d){
		this.p = p;
		this.i = i;
		this.d = d;
	}

	public double getP() {
		return p;
	}

	public double getI() {
		return i;
	}

	public double getD() {
		return d;
	}
}
