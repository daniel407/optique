package org.optique.elements.layer;

import org.optique.elements.material.Material;
import org.optique.exceptions.WavelengthOutOfRangeException;
import org.optique.math.ComplexNumber;


/**
 * Represents a layer of a certain thickness of a material.
 * The thickness value has a dimension of nm.
 * @author 
 *
 */

public class Layer {

	protected String name;
	protected Material material;
	protected double thickness;
	
	
	
	public Layer(){
		
	}
	
	
	public Layer(String name, Material material, double thickness){
		this.material=material;
		this.thickness=thickness;
		this.name=name;
	}
	
	
	public double getThickness(){
		return thickness;
	}
	
	
	
	
	
	
	public double getRefractiveIndexAt(double wavelength){
		return this.material.getRefractiveIndexDispersion().getInterpolatedValueAt(wavelength);
	}
	
	
	public double getExtinctionCoefficientAt(double wavelength){
		return this.material.getExtinctionCoefficientDispersion().getInterpolatedValueAt(wavelength);
	}
	
	
	public ComplexNumber getComplexRefractiveIndexAt(double wavelength){
		return new ComplexNumber(this.getRefractiveIndexAt(wavelength), -this.getExtinctionCoefficientAt(wavelength));
	}
	
	
	public ComplexNumber snelliusAngle(Layer adjacentLayer, double wavelength, ComplexNumber inboundAngle){
		ComplexNumber n0 = this.getComplexRefractiveIndexAt(wavelength);
		ComplexNumber n1 = adjacentLayer.getComplexRefractiveIndexAt(wavelength);
		return n0.divide(n1).multiply(inboundAngle.sine()).asine();
	}
	
	
	public ComplexNumber fresnel_r_senkrecht(Layer adjacentLayer, double wavelength, ComplexNumber inboundAngle){
		ComplexNumber n0 = this.getComplexRefractiveIndexAt(wavelength);
		ComplexNumber n1 = adjacentLayer.getComplexRefractiveIndexAt(wavelength);		

		ComplexNumber outboundAngle = this.snelliusAngle(adjacentLayer, wavelength, inboundAngle);

		ComplexNumber top = n0.multiply(inboundAngle.cosine()).subtract(n1.multiply(outboundAngle.cosine()));
		ComplexNumber bottom = n0.multiply(inboundAngle.cosine()).add(n1.multiply(outboundAngle.cosine()));
		
		return top.divide(bottom);
	}
	
	
	public ComplexNumber fresnel_r_parallel(Layer adjacentLayer, double wavelength, ComplexNumber inboundAngle){
		ComplexNumber n0 = this.getComplexRefractiveIndexAt(wavelength);
		ComplexNumber n1 = adjacentLayer.getComplexRefractiveIndexAt(wavelength);		

		ComplexNumber outboundAngle = this.snelliusAngle(adjacentLayer, wavelength, inboundAngle);
		
		ComplexNumber top = n1.multiply(inboundAngle.cosine()).subtract(n0.multiply(outboundAngle.cosine()));
		ComplexNumber bottom = n1.multiply(inboundAngle.cosine()).add(n0.multiply(outboundAngle.cosine()));
		return top.divide(bottom);
	}
	
	
	public ComplexNumber fresnel_t_senkrecht(Layer adjacentLayer, double wavelength, ComplexNumber inboundAngle){
		ComplexNumber n0 = this.getComplexRefractiveIndexAt(wavelength);
		ComplexNumber n1 = adjacentLayer.getComplexRefractiveIndexAt(wavelength);		

		ComplexNumber outboundAngle = this.snelliusAngle(adjacentLayer, wavelength, inboundAngle);
		
		ComplexNumber top = new ComplexNumber(2,0).multiply(inboundAngle.cosine()).multiply(n0);
		ComplexNumber bottom = n0.multiply(inboundAngle.cosine()).add(n1.multiply(outboundAngle.cosine()));
		return top.divide(bottom);
	}
	
	
	public ComplexNumber fresnel_t_parallel(Layer adjacentLayer, double wavelength, ComplexNumber inboundAngle){
		ComplexNumber n0 = this.getComplexRefractiveIndexAt(wavelength);
		ComplexNumber n1 = adjacentLayer.getComplexRefractiveIndexAt(wavelength);		

		ComplexNumber outboundAngle = this.snelliusAngle(adjacentLayer, wavelength, inboundAngle);
		
		ComplexNumber top = new ComplexNumber(2,0).multiply(inboundAngle.cosine()).multiply(n0);
		ComplexNumber bottom = n1.multiply(inboundAngle.cosine()).add(n0.multiply(outboundAngle.cosine()));
		return top.divide(bottom);
	}
	
	
	public ComplexNumber getPhaseShift(double wavelength, ComplexNumber angleInLayer){

		ComplexNumber n0 = this.getComplexRefractiveIndexAt(wavelength);
		ComplexNumber d0 = new ComplexNumber(this.getThickness()*1e-9,0);
		
		ComplexNumber top = new ComplexNumber(2,0).multiply(new ComplexNumber(Math.PI,0)).multiply(d0).multiply(angleInLayer.cosine()).multiply(n0);
		ComplexNumber bottom = new ComplexNumber(wavelength*1e-9,0);
		return top.divide(bottom);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
}
