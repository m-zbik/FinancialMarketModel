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
package com.heatonresearch.book.introneuralnet.neural.matrix;

import com.heatonresearch.book.introneuralnet.neural.exception.MatrixError;
import com.heatonresearch.book.introneuralnet.neural.som.SelfOrganizingMap;

/**
 * MatrixMath: This class can perform many different mathematical
 * operations on matrixes.
 * 
 * @author Jeff Heaton
 * @version 2.1
 */
public class MatrixMath {

	public static Matrix add(final Matrix a, final Matrix b) {
		if (a.getRows() != b.getRows()) {
			throw new MatrixError(
					"To add the matrices they must have the same number of rows and columns.  Matrix a has "
							+ a.getRows()
							+ " rows and matrix b has "
							+ b.getRows() + " rows.");
		}

		if (a.getCols() != b.getCols()) {
			throw new MatrixError(
					"To add the matrices they must have the same number of rows and columns.  Matrix a has "
							+ a.getCols()
							+ " cols and matrix b has "
							+ b.getCols() + " cols.");
		}

		final double result[][] = new double[a.getRows()][a.getCols()];

		for (int resultRow = 0; resultRow < a.getRows(); resultRow++) {
			for (int resultCol = 0; resultCol < a.getCols(); resultCol++) {
				result[resultRow][resultCol] = a.get(resultRow, resultCol)
						+ b.get(resultRow, resultCol);
			}
		}

		return new Matrix(result);
	}

	public static void copy(final Matrix source, final Matrix target) {
		for (int row = 0; row < source.getRows(); row++) {
			for (int col = 0; col < source.getCols(); col++) {
				target.set(row, col, source.get(row, col));
			}
		}

	}

	public static void copy(final SelfOrganizingMap bestnet,
			final SelfOrganizingMap selfOrganizingMap) {
		// TODO Auto-generated method stub

	}

	public static Matrix deleteCol(final Matrix matrix, final int deleted) {
		if (deleted >= matrix.getCols()) {
			throw new MatrixError("Can't delete column " + deleted
					+ " from matrix, it only has " + matrix.getCols()
					+ " columns.");
		}
		final double newMatrix[][] = new double[matrix.getRows()][matrix
				.getCols() - 1];

		for (int row = 0; row < matrix.getRows(); row++) {
			int targetCol = 0;

			for (int col = 0; col < matrix.getCols(); col++) {
				if (col != deleted) {
					newMatrix[row][targetCol] = matrix.get(row, col);
					targetCol++;
				}

			}

		}
		return new Matrix(newMatrix);
	}

	public static Matrix deleteRow(final Matrix matrix, final int deleted) {
		if (deleted >= matrix.getRows()) {
			throw new MatrixError("Can't delete row " + deleted
					+ " from matrix, it only has " + matrix.getRows()
					+ " rows.");
		}
		final double newMatrix[][] = new double[matrix.getRows() - 1][matrix
				.getCols()];
		int targetRow = 0;
		for (int row = 0; row < matrix.getRows(); row++) {
			if (row != deleted) {
				for (int col = 0; col < matrix.getCols(); col++) {
					newMatrix[targetRow][col] = matrix.get(row, col);
				}
				targetRow++;
			}
		}
		return new Matrix(newMatrix);
	}

	public static Matrix divide(final Matrix a, final double b) {
		final double result[][] = new double[a.getRows()][a.getCols()];
		for (int row = 0; row < a.getRows(); row++) {
			for (int col = 0; col < a.getCols(); col++) {
				result[row][col] = a.get(row, col) / b;
			}
		}
		return new Matrix(result);
	}

	public static double dotProduct(final Matrix a, final Matrix b) {
		if (!a.isVector() || !b.isVector()) {
			throw new MatrixError(
					"To take the dot product, both matrices must be vectors.");
		}

		final Double aArray[] = a.toPackedArray();
		final Double bArray[] = b.toPackedArray();

		if (aArray.length != bArray.length) {
			throw new MatrixError(
					"To take the dot product, both matrices must be of the same length.");
		}

		double result = 0;
		final int length = aArray.length;

		for (int i = 0; i < length; i++) {
			result += aArray[i] * bArray[i];
		}

		return result;
	}

	public static Matrix identity(final int size) {
		if (size < 1) {
			throw new MatrixError("Identity matrix must be at least of size 1.");
		}

		final Matrix result = new Matrix(size, size);

		for (int i = 0; i < size; i++) {
			result.set(i, i, 1);
		}

		return result;
	}

	public static Matrix multiply(final Matrix a, final double b) {
		final double result[][] = new double[a.getRows()][a.getCols()];
		for (int row = 0; row < a.getRows(); row++) {
			for (int col = 0; col < a.getCols(); col++) {
				result[row][col] = a.get(row, col) * b;
			}
		}
		return new Matrix(result);
	}

	public static Matrix multiply(final Matrix a, final Matrix b) {
		if (a.getCols() != b.getRows()) {
			throw new MatrixError(
					"To use ordinary matrix multiplication the number of columns on the first matrix must mat the number of rows on the second.");
		}

		final double result[][] = new double[a.getRows()][b.getCols()];

		for (int resultRow = 0; resultRow < a.getRows(); resultRow++) {
			for (int resultCol = 0; resultCol < b.getCols(); resultCol++) {
				double value = 0;

				for (int i = 0; i < a.getCols(); i++) {

					value += a.get(resultRow, i) * b.get(i, resultCol);
				}
				result[resultRow][resultCol] = value;
			}
		}

		return new Matrix(result);
	}

	public static Matrix subtract(final Matrix a, final Matrix b) {
		if (a.getRows() != b.getRows()) {
			throw new MatrixError(
					"To subtract the matrices they must have the same number of rows and columns.  Matrix a has "
							+ a.getRows()
							+ " rows and matrix b has "
							+ b.getRows() + " rows.");
		}

		if (a.getCols() != b.getCols()) {
			throw new MatrixError(
					"To subtract the matrices they must have the same number of rows and columns.  Matrix a has "
							+ a.getCols()
							+ " cols and matrix b has "
							+ b.getCols() + " cols.");
		}

		final double result[][] = new double[a.getRows()][a.getCols()];

		for (int resultRow = 0; resultRow < a.getRows(); resultRow++) {
			for (int resultCol = 0; resultCol < a.getCols(); resultCol++) {
				result[resultRow][resultCol] = a.get(resultRow, resultCol)
						- b.get(resultRow, resultCol);
			}
		}

		return new Matrix(result);
	}

	public static Matrix transpose(final Matrix input) {
		final double inverseMatrix[][] = new double[input.getCols()][input
				.getRows()];

		for (int r = 0; r < input.getRows(); r++) {
			for (int c = 0; c < input.getCols(); c++) {
				inverseMatrix[c][r] = input.get(r, c);
			}
		}

		return new Matrix(inverseMatrix);
	}

	/**
	 * Calculate the length of a vector.
	 * 
	 * @param v
	 *            vector
	 * @return Vector length.
	 */
	public static double vectorLength(final Matrix input) {
		if (!input.isVector()) {
			throw new MatrixError(
					"Can only take the vector length of a vector.");
		}
		final Double v[] = input.toPackedArray();
		double rtn = 0.0;
		for (int i = 0; i < v.length; i++) {
			rtn += Math.pow(v[i], 2);
		}
		return Math.sqrt(rtn);
	}

	private MatrixMath() {
	}

}
