import java.io.File;
import java.io.IOException;

public class ANNDigitsClassifier {

	public static void main(String[] args) throws IOException {
		int EPOCHS = 50;
		File trainSet = new File("mnist_train.csv");
		File testSet = new File("mnist_test.csv");
		long startTime, endTime;
		float[] learningRate = {0.1f, 0.05f, 0.01f, 0.005f};
		float accuracy;
		NeuralNetwork nn;
		nn = new NeuralNetwork(0.1f, 784, 10, 300);
		System.out.printf("Hidden Units: %d\tLearning Rate: %s\n", 16, "Various");
		for (int epoch = 1; epoch <= EPOCHS; epoch++)
		{
			if (epoch == 10)
				nn.setLearningRate(learningRate[1]);
			else if (epoch == 25)
				nn.setLearningRate(learningRate[2]);
			else if (epoch == 40)
				nn.setLearningRate(learningRate[3]);
			System.out.printf("%d\t", epoch);
			startTime = System.nanoTime();
			nn.train(trainSet, 60000);
			accuracy = nn.test(testSet);
			System.out.printf("% .2f\t", accuracy);
			endTime = System.nanoTime();
			System.out.printf("%.2f\n", (endTime - startTime) / 1000000000f);
		}
	}
}
