package feature;

import java.io.Serializable;

//Class to represent a data point as a feature the name of the method as a string and its Feature vector from the Wordnet
public class Feature implements Serializable {
	private String Label;
	private double[] LabelFeature;
	//Constructor
	public Feature(String str1,double[] DoubleArray){
		Label=str1;
		LabelFeature=new double[DoubleArray.length];
		LabelFeature=DoubleArray;
	}
	//Get-Set methods
	public String getLabel(){return Label;};
	public void setLabel(String label){Label=label;};
	public double[] getLabelFeature(){return LabelFeature;};
}