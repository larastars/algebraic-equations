import java.util.Scanner;

public class AlgebriacEquationsDriver {
	static double dCoefficients [][] = {{-6,33,16},{-7,34,-8},{-25,22,9}};
	static double dRightSides [] = {-36, 43, -46};
	
	public static void main (String [] args)
	{
		int n = setUp();
		// print the equations
		Print("Equations",n,dCoefficients,dRightSides);
		
		double [][] coefficients = new double[n][n];
		double [] rightSides = new double[n];
		int [] oldRow = new int[n];
		
		for(int i = 0; i < n; i++)
		{
			for(int j = 0; j < n; j++)
			{
				coefficients[i][j] = dCoefficients[i][j];
			}
			rightSides[i] = dRightSides[i];
		}
		
		AlgebraicEquations.decompose(n,coefficients, oldRow);
		double [] solution = AlgebraicEquations.substitute(n,coefficients, oldRow, rightSides);
		
		//print out the solutions
		Print("Solution",n,solution);
		
		rightSides = evaluate(n,coefficients,solution);
		
		//print out the vertification
		Print("Verification",n,dRightSides,rightSides);
	}
		
	public static int setUp()
	{
		Scanner keyboard = new Scanner(System.in);
		
		System.out.println("Enter the number of equations and the unknown: ");
		int n = keyboard.nextInt();
		if (n ==0)
		{
			n=3;
		}
		else 
		{
			dCoefficients = new double [(int)Math.abs(n)][(int)Math.abs(n)];
			dRightSides = new double [(int)Math.abs(n)];
			if (n<0)
			{
				n = -n;
				// if n is a negative number, use the default 
				generateEquations (n, dCoefficients, dRightSides);
				
			}
			else 
			{
				for (int row = 0; row <n; row++)
				{
					System.out.println("Enter equation " + row + "'s coefficients separated by spaces:");
					keyboard.nextLine();
					String coeffInput = keyboard.nextLine();
					String [] inputs = coeffInput.split(" ");
					
					for(int col = 0; col < inputs.length ; col++)
					{
						dCoefficients[row][col] = Double.parseDouble(inputs[col]);
					}
					System.out.println("Enter right-side value : ");
					dRightSides[row] = keyboard.nextDouble();
				}	
			}
		}
		return n;
	}
	public static void generateEquations (int n, double [][] coefficients, double [] rightSides)
	{
		for(int i = 0; i < n; i++)
		{
			for(int j = 0; j < n; j++)
			{
				coefficients[i][j] = 100*(Math.random()-0.5);
			}
			dRightSides[i] = 100*(Math.random()-0.5);
		}
	}
	public static void Print (String message, int n, double [][] coefficients, double [] rightSides )
	{
		System.out.println(message);
		for(int i = 0 ; i < n; i++)
		{
			for(int j = 0; j < coefficients[i].length ; j++)
			{
				System.out.print(coefficients[i][j] + "\t");
			}
			System.out.println("\t"+ rightSides[i]);
		}
	}
	public static void Print (String message, int n, double [] solution)
	{
		System.out.println(message);
		for(int i = 0 ; i < n; i++)
		{
			System.out.println(solution[i]);
		}
	}
	public static double [] evaluate (int n, double [][] coefficient, double [] solutions)
	{
		for (int row =0; row <n; row ++)
		{
			for (int col =0; col <n; col++)
			{
				dRightSides[row] += dCoefficients[row][col] * solutions[col];
						
			}
		}
		return dRightSides;
	}
	public static void Print(String message, int n, double [] origRS, double [] checkRS)
	{
		System.out.println(message);
		for(int i = 0; i < n; i++)
		{
			System.out.println(origRS[i] + "\t" + checkRS[i]);
		}
	}


}

class AlgebraicEquations 
{
	
	public static void decompose(int n, double [][] coefficients, int [] oldRow)	
	{
		double [] scalings =doScaling(n, coefficients);
		for (int row = 0; row <n; row++)
		{
			oldRow[row]=row;
		}
		for (int col =0; col<n; col++)
		{
			doUpper(col, coefficients);
			int maxRow = beginLower(col, coefficients, scalings);
			if (maxRow != col)
			{
				for (int k=0; k<n; k++)
				{
					//swap
					double temp = coefficients[maxRow][k];
					coefficients[maxRow][k] = coefficients[col][k];
					coefficients[col][k] = temp;
				}
				int temp = oldRow[maxRow];
				oldRow[maxRow] = oldRow[col];
				oldRow[col] = temp;
				
				scalings[maxRow] = scalings[col];
			}
			if (col != (n-1))
			{
				for (int row = (col+1); row <n; row++)
				{
					coefficients [row][col] /= coefficients [col][col];
				}
			}
		}
		
	}
	public static double [] doScaling (int n, double [][] coefficients)
	{
		double [] scalings = new double [n];
		for (int row =0; row<n; row ++)
		{
			int max =0;
			for (int col =0; col <n; col++)
			{
				
				if (Math.abs(coefficients [row][col]) > max)
				{
					
					max = (int)Math.abs(coefficients [row][col]);
				
				}
			}
			if (max ==0)
			{
				
				System.out.println("Error: \"All coefficients in a row are zero!\"");
			}
			scalings [row] = 1 /max;
		}
		return scalings;
	}
	public static void doUpper(int col, double [][] coefficients)
	{
		for (int row =0; row <col; row++)
		{
			double sum = coefficients [row][col];
			for (int k=0; k<row; k++)
			{
				sum -= coefficients [row][k]* coefficients [k][col];
				
			}
			coefficients [row][col]=sum;
		}
	}
	public static int beginLower(int col, double [][] coefficients, double [] scalings)
	{
		int rowMax = col;
		double max = 0;
		for (int row = col; row<coefficients[col].length; row++)
		{
			double sum = coefficients [row][col];
			for (int k=0; k<col; k++)
			{
				sum -= coefficients [row][k] * coefficients [k][col];
			}
			coefficients [row][col] = sum;
			
			if (scalings [row] * Math.abs(sum) >= max)
			{
				max = scalings [row]* Math.abs(sum);
				rowMax = row;
			}
		}

		return rowMax;
				
	}
	public static  double [] substitute(int n, double [][] lowerUpper, int [] oldRow, double [] rightSides)
	{
		rightSides = forward (n, lowerUpper, oldRow, rightSides);
		backward (n, lowerUpper, rightSides);
		return rightSides;	
	}
	public static double[] forward(int n, double [][] lowerUpper, int [] oldRow, double [] rightSides)
	{
		int firstNonZeroRow = -1;
		double [] solution = new double[n];
		for (int row = 0; row <n ; row++)
		{
			double sum = rightSides [oldRow[row]];
			if (firstNonZeroRow > -1)
			{
				for (int col = firstNonZeroRow; col < row; col++)
				{
					sum -= lowerUpper [row][col] * solution [col];
				}
			}
			else if (sum != 0)
			{
				firstNonZeroRow = row;
				
			}
			solution [row]=sum;
				
		}
		return solution;
	}
	public static void backward(int n, double [][] lowerUpper, double [] rightSides)
	{
			for (int row = (n-1); row >= 0; row--)
			{
				double sum = rightSides [row];
				for (int col = (row +1); col <n; col++)
				{
					sum -= lowerUpper [row][col] * rightSides [col];
				}
				rightSides [row] = sum / lowerUpper [row][row];
			}
	}
}


