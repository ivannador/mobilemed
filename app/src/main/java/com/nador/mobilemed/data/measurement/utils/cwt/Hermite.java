package com.nador.mobilemed.data.measurement.utils.cwt;

/**
 * //from http://www.alglib.net/specialfunctions/polynomials/hermite.php
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
 * Class responsibility: Provide methods for computing Hermite polynomials.
 * 
 */
public class Hermite {
	  
	        

	        /*************************************************************************
	        Calculation of the value of the Hermite polynomial.

	        Parameters:
	            n   -   degree, n>=0
	            x   -   argument

	        Result:
	            the value of the Hermite polynomial Hn at x
	        *************************************************************************/
	        public static double physisistHermitePoly(int n,
	            double x)
	        {
	            double result = 0;
	            int i = 0;
	            double a = 0;
	            double b = 0;


	            //
	            // Prepare A and B
	            //
	            a = 1;
	            b = 2 * x;

	            //
	            // Special cases: N=0 or N=1
	            //
	            if (n == 0)
	            {
	                result = a;
	                return result;
	            }
	            if (n == 1)
	            {
	                result = b;
	                return result;
	            }

	            //
	            // General case: N>=2
	            //
	            for (i = 2; i <= n; i++)
	            {
	                result = 2 * x * b - 2 * (i - 1) * a;
	                a = b;
	                b = result;
	            }
	            return result;
	        }


	        /*************************************************************************
	        Summation of Hermite polynomials using Clenshaw’s recurrence formula.

	        This routine calculates
	            c[0]*H0(x) + c[1]*H1(x) + ... + c[N]*HN(x)

	        Parameters:
	            n   -   degree, n>=0
	            x   -   argument

	        Result:
	            the value of the Hermite polynomial at x
	        *************************************************************************/
	        public static double hermitesum(double[] c,
	            int n,
	            double x)
	        {
	            double result = 0;
	            double b1 = 0;
	            double b2 = 0;
	            int i = 0;

	            b1 = 0;
	            b2 = 0;
	            for (i = n; i >= 0; i--)
	            {
	                result = 2 * (x * b1 - (i + 1) * b2) + c[i];
	                b2 = b1;
	                b1 = result;
	            }
	            return result;
	        }


	        /*************************************************************************
	        Representation of Hn as C[0] + C[1]*X + ... + C[N]*X^N

	        Input parameters:
	            N   -   polynomial degree, n>=0

	        Output parameters:
	            C   -   coefficients
	        *************************************************************************/
	        public static void hermitecoefficients(int n,
	             double[] c)
	        {
	            int i = 0;

	            c = new double[n + 1];
	            for (i = 0; i <= n; i++)
	            {
	                c[i] = 0;
	            }
	            c[n] = Math.exp(n * Math.log(2));
	            for (i = 0; i <= n / 2 - 1; i++)
	            {
	                c[n - 2 * (i + 1)] = -(c[n - 2 * i] * (n - 2 * i) * (n - 2 * i - 1) / 4 / (i + 1));
	            }
	        }

	        // added by M Bishop
	        public static double probabilistHermitePoly(int n, double z)
	        {
	            double m = (double)n;
	            double pHpoly = physisistHermitePoly(n, z / Math.pow(2, .5));
	            double result = pHpoly / (Math.pow(2, m / 2));
	            return result;
	        }	        
	    }


