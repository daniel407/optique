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
import org.optique.miscellaneous.Names;

public class Project {
	
	private String projectFolderPath;
	private List<String> materialFilePaths;
	private List<String> experimentFilePaths;
	private Map<String, Material> materialMap;
	private Map<Integer, Stack> stackMap;
	
	
	public Project(String projectFolderPath){
		this.projectFolderPath=projectFolderPath;
		initiate();
		allocateFiles(projectFolderPath);
		loadMaterials();
		runExperiments();
		//loadStacks();
		//makeCalculations();
	}
	
	private void initiate(){
		materialFilePaths = new ArrayList<String>();
		experimentFilePaths = new ArrayList<String>();
		materialMap = new HashMap<String, Material>();
		stackMap = new HashMap<Integer, Stack>();
		
	}
	

	private void allocateFiles(String projectFolderPath){
		File projectFolder = new File(projectFolderPath);
		File[] files = projectFolder.listFiles();
		for(File file : files){
			try {
				FileReader fr = new FileReader(file);
				BufferedReader br = new BufferedReader(fr);
				String line = br.readLine();
				if(line == null || line.charAt(0)!='#')
					continue;
				String[] lineItems = line.split(",");
				
				if(lineItems[0].equals(Names.MATERIAL_START))
					materialFilePaths.add(file.getAbsolutePath());
					
				if(lineItems[0].equals(Names.EXPERIMENT_START))
					experimentFilePaths.add(file.getAbsolutePath());
				
				
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
	
	
	private void runExperiments(){
		for(String filePath : experimentFilePaths){
			try {
				runExperiment(filePath);
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
	}
	
	
	private void runExperiment(String filePath) throws IOException{
		File file = new File(filePath);
		FileReader fr = new FileReader(file);
		BufferedReader br = new BufferedReader(fr);
		String line = br.readLine();
		if(!line.split(",")[0].equals(Names.EXPERIMENT_START))
			return;
		
		while((line = br.readLine()) != null){
			if(line.split(",").length>0 && line.split(",")[0].equals(Names.STACKS_START))
				loadStacks(br, line);
			
			if(line.split(",").length>0 && line.split(",")[0].equals(Names.CALCULATIONS_START)){
				runCalculations(br, line);
			}
		}
		
		br.close();
		fr.close();
	}
	
	private void loadStacks(BufferedReader br, String line) throws IOException{
		String[] stackNames = line.split(",");
		for(int i=1; i<stackNames.length; i++){
			if(stackNames[i].length()>0)
				stackMap.put(i, new Stack(stackNames[i]));
		}
		
		line=br.readLine();
		
		while((line=br.readLine()).length()>1 && !line.split(",")[0].equals(Names.STACKS_END)){
			String[] lineItems = line.split(",");
			for(int i=1; i<lineItems.length; i++){

				if(!materialMap.containsKey(lineItems[0])){
					JOptionPane.showMessageDialog(null, "Material "+lineItems[0]+" was not found.");
					System.exit(0);
				}
				
				if(lineItems[i].length()>0){
					Layer layer = new Layer(lineItems[0],materialMap.get(lineItems[0]),new Double(lineItems[i]));
					stackMap.get(i).addLayer(layer);
				}
			}
		}
		
		
	}
	
	
	private void runCalculations(BufferedReader br, String line) throws IOException{
		Map<Integer, String> typeMap = new HashMap<Integer, String>();
		Map<Integer, String> polarizationMap = new HashMap<Integer, String>();
		Map<Integer, Double> angleMap = new HashMap<Integer, Double>();
		Map<Integer, Double> wlStartMap = new HashMap<Integer, Double>();
		Map<Integer, Double> wlEndMap = new HashMap<Integer, Double>();
		Map<Integer, Integer> numberOfStepMap = new HashMap<Integer, Integer>();
		Map<Integer, String> targetFileMap = new HashMap<Integer, String>();
		
		line=br.readLine();
		while(line != null && line.length()>1 && !line.split(",")[0].equals(Names.CALCULATIONS_END)){
			
			String[] lineItems = line.split(",");
			for(int i=1; i<lineItems.length; i++){
				
				if(stackMap.containsKey(i)){
					if(lineItems[0].equals(Names.SPECTRUMTYPE))
						typeMap.put(i,lineItems[i]);
				}
				
				if(stackMap.containsKey(i)){
					if(lineItems[0].equals(Names.POLARIZATION))
						polarizationMap.put(i,lineItems[i]);
				}
				
				if(stackMap.containsKey(i)){
					if(lineItems[0].equals(Names.ANGLE_DEG))
						angleMap.put(i,new Double(lineItems[i]));
				}
				
				if(stackMap.containsKey(i)){
					if(lineItems[0].equals(Names.WAVELENGTH_START))
						wlStartMap.put(i,new Double(lineItems[i]));
				}
				
				if(stackMap.containsKey(i)){
					if(lineItems[0].equals(Names.WAVELENGTH_END))
						wlEndMap.put(i,new Double(lineItems[i]));
				}
				
				if(stackMap.containsKey(i)){
					if(lineItems[0].equals(Names.NUMBER_OF_STEPS))
						numberOfStepMap.put(i,new Integer(lineItems[i]));
				}
				
				if(stackMap.containsKey(i)){
					if(lineItems[0].equals(Names.RAW_DATA_FILE))
						targetFileMap.put(i,projectFolderPath+"/"+lineItems[i]);
				}
			}
			line=br.readLine();
			
		}
		
		for(int key : stackMap.keySet()){
			stackMap.get(key).writeSpectrumToFile(typeMap.get(key), polarizationMap.get(key), angleMap.get(key), wlStartMap.get(key), wlEndMap.get(key), numberOfStepMap.get(key), targetFileMap.get(key));
		}
	}
	
	/*
	
	
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
	
	
	*/
	


	
}