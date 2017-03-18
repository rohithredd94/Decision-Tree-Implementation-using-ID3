import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Random;
import java.util.Scanner;

class elements {
	elements parent;        //root element
	elements left;
	elements right;
	boolean isLeaf = false;
	int Attribute = -1;
	int leftIndices[];
	int rightIndices[];
	int label = -1;

}

public class MachineLearning {
    
    private static int count = 0;

	public static void main(String[] args) {
                
        int attributes = 50;
        int rows = 1000;
        //String trainingdata = "S:\\Study\\utd\\ML\\Assignments\\ass2\\train.csv";
        //String testingdata = "S:\\Study\\utd\\ML\\Assignments\\ass2\\test.csv";
        String trainingdata = "train.csv";
        String testingdata = "test.csv";
        int pruning = 0;
                
        Scanner in = new Scanner(System.in);
              
        //System.out.println("Enter the traning data file name with location and extension:" );
        //trainingdata = in.next();

        //System.out.println("Enter the testing data file name with location and extension:" );
        //testingdata = in.next();
            	
		int[] widthAndLength = DimensionsofDataSheet(trainingdata);

		int[][] values = new int[widthAndLength[1]][widthAndLength[0]];
		String[] AttributeNames = new String[widthAndLength[0]];
		int[] isDone = new int[widthAndLength[0]];
		int[] indexList = new int[values.length];
		
		getTheData(trainingdata, values, AttributeNames, isDone, indexList, widthAndLength[0]);
		elements root = constructDecisionTree(null, values, isDone, widthAndLength[0] - 1, indexList, null);
	
		elements pruneTree = postPruneAlgorithm(trainingdata, attributes, rows, root, values, widthAndLength[0] - 1);
		
        double Acc1 =  Accuracy(trainingdata, root)*100;                
		System.out.println("The Accuracy over Training data for decision Tree  " + Acc1 + "%");
	
        double Acc2 =  Accuracy(testingdata, pruneTree)*100;
		System.out.println("The Accuracy over Testing data for decision Tree  " + Acc2 + "%");
		
        double nodes = printTree(root, 0, AttributeNames,1);
	       
        System.out.println("No of nodes : "+nodes);
        System.out.println("Do you want to Prune Tree  (0-yes/1-no):" );
        int num = in.nextInt();
	        
        pruning = num;

        System.out.println("The Final Accuracy over Testing data for decision Tree  " + Acc2 + "%");
        	 
        if (pruning==0)
        {
			elements varroot = BuildTree(null, values, isDone, widthAndLength[0] - 1, indexList, null);
		
			elements varpruneTree = postPruneAlgorithm(trainingdata, attributes, rows, root, values, widthAndLength[0] - 1);
		
            System.out.println(" \n\n\n\n");
                      
	
            nodes = printTree(varpruneTree, 0, AttributeNames,1);
            System.out.println("\n\nThe Accuracy on Testing data by original Tree : " + Acc2);       
            System.out.println("\n\nThe Final Accuracy on Tesing data using Pruned Tree : " + Accuracy(testingdata, varpruneTree)*100);
        }
	}

/* This function will find the best attribute and return a node */
	public static elements GetBestAttributeAndConstructNode(elements root, int[][] values, int[] isDone, int width,int[] indexList) 
	{
		int i = 0;
		int k = 0;
		double maxInfoGain = 0;
		int maxLeftIndex[] = null;
		int maxRightIndex[] = null;
		int maxIndex = 0;
		for (; i < width; i++) {
			if (isDone[i] == 0) {
				double negatives = 0;
				double positives = 0;
				double left = 0;
				double right = 0;
				double leftEntrophy = 0;
				double rightEntrophy = 0;
				int[] leftIndex = new int[values.length];
				int[] rightIndex = new int[values.length];
				double entrophy = 0;
				double rightPositives = 0;
				double infoGain = 0;
				double rightNegatives = 0, leftPositives = 0, leftNegatives = 0;
				for (k = 0; k < indexList.length; k++) {
					if (values[indexList[k]][width] == 1) {
						positives++;
					} else {
						negatives++;
					}
					if (values[indexList[k]][i] == 1) {
						rightIndex[(int) right++] = indexList[k];
						if (values[indexList[k]][width] == 1) {
							rightPositives++;
						} else {
							rightNegatives++;
						}

					} else {
						leftIndex[(int) left++] = indexList[k];
						if (values[indexList[k]][width] == 1) {
							leftPositives++;
						} else {
							leftNegatives++;
						}

					}

				}
  
                                
                entrophy = GetEntrophy(positives,negatives,indexList.length);
                double total = leftPositives + leftPositives;
				leftEntrophy = GetEntrophy(leftPositives,leftNegatives,total);    
				total = rightPositives + rightPositives;
                rightEntrophy = GetEntrophy(rightPositives,rightNegatives,total);
                                            
				if (Double.compare(Double.NaN, entrophy) == 0) {
					entrophy = 0;
				}
				if (Double.compare(Double.NaN, leftEntrophy) == 0) {
					leftEntrophy = 0;
				}
				if (Double.compare(Double.NaN, rightEntrophy) == 0) {
					rightEntrophy = 0;
				}

				infoGain = entrophy
						- ((left / (left + right) * leftEntrophy) + (right / (left + right) * rightEntrophy));
				if (infoGain >= maxInfoGain) {
					maxInfoGain = infoGain;
					maxIndex = i;
					int leftTempArray[] = new int[(int) left];
					for (int index = 0; index < left; index++) {
						leftTempArray[index] = leftIndex[index];
					}
					int rightTempArray[] = new int[(int) right];
					for (int index = 0; index < right; index++) {
						rightTempArray[index] = rightIndex[index];
					}
					maxLeftIndex = leftTempArray;
					maxRightIndex = rightTempArray;

				}
			}
		}
		root.Attribute = maxIndex;
		root.leftIndices = maxLeftIndex;
		root.rightIndices = maxRightIndex;
		return root;
	}

        
    public static double GetEntrophy(double positives, double negatives, double len)
    {
        double entrophy = (-1 * calculateLog(positives / len) * ((positives / len)))
						+ (-1 * calculateLog(negatives / len) * (negatives / len));
        return entrophy;
    }
        
            
    public static double calVariance(double positives,double negatives,double total)
    {
        double variance = ((positives / total)) * (negatives / total);
        return variance;
    }
        
	
        
