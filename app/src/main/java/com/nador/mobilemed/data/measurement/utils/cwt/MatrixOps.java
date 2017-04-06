package com.nador.mobilemed.data.measurement.utils.cwt;

/**
 * Copyright 2014 Mark Bishop This program is free software: you can
 * redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details: http://www.gnu.org/licenses
 * 
 * The author makes no warranty for the accuracy, completeness, safety, or
 * usefulness of any information provided and does not represent that its use
 * would not infringe privately owned right.
 */

import java.util.ArrayList;

/**
 * Class responsibility: Provide methods for basic vector and matrix
 * computations.
 * 
 * @author max
 *
 */
public class MatrixOps {

	/**
	 * 
	 * @return gives an upper bound on the relative error due to rounding in
	 *         floating point arithmetic
	 */
	public static double MachineEpsilonDouble() {
		double eps = 1.0;
		do
			eps /= 2.0;
		while ((1.0 + (eps / 2.0)) != 1.0);
		return eps;
	}

	/**
	 * 
	 * @param n
	 * @return Identity matrix I[n,n]
	 */
	public static ComplexNumber[][] eyeComplex(int n) {
		ComplexNumber[][] eye = new ComplexNumber[n][n];
		initialize(eye);
		for (int i = 0; i < n; i++) {
			eye[i][i].Real = 1.0;
		}
		return eye;
	}

	/**
	 * 
	 * @param n
	 * @return Identity matrix I[n,n]
	 */
	public static double[][] eye(int n) {
		double[][] eye = new double[n][n];
		for (int i = 0; i < n; i++) {
			eye[i][i] = 1.0;
		}
		return eye;
	}
	
	////////////Array Management////////////////

	/**
	 * 
	 * When a ComplexNumber[] is created (new ComplexNumber[n]()), its elements
	 * are null. If a function includes an assignment where an null element
	 * appears on the right-hand side of the assignment (e.g. c = c + a*b) prior
	 * to instance creation an error will be thrown. This method initializes the
	 * array with zeros and prevents the error.
	 */
	public static ComplexNumber[] initialize(ComplexNumber[] v) {
		int n = v.length;
		for (int i = 0; i < n; i++) {
			v[i] = new ComplexNumber();
		}
		return v;
	}

	/**
	 * 
	 * When a ComplexNumber[][] is created (new ComplexNumber[m][n]()), its
	 * elements are null. If a function includes an assignment where a null
	 * element appears on the right-hand side of the assignment (e.g. c = c +
	 * a*b) prior to instance creation an error will be thrown. This method
	 * initializes the array and prevents the error.
	 */
	public static ComplexNumber[][] initialize(ComplexNumber[][] A) {
		int m = A.length;
		for (int i = 0; i < m; i++) {
			A[i] = initialize(A[i]);
		}
		return A;
	}
	
	/**
	 * 
	 * @param A A[rows][columns]
	 * @return ArrayList of doubles[] such that each double[] is a row from A
	 */
	public static ArrayList<double[]> toArraylist(double[][] A){
		ArrayList<double[]> listRows = new ArrayList<double[]>();		
		for(int i = 0;i<A.length;i++){
			listRows.add(A[i]);
		}		
		return listRows;
	}
	
	public static double[][] toDouble(ArrayList<double[]> columnVectors) {
		int m = columnVectors.get(0).length;
		int n = columnVectors.size();
		double[][] matrix = new double[m][n];
		for (int i = 0; i < m; i++) {
			for (int j = 0; j < n; j++) {
				matrix[i][j] = columnVectors.get(j)[i];
			}
		}
		return matrix;
	}

	public static ComplexNumber[][] toComplex(double[][] A) {
		int m = A.length;
		int n = A[0].length;
		ComplexNumber[][] cA = new ComplexNumber[m][n];
		for (int i = 0; i < m; i++) {
			for (int j = 0; j < n; j++) {
				cA[i][j] = new ComplexNumber(A[i][j], 0);
			}
		}
		return cA;
	}

	public static double[][] getRealPart(ComplexNumber[][] A) {
		int m = A.length;
		int n = A[0].length;
		double[][] realPart = new double[m][n];
		for (int i = 0; i < m; i++) {
			for (int j = 0; j < n; j++) {
				realPart[i][j] = A[i][j].Real;
			}
		}
		return realPart;
	}
	
	public static double[] getRealPart(ComplexNumber[] v) {
		int m = v.length;
		
		double[] realPart = new double[m];
		for (int i = 0; i < m; i++) {
			
				realPart[i] = v[i].Real;
			
		}
		return realPart;
	}
	

	public static double[][] getImaginaryPart(ComplexNumber[][] A) {
		int m = A.length;
		int n = A[0].length;
		double[][] realPart = new double[m][n];
		for (int i = 0; i < m; i++) {
			for (int j = 0; j < n; j++) {
				realPart[i][j] = A[i][j].Imaginary;
			}
		}
		return realPart;
	}

