package org.optique.math;

public class ComplexMatrix {

	/**
	 * Represents a matrix of complexNumbers.
	 */
	
	
	private ComplexNumber[][] data;
	
	public ComplexMatrix(ComplexNumber[][] data){
		this.data=data;
	}
	
	public int getRowCount(){
		return data.length;
	}
	
	public int getColumnCount(){
		return data[0].length;
	}
	
	public ComplexNumber getValueAt(int row, int column){
		return this.data[row][column];
	}
	
	public void setValueAt(int row, int column, ComplexNumber value){
		this.data[row][column] = value;
	}
	
	public ComplexMatrix multiply(ComplexMatrix matrix){
		ComplexNumber[][] resultData = new ComplexNumber[this.getRowCount()][matrix.getColumnCount()];
		ComplexMatrix result = new ComplexMatrix(resultData);
		
		for(int i=0; i<this.getRowCount(); i++){
			for(int k=0; k<matrix.getColumnCount(); k++){
				ComplexNumber resultElement = new ComplexNumber(0,0);
				for(int j=0; j<this.getColumnCount(); j++){
					resultElement = resultElement.add(this.getValueAt(i,j).multiply(matrix.getValueAt(j, k)));
				}
				result.setValueAt(i, k, resultElement);
			}
		}
		return result;
	}

	
	public void print(){
		for(int i=0; i<data.length; i++){
			for(int j=0; j<data[i].length; j++){
				System.out.print(i+","+j+": ");
				data[i][j].print();
			
			}
		}
	}
	
	public static ComplexMatrix unity(int size){
		ComplexNumber[][] data = new ComplexNumber[size][size];
		for(int i=0;i<size;i++){
			for(int j=0; j<size; j++){
				if(i==j)
					data[i][j]=new ComplexNumber(1,0);
				else
					data[i][j]=new ComplexNumber(0,0);
			}
		}
		
		return new ComplexMatrix(data);
	}
	
	/**
	 * Calculates the correct determinant if the Matrix is 2-dimensional.
	 * @return
	 */
	public ComplexNumber determinant2D(){
		ComplexNumber A = this.getValueAt(0,0).multiply(this.getValueAt(1,1));
		ComplexNumber B = this.getValueAt(0,1).multiply(this.getValueAt(1,0));
		return A.subtract(B);
	}
	
}
