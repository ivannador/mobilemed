package com.nador.mobilemed.data.measurement.utils.cwt;

/**
 * Author Mark Bishop; 2014
 * License GNU v3; 
 * This class is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
    
 * The author makes no warranty for the accuracy, completeness, safety, or
 * usefulness of any information provided and does not represent that its use
 * would not infringe privately owned right.
 */

/**
 * The development of the first three functions included in this class began with algorithmic 
 * analysis of Fortran functions provided in wavelet.f authored by:
 * Christopher Torrence and Gilbert P. Compo and downloaded circa 12/2008 from:
 * http://paos.colorado.edu/research/wavelets/
 *  
 * The wavelet.f code header included the following Copyright: *
 * Copyright (C) 1998, Christopher Torrence
 * This software may be used, copied, or redistributed as long as it is
 * not sold and this copyright notice is reproduced on each copy made. This
 * routine is provided as is without any express or implied warranties
 * whatsoever.
 * 
 * The header also included parameter descriptions, the wording of which are occasionally used here.
 * 
 * Note that the acronym "DOG" stands for the m-th derivative of the Gaussian distribution.
 **/

import java.util.ArrayList;

/**
 * Class responsibility: Provide methods for continuous wavelet transforms for
 * signals having length that is an even power of two.
 */
public class CWT {

	/**
	 * Continuous wavelet transform for a real, discretely sampled function y =
	 * f(t) of length n where n is an integer power of 2
	 * 
	 * @param y
	 *            The time-sampled signal to transform.
	 * @param dt
	 *            The sampling increment
	 * @param mother
	 *            The wavelet to use in the transform (Morlet, DOG, Paul)
	 * @param param
	 *            wavelet parameter (Morlet param = wave number, Paul param =
	 *            order, DOG param = m::m-th derivative of Gaussian)
	 * @param s0
	 *            The smallest desired scale in the transform (try s0=dt for
	 *            Morlet; s0=dt/4 for Paul)
	 * @param dj
	 *            The desired spacing between successive scales (try 0.25)
	 * @param jtot
	 *            The desired number of scales to be computed
	 * @return An ArrayList[Object] wspcmf[6] where: xspcmf[0] is a
	 *         Complex[n][jtot] multi-resolution matrix, wspcmf[1] is a
	 *         double[jtot] vector of scales, wspcmf[2] is a double[jtot] vector
	 *         of periods, wspcmf[3] is a double[n] cone-of-influence vector,
	 *         wspcmf[4] is the signal mean, and wspcmf[5] is the Fourier
	 *         factor.
	 */
	public static ArrayList<Object> cWT(double[] y, double dt, Wavelet mother,
			double param, double s0, double dj, int jtot) {
		int n = y.length;

		// Result objects
		ArrayList<Object> wspcmf = new ArrayList<Object>();
		ComplexNumber[][] wT = new ComplexNumber[jtot][n];
		MatrixOps.initialize(wT);
		double[] scale = new double[jtot];
		double[] period = new double[jtot];
		double[] coi = new double[n];
		double signalMean = 0;

		// Find the time-series mean and remove it.
		for (int i = 0; i < n; i++) {
			signalMean = signalMean + y[i];
		}
		signalMean = signalMean / (double) n;
		double[] yNoDC = new double[n];
		for (int i = 0; i < n; i++) {
			yNoDC[i] = y[i] - signalMean;
		}

		// Obtain the Fourier transform of the time series
		ComplexNumber[] yfft = Fft.FftRadixCpu(yNoDC);

		// Create a vector of wave numbers such that:
		// w = 2pik/Ndt for k <= N/2 and w = -2pik/Ndt for k > N/2
		double[] w = new double[n];
		w[0] = 0;
		double df = 2.0 * Math.PI / ((double) n * dt);
		for (int i = 2; i <= (n / 2) + 1; i++) {
			w[i - 1] = ((double) i - 1.0) * df;
		}
		for (int i = n / 2 + 2; i <= n; i++) {
			w[i - 1] = -w[n - i + 1];
		}

		// main wavelet loop
		for (int j = 0; j < jtot; j++) {
			scale[j] = s0 * Math.pow(2.0, (double) j * dj);
			ComplexNumber[] daughter = getDaughter(mother, param, scale[j], w,
					dt);

			// Convolution(f, g) = ifft( fft(f) X fft(g) )
			ComplexNumber[] product = new ComplexNumber[n];
			for (int k = 0; k <= n - 1; k++) {
				product[k] = ComplexCalc.multiply(daughter[k], yfft[k]);
			}
			wT[j] = Fft.IfftRadixCpu(product);

			// // We could plot one daughter.
			// if (j == (int) (jtot / 3)) {
			// double[] x = new double[n];
			// for (int i = 0; i < n; i++) {
			// x[i] = i;
			// }
			// Signal s = new Signal(x, MatrixOps.modulus(daughter));
			// s.plot(java.awt.Color.green, 4);
			// }

		}

		// compute period and coi vectors
		double fourierFactor = 0;
		double eFold = 0;
		switch (mother.toString()) {
		case "Morlet":
			fourierFactor = (4.0 * Math.PI)
					/ (param + Math.sqrt(2.0 + Math.pow(param, 2)));
			eFold = fourierFactor / Math.sqrt(2.0);
			break;
		case "Paul":
			fourierFactor = (4.0 * Math.PI) / (2 * param + 1);
			eFold = fourierFactor * Math.sqrt(2.0);
			break;
		case "DOG":
			fourierFactor = 2 * Math.PI * Math.sqrt(2 / (2 * param + 1));
			eFold = fourierFactor / Math.sqrt(2.0);
			break;
		}

		for (int i = 0; i < jtot; i++) {
			period[i] = scale[i] * fourierFactor;
		}

		for (int i = 1; i <= ((n + 1) / 2); i++) {
			coi[i - 1] = eFold * dt * ((i) - 1);
			coi[((n - 1) - i) + 1] = coi[i - 1];
		}

		wspcmf.add(MatrixOps.transpose(wT));
		wspcmf.add(scale);
		wspcmf.add(period);
		wspcmf.add(coi);
		wspcmf.add(signalMean);
		wspcmf.add(fourierFactor);
		return wspcmf;
	}

