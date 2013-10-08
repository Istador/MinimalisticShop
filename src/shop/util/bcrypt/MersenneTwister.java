package shop.util.bcrypt;

import java.security.SecureRandom;

/**
 * This implementation is based on:
 *  - the PHP 5.3.18 implementation in rand.c and php_rand.h
 *  - the Java implementation from Sean Luke (http://www.cs.gmu.edu/~sean/research/) 
 * 
 * @author Robin Christopher Ladiges
 */
public class MersenneTwister {
	private static final int N = 624;
	private static final int M = 397;
	
	//not using 0x... to prevent negative integers which get casted to negative longs
    private static final long x9908b0df = 2567483615l; //0x9908b0df;
    private static final long x80000000 = 2147483648l; //0x80000000;
    private static final long x7fffffff = 2147483647l; //0x7fffffff;
    private static final long x00000001 = 1;
    private static final long xffffffff = 4294967295l;
    private static final long x9d2c5680 = 2636928640l; //0x9d2c5680;
    private static final long xefc60000 = 4022730752l; //0xefc60000;
    
    private static final long MT_RAND_MAX = x7fffffff;
    
    private static long[] s = new long[N];
	private static int si = 0;
    private static final long mag01[] = {0, x9908b0df};
    
    private static boolean is_seeded = false;
    
	
	
	private static long hiBit(long u){ return u & x80000000; }
	private static long loBit(long u){ return u & x00000001; }
	private static long loBits(long u){ return u & x7fffffff; }
	private static long mixBits(long u, long v){ return hiBit(u) | loBits(v); }
	private static long twist(long m,long u, long v){
		return m ^ (mixBits(u,v)>>>1) ^ mag01[(int)loBit(u)];
	}
	
	
	
	private static long generateSeed(){
		long seed = System.currentTimeMillis();
		//seed *= Long.valueOf(System.getProperty("pid"));
		seed ^= (long) (1000000.0 * new SecureRandom().nextDouble());
		return seed;
	}
	
	
	
	private static synchronized void mt_initialize(long seed) {		
		s[0] = seed & xffffffff;
		for(int i = 1; i < N; ++i ) {
			s[i] = ( 1812433253l * ( s[i-1] ^ (s[i-1] >>> 30) ) + i ) & xffffffff;
		}
		si = 0;
		is_seeded = true;
	}
	
	
	
	private static synchronized void mt_reload() {
		int p = 0;
		int i;
		for (i = N - M; i-- != 0; ++p)
			s[p] = twist(s[p+M], s[p], s[p+1]);
		for (i = M; --i != 0; ++p)
			s[p] = twist(s[p+M-N], s[p], s[p+1]);
		s[p] = twist(s[p+M-N], s[p], s[0]);
	}
	
	
	
	public static synchronized void mt_srand() {
		mt_initialize(generateSeed());
	}
	
	
	
	public static synchronized void mt_srand(long seed) {
		mt_initialize(seed);
	}
	
	
	
	public static synchronized long mt_rand() {
		if(!is_seeded) mt_srand();
		
		if (si == 0) {
			mt_reload();
		}
		
		long y = s[si];
		y ^= (y >>> 11);
		y ^= (y <<  7) & x9d2c5680;
		y ^= (y << 15) & xefc60000;
		y ^= (y >>> 18);
		si = (si + 1) % N;
		
		return y >>> 1;
	}
	
	
	
	public static long mt_rand(long min, long max) {
  	  if(min > max) throw new IllegalArgumentException("min must be below equal max");
  	  
  	  return min + (long) ((double) ( (double) max - min + 1.0) * (mt_rand() / ((MT_RAND_MAX) + 1.0)));
	}
	
	
	
	public static long mt_getrandmax(){
		return MT_RAND_MAX;
	}
	
	
	
	
}



/*
Copyright (C) 1997 - 2002, Makoto Matsumoto and Takuji Nishimura,
Copyright (C) 2000 - 2003, Richard J. Wagner
All rights reserved.                          

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions
are met:

1. Redistributions of source code must retain the above copyright
   notice, this list of conditions and the following disclaimer.

2. Redistributions in binary form must reproduce the above copyright
   notice, this list of conditions and the following disclaimer in the
   documentation and/or other materials provided with the distribution.

3. The names of its contributors may not be used to endorse or promote 
   products derived from this software without specific prior written 
   permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
"AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
A PARTICULAR PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/


/*
 * Copyright (c) 2003 by Sean Luke. <br>
 * Portions copyright (c) 1993 by Michael Lecuyer. <br>
 * All rights reserved. <br>
 *
 * <p>Redistribution and use in source and binary forms, with or without 
 * modification, are permitted provided that the following conditions are met:
 * <ul>
 * <li> Redistributions of source code must retain the above copyright notice, 
 * this list of conditions and the following disclaimer.
 * <li> Redistributions in binary form must reproduce the above copyright notice, 
 * this list of conditions and the following disclaimer in the documentation 
 * and/or other materials provided with the distribution.
 * <li> Neither the name of the copyright owners, their employers, nor the 
 * names of its contributors may be used to endorse or promote products 
 * derived from this software without specific prior written permission.
 * </ul>
 * <p>THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE 
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE 
 * DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNERS OR CONTRIBUTORS BE 
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR 
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF 
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS 
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN 
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE 
 * POSSIBILITY OF SUCH DAMAGE.
*/