	public static elements VarianceimpAndConstructNode(int[][] values, elements root,  int[] isDone, int width, int[] indexList) {
		int i = 0;
		int k = 0;
		double maxGain = 0;
		int maxLeftIndex[] = null;
		int maxRightIndex[] = null;
		int maxIndex = -1;
		for (; i < width; i++) {
			if (isDone[i] == 0) {
				double negatives = 0;
				double positives = 0;
				double left = 0;
				double right = 0;
				double leftvariance = 0;
				double rightvariance = 0;

				double variance = 0;
				double rightPositives = 0;
				double Gain = 0;
				double rightNegatives = 0, leftPositives = 0, leftNegatives = 0;

				int[] leftIndex = new int[values.length];
				int[] rightIndex = new int[values.length];
				for (k = 0; k < indexList.length; k++) {
					if (values[indexList[k]][width] == 1) {
						positives++;
					} else {
						negatives++;
					}
					if (values[indexList[k]][i] == 1) {
						rightIndex[(int) right++] = indexList[k];
						if (values[indexList[k]][width] == 1) {
							rightPositives++;
						} else {
							rightNegatives++;
						}

					} else {
						leftIndex[(int) left++] = indexList[k];
						if (values[indexList[k]][width] == 1) {
							leftPositives++;
						} else {
							leftNegatives++;
						}

					}

				}

				
                variance = calVariance(positives,negatives,indexList.length);
                leftvariance = calVariance(leftPositives,leftNegatives,leftPositives+leftNegatives);
                                
                rightvariance = calVariance(rightPositives,rightNegatives,rightPositives+rightNegatives);
                               
                if (Double.compare(Double.NaN, variance) == 0) {
					variance = 0;
				}
				if (Double.compare(Double.NaN, leftvariance) == 0) {
					leftvariance = 0;
				}
				if (Double.compare(Double.NaN, rightvariance) == 0) {
					rightvariance = 0;
				}

				Gain = variance - ((left / (left + right) * leftvariance) + (right / (left + right) * rightvariance));

				if (Gain >= maxGain) {
					maxGain = Gain;
					maxIndex = i;
					int leftTempArray[] = new int[(int) left];
					for (int index = 0; index < left; index++) {
						leftTempArray[index] = leftIndex[index];
					}
					int rightTempArray[] = new int[(int) right];
					for (int index = 0; index < right; index++) {
						rightTempArray[index] = rightIndex[index];
					}
					maxLeftIndex = leftTempArray;
					maxRightIndex = rightTempArray;

				}
			}
		}
		root.Attribute = maxIndex;
		root.leftIndices = maxLeftIndex;
		root.rightIndices = maxRightIndex;
		return root;
	}
        