	/**
	 * Get the daughter wavelet for the specified arguments.
	 * 
	 * @param mother
	 *            The wavelet (Morlet, DOG, Paul)
	 * @param param
	 *            wavelet parameter (Morlet param = wavenumber, Paul param =
	 *            order, DOG param = m::m-th derivative)
	 * @param scaleJ
	 *            The desired wavelet scale to be used for constructing the
	 *            daughter.
	 * @param waveNumbers
	 *            The vector of wave numbers to be used to construct the
	 *            daughter.
	 * @param dt
	 *            The sampling increment (for normalization).
	 * 
	 * @return ComplexNumber[n] vector of daughter
         */
	private static ComplexNumber[] getDaughter(Wavelet mother, double param,
			double scaleJ, double[] waveNumbers, double dt) {
		int n = waveNumbers.length;

		ComplexNumber[] daughter = new ComplexNumber[n];

		if (mother == Wavelet.Morlet) {
			double norm = Math.sqrt(2.0 * Math.PI * scaleJ / dt)
					* Math.pow(Math.PI, -0.25);
			for (int k = 0; k <= n / 2; k++) {
				double expnt = -0.5
						* Math.pow((scaleJ * waveNumbers[k] - param), 2);
				daughter[k] = new ComplexNumber(norm * Math.exp(expnt), 0);
			}
			for (int k = n / 2 + 1; k < n; k++) {
				daughter[k] = ComplexCalc.zero();
			}
		}

		if (mother == Wavelet.Paul) {
			double f = Gamma.factorial(2 * (int) param - 1);
			double norm = Math.sqrt(2 * Math.PI * scaleJ / dt);
			norm *= Math.pow(2, param) / (Math.sqrt(param * f));

			for (int k = 0; k <= n / 2; k++) {
				double expnt = -scaleJ * waveNumbers[k];
				daughter[k] = new ComplexNumber(norm
						* Math.pow(scaleJ * waveNumbers[k], param)
						* Math.exp(expnt), 0);
			}
			for (int k = n / 2 + 1; k < n; k++) {
				daughter[k] = ComplexCalc.zero();
			}
		}

		// Odd DOGs are complex.
		if (mother == Wavelet.DOG) {
			double nf1 = Math.sqrt(2 * Math.PI * scaleJ / dt)
					* Math.sqrt(1 / Gamma.gamma(param + 0.5));
			ComplexNumber nf2 = ComplexCalc.pow(ComplexCalc.i(), param);
			ComplexNumber norm = ComplexCalc.multiply(-nf1, nf2);
			for (int k = 0; k < n; k++) {
				double sw = scaleJ * waveNumbers[k];
				ComplexNumber d1 = ComplexCalc.multiply(norm,
						Math.pow(sw, param));
				double d2 = Math.exp(-Math.pow(sw, 2) / 2);
				daughter[k] = ComplexCalc.multiply(d1, d2);
			}
			// // View daughters (requires signal class):
			// double[] x = new double[n];
			// for (int i = 0; i < n; i++) {
			// x[i]=i;
			// }
			// Signal s = new Signal(x, MatrixOps.modulus(daughter));
			// s.plot(java.awt.Color.green, 4);
			
			// // The loops below are a modified version.
			// // Here the second half of the vector is set to zero.
			// // Inspect the daughters to see why this may make sense.
			// for (int k = 0; k <= n / 2; k++) {
			// double sw = scaleJ * waveNumbers[k];
			//
			// ComplexNumber d1 = ComplexCalc.multiply(norm,
			// Math.pow(sw, param));
			// double d2 = Math.exp(-Math.pow(sw, 2) / 2);
			// daughter[k] = ComplexCalc.multiply(d1, d2);
			// }
			// for (int k = n / 2 + 1; k < n; k++) {
			// daughter[k] = ComplexCalc.zero();
			// }


		}
		return daughter;
	}

