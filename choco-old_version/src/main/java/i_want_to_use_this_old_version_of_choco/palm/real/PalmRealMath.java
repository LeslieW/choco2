//     ___  ___         PaLM library for Choco system
//    /__ \/ __\        (c) 2001 - 2004 -- Narendra Jussien
//   ____\ |/_____
//  /_____\/_____ \     PalmExplanation based constraint programming tool
// |/   (_)(_)   \|
//       \ /            Version 0.1
//       \ /            January 2004
//       \ /
// ______\_/_______     Contibutors: Fran�ois Laburthe, Hadrien Cambazard, Guillaume Rochart...

package i_want_to_use_this_old_version_of_choco.palm.real;

import i_want_to_use_this_old_version_of_choco.palm.Explanation;
import i_want_to_use_this_old_version_of_choco.palm.PalmProblem;
import i_want_to_use_this_old_version_of_choco.palm.real.exp.PalmRealIntervalConstant;
import i_want_to_use_this_old_version_of_choco.real.RealInterval;
import i_want_to_use_this_old_version_of_choco.real.RealMath;

/**
 * Explained interval arithmetic.
 */
public class PalmRealMath extends RealMath {
  public static PalmRealInterval add(PalmProblem pb, RealInterval x, RealInterval y) {
    Explanation expOnInf = pb.makeExplanation();
    Explanation expOnSup = pb.makeExplanation();
    ((PalmRealInterval) x).self_explain(PalmRealInterval.INF, expOnInf);
    ((PalmRealInterval) y).self_explain(PalmRealInterval.INF, expOnInf);

    ((PalmRealInterval) x).self_explain(PalmRealInterval.SUP, expOnSup);
    ((PalmRealInterval) y).self_explain(PalmRealInterval.SUP, expOnSup);
    return new PalmRealIntervalConstant(prevFloat(x.getInf() + y.getInf()),
        nextFloat(x.getSup() + y.getSup()), expOnInf, expOnSup);
  }

  public static PalmRealInterval sub(PalmProblem pb, RealInterval x, RealInterval y) {
    Explanation expOnInf = pb.makeExplanation();
    Explanation expOnSup = pb.makeExplanation();
    ((PalmRealInterval) x).self_explain(PalmRealInterval.INF, expOnInf);
    ((PalmRealInterval) y).self_explain(PalmRealInterval.SUP, expOnInf);

    ((PalmRealInterval) x).self_explain(PalmRealInterval.SUP, expOnSup);
    ((PalmRealInterval) y).self_explain(PalmRealInterval.INF, expOnSup);
    return new PalmRealIntervalConstant(prevFloat(x.getInf() - y.getSup()),
        nextFloat(x.getSup() - y.getInf()), expOnInf, expOnSup);
  }

