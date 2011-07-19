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

/*
         * Copyright 1999-2005 Sun Microsystems, Inc.  All Rights Reserved.
         * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
         *
         * This code is free software; you can redistribute it and/or modify it
         * under the terms of the GNU General Public License version 2 only, as
         * published by the Free Software Foundation.  Sun designates this
         * particular file as subject to the "Classpath" exception as provided
         * by Sun in the LICENSE file that accompanied this code.
         *
         * This code is distributed in the hope that it will be useful, but WITHOUT
         * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
         * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
         * version 2 for more details (a copy is included in the LICENSE file that
         * accompanied this code).
         *
         * You should have received a copy of the GNU General Public License version
         * 2 along with this work; if not, write to the Free Software Foundation,
         * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
         *
         * Please contact Sun Microsystems, Inc., 4150 Network Circle, Santa Clara,
         * CA 95054 USA or visit www.sun.com if you need additional information or
         * have any questions.
         */

package choco.kernel.common.util.objects;

        /** A generic class for pairs.
         *
         *  <p><b>This is NOT part of any API supported by Sun Microsystems.  If
         *  you write code that depends on this, you do so at your own risk.
         *  This code and its internal interfaces are subject to change or
         *  deletion without notice.</b>
         */
        public class Pair<A, B> {

            public final A fst;
            public final B snd;

            public Pair(A fst, B snd) {
                this .fst = fst;
                this .snd = snd;
            }

            public String toString() {
                return "Pair[" + fst + "," + snd + "]";
            }

            private static boolean equals(Object x, Object y) {
                return (x == null && y == null) || (x != null && x.equals(y));
            }

            public boolean equals(Object other) {
                return other instanceof  Pair && equals(fst, ((Pair) other).fst)
                        && equals(snd, ((Pair) other).snd);
            }

            public int hashCode() {
                if (fst == null)
                    return (snd == null) ? 0 : snd.hashCode() + 1;
                else if (snd == null)
                    return fst.hashCode() + 2;
                else
                    return fst.hashCode() * 17 + snd.hashCode();
            }

            public static <A, B> Pair<A, B> of(A a, B b) {
                return new Pair<A, B>(a, b);
            }
        }
