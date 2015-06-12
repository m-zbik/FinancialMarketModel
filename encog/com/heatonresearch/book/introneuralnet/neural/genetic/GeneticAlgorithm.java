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
package com.heatonresearch.book.introneuralnet.neural.genetic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import com.heatonresearch.book.introneuralnet.neural.exception.NeuralNetworkError;

/**
 * GeneticAlgorithm: Implements a genetic algorithm.
 * This is an abstract class.  Other classes are provided in this
 * book that use this base class to train neural networks or
 * provide an answer to the traveling salesman problem.
 * 
 * The genetic algorithm is also capable of using a thread pool
 * to speed execution.  
 *  
 * @author Jeff Heaton
 * @version 2.1
 */
abstract public class GeneticAlgorithm<CHROMOSOME_TYPE extends Chromosome<?, ?>> {
	
	/**
	 * How many chromosomes should be created.
	 */
	private int populationSize;
	
	/**
	 * The percent that should mutate.
	 */
	private double mutationPercent;
	
	/**
	 * What percent should be chosen to mate. They will choose partners from the
	 * entire mating population.
	 */
	private double percentToMate;
	
	/**
	 * Percent of the population that the mating population chooses partners.
	 * from.
	 */
	private double matingPopulation;
	
	/**
	 * Should the same gene be prevented from repeating.
	 */
	private boolean preventRepeat;
	
	/**
	 * How much genetic material should be cut when mating.
	 */
	private int cutLength;
	
	/**
	 * An optional thread pool to use.
	 */
	private ExecutorService pool;

	/**
	 * The population.
	 */
	private CHROMOSOME_TYPE[] chromosomes;

	/**
	 * Get a specific chromosome.
	 * @param i The chromosome to return, 0 for the first one.
	 * @return A chromosome.
	 */
	public CHROMOSOME_TYPE getChromosome(final int i) {
		return this.chromosomes[i];
	}

	/**
	 * Return the entire population.
	 * @return the chromosomes
	 */
	public CHROMOSOME_TYPE[] getChromosomes() {
		return this.chromosomes;
	}

	/**
	 * Get the cut length.
	 * @return
	 */
	public int getCutLength() {
		return this.cutLength;
	}

	/**
	 * Get the mating population.
	 * @return The mating population percent.
	 */
	public double getMatingPopulation() {
		return this.matingPopulation;
	}

	/**
	 * Get the mutation percent.
	 * @return The mutation percent.
	 */
	public double getMutationPercent() {
		return this.mutationPercent;
	}

	/**
	 * Get the percent to mate.
	 * @return The percent to mate.
	 */
	public double getPercentToMate() {
		return this.percentToMate;
	}

	/**
	 * Get the optional threadpool.
	 * @return the pool
	 */
	public ExecutorService getPool() {
		return this.pool;
	}

	/**
	 * Get the population size.
	 * @return The population size.
	 */
	public int getPopulationSize() {
		return this.populationSize;
	}

	/**
	 * Should repeating genes be prevented.
	 * @return True if repeating genes should be prevented.
	 */
	public boolean isPreventRepeat() {
		return this.preventRepeat;
	}

	/**
	 * Modify the weight matrix and thresholds based on the last call to
	 * calcError.
	 * 
	 * @throws NeuralNetworkException
	 */
	public void iteration() throws NeuralNetworkError {

		final int countToMate = (int) (getPopulationSize() * getPercentToMate());
		final int offspringCount = countToMate * 2;
		int offspringIndex = getPopulationSize() - offspringCount;
		final int matingPopulationSize = (int) (getPopulationSize() * getMatingPopulation());

		final Collection<Callable<Integer>> tasks = new ArrayList<Callable<Integer>>();

		// mate and form the next generation
		for (int i = 0; i < countToMate; i++) {
			final CHROMOSOME_TYPE mother = this.chromosomes[i];
			final int fatherInt = (int) (Math.random() * matingPopulationSize);
			final CHROMOSOME_TYPE father = this.chromosomes[fatherInt];
			final CHROMOSOME_TYPE child1 = this.chromosomes[offspringIndex];
			final CHROMOSOME_TYPE child2 = this.chromosomes[offspringIndex + 1];

			final MateWorker<CHROMOSOME_TYPE> worker = new MateWorker<CHROMOSOME_TYPE>(
					mother, father, child1, child2);

			try {
				if (this.pool != null) {
					tasks.add(worker);
				} else {
					worker.call();
				}
			} catch (final Exception e) {
				e.printStackTrace();
			}

			// mother.mate(father,chromosomes[offspringIndex],chromosomes[offspringIndex+1]);
			offspringIndex += 2;
		}

		if (this.pool != null) {
			try {
				this.pool.invokeAll(tasks, 120, TimeUnit.SECONDS);
			} catch (final InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		// sort the next generation
		sortChromosomes();
	}

	/**
	 * Set the specified chromosome.
	 * @param i The chromosome to set.
	 * @param value The value for the specified chromosome.
	 */
	public void setChromosome(final int i, final CHROMOSOME_TYPE value) {
		this.chromosomes[i] = value;
	}

	/**
	 * Set the entire population.
	 * @param chromosomes
	 *            the chromosomes to set
	 */
	public void setChromosomes(final CHROMOSOME_TYPE[] chromosomes) {
		this.chromosomes = chromosomes;
	}

	/**
	 * Set the cut length.
	 * @param cutLength The cut length.
	 */
	public void setCutLength(final int cutLength) {
		this.cutLength = cutLength;
	}

	/**
	 * Set the mating population percent.
	 * @param matingPopulation The mating population percent.
	 */
	public void setMatingPopulation(final double matingPopulation) {
		this.matingPopulation = matingPopulation;
	}

	/**
	 * Set the mutation percent.
	 * @param mutationPercent
	 */
	public void setMutationPercent(final double mutationPercent) {
		this.mutationPercent = mutationPercent;
	}

	/**
	 * Set the percent to mate.
	 * @param percentToMate
	 */
	public void setPercentToMate(final double percentToMate) {
		this.percentToMate = percentToMate;
	}

	/**
	 * Set the optional thread pool.
	 * @param pool
	 *            the pool to set
	 */
	public void setPool(final ExecutorService pool) {
		this.pool = pool;
	}

	/**
	 * Set the population size.
	 * @param populationSize The population size.
	 */
	public void setPopulationSize(final int populationSize) {
		this.populationSize = populationSize;
	}

	/**
	 * Set the gene
	 * @param preventRepeat
	 */
	public void setPreventRepeat(final boolean preventRepeat) {
		this.preventRepeat = preventRepeat;
	}

	public void sortChromosomes() {
		Arrays.sort(this.chromosomes);
	}

}
