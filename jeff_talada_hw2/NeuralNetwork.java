import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Random;
import java.util.Scanner;

public class NeuralNetwork {

	long seed = 123;
	private float learningRate;
	private final int hiddenLayers;
	private Perceptron[] input;
	private Perceptron[] output;
	private Perceptron[][] hidden;
	
	public NeuralNetwork(float learningRate, int input, int output, int ... hidden)
	{
		if (learningRate <= 0)
			throw new IllegalArgumentException();
		this.learningRate = learningRate;
		hiddenLayers = hidden.length;
		if (validNodeCount(input))
			this.input = new Perceptron[input+1];
		if (validNodeCount(output))
			this.output = new Perceptron[output];
		if (validNodeCount(hiddenLayers))
			this.hidden = new Perceptron[hidden.length][];
		for (int i = 0; i < hiddenLayers; i++)
		{
			if (validNodeCount(hidden[i]))
				this.hidden[i] = new Perceptron[hidden[i]+1];
		}
		initializeNN();
	}
	
	public void setLearningRate(float learningRate)
	{
		this.learningRate = learningRate;
	}
	
	private void initializeNN()
	{
		Random r = new Random(seed);
		initializeLayer(r, input, 0);
		initializeOutputLayer(r, output, hidden[hidden.length-1].length);
		for (int i = 0; i < hiddenLayers; i++)
		{
			if (i == 0)
				initializeLayer(r, hidden[i], input.length);
			else
				initializeLayer(r, hidden[i], hidden[i-1].length);
		}
	}
	
	private void initializeLayer(Random r, Perceptron[] layer, int lowerLayerHiddenUnits)
	{
		int nodeCount = layer.length;
		layer[0] = new Perceptron();
		for (int i = 1; i < nodeCount; i++)
		{
			layer[i] = new Perceptron(r, lowerLayerHiddenUnits);
		}
	}

	private void initializeOutputLayer(Random r, Perceptron[] layer, int lowerLayerHiddenUnits)
	{
		int nodeCount = layer.length;
		for (int i = 0; i < nodeCount; i++)
		{
			layer[i] = new Perceptron(r, lowerLayerHiddenUnits);
		}
	}

	private Boolean validNodeCount(int nodeCount) {
		if (nodeCount > 0) {
			return true;
		} else {
			throw new IllegalArgumentException();
		}
	}

	public void train(File file, Integer setSize) throws IOException
	{
		FileInputStream inputStream = new FileInputStream(file);
		Scanner s = new Scanner(inputStream);
		int actual;
		int count = 0;
		while (s.hasNextLine() && (setSize == null || count < setSize))
		{
//			System.out.printf("% .4f\t% .4f\t% .4f\t% .4f\t% .4f\t% .4f\t% .4f\t% .4f\t% .4f\t% .4f\n"
//					, output[0].weights[0]
//					, output[0].weights[1]
//					, output[0].weights[2]
//					, output[0].weights[3]
//					, output[0].weights[4]
//					, output[0].weights[5]
//					, output[0].weights[6]
//					, output[0].weights[7]
//					, output[0].weights[8]
//					, output[0].weights[9]);
			actual = setInputs(s.nextLine().split(","));
			propagateInputs();
			train(actual);
			count++;
		}
		s.close();
		inputStream.close();
	}

	private void train(int target)
	{
		// find output error
		int perceptronCount = output.length;
		for (int j = 0; j < perceptronCount; j++)   //get output error
		{
			float expected = (j == target) ? 1f : 0f;
			output[j].error = output[j].value * (1 - output[j].value) * (expected - output[j].value);
		}
		
		// back propagate errors
		backPropagate(output, hidden[hiddenLayers-1], 0);
		if (hiddenLayers > 1)
		{
			for (int i = hiddenLayers-1; i > 0; i--)
			{
				backPropagate(hidden[i], hidden[i-1], 1);
			}
		}
		backPropagate(hidden[0], input, 1);
	}
	
