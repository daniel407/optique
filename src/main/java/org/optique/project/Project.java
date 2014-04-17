package org.optique.project;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;

import org.optique.elements.layer.Layer;
import org.optique.elements.material.Material;
import org.optique.elements.spectrum.Spectrum;
import org.optique.elements.stack.Stack;
import org.optique.math.ComplexNumber;

public class Project {
	
	private List<String> materialFilePaths;
	private List<String> stackFilePaths;
	private String spectraFilePath;
	private Map<String, Material> materialMap;
	private Map<String, Stack> stackMap;
	
	
	public Project(String projectFolderPath){
		initiate();
		allocateFiles(projectFolderPath);
		loadMaterials();
		loadStacks();
		makeCalculations();
	}
	
	private void initiate(){
		materialFilePaths = new ArrayList<String>();
		stackFilePaths = new ArrayList<String>();
		materialMap = new HashMap<String, Material>();
		stackMap = new HashMap<String, Stack>();
		
	}
	

	private void allocateFiles(String projectFolderPath){
		File projectFolder = new File(projectFolderPath);
		File[] files = projectFolder.listFiles();
		for(File file : files){
			try {
				FileReader fr = new FileReader(file);
				BufferedReader br = new BufferedReader(fr);
				String line = br.readLine();
				if(line.charAt(0)!='#')
					continue;
				String[] lineItems = line.split(",");
				
				if(lineItems[0].equals("#Material"))
					materialFilePaths.add(file.getAbsolutePath());
					
				if(lineItems[0].equals("#Stack"))
					stackFilePaths.add(file.getAbsolutePath());
				
				if(lineItems[0].equals("#Spectra")){
					if(spectraFilePath != null)
						JOptionPane.showMessageDialog(null, "Warning. Multiple spectra files.");
					spectraFilePath = file.getAbsolutePath();
				}
				br.close();
				fr.close();
				
			} catch (FileNotFoundException e) {
				JOptionPane.showMessageDialog(null, "File "+file.getName()+" was not found.");
				System.exit(0);
			} catch (IOException e) {
				JOptionPane.showMessageDialog(null, "IO Exception occurred.");
				System.exit(1);
			}
				
			
			
		}
	}
	
	
	private void loadMaterials(){
		for(String filePath : materialFilePaths){
			Material material = new Material(filePath);
			materialMap.put(material.getName(), material);
		}
	}
	
	
	private void loadStacks(){
		for(String filePath : stackFilePaths){
			ArrayList<Layer> layers = new ArrayList<Layer>();
			
			File file = new File(filePath);
			
			try {
				FileReader fr = new FileReader(file);
				BufferedReader br = new BufferedReader(fr);
				
				if(br.readLine().split(",")[0].equals("#Stack")){
					br.readLine();
					String line;
					while((line=br.readLine())!=null){
						String[] lineItems = line.split(",");
						if(!materialMap.containsKey(lineItems[0])){
							JOptionPane.showMessageDialog(null, "Material"+lineItems[0]+" was not found.");
							System.exit(0);
						}
						
						Layer layer = new Layer(lineItems[0],materialMap.get(lineItems[0]), new Double(lineItems[1]));
						layers.add(layer);
					}
					
					br.close();
					fr.close();
					
					stackMap.put(file.getName(), new Stack(layers));
				}
			} catch (FileNotFoundException e) {
				JOptionPane.showMessageDialog(null, "File "+file.getName()+" was not found.");
				System.exit(0);
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(1);
			}
			
			
			
		}
	}
	
	
	private void makeCalculations(){
		for(String stackName : stackMap.keySet()){
			Stack stack = stackMap.get(stackName);
			//TODO further develop this function
			Spectrum spectrum = stack.generateSpectrum("test", Spectrum.TA, 300, 1000, 71, new ComplexNumber(0,0));
			File outputFile = new File("test.png");
			try {
				spectrum.plot(300, 300, outputFile);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		
	}
	
}
