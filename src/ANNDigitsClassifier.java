import java.io.File;
import java.io.IOException;

public class ANNDigitsClassifier {

	public static void main(String[] args) throws IOException {
		NeuralNetwork nn = new NeuralNetwork(0.01f, 784, 10, 16);	
//		nn.test();
		nn.train(new File("mnist_test.csv"), 10);
//		nn.test();
	}

}
