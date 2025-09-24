import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class asst3_ugartere {
	
	public static void main(String[] args) {
		//Start the program and set Buffered writers and readers appropriately
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter("output.txt"));
			BufferedReader br = new BufferedReader(new FileReader("input.txt"));
			BufferedReader br_check = new BufferedReader(new FileReader("input.txt"));
			BufferedReader br2 = new BufferedReader(new FileReader("config.txt"));
			
		//Check if user specified input file
			if(args.length == 0) {
				br = new BufferedReader(new FileReader("input.txt"));
				bw.write("No input file specified. Using default: input.txt");
				bw.write("\n");
				bw.write("Input file: input.txt");
				bw.write("\n");
			}
			else {
				br = new BufferedReader(new FileReader(args[0]));
				bw.write("Input file: " + args[0]);
				bw.write("\n");
			}
			//Start writing on the output doc
				bw.write("Output file: output.txt");
				bw.write("\n");
			
				//read the config file and set the parallel execution to true or false
				String line = br2.readLine();
				String[] line_list = line.split("=");
				String execution_type = line_list[1];
				if(execution_type.equals("true")) {
					bw.write("Execution mode: parallel");
					bw.write("\n");
				}
				else {
					bw.write("Execution mode: sequential");
					bw.write("\n");
				}
				
				bw.write("\n");
				
				//Error handling for when matrix is not square
				if (Matrix.check_matrix(br_check) == false) {
					bw.write("Error: Matrix must be square.");
					bw.close();
					throw new IllegalArgumentException("Error: Matrix must be square.");
					
				}
				
				bw.write("Matrix A:");
				bw.write("\n");
				
				//Initilize matrix a by reading from the input file
				double[][] A = Matrix.init_A_Matrix(br);
				
				for(int i = 0; i < A.length ; i++) {
					for(int j = 0; j < A.length ; j++) {
						bw.write(String.format("%.1f",A[i][j]));
						bw.write(" ");
					}
				bw.write("\n");
				}
				
				//Initialize matrix L and U
				double[][] L = Matrix.init_Matrix(A.length);
				double[][] U = Matrix.init_Matrix(A.length);
				
				bw.write("\n");
				
				//Calculate sequentially or in parallel depending on the config file input
				if(execution_type.equals("true")) {
					bw.close();
					Execution_Parallel.calculating(L,U,A);
				}
				else {
					bw.close();
					Execution.calculating(L,U,A);
				}
				
				bw = new BufferedWriter(new FileWriter("output.txt",true));
				
				//Calculate for the Difference matrix
				double [][] D = Execution.calculateDotProduct(L, U);
				double[][] R = Execution.substract_matrix(A, D);
				
				
				//Start writing final results to the output file
				bw.write("\n");
				bw.write("Final Matrix L:");
				bw.write("\n");
				
				for(int i = 0; i < A.length ; i++) {
					for(int j = 0; j < A.length ; j++) {
						bw.write(String.format("%.1f",L[i][j]));
						bw.write(" ");
					}
				bw.write("\n");
				}
				
				bw.write("\n");
				
				bw.write("Final Matrix U:");
				bw.write("\n");
				
				for(int i = 0; i < A.length ; i++) {
					for(int j = 0; j < A.length ; j++) {
						bw.write(String.format("%.1f",U[i][j]));
						bw.write(" ");
					}
				bw.write("\n");
				}
				
				bw.write("\n");
				
				bw.write("Difference Matrix (A - LU):");
				
				bw.write("\n");
				
				
				for(int i = 0; i < A.length ; i++) {
					for(int j = 0; j < A.length ; j++) {
						bw.write(String.format("%.4f",R[i][j]));
						bw.write(" ");
					}
				 bw.write("\n");
				}
				
				//Calculate and write the tolerance
				bw.write("\n");
				bw.write("Tolerance (difference between A and LU): ");
				bw.write(String.valueOf(String.format("%.4f",Execution.tolerance(R))));
				bw.write("\n");
				bw.write("\n");
				bw.write("Decomposition complete. Results written to output.txt");
				bw.close();
		}
		catch (FileNotFoundException e) {
			return;
		} 
		catch (IOException e) {
			return;
		}
		catch (IllegalArgumentException e ) {
			return;
		}
		
		
	}
		
		
		
}







class Matrix {
	