	/**
	 * 
	 * @param wavelet
	 * @return A partial selection of valid parameters
	 */
	public static double[] getSelectedParamChoices(Wavelet wavelet) {
		double[] validParams = null;
		if (wavelet == Wavelet.Morlet) {
			validParams = new double[] { 5, 5.336, 6, 7, 8, 10, 12, 14, 16, 20 };
		} else if (wavelet == Wavelet.Paul) {
			validParams = new double[] { 4, 5, 6, 7, 8, 10, 16, 20, 30, 40 };
		} else if (wavelet == Wavelet.DOG) {
			validParams = new double[] { 2, 4, 5, 6, 8, 12, 16, 20, 30, 60 };
		}
		return validParams;
	}

	/**
	 * 
	 * @param mother
	 *            The wavelet.
	 * @param param
	 *            The wavelet parameter
	 * @return An arraylist(Object) psi0C[2], where psi0C[0] is psi(0) and
	 *         psi0C[1] is the reconstruction factor as determined empirically
	 *         using a delta function. Psi(0) is a ComplexNumber for odd
	 *         parameters of Paul wavelets, otherwise it is a double precision,
	 *         real value.
	 */
	private static ArrayList<Object> getEmpiricalFactors(Wavelet mother,
			double param) {
		double dt = 1;
		int n = 2;
		double s0 = 0.01;
		double dj = 0.1;
		int jtot = 120;
		if (mother == Wavelet.DOG) {
			n = 256;
			jtot = 160;
		}
		double[] deltaFn = new double[n];
		{
			for (int i = 0; i < deltaFn.length; i++) {
				deltaFn[i] = 0;
			}
			deltaFn[0] = 1;
		}

		ArrayList<Object> wspcmf = CWT.cWT(deltaFn, dt, mother, param, s0, dj,
				jtot);

		ComplexNumber[][] wave = (ComplexNumber[][]) wspcmf.get(0);
		double[] scale = (double[]) wspcmf.get(1);
		double fourierFactor = (double) wspcmf.get(5);
		System.out.println(mother + " " + param + ": Fourier factor "
				+ fourierFactor);

		double[][] rWave = MatrixOps.getRealPart(wave);

		for (int j = 0; j < scale.length; j++) {
			for (int i = 0; i < deltaFn.length; i++) {
				rWave[i][j] = rWave[i][j] / Math.sqrt(scale[j]);
			}
		}
		double[] sums = new double[deltaFn.length];
		for (int i = 0; i < deltaFn.length; i++) {
			sums[i] = 0;
			for (int j = 0; j < scale.length; j++) {
				sums[i] += rWave[i][j];
			}
		}

		// double max = MatrixOps.vectorInfinityNorm(sums);
		double hi = MatrixOps.max(sums);
		double lo = MatrixOps.min(sums);
		double max = hi;
		if (Math.abs(lo) > hi) {
			max = lo;
		} // signed val of infinity norm

		double sqrtDt = Math.sqrt(dt);
		double psi0 = 0;
		ComplexNumber cPsi0 = ComplexCalc.zero();

		switch (mother) {
		case Morlet:
			psi0 = 1 / (Math.pow(Math.PI, 0.25));
			break;
		case Paul:
			int m = (int) param;
			ComplexNumber numerator = ComplexCalc.multiply(ComplexCalc
					.multiply(ComplexCalc.pow(ComplexCalc.i(), m),
							Math.pow(2, m)), Gamma.factorial(m));
			double denominator = Math.sqrt((Math.PI * Gamma.factorial(2 * m)));
			cPsi0 = ComplexCalc.divide(numerator, denominator);
			psi0 = cPsi0.Real;
			break;
		case DOG:
			m = (int) param;
			psi0 = m + 0.5;
			if (m % 2 != 0) {
				// Asymmetric!
				psi0 = Hermite.probabilistHermitePoly(m, 1)
						/ Math.sqrt(Gamma.gamma(psi0));
			} else {
				psi0 = Hermite.probabilistHermitePoly(m, 0)
						/ Math.sqrt(Gamma.gamma(psi0));
			}

			psi0 = psi0 * Math.pow(-1, m + 1);
			break;
		}
		ArrayList<Object> psi0C = new ArrayList<Object>();
		if (mother == Wavelet.Paul && param % 2 != 0) {
			double result = max * dj * sqrtDt;
			ComplexNumber C = ComplexCalc.divide(result, cPsi0);
			psi0C.add(cPsi0);
			psi0C.add(C);
			System.out.println(mother + " " + param + ": psi0 = (" + cPsi0.Real
					+ ", " + cPsi0.Imaginary + "i), C = (" + C.Real + ", "
					+ C.Imaginary + ")");
		} else {
			double C = max * dj * sqrtDt / psi0;
			psi0C.add(psi0);
			psi0C.add(C);
			System.out.println(mother + " " + param + ": psi0 = " + psi0
					+ ", C = " + C);
		}
		System.out.println();
		return psi0C;
	}

