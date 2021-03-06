package org.optique.elements.stack;

import java.io.IOException;
import java.util.ArrayList;

import org.optique.elements.layer.Layer;
import org.optique.elements.spectrum.Spectrum;
import org.optique.math.ComplexMatrix;
import org.optique.math.ComplexNumber;

public class Stack {

	public static final int SENKRECHT = 1;
	public static final int PARALLEL = 2;
	public static final int AVERAGE = 3;
	
	
	private String name;
	private Layer[] layers;
	private ComplexNumber[] angles;
	
	
	public Stack(){
		layers = new Layer[0];
	}
	
	public Stack(String name){
		this.name=name;
		layers = new Layer[0];
		angles = new ComplexNumber[layers.length];
	}
	
	public Stack(String name, Layer[] layers){
		this.name=name;
		this.layers=layers;
		angles = new ComplexNumber[layers.length];
	}
	
	public Stack(Layer[] layers){
		this.layers=layers;
		angles = new ComplexNumber[layers.length];
	}
	
	public Stack(ArrayList<Layer> layers){
		this.layers = layers.toArray(new Layer[0]);
		angles = new ComplexNumber[this.layers.length];
	}
	
	
	
	
	
	
	public void setName(String name){
		this.name=name;
	}
	
	
	public String getName(String name){
		return this.name;
	}
	
	
  	public int getNumberOfLayers(){
		return layers.length;
	}
	
	
	public void computeLayerAngles(ComplexNumber inboundAngle, double wavelength){
		angles[0]=inboundAngle;
		for(int i=1; i<angles.length; i++){
			angles[i] = layers[i-1].snelliusAngle(layers[i], wavelength, angles[i-1]);
		}
		
		
	}
	
	
	public ComplexMatrix getDM(int layerIndex, int polarization, double wavelength, ComplexNumber angle){
		Layer l0 = layers[layerIndex];
		Layer l1 = layers[layerIndex+1];
		ComplexNumber t = null;
		if(polarization == Stack.SENKRECHT)
			t = l0.fresnel_t_senkrecht(l1, wavelength, angle);
		if(polarization == Stack.PARALLEL)
			t = l0.fresnel_t_parallel(l1, wavelength, angle);
		ComplexNumber[][] dmData = new ComplexNumber[2][2];
		dmData[0][0] = new ComplexNumber(1,0).divide(t);
		dmData[1][1] = new ComplexNumber(1,0).divide(t);
		dmData[0][1] = new ComplexNumber(0,0);
		dmData[1][0] = new ComplexNumber(0,0);
		return new ComplexMatrix(dmData);
	}
	
	
	public ComplexMatrix getDP(int layerIndex, int polarization, double wavelength, ComplexNumber angle){
		Layer l0 = layers[layerIndex];
		Layer l1 = layers[layerIndex+1];
		ComplexNumber r = null;
		if(polarization == Stack.SENKRECHT)
			r = l0.fresnel_r_senkrecht(l1, wavelength, angle);
		if(polarization == Stack.PARALLEL)
			r = l0.fresnel_r_parallel(l1, wavelength, angle);
		ComplexNumber[][] dpData = new ComplexNumber[2][2];
		dpData[0][0] = new ComplexNumber(1,0);
		dpData[1][1] = new ComplexNumber(1,0);
		dpData[0][1] = r;
		dpData[1][0] = r;
		return new ComplexMatrix(dpData);
	}
	
	
	public ComplexMatrix getP(int layerIndex, double wavelength, ComplexNumber angle){
		Layer l0 = layers[layerIndex];
		ComplexNumber delta = l0.getPhaseShift(wavelength, angle);
		ComplexNumber[][] pData = new ComplexNumber[2][2];
		pData[0][0] = new ComplexNumber(0,1).multiply(delta).exponent();
		pData[1][1] = new ComplexNumber(0,-1).multiply(delta).exponent();
		pData[0][1] = new ComplexNumber(0,0);
		pData[1][0] = new ComplexNumber(0,0);
		return new ComplexMatrix(pData);
	}
	
	
	public ComplexMatrix getCoherentTransferMatrix(int polarization, double wavelength, ComplexNumber angle, int fromLayer, int toLayer){
		this.computeLayerAngles(angle, wavelength);
		
		Layer[] subsetLayers = new Layer[toLayer-fromLayer+1];
		ComplexNumber[] subsetAngles = new ComplexNumber[toLayer-fromLayer+1];
		int j=0;
		for(int i=fromLayer; i<=toLayer; i++){
			subsetLayers[j]=layers[i];
			subsetAngles[j]=angles[i];
			j++;
		}
		
		ComplexMatrix transferMatrix = this.getDP(toLayer-1, polarization, wavelength, angles[toLayer-1]);

		
		for(int i=subsetLayers.length-2; i>0; i--){
			transferMatrix = this.getDM(i, polarization, wavelength, subsetAngles[i]).multiply(transferMatrix);
			transferMatrix = this.getP(i, wavelength, subsetAngles[i]).multiply(transferMatrix);
			transferMatrix = this.getDP(i-1, polarization, wavelength, subsetAngles[i-1]).multiply(transferMatrix);
		}
		
		
		transferMatrix = this.getDM(fromLayer, polarization, wavelength, angles[fromLayer]).multiply(transferMatrix);

		return transferMatrix;
	}
	
	
	public ComplexMatrix getTintMatrix(int polarization, double wavelength, ComplexNumber angle, int fromLayer, int toLayer){
		ComplexMatrix transferMatrix = this.getCoherentTransferMatrix(polarization, wavelength, angle, fromLayer, toLayer);
		
		ComplexNumber r = transferMatrix.getValueAt(1, 0).divide(transferMatrix.getValueAt(0, 0));
		ComplexNumber rb = new ComplexNumber(-1,0).multiply(transferMatrix.getValueAt(0, 1).divide(transferMatrix.getValueAt(0, 0)));
		ComplexNumber t = new ComplexNumber(1,0).divide(transferMatrix.getValueAt(0, 0));
		ComplexNumber tb = transferMatrix.determinant2D().divide(transferMatrix.getValueAt(0, 0));
		
		
		ComplexNumber[][] tintData = new ComplexNumber[2][2];
		tintData[0][0] = new ComplexNumber(1/Math.pow(t.absoluteValue(),2),0);
		tintData[0][1] = new ComplexNumber(-Math.pow(rb.absoluteValue(),2)/Math.pow(t.absoluteValue(), 2),0);
		tintData[1][0] = new ComplexNumber(Math.pow(r.absoluteValue(),2)/Math.pow(t.absoluteValue(), 2),0);
		tintData[1][1] = new ComplexNumber((Math.pow(t.multiply(tb).absoluteValue(), 2)-Math.pow(r.multiply(rb).absoluteValue(), 2))/Math.pow(t.absoluteValue(),2),0);
		
		return new ComplexMatrix(tintData);
	}
	
	
	public ComplexMatrix getPintMatrix(double wavelength, ComplexNumber angle, int layerIndex){
		Layer l0 = layers[layerIndex];
		ComplexNumber delta = l0.getPhaseShift(wavelength, angle);
		ComplexNumber[][] pdata = new ComplexNumber[2][2];
		pdata[0][0] = new ComplexNumber(Math.pow(new ComplexNumber(0,1).multiply(delta).exponent().absoluteValue(), 2),0);
		pdata[1][1] = new ComplexNumber(Math.pow(new ComplexNumber(0,-1).multiply(delta).exponent().absoluteValue(), 2),0);
		pdata[0][1] = new ComplexNumber(0,0);
		pdata[1][0] = new ComplexNumber(0,0);
		return new ComplexMatrix(pdata);
		
	}
	
	
	public ComplexMatrix getTransferMatrixOfStackWithIncoherentSubstrate(int polarization, double wavelength, ComplexNumber angle){
		int substrateLayerIndex = this.layers.length-2;
		ComplexMatrix A = this.getTintMatrix(polarization, wavelength, angle, substrateLayerIndex, substrateLayerIndex+1);
		ComplexMatrix B = this.getPintMatrix(wavelength, angle, substrateLayerIndex);
		ComplexMatrix C = this.getTintMatrix(polarization, wavelength, angle, 0, substrateLayerIndex);
		ComplexMatrix iTM = C.multiply(B.multiply(A));
		return iTM;
	}
	
	
	public double[] getReflectionsAndTransmissionCoefficientsFromStackWithIncoherentSubstrate(double wavelength, ComplexNumber angle){
		double[] coefficients = new double[11];
		ComplexMatrix iTM = null;
		
		
		//SENKRECHT
		iTM = getTransferMatrixOfStackWithIncoherentSubstrate(SENKRECHT, wavelength, angle);
		coefficients[Spectrum.RS] = iTM.getValueAt(1, 0).absoluteValue()/iTM.getValueAt(0, 0).absoluteValue();
		coefficients[Spectrum.RBS] = iTM.getValueAt(0, 1).absoluteValue()/iTM.getValueAt(0, 0).absoluteValue();
		coefficients[Spectrum.TS] = 1/iTM.getValueAt(0, 0).absoluteValue();
		
		//PARALLEL
		iTM = getTransferMatrixOfStackWithIncoherentSubstrate(PARALLEL, wavelength, angle);
		coefficients[3] = iTM.getValueAt(1, 0).absoluteValue()/iTM.getValueAt(0, 0).absoluteValue();
		coefficients[4] = iTM.getValueAt(0, 1).absoluteValue()/iTM.getValueAt(0, 0).absoluteValue();
		coefficients[5] = 1/iTM.getValueAt(0, 0).absoluteValue();
		

		coefficients[6] = (coefficients[0]+coefficients[3])/2;
		coefficients[7] = (coefficients[1]+coefficients[4])/2;
		coefficients[8] = (coefficients[2]+coefficients[5])/2;
		
		
		//SE
		ComplexMatrix tmp = getCoherentTransferMatrix(PARALLEL, wavelength, angle, 0, this.getNumberOfLayers()-2);
		ComplexNumber rp = tmp.getValueAt(1, 0).divide(tmp.getValueAt(0, 0));
		ComplexMatrix tms = getCoherentTransferMatrix(SENKRECHT, wavelength, angle, 0, this.getNumberOfLayers()-2);
		ComplexNumber rs = tms.getValueAt(1, 0).divide(tms.getValueAt(0, 0));
		
		
		ComplexNumber rho = rp.divide(rs);
		double psi = Math.atan(Math.sqrt(Math.pow(rho.getRealPart(),2) + Math.pow(rho.getImaginaryPart(),2)))*180/Math.PI;
		double del = Math.asin((rho.divide(new ComplexNumber(psi,0))).getImaginaryPart());
		coefficients[Spectrum.PSI] = psi;
		coefficients[Spectrum.DEL] = del;
		
		
		return coefficients;
	}
	
	
	public Spectrum generateSpectrum(String name, int spectrumType, double wavelengthStart, double wavelengthEnd, int numberOfSteps, ComplexNumber angle){
		Spectrum spectrum = new Spectrum(name);
		double stepSize = (wavelengthEnd-wavelengthStart)/(numberOfSteps-1);
		for(int i=0; i<numberOfSteps; i++){
			double wavelength = wavelengthStart + i*stepSize;
			double[] coefficients = this.getReflectionsAndTransmissionCoefficientsFromStackWithIncoherentSubstrate(wavelength, angle);
			spectrum.put(wavelengthStart+i*stepSize, coefficients[spectrumType]);
		}
		return spectrum;
	}
	
	
	
	public void addLayer(Layer layer){
		Layer[] newLayers = new Layer[layers.length+1];
		for(int i=0; i<layers.length; i++){
			newLayers[i]=layers[i];
		}
		newLayers[newLayers.length-1]=layer;
		this.layers=newLayers;
		angles = new ComplexNumber[this.layers.length];
	}
	
	
	public void writeSpectrumToFile(String spectrumType, String polarization, double angleOfIncidence, double wavelengthStart, double wavelengthEnd, int numberOfSteps, String targetFilePath) throws IOException{
		String name = this.name + " " + spectrumType + " " + polarization + " " + angleOfIncidence + "deg";
		
		int spectrumCode = Spectrum.spectraCode(spectrumType, polarization);
		
		Spectrum spectrum = this.generateSpectrum(name, spectrumCode, wavelengthStart, wavelengthEnd, numberOfSteps, new ComplexNumber(Math.PI*angleOfIncidence/180,0));
		
		spectrum.printToFile(targetFilePath);
		
	}
	
	
	
}
