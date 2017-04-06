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
 * Class responsibility: Provide mathematical functions for scalar complex
 * number inputs.
 * 
 * @author max
 *
 */
public class ComplexCalc {

	private static ComplexNumber negitive_i;

	public static ComplexNumber e() {
		ComplexNumber e = new ComplexNumber(Math.E, 0);
		return e;
	}

	public static ComplexNumber pi() {
		ComplexNumber pi = new ComplexNumber(Math.PI, 0);
		return pi;
	}

	public static ComplexNumber i() {
		ComplexNumber sqrtMinus1 = new ComplexNumber(0, 1.0);
		return sqrtMinus1;
	}

	public static ComplexNumber minusOne() {
		ComplexNumber cM1 = new ComplexNumber(-1.0, 0);
		return cM1;
	}

	public static ComplexNumber zero() {
		ComplexNumber zero = new ComplexNumber();
		return zero;
	}

	public static ComplexNumber MinusI() {
		ComplexNumber minusI = new ComplexNumber(0, -1);
		return minusI;
	}

	public static ComplexNumber pointFive() {
		ComplexNumber point5 = new ComplexNumber(0.5, 0);
		return point5;
	}

	public static ComplexNumber one() {
		ComplexNumber one = new ComplexNumber(1.0, 0);
		return one;
	}

	public static ComplexNumber two() {
		ComplexNumber two = new ComplexNumber(2.0, 0);
		return two;
	}

	public static Boolean isEqual(ComplexNumber x, ComplexNumber y) {
		if (x.Real == y.Real && x.Imaginary == y.Imaginary) {
			return true;
		} else {
			return false;
		}
	}

	public static Boolean isZero(ComplexNumber z) {
		if (z.Real == 0 && z.Imaginary == 0) {
			return true;
		} else {
			return false;
		}
	}

	public static ComplexNumber toComplex(double real) {
		ComplexNumber result = new ComplexNumber(real, 0);
		return result;
	}

	public static ComplexNumber[] toComplex(double[] real) {
		int len = real.length;
		ComplexNumber[] result = new ComplexNumber[len];
		for (int i = 0; i < len; i++) {
			result[i].Real = real[i];
		}
		return result;
	}

	public static ComplexNumber add(ComplexNumber x, ComplexNumber y) {
		return new ComplexNumber(x.Real + y.Real, x.Imaginary + y.Imaginary);
	}

	public static ComplexNumber add(double x, ComplexNumber y) {
		return add(toComplex(x), y);
	}

	public static ComplexNumber add(ComplexNumber x, double y) {
		return add(x, toComplex(y));
	}

	public static ComplexNumber add(double x, double y) {
		return toComplex(x + y);
	}

	public static ComplexNumber subtract(ComplexNumber x, ComplexNumber y) {
		return new ComplexNumber(x.Real - y.Real, x.Imaginary - y.Imaginary);
	}

	public static ComplexNumber subtract(double x, ComplexNumber y) {
		return subtract(toComplex(x), y);
	}

	public static ComplexNumber subtract(ComplexNumber x, double y) {
		return subtract(x, toComplex(y));
	}

	public static ComplexNumber subtract(double x, double y) {
		return toComplex(x - y);
	}

	public static ComplexNumber multiply(ComplexNumber x, ComplexNumber y) {
		ComplexNumber result = new ComplexNumber();
		result.Real = (x.Real * y.Real) - (x.Imaginary * y.Imaginary);
		result.Imaginary = (x.Real * y.Imaginary) + (x.Imaginary * y.Real);
		return result;
	}

	public static ComplexNumber multiply(double x, ComplexNumber y) {
		return multiply(toComplex(x), y);
	}

	public static ComplexNumber multiply(ComplexNumber x, double y) {
		return multiply(x, toComplex(y));
	}

	public static ComplexNumber multiply(double x, double y) {
		return toComplex(x * y);
	}

	public static ComplexNumber divide(ComplexNumber num, ComplexNumber denom) {
		ComplexNumber result = new ComplexNumber();
		double divisor = (Math.pow(denom.Real, 2))
				+ (Math.pow(denom.Imaginary, 2));
		result.Real = ((num.Real * denom.Real) + (num.Imaginary * denom.Imaginary))
				/ divisor;
		result.Imaginary = ((num.Imaginary * denom.Real) - (num.Real * denom.Imaginary))
				/ divisor;
		return result;
	}