	/**
	 * 
	 * @param mother
	 * @param param
	 * @param rWT
	 *            real part of mra matrix
	 * @param scales
	 * @param dj
	 * @param dt
	 * @param signalMean
	 * @return a reconstructed signal, except when mother is an odd DOG.
	 */
	public static double[] cwtReconstruct(Wavelet mother, double param,
			double[][] rWT, double[] scales, double dj, double dt,
			double signalMean) {
		ArrayList<Object> psi0C = getEmpiricalFactors(mother, param);

		double factor = 0;

		if (psi0C.get(0) instanceof Double) {
			double psi0 = (double) psi0C.get(0);
			double c = (double) psi0C.get(1);
			factor = dj * Math.sqrt(dt) / (psi0 * c);
		} else {
			ComplexNumber cFactor = ComplexCalc.zero();
			ComplexNumber psi0 = (ComplexNumber) psi0C.get(0);
			ComplexNumber c = (ComplexNumber) psi0C.get(1);
			cFactor = ComplexCalc.divide(dj * Math.sqrt(dt),
					ComplexCalc.multiply(psi0, c));
			factor = cFactor.Real;
		}

		double[] y = new double[rWT.length];
		for (int i = 0; i < rWT.length; i++) {
			double summer = 0;
			for (int j = 0; j < scales.length; j++) {
				summer += (rWT[i][j] / Math.sqrt(scales[j]));
			}
			y[i] = summer * factor + signalMean;
		}
		return y;
	}
	
	public static enum Wavelet {
		Morlet, DOG, Paul
	}
}