	public static boolean check_matrix(BufferedReader br) {
		boolean result = false;
		String line = "";
		
		int num_lines = 0;
		
		try { //read the file and check whether the number of elements in one line equals the number of lines.
			line = br.readLine();
			num_lines += 1;
			String[] numbers_strings = line.strip().split(" ");
			while((line = br.readLine()) != null){
				num_lines += 1;
			}
			
			if(num_lines != numbers_strings.length){
				result = false;
			}
			else {
				result = true;
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		return result;
		
	}
	
	public static double[][] init_A_Matrix(BufferedReader br) {
		double[][] A_matrix = null;	
		
		String line = "";
		try { //Start reading from file
			line = br.readLine();
		
			String[] numbers_strings = line.strip().split(" ");
		
			A_matrix = new double[numbers_strings.length][numbers_strings.length];	
		
			//Set all the elements in A based on the numbers in each line
			for(int i = 0; i < numbers_strings.length ; i++) {
				if (line!=null){numbers_strings = line.strip().split(" ");}
				else {break;}
				
				for(int j = 0; j < numbers_strings.length ; j++) {
					A_matrix[i][j] = Double.parseDouble(numbers_strings[j]);
				}
				line = br.readLine();
			}
	
		} 
		catch (NumberFormatException e) {
			e.printStackTrace();
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
		return A_matrix;
		
				
	}
	
	
	public static double[][] init_Matrix(int length){
		//Initialize a matrix by setting all elements to 0
		double[][] X_matrix = new double[length][length];
		for(int i = 0; i < length ; i++) {
			for(int j = 0; j < length ; j++) {
				X_matrix[i][j] = 0;	
			}
		}
		return X_matrix;
	}
}	







class Execution {
	
	public static void calculating(double[][] L, double[][] U, double[][] A){
		
		for(int i = 0; i < A.length ; i++) {
			
			//Updating U
			for(int k = i; k < A.length ; k++) {
				
				double sum = 0;
				for (int j = 0; j < i; j++) {
					sum += L[i][j]*U[j][k];
				}
				
				U[i][k] = A[i][k]-sum;
				
				if(i == k && U[i][i] == 0){
					try {
						
						BufferedWriter rt = new BufferedWriter(new FileWriter ("output.txt", true));
						rt.write("Matrix is singular, cannot perform decomposition");
						rt.close();

						
					}
					catch(IOException e1){
						return;
					}
					System.exit(0);
						
				}
			
			}
	
		
			
			//Updating L
			
			for(int k = i; k < A.length ; k++) {
				
				if(i==k) {
					L[i][i] = 1;
				}
				else {
					double sum = 0;
					for(int j = 0; j < i; j++) {
						sum += L[k][j]*U[j][i];
					}
					
					L[k][i] = (A[k][i] - sum)*(1/U[i][i]);
				}
			}
		}
	}

	
	
	public static double[][] calculateDotProduct(double[][] L, double[][] U) {
        
        double[][] result = new double[L.length][L.length];

        // Calculate the dot product
        for (int i = 0; i < L.length; i++) {
            for (int j = 0; j < L.length; j++) {
                result[i][j] = 0;
                for (int k = 0; k < L.length; k++) {
                    result[i][j] += L[i][k] * U[k][j];
                }
            }
        }

        return result;
    }
	
	public static double[][] substract_matrix(double[][] A, double[][] D) {
        
        double[][] result = new double[A.length][A.length];

        // Calculate the dot product
        for (int i = 0; i < A.length; i++) {
            for (int j = 0; j < A.length; j++) {
                result[i][j] = A[i][j] - D[i][j];
            }
        }
        return result;
    }
	
	public static double tolerance(double[][] R) {
		double sum = 0;
		
		//Get the norm of R
		for (int i = 0; i < R.length; i++) {
            for (int j = 0; j < R.length; j++) {
                sum += R[i][j]*R[i][j];
            }
        }
        return Math.sqrt(sum);
    }
}
		
	
	
class Execution_Parallel extends Execution{
	public static void calculating(double[][] L, double[][] U, double[][] A) {
		int n = A.length;

        // Get the number of available processors
        int numThreads = Runtime.getRuntime().availableProcessors();
        Thread[] threads = new Thread[numThreads];

        // Calculate the chunk size for each thread
        int chunkSize = n / numThreads;

        // Create and start threads
        for (int i = 0; i < numThreads; i++) {
            final int startRow = i * chunkSize;
            final int endRow = (i == numThreads - 1) ? n : startRow + chunkSize; // Ensure the last thread handles any remainder

            threads[i] = new Thread(() -> {
                for (int row = startRow; row < endRow; row++) {
                	//Updating U
        			for(int k = row; k < A.length ; k++) {
        				
        				double sum = 0;
        				for (int j = 0; j < row; j++) {
        					sum += L[row][j]*U[j][k];
        				}
        				
        				U[row][k] = A[row][k]-sum;
        				
        				if(row == k && U[row][row] == 0){
        					try {
        						BufferedWriter rt = new BufferedWriter(new FileWriter ("output.txt", true));
        						rt.write("Matrix is singular, cannot perform decomposition");
        						rt.close();
        						
        					}
        					catch(IOException e1){
        						return;
        					}
        					System.exit(0);
        						
        				}
        	
        			}
        			
        			//Updating L
        			
        			for(int k = row; k < A.length ; k++) {
        				
        				if(row==k) {
        					L[row][row] = 1;
        				}
        				else {
        					double sum = 0;
        					for(int j = 0; j < row; j++) {
        						sum += L[k][j]*U[j][row];
        					}
        					
        					L[k][row] = (A[k][row] - sum)*(1/U[row][row]);
        				}
        			}
                }
            });
            threads[i].start();
        }

        // Join threads to wait for all to finish
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
	}
}	
	

	
	
	

	

