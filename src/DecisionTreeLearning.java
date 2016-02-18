import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Random;


class DecisionTree {
	
	Calculation cl;
	ArrayList<String[]> dataset;
	ArrayList<String[]> validationSet;
	ArrayList<String[]> testSet;
	int length;
	String attributes[];
	String pattern;
	int count=1;
	boolean preorderFlag = false;
	int classVarPrunedTree;
	int noOfLeafInPrunedTree;
	

	DecisionTree() {
		cl=new Calculation();
		dataset = new ArrayList<String[]>();
		validationSet = new ArrayList<String[]>();
		testSet = new ArrayList<String[]>();
		length = 0;
		attributes = new String[20];
		pattern = "";
	}
	
	void buildDecisionTree(Node n, ArrayList<Integer> datapoints, int branch) {
		if(datapoints.size()==1 && datapoints.get(0)==7){
			System.out.println("s");
		}
		if (n.right != null || n.rightValue != 2) {
			if (n.parent == null) {
				return;
			} else {
				buildDecisionTree(n.parent, n.parent.rightDatapoints, 1);
			}
		} else {
	
			int countOnes = 0;
			boolean flag = false;
			boolean allAtrributesVisited = true;
			
			//checking whether all attributes are considered
			for (int i = 0; i < length - 1; i++) {
				if (n.visited.get(i) == false) {
					allAtrributesVisited = false;
					break;
				}
			}
			for (int i = 0; i < datapoints.size(); i++) {
				countOnes += Integer.parseInt(dataset.get(datapoints.get(i))[length - 1]);
			}
	
			int classVal = 2;
			
			//if all the datapoints are positive
			if (countOnes == datapoints.size()) {
	
				classVal = 1;
			} /*if all datapoints are negative*/else if (countOnes == 0) {
				classVal = 0;
			} else if (allAtrributesVisited) {
				if (countOnes < datapoints.size() / 2) {
					classVal = 0;
				} else {
					classVal = 1;
				}
			}
	
			if (classVal != 2) {
				if (branch == 0) {
					n.leftValue = classVal;
					buildDecisionTree(n, n.rightDatapoints, 1);
	
				} else if (branch == 1) {
					n.rightValue = classVal;
					buildDecisionTree(n.parent, n.parent.rightDatapoints, 1);
				}
			} else {
				int childIndex = findBestAttribute(n,datapoints);
				Node childNode = new Node();
				childNode.value = childIndex;
	
				if (branch == 0) {
					n.left = childNode;
				} else {
					n.right = childNode;
				}
	
				childNode.parent = n;
	
				for (int i = 0; i < length - 1; i++) {
					childNode.visited.add(n.visited.get(i));
				}
	
				childNode.visited.set(childIndex, true);
				ArrayList<Integer> zeroIndexes = new ArrayList<Integer>();
				ArrayList<Integer> oneIndexes = new ArrayList<Integer>();
	
				for (int i = 0; i < datapoints.size(); i++) {
					if (dataset.get(datapoints.get(i))[childIndex].equals("0")) {
						zeroIndexes.add(datapoints.get(i));
					} else {
						oneIndexes.add(datapoints.get(i));
					}
	
				}
	
				for (int i = 0; i < zeroIndexes.size(); i++) {
					childNode.leftDatapoints.add(zeroIndexes.get(i));
				}
				for (int i = 0; i < oneIndexes.size(); i++) {
					childNode.rightDatapoints.add(oneIndexes.get(i));
				}
				buildDecisionTree(childNode, childNode.leftDatapoints, 0);
	
				}
			}
		
	}
	
	
	int findBestAttribute(Node rootNode, ArrayList<Integer> datapoints) {
		long noOfPosEg = 0;
		for (int i = 0; i < datapoints.size(); i++) {
			noOfPosEg += Integer.parseInt(dataset.get(datapoints.get(i))[length- 1]);
		}
	
		double totalEntropy = cl.calculateEntropy(datapoints.size(),noOfPosEg);
	
		long noOfPosZeroEg;
		long zeroLength;
		double zeroEntropy;
		double oneEntropy;
		double infoGain;
		double max = 2;
		int index = 0;
	
		for (int j = 0; j < length - 1; j++) {
			if (rootNode.visited.get(j) == false) {
				noOfPosZeroEg = 0;
				zeroLength = 0;
	
				for (int i = 0; i < datapoints.size(); i++) {
					if ((dataset.get(datapoints.get(i))[j]).equals("0")) {
						zeroLength++;
						noOfPosZeroEg += Integer.parseInt(dataset.get(datapoints.get(i))[length - 1]);

					}
				}
	
				zeroEntropy = cl.calculateEntropy(zeroLength,noOfPosZeroEg);
				oneEntropy = cl.calculateEntropy(datapoints.size() - zeroLength, noOfPosEg- noOfPosZeroEg);
	
				infoGain = cl.calculateInfoGain(totalEntropy, zeroEntropy,oneEntropy, zeroLength, datapoints.size());
	
				if (max == 2) {
					max = infoGain;
					index = j;
				} else if (infoGain > max) {
					max = infoGain;
					index = j;
				}
	
			}
		}
	
		return index;
	}
	
