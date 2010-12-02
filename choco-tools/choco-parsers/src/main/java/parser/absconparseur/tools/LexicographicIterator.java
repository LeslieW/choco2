/**
 *  Copyright (c) 1999-2010, Ecole des Mines de Nantes
 *  All rights reserved.
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *
 *      * Redistributions of source code must retain the above copyright
 *        notice, this list of conditions and the following disclaimer.
 *      * Redistributions in binary form must reproduce the above copyright
 *        notice, this list of conditions and the following disclaimer in the
 *        documentation and/or other materials provided with the distribution.
 *      * Neither the name of the Ecole des Mines de Nantes nor the
 *        names of its contributors may be used to endorse or promote products
 *        derived from this software without specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE REGENTS AND CONTRIBUTORS ``AS IS'' AND ANY
 *  EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED. IN NO EVENT SHALL THE REGENTS AND CONTRIBUTORS BE LIABLE FOR ANY
 *  DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 *  ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package parser.absconparseur.tools;

/**
 * This class allows iterating all tuples in a lexicograpic order from a given set of domains.
 */
public class LexicographicIterator {
	private final int[] tuple;

	private final int[] position;

	private final int[][] domains;

	public LexicographicIterator(int[][] domains) {
		this.domains = domains;
		tuple = new int[domains.length];
		position = new int[domains.length];
	}

	public int[] getFirstTuple() {
		for (int i = 0; i < tuple.length; i++) {
			tuple[i] = domains[i][0];
			position[i] = 0;
		}
		return tuple;
	}

	public int[] getNextTupleAfter(int[] tuple) {
		for (int i = tuple.length - 1; i >= 0; i--) {
			if (position[i] < domains[i].length - 1) {
				position[i]++;
				tuple[i] = domains[i][position[i]];
				return tuple;
			}
			tuple[i] = domains[i][0];
			position[i] = 0;
		}
		return null;
	}
}