	public static ComplexNumber divide(double num, ComplexNumber denom) {
		return divide(toComplex(num), denom);
	}

	public static ComplexNumber divide(ComplexNumber num, double denom) {
		return divide(num, toComplex(denom));
	}

	public static ComplexNumber divide(double num, double denom) {
		return toComplex(num / denom);
	}

	public static double modulus(ComplexNumber z) {
		return Math.pow((Math.pow(z.Real, 2) + Math.pow(z.Imaginary, 2)), 0.5);
	}

	public static double modulus(double z) {
		return Math.abs(z);
	}

	public static double arg(ComplexNumber z) {
		if (z.Real == 0) {
			if (z.Imaginary < 0) {
				return (-.5 * Math.PI);
			} else if (z.Imaginary > 0) {
				return (.5 * Math.PI);
			} else {
				return 0;
			}
		} else if (z.Imaginary == 0) {
			if (z.Real > 0) {
				return 0;
			} else {
				return Math.PI;
			}
		} else {
			return Math.atan2(z.Imaginary, z.Real);

		}
	}

	public static double arg(double z) {
		if (z >= 0) {
			return 0;
		} else {
			return Math.PI;
		}
	}

	public static ComplexNumber conjugate(ComplexNumber z) {
		ComplexNumber result = new ComplexNumber(z.Real, -z.Imaginary);
		return result;
	}

	public static ComplexNumber conjugate(double z) {
		return toComplex(z);
	}

	public static double absoluteSquare(ComplexNumber z) {
		ComplexNumber result = new ComplexNumber();
		result = multiply(z, conjugate(z));
		return result.Real;
	}

	public static ComplexNumber ln(ComplexNumber z) {
		ComplexNumber result = new ComplexNumber();
		result.Real = Math.log(modulus(z));
		result.Imaginary = arg(z);
		return result;
	}

	public static ComplexNumber ln(double x) {
		return ln(toComplex(x));
	}

	public static ComplexNumber logBaseB(ComplexNumber z, ComplexNumber Base) {
		ComplexNumber result = new ComplexNumber();
		result = divide(ln(z), ln(Base));
		return result;
	}

	public static ComplexNumber sin(ComplexNumber z) {
		ComplexNumber denom = new ComplexNumber(0, 2);
		ComplexNumber num1 = new ComplexNumber(-z.Imaginary, z.Real);
		ComplexNumber num2 = new ComplexNumber(z.Imaginary, -z.Real);
		return divide(subtract(exp(num1), exp(num2)), denom);
	}

	public static ComplexNumber sin(double z) {
		return toComplex(Math.sin(z));
	}

	public static ComplexNumber arcSin(ComplexNumber z) {
		ComplexNumber result = new ComplexNumber();
		ComplexNumber f = new ComplexNumber();
		f = ln(add(multiply(i(), z),
				pow(subtract(1, pow(z, two())), pointFive())));
		result = multiply(negitive_i, f);
		return result;
	}

	public static ComplexNumber cos(ComplexNumber z) {
		ComplexNumber num1 = new ComplexNumber(-z.Imaginary, z.Real);
		ComplexNumber num2 = new ComplexNumber(z.Imaginary, -z.Real);
		return divide(add(exp(num1), exp(num2)), 2);
	}

	public static ComplexNumber cos(double z) {
		return toComplex(Math.cos(z));
	}

	public static ComplexNumber arcCos(ComplexNumber z) {
		return subtract(divide(pi(), two()), arcSin(z));
	}

	public static ComplexNumber tan(ComplexNumber z) {
		return divide(sin(z), cos(z));
	}

	public static ComplexNumber arcTan(ComplexNumber z) {
		ComplexNumber result = new ComplexNumber();
		result = subtract(ln(subtract(1, multiply(i(), z))),
				ln(add(1, multiply(i(), z))));
		result = multiply(divide(i(), two()), result);
		return result;
	}

	public static ComplexNumber sinh(ComplexNumber z) {
		ComplexNumber neg = new ComplexNumber();
		neg.Real = -z.Real;
		neg.Imaginary = -z.Imaginary;
		return divide(subtract(exp(z), exp(neg)), two());
	}