	/*
	 * This Function will Construct The decision Tree
	 */
	public static elements constructDecisionTree(elements root, int[][] values, int[] isDone, int width, int[] indexList,
			elements parent) {
		if (root == null) {
			root = new elements();
			if (indexList == null || indexList.length == 0) {
				root.label = finalclassification(root, values, width);
				root.isLeaf = true;
				return root;
			}
			if (AllPositive(indexList, values, width)) {
				root.label = 1;
				root.isLeaf = true;
				return root;
			}
			if (AllNegative(indexList, values, width)) {
				root.label = 0;
				root.isLeaf = true;
				return root;
			}
			if (width == 1 || allattributesdone(isDone)) {
				root.label = finalclassification(root, values, width);
				root.isLeaf = true;
				return root;
			}
		}
		root = GetBestAttributeAndConstructNode(root, values, isDone, width,
		 indexList);
		root.parent = parent;
		if (root.Attribute != -1)
			isDone[root.Attribute] = 1;
		int leftIsDone[] = new int[isDone.length];
		int rightIsDone[] = new int[isDone.length];
		for (int j = 0; j < isDone.length; j++) {
			leftIsDone[j] = isDone[j];
			rightIsDone[j] = isDone[j];

		}

		root.left = constructDecisionTree(root.left, values, leftIsDone, width, root.leftIndices, root);
		root.right = constructDecisionTree(root.right, values, rightIsDone, width, root.rightIndices, root);
		return root;
	}

	public static elements BuildTree(elements root, int[][] values, int[] isDone, int width, int[] indexList,
			elements parent) {
		if (root == null) {
			root = new elements();
			if (indexList == null || indexList.length == 0) {
				root.label = finalclassification(root, values, width);
				root.isLeaf = true;
				return root;
			}
			if (AllPositive(indexList, values, width)) {
				root.label = 1;
				root.isLeaf = true;
				return root;
			}
			if (AllNegative(indexList, values, width)) {
				root.label = 0;
				root.isLeaf = true;
				return root;
			}
			if (width == 1 || allattributesdone(isDone)) {
				root.label = finalclassification(root, values, width);
				root.isLeaf = true;
				return root;
			}
		}
		
		root = VarianceimpAndConstructNode(values, root, isDone, width, indexList);
		root.parent = parent;
		if (root.Attribute != -1)
			isDone[root.Attribute] = 1;
		int leftIsDone[] = new int[isDone.length];
		int rightIsDone[] = new int[isDone.length];
		for (int j = 0; j < isDone.length; j++) {
			leftIsDone[j] = isDone[j];
			rightIsDone[j] = isDone[j];

		}

		root.left = constructDecisionTree(root.left, values, leftIsDone, width, root.leftIndices, root);
		root.right = constructDecisionTree(root.right, values, rightIsDone, width, root.rightIndices, root);
		return root;
	}
	/*
	Implementing the postpruning algorithm given in the document
	 */
	public static elements postPruneAlgorithm(String filePath, int L, int K, elements root, int[][] values, int width) {
		elements postPrunedTree = new elements();
		int i = 0;
		postPrunedTree = root;
		double maxAccuracy = Accuracy(filePath, root);
		for (i = 0; i < L; i++) {
			elements newRoot = duplicate(root);
			Random randomNumbers = new Random();
			int M = 1 + randomNumbers.nextInt(K);
			for (int j = 1; j <= M; j++) {
				count = 0;
				int noOfNonLeafNodes = CountNonLeafNodes(newRoot);
				if (noOfNonLeafNodes == 0)
					break;
				elements nodeArray[] = new elements[noOfNonLeafNodes];
				FillArray(newRoot, nodeArray);
				int s = randomNumbers.nextInt(noOfNonLeafNodes);
				nodeArray[s].isLeaf = true;
				nodeArray[s].label = finalclassificationatnode(nodeArray[s], values, width);
				nodeArray[s].left = null;
				nodeArray[s].right = null;

			}
			double accuracy = Accuracy(filePath, newRoot);

			if (accuracy > maxAccuracy) {
				postPrunedTree = newRoot;
				maxAccuracy = accuracy;
			}
		}
		return postPrunedTree;
	}

