import java.io.File;
import java.io.IOException;

public class ANNDigitsClassifier {

	public static void main(String[] args) throws IOException {
		int EPOCHS = 50;
		File trainSet = new File("mnist_train.csv");
		File testSet = new File("mnist_test.csv");
		long startTime, endTime;
		float learningRate = 0.1f;
		int[] trainingCount = {10, 100, 1000, 10000, 20000, 40000, 60000};
		float accuracy;
		NeuralNetwork nn;
		nn = new NeuralNetwork(0.1f, 784, 10, 16);
		System.out.printf("Hidden Units: %d\tLearning Rate: %.2f\n", 16, learningRate);
		for (int count : trainingCount)
		{
			for (int epoch = 1; epoch <= EPOCHS; epoch++)
			{
				System.out.printf("%d\t", epoch);
				startTime = System.nanoTime();
				nn.train(trainSet, count);
				accuracy = nn.test(testSet);
				System.out.printf("% .2f\t", accuracy);
				endTime = System.nanoTime();
				System.out.printf("%.2f\n", (endTime - startTime) / 1000000000f);
			}
		}
	}
}
