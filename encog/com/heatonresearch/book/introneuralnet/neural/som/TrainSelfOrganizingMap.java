/**
 * Introduction to Neural Networks with Java, 2nd Edition
 * Copyright 2008 by Heaton Research, Inc. 
 * http://www.heatonresearch.com/books/java-neural-2/
 * 
 * ISBN13: 978-1-60439-008-7  	 
 * ISBN:   1-60439-008-5
 *   
 * This class is released under the:
 * GNU Lesser General Public License (LGPL)
 * http://www.gnu.org/copyleft/lesser.html
 */
package com.heatonresearch.book.introneuralnet.neural.som;

import com.heatonresearch.book.introneuralnet.neural.matrix.Matrix;
import com.heatonresearch.book.introneuralnet.neural.matrix.MatrixMath;

/**
 * TrainSelfOrganizingMap: Implements an unsupervised training algorithm for use
 * with a Self Organizing Map.
 * 
 * @author Jeff Heaton
 * @version 2.1
 */
public class TrainSelfOrganizingMap {
	
	/**
	 * The learning method, either additive or subtractive.
	 * @author jheaton
	 *
	 */
	public enum LearningMethod 
	{
		ADDITIVE,
		SUBTRACTIVE
	}

	/**
	 * The self organizing map to train.
	 */
	private final SelfOrganizingMap som;
	
	/**
	 * The learning method.
	 */
	protected LearningMethod learnMethod;

	/**
	 * The learning rate.
	 */
	protected double learnRate;

	/**
	 * Reduction factor.
	 */
	protected double reduction = .99;

	/**
	 * Mean square error of the network for the iteration.
	 */
	protected double totalError;


	/**
	 * Mean square of the best error found so far.
	 */
	protected double globalError;

	/**
	 * Keep track of how many times each neuron won.
	 */
	int won[];
	
	/**
	 * The training sets.
	 */
	double train[][];

	/**
	 * How many output neurons.
	 */
	private final int outputNeuronCount;
	
	/**
	 * How many input neurons.
	 */
	private final int inputNeuronCount;
	
	/** 
	 * The best network found so far.
	 */
	private final SelfOrganizingMap bestnet;


	/**
	 * The best error found so far.
	 */
	private double bestError;
	
	/**
	 * The work matrix, used to calculate corrections.
	 */
	private Matrix work;
	
	/**
	 * The correction matrix, will be applied to the weight matrix after each
	 * training iteration.
	 */
	private Matrix correc;

	/**
	 * Construct the trainer for a self organizing map.
	 * @param som The self organizing map.
	 * @param train The training method.
	 * @param learnMethod The learning method.
	 * @param learnRate The learning rate.
	 */
	public TrainSelfOrganizingMap(final SelfOrganizingMap som,
			final double train[][],LearningMethod learnMethod,double learnRate) {
		this.som = som;
		this.train = train;
		this.totalError = 1.0;
		this.learnMethod = learnMethod;
		this.learnRate = learnRate;

		this.outputNeuronCount = som.getOutputNeuronCount();
		this.inputNeuronCount = som.getInputNeuronCount();

		this.totalError = 1.0;

		for (int tset = 0; tset < train.length; tset++) {
			final Matrix dptr = Matrix.createColumnMatrix(train[tset]);
			if (MatrixMath.vectorLength(dptr) < SelfOrganizingMap.VERYSMALL) {
				throw (new RuntimeException(
						"Multiplicative normalization has null training case"));
			}

		}

		this.bestnet = new SelfOrganizingMap(this.inputNeuronCount,
				this.outputNeuronCount, this.som.getNormalizationType());

		this.won = new int[this.outputNeuronCount];
		this.correc = new Matrix(this.outputNeuronCount,
				this.inputNeuronCount + 1);
		if (this.learnMethod == LearningMethod.ADDITIVE) {
			this.work = new Matrix(1, this.inputNeuronCount + 1);
		} else {
			this.work = null;
		}

		initialize();
		this.bestError = Double.MAX_VALUE;
	}

	/**
	 * Adjust the weights and allow the network to learn.
	 */
	protected void adjustWeights()
	{
		for (int i = 0; i < this.outputNeuronCount; i++) {

			if (this.won[i] == 0) {
				continue;
			}

			double f = 1.0 / this.won[i];
			if (this.learnMethod == LearningMethod.SUBTRACTIVE) {
				f *= this.learnRate;
			}

			double length = 0.0;

			for (int j = 0; j <= this.inputNeuronCount; j++) {
				final double corr = f * this.correc.get(i, j);
				this.som.getOutputWeights().add(i, j, corr);
				length += corr * corr;
			}
		}
	}

	/**
	 * Copy the weights from one matrix to another.
	 * @param source The source SOM.
	 * @param target The target SOM.
	 */
	private void copyWeights(final SelfOrganizingMap source,
			final SelfOrganizingMap target) {

		MatrixMath.copy(source.getOutputWeights(), target
				.getOutputWeights());
	}


