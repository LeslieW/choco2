package samples.tutorials.lns.rcpsp;/*
 * Created by IntelliJ IDEA.
 * User: sofdem - sophie.demassey{at}mines-nantes.fr
 * Date: 11/01/11 - 16:49
 */

import choco.kernel.common.util.tools.ArrayUtils;

/** @author Sophie Demassey */
public class RCPSPData {

final int nAct;
final int nRes;
int[][] pred;
int[] duration;
int[][] request;
int[] capacity;
int durationSum;

public RCPSPData(int nAct, int nRes)
{
	this.nAct = nAct;
	this.nRes = nRes;
	pred = new int[nAct][nAct];
	duration = new int[nAct];
	request = new int[nAct][nRes];
	capacity = new int[nRes];
}

public void setPrecedence(int actPred, int actSucc)
{
	this.pred[actPred][actSucc] = 1;
}

public void setDuration(int act, int duration)
{
	this.duration[act] = duration;
}

public void setRequest(int act, int res, int request)
{
	this.request[act][res] = request;
}

public void setCapacity(int res, int capacity)
{
	this.capacity[res] = capacity;
}

public int nAct()
{
	return nAct;
}

public int nRes()
{
	return nRes;
}

public boolean isPrecedence(int act1, int act2)
{
	return pred[act1][act2] > 0;
}

public int[] getDurations()
{
	return duration;
}

public int getRequest(int act, int res)
{
	return request[act][res];
}

public int[] getRequests(int res)
{
	return ArrayUtils.transpose(request)[res];
}

public int getCapacity(int res)
{
	return capacity[res];
}

public int getDurationSum()
{
	if (durationSum == 0) {
		for (int d : duration) { durationSum += d; }
	}
	return durationSum;
}

@Override
public String toString()
{
	return "RCPSPData{" + "nAct=" + nAct + ", nRes=" + nRes + '}';
}

}
