package ClusterPackage.DataSet;

public class Data {
	
	private Example data[];
	private int numberOfExamples;
	
	public int getNumberOfExamples(){
		return this.numberOfExamples;
	}

	public Example getExample(int exampleIndex){
		return data[exampleIndex];
	}

	public double [][] distance(){
		double[][] distance_matrix = new double[numberOfExamples][numberOfExamples];
		for(int raw=0; raw < numberOfExamples; raw++){
			for(int column=0; column < numberOfExamples; column++){
				if (raw < column)
					distance_matrix[raw][column] = data[raw].distance(data[column]);
				else
					distance_matrix[raw][column] = 0.0;
			}
		}
		return distance_matrix;
	}

	public String toString(){
		String str = "Data examples:\n";
		for(int c=0; c<numberOfExamples; c++){
			str += "\tExample n."+(c+1)+": ";
			str += data[c].toString()+";\n";
		}
		return str;
	}

	public Data(){
		//data
		data = new Example [5];
		Example e=new Example(3);
		e.set(0, 1.0);
		e.set(1, 2.0);
		e.set(2, 0.0);
		data[0]=e;
		
		e=new Example(3);
		e.set(0, 0.0);
		e.set(1, 1.0);
		e.set(2, -1.0);
		data[1]=e;
		
		e=new Example(3);
		e.set(0, 1.0);
		e.set(1, 3.0);
		e.set(2, 5.0);
		data[2]=e;
		
		
		e=new Example(3);
		e.set(0, 1.0);
		e.set(1, 3.0);
		e.set(2, 4.0);
		data[3]=e;
		
		e=new Example(3);
		e.set(0, 2.0);
		e.set(1, 2.0);
		e.set(2, 0.0);
		data[4]=e;
						
		// numberOfExamples		
		 numberOfExamples=5;		 
	}
	
	

	public static void main(String args[]){
		Data trainingSet=new Data();
		System.out.println(trainingSet);
		double [][] distancematrix=trainingSet.distance();
		System.out.println("Distance matrix:\n");
		for(int i=0;i<distancematrix.length;i++) {
			for(int j=0;j<distancematrix.length;j++)
				System.out.print(distancematrix[i][j]+"\t");
			System.out.println("");
		}
		
	}

}
