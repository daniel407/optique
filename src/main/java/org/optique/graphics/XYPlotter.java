package org.optique.graphics;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.TreeMap;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class XYPlotter {
	
	public XYPlotter(){
		
	}
	
	public XYPlotter(String seriesName, ArrayList<Double> x, ArrayList<Double> y, String title, String xAxis, String yAxis){
		XYSeries series = new XYSeries(seriesName);
		for(int i=0; i<x.size(); i++){
			series.add(x.get(i), y.get(i));
		}
		XYSeriesCollection dataset = new XYSeriesCollection();
		dataset.addSeries(series);
		JFreeChart chart = ChartFactory.createXYLineChart(title, xAxis, yAxis, 	dataset, PlotOrientation.VERTICAL,	true,true,false);
        XYPlot plot = (XYPlot) chart.getPlot();
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        renderer.setSeriesLinesVisible(0, false);
        renderer.setSeriesLinesVisible(1, false);       
        plot.setRenderer(renderer);
		ChartFrame chartFrame = new ChartFrame(title,chart);
		chartFrame.setSize(300,300);
		chartFrame.setVisible(true);
	}
	
	public XYPlotter(String seriesName, double[] x, double[] y, String title, String xAxis, String yAxis){
		XYSeries series = new XYSeries(seriesName);
		for(int i=0; i<x.length; i++){
			series.add(x[i], y[i]);
		}
		XYSeriesCollection dataset = new XYSeriesCollection();
		dataset.addSeries(series);
		JFreeChart chart = ChartFactory.createXYLineChart(title, xAxis, yAxis, 	dataset, PlotOrientation.VERTICAL,	true,true,false);
        XYPlot plot = (XYPlot) chart.getPlot();
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        renderer.setSeriesLinesVisible(0, false);
        renderer.setSeriesLinesVisible(1, false);       
        plot.setRenderer(renderer);
		ChartFrame chartFrame = new ChartFrame(title,chart);
		chartFrame.setSize(300,300);
		chartFrame.setVisible(true);
	}
	
	public XYPlotter(String seriesName, TreeMap<Double, Double> data, String title, String xAxis, String yAxis, int width, int height, File outputFile) throws IOException{
		XYSeries series = new XYSeries(seriesName);
		for(Double key : data.keySet()){
			series.add(key, data.get(key));
		}
		XYSeriesCollection dataset = new XYSeriesCollection();
		dataset.addSeries(series);
		JFreeChart chart = ChartFactory.createXYLineChart(title, xAxis, yAxis, 	dataset, PlotOrientation.VERTICAL,	true,true,false);
		XYPlot plot = (XYPlot) chart.getPlot();
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        renderer.setSeriesLinesVisible(0, false);
        renderer.setSeriesLinesVisible(1, false);       
        plot.setRenderer(renderer);
        if(outputFile == null){
    		ChartFrame chartFrame = new ChartFrame(title,chart);
    		chartFrame.setSize(width,height);
    		chartFrame.setVisible(true);
        } else {
			ChartUtilities.saveChartAsPNG(outputFile, chart, width, height);
        }

	}
	
	
	
  	public void chartAndOverlay(TreeMap<String,TreeMap<Double,Double>> data, String title, String xAxis, String yAxis){
  		
  		XYSeriesCollection dataset = new XYSeriesCollection();
  		
  		for(String seriesLabel : data.keySet()){
  			XYSeries xySeries = new XYSeries(seriesLabel);
  			TreeMap<Double, Double> seriesData = data.get(seriesLabel);
  			System.out.println(seriesLabel);
  			for(Double x : seriesData.keySet()){
  				xySeries.add(x, seriesData.get(x));
  			}
  			dataset.addSeries(xySeries);
  		}
  		
  		JFreeChart chart = ChartFactory.createXYLineChart(title, xAxis, yAxis, 	dataset, PlotOrientation.VERTICAL,	true,true,false);
        XYPlot plot = (XYPlot) chart.getPlot();
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        for(int i=0; i<data.size(); i++){
        	renderer.setSeriesLinesVisible(i, true); 
        }
        plot.setRenderer(renderer);
		ChartFrame chartFrame = new ChartFrame(title,chart);
		chartFrame.setSize(300,300);

		chartFrame.setVisible(true);
  	}

}
