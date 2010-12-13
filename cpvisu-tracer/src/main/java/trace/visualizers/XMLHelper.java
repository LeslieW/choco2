/**
 * Copyright (c) 1999-2010, Ecole des Mines de Nantes
 * All rights reserved.
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the Ecole des Mines de Nantes nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE REGENTS AND CONTRIBUTORS ``AS IS'' AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE REGENTS AND CONTRIBUTORS BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package trace.visualizers;

import choco.kernel.common.util.iterators.DisposableIntIterator;
import choco.kernel.solver.variables.integer.IntDomain;
import choco.kernel.solver.variables.integer.IntDomainVar;
import choco.kernel.solver.variables.set.SetVar;
import org.slf4j.Logger;
import trace.CPVizConstant;


/**
 * <br/>
 *
 * @author Charles Prud'homme
 * @since 9 déc. 2010
 */
public class XMLHelper {

    static StringBuilder values = new StringBuilder();

    protected XMLHelper() {
    }

    protected static void dvar(IntDomainVar ivar, int idx, Logger logger, String prefix) {
        if (ivar.isInstantiated()) {
            logger.info(prefix + CPVizConstant.V_INTEGER_TAG, idx, ivar.getVal());
        } else {
            logger.info(prefix + CPVizConstant.V_DVAR_TAG, idx, domain(ivar.getDomain()));
        }
    }

    private static String domain(IntDomain dom) {
        values.setLength(0);
        if (dom.isEnumerated()) {
            DisposableIntIterator it = dom.getIterator();
            while (it.hasNext()) {
                values.append(it.next()).append(' ');
            }
            it.dispose();
        } else {
            values.append(dom.getInf()).append(" .. ").append(dom.getSup());
        }
        return values.toString();
    }

    protected static void svar(SetVar svar, int idx, Logger logger, String prefix) {
        if (svar.isInstantiated()) {
            logger.info(prefix + CPVizConstant.V_SINTEGER_TAG, idx, domain(svar.getDomain().getKernelIterator()));
        } else {
            logger.info(prefix + CPVizConstant.V_SVAR_TAG, new Object[]{idx, domain(svar.getDomain().getKernelIterator()),
                    domain(svar.getDomain().getEnveloppeIterator())});
        }
    }

    private static String domain(DisposableIntIterator it) {
        values.setLength(0);
        while (it.hasNext()) {
            values.append(it.next()).append(' ');
        }
        it.dispose();

        return values.toString();
    }
}
