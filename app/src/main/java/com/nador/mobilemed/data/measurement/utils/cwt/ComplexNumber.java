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
 * Class responsibility: Provide and object that represents a complex number. An
 * instance of the class has a real component (a double) and an imaginary
 * component (also a double).
 */
public class ComplexNumber {

	public ComplexNumber() {
		this.Real = 0;
		this.Imaginary = 0;
	}

	public ComplexNumber(double real, double imaginary) {
		this.Real = real;
		this.Imaginary = imaginary;
	}

	public double Real;
	public double Imaginary;

	public String toCString() {
		String i0 = "", i1 = "", i2 = "";
		if (Imaginary != 0) {
			i1 = "i";
			double magI = Math.abs(Imaginary);
			i2 = Double.toString(magI);
			if (Imaginary < 0) {
				i0 = " -";
			} else {
				i0 = " +";
			}
		}

		String str = Double.toString(Real) + i0 + i1 + i2;
		return str;
	}

	/**
	 * Attempts to parse a string representation of a complex number.
	 * 
	 * A string representation of a double will parse to a complex
	 * representation. Otherwise, the input must delimit real from imaginary
	 * parts with either [space]+ or [space]-(use ### +i### and don't use ###
	 * i### or ###+i### ) The real part may be absent but prefer 0 [+/-]i###. If
	 * the string value ends with i then this parser can interpret the string
	 * provided the real and imaginary parts are delimited properly (e.g. ###
	 * +###i will work).
	 * 
	 * @param strVal
	 */
	public void setFrom(String strVal) {
		if (isNumeric(strVal)) {
			Real = Double.parseDouble(strVal);
			Imaginary = 0;
		} else {
			strVal = strVal.toLowerCase();
			strVal = strVal.trim();
			strVal = strVal.replace("j", "i");
			strVal = strVal.replace("i ", "i");
			strVal = strVal.replace(" i", "i");
			if (strVal.charAt(0) == "i".charAt(0)) {
				strVal = "+i";
			}
			if (strVal.charAt(1) == "i".charAt(0)) {
				strVal = "0 " + strVal;
			}
			if (strVal.endsWith("i")) {
				strVal = strVal.replace(" +", " +i");
				strVal = strVal.replace(" -", " -i");
				strVal.substring(0, strVal.length() - 2);
			}
			String[] value = strVal.split(" ");
			Real = Double.parseDouble(value[0]);
			double sign = 1.0;
			String[] img = value[1].split("i");
			if (img[0].contains("-")) {
				sign = -1.0;
			}
			if (img.length == 1) {
				Imaginary = sign;
			} else {
				Imaginary = sign * Double.parseDouble(img[1]);
			}
		}
	}

	/**
	 * 
	 * @param str
	 *            a string
	 * @return True if pattern is regex match "-?\\d+(\\.\\d+)?(E-?\\d+)?" i.e.
	 *         a number with optional '-', decimal point or E+/-.
	 */
	public static boolean isNumeric(String str) {
		return str.matches("-?\\d+(\\.\\d+)?(E-?\\d+)?");
	}

}