	private void perceptronOutput(Perceptron perceptron, Perceptron[] inputLayer)
	{
		float total = 0f;
		int inputCount = inputLayer.length;
		for (int i = 0; i < inputCount; i++)
		{
			total += perceptron.weights[i] * inputLayer[i].value;
		}
		perceptron.value = (float) (1.0 / (1 + Math.pow(Math.E, (-1 * total))));
	}
	
	private void propagateInputs()
	{
		int perceptronCount = 0;
		perceptronCount = 0;
		for (int i = 0; i < hiddenLayers; i++)
		{
			perceptronCount = hidden[i].length;
			for (int j = 1; j < perceptronCount; j++)
			{
				if (i == 0)
					perceptronOutput(hidden[i][j], input);
				else
					perceptronOutput(hidden[i][j], hidden[i-1]);
			}
		}
		perceptronCount = output.length;
		for (int j = 0; j < perceptronCount; j++)
		{
			perceptronOutput(output[j], hidden[hiddenLayers-1]);
		}
	}
	
	private int getGuess()
	{
		int index = 0;
		float value = 0f;
		for (int i = 0; i < output.length; i++)
		{
			if (output[i].value > value)
			{
				index = i;
				value = output[i].value;
			}
		}
		return index;
	}
	
	private void backPropagate(Perceptron[] outputLayer, Perceptron[] inputLayer, int w0)
	{
		int inputNodeCount = inputLayer.length;
		int outputNodeCount = outputLayer.length;
		
		for (int i = 0; i < inputNodeCount; i++)
		{
			inputLayer[i].error = 0f;
			for (int j = w0; j < outputNodeCount; j++)
			{
				inputLayer[i].error += outputLayer[j].weights[i] * outputLayer[j].error;
			}
			inputLayer[i].error *= inputLayer[i].value * (1 - inputLayer[i].value);
		}
		for (int i = w0; i < outputNodeCount; i++)
		{
			for (int j = 0; j < inputNodeCount; j++)
			{
				outputLayer[i].weights[j] += learningRate * outputLayer[i].error * inputLayer[j].value;
			}
		}
	}
	
	public float test(File testSet) throws IOException
	{
		FileInputStream inputStream = new FileInputStream(testSet);
		Scanner s = new Scanner(inputStream);
		int actual = 0;
		int count = 0;
		int correct = 0;
		int guess = 0;
		while (s.hasNextLine())
		{
			actual = setInputs(s.nextLine().split(","));
			propagateInputs();
			guess = getGuess();
			if (actual == guess)
				correct++;
			count++;
		}
		s.close();
		inputStream.close();
		return ((float) correct/count*100);
	}
	
	private int setInputs(String[] inputs)
	{
		int actual = Integer.parseInt(inputs[0]);
		for (int i = 1; i < input.length; i++)
		{
			input[i].value = Float.parseFloat(inputs[i]) / 255.0f;
		}
		return actual;
	}
	
	public void testSetup()
	{
		System.out.println("Learning Rate: " + learningRate);
		System.out.println("Inputs: " + input.length);
		System.out.println("...");
		System.out.println("Hidden layers: " + hiddenLayers);
		for (int i = 0; i < hiddenLayers; i++)
		{
			System.out.println("  Hidden units: " + hidden[i].length);
			System.out.println("  Hidden weights: " + hidden[i][1].weights.length);
			for (int j = 0; j < hidden[i][1].weights.length; j++)
			{
				System.out.println("    " + j + ": "+ hidden[i][1].weights[j]);
			}
			System.out.println("...");
		}
		System.out.println("Outputs: " + output.length);
		System.out.println("Output weights: " + output[0].weights.length);
		for (int i = 0; i < output[0].weights.length; i++)
		{
			System.out.println("    " + i + ": "+ output[0].weights[i]);
		}
	}
	
	class Perceptron
	{
		protected float[] weights;
		protected float value;
		protected float error;
		
		Perceptron(Random r, int weights)
		{
			this.weights = new float[weights];
			for (int i = 0; i < weights; i++)
				this.weights[i] = r.nextFloat() / (weights / 10);
			value = 0f;
			error = 0f;
		}
		
		Perceptron()
		{
			weights = new float[0];
			value = 1f;
			error = 0f;
		}
	}
}












