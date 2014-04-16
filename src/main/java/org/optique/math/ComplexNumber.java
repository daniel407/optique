package org.optique.math;

public class ComplexNumber {

	/**
	 * Represents a complex number with real part x and imagingary part y.
	 * z = x + iy
	 * 
	 */
	
	
	private double x;
	private double y;
	
	
	public ComplexNumber(double x, double y){
		this.x=x;
		this.y=y;
	}
	
	public double getRealPart(){
		return this.x;
	}
	
	public double getImaginaryPart(){
		return this.y;
	}
	
	/**
	 * Computes exp(z) and returns it as a complex number
	 * @return
	 */
	
	public ComplexNumber exponent(){
		double x = Math.exp(this.getRealPart())*Math.cos(this.getImaginaryPart());
		double y = Math.exp(this.getRealPart())*Math.sin(this.getImaginaryPart());
		return new ComplexNumber(x,y);
	}
	
	public double absoluteValue(){
		return Math.sqrt(this.x*this.x + this.y*this.y);
	}
	
	public ComplexNumber add(ComplexNumber z){
		double newReal = this.x + z.getRealPart();
		double newImaginary = this.y + z.getImaginaryPart();
		return new ComplexNumber(newReal, newImaginary);
	}
	
	public ComplexNumber subtract(ComplexNumber z){
		double newReal = this.x - z.getRealPart();
		double newImaginary = this.y - z.getImaginaryPart();
		return new ComplexNumber(newReal, newImaginary);
	}
	
	public ComplexNumber multiply(ComplexNumber z){
		double newReal = this.x*z.getRealPart() - this.y*z.getImaginaryPart();
		double newImaginary = this.y*z.getRealPart() + this.x*z.getImaginaryPart();
		return new ComplexNumber(newReal, newImaginary);
	}
	
	public ComplexNumber divide(ComplexNumber z){
		double newReal = (this.x*z.getRealPart() + this.y*z.getImaginaryPart())/(z.getRealPart()*z.getRealPart() + z.getImaginaryPart()*z.getImaginaryPart());
		double newImaginary = (this.y*z.getRealPart()-this.x*z.getImaginaryPart())/(z.getRealPart()*z.getRealPart() + z.getImaginaryPart()*z.getImaginaryPart());
		return new ComplexNumber(newReal, newImaginary);
	}
	
	public ComplexNumber complexConjugate(){
		return new ComplexNumber(this.x, -this.y);
	}
	
	public ComplexNumber sine(){
		double newReal = Math.sin(this.x)*Math.cosh(this.y);
		double newImaginary = Math.cos(this.x)*Math.sinh(this.y);
		return new ComplexNumber(newReal, newImaginary);
	}
	
	public ComplexNumber cosine(){
		double newReal = Math.cos(this.x)*Math.cosh(this.y);
		double newImaginary = -1*Math.sin(this.x)*Math.sinh(this.y);
		return new ComplexNumber(newReal, newImaginary);
	}
	
	public double getPhase(){
		return Math.atan(this.y/this.x);
	}
	
	public ComplexNumber ln(){
		double newReal = Math.log(this.absoluteValue());
		double newImaginary = this.getPhase();
		return new ComplexNumber(newReal, newImaginary);		
	}
	
	public ComplexNumber asine(){
		ComplexNumber oneMinusZsquared = new ComplexNumber(1,0).subtract(this.multiply(this));
		ComplexNumber a = new ComplexNumber(0,oneMinusZsquared.getPhase()*0.5).exponent().multiply(new ComplexNumber(Math.sqrt(oneMinusZsquared.absoluteValue()),0));
		ComplexNumber b = this.multiply(new ComplexNumber(0,1));
		ComplexNumber c = (a.add(b)).ln().divide(new ComplexNumber(0,1));
		return c;
	}
	
	public void print(){
		System.out.println("z = "+this.x+" + i"+this.y);
	}
	
}