	Node prune(Node originalTree, int l, int k) {
		Node tempTree = new Node();
		Random rand = new Random();
		boolean isRootNull = false;
		Node bestTree = new Node();
		copyTree(originalTree, bestTree);
		double originalAccuracy = getAccuracyUsingTest(bestTree);
		System.out.println("Accuracy before pruning " + originalAccuracy);

		for (int i = 0; i < l; i++) {
			double bestTreeAccuracy = getAccuracyUsingValidation(bestTree);

			copyTree(originalTree, tempTree);
			int m = rand.nextInt(k) + 1;

			for (int j = 1; j <= m; j++) {
				count = 1;
				preorderFlag = false;
				orderNodes(tempTree);

				int p = rand.nextInt(count - 1) + 1;

				if (p > 1) {

					preOrder(tempTree, p);

				} else {

					isRootNull = true;
					break;
				}
				
				
			}
			if (isRootNull == true) {

				isRootNull = false;
				continue;
			}
			double prunedAccuracy = getAccuracyUsingValidation(tempTree);
		//	System.out.println("intermediate "+prunedAccuracy);
			if (prunedAccuracy > bestTreeAccuracy) {
				copyTree(tempTree, bestTree);
			}

		}
		
		System.out.println("Accuracy after pruning"+ getAccuracyUsingTest(bestTree));
		return bestTree;

	}

	
	void orderNodes(Node root) {
		if (root == null) {
			return;
		}

		root.number = count++;

		orderNodes(root.left);
		orderNodes(root.right);

	}
	
	void preOrder(Node root, int p) {
		if (root == null) {
			return;
		}
		if (root.number == p && preorderFlag == false) {
			int classVal;
			preorderFlag = true;
			if (root.parent == null) {
				root = null;
			} else if (root.parent.left == root) {

				classVal = getClassValue(root, root.leftDatapoints);
				root.parent.left = null;
				root.parent.leftValue = classVal;
			} else if (root.parent.right == root) {

				classVal = getClassValue(root, root.rightDatapoints);
				root.parent.right = null;
				root.parent.rightValue = classVal;
			}
			return;
		}
		preOrder(root.left, p);
		preOrder(root.right, p);
	}

	int getClassValue(Node root, ArrayList<Integer> datapoints) {
		int count = 0;
		for (int i = 0; i < datapoints.size(); i++) {
			count += Integer.parseInt(dataset.get(datapoints.get(i))[length - 1]);
		}

		if (count > datapoints.size() / 2) {
			return 1;
		}
		return 0;
	}
	
	void copyTree(Node root, Node duplicateNode) {
		if (root == null) {
			return;
		}

		duplicateNode.value = root.value;
		duplicateNode.leftValue = root.leftValue;
		duplicateNode.rightValue = root.rightValue;

		if (root.left != null) {
			duplicateNode.left = new Node();
			duplicateNode.left.parent = duplicateNode;
			copyTree(root.left, duplicateNode.left);
		}
		if (root.right != null) {
			duplicateNode.right = new Node();
			duplicateNode.right.parent = duplicateNode;
			copyTree(root.right, duplicateNode.right);
		}
	}
	
	int getClassValueUsingTree(Node root,int i,String datasetName){
		String val = "";
		while (root != null) {
			
			if(datasetName.equalsIgnoreCase("Validation")){
				val = validationSet.get(i)[root.value];
			}
			else{
				val = testSet.get(i)[root.value];
			}
			if (val.equals("0")) {
				if (root.left == null) {
					return root.leftValue;
				}
				root = root.left;
			} else {
				if (root.right == null) {
					return root.rightValue;
				}
				root = root.right;
			}
		}
		return 2;
	
	}
	
	double getAccuracyUsingValidation(Node root) {
		int value = 2;
		int count = 0;
		for (int i = 0; i < validationSet.size(); i++) {

			value = getClassValueUsingTree(root, i,"Validation");

			if (value == Integer.parseInt(validationSet.get(i)[validationSet.get(i).length - 1])) {
				count++;
			}
		}
		return count * 100.0000 / validationSet.size();

	}

	
	double getAccuracyUsingTest(Node root) {
		int value = 2;
		int count = 0;
		for (int i = 0; i < testSet.size(); i++) {

			value = getClassValueUsingTree(root, i,"Test");

			if (value == Integer.parseInt(testSet.get(i)[testSet.get(i).length - 1])) {
				count++;
			}
		}
		return count * 100.0000 / testSet.size();

	}
	