	public static ComplexNumber arcSinh(ComplexNumber z) {
		return ln(add(z, pow(add(pow(z, two()), 1), pointFive())));
	}

	public static ComplexNumber cosh(ComplexNumber z) {
		ComplexNumber neg = new ComplexNumber(-z.Real, -z.Imaginary);
		return divide(add(exp(z), exp(neg)), two());
	}

	public static ComplexNumber arcCosh(ComplexNumber z) {
		return ln(add(
				z,
				multiply(pow(add(z, minusOne()), pointFive()),
						pow(add(z, 1), pointFive()))));
	}

	public static ComplexNumber tanh(ComplexNumber z) {
		return divide(sinh(z), cosh(z));
	}

	public static ComplexNumber arcTanh(ComplexNumber z) {
		return divide(subtract(ln(add(1, z)), ln(subtract(1, z))), two());
	}

	public static ComplexNumber reciprocal(ComplexNumber z) {
		return divide(1, z);
	}

	public static ComplexNumber reciprocal(double z) {
		return toComplex(1 / z);
	}

	public static ComplexNumber exp(double z) {
		return pow(e(), z);
	}

	public static ComplexNumber exp(ComplexNumber z) {
		return pow(e(), z);
	}

	public static ComplexNumber pow(double z, double exponent) {
		return new ComplexNumber(Math.pow(z, exponent), 0);
	}

	public static ComplexNumber pow(ComplexNumber z, double exponent) {
		ComplexNumber result = new ComplexNumber();
		if (z.Real > 0 && z.Imaginary == 0) {
			result.Imaginary = 0;
			result.Real = Math.pow(z.Real, exponent);
		}
		double iDbl = 0;
		int i;
		int j;

		if (exponent >= 1 || exponent <= -1) {
			iDbl = Math.abs((int) exponent);
			i = (int) iDbl; // truncate
			result = z;
			for (j = 1; j <= i - 1; j++) {
				result = multiply(z, result);
			}
			double newPow = Math.abs(exponent) - iDbl;
			if (newPow != 0) {
				ComplexNumber temp = new ComplexNumber();
				double r = modulus(z);
				double coeff = Math.pow(r, newPow);
				double arg = arg(z);
				temp.Real = coeff * (Math.cos(arg * newPow));
				temp.Imaginary = coeff * (Math.sin(arg * newPow));
				result = multiply(temp, result);
			}
			if (exponent < 0) {
				return divide(1, result);
			} else {
				return result;
			}
		} else {
			double r = modulus(z);
			double coeff = Math.pow(r, exponent);
			double arg = arg(z);
			if (z.Real < 0 && z.Imaginary == 0
					&& (exponent == 0.5 || exponent == -0.5)) {
				result.Real = 0; // cos(pi/2) should be 0 but isn't in floating
									// point precision
			} else {
				result.Real = coeff * (Math.cos(arg * exponent));
			}
			result.Imaginary = coeff * (Math.sin(arg * exponent));
			return result;
		}
	}

	public static ComplexNumber pow(ComplexNumber z, ComplexNumber exponent) {
		if (exponent.Imaginary == 0) {
			return pow(z, exponent.Real);
		}
		double coeff = (Math.pow(
				Math.pow(z.Real, 2) + Math.pow(z.Imaginary, 2),
				exponent.Real / 2))
				* Math.exp(-exponent.Imaginary * arg(z));
		ComplexNumber result = new ComplexNumber();
		result.Real = coeff
				* Math.cos((exponent.Real * arg(z))
						+ (exponent.Imaginary
								* Math.log(Math.pow(z.Real, 2)
										+ Math.pow(z.Imaginary, 2)) / 2));
		result.Imaginary = coeff
				* Math.sin((exponent.Real * arg(z))
						+ (exponent.Imaginary
								* Math.log(Math.pow(z.Real, 2)
										+ Math.pow(z.Imaginary, 2)) / 2));
		return result;
	}

	public static ComplexNumber pow(double z, ComplexNumber exponent) {
		return pow(toComplex(z), exponent);
	}

	public static ComplexNumber sign(ComplexNumber z) {
		if (isZero(z)) {
			return zero();
		} else {
			return divide(z, modulus(z));
		}
	}

}