	/*
	 * This will be print the tree if we give yes.
	 */
	private static double printTree(elements root, int printLines, String[] AttributeNames, double nodenum) {
            
                
		int printLinesForThisLoop = printLines;
		if(root.isLeaf==true){
                        String temp = "";
			if(root.label==0) temp = "\t| leaf with class neagtive";
                        if(root.label==1) temp = "\t| leaf with class positive";
                      
                        System.out.println(" " + root.label + temp);
                        nodenum++;
                        return nodenum;
		}
		for (int i = 0; i < printLinesForThisLoop; i++) {
			System.out.print("|  ");
		}
		if (root.left != null && root.left.isLeaf && root.Attribute != -1)
			System.out.print(AttributeNames[root.Attribute] + "= 0 :");
		else if (root.Attribute != -1)
			System.out.println(AttributeNames[root.Attribute] + "= 0 :");

		printLines++;
		nodenum = printTree(root.left, printLines, AttributeNames,nodenum);
		for (int i = 0; i < printLinesForThisLoop; i++) {
			System.out.print("|  ");
		}
		if (root.right != null && root.right.isLeaf && root.Attribute != -1)
			System.out.print(AttributeNames[root.Attribute] + "= 1 :");
		else if (root.Attribute != -1)
			System.out.println(AttributeNames[root.Attribute] + "= 1 :");
		nodenum = printTree(root.right, printLines, AttributeNames,nodenum);
                
                return nodenum;
}

	/*
	 * This function checks if all the examples have output as 1
	 */
	public static boolean AllPositive(int[] indexList, int[][] values, int width) {
		boolean a = true;
		for (int i : indexList) {
			if (values[i][width] == 0) {
				a = false;
				break;
			}
		}
		return a;

	}

	/* This function checks if all the examples have output as 0 i */
	public static boolean AllNegative(int[] indexList, int[][] values, int width) {
		boolean a = true;
		for (int i : indexList) {
			if (values[i][width] == 1) {
				a = false;
				break;

			}
		}
		return a;

	}

	/*
	 * This function will check if all the Attributes are processed or not.
	 */
	public static boolean allattributesdone(int[] isDone) {
		boolean allDone = true;
		for (int i : isDone) {
			if (i == 0)
				allDone = false;
		}
		return allDone;
	}

	private static double calculateLog(double fraction) {
		return Math.log10(fraction) / Math.log10(2);
	}

	// This function will set the possible classification at a node
	public static int finalclassification(elements root, int[][] values, int width) {
		int ones = 0;
		int zeros = 0;
		if (root.parent == null) {
			int i = 0;
			for (i = 0; i < values.length; i++) {
				if (values[i][width] == 1) {
					ones++;
				} else {
					zeros++;
				}
			}
		} else {
			for (int i : root.parent.leftIndices) {
				if (values[i][width] == 1) {
					ones++;
				} else {
					zeros++;
				}
			}

			for (int i : root.parent.rightIndices) {
				if (values[i][width] == 1) {
					ones++;
				} else {
					zeros++;
				}
			}
		}
		return zeros > ones ? 0 : 1;

	}

	/*
	  This function will create copy for the given tree and returns it.
	 */
	public static elements duplicate(elements root) {
		if (root == null)
			return root;

		elements temp = new elements();
		temp.label = root.label;
		temp.isLeaf = root.isLeaf;
		temp.leftIndices = root.leftIndices;
		temp.rightIndices = root.rightIndices;
		temp.Attribute = root.Attribute;
		temp.parent = root.parent;
		temp.left = duplicate(root.left); // cloning left child
		temp.right = duplicate(root.right); // cloning right child
		return temp;
	}

	/*
	 * This maps the nodes of tree into an array of nodes
	 */
	private static void FillArray(elements root, elements[] Array) {
		if (root == null || root.isLeaf) {
			return;
		}
		Array[count++] = root;

		FillArray(root.left, Array);

		FillArray(root.right, Array);

	}