	public static double[] getImaginaryPart(ComplexNumber[] v) {
		int m = v.length;
		
		double[] realPart = new double[m];
		for (int i = 0; i < m; i++) {
			
				realPart[i] = v[i].Imaginary;
			
		}
		return realPart;
	}
	
	public static ComplexNumber[] getColumnAsVector(ComplexNumber[][] A,
			int colIndex) {
		int m = A.length;
		ComplexNumber[] col = new ComplexNumber[m];
		for (int i = 0; i < m; i++) {
			col[i] = A[i][colIndex];
		}
		return col;
	}

	public static double[] getColumnAsVector(double[][] A, int colIndex) {
		int m = A.length;
		double[] col = new double[m];
		for (int i = 0; i < m; i++) {
			col[i] = A[i][colIndex];
		}
		return col;
	}

	public static ComplexNumber[] getRowAsVector(ComplexNumber[][] A,
			int rowIndex) {
		int n = A[0].length;
		System.out.print(A.length + "\n");
		System.out.print(A[0].length + "\n");
		ComplexNumber[] col = new ComplexNumber[n];
		for (int i = 0; i < n; i++) {
			col[i] = A[rowIndex][i];
		}
		return col;
	}

	public static double[] getRowAsVector(double[][] A, int rowIndex) {
		int n = A[0].length;
		System.out.print(A.length + "\n");
		System.out.print(A[0].length + "\n");
		double[] col = new double[n];
		for (int i = 0; i < n; i++) {
			col[i] = A[rowIndex][i];
		}
		return col;
	}

	public static double[][] vectorToRowMatrix(double[] v) {
		int n = v.length;
		double[][] row = new double[1][n];
		for (int i = 0; i < n; i++) {
			row[0][i] = v[i];
		}
		return row;
	}

	public static ComplexNumber[][] vectorToRowMatrix(ComplexNumber[] v) {
		int n = v.length;
		ComplexNumber[][] row = new ComplexNumber[1][n];
		for (int i = 0; i < n; i++) {
			row[0][i] = v[i];
		}
		return row;
	}

	public static double[][] vectorToColumnMatrix(double[] v) {
		int n = v.length;
		double[][] row = new double[n][1];
		for (int i = 0; i < n; i++) {
			row[i][0] = v[i];
		}
		return row;
	}

	public static ComplexNumber[][] vectorToColumnMatrix(ComplexNumber[] v) {
		int n = v.length;
		ComplexNumber[][] row = new ComplexNumber[n][1];
		for (int i = 0; i < n; i++) {
			row[i][0] = v[i];
		}
		return row;
	}

	public static ComplexNumber[] toComplex(double[] v) {
		int n = v.length;
		ComplexNumber[] cV = new ComplexNumber[n];
		for (int i = 0; i < n; i++) {
			cV[i] = new ComplexNumber(v[i], 0);
		}
		return cV;
	}
	
	/**
	 * 
	 * @param x
	 *            the sequence to pad
	 * @return If necessary, expanded sequence such that its length is an even
	 *         power of 2 by adding additional zero values.
	 */
	public static double[] padPow2(double[] x) {
		int sizeIn = x.length;
		double log2N = Math.log(sizeIn) / Math.log(2);
		double ceiling = Math.ceil(log2N);
		if (log2N < ceiling) {
			log2N = ceiling;
			int sizePad = (int) Math.pow(2, log2N);
			double[] padX = new double[sizePad];
			for (int i = 0; i < sizePad; i++) {
				if (i < sizeIn) {
					padX[i] = x[i];
				} else {
					padX[i] = 0;
				}
			}
			return padX;
		} else {
			return x;
		}
	}

	/**
	 * 
	 * @param xy
	 *            A double[][] where xy[0] = x and xy[1] = f(x)
	 * @return If necessary, expanded sequence such that its length is an even
	 *         power of 2 by adding additional zero values.
	 */
	public static double[][] padPow2(double[][] xy) {
		int sizeIn = xy[0].length;
		double log2N = Math.log(sizeIn) / Math.log(2);
		double ceiling = Math.ceil(log2N);
		if (log2N < ceiling) {
			log2N = ceiling;
			int sizePad = (int) Math.pow(2, log2N);
			double[][] padXY = new double[2][sizePad];
			double dx = padXY[0][1] - padXY[0][0];
			for (int i = 0; i < sizePad; i++) {
				if (i < sizeIn) {
					padXY[0][i] = xy[0][i];
					padXY[1][i] = xy[1][i];
				} else {
					padXY[0][i] = padXY[0][i - 1] + dx;
					padXY[1][i] = 0;
				}
			}
			return padXY;
		} else {
			return xy;
		}
	}

	/**
	 * 
	 * @param real
	 *            a vector of real numbers that may be a fixed-stride
	 *            representation of a matrix
	 * @return a CUDA compatible complex interleaved
	 */
	public static ComplexNumber[] interleavedRealToComplex(float[] real) {
		int len = real.length;
		ComplexNumber[] result = new ComplexNumber[len / 2];
		for (int i = 0; i < len; i += 2) {
			result[i / 2].Real = real[i];
			result[i / 2].Imaginary = real[i + 1];
		}
		return result;
	}

