package com.nador.mobilemed.data.measurement.utils.cwt;

/**
 * Based on Robert Sedgwick and Kevin Wayne recursive procedure
 * 
 * Author Mark Bishop; 2003 * 
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
 * Class responsibility: Provide functions for forward and inverse Fourier
 * transforms
 */

public class Fft {

	/**
	 * 
	 * @param ftD
	 *            time domain function
	 * @return frequency domain function
	 */
	public static ComplexNumber[] FftRadixCpu(ComplexNumber[] ft) {
		int n = ft.length;
		ComplexNumber[] hf = new ComplexNumber[n];
		MatrixOps.initialize(hf);
		if (n == 1) {
			hf[0] = ft[0];
			return hf;
		}
		ComplexNumber[] even = new ComplexNumber[n / 2];
		ComplexNumber[] odd = new ComplexNumber[n / 2];
		for (int k = 0; k < n / 2; k++) {
			even[k] = ft[2 * k];
			odd[k] = ft[2 * k + 1];
		}
		ComplexNumber[] q = FftRadixCpu(even);
		ComplexNumber[] r = FftRadixCpu(odd);
		for (int k = 0; k < n / 2; k++) {
			double kth = -2 * k * Math.PI / n;
			ComplexNumber wk = new ComplexNumber();
			wk.Real = Math.cos(kth);
			wk.Imaginary = Math.sin(kth);

			hf[k] = ComplexCalc.add(q[k], (ComplexCalc.multiply(wk, r[k])));
			hf[k + n / 2] = ComplexCalc.subtract(q[k],
					(ComplexCalc.multiply(wk, r[k])));
		}
		return hf;
	}

	/**
	 * 
	 * @param ftD
	 *            time domain function
	 * @return frequency domain function
	 */
	public static ComplexNumber[] FftRadixCpu(double[] ftD) {
		ComplexNumber[] ft = MatrixOps.toComplex(ftD);
		return FftRadixCpu(ft);
	}

	/**
	 * 
	 * @param hf
	 *            frequency domain function
	 * @return time domain function
	 */
	public static ComplexNumber[] IfftRadixCpu(ComplexNumber[] hf) {
		int n = hf.length;

		// take conjugate
		for (int i = 0; i < n; i++)
			hf[i] = ComplexCalc.conjugate(hf[i]);

		// compute forward FFT
		ComplexNumber[] ft = FftRadixCpu(hf);

		// take conjugate again
		for (int i = 0; i < n; i++)
			ft[i] = ComplexCalc.conjugate(ft[i]);

		// divide by N
		double rcrpln = 1.0 / (double) (n);
		for (int i = 0; i < n; i++) {
			ft[i] = ComplexCalc.multiply(ft[i], rcrpln);
		}
		return ft;
	}
}