  public static PalmRealInterval mul(PalmProblem pb, RealInterval x, RealInterval y) {
    double i, s;
    Explanation expOnInf = pb.makeExplanation();
    Explanation expOnSup = pb.makeExplanation();

    /*((PalmRealInterval)x).self_explain(PalmRealInterval.DOM, expOnInf);
    ((PalmRealInterval)x).self_explain(PalmRealInterval.DOM, expOnSup);
    ((PalmRealInterval)y).self_explain(PalmRealInterval.DOM, expOnInf);
    ((PalmRealInterval)y).self_explain(PalmRealInterval.DOM, expOnSup);*/

    if ((x.getInf() == 0.0 && x.getSup() == 0.0)) {
      i = 0.0;
      s = NEG_ZER0; // Ca peut etre utile pour rejoindre des intervalles : si on veut aller de -5 a 0,
      // ca sera 0-.
      ((PalmRealInterval) x).self_explain(PalmRealInterval.DOM, expOnInf);
      ((PalmRealInterval) x).self_explain(PalmRealInterval.DOM, expOnSup);
    } else if ((y.getInf() == 0.0 && y.getSup() == 0.0)) {
      i = 0.0;
      s = NEG_ZER0;
      ((PalmRealInterval) y).self_explain(PalmRealInterval.DOM, expOnInf);
      ((PalmRealInterval) y).self_explain(PalmRealInterval.DOM, expOnSup);
    } else {
      if (x.getInf() >= 0.0) {
        ((PalmRealInterval) x).self_explain(PalmRealInterval.INF, expOnInf);
        ((PalmRealInterval) x).self_explain(PalmRealInterval.INF, expOnSup);
        if (y.getInf() >= 0.0) {
          ((PalmRealInterval) y).self_explain(PalmRealInterval.INF, expOnInf);
          ((PalmRealInterval) y).self_explain(PalmRealInterval.INF, expOnSup);
          // Inf Bound
          // No more explanations
          i = Math.max(ZERO, prevFloat(x.getInf() * y.getInf())); // Si x et y positifs, on ne veut pas etre n?gatif !
          // Sup Bound
          // Upper bounds of x and y explanations
          ((PalmRealInterval) x).self_explain(PalmRealInterval.SUP, expOnSup);
          ((PalmRealInterval) y).self_explain(PalmRealInterval.SUP, expOnSup);
          s = nextFloat(x.getSup() * y.getSup());
        } else if (y.getSup() <= 0.0) {
          ((PalmRealInterval) y).self_explain(PalmRealInterval.SUP, expOnInf);
          ((PalmRealInterval) y).self_explain(PalmRealInterval.SUP, expOnSup);
          // Inf Bound
          // X sup and Y inf
          ((PalmRealInterval) x).self_explain(PalmRealInterval.SUP, expOnInf);
          ((PalmRealInterval) y).self_explain(PalmRealInterval.INF, expOnInf);
          i = prevFloat(x.getSup() * y.getInf());
          // Sup Bound
          s = Math.min(ZERO, nextFloat(x.getInf() * y.getSup()));
        } else {
          // Inf Bound
          // X sup and Y inf
          ((PalmRealInterval) x).self_explain(PalmRealInterval.SUP, expOnInf);
          ((PalmRealInterval) y).self_explain(PalmRealInterval.INF, expOnInf);
          i = prevFloat(x.getSup() * y.getInf());
          // Sup Bound
          // X sup and Y sup
          ((PalmRealInterval) x).self_explain(PalmRealInterval.SUP, expOnSup);
          ((PalmRealInterval) y).self_explain(PalmRealInterval.SUP, expOnSup);
          s = nextFloat(x.getSup() * y.getSup());
        }
      } else if (x.getSup() <= 0.0) {
        ((PalmRealInterval) x).self_explain(PalmRealInterval.SUP, expOnInf);
        ((PalmRealInterval) x).self_explain(PalmRealInterval.SUP, expOnSup);
        if (y.getInf() >= 0.0) {
          ((PalmRealInterval) y).self_explain(PalmRealInterval.INF, expOnInf);
          ((PalmRealInterval) y).self_explain(PalmRealInterval.INF, expOnSup);
          // Inf Bound
          // X inf and Y sup
          ((PalmRealInterval) x).self_explain(PalmRealInterval.INF, expOnInf);
          ((PalmRealInterval) y).self_explain(PalmRealInterval.SUP, expOnInf);
          i = prevFloat(x.getInf() * y.getSup());
          // Sup Bound
          // -
          s = Math.min(ZERO, nextFloat(x.getSup() * y.getInf()));
        } else if (y.getSup() <= 0.0) {
          ((PalmRealInterval) y).self_explain(PalmRealInterval.SUP, expOnInf);
          ((PalmRealInterval) y).self_explain(PalmRealInterval.SUP, expOnSup);
          // Inf Bound
          // -
          i = Math.max(ZERO, prevFloat(x.getSup() * y.getSup()));
          // Sup Bound
          // X inf and Y inf
          ((PalmRealInterval) x).self_explain(PalmRealInterval.INF, expOnSup);
          ((PalmRealInterval) y).self_explain(PalmRealInterval.INF, expOnSup);
          s = nextFloat(x.getInf() * y.getInf());
        } else {
          // Inf Bound
          // X inf and Y sup
          ((PalmRealInterval) x).self_explain(PalmRealInterval.INF, expOnInf);
          ((PalmRealInterval) y).self_explain(PalmRealInterval.SUP, expOnInf);
          i = prevFloat(x.getInf() * y.getSup());
          // Sup Bound
          // X inf and Y inf
          ((PalmRealInterval) x).self_explain(PalmRealInterval.INF, expOnSup);
          ((PalmRealInterval) y).self_explain(PalmRealInterval.INF, expOnSup);
          s = nextFloat(x.getInf() * y.getInf());
        }
      } else {
        if (y.getInf() >= 0.0) {
          ((PalmRealInterval) y).self_explain(PalmRealInterval.INF, expOnInf);
          ((PalmRealInterval) y).self_explain(PalmRealInterval.INF, expOnSup);
          // Inf Bound
          // X inf and Y sup
          ((PalmRealInterval) x).self_explain(PalmRealInterval.INF, expOnInf);
          ((PalmRealInterval) y).self_explain(PalmRealInterval.SUP, expOnInf);
          i = prevFloat(x.getInf() * y.getSup());
          // Sup Bound
          // X sup and Y sup
          ((PalmRealInterval) x).self_explain(PalmRealInterval.SUP, expOnSup);
          ((PalmRealInterval) y).self_explain(PalmRealInterval.SUP, expOnSup);
          s = nextFloat(x.getSup() * y.getSup());
        } else if (y.getSup() <= 0.0) {
          ((PalmRealInterval) y).self_explain(PalmRealInterval.SUP, expOnInf);
          ((PalmRealInterval) y).self_explain(PalmRealInterval.SUP, expOnSup);
          // Inf Bound
          // X sup and Y inf
          ((PalmRealInterval) x).self_explain(PalmRealInterval.SUP, expOnInf);
          ((PalmRealInterval) y).self_explain(PalmRealInterval.INF, expOnInf);
          i = prevFloat(x.getSup() * y.getInf());
          // Sup Bound
          // X inf and Y inf
          ((PalmRealInterval) x).self_explain(PalmRealInterval.INF, expOnSup);
          ((PalmRealInterval) y).self_explain(PalmRealInterval.INF, expOnSup);
          s = nextFloat(x.getInf() * y.getInf());
        } else {
          ((PalmRealInterval) x).self_explain(PalmRealInterval.DOM, expOnInf);
          ((PalmRealInterval) x).self_explain(PalmRealInterval.DOM, expOnSup);
          ((PalmRealInterval) y).self_explain(PalmRealInterval.DOM, expOnInf);
          ((PalmRealInterval) y).self_explain(PalmRealInterval.DOM, expOnSup);
          i = Math.min(prevFloat(x.getInf() * y.getSup()),
              prevFloat(x.getSup() * y.getInf()));
          s = Math.max(nextFloat(x.getInf() * y.getInf()),
              nextFloat(x.getSup() * y.getSup()));
        }
      }
    }

    return new PalmRealIntervalConstant(i, s, expOnInf, expOnSup);
  }

