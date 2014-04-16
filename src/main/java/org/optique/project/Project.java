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

import org.optique.elements.material.Material;

public class Project {
	
	private List<String> materialFilePaths;
	private List<String> stackFilePaths;
	private String spectraFilePath;
	private Map<String, Material> materialMap;
	
	
	public Project(String projectFolderPath){
		initiate();
		allocateFiles(projectFolderPath);
		
		
		
		
	}
	
	private void initiate(){
		materialFilePaths = new ArrayList<String>();
		stackFilePaths = new ArrayList<String>();
		materialMap = new HashMap<String, Material>();
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
	
	
}
