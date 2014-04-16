package org.optique.elements.material;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import org.optique.elements.spectrum.Dispersion;


/**
 * This class represents a material with optical characteristics such as refractive index and extinction coefficient.
 * The wavelength values used in the dispersion of n and k are of the dimension nm.
 * 
 * @author 
 *
 */

public class Material {
	
	protected String name;
	protected Dispersion refractive_indices;
	protected Dispersion extinction_coefficients;
	
	public Material(String materialFilePath){
		File file = new File(materialFilePath);
		
		refractive_indices = new Dispersion();
		extinction_coefficients = new Dispersion();
		
		try {
			FileReader fr = new FileReader(file);
			BufferedReader br = new BufferedReader(fr);
			String firstItem = br.readLine().split(",")[0];
			if(firstItem.equals("#Material")){
				br.readLine();
				String line = null;
				while((line=br.readLine())!=null){
					String[] lineItems = line.split(",");
					refractive_indices.put(new Double(lineItems[0]), new Double(lineItems[1]));
					extinction_coefficients.put(new Double(lineItems[0]), new Double(lineItems[2]));
				}
				this.name = file.getName().split("\\.")[0];
			}
			br.close();
			fr.close();
		} catch (FileNotFoundException e) {
			JOptionPane.showMessageDialog(null, "File "+file.getName()+" was not found.");
			System.exit(0);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	
	}
	
	public Dispersion getRefractiveIndexDispersion(){
		return this.refractive_indices;
	}
	
	public Dispersion getExtinctionCoefficientDispersion(){
		return this.extinction_coefficients;
	}

	public String getName(){
		return this.name;
	}
}