	/*
	 * This function will measure Accuracy of the decision tree
	 */
	private static double Accuracy(String filePath, elements root) {
		int[][] validationSet = constructValidationSet(filePath);
		double count = 0;
		for (int i = 1; i < validationSet.length; i++) {
			count += Classificationcheck(validationSet[i], root);
		}
		return count / validationSet.length;
	}

	/*
	 * This function will verify if the given Example is correctly classified
	 * based on the decision tree
	 * 
	 */
	private static int Classificationcheck(int[] setValues, elements newRoot) {
		int index = newRoot.Attribute;
		int correctlyClassified = 0;
                elements testingNode = newRoot;
		while (testingNode.label == -1) {

			if (setValues[index] == 1) {
				testingNode = testingNode.right;
			} else {
				testingNode = testingNode.left;
			}
			if (testingNode.label == 1 || testingNode.label == 0) {
				if (setValues[setValues.length - 1] == testingNode.label) {
					correctlyClassified = 1;
					break;
				} else {
					break;
				}
			}
			index = testingNode.Attribute;
		}
		return correctlyClassified;
	}

	/*
	 * This method will construct and return the validation set array from the
	 * file path specified.
	 */
	private static int[][] constructValidationSet(String filePath) {
		int[] widthAndLength = DimensionsofDataSheet(filePath);

		int[][] validationSet = new int[widthAndLength[1]][widthAndLength[0]];
		BufferedReader Reader = null;
		String line = "";
		try {
			Reader = new BufferedReader(new FileReader(filePath));
			int i = 0;
			int count = 0;
			while ((line = Reader.readLine()) != null) {
				String[] lineParameters = line.split(",");
				int j = 0;
				if (count == 0) {
					count++;
					continue;
				} else {
					for (String lineParameter : lineParameters) {
						validationSet[i][j++] = Integer.parseInt(lineParameter);
					}
				}
				i++;
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (Reader != null) {
				try {
					Reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return validationSet;
	}

	/*
	 * This function will return the finalValue of the classification based on
	 * manjority
	 */
	private static int finalclassificationatnode(elements root, int[][] values, int width) {
		int ones = 0;
		int zeros = 0;
		if (root.leftIndices != null) {
			for (int i : root.leftIndices) {
				if (values[i][width] == 1) {
					ones++;
				} else {
					zeros++;
				}
			}
		}

		if (root.rightIndices != null) {
			for (int i : root.rightIndices) {
				if (values[i][width] == 1) {
					ones++;
				} else {
					zeros++;
				}
			}
		}
		return zeros > ones ? 0 : 1;
	}

	/*
	 * This function counts the number of non leaf nodes and returns it's count.
	 */
	private static int CountNonLeafNodes(elements root) {
		if (root == null || root.isLeaf)
			return 0;
		else
			return (1 + CountNonLeafNodes(root.left) + CountNonLeafNodes(root.right));
	}


	private static int[] DimensionsofDataSheet(String csvFile) {
		BufferedReader Reader = null;
		String line = "";
		int count = 0;
		int[] widthAndLength = new int[2];
		try {

			Reader = new BufferedReader(new FileReader(csvFile));
			while ((line = Reader.readLine()) != null) {
				if (count == 0) {
					String[] country = line.split(",");
					widthAndLength[0] = country.length;
				}
				count++;
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (Reader != null) {
				try {
					Reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		widthAndLength[1] = count;
		return widthAndLength;
	}

	/*
	 * This function loads all the required data
	 */
	private static void getTheData(String filePath, int[][] values, String[] AttributeNames, int[] isDone,
			int[] indexList, int width) {
		BufferedReader Reader = null;
		String line = "";
		for (int s = 0; s < width; s++) {
			isDone[s] = 0;
		}
		int s = 0;
		for (s = 0; s < values.length; s++) {
			indexList[s] = s;
		}
		try {
                    
			Reader = new BufferedReader(new FileReader(filePath));
			int i = 0;
			while ((line = Reader.readLine()) != null) {
				String[] lineParameters = line.split(",");
				int j = 0;
				if (i == 0) {
					for (String lineParameter : lineParameters) {
						AttributeNames[j++] = lineParameter;
					}
				}

				else {

					for (String lineParameter : lineParameters) {
						values[i][j++] = Integer.parseInt(lineParameter);
					}
				}
				i++;
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (Reader != null) {
				try {
					Reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}


}