	void printTree(Node root, String pattern, String branchDir) {
		if (root == null) {
			return;
		}

		if (root.parent != null) {
			System.out.println(pattern + attributes[root.parent.value] + " = "
					+ branchDir + " : ");
		}

		if (root.parent != null) {

			pattern += "| ";
		}
		if (root.left == null) {

			System.out.println(pattern + attributes[root.value] + " = 0 : "+ root.leftValue);
		}
		printTree(root.left, pattern, "0");
		if (root.right == null) {
			System.out.println(pattern + attributes[root.value] + " = 1 : "+ root.rightValue);
		}

		printTree(root.right, pattern, "1");

	}
		

}

public class DecisionTreeLearning {

	public static void main (String[] args)throws Exception {
		// TODO Auto-generated method stub
	
		DecisionTree dt= new DecisionTree();
		Calculation c = new Calculation();
		//BufferedReader br = new BufferedReader(new FileReader("Book2.csv"));
		
		BufferedReader br = new BufferedReader(new FileReader(args[2]));
		String line = "";
		line=br.readLine();
		dt.attributes = line.split(",");
		dt.length = line.split(",").length;
		for(String x:dt.attributes) {
			System.out.print(x);
		}
		
		while ((line = br.readLine()) != null) {
			dt.dataset.add(line.split(","));
		}	
		
		br = new BufferedReader(new FileReader(args[3]));
		line = "";
		line=br.readLine();
		while ((line = br.readLine()) != null) {
			dt.validationSet.add(line.split(","));
		}

		br = new BufferedReader(new FileReader(args[4]));
		line = "";
		line=br.readLine();
		while ((line = br.readLine()) != null) {
			dt.testSet.add(line.split(","));
		}
		
		System.out.println("Length : "+dt.length);
		
		long noOfPositive = 0;
		
		for(String[] i:dt.dataset){
			noOfPositive += Integer.parseInt(i[dt.length-1]);
		}
		System.out.println("no of pos : "+noOfPositive);
	
		double totalEntropy = c.calculateEntropy(dt.dataset.size(),noOfPositive);
		System.out.println("Total entropy : "+totalEntropy);
		
		
		long noOfEgClassOne;
		long noOfEgZeroValue;
		double zeroEntropy;
		double oneEntropy;
		double infoGain;
		double max = 2;
		int index = 0;
		for (int j = 0; j < dt.length - 1; j++) {
			noOfEgClassOne = 0;
			noOfEgZeroValue = 0;
			for (int i = 0; i < dt.dataset.size(); i++) {
				if (dt.dataset.get(i)[j].equals("0")) {
					noOfEgZeroValue++;
					noOfEgClassOne += Integer.parseInt(dt.dataset.get(i)[dt.length - 1]);

				}
			}
			zeroEntropy = c.calculateEntropy(noOfEgZeroValue, noOfEgClassOne);
			oneEntropy = c.calculateEntropy(dt.dataset.size() - noOfEgZeroValue,noOfPositive - noOfEgClassOne);

			infoGain = c.calculateInfoGain(totalEntropy, zeroEntropy,oneEntropy, noOfEgZeroValue, dt.dataset.size());

			if (max == 2) {
				max = infoGain;
			} else if (infoGain > max) {
				max = infoGain;
				index = j;
			}
		}
		int rootIndex = index;
		System.out.println("root element : "+rootIndex+","+dt.attributes[rootIndex]);	
		Node rootNode = new Node();
		rootNode.value = rootIndex;

		ArrayList<Integer> zeroIndexes = new ArrayList<Integer>();
		ArrayList<Integer> oneIndexes = new ArrayList<Integer>();

		long countZero = 0;
		long countOne = 0;
		long countZeroPositives = 0;
		long countOnePositives = 0;
		for (int i = 0; i < dt.dataset.size(); i++) {
			if (dt.dataset.get(i)[rootIndex].equals("0")) {
				zeroIndexes.add(i);
				countZero++;
				countZeroPositives += Integer.parseInt((dt.dataset.get(i)[dt.length - 1]));
			} else {
				oneIndexes.add(i);
				countOne++;
				countOnePositives += Integer.parseInt((dt.dataset.get(i)[dt.length - 1]));
			}
		}

		for (int i = 0; i < dt.length - 1; i++) {
			rootNode.visited.add(false);
		}

		rootNode.visited.set(rootIndex, true);

		for (int i = 0; i < zeroIndexes.size(); i++) {
			rootNode.leftDatapoints.add(zeroIndexes.get(i));
		}

		for (int i = 0; i < oneIndexes.size(); i++) {
			rootNode.rightDatapoints.add(oneIndexes.get(i));
		}

		dt.buildDecisionTree(rootNode, rootNode.leftDatapoints, 0);
		
		
		int l = Integer.parseInt(args[0]);

		int k = Integer.parseInt(args[1]);
		System.out.println("Heuristic: Information Gain");
		Node pruneNode = dt.prune(rootNode, l, k);
		if (args[5].equalsIgnoreCase("Yes")) {
			System.out.println("Decision tree before pruning");
			dt.printTree(rootNode, "", "0");
			System.out.println("Decision tree after pruning");
			dt.printTree(pruneNode, "", "0");
		}
	}
	

}
