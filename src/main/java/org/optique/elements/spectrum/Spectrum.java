package org.optique.elements.spectrum;

import java.util.Iterator;
import java.util.Set;
import java.util.TreeMap;

import org.optique.exceptions.WavelengthOutOfRangeException;
import org.optique.graphics.XYPlotter;

public class Spectrum extends TreeMap<Double, Double>{

	/**
	 * Represents a spectrum which maps intensity values to the wavelength. 
	 * Intensity values range within 0-1. 
	 * Wavelength values are of the dimension nm.
	 * 
	 * 
	 * 
	 */
	
	
	
	
	
	public static final int RS = 0;
	public static final int RBS = 1;
	public static final int TS = 2;
	
	public static final int RP = 3;
	public static final int RBP = 4;
	public static final int TP = 5;
	
	public static final int RA = 6;
	public static final int RBA = 7;
	public static final int TA = 8;
	
	public static final int PSI = 9;
	public static final int DEL = 10;
	
	
	
	
	
	
	
	
	
	private String name;
	
	
	public Spectrum(){
		
	}
	
	
	public Spectrum(String name){
		this.name=name;
	}
	
	
	/**
	 * If value is not tabulated for a particular wavelength, this function delivers an interpolated value
	 * 
	 * @param wavelength
	 * @return
	 */
	public double getInterpolatedValueAt(double wavelength){
		if(wavelength<this.firstKey() || wavelength>this.lastKey()){
			throw new WavelengthOutOfRangeException();
		}
		double value = 0;
		Set<Double> wavelengths = this.keySet();
		double nextLowerWavelength = this.firstKey();
		double nextHigherWavelength = this.firstKey();
		Iterator<Double> iterator = wavelengths.iterator();
		while(iterator.hasNext()){
			double key = iterator.next();
			nextLowerWavelength = nextHigherWavelength;
			nextHigherWavelength = key;
			if(wavelength >= nextLowerWavelength && wavelength <= nextHigherWavelength){
				double b = (this.get(nextHigherWavelength)-this.get(nextLowerWavelength))/(nextHigherWavelength-nextLowerWavelength);
				double a = this.get(nextLowerWavelength) - b*nextLowerWavelength;
				value = a + b*wavelength;
			}
		}
		return value;
	}
	
	
	public void plot(){
		XYPlotter xyPlotter = new XYPlotter(name, this, name, "wavelength (nm)", "value");
	}
	
	public void print(){
		for(double key : this.keySet()){
			System.out.println(key + " : " + this.get(key));
		}
			
	}
	
	
	
}