	/**
	 * Evaludate the current error level of the network.
	 */
	public void evaluateErrors() {

		this.correc.clear();

		for (int i = 0; i < this.won.length; i++) {
			this.won[i] = 0;
		}

		this.globalError = 0.0;
		// loop through all training sets to determine correction
		for (int tset = 0; tset < this.train.length; tset++) {
			final NormalizeInput input = new NormalizeInput(this.train[tset],
					this.som.getNormalizationType());
			final int best = this.som.winner(input);

			this.won[best]++;
			final Matrix wptr = this.som.getOutputWeights().getRow(best);

			double length = 0.0;
			double diff;

			for (int i = 0; i < this.inputNeuronCount; i++) {
				diff = this.train[tset][i] * input.getNormfac()
						- wptr.get(0, i);
				length += diff * diff;
				if (this.learnMethod == LearningMethod.SUBTRACTIVE) {
					this.correc.add(best, i, diff);
				} else {
					this.work.set(0, i, this.learnRate * this.train[tset][i]
							* input.getNormfac() + wptr.get(0, i));
				}
			}
			diff = input.getSynth() - wptr.get(0, this.inputNeuronCount);
			length += diff * diff;
			if (this.learnMethod ==LearningMethod.SUBTRACTIVE) {
				this.correc.add(best, this.inputNeuronCount, diff);
			} else {
				this.work
						.set(0, this.inputNeuronCount, this.learnRate
								* input.getSynth()
								+ wptr.get(0, this.inputNeuronCount));
			}

			if (length > this.globalError) {
				this.globalError = length;
			}

			if (this.learnMethod == LearningMethod.ADDITIVE) {
				normalizeWeight(this.work, 0);
				for (int i = 0; i <= this.inputNeuronCount; i++) {
					this.correc.add(best, i, this.work.get(0, i)
							- wptr.get(0, i));
				}
			}

		}

		this.globalError = Math.sqrt(this.globalError);
	}

	/**
	 * Force a win, if no neuron won.
	 */
	protected void forceWin() {
		int best, which = 0;

		final Matrix outputWeights = this.som.getOutputWeights();
		
		// Loop over all training sets.  Find the training set with
		// the least output.
		double dist = Double.MAX_VALUE;
		for (int tset = 0; tset < this.train.length; tset++) {
			best = this.som.winner(this.train[tset]);
			final double output[] = this.som.getOutput();
			
			if (output[best] < dist) {
				dist = output[best];
				which = tset;
			}
		}

		final NormalizeInput input = new NormalizeInput(this.train[which],
				this.som.getNormalizationType());
		best = this.som.winner(input);
		final double output[] = this.som.getOutput();

		dist = Double.MIN_VALUE;
		int i = this.outputNeuronCount;
		while ((i--) > 0) {
			if (this.won[i] != 0) {
				continue;
			}
			if (output[i] > dist) {
				dist = output[i];
				which = i;
			}
		}

		for (int j = 0; j < input.getInputMatrix().getCols(); j++) {
			outputWeights.set(which, j, input.getInputMatrix().get(0,j));
		}

		normalizeWeight(outputWeights, which);
	}

	/**
	 * Get the best error so far.
	 * @return The best error so far.
	 */
	public double getBestError() {
		return this.bestError;
	}

	/**
	 * Get the error for this iteration.
	 * @return The error for this iteration.
	 */
	public double getTotalError() {
		return this.totalError;
	}

	/**
	 * Called to initialize the SOM.
	 */
	public void initialize() {

		this.som.getOutputWeights().ramdomize(-1, 1);

		for (int i = 0; i < this.outputNeuronCount; i++) {
			normalizeWeight(this.som.getOutputWeights(), i);
		}
	}

	/**
	 * This method is called for each training iteration. Usually this method is
	 * called from inside a loop until the error level is acceptable.
	 */
	public void iteration() {

		evaluateErrors();

		this.totalError = this.globalError;

		if (this.totalError < this.bestError) {
			this.bestError = this.totalError;
			copyWeights(this.som, this.bestnet);
		}

		int winners = 0;
		for (int i = 0; i < this.won.length; i++) {
			if (this.won[i] != 0) {
				winners++;
			}
		}

		if ((winners < this.outputNeuronCount) && (winners < this.train.length)) {
			forceWin();
			return;
		}

		adjustWeights();

		if (this.learnRate > 0.01) {
			this.learnRate *= this.reduction;
		}
	}

	
	/**
	 * Normalize the specified row in the weight matrix.
	 * @param matrix The weight matrix.
	 * @param row The row to normalize.
	 */
	protected void normalizeWeight(final Matrix matrix, final int row) {

		double len = MatrixMath.vectorLength(matrix.getRow(row));
		len = Math.max(len, SelfOrganizingMap.VERYSMALL);

		len = 1.0 / len;
		for (int i = 0; i < this.inputNeuronCount; i++) {
			matrix.set(row, i, matrix.get(row, i) * len);
		}
		matrix.set(row, this.inputNeuronCount, 0);
	}
}