  /**
   * y should not contain 0 !
   *
   * @param x
   * @param y
   * @return TODO
   */
  public static PalmRealInterval odiv(PalmProblem pb, RealInterval x, RealInterval y) {
    Explanation expOnInf = pb.makeExplanation();
    Explanation expOnSup = pb.makeExplanation();

    /*((PalmRealInterval)x).self_explain(PalmRealInterval.DOM, expOnInf);
    ((PalmRealInterval)x).self_explain(PalmRealInterval.DOM, expOnSup);
    ((PalmRealInterval)y).self_explain(PalmRealInterval.DOM, expOnInf);
    ((PalmRealInterval)y).self_explain(PalmRealInterval.DOM, expOnSup);*/

    if (y.getInf() <= 0.0 && y.getSup() >= 0.0) {
      throw new UnsupportedOperationException();
    } else {
      double yl = y.getInf();
      double yh = y.getSup();
      double i, s;
      if (yh == 0.0) yh = NEG_ZER0;

      if (x.getInf() >= 0.0) {
        ((PalmRealInterval) x).self_explain(PalmRealInterval.INF, expOnInf);
        ((PalmRealInterval) x).self_explain(PalmRealInterval.INF, expOnSup);
        if (yl >= 0.0) {
          ((PalmRealInterval) y).self_explain(PalmRealInterval.INF, expOnInf);
          ((PalmRealInterval) y).self_explain(PalmRealInterval.INF, expOnSup);
          // Inf Bound
          // Y sup
          ((PalmRealInterval) y).self_explain(PalmRealInterval.SUP, expOnInf);
          i = Math.max(ZERO, prevFloat(x.getInf() / yh));
          // Sup Bound
          // X sup
          ((PalmRealInterval) x).self_explain(PalmRealInterval.SUP, expOnSup);
          s = nextFloat(x.getSup() / yl);
        } else { // yh <= 0
          ((PalmRealInterval) y).self_explain(PalmRealInterval.SUP, expOnInf);
          ((PalmRealInterval) y).self_explain(PalmRealInterval.SUP, expOnSup);
          // Inf Bound
          // X sup
          ((PalmRealInterval) x).self_explain(PalmRealInterval.SUP, expOnInf);
          i = prevFloat(x.getSup() / yh);
          // Sup Bound
          // Y inf
          ((PalmRealInterval) y).self_explain(PalmRealInterval.INF, expOnSup);
          s = Math.min(ZERO, nextFloat(x.getInf() / yl));
        }
      } else if (x.getSup() <= 0.0) {
        ((PalmRealInterval) x).self_explain(PalmRealInterval.SUP, expOnInf);
        ((PalmRealInterval) x).self_explain(PalmRealInterval.SUP, expOnSup);
        if (yl >= 0.0) {
          ((PalmRealInterval) y).self_explain(PalmRealInterval.INF, expOnInf);
          ((PalmRealInterval) y).self_explain(PalmRealInterval.INF, expOnSup);
          // Inf Bound
          // X inf
          ((PalmRealInterval) x).self_explain(PalmRealInterval.INF, expOnInf);
          i = prevFloat(x.getInf() / yl);
          // Sup Bound
          // Y sup
          ((PalmRealInterval) y).self_explain(PalmRealInterval.SUP, expOnSup);
          s = Math.min(ZERO, nextFloat(x.getSup() / yh));
        } else {
          ((PalmRealInterval) y).self_explain(PalmRealInterval.SUP, expOnInf);
          ((PalmRealInterval) y).self_explain(PalmRealInterval.SUP, expOnSup);
          // Inf Bound
          // Y inf
          ((PalmRealInterval) y).self_explain(PalmRealInterval.INF, expOnInf);
          i = Math.max(ZERO, prevFloat(x.getSup() / yl));
          // Sup Bound
          // X inf
          ((PalmRealInterval) x).self_explain(PalmRealInterval.INF, expOnSup);
          s = nextFloat(x.getInf() / yh);
        }
      } else {
        if (yl >= 0.0) {
          ((PalmRealInterval) y).self_explain(PalmRealInterval.INF, expOnInf);
          ((PalmRealInterval) y).self_explain(PalmRealInterval.INF, expOnSup);
          // Inf Bound
          // X inf
          ((PalmRealInterval) x).self_explain(PalmRealInterval.INF, expOnInf);
          i = prevFloat(x.getInf() / yl);
          // Sup Bound
          // X sup
          ((PalmRealInterval) x).self_explain(PalmRealInterval.SUP, expOnSup);
          s = nextFloat(x.getSup() / yl);
        } else {
          ((PalmRealInterval) y).self_explain(PalmRealInterval.SUP, expOnInf);
          ((PalmRealInterval) y).self_explain(PalmRealInterval.SUP, expOnSup);
          // Inf Bound
          // X sup
          ((PalmRealInterval) x).self_explain(PalmRealInterval.SUP, expOnInf);
          i = prevFloat(x.getSup() / yh);
          // Sup Bound
          // X inf
          ((PalmRealInterval) x).self_explain(PalmRealInterval.INF, expOnSup);
          s = nextFloat(x.getInf() / yh);
        }
      }
      return new PalmRealIntervalConstant(i, s, expOnInf, expOnSup);
    }
  }