	public static double[] deepCopy(double[] v) {
		int n = v.length;
		double[] copy = new double[n];
		for (int j = 0; j < n; j++) {
			copy[j] = v[j];
		}
		return copy;
	}

	public static double[][] deepCopy(double[][] A) {
		int m = A.length;
		int n = A[0].length;
		double[][] copy = new double[m][n];
		for (int i = 0; i < m; i++) {
			for (int j = 0; j < n; j++) {
				copy[i][j] = A[i][j];
			}
		}
		return copy;
	}

	public static ComplexNumber[] deepCopy(ComplexNumber[] v) {
		int n = v.length;
		ComplexNumber[] copy = new ComplexNumber[n];
		for (int j = 0; j < n; j++) {
			copy[j] = v[j];
		}
		return copy;
	}

	public static ComplexNumber[][] deepCopy(ComplexNumber[][] A) {
		int m = A.length;
		int n = A[0].length;
		ComplexNumber[][] copy = new ComplexNumber[m][n];
		for (int i = 0; i < m; i++) {
			for (int j = 0; j < n; j++) {
				copy[i][j] = A[i][j];
			}
		}
		return copy;
	}
	
	public static ArrayList<double[]> deepCopy(ArrayList<double[]> A){
		ArrayList<double[]> copy = new ArrayList<double[]>();
		for(double[] vector : A){
			int len = vector.length;
			double[] cVector=new double[len];
			for(int i = 0; i<len;i++){
				cVector[i] = vector[i];
			}
			copy.add(cVector);
		}		
		return copy;
	}

	
	//Operations
	
	public static double[][] modulus(ComplexNumber[][] A) {
		int m = A.length;
		int n = A[0].length;
		double[][] absA = new double[m][n];
		for (int i = 0; i < m; i++) {
			for (int j = 0; j < n; j++) {
				absA[i][j] = ComplexCalc.modulus(A[i][j]);
			}
		}
		return absA;
	}

	public static double[] modulus(ComplexNumber[] v) {
		int n = v.length;
		double[] absV = new double[n];
		for (int i = 0; i < n; i++) {
			absV[i] = ComplexCalc.modulus(v[i]);
		}
		return absV;
	}

	public static ComplexNumber[] conjugate(ComplexNumber[] v) {
		int n = v.length;
		ComplexNumber[] conj = new ComplexNumber[n];
		for (int i = 0; i < n; i++) {
			conj[i] = ComplexCalc.conjugate(v[i]);
		}
		return conj;
	}
	
	public static ComplexNumber[][] conjugate(ComplexNumber[][] A) {
		int m = A.length;		
		ComplexNumber[][] conj = new ComplexNumber[m][];
		for (int i = 0; i < m; i++) {
			int n = A[i].length;
			conj[i]= new ComplexNumber[n];
			for(int j = 0;j<n;j++){
				
			conj[i][j] = ComplexCalc.conjugate(A[i][j]);	
			}			
		}
		return conj;
	}

	// ////////////////// Transposition ////////////

