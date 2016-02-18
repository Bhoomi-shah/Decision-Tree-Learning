
public class Calculation {
	
	double calculateEntropy(long totalSample, double noOfPos) {
		if (totalSample == 0) {
			return 0;
		}
		double proportionPositive = (double) noOfPos / totalSample;
		double proprtioNegative = 1 - proportionPositive;
		
		double logOfPositive = Math.log(proportionPositive) / Math.log(2);
		double logOfNegative = Math.log(proprtioNegative) / Math.log(2);

		if (proportionPositive == 0) {
			logOfPositive = 0;
		}
		if (proprtioNegative == 0) {
			logOfNegative = 0;
		}

		double entropy = -(proportionPositive * logOfPositive) - (proprtioNegative * logOfNegative);

		return entropy;
	}
	
	double calculateInfoGain(double totalentropy, double zeroEntropy,double oneEntropy, long noOfZero, long total) {
		double infoGain = totalentropy
				- (noOfZero * zeroEntropy / total)
				- ((total - noOfZero) * oneEntropy / total);
		return infoGain;
	}
}