  public static PalmRealInterval odiv_wrt(PalmProblem pb, RealInterval x, RealInterval y, RealInterval res) {
    if (y.getInf() > 0.0 || y.getSup() < 0.0) {  // y != 0
      return odiv(pb, x, y);
    } else {
      double resl = res.getInf();
      double resh = res.getSup();
      Explanation expOnInf = pb.makeExplanation();
      Explanation expOnSup = pb.makeExplanation();
      // TODO : voir si on peut faire mieux !
      ((PalmRealInterval) x).self_explain(PalmRealInterval.DOM, expOnInf);
      ((PalmRealInterval) x).self_explain(PalmRealInterval.DOM, expOnSup);
      ((PalmRealInterval) y).self_explain(PalmRealInterval.DOM, expOnInf);
      ((PalmRealInterval) y).self_explain(PalmRealInterval.DOM, expOnSup);
      ((PalmRealInterval) res).self_explain(PalmRealInterval.DOM, expOnInf);
      ((PalmRealInterval) res).self_explain(PalmRealInterval.DOM, expOnSup);

      if (x.getInf() >= 0.0) {
        double tmp_neg = nextFloat(x.getInf() / y.getInf()); // la plus grande valeur negative
        double tmp_pos = prevFloat(x.getInf() / y.getSup()); // la plus petite valeur positive

        if ((resl > tmp_neg || resl == 0.0) && resl < tmp_pos) resl = tmp_pos;
        if ((resh < tmp_pos || resh == 0.0) && resh > tmp_neg) resh = tmp_neg;
      } else if (x.getSup() <= 0.0) {
        double tmp_neg = nextFloat(x.getSup() / y.getSup());
        double tmp_pos = nextFloat(x.getSup() / y.getInf());

        if ((resl > tmp_neg || resl == 0.0) && resl < tmp_pos) resl = tmp_pos;
        if ((resh < tmp_pos || resh == 0.0) && resh > tmp_neg) resh = tmp_neg;
      }

      return new PalmRealIntervalConstant(resl, resh, expOnInf, expOnSup);
    }
  }
}