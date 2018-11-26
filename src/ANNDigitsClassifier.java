
public class ANNDigitsClassifier {

	public static void main(String[] args) {
		NeuralNetwork nn = new NeuralNetwork(784, 10, 16);	
		nn.test();
	}

}
