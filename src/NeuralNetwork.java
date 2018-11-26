import java.util.Random;

public class NeuralNetwork {

	private final float w0 = 0.01f;
	private int[] input;
	private float[][] output;
	private float[][][] hidden;
	
	public NeuralNetwork(int input, int output, int ... hidden)
	{
		if (validNodeCount(input))
			this.input = new int[input];
		if (validNodeCount(output))
			this.output = new float[output][hidden[hidden.length-1]+1];
		if (validNodeCount(hidden.length))
			this.hidden = new float[hidden.length][][];
		if (validNodeCount(hidden[0]))
			this.hidden[0] = new float[hidden[0]][input+1];
		for (int i = 1; i < hidden.length; i++)
		{
			if (validNodeCount(hidden[i]))
				this.hidden[i] = new float[hidden[i]][hidden[i-1]+1];
		}
		initializeNN();
	}
	
	private void initializeNN()
	{
		Random r = new Random();
		initializeLayer(r, output);
		for (int i = 0; i < hidden.length; i++)
		{
			initializeLayer(r, hidden[i]);
		}
	}
	
	private void initializeLayer(Random r, float[][] layer)
	{
		int nodeCount = layer.length;
		int weightCount = layer[0].length;
		for (int i = 0; i < nodeCount; i++)
		{
			for (int j = 0; j < weightCount-1; j++)
			{
				layer[i][j] = r.nextFloat();
			}
			layer[i][weightCount-1] = w0;
		}
	}

	private Boolean validNodeCount(int nodeCount) {
		if (nodeCount > 0) {
			return true;
		} else {
			throw new IllegalArgumentException();
		}
	}

	
	
	public void test()
	{
		System.out.println("Inputs: " + input.length);
		System.out.println("...");
		System.out.println("Hidden layers: " + hidden.length);
		for (int i = 0; i < hidden.length; i++)
		{
			System.out.println("  Hidden units: " + hidden[i].length);
			System.out.println("  Hidden weights: " + hidden[i][0].length);
			for (int j = 0; j < hidden[i][0].length; j++)
			{
				System.out.println("    " + j + ": "+ hidden[i][0][j]);
			}
			System.out.println("...");
		}
		System.out.println("Outputs: " + output.length);
		System.out.println("Output weights: " + output[0].length);
		for (int i = 0; i < output[0].length; i++)
		{
			System.out.println("    " + i + ": "+ output[0][i]);
		}
	}
	
	private void setInput() {
		
	}

}