	public static ComplexNumber[][] transpose(ComplexNumber[][] A) {
		int m = A.length;
		int n = A[0].length;
		ComplexNumber[][] AT = new ComplexNumber[n][m];
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < m; j++) {
				AT[i][j] = A[j][i];
			}
		}
		return AT;
	}

	public static double[][] transpose(double[][] A) {
		int m = A.length;
		int n = A[0].length;
		double[][] AT = new double[n][m];
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < m; j++) {
				AT[i][j] = A[j][i];
			}
		}
		return AT;
	}

	public static ComplexNumber[][] conjugateTranspose(ComplexNumber[][] A) {
		int m = A.length;
		int n = A[0].length;
		ComplexNumber[][] conjT = new ComplexNumber[n][m];
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < m; j++) {
				conjT[i][j] = ComplexCalc.conjugate(A[j][i]);
			}
		}
		return conjT;
	}

	// /////////////////// scale ///////////////////////

	public static ComplexNumber[][] scale(ComplexNumber alpha,
			ComplexNumber[][] A) {
		int m = A.length;
		int n = A[0].length;
		ComplexNumber[][] alphaA = new ComplexNumber[m][n];
		for (int i = 0; i < m; i++) {
			for (int j = 0; j < n; j++) {
				alphaA[i][j] = ComplexCalc.multiply(alpha, A[i][j]);
			}
		}
		return alphaA;
	}

	public static ComplexNumber[][] scale(double alpha, ComplexNumber[][] A) {
		int m = A.length;
		int n = A[0].length;
		ComplexNumber[][] alphaA = new ComplexNumber[m][n];
		for (int i = 0; i < m; i++) {
			for (int j = 0; j < n; j++) {
				alphaA[i][j] = ComplexCalc.multiply(alpha, A[i][j]);
			}
		}
		return alphaA;
	}

	public static ComplexNumber[][] scale(ComplexNumber alpha, double[][] A) {
		int m = A.length;
		int n = A[0].length;
		ComplexNumber[][] alphaA = new ComplexNumber[m][n];
		for (int i = 0; i < m; i++) {
			for (int j = 0; j < n; j++) {
				alphaA[i][j] = ComplexCalc.multiply(alpha, A[i][j]);
			}
		}
		return alphaA;
	}

	public static double[][] scale(double alpha, double[][] A) {
		int m = A.length;
		int n = A[0].length;
		double[][] alphaA = new double[m][n];
		for (int i = 0; i < m; i++) {
			for (int j = 0; j < n; j++) {
				alphaA[i][j] = alpha * A[i][j];
			}
		}
		return alphaA;
	}

	public static ComplexNumber[] scale(ComplexNumber alpha, ComplexNumber[] v) {
		int n = v.length;
		ComplexNumber[] alphaV = new ComplexNumber[n];
		for (int i = 0; i < n; i++) {
			alphaV[i] = ComplexCalc.multiply(alpha, v[i]);
		}
		return alphaV;
	}

	public static ComplexNumber[] scale(double alpha, ComplexNumber[] v) {
		int n = v.length;
		ComplexNumber[] alphaV = new ComplexNumber[n];
		for (int i = 0; i < n; i++) {
			alphaV[i] = ComplexCalc.multiply(alpha, v[i]);
		}
		return alphaV;
	}

	public static ComplexNumber[] scale(ComplexNumber alpha, double[] v) {
		int n = v.length;
		ComplexNumber[] alphaV = new ComplexNumber[n];
		for (int i = 0; i < n; i++) {
			alphaV[i] = ComplexCalc.multiply(alpha, v[i]);
		}
		return alphaV;
	}

	public static double[] scale(double alpha, double[] v) {
		int n = v.length;
		double[] alphaV = new double[n];
		for (int i = 0; i < n; i++) {
			alphaV[i] = alpha * v[i];
		}
		return alphaV;
	}

	// /////////////////// add ///////////////////////

	public static ComplexNumber[][] add(ComplexNumber[][] A, ComplexNumber[][] B) {
		int m = A.length;
		int n = A[0].length;
		ComplexNumber[][] C = new ComplexNumber[m][n];
		for (int i = 0; i < m; i++) {
			for (int j = 0; j < n; j++) {
				C[i][j] = ComplexCalc.add(A[i][j], B[i][j]);
			}
		}
		return C;
	}

	public static ComplexNumber[][] add(ComplexNumber[][] A, double[][] B) {
		int m = A.length;
		int n = A[0].length;
		ComplexNumber[][] C = new ComplexNumber[m][n];
		for (int i = 0; i < m; i++) {
			for (int j = 0; j < n; j++) {
				C[i][j] = ComplexCalc.add(A[i][j], B[i][j]);
			}
		}
		return C;
	}

	public static ComplexNumber[][] add(double[][] A, ComplexNumber[][] B) {
		int m = A.length;
		int n = A[0].length;
		ComplexNumber[][] C = new ComplexNumber[m][n];
		for (int i = 0; i < m; i++) {
			for (int j = 0; j < n; j++) {
				C[i][j] = ComplexCalc.add(A[i][j], B[i][j]);
			}
		}
		return C;
	}

	public static double[][] add(double[][] A, double[][] B) {
		int m = A.length;
		int n = A[0].length;
		double[][] C = new double[m][n];
		for (int i = 0; i < m; i++) {
			for (int j = 0; j < n; j++) {
				C[i][j] = (A[i][j] + B[i][j]);
			}
		}
		return C;
	}

	public static ComplexNumber[] add(ComplexNumber[] v1, ComplexNumber[] v2) {
		int n = v1.length;
		ComplexNumber[] v1Pv2 = new ComplexNumber[n];
		for (int i = 0; i < n; i++) {
			v1Pv2[i] = ComplexCalc.add(v1[i], v2[i]);
		}
		return v1Pv2;
	}

	public static ComplexNumber[] add(double[] v1, ComplexNumber[] v2) {
		int n = v1.length;
		ComplexNumber[] v1Pv2 = new ComplexNumber[n];
		for (int i = 0; i < n; i++) {
			v1Pv2[i] = ComplexCalc.add(v1[i], v2[i]);
		}
		return v1Pv2;
	}

	public static ComplexNumber[] add(ComplexNumber[] v1, double[] v2) {
		int n = v1.length;
		ComplexNumber[] v1Pv2 = new ComplexNumber[n];
		for (int i = 0; i < n; i++) {
			v1Pv2[i] = ComplexCalc.add(v1[i], v2[i]);
		}
		return v1Pv2;
	}

	public static double[] add(double[] v1, double[] v2) {
		int n = v1.length;
		double[] v1Pv2 = new double[n];
		for (int i = 0; i < n; i++) {
			v1Pv2[i] = v1[i] + v2[i];
		}
		return v1Pv2;
	}

	// /////////////////// multiply ///////////////////////

	public static ComplexNumber[][] multiply(ComplexNumber[][] A,
			ComplexNumber[][] B) {
		int m = A.length;
		int p = A[0].length;
		int n = B[0].length;
		ComplexNumber[][] C = new ComplexNumber[m][n];
		initialize(C);
		for (int i = 0; i < m; i++) {
			for (int k = 0; k < p; k++) {
				for (int j = 0; j < n; j++) {
					C[i][j] = ComplexCalc.add(C[i][j],
							ComplexCalc.multiply(A[i][k], B[k][j]));
				}
			}
		}
		return C;
	}

	public static ComplexNumber[][] multiply(double[][] A, ComplexNumber[][] B) {
		int m = A.length;
		int p = A[0].length;
		int n = B[0].length;
		ComplexNumber[][] C = new ComplexNumber[m][n];
		initialize(C);
		for (int i = 0; i < m; i++) {
			for (int k = 0; k < p; k++) {
				for (int j = 0; j < n; j++) {
					C[i][j] = ComplexCalc.add(C[i][j],
							ComplexCalc.multiply(A[i][k], B[k][j]));
				}
			}
		}
		return C;
	}

	public static ComplexNumber[][] multiply(ComplexNumber[][] A, double[][] B) {
		int m = A.length;
		int p = A[0].length;
		int n = B[0].length;
		ComplexNumber[][] C = new ComplexNumber[m][n];
		initialize(C);
		for (int i = 0; i < m; i++) {
			for (int k = 0; k < p; k++) {
				for (int j = 0; j < n; j++) {
					C[i][j] = ComplexCalc.add(C[i][j],
							ComplexCalc.multiply(A[i][k], B[k][j]));
				}
			}
		}
		return C;
	}

	public static double[][] multiply(double[][] A, double[][] B) {
		int m = A.length;
		int p = A[0].length;
		int n = B[0].length;
		double[][] C = new double[m][n];
		for (int i = 0; i < m; i++) {
			for (int k = 0; k < p; k++) {
				for (int j = 0; j < n; j++) {
					C[i][j] = C[i][j] + A[i][k] * B[k][j];
				}
			}
		}
		return C;
	}

	public static double[] multiply(double[][] A, double[] b) {
		int m = A.length;
		int n = A[0].length;
		double[] v = new double[m];
		for (int k = 0; k < n; k++) {
			for (int i = 0; i < m; i++) {
				v[i] += A[i][k] * b[k];
			}
		}
		return v;
	}

	public static ComplexNumber[] multiply(ComplexNumber[][] A, double[] b) {
		int m = A.length;
		int n = A[0].length;
		ComplexNumber[] v = new ComplexNumber[m];
		initialize(v);
		for (int k = 0; k < n; k++) {
			for (int i = 0; i < m; i++) {
				v[i] = ComplexCalc.add(v[i],
						ComplexCalc.multiply(A[i][k], b[k]));
			}
		}
		return v;
	}

	public static ComplexNumber[] multiply(double[][] A, ComplexNumber[] b) {
		int m = A.length;
		int n = A[0].length;
		ComplexNumber[] v = new ComplexNumber[m];
		initialize(v);
		for (int k = 0; k < n; k++) {
			for (int i = 0; i < m; i++) {
				v[i] = ComplexCalc.add(v[i],
						ComplexCalc.multiply(A[i][k], b[k]));
			}
		}
		return v;
	}

	public static ComplexNumber[] multiply(ComplexNumber[][] A,
			ComplexNumber[] b) {
		int m = A.length;
		int n = A[0].length;
		ComplexNumber[] v = new ComplexNumber[m];
		initialize(v);
		for (int k = 0; k < n; k++) {
			for (int i = 0; i < m; i++) {
				v[i] = ComplexCalc.add(v[i],
						ComplexCalc.multiply(A[i][k], b[k]));
			}
		}
		return v;
	}

	// ************************* Hadamand product ************************

	public static ComplexNumber[][] Hadamard(ComplexNumber[][] A,
			ComplexNumber[][] B) {
		int m = A.length;
		int n = A[0].length;
		ComplexNumber[][] C = new ComplexNumber[m][n];
		for (int i = 0; i < m; i++) {
			for (int j = 0; j < n; j++) {
				C[i][j] = ComplexCalc.multiply(A[i][j], B[i][j]);
			}
		}
		return C;
	}

	public static ComplexNumber[][] Hadamard(ComplexNumber[][] A, double[][] B) {
		int m = A.length;
		int n = A[0].length;
		ComplexNumber[][] C = new ComplexNumber[m][n];
		for (int i = 0; i < m; i++) {
			for (int j = 0; j < n; j++) {
				C[i][j] = ComplexCalc.multiply(A[i][j], B[i][j]);
			}
		}
		return C;
	}

	public static ComplexNumber[][] Hadamard(double[][] A, ComplexNumber[][] B) {
		int m = A.length;
		int n = A[0].length;
		ComplexNumber[][] C = new ComplexNumber[m][n];
		for (int i = 0; i < m; i++) {
			for (int j = 0; j < n; j++) {
				C[i][j] = ComplexCalc.multiply(A[i][j], B[i][j]);
			}
		}
		return C;
	}

	public static double[][] Hadamard(double[][] A, double[][] B) {
		int m = A.length;
		int n = A[0].length;
		double[][] C = new double[m][n];
		for (int i = 0; i < m; i++) {
			for (int j = 0; j < n; j++) {
				C[i][j] = A[i][j] * B[i][j];
			}
		}
		return C;
	}

	public static ComplexNumber[] Hadamard(double[] A, ComplexNumber[] B) {
		int m = A.length;
		ComplexNumber[] C = new ComplexNumber[m];
		for (int i = 0; i < m; i++) {
			C[i] = ComplexCalc.multiply(A[i], B[i]);
		}
		return C;
	}

	public static ComplexNumber[] Hadamard(ComplexNumber[] A, double[] B) {
		int m = A.length;
		ComplexNumber[] C = new ComplexNumber[m];
		for (int i = 0; i < m; i++) {
			C[i] = ComplexCalc.multiply(A[i], B[i]);
		}
		return C;
	}

	public static ComplexNumber[] Hadamard(ComplexNumber[] A, ComplexNumber[] B) {
		int m = A.length;
		ComplexNumber[] C = new ComplexNumber[m];
		for (int i = 0; i < m; i++) {
			C[i] = ComplexCalc.multiply(A[i], B[i]);
		}
		return C;
	}

	public static double[] Hadamard(double[] A, double[] B) {
		int m = A.length;
		double[] C = new double[m];
		for (int i = 0; i < m; i++) {
			C[i] = A[i] * B[i];
		}
		return C;
	}

	// ///// inner product

	/**
	 * 
	 * @param a
	 *            a[n]
	 * @param b
	 *            a[n]
	 * @return ab[n] = a dot b
	 */
	public static double vectorInnerProduct(double[] a, double[] b) {
		int n = a.length;
		double ab = 0;
		for (int i = 0; i < n; i++) {
			ab += a[i] * b[i];
		}
		return ab;
	}

	public static ComplexNumber vectorInnerProduct(ComplexNumber[] a, double[] b) {
		int n = a.length;
		ComplexNumber ab = new ComplexNumber();
		for (int i = 0; i < n; i++) {
			ab = ComplexCalc.add(ab, ComplexCalc.multiply(a[i], b[i]));
		}
		return ab;
	}

	public static ComplexNumber vectorInnerProduct(double[] a, ComplexNumber[] b) {
		int n = a.length;
		ComplexNumber ab = new ComplexNumber();
		for (int i = 0; i < n; i++) {
			ab = ComplexCalc.add(ab, ComplexCalc.multiply(a[i], b[i]));
		}
		return ab;
	}

	public static ComplexNumber vectorInnerProduct(ComplexNumber[] a,
			ComplexNumber[] b) {
		int n = a.length;
		ComplexNumber ab = new ComplexNumber();
		;
		for (int i = 0; i < n; i++) {
			ab = ComplexCalc.add(ab, ComplexCalc.multiply(a[i], b[i]));
		}
		return ab;
	}

	// /// tensor product

	public static double[][] vectorOuterProduct(double[] v1, double[] v2) {
		int m = v1.length;
		int n = v2.length;
		double[][] C = new double[m][n];
		for (int i = 0; i < m; i++) {
			for (int j = 0; j < n; j++) {
				C[i][j] += v1[i] * v2[j];
			}
		}
		return C;
	}

	public static ComplexNumber[][] vectorOuterProduct(ComplexNumber[] v1,
			double[] v2) {
		int m = v1.length;
		int n = v2.length;
		ComplexNumber[][] C = new ComplexNumber[m][n];
		for (int i = 0; i < m; i++) {
			for (int j = 0; j < n; j++) {
				C[i][j] = ComplexCalc.multiply(v1[i], v2[j]);
			}
		}
		return C;
	}

	public static ComplexNumber[][] vectorOuterProduct(double[] v1,
			ComplexNumber[] v2) {
		int m = v1.length;
		int n = v2.length;
		ComplexNumber[][] C = new ComplexNumber[m][n];
		for (int i = 0; i < m; i++) {
			for (int j = 0; j < n; j++) {
				C[i][j] = ComplexCalc.multiply(v1[i], v2[j]);
			}
		}
		return C;
	}

	public static ComplexNumber[][] vectorOuterProduct(ComplexNumber[] v1,
			ComplexNumber[] v2) {
		int m = v1.length;
		int n = v2.length;
		ComplexNumber[][] C = new ComplexNumber[m][n];
		for (int i = 0; i < m; i++) {
			for (int j = 0; j < n; j++) {
				C[i][j] = ComplexCalc.multiply(v1[i], v2[j]);
			}
		}
		return C;
	}

	// //// norms

	public static double vector2Norm(double[] v) {
		int n = v.length;
		double norm = 0;
		for (int i = 0; i < n; i++) {
			norm += Math.pow(v[i], 2);
		}
		norm = Math.pow(norm, 0.5);
		return norm;
	}

	public static double vector2Norm(ComplexNumber[] v) {
		int n = v.length;
		double norm = 0;
		for (int i = 0; i < n; i++) {
			norm += Math.pow(ComplexCalc.modulus(v[i]), 2);
		}
		norm = Math.pow(norm, 0.5);
		return norm;
	}

	public static double matrixFNorm(double[][] A) {
		int m = A.length;
		int n = A[0].length;
		double norm = 0;
		for (int i = 0; i < m; i++) {
			for (int j = 0; j < n; j++) {
				norm += (Math.pow(A[i][j], 2));
			}
		}
		norm = Math.pow(norm, 0.5);
		return norm;
	}

	/**
	 * 
	 * @param v
	 *            vector v[n] as double[]
	 * @return matrix F norm of v
	 */
	public static double matrixFNorm(ComplexNumber[][] A) {
		int m = A.length;
		int n = A[0].length;
		double norm = 0;
		for (int i = 0; i < m; i++) {
			for (int j = 0; j < n; j++) {
				norm += Math.pow(ComplexCalc.modulus(A[i][j]), 2);
			}
		}
		norm = Math.pow(norm, 0.5);
		return norm;
	}

	/**
	 * 
	 * @param v
	 *            vector v[n] as double[]
	 * @return InfinityNorm(v) ... absolute value of highest magnitude element
	 */
	public static double vectorInfinityNorm(double[] v) {
		double lFinity = 0;
		int n = v.length;
		lFinity = Math.abs(v[0]);
		for (int i = 1; i < n; i++) {
			double abs = Math.abs(v[i]);
			if (abs > lFinity) {
				lFinity = abs;
			}
		}
		return lFinity;
	}

	public static double vectorInfinityNorm(ComplexNumber[] vector) {// Linfty
																		// Norm
		int n = vector.length;
		double max = 0;
		for (int i = 0; i < n; i++) {
			double mod = ComplexCalc.modulus(vector[i]);
			if (mod > max) {
				max = mod;
			}
		}

		return max;
	}
	
	// min / max
	
	public static double max(ArrayList<double[]> vectorList){
		double max=Double.NEGATIVE_INFINITY;
		for(int i = 0;i<vectorList.size();i++){
			for(int j = 0;j<vectorList.get(i).length;j++){
				if(vectorList.get(i)[j] > max){
					max=vectorList.get(i)[j];
				}
			}
		}		
		return max;		
	}
	
	public static double min(ArrayList<double[]> vectorList){
		double min=Double.POSITIVE_INFINITY;
		for(int i = 0;i<vectorList.size();i++){
			for(int j = 0;j<vectorList.get(i).length;j++){
				if(vectorList.get(i)[j] < min){
					min=vectorList.get(i)[j];
				}
			}
		}		
		return min;		
	}
	
	public static double min(double[] A) {
		int n = A.length;
		double min = Double.POSITIVE_INFINITY;
		for (int i = 0; i < n; i++) {
			if (A[i] < min) {
				min = A[i];
			}
		}
		return min;
	}

	public static double max(double[] A) {
		int n = A.length;
		double max = Double.NEGATIVE_INFINITY;
		for (int i = 0; i < n; i++) {
			if (A[i] > max) {
				max = A[i];
			}
		}
		return max;
	}
	
	public static double min(double[][] A) {
		int m = A.length;
		int n = A[0].length;
		double min = Double.POSITIVE_INFINITY;
		for (int i = 0; i < m; i++) {
			for (int j = 0; j < n; j++) {
				if (A[i][j] < min) {
					min = A[i][j];
				}
			}
		}
		return min;
	}

	public static double max(double[][] A) {
		int m = A.length;
		int n = A[0].length;
		double max = Double.NEGATIVE_INFINITY;
		for (int i = 0; i < m; i++) {
			for (int j = 0; j < n; j++) {
				if (A[i][j] > max) {
					max = A[i][j];
				}
			}
		}
		return max;
	}
	
	public static int max(int[] vector) {
		int max = vector[0];
		for (int i = 1; i < vector.length; i++) {
			if (vector[i] > max) {
				max = vector[i];
			}
		}
		return max;
	}

	public static int min(int[] vector) {
		int min = vector[0];
		for (int i = 1; i < vector.length; i++) {
			if (vector[i] < min) {
				min = vector[i];
			}
		}
		return min;
	}
	
	/**
	 * 
	 * @param plots
	 *            p sets of xy data as a double[p][index][] array where, for p
	 *            sets, plots[0:p-1][0] = x and plots[0:p-1][1] = y
	 * @param index
	 *            0 for x and 1 for y
	 * @return minimum of all x or y
	 */
	public static double min(double[][][] plots, int index) {
		int p = plots.length;
		double min = Double.POSITIVE_INFINITY;
		for (int i = 0; i < p; i++) {
			double[] val = plots[i][index];
			double temp = MatrixOps.min(val);
			if (temp < min) {
				min = temp;
			}
		}
		return min;
	}

	/**
	 * 
	 * @param plots
	 *            p sets of xy data as a double[p][index][] array where, for p
	 *            sets, plots[0:p-1][0] = x and plots[0:p-1][1] = y
	 * @param index
	 *            0 for x and 1 for y
	 * @return maximum of all x or y
	 */
	public static double max(double[][][] plots, int index) {
		int p = plots.length;
		double max = Double.NEGATIVE_INFINITY;
		for (int i = 0; i < p; i++) {
			double[] val = plots[i][index];
			double temp = MatrixOps.max(val);
			if (temp > max) {
				max = temp;
			}
		}
		return max;
	}
	
	// linear solutions

	/**
	 * 
	 * @param A
	 *            A[m,n] where A is upper triangular
	 * @param b
	 *            b[m]
	 * @return x[n] where Ax = b
	 */
	public static double[] backCalculateX(double[][] upperTriangularMatrix,
			double[] b) {
		int n = upperTriangularMatrix[0].length;
		b[n - 1] = b[n - 1] / upperTriangularMatrix[n - 1][n - 1];
		for (int i = n - 2; i >= 0; i += -1) {
			double temp = 0;
			for (int j = n - 1; j >= i + 1; j += -1) {
				temp = temp - upperTriangularMatrix[i][j] * b[j];
			}
			temp = temp + b[i];
			b[i] = temp / upperTriangularMatrix[i][i];
		}
		return b;
	}

	public static ComplexNumber[] backCalculateX(
			double[][] upperTriangularMatrix, ComplexNumber[] b) {
		int n = upperTriangularMatrix[0].length;
		b[n - 1] = ComplexCalc.divide(b[n - 1],
				upperTriangularMatrix[n - 1][n - 1]);
		for (int i = n - 2; i >= 0; i += -1) {
			ComplexNumber temp = new ComplexNumber();
			for (int j = n - 1; j >= i + 1; j += -1) {
				temp = ComplexCalc
						.subtract(temp, ComplexCalc.multiply(
								upperTriangularMatrix[i][j], b[j]));
			}
			temp = ComplexCalc.add(temp, b[i]);
			b[i] = ComplexCalc.divide(temp, upperTriangularMatrix[i][i]);
		}
		return b;
	}

	public static ComplexNumber[] backCalculateX(
			ComplexNumber[][] upperTriangularMatrix, double[] b) {
		int n = upperTriangularMatrix[0].length;
		ComplexNumber[] cB = toComplex(b);
		cB[n - 1] = ComplexCalc.divide(cB[n - 1],
				upperTriangularMatrix[n - 1][n - 1]);
		for (int i = n - 2; i >= 0; i += -1) {
			ComplexNumber temp = new ComplexNumber();
			for (int j = n - 1; j >= i + 1; j += -1) {
				temp = ComplexCalc.subtract(temp, ComplexCalc.multiply(
						upperTriangularMatrix[i][j], cB[j]));
			}
			temp = ComplexCalc.add(temp, cB[i]);
			cB[i] = ComplexCalc.divide(temp, upperTriangularMatrix[i][i]);
		}
		return cB;
	}

	public static ComplexNumber[] backCalculateX(
			ComplexNumber[][] upperTriangularMatrix, ComplexNumber[] b) {
		int n = upperTriangularMatrix[0].length;
		b[n - 1] = ComplexCalc.divide(b[n - 1],
				upperTriangularMatrix[n - 1][n - 1]);
		for (int i = n - 2; i >= 0; i += -1) {
			ComplexNumber temp = new ComplexNumber();
			for (int j = n - 1; j >= i + 1; j += -1) {
				temp = ComplexCalc
						.subtract(temp, ComplexCalc.multiply(
								upperTriangularMatrix[i][j], b[j]));
			}
			temp = ComplexCalc.add(temp, b[i]);
			b[i] = ComplexCalc.divide(temp, upperTriangularMatrix[i][i]);
		}
		return b;
	}

	public static double[][] createVandermonde(double[] x, int order) {
		int m = x.length;
		int n = order;
		double[][] V = new double[m][n + 1];
		for (int i = 0; i < m; i++) {
			for (int j = 0; j <= n; j++) {
				V[i][j] = Math.pow(x[i], (double) j);
			}
		}
		return V;
	}

	public static ComplexNumber[][] createVandermonde(ComplexNumber[] x,
			int order) {
		int m = x.length;
		int n = order;
		ComplexNumber[][] V = new ComplexNumber[m][n + 1];
		for (int i = 0; i < m; i++) {
			for (int j = 0; j <= n; j++) {
				V[i][j] = ComplexCalc.pow(x[i], (double) j);
			}
		}
		return V;
	}

}
