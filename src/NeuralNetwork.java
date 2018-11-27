import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Random;
import java.util.Scanner;

public class NeuralNetwork {

	long seed = 123;
	private final float w0;
	private final int hiddenLayers;
	private Perceptron[] input;
	private Perceptron[] output;
	private Perceptron[][] hidden;
	
	
	public NeuralNetwork(float learningRate, int input, int output, int ... hidden)
	{
		if (learningRate <= 0)
			throw new IllegalArgumentException();
		w0 = learningRate;
		hiddenLayers = hidden.length;
		if (validNodeCount(input))
			this.input = new Perceptron[input];
		if (validNodeCount(output))
			this.output = new Perceptron[output];
		if (validNodeCount(hiddenLayers))
			this.hidden = new Perceptron[hidden.length][];
		for (int i = 0; i < hiddenLayers; i++)
		{
			if (validNodeCount(hidden[i]))
				this.hidden[i] = new Perceptron[hidden[i]];
		}
		initializeNN();
	}
	
	private void initializeNN()
	{
		Random r = new Random(seed);
		initializeLayer(r, input, 0);
		initializeLayer(r, output, hidden[hidden.length-1].length);
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
		String[] line;
		int actual;
		int guess;
		int count = 0;
		while (s.hasNextLine() && (setSize == null || count < setSize))
		{
			line = s.nextLine().split(",");
			actual = Integer.parseInt(line[0]);
			for (int i = 0; i < input.length; i++)
			{
				input[i].value = Float.parseFloat(line[i+1]);
			}
			guess = train(count, actual);
			count++;
		}
		s.close();
		inputStream.close();
	}

	private int train(int iteration, int actual)
	{
		int guess = 0;
		int perceptronCount = 0;
		perceptronCount = hidden[0].length;
		for (int i = 0; i < hiddenLayers; i++)
		{
			perceptronCount = hidden[i].length;
			for (int j = 0; j < perceptronCount; j++)
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
		guess = getGuess();
		System.out.println(iteration + ": " + actual + "\tguess: " + guess);
		return guess;
	}
	
	private void perceptronOutput(Perceptron perceptron, Perceptron[] input)
	{
		float total = 0f;
		int inputCount = input.length;
		for (int i = 0; i < inputCount; i++)
		{
			total += perceptron.weights[i] * input[i].value;
		}
		total += w0;
		perceptron.value = total;
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
	
	public void test()
	{
		System.out.println("Learning Rate: " + w0);
		System.out.println("Inputs: " + input.length);
		System.out.println("...");
		System.out.println("Hidden layers: " + hiddenLayers);
		for (int i = 0; i < hiddenLayers; i++)
		{
			System.out.println("  Hidden units: " + hidden[i].length);
			System.out.println("  Hidden weights: " + hidden[i][0].getWeights().length);
			for (int j = 0; j < hidden[i][0].getWeights().length; j++)
			{
				System.out.println("    " + j + ": "+ hidden[i][0].weights[j]);
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
		
		Perceptron(Random r, int weights)
		{
			this.weights = new float[weights];
			for (int i = 0; i < weights; i++)
				this.weights[i] = r.nextFloat();
			value = 0f;
		}
		
		float[] getWeights()
		{
			return weights;
		}
		
		float getValue()
		{
			return value;
		}
	}
	
}












