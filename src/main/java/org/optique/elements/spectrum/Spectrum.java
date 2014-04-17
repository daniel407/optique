package org.optique.elements.spectrum;

import java.io.File;
import java.io.IOException;
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
	
	/**
	 * Plots the spectrum into a png file.
	 * If outputfile is null, the plot will be drawn in a frame.
	 * @param width
	 * @param height
	 * @param outputFile
	 * @throws IOException
	 */
	public void plot(int width, int height, File outputFile) throws IOException{
		XYPlotter xyPlotter = new XYPlotter(name, this, name, "wavelength (nm)", "value", width, height, outputFile);
	}
	
	
	
	public void print(){
		for(double key : this.keySet()){
			System.out.println(key + " : " + this.get(key));
		}
			
	}
	
	
	public static int spectraCode(String polarization, String spectrumType){
		
		if(polarization.equals("senkrecht")){
			if(spectrumType.equals("transmission"))
				return Spectrum.TS;
			if(spectrumType.equals("front reflection"))
				return Spectrum.RS;
			if(spectrumType.equals("back reflection"))
				return Spectrum.RBS;
			
		}
		
		if(polarization.equals("parallel")){
			if(spectrumType.equals("transmission"))
				return Spectrum.TP;
			if(spectrumType.equals("front reflection"))
				return Spectrum.RP;
			if(spectrumType.equals("back reflection"))
				return Spectrum.RBP;
		}
		
		if(polarization.equals("average")){
			if(spectrumType.equals("transmission"))
				return Spectrum.TA;
			if(spectrumType.equals("front reflection"))
				return Spectrum.RA;
			if(spectrumType.equals("back reflection"))
				return Spectrum.RBA;
		}
		
		
		return -1;
	}
	
	
	
}
