package com.nador.mobilemed.data.measurement.utils.cwt;

/**
 * From: //http://rosettacode.org/wiki/Gamma_function#Java, which sites:
 * From: Preda,Godfrey,Godfrey, http://mrob.com/pub/ries/lanczos-gamma.html
 * 
 * Modifying author Mark Bishop; 2015
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
 */

/**
 * Class responsibility: Provide methods for computing factorials and gamma
 * functions. Implementation of Stirling's approximation and Lanczos
 * approximation.
 */

public class Gamma {

	public static double gamma(double x) {
		double[] p = { 0.99999999999999709182, 57.156235665862923517,
				-59.597960355475491248, 14.136097974741747174,
				-0.49191381609762019978, .33994649984811888699e-4,
				.46523628927048575665e-4, -.98374475304879564677e-4,
				.15808870322491248884e-3, -.21026444172410488319e-3,
				.21743961811521264320e-3, -.16431810653676389022e-3,
				.84418223983852743293e-4, -.26190838401581408670e-4,
				.36899182659531622704e-5 };
		double g = 4.7421875;
		if (x < 0.5)
			return Math.PI / (Math.sin(Math.PI * x) * gamma(1 - x));

		x -= 1;
		double a = p[0];
		double t = x + g + 0.5;
		for (int i = 1; i < p.length; i++) {
			a += p[i] / (x + i);
		}

		return Math.sqrt(2 * Math.PI) * Math.pow(t, x + 0.5) * Math.exp(-t) * a;
	}

	public static double factorial(int val) {
		if (val == 0) {
			return 1;
		}
		double result = (double) (val);
		int i;
		for (i = 1; i <= val - 1; i++) {
			result = result * (val - (double) (i));
		}
		return result;
	}

}
