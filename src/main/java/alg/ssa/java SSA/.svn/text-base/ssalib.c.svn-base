/* ssalib.f -- translated by f2c (version 19970805).
   You must link the resulting object file with the libraries:
	-lf2c -lm   (in that order)
*/

#include "f2c.h"

/* Common Block Declarations */

struct {
    real rho[10000]	/* was [100][100] */, rho1[10000]	/* was [100][
	    100] */, c__[10000]	/* was [100][100] */, eval[10000], x[1111]	
	    /* was [101][11] */, x2[1111]	/* was [101][11] */;
    integer indi[4950], indj[4950];
    real dist[4950], phi[11];
    integer noties[1000];
    real prox[4950], cmean[10];
    integer isten[4950];
    real r__[4950];
    integer ix[10000];
} _BLNK__;

#define _BLNK__1 _BLNK__

/* Table of constant values */
cllist cl__1;
integer f_clos();
static recalc;

static integer c__1 = 1;
static integer c__9 = 9;
static integer c__0 = 0;
static integer c__4 = 4;
static real c_b675 = (float)1.;
static integer c__3 = 3;

/* Main program */ MAIN__()
{
    /* Format strings */
    static char fmt_533[] = "(a80)";
    static char fmt_326[] = "(2f4.0)";
    static char fmt_332[] = "(24i3)";
    static char fmt_155[] = "";
    static char fmt_156[] = "";
    static char fmt_325[] = "(36a2)";
    static char fmt_333[] = "(\0021GUTTMAN-LINGOES SSA-I COORDINATES FOR \
\002,i3,\002 DIMENSIONS\002,\002 SEMI-STRONG MONOTONICITY \002,/,10x,\002 DI\
MENSION\002,10i10)";
    static char fmt_334[] = "(\0021KRUSKAL-GUTTMAN-LINGOES SSA-I COORDINATES\
 FOR \002,i3,\002 DIMENSIONS\002,\002 WEAK MONOTONICITY \002,/,10x,\002 DIME\
NSION\002,10i10)";
    static char fmt_329[] = "(\002 \002,120(\002-\002)/15x,\002CENTRALITY\
\002/\002 VARIABLE         INDEX\002)";
    static char fmt_330[] = "(i10,5x2p11f10.3)";
    static char fmt_335[] = "(\002 GUTTMAN-LINGOES' COEFFICIENT OF ALIENATIO\
N =\002,f8.5,\002 IN\002,i4,\002 ITERATIONS.\002,/,\002 KRUSKAL'S STRESS \
=\002,f8.5)";
    static char fmt_336[] = "(\002 KRUSKAL'S STRESS =\002,f8.5,\002 IN\002,i\
4,\002 ITERATIONS.\002,/,\002 GUTTMAN-LINGOES' COEFFICIENT OF ALIENATION \
=\002,f8.5)";
    static char fmt_340[] = "(\002 G-L'S PHI FOR LOCAL MONOTONICITY =\002,f8\
.5,/,)";
    static char fmt_339[] = "(\002 FURTHER ITERATIONS MAY BE REQUIERD IN THI\
S M ****. \002)";
    static char fmt_328[] = "(10f8.3)";
    static char fmt_338[] = "(\002 *** MORE THAN 500 TIED BLOCKS.REDUCE OR R\
EDIMENISION NOTIES\002)";
    static char fmt_331[] = "(\002+\002,109xf9.5,i8)";
    static char fmt_351[] = "(\0021SSA1 SMALLEST SPACE ANALYSIS -1(UNCONDITI\
ONAL MATRIX)\002,/\002 PROGRAMMED BY JAMES LINGOES UNIV OF MICHIGAN DEPT OF \
PSYCHOLOGY\002,/\002 IMPLEMENTED ON ULCC 7600 JAN74 BY STEVE TAGG UNIV SURRE\
Y DEPT OF PSYCHOLOGY\002)";
    static char fmt_352[] = "(\002 IF YOU PUBLISH ACKNOWLEDGE THIS REFERENCE\
 AT LEAST\002,/\002 THE GUTTMAN LINGOES NONMETRIC PROGRAM SERIES\002,/\002 J\
AMES C. LINGOES\002,/\002 ANN ARBOR MICHIGAN ; MATHESIS PRESS ;1973 \002)";
    static char fmt_605[] = "(///\002   ANALYSIS FINISHED\002)";

    /* System generated locals */
    integer i__1, i__2, i__3;
    real r__1, r__2;
    olist o__1;

    /* Builtin functions */
    integer f_open(), s_rsfe(), do_fio(), e_rsfe(), s_rsle(), do_lio(), 
	    e_rsle();
    double sqrt();
    integer s_wsfe(), e_wsfe();
    /* Subroutine */ int s_stop();

    /* Local variables */
    extern /* Subroutine */ int glac_();
    static real code, fnel;
    static integer ifbu, mind, maxd, isim;
    extern /* Subroutine */ int menu_();
    static integer ifsr, iter, itct, miss;
    static char fout[80];
    static real avst, zero;
    extern /* Subroutine */ int sort_();
    static integer nelp1;
    static real d__;
    static integer i__, j, k, l, m;
    static real s;
    extern /* Subroutine */ int eigen_();
    static integer lfact, ifglk, iffix;
    extern /* Subroutine */ int pmcor_();
    static real d1, ptemp;
    static integer n1;
    extern /* Subroutine */ int mxout_();
    static real aa[4950], bb[4950], cc[10000], dd;
    static integer id, kd, ld, md, nd, ii;
    static real fn[100];
    static integer jj, kk, ij, ll, ji, mm, nn, lr, mr, nr, nt, ifconf;
    extern /* Subroutine */ int euclid_();
    static real sx;
    extern /* Subroutine */ int confin_();
    static real xx[100];
    static integer ip1;
    static real stress, strlst;
    static integer idd[100], ifc, ifd, ife, ifg, ifk;
    static real dim;
    static integer ind;
    static char fin[80];
    static integer nig, igo, nel;
    static char ans[1];
    extern /* Subroutine */ int fit_();
    static real glp, fnr;
    static char fmt[80];
    static real fnt, dsq;
    static integer nit;
    static real cut, dsv;
    static integer isw, isx[100];
    extern /* Subroutine */ int initcon_();
    static integer mdm1, nrm1, ipt1, ipt2, nrp1;

    /* Fortran I/O blocks */
    static cilist io___5 = { 0, 11, 0, fmt_533, 0 };
    static cilist io___7 = { 0, 11, 0, fmt_533, 0 };
    static cilist io___47 = { 0, 11, 0, 0, 0 };
    static cilist io___51 = { 0, 1, 0, fmt_326, 0 };
    static cilist io___53 = { 0, 1, 0, fmt_332, 0 };
    static cilist io___64 = { 0, 1, 0, fmt, 0 };
    static cilist io___87 = { 0, 3, 0, fmt_325, 0 };
    static cilist io___88 = { 0, 3, 0, fmt_333, 0 };
    static cilist io___89 = { 0, 3, 0, fmt_334, 0 };
    static cilist io___90 = { 0, 3, 0, fmt_329, 0 };
    static cilist io___91 = { 0, 10, 0, fmt_330, 0 };
    static cilist io___92 = { 0, 3, 0, fmt_330, 0 };
    static cilist io___93 = { 0, 3, 0, fmt_335, 0 };
    static cilist io___94 = { 0, 12, 0, fmt_335, 0 };
    static cilist io___95 = { 0, 3, 0, fmt_336, 0 };
    static cilist io___96 = { 0, 12, 0, fmt_336, 0 };
    static cilist io___97 = { 0, 3, 0, fmt_340, 0 };
    static cilist io___98 = { 0, 12, 0, fmt_340, 0 };
    static cilist io___99 = { 0, 3, 0, fmt_339, 0 };
    static cilist io___100 = { 0, 3, 0, fmt_328, 0 };
    static cilist io___102 = { 0, 3, 0, fmt_338, 0 };
    static cilist io___105 = { 0, 3, 0, fmt_330, 0 };
    static cilist io___106 = { 0, 3, 0, fmt_331, 0 };
    static cilist io___108 = { 0, 3, 0, fmt_351, 0 };
    static cilist io___109 = { 0, 3, 0, fmt_352, 0 };
    static cilist io___110 = { 0, 3, 0, fmt_605, 0 };


    /* Assigned format variables */
    static char *n1_fmt;

/*     Following line added by Malcolm Huntley November 20th. 1996 */
/*     NR=100,NR**2=10000,NR+1=101,MAXD+1=11,(NR*(NR-1))/2=4950,         S
SA1       11*/
/*     2*NUMBER OF TIED BLOCKS=1000,MAXD=10                              S
SA1       12*/

/* **************************************************************** */
/**                       ASSIGN FILES                           *        
SSA1       13*/
/* **************************************************************** */
/* 	  Added by Malcolm Huntley 20 November 1996: intialize routine */
    for (k = 1; k <= 4950; ++k) {
	aa[k - 1] = (float)0.;
	bb[k - 1] = (float)0.;
	cc[k - 1] = (float)0.;
/* L1111: */
    }
/* 	  End of ammended code */
    o__1.oerr = 0;
    o__1.ounit = 11;
    o__1.ofnmlen = 8;
    o__1.ofnm = "INST.TMP";
    o__1.orl = 0;
    o__1.osta = "OLD";
    o__1.oacc = 0;
    o__1.ofm = 0;
    o__1.oblnk = 0;
    f_open(&o__1);


/* L532: */
/* L533: */
/* L534: */
/* L535: */
/* L536: */
/* L537: */

    s_rsfe(&io___5);
    do_fio(&c__1, fin, 80L);
    e_rsfe();
    s_rsfe(&io___7);
    do_fio(&c__1, fout, 80L);
    e_rsfe();
    o__1.oerr = 0;
    o__1.ounit = 1;
    o__1.ofnmlen = 80;
    o__1.ofnm = fin;
    o__1.orl = 0;
    o__1.osta = "OLD";
    o__1.oacc = 0;
    o__1.ofm = 0;
    o__1.oblnk = 0;
    f_open(&o__1);
    if (recalc == 0) {
	o__1.oerr = 0;
	o__1.ounit = 3;
	o__1.ofnmlen = 80;
	o__1.ofnm = fout;
	o__1.orl = 0;
	o__1.osta = "UNKNOWN";
	o__1.oacc = 0;
	o__1.ofm = 0;
	o__1.oblnk = 0;
	f_open(&o__1);
    }
    if (recalc == 0) {
	o__1.oerr = 0;
	o__1.ounit = 10;
	o__1.ofnmlen = 10;
	o__1.ofnm = "COORDS.TMP";
	o__1.orl = 0;
	o__1.osta = "UNKNOWN";
	o__1.oacc = 0;
	o__1.ofm = 0;
	o__1.oblnk = 0;
	f_open(&o__1);
    }
    o__1.oerr = 0;
    o__1.ounit = 12;
    o__1.ofnmlen = 10;
    o__1.ofnm = "COEFFS.TMP";
    o__1.orl = 0;
    o__1.osta = "UNKNOWN";
    o__1.oacc = 0;
    o__1.ofm = 0;
    o__1.oblnk = 0;
    f_open(&o__1);
    o__1.oerr = 0;
    o__1.ounit = 2;
    o__1.ofnmlen = 10;
    o__1.ofnm = "PREDEF.TMP";
    o__1.orl = 0;
    o__1.osta = "OLD";
    o__1.oacc = 0;
    o__1.ofm = 0;
    o__1.oblnk = 0;
    f_open(&o__1);

/* **************************************************************** */
/* *                   SUBROUTINE DIMENSIONING                    * */
/* **************************************************************** */

    md = 200;
    nd = 11;
    kd = 4950;
    ld = md + 1;
    mdm1 = md - 1;
    kd = md * mdm1 / 2;

/* *************************************************************** */
/* *                   SET TOLERANCE FOR ZERO                    * */
/* *************************************************************** */

    zero = (float)1e-6;

/* *************************************************************** */
/* *            INPUT THE PARAMETERS FOR THE ANALYSIS            * */
/* *************************************************************** */

    menu_(fmt, &nr, &mind, &maxd, &isim, &ifd, &ifc, &ifglk, &ifconf, &iffix, 
	    &ifsr, &miss, &ife, &ifg, &code, &cut, 80L);

/* *************************************************************** */
/* *                        INITIALIZATION                       * */
/* *************************************************************** */
/* 	  Iter changed from 100 to 500 22 October 1995 Malcolm Huntley */
    iter = 500;
    lr = 0;
    isw = 0;
    nit = 5;
    ifk = 0;
    if (ife == 1 && mind == 0) {
	mind = 1;
    }
    igo = 2;
    if (ifg + isim == -1) {
	igo = 1;
    }
    if (nr > 50 && iffix == 0) {
	iter = 150;
    }
    nrp1 = nr + 1;
    nrm1 = nr - 1;
    fnr = (real) nr;

/* *************************************************************** */
/* *                    INPUT CONFIGURATION                      * */
/* *************************************************************** */

    if (ifconf != 0) {
	goto L2;
    } else {
	goto L7;
    }
L2:
    confin_(&mind, &nr, &maxd, _BLNK__1.x, _BLNK__1.cmean, &c__1);

/* *************************************************************** */
/* *         INITIALIZE FOR FIXED CONFIGURATION OPTION           * */
/* *************************************************************** */

L7:
    if (iffix != 0) {
	goto L8;
    } else {
	goto L18;
    }
L8:
    initcon_(&nr, &maxd, &miss, &ifsr, &isw, &md, fmt, _BLNK__1.prox, 
	    _BLNK__1.c__, _BLNK__1.isten, _BLNK__1.eval, &nel, 80L);
    goto L84;

/* *************************************************************** */
/* *                    GENERATE COEFFICIENTS                    * */
/* *************************************************************** */

L18:
    jj = 0;
    if (ifsr != 0) {
	goto L19;
    } else {
	goto L37;
    }
L19:
/* L899: */
/* L900: */
    if (isim != 0) {
	goto L9009;
    } else {
	goto L9000;
    }
L9000:
    euclid_(&nr, fmt, _BLNK__1.c__, &sx, xx, fn, &dd, 80L);
    goto L9010;
L9009:
/* L9001: */
    s_rsle(&io___47);
    do_lio(&c__9, &c__1, ans, 1L);
    e_rsle();
    if (*(unsigned char *)ans == 'N' || *(unsigned char *)ans == 'n') {
	goto L9004;
    }
/* L9003: */
    glac_(&nr, fmt, idd, isx, _BLNK__1.c__, fn, aa, bb, cc, 80L);
    goto L9010;
L9004:
    pmcor_(&nr, &md, fmt, _BLNK__1.prox, _BLNK__1.c__, &_BLNK__1.prox[ld - 1],
	     _BLNK__1.eval, 80L);
L9010:
/* L901: */
/* L902: */

    if (miss == 0) {
	goto L71;
    }
    nel = nr * nrm1 / 2;

/*     GENERATE  PASSIVE CELL STENCIL */

    s_rsfe(&io___51);
    do_fio(&c__1, (char *)&nig, (ftnlen)sizeof(integer));
    e_rsfe();
    if (nig != 9999) {
	goto L20;
    }
    goto L23;
L20:
    nig <<= 1;
    s_rsfe(&io___53);
    i__1 = nig;
    for (ii = 1; ii <= i__1; ++ii) {
	do_fio(&c__1, (char *)&_BLNK__1.ix[ii - 1], (ftnlen)sizeof(integer));
    }
    e_rsfe();
    _BLNK__1.ix[nig] = 0;
    kk = 1;
    ii = 0;
    i__1 = nrm1;
    for (i__ = 1; i__ <= i__1; ++i__) {
	ip1 = i__ + 1;
	i__2 = nr;
	for (j = ip1; j <= i__2; ++j) {
	    ++ii;
	    if (_BLNK__1.ix[kk - 1] == i__ && _BLNK__1.ix[kk] == j) {
		goto L21;
	    }
	    _BLNK__1.isten[ii - 1] = 0;
	    goto L22;
L21:
	    _BLNK__1.isten[ii - 1] = 1;
	    kk += 2;
L22:
	    ;
	}
    }
L23:
    if (ifg >= 0) {
	goto L34;
    }

/*    TIE INPUT FOR LOCAL MONOTONICITY/CLUSTERING                        S
SA1      128*/

    ll = 0;
    kk = 0;
    dim = (float)0.;
    i__2 = nrm1;
    for (i__ = 1; i__ <= i__2; ++i__) {
	ip1 = i__ + 1;
	i__1 = nr;
	for (j = ip1; j <= i__1; ++j) {
	    ++ll;
	    if (_BLNK__1.isten[ll - 1] == 1) {
		goto L27;
	    }
	    switch ((int)igo) {
		case 1:  goto L24;
		case 2:  goto L25;
	    }
L24:
	    if (_BLNK__1.c__[j + i__ * 100 - 101] - cut >= (float)0.) {
		goto L26;
	    } else {
		goto L27;
	    }
L25:
	    if (cut - _BLNK__1.c__[j + i__ * 100 - 101] >= (float)0.) {
		goto L26;
	    } else {
		goto L27;
	    }
L26:
	    dim += _BLNK__1.c__[j + i__ * 100 - 101];
	    ++kk;
L27:
	    ;
	}
    }
    if (kk > 1) {
	goto L28;
    }
    ifg = 0;
    goto L34;
L28:
    fnt = (real) kk;
    ll = 0;
    dim /= fnt;
    i__1 = nrm1;
    for (i__ = 1; i__ <= i__1; ++i__) {
	ip1 = i__ + 1;
	i__2 = nr;
	for (j = ip1; j <= i__2; ++j) {
	    ++ll;
	    if (_BLNK__1.isten[ll - 1] == 1) {
		goto L32;
	    }
	    switch ((int)igo) {
		case 1:  goto L29;
		case 2:  goto L30;
	    }
L29:
	    if (_BLNK__1.c__[j + i__ * 100 - 101] - cut >= (float)0.) {
		goto L31;
	    } else {
		goto L32;
	    }
L30:
	    if (cut - _BLNK__1.c__[j + i__ * 100 - 101] >= (float)0.) {
		goto L31;
	    } else {
		goto L32;
	    }
L31:
	    _BLNK__1.c__[j + i__ * 100 - 101] = dim;
L32:
	    ;
	}
    }
    if (ifg == -2) {
	goto L33;
    }
    ij = kk + 1;
    goto L34;
L33:
    ij = 1;
    ji = kk;
L34:
    if (ifsr != 0) {
	goto L35;
    } else {
	goto L41;
    }
L35:
    ii = 0;
    i__2 = nrm1;
    for (i__ = 1; i__ <= i__2; ++i__) {
	ip1 = i__ + 1;
	_BLNK__1.c__[i__ + i__ * 100 - 101] = (float)0.;
	i__1 = nr;
	for (j = ip1; j <= i__1; ++j) {
	    ++ii;
	    _BLNK__1.prox[ii - 1] = _BLNK__1.c__[j + i__ * 100 - 101];
	    _BLNK__1.eval[ii - 1] = _BLNK__1.prox[ii - 1];
	    if (_BLNK__1.isten[ii - 1] == 1) {
		goto L36;
	    }
	    ++jj;
	    _BLNK__1.r__[jj - 1] = _BLNK__1.c__[j + i__ * 100 - 101];
L36:
	    ;
	}
    }
    if (jj == nel) {
	lr = 1;
    }
    goto L45;
L37:
    i__1 = nr;
    for (i__ = 2; i__ <= i__1; ++i__) {
	ip1 = i__ - 1;
	s_rsfe(&io___64);
	i__2 = ip1;
	for (j = 1; j <= i__2; ++j) {
	    do_fio(&c__1, (char *)&_BLNK__1.c__[i__ + j * 100 - 101], (ftnlen)
		    sizeof(real));
	}
	e_rsfe();
/* L38: */
    }
    if (miss == 0) {
	goto L71;
    }

/*    GENERATE PASSIVE CELL STENCIL                                      S
SA1      184*/

    ii = 0;
    s = (float)0.;
    i__1 = nrm1;
    for (i__ = 1; i__ <= i__1; ++i__) {
	ip1 = i__ + 1;
	_BLNK__1.c__[i__ + i__ * 100 - 101] = (float)0.;
	i__2 = nr;
	for (j = ip1; j <= i__2; ++j) {
	    ++ii;
	    if (_BLNK__1.c__[j + j * 100 - 101] - code != (float)0.) {
		goto L3339;
	    } else {
		goto L39;
	    }
L3339:
	    s += _BLNK__1.c__[j + i__ * 100 - 101];
	    _BLNK__1.isten[ii - 1] = 0;
	    goto L40;
L39:
	    _BLNK__1.isten[ii - 1] = 1;
	    ++jj;
L40:
	    ;
	}
    }
    goto L23;
L41:
    if (jj == 0) {
	lr = 1;
    }

/*    REPLACE MISSING CELLS WITH GRAND MEN                               S
SA1      201*/

    s /= (real) (ii - jj);
    ii = 0;
    jj = 0;
    i__2 = nrm1;
    for (i__ = 1; i__ <= i__2; ++i__) {
	ip1 = i__ + 1;
	i__1 = nr;
	for (j = ip1; j <= i__1; ++j) {
	    ++ii;
	    if (_BLNK__1.c__[j + j * 100 - 101] - code != (float)0.) {
		goto L4442;
	    } else {
		goto L42;
	    }
L4442:
	    _BLNK__1.c__[j + i__ * 100 - 101] = s;
	    goto L43;
L42:
	    ++jj;
	    _BLNK__1.r__[jj - 1] = _BLNK__1.c__[j + i__ * 100 - 101];
L43:
	    _BLNK__1.prox[ii - 1] = _BLNK__1.c__[j + i__ * 100 - 101];
	    _BLNK__1.eval[ii - 1] = _BLNK__1.prox[ii - 1];
/* L44: */
	}
    }
L45:
    _BLNK__1.c__[nr + nr * 100 - 101] = (float)0.;
    lfact = mind;

/*    PRINT INPUT VALUES                                                 S
SA1      219*/

    if (ifd == 0) {
	goto L7447;
    }
    mxout_(_BLNK__1.c__, &nr, &c__0, &md);
L7447:
    if (ifconf != 0) {
	goto L63;
    } else {
	goto L46;
    }
L46:
    sort_(&isw, &ii, _BLNK__1.prox, &isim, _BLNK__1.indi, &md);
    s = (float)0.;
    i__1 = ii;
    for (j = 1; j <= i__1; ++j) {
	mm = _BLNK__1.indi[j - 1];
	s += (float)1.;
/* L47: */
	_BLNK__1.prox[mm - 1] = s;
    }

/*    SUBSTITUTE MEAN RANKS FOR TIES                                     S
SA1      230*/

    j = 1;
    k = j;
L48:
    ll = 1;
    mm = _BLNK__1.indi[j - 1];
    d1 = _BLNK__1.prox[mm - 1];
L49:
    ++k;
    nn = _BLNK__1.indi[k - 1];
    if (_BLNK__1.eval[mm - 1] - _BLNK__1.eval[nn - 1] != (float)0.) {
	goto L51;
    } else {
	goto L50;
    }
L50:
    ++ll;
    d1 += _BLNK__1.prox[nn - 1];
    if (k - ii != 0) {
	goto L49;
    } else {
	goto L52;
    }
L51:
    if (ll - 1 != 0) {
	goto L52;
    } else {
	goto L54;
    }
L52:
    d1 /= (real) ll;
    kk = j;
    i__1 = ll;
    for (i__ = 1; i__ <= i__1; ++i__) {
	mm = _BLNK__1.indi[kk - 1];
	_BLNK__1.prox[mm - 1] = d1;
/* L53: */
	++kk;
    }
L54:
    j = k;
    if (j - ii != 0) {
	goto L48;
    } else {
	goto L55;
    }

/*    OBTAIN LINGOES ROSKAM INITIAL CONFIGURATION                        S
SA1      251*/

L55:
    kk = 0;
    i__1 = nrm1;
    for (i__ = 1; i__ <= i__1; ++i__) {
	ip1 = i__ + 1;
	i__2 = nr;
	for (j = ip1; j <= i__2; ++j) {
	    ++kk;
	    _BLNK__1.c__[i__ + j * 100 - 101] = _BLNK__1.prox[kk - 1];
/* L56: */
	    _BLNK__1.c__[j + i__ * 100 - 101] = _BLNK__1.c__[i__ + j * 100 - 
		    101];
	}
    }
    s = (float)0.;
    i__2 = nr;
    for (i__ = 1; i__ <= i__2; ++i__) {
	_BLNK__1.eval[i__ - 1] = (float)0.;
	i__1 = nr;
	for (j = 1; j <= i__1; ++j) {
/* L57: */
	    _BLNK__1.eval[i__ - 1] += _BLNK__1.c__[i__ + j * 100 - 101];
	}
	_BLNK__1.eval[i__ - 1] /= fnr;
/* L58: */
	s += _BLNK__1.eval[i__ - 1];
    }
    s /= fnr;
    i__2 = nr;
    for (i__ = 1; i__ <= i__2; ++i__) {
	i__1 = nr;
	for (j = i__; j <= i__1; ++j) {
	    _BLNK__1.c__[i__ + j * 100 - 101] = _BLNK__1.eval[i__ - 1] + 
		    _BLNK__1.eval[j - 1] - s - _BLNK__1.c__[i__ + j * 100 - 
		    101];
/* L59: */
	    _BLNK__1.c__[j + i__ * 100 - 101] = _BLNK__1.c__[i__ + j * 100 - 
		    101];
	}
    }

/*    OBTAIN PRININCIPAL AXES SOLUTION                                   S
SA1      271*/

    eigen_(_BLNK__1.c__, _BLNK__1.rho, &nr, _BLNK__1.phi, &maxd, &md, 
	    _BLNK__1.x, &_BLNK__1.x[303], &_BLNK__1.x[404], &_BLNK__1.x[505], 
	    &_BLNK__1.x[606], &_BLNK__1.x[707], &c__1);
    i__1 = maxd;
    for (j = 1; j <= i__1; ++j) {
	i__2 = nr;
	for (i__ = 1; i__ <= i__2; ++i__) {
/* L60: */
	    _BLNK__1.x[i__ + j * 101 - 102] = _BLNK__1.rho[i__ + j * 100 - 
		    101];
	}
    }
    i__2 = maxd;
    for (j = 1; j <= i__2; ++j) {
	s = (float)0.;
	i__1 = nr;
	for (i__ = 1; i__ <= i__1; ++i__) {
/* L61: */
	    s += _BLNK__1.x[i__ + j * 101 - 102];
	}
	s /= fnr;
	i__1 = nr;
	for (k = 1; k <= i__1; ++k) {
/* L62: */
	    _BLNK__1.x[k + j * 101 - 102] -= s;
	}
    }
    dim = (real) (jj + 1);
L63:
    nel = jj;

/*    SORT COMPARABLES ONLY                                              S
SA1      286*/

    sort_(&isw, &nel, _BLNK__1.r__, &isim, _BLNK__1.indi, &md);

/*    INDEX TEIES AMONG COMPARABLES                                      S
SA1      288*/

    _BLNK__1.noties[0] = 0;
    j = 1;
    ii = -1;
    k = j;
    nt = 0;
L64:
    ll = 1;
    mm = _BLNK__1.indi[j - 1];
L65:
    ++k;
    nn = _BLNK__1.indi[k - 1];
    if (_BLNK__1.r__[mm - 1] - _BLNK__1.r__[nn - 1] != (float)0.) {
	goto L67;
    } else {
	goto L66;
    }
L66:
    ++ll;
    if (k - nel != 0) {
	goto L65;
    } else {
	goto L68;
    }
L67:
    if (ll - 1 != 0) {
	goto L68;
    } else {
	goto L69;
    }
L68:
    ii += 2;
    nt += 2;

/*    *** IF NOTIES IS RE-DIMENSIONED ALSO ADJEUST FOLLOWING TEST        S
SA1      304*/

    if (nt > 1000) {
	goto L321;
    }
    _BLNK__1.noties[ii - 1] = ll;
L69:
    j = k;
    if (j - nel != 0) {
	goto L64;
    } else {
	goto L70;
    }
L70:
    if (ifconf != 0) {
	goto L111;
    } else {
	goto L103;
    }
L71:
    if (ifg >= 0) {
	goto L82;
    }

/*    TIE INPUT FOR LOCAL NMONOTONICITY CLUSTERING                       S
SA1      311*/

    kk = 0;
    dim = (float)0.;
    i__1 = nrm1;
    for (i__ = 1; i__ <= i__1; ++i__) {
	ip1 = i__ + 1;
	i__2 = nr;
	for (j = ip1; j <= i__2; ++j) {
	    switch ((int)igo) {
		case 1:  goto L72;
		case 2:  goto L73;
	    }
L72:
	    if (_BLNK__1.c__[j + i__ * 100 - 101] - cut >= (float)0.) {
		goto L74;
	    } else {
		goto L75;
	    }
L73:
	    if (cut - _BLNK__1.c__[j - 1] >= (float)0.) {
		goto L74;
	    } else {
		goto L75;
	    }
L74:
	    dim += _BLNK__1.c__[j + i__ * 100 - 101];
	    ++kk;
L75:
	    ;
	}
    }
    if (kk > 1) {
	goto L76;
    }
    ifg = 0;
    goto L82;
L76:
    fnt = (real) kk;
    dim /= fnt;
    i__2 = nrm1;
    for (i__ = 1; i__ <= i__2; ++i__) {
	ip1 = i__ + 1;
	i__1 = nr;
	for (j = ip1; j <= i__1; ++j) {
	    switch ((int)igo) {
		case 1:  goto L77;
		case 2:  goto L78;
	    }
L77:
	    if (_BLNK__1.c__[j + i__ * 100 - 101] - cut >= (float)0.) {
		goto L79;
	    } else {
		goto L80;
	    }
L78:
	    if (cut - _BLNK__1.c__[j + i__ * 100 - 101] >= (float)0.) {
		goto L79;
	    } else {
		goto L80;
	    }
L79:
	    _BLNK__1.c__[j + i__ * 100 - 101] = dim;
L80:
	    ;
	}
    }
    if (ifg == -2) {
	goto L81;
    }
    ij = kk + 1;
    goto L82;
L81:
    ij = 1;
    ji = kk;
L82:
    i__1 = nrm1;
    for (i__ = 1; i__ <= i__1; ++i__) {
	ip1 = i__ + 1;
	_BLNK__1.c__[i__ + i__ * 100 - 101] = (float)0.;
	i__2 = nr;
	for (j = ip1; j <= i__2; ++j) {
	    ++jj;
	    _BLNK__1.prox[jj - 1] = _BLNK__1.c__[j + i__ * 100 - 101];
/* L83: */
	    _BLNK__1.eval[jj - 1] = _BLNK__1.prox[jj - 1];
	}
    }
    _BLNK__1.c__[nr + nr * 100 - 101] = (float)0.;
    nel = jj;
    lfact = mind;

/*    PRINT OUT INPUT COEFFICIENTS                                       S
SA1      351*/

    if (ifd == 0) {
	goto L7448;
    }
    i__2 = nr;
    for (j = 1; j <= i__2; ++j) {
	i__1 = nr;
	for (i__ = 1; i__ <= i__1; ++i__) {
/* L7500: */
	    _BLNK__1.rho1[i__ + j * 100 - 101] = _BLNK__1.c__[i__ + j * 100 - 
		    101];
	}
    }
    mxout_(_BLNK__1.rho1, &nr, &c__0, &md);
L7448:

/*    SOR T SUBSCRIPTS OF INDI ACCORDING TO BSLUES IN DIST               S
SA1      358*/
/*    AND DIRECTION OF ISIM                                              S
SA1      359*/

L84:
    if (iffix == 1 && miss == 1) {
	isw = 0;
    }
    sort_(&isw, &nel, _BLNK__1.prox, &isim, _BLNK__1.indi, &kd);
    if (ifconf != 0) {
	goto L87;
    } else {
	goto L85;
    }

/*    SUBSTITUTE RANKS FOR SIMILARITIES/DISSIMILARITIES                  S
SA1      363*/

L85:
    s = (float)0.;
    i__1 = nel;
    for (j = 1; j <= i__1; ++j) {
	mm = _BLNK__1.indi[j - 1];
	s += (float)1.;
/* L86: */
	_BLNK__1.prox[mm - 1] = s;
    }

/*    CHECK FOR TIES IN INPUT VALUES                                     S
SA1      369*/

L87:
    _BLNK__1.noties[0] = 0;
/*    IF (NEL.EQ.0) WRITE (3,343) ID                                     S
SA1      371*/
    if (nel == 0) {
	goto L9;
    }
/* L343: */
/*    IF (NEL.EQ.1) WRITE (3,344) ID                                     S
SA1      374*/
    if (nel == 1) {
	goto L9;
    }
/* L344: */
    j = 1;
    ii = -1;
    k = j;
    nt = 0;
L88:
    ll = 1;
    mm = _BLNK__1.indi[j - 1];
    d1 = _BLNK__1.prox[mm - 1];
L89:
    ++k;
    nn = _BLNK__1.indi[k - 1];
    if (_BLNK__1.eval[mm - 1] - _BLNK__1.eval[nn - 1] != (float)0.) {
	goto L91;
    } else {
	goto L90;
    }
L90:
    ++ll;
    d1 += _BLNK__1.prox[nn - 1];
    if (k - nel != 0) {
	goto L89;
    } else {
	goto L92;
    }
L91:
    if (ll - 1 != 0) {
	goto L92;
    } else {
	goto L95;
    }
L92:
    ii += 2;
    nt += 2;

/*    **** IF NOTIES IS RE-DIMENSIONED ,ALSO ADJEUST FOLLOWING TEST      S
SA1      393*/

    if (nt > 1000) {
	goto L321;
    }
    _BLNK__1.noties[ii - 1] = ll;
    _BLNK__1.noties[ii] = j;
    if (ifconf != 0) {
	goto L95;
    } else {
	goto L93;
    }
L93:
    d1 /= (real) ll;
    jj = j;
    i__1 = ll;
    for (kk = 1; kk <= i__1; ++kk) {
	mm = _BLNK__1.indi[jj - 1];
	_BLNK__1.prox[mm - 1] = d1;
/* L94: */
	++jj;
    }
L95:
    j = k;
    if (j - nel != 0) {
	goto L88;
    } else {
	goto L96;
    }
L96:
    if (ifconf != 0) {
	goto L111;
    } else {
	goto L97;
    }

/*    FORM C-MATRIX BASED ON RANKS                                       S
SA1      407*/

L97:
    fnel = (real) nel;
    ii = 0;
    i__1 = nrm1;
    for (i__ = 1; i__ <= i__1; ++i__) {
	ip1 = i__ + 1;
	i__2 = nr;
	for (j = ip1; j <= i__2; ++j) {
	    ++ii;
	    _BLNK__1.rho1[i__ + j * 100 - 101] = (float)1. - _BLNK__1.prox[ii 
		    - 1] / fnel;
/* L98: */
	    _BLNK__1.rho1[j + i__ * 100 - 101] = _BLNK__1.rho1[i__ + j * 100 
		    - 101];
	}
    }
    i__2 = nr;
    for (i__ = 1; i__ <= i__2; ++i__) {
	_BLNK__1.rho1[i__ + i__ * 100 - 101] = fnr;
	i__1 = nr;
	for (j = 1; j <= i__1; ++j) {
	    if (i__ - j != 0) {
		goto L99;
	    } else {
		goto L100;
	    }
L99:
	    _BLNK__1.rho1[i__ + i__ * 100 - 101] -= _BLNK__1.rho1[i__ + j * 
		    100 - 101];
L100:
	    ;
	}
/* L101: */
    }

/*    OBTAIN INITIAL CONFIGURATION                                       S
SA1      423*/

    kk = maxd + 1;
    i__2 = nr;
    for (j = 1; j <= i__2; ++j) {
	i__1 = nr;
	for (i__ = 1; i__ <= i__1; ++i__) {
/* L7501: */
	    _BLNK__1.c__[i__ + j * 100 - 101] = _BLNK__1.rho1[i__ + j * 100 - 
		    101];
	}
    }
    eigen_(_BLNK__1.c__, _BLNK__1.rho, &nr, _BLNK__1.phi, &kk, &md, 
	    _BLNK__1.x, &_BLNK__1.x[303], &_BLNK__1.x[404], &_BLNK__1.x[505], 
	    &_BLNK__1.x[606], &_BLNK__1.x[707], &c__1);
    i__1 = kk;
    for (j = 2; j <= i__1; ++j) {
	_BLNK__1.phi[j - 2] = _BLNK__1.phi[j - 1];
	i__2 = nr;
	for (i__ = 1; i__ <= i__2; ++i__) {
/* L102: */
	    _BLNK__1.x[i__ + (j - 1) * 101 - 102] = _BLNK__1.rho[i__ + j * 
		    100 - 101];
	}
    }

/*    DETERMINE WHETHER TO GO UP OR DOWN                                 S
SA1      434*/

    dim = fnr;
L103:
    if (mind != 0) {
	goto L111;
    } else {
	goto L104;
    }
L104:
    dim /= (float)2.;
    kk = 0;
    i__2 = maxd;
    for (j = 1; j <= i__2; ++j) {
	if (_BLNK__1.phi[j - 1] - dim >= (float)0.) {
	    goto L105;
	} else {
	    goto L107;
	}
L105:
	++kk;
/* L106: */
    }
L107:
    if (kk != 0) {
	goto L109;
    } else {
	goto L108;
    }
L108:
    maxd = 1;
    goto L110;
L109:
    maxd = kk;
L110:
    mind = maxd;
    iter = 75;
L111:
    m = mind - 1;
    nelp1 = nel + 1;
    if (lr == 1) {
	miss = 0;
    }
    if (ifg <= 0) {
	goto L112;
    }
    if (ife + iffix + miss > 0) {
	ifg = 0;
    }
L112:
    ++miss;
    if (ifg == -1) {
	ji = nel;
    }
    if (iffix == 0) {
	goto L114;
    }
    i__2 = maxd;
    for (j = 1; j <= i__2; ++j) {
	ipt1 = _BLNK__1.indi[0];
/* L113: */
	_BLNK__1.x[ld + j * 101 - 102] = _BLNK__1.x[ipt1 + j * 101 - 102];
    }

/*    INITIALIZATION                                                     S
SA1      460*/

L114:
    ++m;
    n1 = 0;
    n1_fmt = fmt_155;
    if (m == 1) {
	nit = 1;
    }
L115:
    strlst = (float)1.;
    avst = (float)0.;
    ifbu = 0;
    nn = 0;
    ii = 5;
L116:
    itct = 0;
L117:
    ++nn;

/*    CALCULATE DISTANCES                                                S
SA1      471*/

L118:
    if (iffix != 0) {
	goto L119;
    } else {
	goto L132;
    }
L119:
    switch ((int)miss) {
	case 1:  goto L126;
	case 2:  goto L120;
    }
L120:
    jj = 0;
    if (ife == 0 || m == 1) {
	goto L123;
    }
    i__2 = nr;
    for (i__ = 1; i__ <= i__2; ++i__) {
	if (_BLNK__1.isten[i__ - 1] == 1) {
	    goto L122;
	}
	++jj;
	_BLNK__1.dist[jj - 1] = (float)0.;
	i__1 = m;
	for (j = 1; j <= i__1; ++j) {
/* L121: */
	    _BLNK__1.dist[jj - 1] += (r__1 = _BLNK__1.x[i__ + j * 101 - 102] 
		    - _BLNK__1.x[ld + j * 101 - 102], dabs(r__1));
	}
	if (_BLNK__1.dist[jj - 1] < zero) {
	    _BLNK__1.dist[jj - 1] = zero;
	}
L122:
	;
    }
    goto L153;
L123:
    i__2 = nr;
    for (i__ = 1; i__ <= i__2; ++i__) {
	if (_BLNK__1.isten[i__ - 1] == 1) {
	    goto L125;
	}
	++jj;
	_BLNK__1.dist[jj - 1] = (float)0.;
	i__1 = m;
	for (j = 1; j <= i__1; ++j) {
/* L124: */
/* Computing 2nd power */
	    r__1 = _BLNK__1.x[i__ + j * 101 - 102] - _BLNK__1.x[ld + j * 101 
		    - 102];
	    _BLNK__1.dist[jj - 1] += r__1 * r__1;
	}
	_BLNK__1.dist[jj - 1] = sqrt(_BLNK__1.dist[jj - 1]);
	if (_BLNK__1.dist[jj - 1] < zero) {
	    _BLNK__1.dist[jj - 1] = zero;
	}
L125:
	;
    }
    goto L153;
L126:
    if (ife == 0 || m == 1) {
	goto L129;
    }
    i__2 = nr;
    for (i__ = 1; i__ <= i__2; ++i__) {
	_BLNK__1.dist[i__ - 1] = (float)0.;
	i__1 = m;
	for (j = 1; j <= i__1; ++j) {
/* L127: */
	    _BLNK__1.dist[i__ - 1] += (r__1 = _BLNK__1.x[i__ + j * 101 - 102] 
		    - _BLNK__1.x[ld + j * 101 - 102], dabs(r__1));
	}
	if (_BLNK__1.dist[i__ - 1] < zero) {
	    _BLNK__1.dist[i__ - 1] = zero;
	}
/* L128: */
    }
    goto L153;
L129:
    i__2 = nr;
    for (i__ = 1; i__ <= i__2; ++i__) {
	_BLNK__1.dist[i__ - 1] = (float)0.;
	i__1 = m;
	for (j = 1; j <= i__1; ++j) {
/* L130: */
/* Computing 2nd power */
	    r__1 = _BLNK__1.x[i__ + j * 101 - 102] - _BLNK__1.x[ld + j * 101 
		    - 102];
	    _BLNK__1.dist[i__ - 1] += r__1 * r__1;
	}
	_BLNK__1.dist[i__ - 1] = sqrt(_BLNK__1.dist[i__ - 1]);
	if (_BLNK__1.dist[i__ - 1] < zero) {
	    _BLNK__1.dist[i__ - 1] = zero;
	}
/* L131: */
    }
    goto L153;
L132:
    if (itct != 0) {
	goto L139;
    }
    if (ifg != 1 || ife == 0) {
	goto L136;
    }

/*    CENTER COORDINATES                                                 S
SA1      514*/

    i__2 = m;
    for (j = 1; j <= i__2; ++j) {
	s = (float)0.;
	i__1 = nr;
	for (i__ = 1; i__ <= i__1; ++i__) {
/* L133: */
	    s += _BLNK__1.x[i__ + j * 101 - 102];
	}
	s /= fnr;
	i__1 = nr;
	for (i__ = 1; i__ <= i__1; ++i__) {
/* L134: */
	    _BLNK__1.x[i__ + j * 101 - 102] -= s;
	}
/* L135: */
    }

/*    NORMALIZE COORDINATES                                              S
SA1      523*/

L136:
    s = (float)0.;
    i__2 = m;
    for (j = 1; j <= i__2; ++j) {
	i__1 = nr;
	for (i__ = 1; i__ <= i__1; ++i__) {
/* L137: */
/* Computing 2nd power */
	    r__1 = _BLNK__1.x[i__ + j * 101 - 102];
	    s += r__1 * r__1;
	}
    }
    s = sqrt(s);
    i__1 = m;
    for (j = 1; j <= i__1; ++j) {
	i__2 = nr;
	for (i__ = 1; i__ <= i__2; ++i__) {
/* L138: */
	    _BLNK__1.x[i__ + j * 101 - 102] /= s;
	}
    }
L139:
    jj = 0;
    switch ((int)miss) {
	case 1:  goto L146;
	case 2:  goto L140;
    }
L140:
    kk = 0;
    if (ife == 0 || m == 1) {
	goto L143;
    }
    i__2 = nrm1;
    for (i__ = 1; i__ <= i__2; ++i__) {
	ip1 = i__ + 1;
	i__1 = nr;
	for (j = ip1; j <= i__1; ++j) {
	    ++jj;
	    if (_BLNK__1.isten[jj - 1] == 1) {
		goto L142;
	    }
	    ++kk;
	    _BLNK__1.dist[kk - 1] = (float)0.;
	    i__3 = m;
	    for (k = 1; k <= i__3; ++k) {
/* L141: */
		_BLNK__1.dist[kk - 1] += (r__1 = _BLNK__1.x[i__ + k * 101 - 
			102] - _BLNK__1.x[j + k * 101 - 102], dabs(r__1));
	    }
	    if (_BLNK__1.dist[kk - 1] < zero) {
		_BLNK__1.dist[kk - 1] = zero;
	    }
L142:
	    ;
	}
    }
    goto L152;
L143:
    i__1 = nrm1;
    for (i__ = 1; i__ <= i__1; ++i__) {
	ip1 = i__ + 1;
	i__2 = nr;
	for (j = ip1; j <= i__2; ++j) {
	    ++jj;
	    if (_BLNK__1.isten[jj - 1] == 1) {
		goto L145;
	    }
	    ++kk;
	    _BLNK__1.dist[kk - 1] = (float)0.;
	    i__3 = m;
	    for (k = 1; k <= i__3; ++k) {
/* L144: */
/* Computing 2nd power */
		r__1 = _BLNK__1.x[i__ + k * 101 - 102] - _BLNK__1.x[j + k * 
			101 - 102];
		_BLNK__1.dist[kk - 1] += r__1 * r__1;
	    }
	    _BLNK__1.dist[kk - 1] = sqrt(_BLNK__1.dist[kk - 1]);
	    if (_BLNK__1.dist[kk - 1] < zero) {
		_BLNK__1.dist[kk - 1] = zero;
	    }
L145:
	    ;
	}
    }
    goto L152;
L146:
    if (ife == 0 || m == 1) {
	goto L149;
    }
    i__2 = nrm1;
    for (i__ = 1; i__ <= i__2; ++i__) {
	ip1 = i__ + 1;
	i__1 = nr;
	for (j = ip1; j <= i__1; ++j) {
	    ++jj;
	    _BLNK__1.dist[jj - 1] = (float)0.;
	    i__3 = m;
	    for (k = 1; k <= i__3; ++k) {
/* L147: */
		_BLNK__1.dist[jj - 1] += (r__1 = _BLNK__1.x[i__ + k * 101 - 
			102] - _BLNK__1.x[j + k * 101 - 102], dabs(r__1));
	    }
	    if (_BLNK__1.dist[jj - 1] < zero) {
		_BLNK__1.dist[jj - 1] = zero;
	    }
/* L148: */
	}
    }
    goto L152;
L149:
    i__1 = nrm1;
    for (i__ = 1; i__ <= i__1; ++i__) {
	ip1 = i__ + 1;
	i__2 = nr;
	for (j = ip1; j <= i__2; ++j) {
	    ++jj;
	    _BLNK__1.dist[jj - 1] = (float)0.;
	    i__3 = m;
	    for (k = 1; k <= i__3; ++k) {
/* L150: */
/* Computing 2nd power */
		r__1 = _BLNK__1.x[i__ + k * 101 - 102] - _BLNK__1.x[j + k * 
			101 - 102];
		_BLNK__1.dist[jj - 1] += r__1 * r__1;
	    }
	    _BLNK__1.dist[jj - 1] = sqrt(_BLNK__1.dist[jj - 1]);
	    if (_BLNK__1.dist[jj - 1] < zero) {
		_BLNK__1.dist[jj - 1] = zero;
	    }
/* L151: */
	}
    }
L152:
    if (ifk != 0) {
	goto L157;
    } else {
	goto L153;
    }
L153:
    if (itct != 0) {
	goto L193;
    } else {
	goto L154;
    }
L154:
    switch ((int)n1) {
	case 0: goto L155;
	case 1: goto L156;
    }

/*    SORT DISTANCES LOW TO HIGH                                         S
SA1      585*/

L155:
    sort_(&c__0, &nel, _BLNK__1.dist, &c__0, _BLNK__1.indj, &md);
    n1 = 1;
    n1_fmt = fmt_156;
    goto L157;
L156:
    sort_(&c__1, &nel, _BLNK__1.dist, &c__0, _BLNK__1.indj, &md);

/*    IF TIES EXIT IN INPUT,OPTIMALLY PERMUTE INDICES OF INDI TO         S
SA1      590*/

L157:
    if (_BLNK__1.noties[0] != 0) {
	goto L158;
    } else {
	goto L160;
    }
L158:
    i__2 = nt;
    for (j = 1; j <= i__2; j += 2) {
	kk = _BLNK__1.noties[j - 1];
	ll = _BLNK__1.noties[j];
	sort_(&c__1, &kk, _BLNK__1.dist, &c__0, &_BLNK__1.indi[ll - 1], &md);
/* L159: */
    }
L160:
    if (iffix != 0) {
	goto L163;
    } else {
	goto L161;
    }
L161:
    if (ifk != 0) {
	goto L162;
    } else {
	goto L163;
    }

/*    OBTAIN KRUSKAL'S MONOTONE REGRESSION BEST FIT VALUES               S
SA1      599*/

L162:
    fit_(&nel, _BLNK__1.dist, _BLNK__1.indi, &_BLNK__1.eval[nelp1 - 1], 
	    _BLNK__1.eval, _BLNK__1.indj, &kd);
    goto L165;

/*    CELL-WISE PERMUTE D TO D* (RANK IMAGES)                            S
SA1      602*/

L163:
    i__2 = nel;
    for (i__ = 1; i__ <= i__2; ++i__) {
	ipt1 = _BLNK__1.indi[i__ - 1];
	ipt2 = _BLNK__1.indj[i__ - 1];
/* L164: */
	_BLNK__1.eval[ipt1 - 1] = _BLNK__1.dist[ipt2 - 1];
    }

/*    CALCULATE NORMALIZED PHI/STRESS**2                                 S
SA1      607*/

L165:
    stress = (float)0.;
    d__ = (float)0.;
    d1 = (float)0.;
    s = (float)0.;
    i__2 = nel;
    for (i__ = 1; i__ <= i__2; ++i__) {
/* Computing 2nd power */
	r__1 = _BLNK__1.dist[i__ - 1];
	d1 += r__1 * r__1;
	d__ += _BLNK__1.dist[i__ - 1] * _BLNK__1.eval[i__ - 1];
	if (ifg != 1) {
	    goto L166;
	}
	dim = _BLNK__1.dist[i__ - 1] / _BLNK__1.eval[i__ - 1];
/* Computing 2nd power */
	r__1 = dim - (float)1.;
	stress += r__1 * r__1;
/* Computing 2nd power */
	r__1 = dim + (float)1.;
	s += r__1 * r__1;
L166:
	;
    }
    if (ifg != 1) {
	goto L167;
    }
    stress /= s;
    goto L168;
L167:
    stress = (float)1. - d__ / d1;
L168:
    if (ifg >= 0) {
	goto L171;
    }
    if (recalc == 0) {
	goto L171;
    }

/*    TIE LARGE/SMALL DISTANCES(SECONDARY APPROACH                       S
SA1      625*/

    s = (float)0.;
    i__2 = ji;
    for (j = ij; j <= i__2; ++j) {
	ipt1 = _BLNK__1.indi[j - 1];
/* L169: */
	s += _BLNK__1.eval[ipt1 - 1];
    }
    s /= fnt;
    i__2 = ji;
    for (j = ij; j <= i__2; ++j) {
	ipt1 = _BLNK__1.indi[j - 1];
/* L170: */
	_BLNK__1.eval[ipt1 - 1] = s;
    }
L171:
    if (stress > strlst) {
	goto L177;
    }
    if (recalc == 1) {
	goto L177;
    }

/*    SAVE BEST CONFIGURATION                                            S
SA1      635*/

    if (iffix != 0) {
	goto L172;
    } else {
	goto L174;
    }
L172:
    i__2 = maxd;
    for (j = 1; j <= i__2; ++j) {
/* L173: */
	_BLNK__1.x2[ld + j * 101 - 102] = _BLNK__1.x[ld + j * 101 - 102];
    }
    goto L176;
L174:
    i__2 = nr;
    for (i__ = 1; i__ <= i__2; ++i__) {
	i__1 = m;
	for (j = 1; j <= i__1; ++j) {
/* L175: */
	    _BLNK__1.x2[i__ + j * 101 - 102] = _BLNK__1.x[i__ + j * 101 - 102]
		    ;
	}
    }
L176:
    strlst = stress;
    dsv = d__;
    dsq = d1;

/*    TEST FOR TERMINATION                                               S
SA1      646*/

L177:
    if (stress < zero) {
	goto L251;
    }
    if (recalc == 1) {
	goto L251;
    }
    if (nn >= 6) {
	goto L178;
    }
    avst += stress;
    _BLNK__1.phi[nn - 1] = stress;
    goto L189;
L178:
    if (ii == 5) {
	ii = 0;
    }
    ++ii;
    if (ifk == 1 && stress * (float)5. / avst > (float).995) {
	goto L182;
    }
    if (stress <= _BLNK__1.phi[ii - 1]) {
	goto L186;
    }
    ++ifbu;

/*    RESTORE BEST CONFIGURATION                                         S
SA1      657*/

L179:
    if (iffix != 0) {
	goto L180;
    } else {
	goto L182;
    }
L180:
    i__1 = maxd;
    for (j = 1; j <= i__1; ++j) {
/* L181: */
	_BLNK__1.x[ld + j * 101 - 102] = _BLNK__1.x2[ld + j * 101 - 102];
    }
    goto L184;
L182:
    i__1 = nr;
    for (i__ = 1; i__ <= i__1; ++i__) {
	i__2 = m;
	for (j = 1; j <= i__2; ++j) {
/* L183: */
	    _BLNK__1.x[i__ + j * 101 - 102] = _BLNK__1.x2[i__ + j * 101 - 102]
		    ;
	}
    }
L184:
    stress = strlst;
    if (ifg == 1) {
	glp = stress;
    }
    d__ = dsv;
    d1 = dsq;
    if (ifbu == 2 || ifbu == 1 && nr > 9 && m != 1) {
	goto L251;
    }
    if (ifk != 0) {
	goto L251;
    } else {
	goto L185;
    }
L185:
    --ii;
    goto L116;
L186:
    if (nn - iter != 0) {
	goto L188;
    } else {
	goto L187;
    }
L187:
    ifbu = 2;
    goto L179;
L188:
    avst = avst - _BLNK__1.phi[ii - 1] + stress;
    _BLNK__1.phi[ii - 1] = stress;
L189:
    if (ifk != 0) {
	goto L193;
    } else {
	goto L190;
    }
L190:
    if (ifbu - 1 != 0) {
	goto L193;
    } else {
	goto L191;
    }

/*    SWITCH TO SINGLE PHASE ALGORITHM(RANK IMAGES                       S
SA1      680*/

L191:
    i__2 = nel;
    for (j = 1; j <= i__2; ++j) {
	ipt1 = _BLNK__1.indj[j - 1];
	ipt2 = _BLNK__1.indi[j - 1];
/* L192: */
	_BLNK__1.eval[ipt1 - 1] = (_BLNK__1.dist[ipt2 - 1] + _BLNK__1.eval[
		ipt1 - 1]) / (float)2.;
    }
L193:
    if (iffix != 0) {
	goto L194;
    } else {
	goto L213;
    }

/*    DETERMINE CORRECTIONS FOR ADDED VARIABLE                           S
SA1      686*/

L194:
    if (ife == 0 || m == 1) {
	goto L203;
    }
    i__2 = m;
    for (k = 1; k <= i__2; ++k) {
	jj = 0;
	i__1 = nr;
	for (i__ = 1; i__ <= i__1; ++i__) {
	    switch ((int)miss) {
		case 1:  goto L196;
		case 2:  goto L195;
	    }
L195:
	    if (_BLNK__1.isten[i__ - 1] == 1) {
		goto L198;
	    }
	    ++jj;
	    mm = jj;
	    goto L197;
L196:
	    mm = i__;
L197:
	    s = (r__1 = _BLNK__1.x[i__ + k * 101 - 102] - _BLNK__1.x[ld + k * 
		    101 - 102], dabs(r__1));
	    if (s < zero) {
		s = zero;
	    }
	    _BLNK__1.c__[i__ - 1] = _BLNK__1.eval[mm - 1] / s;
	    _BLNK__1.c__[i__ + 99] = _BLNK__1.dist[mm - 1] / s;
	    goto L199;
L198:
	    _BLNK__1.c__[i__ - 1] = (float)1.;
	    _BLNK__1.c__[i__ + 99] = (float)1.;
L199:
	    ;
	}
	s = (float)1.;
	d1 = (float)1.;
	i__1 = nr;
	for (i__ = 1; i__ <= i__1; ++i__) {
	    s += _BLNK__1.c__[i__ - 1];
	    d1 += _BLNK__1.c__[i__ + 99];
/* L200: */
	    _BLNK__1.x2[i__ + nd * 101 - 102] = _BLNK__1.c__[i__ + 99] - 
		    _BLNK__1.c__[i__ - 1];
	}

/*    MODIFY COORDINATE                                               
   SSA1      711*/

	_BLNK__1.x2[ld + k * 101 - 102] = (float)0.;
	i__1 = nr;
	for (i__ = 1; i__ <= i__1; ++i__) {
/* L201: */
	    _BLNK__1.x2[ld + k * 101 - 102] += _BLNK__1.x[i__ + k * 101 - 102]
		     * _BLNK__1.x2[i__ + nd * 101 - 102];
	}
/* L202: */
	_BLNK__1.x[ld + k * 101 - 102] = (_BLNK__1.x2[ld + k * 101 - 102] + 
		_BLNK__1.x[ld + k * 101 - 102] * s) / d1;
    }
    goto L249;
L203:
    switch ((int)miss) {
	case 1:  goto L207;
	case 2:  goto L204;
    }
L204:
    jj = 0;
    i__2 = nr;
    for (i__ = 1; i__ <= i__2; ++i__) {
	if (_BLNK__1.isten[i__ - 1] == 1) {
	    goto L205;
	}
	++jj;
	_BLNK__1.x2[i__ + nd * 101 - 102] = (float)1. - _BLNK__1.eval[jj - 1] 
		/ _BLNK__1.dist[jj - 1];
	goto L206;
L205:
	_BLNK__1.x2[i__ + nd * 101 - 102] = (float)0.;
L206:
	;
    }
    goto L209;
L207:
    i__2 = nr;
    for (i__ = 1; i__ <= i__2; ++i__) {
	_BLNK__1.x2[i__ + nd * 101 - 102] = (float)1. - _BLNK__1.eval[i__ - 1]
		 / _BLNK__1.dist[i__ - 1];
/* L208: */
    }
L209:
    _BLNK__1.x2[ld + nd * 101 - 102] = fnr;
    i__2 = nr;
    for (i__ = 1; i__ <= i__2; ++i__) {
/* L210: */
	_BLNK__1.x2[ld + nd * 101 - 102] -= _BLNK__1.x2[i__ + nd * 101 - 102];
    }

/*    MODIFY COORDINATES OF ADDED VARIABLE                               S
SA1      733*/

    i__2 = maxd;
    for (j = 1; j <= i__2; ++j) {
	_BLNK__1.x2[ld + j * 101 - 102] = (float)0.;
	i__1 = nr;
	for (i__ = 1; i__ <= i__1; ++i__) {
/* L211: */
	    _BLNK__1.x2[ld + j * 101 - 102] += _BLNK__1.x[i__ + j * 101 - 102]
		     * _BLNK__1.x2[i__ + nd * 101 - 102];
	}
	_BLNK__1.x2[ld + j * 101 - 102] = (_BLNK__1.x2[ld + j * 101 - 102] + 
		_BLNK__1.x[ld + j * 101 - 102] * _BLNK__1.x2[ld + nd * 101 - 
		102]) / fnr;
/* L212: */
	_BLNK__1.x[ld + j * 101 - 102] = _BLNK__1.x2[ld + j * 101 - 102];
    }
    goto L249;
L213:
    if (ife == 0 || m == 1) {
	goto L227;
    }

/*    CONSTRUCT C(I,J,A)                                                 S
SA1      742*/

    i__2 = m;
    for (k = 1; k <= i__2; ++k) {
	kk = 0;
	jj = 0;
	i__1 = nrm1;
	for (i__ = 1; i__ <= i__1; ++i__) {
	    ip1 = i__ + 1;
	    i__3 = nr;
	    for (j = ip1; j <= i__3; ++j) {
		++jj;
		switch ((int)miss) {
		    case 1:  goto L215;
		    case 2:  goto L214;
		}
L214:
		if (_BLNK__1.isten[jj - 1] == 1) {
		    goto L217;
		}
		++kk;
		mm = kk;
		goto L216;
L215:
		mm = jj;
L216:
		s = (r__1 = _BLNK__1.x[i__ + k * 101 - 102] - _BLNK__1.x[j + 
			k * 101 - 102], dabs(r__1));
		if (s < zero) {
		    s = zero;
		}
		_BLNK__1.c__[i__ + j * 100 - 101] = _BLNK__1.eval[mm - 1] / s;
		_BLNK__1.c__[j + i__ * 100 - 101] = _BLNK__1.dist[mm - 1] / s;
		goto L218;
L217:
		_BLNK__1.c__[i__ + j * 100 - 101] = (float)1.;
		_BLNK__1.c__[j + i__ * 100 - 101] = (float)1.;
L218:
		;
	    }
	}
	i__3 = nr;
	for (i__ = 1; i__ <= i__3; ++i__) {
	    _BLNK__1.c__[i__ + i__ * 100 - 101] = (float)1.;
	    _BLNK__1.x[i__ + nd * 101 - 102] = (float)1.;
	    i__1 = nr;
	    for (j = 1; j <= i__1; ++j) {
		if (i__ == j) {
		    goto L220;
		}
		if (i__ > j) {
		    goto L219;
		}
		_BLNK__1.x[i__ + nd * 101 - 102] += _BLNK__1.c__[i__ + j * 
			100 - 101];
		_BLNK__1.c__[i__ + i__ * 100 - 101] += _BLNK__1.c__[i__ + j * 
			100 - 101];
		goto L220;
L219:
		_BLNK__1.x[i__ + nd * 101 - 102] += _BLNK__1.c__[i__ + j * 
			100 - 101];
		_BLNK__1.c__[i__ + i__ * 100 - 101] += _BLNK__1.c__[j + i__ * 
			100 - 101];
L220:
		;
	    }
/* L221: */
	}
	i__3 = nrm1;
	for (i__ = 1; i__ <= i__3; ++i__) {
	    ip1 = i__ + 1;
	    i__1 = nr;
	    for (j = ip1; j <= i__1; ++j) {
		_BLNK__1.c__[i__ + j * 100 - 101] = _BLNK__1.c__[j + i__ * 
			100 - 101] - _BLNK__1.c__[i__ + j * 100 - 101];
/* L222: */
		_BLNK__1.c__[j + i__ * 100 - 101] = _BLNK__1.c__[i__ + j * 
			100 - 101];
	    }
	}

/*    AOOLY CORRECTIONS TO COORDINATES                                
   SSA1      782*/

	i__1 = nr;
	for (i__ = 1; i__ <= i__1; ++i__) {
	    _BLNK__1.x2[i__ + nd * 101 - 102] = (float)0.;
	    i__3 = nr;
	    for (j = 1; j <= i__3; ++j) {
/* L223: */
		_BLNK__1.x2[i__ + nd * 101 - 102] += _BLNK__1.x[j + k * 101 - 
			102] * _BLNK__1.c__[i__ + j * 100 - 101];
	    }
/* L224: */
	    _BLNK__1.x2[i__ + nd * 101 - 102] /= _BLNK__1.x[i__ + nd * 101 - 
		    102];
	}
	i__1 = nr;
	for (l = 1; l <= i__1; ++l) {
/* L225: */
	    _BLNK__1.x[l + k * 101 - 102] = _BLNK__1.x2[l + nd * 101 - 102];
	}
/* L226: */
    }
    goto L249;

/*    COMPUTE C-MATRIX                                                   S
SA1      792*/

L227:
    jj = 0;
    switch ((int)miss) {
	case 1:  goto L231;
	case 2:  goto L228;
    }
L228:
    kk = 0;
    i__2 = nrm1;
    for (i__ = 1; i__ <= i__2; ++i__) {
	ip1 = i__ + 1;
	i__1 = nr;
	for (j = ip1; j <= i__1; ++j) {
	    ++jj;
	    if (_BLNK__1.isten[jj - 1] == 1) {
		goto L229;
	    }
	    ++kk;
	    _BLNK__1.c__[i__ + j * 100 - 101] = (float)1. - _BLNK__1.eval[kk 
		    - 1] / _BLNK__1.dist[kk - 1];
	    goto L230;
L229:
	    _BLNK__1.c__[i__ + j * 100 - 101] = (float)0.;
L230:
	    _BLNK__1.c__[j + i__ * 100 - 101] = _BLNK__1.c__[i__ + j * 100 - 
		    101];
	}
    }
    goto L234;
L231:
    i__1 = nrm1;
    for (i__ = 1; i__ <= i__1; ++i__) {
	ip1 = i__ + 1;
	i__2 = nr;
	for (j = ip1; j <= i__2; ++j) {
	    ++jj;
	    _BLNK__1.c__[i__ + j * 100 - 101] = (float)1. - _BLNK__1.eval[jj 
		    - 1] / _BLNK__1.dist[jj - 1];
	    if (ifg != 1) {
		goto L232;
	    }
	    _BLNK__1.c__[i__ + j * 100 - 101] = (float)1. / _BLNK__1.dist[jj 
		    - 1];
	    _BLNK__1.c__[j + i__ * 100 - 101] = (float)1. / _BLNK__1.eval[jj 
		    - 1];
	    goto L233;
L232:
	    _BLNK__1.c__[j + i__ * 100 - 101] = _BLNK__1.c__[i__ + j * 100 - 
		    101];
L233:
	    ;
	}
    }
L234:
    if (ifg != 1) {
	goto L242;
    }

/*    PREPARE C-MATRIX FOR LOCAL MONOTONICITY                            S
SA1      819*/

    i__2 = nr;
    for (i__ = 1; i__ <= i__2; ++i__) {
	_BLNK__1.c__[i__ + i__ * 100 - 101] = (float)1.;
	_BLNK__1.x[i__ + nd * 101 - 102] = (float)1.;
	i__1 = nr;
	for (j = 1; j <= i__1; ++j) {
	    if (i__ == j) {
		goto L236;
	    }
	    if (i__ > j) {
		goto L235;
	    }
	    _BLNK__1.x[i__ + nd * 101 - 102] += _BLNK__1.c__[j + i__ * 100 - 
		    101];
	    _BLNK__1.c__[i__ + i__ * 100 - 101] += _BLNK__1.c__[i__ + j * 100 
		    - 101];
	    goto L236;
L235:
	    _BLNK__1.x[i__ + nd * 101 - 102] += _BLNK__1.c__[i__ + j * 100 - 
		    101];
	    _BLNK__1.c__[i__ + i__ * 100 - 101] += _BLNK__1.c__[j + i__ * 100 
		    - 101];
L236:
	    ;
	}
/* L237: */
    }
    i__2 = nrm1;
    for (i__ = 1; i__ <= i__2; ++i__) {
	ip1 = i__ + 1;
	i__1 = nr;
	for (j = ip1; j <= i__1; ++j) {
	    _BLNK__1.c__[i__ + j * 100 - 101] = _BLNK__1.c__[j + i__ * 100 - 
		    101] - _BLNK__1.c__[i__ + j * 100 - 101];
/* L238: */
	    _BLNK__1.c__[j + i__ * 100 - 101] = _BLNK__1.c__[i__ + j * 100 - 
		    101];
	}
    }

/*    APPLY CORE CTIONS TO X                                             S
SA1      838*/

    i__1 = m;
    for (k = 1; k <= i__1; ++k) {
	i__2 = nr;
	for (i__ = 1; i__ <= i__2; ++i__) {
	    _BLNK__1.x2[nd * 101 - 101] = (float)0.;
	    i__3 = nr;
	    for (j = 1; j <= i__3; ++j) {
/* L239: */
		_BLNK__1.x2[i__ + nd * 101 - 102] += _BLNK__1.x[j + k * 101 - 
			102] * _BLNK__1.c__[i__ + j * 100 - 101];
	    }
/* L240: */
	    _BLNK__1.x2[i__ + nd * 101 - 102] /= _BLNK__1.x[i__ + nd * 101 - 
		    102];
	}
	i__2 = nr;
	for (l = 1; l <= i__2; ++l) {
/* L241: */
	    _BLNK__1.x[l + k * 101 - 102] = _BLNK__1.x2[l + nd * 101 - 102];
	}
    }
    goto L249;
L242:
    i__2 = nr;
    for (i__ = 1; i__ <= i__2; ++i__) {
	_BLNK__1.c__[i__ + i__ * 100 - 101] = fnr;
	i__1 = nr;
	for (j = 1; j <= i__1; ++j) {
	    if (i__ - j != 0) {
		goto L243;
	    } else {
		goto L244;
	    }
L243:
	    _BLNK__1.c__[i__ + i__ * 100 - 101] -= _BLNK__1.c__[i__ + j * 100 
		    - 101];
L244:
	    ;
	}
/* L245: */
    }
    i__2 = m;
    for (k = 1; k <= i__2; ++k) {
	i__1 = nr;
	for (i__ = 1; i__ <= i__1; ++i__) {
	    _BLNK__1.x2[i__ + nd * 101 - 102] = (float)0.;
	    i__3 = nr;
	    for (j = 1; j <= i__3; ++j) {
/* L246: */
		_BLNK__1.x2[i__ + nd * 101 - 102] += _BLNK__1.x[j + k * 101 - 
			102] * _BLNK__1.c__[i__ + j * 100 - 101];
	    }
/* L247: */
	    _BLNK__1.x2[i__ + nd * 101 - 102] /= fnr;
	}
	i__1 = nr;
	for (l = 1; l <= i__1; ++l) {
/* L248: */
	    _BLNK__1.x[l + k * 101 - 102] = _BLNK__1.x2[l + nd * 101 - 102];
	}
    }
L249:
    if (ifk + ifbu != 0) {
	goto L117;
    } else {
	goto L250;
    }

/*    TEST FOR TERMINATION OF PHASE1 ITERATIONS                          S
SA1      864*/

L250:
    ++itct;
    if (nit - itct != 0) {
	goto L118;
    } else {
	goto L116;
    }
/*    COMPUTE FINAL DISTANCES                                            S
SA1      867*/
L251:
    if (iffix != 0) {
	goto L322;
    } else {
	goto L252;
    }
L252:
    if (recalc == 0) {
	s_wsfe(&io___87);
	e_wsfe();
    }
    jj = 0;
    switch ((int)miss) {
	case 1:  goto L259;
	case 2:  goto L253;
    }
L253:
    kk = 0;
    i__1 = nrm1;
    for (i__ = 1; i__ <= i__1; ++i__) {
	ip1 = i__ + 1;
	i__2 = nr;
	for (j = ip1; j <= i__2; ++j) {
	    ++jj;
	    if (_BLNK__1.isten[jj - 1] == 1) {
		goto L258;
	    }
	    ++kk;
	    _BLNK__1.dist[kk - 1] = (float)0.;
	    i__3 = m;
	    for (k = 1; k <= i__3; ++k) {
		if (ife != 0) {
		    goto L254;
		} else {
		    goto L255;
		}
L254:
		_BLNK__1.dist[kk - 1] += (r__1 = _BLNK__1.x[i__ + k * 101 - 
			102] - _BLNK__1.x[j + k * 101 - 102], dabs(r__1));
		goto L256;
L255:
/* Computing 2nd power */
		r__1 = _BLNK__1.x[i__ + k * 101 - 102] - _BLNK__1.x[j + k * 
			101 - 102];
		_BLNK__1.dist[kk - 1] += r__1 * r__1;
L256:
		;
	    }
	    if (ife != 0) {
		goto L258;
	    } else {
		goto L257;
	    }
L257:
	    _BLNK__1.dist[kk - 1] = sqrt(_BLNK__1.dist[kk - 1]);
L258:
	    ;
	}
    }
    goto L265;
L259:
    i__2 = nrm1;
    for (i__ = 1; i__ <= i__2; ++i__) {
	ip1 = i__ + 1;
	i__1 = nr;
	for (j = ip1; j <= i__1; ++j) {
	    ++jj;
	    _BLNK__1.dist[jj - 1] = (float)0.;
	    i__3 = m;
	    for (k = 1; k <= i__3; ++k) {
		if (ife != 0) {
		    goto L260;
		} else {
		    goto L261;
		}
L260:
		_BLNK__1.dist[jj - 1] += (r__1 = _BLNK__1.x[i__ + k * 101 - 
			102] - _BLNK__1.x[j + k * 101 - 102], dabs(r__1));
		goto L262;
L261:
/* Computing 2nd power */
		r__1 = _BLNK__1.x[i__ + k * 101 - 102] - _BLNK__1.x[j + k * 
			101 - 102];
		_BLNK__1.dist[jj - 1] += r__1 * r__1;
L262:
		;
	    }
	    if (ife != 0) {
		goto L264;
	    } else {
		goto L263;
	    }
L263:
	    _BLNK__1.dist[jj - 1] = sqrt(_BLNK__1.dist[jj - 1]);
L264:
	    ;
	}
    }
L265:
    if (ifk != 0) {
	goto L270;
    } else {
	goto L266;
    }
L266:
    if (_BLNK__1.noties[0] != 0) {
	goto L267;
    } else {
	goto L269;
    }
L267:
    i__1 = nt;
    for (j = 1; j <= i__1; j += 2) {
	kk = _BLNK__1.noties[j - 1];
	ll = _BLNK__1.noties[j];
	sort_(&c__1, &kk, _BLNK__1.dist, &c__0, &_BLNK__1.indi[ll - 1], &md);
/* L268: */
    }
L269:
    fit_(&nel, _BLNK__1.dist, _BLNK__1.indi, &_BLNK__1.eval[nelp1 - 1], 
	    _BLNK__1.eval, _BLNK__1.indj, &kd);
    goto L275;
L270:
    sort_(&c__0, &nel, _BLNK__1.dist, &c__0, _BLNK__1.indj, &md);
    if (_BLNK__1.noties[0] != 0) {
	goto L271;
    } else {
	goto L273;
    }
L271:
    i__1 = nt;
    for (j = 1; j <= i__1; j += 2) {
	kk = _BLNK__1.noties[j - 1];
	ll = _BLNK__1.noties[j];
	sort_(&c__1, &kk, _BLNK__1.dist, &c__0, &_BLNK__1.indi[ll - 1], &md);
/* L272: */
    }
L273:
    i__1 = nel;
    for (j = 1; j <= i__1; ++j) {
	ipt1 = _BLNK__1.indi[j - 1];
	ipt2 = _BLNK__1.indj[j - 1];
/* L274: */
	_BLNK__1.eval[ipt1 - 1] = _BLNK__1.dist[ipt2 - 1];
    }
L275:
    s = (float)0.;
    i__1 = nel;
    for (j = 1; j <= i__1; ++j) {
/* L276: */
	s += _BLNK__1.dist[j - 1] * _BLNK__1.eval[j - 1];
    }
    if (ifk != 0) {
	goto L278;
    } else {
	goto L277;
    }
L277:
    s = sqrt((float)1. - s / d1);
    if (ifg == 1) {
	stress = (float)1. - d__ / d1;
    }
/* Computing 2nd power */
    r__1 = (float)1. - stress;
    stress = sqrt((float)1. - r__1 * r__1);
    if (recalc == 0) {
	s_wsfe(&io___88);
	do_fio(&c__1, (char *)&m, (ftnlen)sizeof(integer));
	i__1 = m;
	for (mm = 1; mm <= i__1; ++mm) {
	    do_fio(&c__1, (char *)&mm, (ftnlen)sizeof(integer));
	}
	e_wsfe();
    }
    goto L279;
L278:
/* Computing 2nd power */
    r__1 = s / d1;
    s = sqrt((float)1. - r__1 * r__1);
    if (ifg == 1) {
	stress = (float)1. - d__ / d1;
    }
    stress = sqrt(stress);
    if (recalc == 0) {
	s_wsfe(&io___89);
	do_fio(&c__1, (char *)&m, (ftnlen)sizeof(integer));
	i__1 = m;
	for (mm = 1; mm <= i__1; ++mm) {
	    do_fio(&c__1, (char *)&mm, (ftnlen)sizeof(integer));
	}
	e_wsfe();
    }
L279:
    if (recalc == 0) {
	s_wsfe(&io___90);
	e_wsfe();
    }
    if (m == 1 || ife == 1) {
	goto L284;
    }

/*    PERFORM PRINCIPAL AXIS ROTATION                                    S
SA1      939*/

    i__1 = m;
    for (i__ = 1; i__ <= i__1; ++i__) {
	i__2 = m;
	for (j = i__; j <= i__2; ++j) {
	    _BLNK__1.c__[i__ + j * 100 - 101] = (float)0.;
	    i__3 = nr;
	    for (k = 1; k <= i__3; ++k) {
/* L280: */
		_BLNK__1.c__[i__ + j * 100 - 101] += _BLNK__1.x[k + i__ * 101 
			- 102] * _BLNK__1.x[k + j * 101 - 102];
	    }
/* L281: */
	    _BLNK__1.c__[j + i__ * 100 - 101] = _BLNK__1.c__[i__ + j * 100 - 
		    101];
	}
    }

/*    CALL HOUSEHOLDER SUBROUTINE                                        S
SA1      946*/

    eigen_(_BLNK__1.c__, _BLNK__1.rho, &m, _BLNK__1.phi, &m, &md, _BLNK__1.x2,
	     &_BLNK__1.x2[303], &_BLNK__1.x2[404], &_BLNK__1.x2[505], &
	    _BLNK__1.x2[606], &_BLNK__1.x2[707], &c__0);
    i__2 = nr;
    for (i__ = 1; i__ <= i__2; ++i__) {
	i__1 = m;
	for (j = 1; j <= i__1; ++j) {
	    _BLNK__1.x2[i__ + j * 101 - 102] = (float)0.;
	    i__3 = m;
	    for (k = 1; k <= i__3; ++k) {
/* L282: */
		_BLNK__1.x2[i__ + j * 101 - 102] += _BLNK__1.x[i__ + k * 101 
			- 102] * _BLNK__1.rho[k + j * 100 - 101];
	    }
	}
	i__3 = m;
	for (l = 1; l <= i__3; ++l) {
/* L283: */
	    _BLNK__1.x[i__ + l * 101 - 102] = _BLNK__1.x2[i__ + l * 101 - 102]
		    ;
	}
    }
L284:
    dim = (float)0.;
    i__3 = m;
    for (k = 1; k <= i__3; ++k) {
	_BLNK__1.rho[k * 100 - 100] = (float)0.;
	_BLNK__1.rho[k * 100 - 99] = (float)0.;
	i__2 = nr;
	for (i__ = 1; i__ <= i__2; ++i__) {
/* Computing MIN */
	    r__1 = _BLNK__1.rho[k * 100 - 100], r__2 = _BLNK__1.x[i__ + k * 
		    101 - 102];
	    _BLNK__1.rho[k * 100 - 100] = dmin(r__1,r__2);
/* L285: */
/* Computing MAX */
	    r__1 = _BLNK__1.rho[k * 100 - 99], r__2 = _BLNK__1.x[i__ + k * 
		    101 - 102];
	    _BLNK__1.rho[k * 100 - 99] = dmax(r__1,r__2);
	}
/* L286: */
/* Computing MAX */
	r__1 = dim, r__2 = _BLNK__1.rho[k * 100 - 99] - _BLNK__1.rho[k * 100 
		- 100];
	dim = dmax(r__1,r__2);
    }
    dim = (float)2. / dim;
    i__3 = m;
    for (k = 1; k <= i__3; ++k) {
	i__2 = nr;
	for (i__ = 1; i__ <= i__2; ++i__) {
/* L287: */
	    _BLNK__1.x2[i__ + k * 101 - 102] = dim * (_BLNK__1.x[i__ + k * 
		    101 - 102] - _BLNK__1.rho[k * 100 - 100]) - (float)1.;
	}
    }

/*    COMPUTE CENTRALITY INDEX,I.E. DISTANCE FROM TRUE ORIGIN            S
SA1      968*/

    mm = m + 1;
    i__2 = nr;
    for (i__ = 1; i__ <= i__2; ++i__) {
	_BLNK__1.x2[i__ + mm * 101 - 102] = (float)0.;
	i__3 = m;
	for (k = 1; k <= i__3; ++k) {
/* L288: */
/* Computing 2nd power */
	    r__1 = _BLNK__1.x[i__ + k * 101 - 102];
	    _BLNK__1.x2[i__ + mm * 101 - 102] += r__1 * r__1;
	}
	_BLNK__1.x2[i__ + mm * 101 - 102] = dim * sqrt(_BLNK__1.x2[i__ + mm * 
		101 - 102]);
/* L289: */
    }

/*    PRINT OUT FINAL CONFIGURATION                                      S
SA1      980*/

    i__2 = nr;
    for (i__ = 1; i__ <= i__2; ++i__) {
/* L999: */
	if (recalc == 0) {
	    s_wsfe(&io___91);
	    do_fio(&c__1, (char *)&i__, (ftnlen)sizeof(integer));
	    do_fio(&c__1, (char *)&_BLNK__1.x2[i__ + mm * 101 - 102], (ftnlen)
		    sizeof(real));
	    i__3 = m;
	    for (k = 1; k <= i__3; ++k) {
		do_fio(&c__1, (char *)&_BLNK__1.x2[i__ + k * 101 - 102], (
			ftnlen)sizeof(real));
	    }
	    e_wsfe();
	}
    }
    i__3 = nr;
    for (i__ = 1; i__ <= i__3; ++i__) {
/* L290: */
	if (recalc == 0) {
	    s_wsfe(&io___92);
	    do_fio(&c__1, (char *)&i__, (ftnlen)sizeof(integer));
	    do_fio(&c__1, (char *)&_BLNK__1.x2[i__ + mm * 101 - 102], (ftnlen)
		    sizeof(real));
	    i__2 = m;
	    for (k = 1; k <= i__2; ++k) {
		do_fio(&c__1, (char *)&_BLNK__1.x2[i__ + k * 101 - 102], (
			ftnlen)sizeof(real));
	    }
	    e_wsfe();
	}
    }
/*     WRITE (12,920) M */
/* L920: */
    if (ifk != 0) {
	goto L292;
    } else {
	goto L291;
    }
L291:
    if (recalc == 0) {
	s_wsfe(&io___93);
	do_fio(&c__1, (char *)&stress, (ftnlen)sizeof(real));
	do_fio(&c__1, (char *)&nn, (ftnlen)sizeof(integer));
	do_fio(&c__1, (char *)&s, (ftnlen)sizeof(real));
	e_wsfe();
    }
    s_wsfe(&io___94);
    do_fio(&c__1, (char *)&stress, (ftnlen)sizeof(real));
    do_fio(&c__1, (char *)&nn, (ftnlen)sizeof(integer));
    do_fio(&c__1, (char *)&s, (ftnlen)sizeof(real));
    e_wsfe();
    goto L293;
L292:
    if (recalc == 0) {
	s_wsfe(&io___95);
	do_fio(&c__1, (char *)&stress, (ftnlen)sizeof(real));
	do_fio(&c__1, (char *)&nn, (ftnlen)sizeof(integer));
	do_fio(&c__1, (char *)&s, (ftnlen)sizeof(real));
	e_wsfe();
    }
    s_wsfe(&io___96);
    do_fio(&c__1, (char *)&stress, (ftnlen)sizeof(real));
    do_fio(&c__1, (char *)&nn, (ftnlen)sizeof(integer));
    do_fio(&c__1, (char *)&s, (ftnlen)sizeof(real));
    e_wsfe();
L293:
    if (ifg == 1) {
    }
    if (recalc == 0) {
	s_wsfe(&io___97);
	do_fio(&c__1, (char *)&glp, (ftnlen)sizeof(real));
	e_wsfe();
    }
    if (ifg == 1) {
	s_wsfe(&io___98);
	do_fio(&c__1, (char *)&glp, (ftnlen)sizeof(real));
	e_wsfe();
    }
    ifglk = -ifglk;
    if (ifglk < 0) {
	goto L294;
    } else if (ifglk == 0) {
	goto L296;
    } else {
	goto L295;
    }

/*    SWITCH TO SINGLE PHAS ALGORITHM(KRUSKALS MONOTONE REGRESSION       S
SA1      990*/

L294:
    ifk = 1;
    goto L115;

/*    SWITCH TO DOUBLE PHAS ALGORITHM(RANK IMAGES)                       S
SA1      993*/

L295:
    ifk = 0;
L296:
    if (lfact == 0 && m > 1) {
	goto L314;
    }
L297:
    if (m - 1 != 0) {
	goto L298;
    } else {
	goto L320;
    }

/*    CALL ON PLOT SUBROUTINE                                            S
SA1      997*/

L298:
    if (ifd != 0) {
	goto L401;
    } else {
	goto L400;
    }
L401:
L400:
    if (nn < iter) {
	goto L299;
    }
    if (recalc == 0) {
	s_wsfe(&io___99);
	e_wsfe();
    }
    goto L300;
L299:
    if (ifc != 0) {
	goto L300;
    } else {
	goto L302;
    }

/*    PUNCH COORDINATES                                                  S
SA1     1005*/

L300:
    i__2 = nr;
    for (i__ = 1; i__ <= i__2; ++i__) {
	if (ifc != 3) {
	    goto L301;
	}
L301:
	if (recalc == 0) {
	    s_wsfe(&io___100);
	    i__3 = m;
	    for (j = 1; j <= i__3; ++j) {
		do_fio(&c__1, (char *)&_BLNK__1.x2[i__ + j * 101 - 102], (
			ftnlen)sizeof(real));
	    }
	    e_wsfe();
	}
    }
L302:
    if (ifd != 0) {
	goto L303;
    } else {
	goto L320;
    }

/*    PRINT DISTANCES                                                    S
SA1     1011*/

L303:
    switch ((int)miss) {
	case 1:  goto L311;
	case 2:  goto L304;
    }
L304:
    i__3 = nrm1;
    for (i__ = 1; i__ <= i__3; ++i__) {
	_BLNK__1.c__[i__ + i__ * 100 - 101] = (float)0.;
	ip1 = i__ + 1;
	i__2 = nr;
	for (j = ip1; j <= i__2; ++j) {
	    _BLNK__1.c__[j + i__ * 100 - 101] = (float)0.;
	    i__1 = m;
	    for (k = 1; k <= i__1; ++k) {
		if (ife != 0) {
		    goto L305;
		} else {
		    goto L306;
		}
L305:
		_BLNK__1.c__[j + i__ * 100 - 101] += (r__1 = _BLNK__1.x[i__ + 
			k * 101 - 102] - _BLNK__1.x[j + k * 101 - 102], dabs(
			r__1));
		goto L307;
L306:
/* Computing 2nd power */
		r__1 = _BLNK__1.x[i__ + k * 101 - 102] - _BLNK__1.x[j + k * 
			101 - 102];
		_BLNK__1.c__[j + i__ * 100 - 101] += r__1 * r__1;
L307:
		;
	    }
	    if (ife != 0) {
		goto L308;
	    } else {
		goto L309;
	    }
L308:
	    _BLNK__1.c__[j + i__ * 100 - 101] *= dim;
	    goto L310;
L309:
	    _BLNK__1.c__[j + i__ * 100 - 101] = sqrt(_BLNK__1.c__[j + i__ * 
		    100 - 101]) * dim;
L310:
	    ;
	}
    }
    goto L313;
L311:
    jj = 0;
    i__2 = nrm1;
    for (i__ = 1; i__ <= i__2; ++i__) {
	_BLNK__1.c__[i__ + i__ * 100 - 101] = (float)0.;
	ip1 = i__ + 1;
	i__3 = nr;
	for (j = ip1; j <= i__3; ++j) {
	    ++jj;
/* L312: */
	    _BLNK__1.c__[j + i__ * 100 - 101] = _BLNK__1.dist[jj - 1] * dim;
	}
    }
L313:
    _BLNK__1.c__[nr + nr * 100 - 101] = (float)0.;
    mxout_(_BLNK__1.c__, &nr, &c__1, &md);
    goto L320;

/*    DETERMINE HOW MANY DIMENSIONS TO DROP WHEN MIND= O                 S
SA1     1040*/

L314:
    kk = 1;
    mm = m;
    i__3 = mm;
    for (j = 2; j <= i__3; ++j) {
	ptemp = _BLNK__1.phi[j - 1] / _BLNK__1.phi[0];
	if (ptemp <= (float).3) {
	    goto L316;
	}
	++kk;
/* L315: */
    }
L316:
    m = kk - 1;
    maxd = kk;
    if (iter - 100 != 0) {
	goto L317;
    } else {
	goto L318;
    }
L317:
    iter += 25;
L318:
    if (mm - kk != 0) {
	goto L114;
    } else {
	goto L319;
    }
L319:
    ++m;
    goto L297;

/*    TEST FO  END                                                       S
SA1     1055*/

L320:
    if (stress <= (float)1e-4) {
	goto L600;
    }
    if (m - maxd != 0) {
	goto L114;
    } else {
	goto L600;
    }

/*    **** IF NOTIES IS REDIMENSIONED ALSO CHANGE NUMBER OF TIES IN FORM S
SA1     1058*/
/*    REFERENCED I N FOLLOWING STATEMENT                                 S
SA1     1059*/

L321:
    if (recalc == 0) {
	s_wsfe(&io___102);
	e_wsfe();
    }
    goto L600;

/*    PRINT COORDINATES OF ADDED VARIABLE                                S
SA1     1062*/

L322:
    ind = id;
    i__3 = maxd;
    for (j = 1; j <= i__3; ++j) {
/* L323: */
	_BLNK__1.x[ld + j * 101 - 102] += _BLNK__1.cmean[j - 1];
    }
    if (recalc == 0) {
	s_wsfe(&io___105);
	do_fio(&c__1, (char *)&ind, (ftnlen)sizeof(integer));
	i__3 = maxd;
	for (j = 1; j <= i__3; ++j) {
	    do_fio(&c__1, (char *)&_BLNK__1.x[ld + j * 101 - 102], (ftnlen)
		    sizeof(real));
	}
	e_wsfe();
    }
    isw = 1;
/* Computing 2nd power */
    r__1 = (float)1. - stress;
    stress = sqrt((float)1. - r__1 * r__1);
    if (recalc == 0) {
	s_wsfe(&io___106);
	do_fio(&c__1, (char *)&stress, (ftnlen)sizeof(real));
	do_fio(&c__1, (char *)&nn, (ftnlen)sizeof(integer));
	e_wsfe();
    }
/*     IF (IFC.GT.0) WRITE (3,328) (X(LD,J),J=1,MAXD) */
    miss = mr;
    goto L9;
/* L350: */
    if (recalc == 0) {
	s_wsfe(&io___108);
	e_wsfe();
    }
    if (recalc == 0) {
	s_wsfe(&io___109);
	e_wsfe();
    }

/*    ***** FORMAT STATEMENTS  ****                                      S
SA1     1083*/

/* L324: */
/* L327: */
/* L337: */
/* L341: */
/* L342: */
/* L500: */
/* L510: */
L600:
    if (recalc == 0) {
	s_wsfe(&io___110);
	e_wsfe();
    }

    delFiles();
L9:
    ;//s_stop("", 0L);
} /* MAIN__ */

delFiles()
{
	cl__1.cerr = 0;
    cl__1.cunit = 1;
    cl__1.csta = "DELETE";
    f_clos(&cl__1);
    cl__1.cerr = 0;
    cl__1.cunit = 2;
    cl__1.csta = "DELETE";
    f_clos(&cl__1);
    if (recalc == 0) {
	cl__1.cerr = 0;
	cl__1.cunit = 3;
	cl__1.csta = "DELETE";
	f_clos(&cl__1);
    }
    if (recalc == 0) {
	cl__1.cerr = 0;
	cl__1.cunit = 10;
	cl__1.csta = 0;
	f_clos(&cl__1);
    }
    cl__1.cerr = 0;
    cl__1.cunit = 11;
    cl__1.csta = "DELETE";
    f_clos(&cl__1);
    cl__1.cerr = 0;
    cl__1.cunit = 12;
    cl__1.csta = 0;
    f_clos(&cl__1);
}


/* Subroutine */ int sort_(isw, nel, dist, isim, index, md)
integer *isw, *nel;
real *dist;
integer *isim, *index, *md;
{
    /* System generated locals */
    integer i__1;

    /* Local variables */
    static integer i__, j, m, kk, ll, ipm, ipt1, ipt2;

    /* Parameter adjustments */
    --index;
    --dist;

    /* Function Body */
    if (*isw != 0) {
	goto L3;
    } else {
	goto L1;
    }
L1:
    i__1 = *nel;
    for (j = 1; j <= i__1; ++j) {
/* L2: */
	index[j] = j;
    }
L3:
    m = *nel;
L4:
    m /= 2;
    if (m != 0) {
	goto L5;
    } else {
	goto L12;
    }
L5:
    kk = *nel - m;
    j = 1;
L6:
    i__ = j;
L7:
    ipm = i__ + m;
    if (*isim != 0) {
	goto L9;
    } else {
	goto L8;
    }
L8:
    ipt1 = index[i__];
    ipt2 = index[ipm];
    if (dist[ipt1] > dist[ipt2]) {
	goto L11;
    }
    goto L10;
L9:
    ipt1 = index[i__];
    ipt2 = index[ipm];
    if (dist[ipt1] < dist[ipt2]) {
	goto L11;
    }
L10:
    ++j;
    if (j > kk) {
	goto L4;
    }
    goto L6;
L11:
    ll = index[i__];
    index[i__] = index[ipm];
    index[ipm] = ll;
    i__ -= m;
    if (i__ < 1) {
	goto L10;
    }
    goto L7;
L12:
    return 0;
} /* sort_ */




/* Subroutine */ int pmcor_(n, md, fmt, d__, r__, fn, sx, fmt_len)
integer *n, *md;
char *fmt;
real *d__, *r__, *fn, *sx;
ftnlen fmt_len;
{
    /* Format strings */
    static char fmt_59[] = "(\002 TYPE OF COEFFICIENT.....PEARSON P.M. CORRE\
LATION.\002)";
    static char fmt_110[] = "(\002 NUMBER OF CASES READ....\002,i4)";
    static char fmt_111[] = "(//,40x,40(\002-\002))";
    static char fmt_112[] = "(40x,\002 ITEM      MEAN        S.D.      VARIA\
NCE \002)";
    static char fmt_113[] = "(40x,40(\002-\002))";
    static char fmt_205[] = "(40x,i5,3f12.4)";

    /* System generated locals */
    integer i__1, i__2;
    cilist ci__1;

    /* Builtin functions */
    integer s_wsfe(), e_wsfe(), s_rsle(), do_lio(), e_rsle(), s_rsfe(), 
	    do_fio(), e_rsfe();
    double sqrt();

    /* Local variables */
    static real code, xbar;
    static integer msdt, miss, i__, j;
    static real t, b1, b2, sd, recalc;
    static integer nn, nm1;
    static char ans[1];
    static real var;

    /* Fortran I/O blocks */
    static cilist io___122 = { 0, 3, 0, fmt_59, 0 };
    static cilist io___123 = { 0, 11, 0, 0, 0 };
    static cilist io___125 = { 0, 11, 0, 0, 0 };
    static cilist io___133 = { 0, 3, 0, fmt_110, 0 };
    static cilist io___134 = { 0, 3, 0, fmt_111, 0 };
    static cilist io___135 = { 0, 3, 0, fmt_112, 0 };
    static cilist io___136 = { 0, 3, 0, fmt_113, 0 };
    static cilist io___140 = { 0, 3, 0, fmt_205, 0 };
    static cilist io___141 = { 0, 3, 0, fmt_113, 0 };



    /* Parameter adjustments */
    --sx;
    --fn;
    r__ -= 101;
    --d__;

    /* Function Body */
    miss = 0;
    code = (float)9e20;

    if (recalc == (float)0.) {
	s_wsfe(&io___122);
	e_wsfe();
    }
/* L10: */
    s_rsle(&io___123);
    do_lio(&c__9, &c__1, ans, 1L);
    e_rsle();
    if (*(unsigned char *)ans == 'Y' || *(unsigned char *)ans == 'y') {
	miss = 1;
    }
    if (miss != 0) {
	goto L30;
    } else {
	goto L50;
    }
L30:
/* L40: */
    s_rsle(&io___125);
    do_lio(&c__4, &c__1, (char *)&code, (ftnlen)sizeof(real));
    e_rsle();
L50:
    i__1 = *n;
    for (j = 1; j <= i__1; ++j) {
	i__2 = j;
	for (i__ = 1; i__ <= i__2; ++i__) {
/* L55: */
	    r__[i__ + j * 100] = (float)0.;
	}
	sx[j] = (float)0.;
/* L60: */
	fn[j] = (float)0.;
    }

    nn = 0;
L70:
    ci__1.cierr = 0;
    ci__1.ciend = 1;
    ci__1.ciunit = 1;
    ci__1.cifmt = fmt;
    i__1 = s_rsfe(&ci__1);
    if (i__1 != 0) {
	goto L100;
    }
    i__2 = *n;
    for (j = 1; j <= i__2; ++j) {
	i__1 = do_fio(&c__1, (char *)&d__[j], (ftnlen)sizeof(real));
	if (i__1 != 0) {
	    goto L100;
	}
    }
    i__1 = e_rsfe();
    if (i__1 != 0) {
	goto L100;
    }
    ++nn;
    msdt = 0;
    i__1 = *n;
    for (j = 1; j <= i__1; ++j) {
	if (d__[j] - code != (float)0.) {
	    goto L90;
	} else {
	    goto L92;
	}
L92:
	msdt = 1;
L90:
	;
    }
    if (msdt == 0) {
	goto L80;
    }
    --nn;
    goto L70;
L80:
    i__1 = *n;
    for (j = 1; j <= i__1; ++j) {
	fn[j] += d__[j];
	sx[j] += d__[j] * d__[j];
	i__2 = j;
	for (i__ = 1; i__ <= i__2; ++i__) {
	    r__[i__ + j * 100] += d__[i__] * d__[j];
/* L95: */
	}
    }
    goto L70;

L100:
    i__2 = *n;
    for (j = 1; j <= i__2; ++j) {
	i__1 = j;
	for (i__ = 1; i__ <= i__1; ++i__) {
	    t = r__[i__ + j * 100] - fn[i__] * fn[j] / nn;
	    b1 = sx[i__] - fn[i__] * fn[i__] / nn;
	    b2 = sx[j] - fn[j] * fn[j] / nn;
	    r__[i__ + j * 100] = t / sqrt(b1 * b2);
/* L105: */
	    r__[j + i__ * 100] = r__[i__ + j * 100];
	}
    }

/* L110: */

    i__1 = *n;
    for (j = 1; j <= i__1; ++j) {
	sx[j] = sqrt((sx[j] - fn[j] * fn[j] / nn) / nn);
/* L120: */
	fn[j] /= nn;
    }
    if (recalc == (float)0.) {
	s_wsfe(&io___133);
	do_fio(&c__1, (char *)&nn, (ftnlen)sizeof(integer));
	e_wsfe();
    }
    if (recalc == (float)0.) {
	s_wsfe(&io___134);
	e_wsfe();
    }
    if (recalc == (float)0.) {
	s_wsfe(&io___135);
	e_wsfe();
    }
    if (recalc == (float)0.) {
	s_wsfe(&io___136);
	e_wsfe();
    }
    i__1 = *n;
    for (i__ = 1; i__ <= i__1; ++i__) {
	xbar = fn[i__];
	var = sx[i__] * sx[i__];
	sd = sx[i__];
	if (recalc == (float)0.) {
	    s_wsfe(&io___140);
	    do_fio(&c__1, (char *)&i__, (ftnlen)sizeof(integer));
	    do_fio(&c__1, (char *)&xbar, (ftnlen)sizeof(real));
	    do_fio(&c__1, (char *)&sd, (ftnlen)sizeof(real));
	    do_fio(&c__1, (char *)&var, (ftnlen)sizeof(real));
	    e_wsfe();
	}
/* L210: */
    }
    if (recalc == (float)0.) {
	s_wsfe(&io___141);
	e_wsfe();
    }
    nm1 = *n - 1;

/* L556: */
    return 0;
} /* pmcor_ */




/* Subroutine */ int euclid_(m, fmt, r__, sx, xx, fn, d__, fmt_len)
integer *m;
char *fmt;
real *r__, *sx, *xx, *fn, *d__;
ftnlen fmt_len;
{
    /* Format strings */
    static char fmt_59[] = "(\002 TYPE OF COEFFICIENT.....EUCLIDEAN DISTAN\
CE.\002)";
    static char fmt_40[] = "(\002 NUMBER OF CASES READ....\002,i4)";
    static char fmt_800[] = "(//,40x,40(\002-\002))";
    static char fmt_802[] = "(40x,\002ITEM      MEAN        S.D.      VARIAN\
CE\002)";
    static char fmt_804[] = "(40x,40(\002-\002))";
    static char fmt_806[] = "(40x,i4,3f12.4)";

    /* System generated locals */
    integer i__1, i__2;
    real r__1;
    cilist ci__1;
    alist al__1;

    /* Builtin functions */
    integer f_rew(), s_wsfe(), e_wsfe(), s_rsle(), do_lio(), e_rsle(), s_rsfe(
	    ), do_fio(), e_rsfe();
    double sqrt();

    /* Local variables */
    static real code, xbar;
    static integer miss, i__, j, k, l;
    static real sd, recalc;
    static integer nn;
    static real tn;
    static char ans[1];
    static real var;

    /* Fortran I/O blocks */
    static cilist io___146 = { 0, 3, 0, fmt_59, 0 };
    static cilist io___148 = { 0, 11, 0, 0, 0 };
    static cilist io___150 = { 0, 11, 0, 0, 0 };
    static cilist io___156 = { 0, 3, 0, fmt_40, 0 };
    static cilist io___157 = { 0, 3, 0, fmt_800, 0 };
    static cilist io___158 = { 0, 3, 0, fmt_802, 0 };
    static cilist io___159 = { 0, 3, 0, fmt_804, 0 };
    static cilist io___163 = { 0, 3, 0, fmt_806, 0 };
    static cilist io___164 = { 0, 3, 0, fmt_804, 0 };



    /* Parameter adjustments */
    --d__;
    --fn;
    --xx;
    --sx;
    r__ -= 101;

    /* Function Body */
    al__1.aerr = 0;
    al__1.aunit = 1;
    f_rew(&al__1);
    miss = 0;
    code = (float)9e20;

    if (recalc == (float)0.) {
	s_wsfe(&io___146);
	e_wsfe();
    }
    i__1 = *m;
    for (i__ = 1; i__ <= i__1; ++i__) {
	sx[i__] = (float)0.;
	xx[i__] = (float)0.;
/* L5: */
	fn[i__] = (float)0.;
    }
/* L600: */
    s_rsle(&io___148);
    do_lio(&c__9, &c__1, ans, 1L);
    e_rsle();
    if (*(unsigned char *)ans == 'Y' || *(unsigned char *)ans == 'y') {
	miss = 1;
    }
    if (miss != 0) {
	goto L700;
    } else {
	goto L701;
    }
L700:
/* L604: */
    s_rsle(&io___150);
    do_lio(&c__4, &c__1, (char *)&code, (ftnlen)sizeof(real));
    e_rsle();
L650:
    ci__1.cierr = 0;
    ci__1.ciend = 1;
    ci__1.ciunit = 1;
    ci__1.cifmt = fmt;
    i__1 = s_rsfe(&ci__1);
    if (i__1 != 0) {
	goto L670;
    }
    i__2 = *m;
    for (j = 1; j <= i__2; ++j) {
	i__1 = do_fio(&c__1, (char *)&d__[j], (ftnlen)sizeof(real));
	if (i__1 != 0) {
	    goto L670;
	}
    }
    i__1 = e_rsfe();
    if (i__1 != 0) {
	goto L670;
    }
    i__1 = *m;
    for (i__ = 1; i__ <= i__1; ++i__) {
	if (d__[i__] - code != (float)0.) {
	    goto L6660;
	} else {
	    goto L660;
	}
L6660:
	sx[i__] += d__[i__];
	fn[i__] += (float)1.;
L660:
	;
    }
    goto L650;
L670:
    i__1 = *m;
    for (i__ = 1; i__ <= i__1; ++i__) {
	sx[i__] /= fn[i__];
/* L680: */
	fn[i__] = (float)0.;
    }
    al__1.aerr = 0;
    al__1.aunit = 1;
    f_rew(&al__1);
L701:
    nn = 0;
L10:
    ci__1.cierr = 0;
    ci__1.ciend = 1;
    ci__1.ciunit = 1;
    ci__1.cifmt = fmt;
    i__1 = s_rsfe(&ci__1);
    if (i__1 != 0) {
	goto L25;
    }
    i__2 = *m;
    for (l = 1; l <= i__2; ++l) {
	i__1 = do_fio(&c__1, (char *)&d__[l], (ftnlen)sizeof(real));
	if (i__1 != 0) {
	    goto L25;
	}
    }
    i__1 = e_rsfe();
    if (i__1 != 0) {
	goto L25;
    }
    ++nn;
    if (miss != 0) {
	goto L720;
    } else {
	goto L710;
    }
L720:
    i__1 = *m;
    for (i__ = 1; i__ <= i__1; ++i__) {
	if (d__[i__] - code != (float)0.) {
	    goto L730;
	} else {
	    goto L7730;
	}
L7730:
	d__[i__] = sx[i__];
L730:
	;
    }
L710:
    i__1 = *m;
    for (j = 1; j <= i__1; ++j) {
	fn[j] += d__[j];
/* Computing 2nd power */
	r__1 = d__[j];
	xx[j] += r__1 * r__1;
	i__2 = *m;
	for (k = 1; k <= i__2; ++k) {
/* L20: */
/* Computing 2nd power */
	    r__1 = d__[j] - d__[k];
	    r__[j + k * 100] += r__1 * r__1;
	}
    }
    goto L10;
L25:
    tn = (real) nn;
    if (recalc == (float)0.) {
	s_wsfe(&io___156);
	do_fio(&c__1, (char *)&nn, (ftnlen)sizeof(integer));
	e_wsfe();
    }
    if (recalc == (float)0.) {
	s_wsfe(&io___157);
	e_wsfe();
    }
    if (recalc == (float)0.) {
	s_wsfe(&io___158);
	e_wsfe();
    }
    if (recalc == (float)0.) {
	s_wsfe(&io___159);
	e_wsfe();
    }
    i__2 = *m;
    for (j = 1; j <= i__2; ++j) {
	xbar = fn[j] / tn;
/* Computing 2nd power */
	r__1 = fn[j];
	var = (xx[j] - r__1 * r__1 / tn) / tn;
	sd = sqrt(var);
	if (recalc == (float)0.) {
	    s_wsfe(&io___163);
	    do_fio(&c__1, (char *)&j, (ftnlen)sizeof(integer));
	    do_fio(&c__1, (char *)&xbar, (ftnlen)sizeof(real));
	    do_fio(&c__1, (char *)&sd, (ftnlen)sizeof(real));
	    do_fio(&c__1, (char *)&var, (ftnlen)sizeof(real));
	    e_wsfe();
	}
	i__1 = *m;
	for (k = 1; k <= i__1; ++k) {
/* L30: */
	    r__[j + k * 100] = sqrt(r__[j + k * 100]);
	}
    }
    if (recalc == (float)0.) {
	s_wsfe(&io___164);
	e_wsfe();
    }
/* L15: */
    return 0;
} /* euclid_ */




/* Subroutine */ int glac_(m, fmt, a, b, c__, d__, aa, bb, cc, fmt_len)
integer *m;
char *fmt;
real *a, *b, *c__, *d__, *aa, *bb, *cc;
ftnlen fmt_len;
{
    /* Format strings */
    static char fmt_17[] = "(i1)";
    static char fmt_20[] = "(\002 TYPE OF COEFFICIENT.....JACCARD INDEX.\002)"
	    ;
    static char fmt_21[] = "(\002 TYPE OF COEFFICIENT.....G INDEX.\002)";
    static char fmt_22[] = "(\002 TYPE OF COEFFICIENT....YULE Q.\002)";
    static char fmt_23[] = "(\002 TYPE OF COEFFICIENT....PEARSON (PHI).\002)";
    static char fmt_142[] = "(\002 PROFILES. \002,i4,\002 CASES CONTAINED NO\
N-BINARY DATA.\002)";
    static char fmt_170[] = "(40x,42(\002-\002))";
    static char fmt_160[] = "(40x,\002 VARIABLE       N OF 1`S         % OF \
1`S.\002)";
    static char fmt_180[] = "(40x,i6,10x,f6,12x,f8.3,12x,f6)";

    /* System generated locals */
    integer i__1, i__2;
    cilist ci__1;

    /* Builtin functions */
    integer s_rsfe(), do_fio(), e_rsfe(), s_wsfe(), e_wsfe();
    double sqrt();

    /* Local variables */
    static integer i__, j, k, n;
    static real p, a1, b1, c1, d1;
    static integer m1;
    static real recalc;
    static integer mp, nr;
    static real tm, tn;
    static integer nw, nx, ian;
    static real tnw;

    /* Fortran I/O blocks */
    static cilist io___166 = { 0, 11, 0, fmt_17, 0 };
    static cilist io___169 = { 0, 3, 0, fmt_20, 0 };
    static cilist io___170 = { 0, 3, 0, fmt_21, 0 };
    static cilist io___171 = { 0, 3, 0, fmt_22, 0 };
    static cilist io___172 = { 0, 3, 0, fmt_23, 0 };
    static cilist io___187 = { 0, 3, 0, fmt_142, 0 };
    static cilist io___189 = { 0, 3, 0, fmt_170, 0 };
    static cilist io___190 = { 0, 3, 0, fmt_160, 0 };
    static cilist io___191 = { 0, 3, 0, fmt_170, 0 };
    static cilist io___193 = { 0, 3, 0, fmt_180, 0 };
    static cilist io___194 = { 0, 3, 0, fmt_170, 0 };



    /* Parameter adjustments */
    --cc;
    --bb;
    --aa;
    --d__;
    c__ -= 101;
    --b;
    --a;

    /* Function Body */
    i__1 = *m;
    for (i__ = 1; i__ <= i__1; ++i__) {
/* L5: */
	b[i__] = (float)0.;
    }
    s_rsfe(&io___166);
    do_fio(&c__1, (char *)&ian, (ftnlen)sizeof(integer));
    e_rsfe();
    if (ian == 1) {
	if (recalc == (float)0.) {
	    s_wsfe(&io___169);
	    e_wsfe();
	}
    }
    if (ian == 2) {
	if (recalc == (float)0.) {
	    s_wsfe(&io___170);
	    e_wsfe();
	}
    }
    if (ian == 3) {
	if (recalc == (float)0.) {
	    s_wsfe(&io___171);
	    e_wsfe();
	}
    }
    if (ian == 4) {
	if (recalc == (float)0.) {
	    s_wsfe(&io___172);
	    e_wsfe();
	}
    }
/* L10: */
/* L11: */
/* L12: */
/* L13: */
/* L14: */
/* L15: */
/* L16: */
    nr = 0;
    nw = 0;
    tm = (real) (*m);
    m1 = *m - 1;
    k = 0;
    i__1 = *m;
    for (i__ = 1; i__ <= i__1; ++i__) {
	b[i__] = (float)0.;
	i__2 = *m;
	for (j = i__; j <= i__2; ++j) {
	    aa[k] = (float)0.;
	    bb[k] = (float)0.;
/* L25: */
	    cc[k] = (float)0.;
	}
    }
L40:
    ci__1.cierr = 0;
    ci__1.ciend = 1;
    ci__1.ciunit = 1;
    ci__1.cifmt = fmt;
    i__2 = s_rsfe(&ci__1);
    if (i__2 != 0) {
	goto L130;
    }
    i__1 = *m;
    for (j = 1; j <= i__1; ++j) {
	i__2 = do_fio(&c__1, (char *)&a[j], (ftnlen)sizeof(real));
	if (i__2 != 0) {
	    goto L130;
	}
    }
    i__2 = e_rsfe();
    if (i__2 != 0) {
	goto L130;
    }
    ++nr;
    mp = 0;
    i__2 = *m;
    for (i__ = 1; i__ <= i__2; ++i__) {
	if (a[i__] > (float)1. || a[i__] < (float)0.) {
	    ++mp;
	}
/* L41: */
    }
    if (mp > 0) {
	goto L40;
    }
    ++nw;
    k = 0;
    i__2 = *m;
    for (i__ = 1; i__ <= i__2; ++i__) {
	b[i__] += a[i__];
	i__1 = *m;
	for (j = i__; j <= i__1; ++j) {
	    ++k;
	    if (a[i__] == (float)1. && a[j] == (float)1.) {
		++aa[k];
	    }
	    if (a[i__] == (float)1. && a[j] == (float)0.) {
		++bb[k];
	    }
	    if (a[i__] == (float)0. && a[j] == (float)1.) {
		++cc[k];
	    }
/* L42: */
	}
    }
    goto L40;
L130:
    k = 0;
    tnw = (real) nw;
    i__1 = *m;
    for (i__ = 1; i__ <= i__1; ++i__) {
	i__2 = *m;
	for (j = i__; j <= i__2; ++j) {
	    ++k;
	    a1 = aa[k];
	    b1 = bb[k];
	    c1 = cc[k];
	    d1 = nw - (a1 + b1 + c1);
	    if (ian == 1) {
		c__[i__ + j * 100] = a1 / (a1 + b1 + c1);
	    }
	    if (ian == 2) {
		c__[i__ + j * 100] = (a1 + d1 - (b1 + c1)) / tnw;
	    }
	    if (ian == 3) {
		c__[i__ + j * 100] = (a1 * d1 - b1 * c1) / (a1 * d1 + b1 * c1)
			;
	    }
	    if (ian == 4) {
		c__[i__ + j * 100] = (a1 * d1 - b1 * c1) / sqrt((a1 + b1) * (
			a1 + c1) * (b1 + d1) * (c1 + d1));
	    }
/* L100: */
	    c__[j + i__ * 100] = c__[i__ + j * 100];
	}
    }
    nx = nr - nw;
    n = nw;
    if (recalc == (float)0.) {
	s_wsfe(&io___187);
	do_fio(&c__1, (char *)&nx, (ftnlen)sizeof(integer));
	e_wsfe();
    }
/* L140: */
/* L150: */
    tn = (real) n;
    if (recalc == (float)0.) {
	s_wsfe(&io___189);
	e_wsfe();
    }
    if (recalc == (float)0.) {
	s_wsfe(&io___190);
	e_wsfe();
    }
    if (recalc == (float)0.) {
	s_wsfe(&io___191);
	e_wsfe();
    }
/* L180: */
    i__2 = *m;
    for (j = 1; j <= i__2; ++j) {
	p = b[j] / tn * (float)100.;
/* L190: */
	if (recalc == (float)0.) {
	    s_wsfe(&io___193);
	    do_fio(&c__1, (char *)&j, (ftnlen)sizeof(integer));
	    do_fio(&c__1, (char *)&b[j], (ftnlen)sizeof(real));
	    do_fio(&c__1, (char *)&p, (ftnlen)sizeof(real));
	    do_fio(&c__1, (char *)&tn, (ftnlen)sizeof(real));
	    e_wsfe();
	}
    }
    if (recalc == (float)0.) {
	s_wsfe(&io___194);
	e_wsfe();
    }
    return 0;
} /* glac_ */





/*    EIGEN                                                              EIGN 
       2*/
/*    EIGENVALUES AND NORMALIZED EIGENVECTORS OF A REAL SYMMETRIC MATRIX EIGN 
       3*/
/*    PROGRAMMED BY GARBOW, ARGONNE, 1965 AND MODIFIED BY LINGOES, U OF  EIGN 
       4*/
/*    M, 1966 USING HOUSEHOLDER'S TRIDIAGONALIZATION PROCEDURE AND       EIGN 
       5*/
/*    INVERSE ITERATIONS TO OBTAIN EIGENVECTORS.  COMPLETE MULTIPLICITY  EIGN 
       6*/
/*    OF EIGENSYSTEM IS DETERMINED.  IF NZ=1 VECTORS ARE NORMALIZED.     EIGN 
       7*/
/*    EIGENVALUES ARE RETURNED IN VALU AND NORMALIZED EIGENVECTORS ARE   EIGN 
       8*/
/*    STORED IN B.  NSUB IS ORDER OF MATRICES A AND B AND MSUB IS THE    EIGN 
       9*/
/*    NUMBER OF ROOTS AND VECTORS DESIRED.                               EIGN 
      10*/
/*                                                                       EIGN 
      11*/
/* Subroutine */ int eigen_(a, b, nsub, valu, msub, md, t, diag, superd, u, 
	index, v, nz)
real *a, *b;
integer *nsub;
real *valu;
integer *msub, *md;
real *t, *diag, *superd, *u;
integer *index;
real *v;
integer *nz;
{
    /* System generated locals */
    integer i__1, i__2, i__3;
    real r__1;

    /* Builtin functions */
    double sqrt(), r_sign();

    /* Local variables */
    static integer iter;
    static real temp, temp1, temp2;
    static integer i__, j, k, l, m, n;
    static real p;
    static integer match;
    static real tempa, anorm, e1;
    static integer i1, i2;
    static real vtemp;
    static integer l1;
    static real vnorm, t0, t1, t2, anorm2, vnorm2, scalar;
    static integer jp1, nm1, np1;
    static real div, tau, tom, sum, eps1;

/*                                                                        
EIGN       14*/

/*     INITIALIZATION */
    /* Parameter adjustments */
    --v;
    --index;
    --u;
    --superd;
    --diag;
    t -= 101;
    --valu;
    b -= 101;
    a -= 101;

    /* Function Body */
    n = *nsub;
    m = *msub;
    np1 = n + 1;
    nm1 = n - 1;
    e1 = (float)1e-8;
    i__1 = n;
    for (i__ = 1; i__ <= i__1; ++i__) {
	i__2 = n;
	for (j = 1; j <= i__2; ++j) {
	    if (i__ - j != 0) {
		goto L2;
	    } else {
		goto L1;
	    }
L1:
	    b[i__ + j * 100] = (float)1.;
	    goto L3;
L2:
	    b[i__ + j * 100] = (float)0.;
L3:
	    ;
	}
    }
/*     HOUSEHOLDER SIMILARITY TRANSFORMATION TO CO-DIAGONAL FORM */
/*    REDUCE COLUMN OF MATRIX                                            E
IGN       33*/
    i__2 = nm1;
    for (i__ = 1; i__ <= i__2; ++i__) {
	if (i__ - nm1 != 0) {
	    goto L4;
	} else {
	    goto L13;
	}
L4:
	i1 = i__ + 1;
	i2 = i1 + 1;
	sum = (float)0.;
	i__1 = n;
	for (j = i2; j <= i__1; ++j) {
/* L5: */
/* Computing 2nd power */
	    r__1 = a[j + i__ * 100];
	    sum += r__1 * r__1;
	}
	if (sum != (float)0.) {
	    goto L6;
	} else {
	    goto L13;
	}
L6:
	j = i1;
	temp = a[j + i__ * 100];
/* Computing 2nd power */
	r__1 = temp;
	sum = sqrt(sum + r__1 * r__1);
	tempa = r_sign(&sum, &temp);
	a[j + i__ * 100] = -tempa;
	u[j] = sqrt(dabs(temp) / sum + (float)1.);
	if (temp != (float)0.) {
	    goto L1110;
	} else {
	    goto L110;
	}
L1110:
	r__1 = u[j] * sum;
	div = r_sign(&r__1, &temp);
	goto L111;
L110:
	div = (r__1 = u[j] * sum, dabs(r__1));
L111:
	i__1 = n;
	for (j = i2; j <= i__1; ++j) {
/* L7: */
	    u[j] = a[j + i__ * 100] / div;
	}
	scalar = (float)0.;
	i__1 = n;
	for (j = i1; j <= i__1; ++j) {
	    v[j] = (float)0.;
	    i__3 = n;
	    for (k = i1; k <= i__3; ++k) {
		temp1 = a[k + j * 100] * u[k];
/* L8: */
		v[j] += temp1;
	    }
	    temp2 = v[j] * u[j];
	    scalar += temp2;
/* L9: */
	}
	scalar /= (float)2.;
	i__1 = n;
	for (j = i1; j <= i__1; ++j) {
	    v[j] -= scalar * u[j];
	    i__3 = j;
	    for (k = i1; k <= i__3; ++k) {
		a[k + j * 100] -= u[k] * v[j] + u[j] * v[k];
		a[j + k * 100] = a[k + j * 100];
/* L10: */
	    }
	}
/*    SAVE ROTATION FOR LATER APPLICATION TO CO-DIAGONAL VECTORS      
   EIGN       71*/
	i__3 = n;
	for (k = 2; k <= i__3; ++k) {
	    temp = (float)0.;
	    i__1 = n;
	    for (j = i1; j <= i__1; ++j) {
/* L11: */
		temp += u[j] * b[j + k * 100];
	    }
	    i__1 = n;
	    for (j = i1; j <= i__1; ++j) {
		b[j + k * 100] -= u[j] * temp;
/* L12: */
	    }
	}
/*    MOVE CO-DIAGONAL FORM ELEMENTS FOR ITERATIVE PROCEDURE          
   EIGN       79*/
L13:
	j = i__;
	diag[i__] = a[j + i__ * 100];
	jp1 = j + 1;
	superd[i__] = a[jp1 + i__ * 100];
/* L14: */
    }
    diag[n] = a[n + n * 100];
/*    DETERMINE EIGENVALUES FROM STURM CHAIN OF CO-DIAGONAL MINORS       E
IGN       86*/
/*    CALCULATE NORM OF MATRIX AND INITIALIZE EIGENVALUE BOUNDS          E
IGN       87*/
/* Computing 2nd power */
    r__1 = diag[1];
    anorm2 = r__1 * r__1;
    i__2 = n;
    for (l = 2; l <= i__2; ++l) {
/* Computing 2nd power */
	r__1 = superd[l - 1];
	v[l - 1] = r__1 * r__1;
/* Computing 2nd power */
	r__1 = diag[l];
	anorm2 = r__1 * r__1 + v[l - 1] + v[l - 1] + anorm2;
/* L15: */
    }
    anorm = sqrt(anorm2);
    i__2 = m;
    for (l = 1; l <= i__2; ++l) {
	valu[l] = anorm;
	u[l] = -anorm;
/* L16: */
    }
    eps1 = anorm * e1;
    if (eps1 != (float)0.) {
	goto L17;
    } else {
	goto L77;
    }
/*    CHOOSE NEW TRIAL VALUE WHILE TESTING BOUNDS FOR CONVERGENCE        E
IGN      100*/
L17:
    i__2 = m;
    for (l = 1; l <= i__2; ++l) {
	iter = 0;
	vtemp = eps1;
L18:
	tau = (valu[l] + u[l]) / (float)2.;
	if (iter - 10 != 0) {
	    goto L20;
	} else {
	    goto L19;
	}
L19:
	vtemp *= (float)10.;
	iter = 0;
L20:
	if ((tau - u[l]) * (float)2. - vtemp <= (float)0.) {
	    goto L37;
	} else {
	    goto L21;
	}
/*    DETERMINE SIGNS OF PRINCIPAL MINORS                             
   EIGN      109*/
L21:
	match = 0;
	++iter;
	t2 = (float)0.;
	t1 = (float)1.;
	i__1 = n;
	for (l1 = 1; l1 <= i__1; ++l1) {
	    p = diag[l1] - tau;
	    if (t2 != (float)0.) {
		goto L23;
	    } else {
		goto L22;
	    }
L22:
	    t1 = r_sign(&c_b675, &t1);
L23:
	    if (t1 != (float)0.) {
		goto L25;
	    } else {
		goto L24;
	    }
L24:
	    t0 = -r_sign(&c_b675, &t2);
	    t2 = (float)0.;
	    if (v[l1 - 1] != (float)0.) {
		goto L28;
	    } else {
		goto L22;
	    }
L25:
	    if (l1 == 1) {
		goto L26;
	    }
	    t0 = p - v[l1 - 1] * t2 / t1;
	    goto L27;
L26:
	    t0 = p;
L27:
	    t2 = (float)1.;
/*    COUNT AGREEMENTS IN SIGN (ZERO CONSIDERED POSITIVE)         
       EIGN      127*/
L28:
	    if (t0 < (float)0.) {
		goto L31;
	    } else if (t0 == 0) {
		goto L29;
	    } else {
		goto L30;
	    }
L29:
	    t2 = t1;
	    if (t2 >= (float)0.) {
		goto L30;
	    } else {
		goto L31;
	    }
L30:
	    ++match;
L31:
	    t1 = t0;
/* L32: */
	}
/*    ESTABLISH TIGHTER BOUNDS ON EIGENVALUES                         
   EIGN      134*/
	i__1 = m;
	for (l1 = l; l1 <= i__1; ++l1) {
	    if (l1 - match <= 0) {
		goto L35;
	    } else {
		goto L33;
	    }
L33:
	    if (valu[l1] - tau <= (float)0.) {
		goto L18;
	    } else {
		goto L34;
	    }
L34:
	    valu[l1] = tau;
	    goto L36;
L35:
	    u[l1] = tau;
L36:
	    ;
	}
	goto L18;
L37:
	;
    }
/*    EIGENVECTORS OF CO-DIAGONAL SYMMETRIC MATRIX -- INVERSE ITERATION  E
IGN      144*/
/*    CHECK FOR REPEATED VALUE                                           E
IGN      145*/
    i__2 = m;
    for (i__ = 1; i__ <= i__2; ++i__) {
	if (i__ - 2 >= 0) {
	    goto L38;
	} else {
	    goto L39;
	}
L38:
	if (valu[i__ - 1] - valu[i__] - (float).001 >= (float)0.) {
	    goto L39;
	} else {
	    goto L40;
	}
L39:
	i1 = -1;
L40:
	++i1;
/*    TRIANGULARIZE CO-DIAGONAL FORM AFTER EIGENVALUE SUBTRACTION     
   EIGN      151*/
	i__1 = n;
	for (l = 1; l <= i__1; ++l) {
	    v[l] = eps1;
	    t[l + 200] = diag[l] - valu[i__];
	    if (l - n != 0) {
		goto L42;
	    } else {
		goto L41;
	    }
L41:
	    t[l + 300] = (float)0.;
	    goto L45;
L42:
	    t[l + 300] = superd[l];
	    if (t[l + 300] != (float)0.) {
		goto L44;
	    } else {
		goto L43;
	    }
L43:
	    t[l + 300] = eps1;
L44:
	    t[l + 101] = t[l + 300];
L45:
	    ;
	}
	i__1 = n;
	for (j = 1; j <= i__1; ++j) {
	    t[j + 100] = t[j + 200];
	    t[j + 200] = t[j + 300];
	    t[j + 300] = (float)0.;
	    vtemp = (r__1 = t[j + 100], dabs(r__1));
	    if (j - n != 0) {
		goto L48;
	    } else {
		goto L46;
	    }
L46:
	    if (vtemp != (float)0.) {
		goto L52;
	    } else {
		goto L47;
	    }
L47:
	    t[j + 100] = eps1;
	    goto L52;
L48:
	    index[j] = 0;
	    if ((r__1 = t[j + 101], dabs(r__1)) - vtemp <= (float)0.) {
		goto L51;
	    } else {
		goto L49;
	    }
L49:
	    index[j] = 1;
	    for (k = 1; k <= 3; ++k) {
		vtemp = t[j + k * 100];
		t[j + k * 100] = t[j + 1 + k * 100];
		t[j + 1 + k * 100] = vtemp;
/* L50: */
	    }
L51:
	    vtemp = t[j + 101] / t[j + 100];
	    u[j] = vtemp;
	    t[j + 201] -= vtemp * t[j + 200];
	    t[j + 301] -= vtemp * t[j + 300];
L52:
	    ;
	}
	iter = 1;
	v[n + 1] = (float)0.;
	v[n + 2] = (float)0.;
	if (i1 != 0) {
	    goto L60;
	} else {
	    goto L53;
	}
/*    BACK SUBSTITUTE TO OBTAIN EIGENVECTOR                           
   EIGN      189*/
L53:
	i__1 = n;
	for (l1 = 1; l1 <= i__1; ++l1) {
	    l = np1 - l1;
	    tom = v[l] - t[l + 200] * v[l + 1] - t[l + 300] * v[l + 2];
	    v[l] = tom / t[l + 100];
/* L54: */
	}
	switch ((int)iter) {
	    case 1:  goto L55;
	    case 2:  goto L60;
	}
/*    PERFORM SECOND ITERATION                                        
   EIGN      196*/
L55:
	iter = 2;
L56:
	i__1 = n;
	for (l = 2; l <= i__1; ++l) {
	    if (index[l - 1] != 0) {
		goto L57;
	    } else {
		goto L58;
	    }
L57:
	    vtemp = v[l - 1];
	    v[l - 1] = v[l];
	    v[l] = vtemp;
L58:
	    v[l] -= u[l - 1] * v[l - 1];
/* L59: */
	}
	goto L53;
/*    ORTHOGONALIZE VECTOR TO OTHERS ASSOCIATED WITH REPEATED ROOT    
   EIGN      206*/
L60:
	if (i1 != 0) {
	    goto L61;
	} else {
	    goto L64;
	}
L61:
	i__1 = i1;
	for (l1 = 1; l1 <= i__1; ++l1) {
	    k = i__ - l1;
	    vtemp = (float)0.;
	    i__3 = n;
	    for (j = 1; j <= i__3; ++j) {
/* L62: */
		vtemp += a[j + k * 100] * v[j];
	    }
	    i__3 = n;
	    for (j = 1; j <= i__3; ++j) {
/* L63: */
		v[j] -= a[j + k * 100] * vtemp;
	    }
	}
L64:
	switch ((int)iter) {
	    case 1:  goto L56;
	    case 2:  goto L65;
	}
/*    NORMALIZE VECTOR TO UNIT LENGTH                                 
   EIGN      216*/
L65:
	vnorm2 = (float)0.;
	sum = (float)0.;
	i__3 = n;
	for (l = 1; l <= i__3; ++l) {
	    if (sum - (r__1 = v[l], dabs(r__1)) >= (float)0.) {
		goto L67;
	    } else {
		goto L66;
	    }
L66:
	    sum = (r__1 = v[l], dabs(r__1));
L67:
	    ;
	}
	i__3 = n;
	for (l = 1; l <= i__3; ++l) {
	    v[l] /= sum;
/* L68: */
/* Computing 2nd power */
	    r__1 = v[l];
	    vnorm2 += r__1 * r__1;
	}
	vnorm = sqrt(vnorm2);
	i__3 = n;
	for (j = 1; j <= i__3; ++j) {
/* L69: */
	    a[j + i__ * 100] = v[j] / vnorm;
	}
/* L70: */
    }
/*    ROTATION OF CO-DIAGONAL VECTORS INTO MATRIX EIGENVECTORS           E
IGN      230*/
    i__2 = m;
    for (i__ = 1; i__ <= i__2; ++i__) {
	i__3 = n;
	for (k = 2; k <= i__3; ++k) {
	    u[k] = (float)0.;
	    i__1 = n;
	    for (j = 2; j <= i__1; ++j) {
/* L71: */
		u[k] += b[j + k * 100] * a[j + i__ * 100];
	    }
	}
	i__1 = n;
	for (j = 2; j <= i__1; ++j) {
/* L72: */
	    a[j + i__ * 100] = u[j];
	}
    }
    if (*nz == 0) {
	goto L75;
    }
/*    NORMALIZE LENGTH OF VECTORS TO EIGENVALUES AND STORE IN B(I,J)     E
IGN      239*/
    i__1 = m;
    for (j = 1; j <= i__1; ++j) {
	if (valu[j] <= (float)0.) {
	    goto L77;
	} else {
	    goto L73;
	}
L73:
	vtemp = sqrt(valu[j]);
	i__2 = n;
	for (i__ = 1; i__ <= i__2; ++i__) {
/* L74: */
	    b[i__ + j * 100] = a[i__ + j * 100] * vtemp;
	}
    }
    goto L77;
L75:
    i__2 = m;
    for (j = 1; j <= i__2; ++j) {
	i__1 = n;
	for (i__ = 1; i__ <= i__1; ++i__) {
/* L76: */
	    b[i__ + j * 100] = a[i__ + j * 100];
	}
    }
L77:
    return 0;
} /* eigen_ */




/*    MXOUT                                                              MXOT 
       2*/
/*     SUBROUTINE TO PRINT OUT COEFFICIENT MATRIX IN MATRIX FORM. */

/* Subroutine */ int mxout_(r__, n, isw, md)
real *r__;
integer *n, *isw, *md;
{
    /* Format strings */
    static char fmt_19[] = "(\0021\002,36(\002 \002),\002   O R I G I N A L \
     C O E F F I C I E N T S\002,19(\002 \002),\002PAGE NO. \002,i2,\002 OF\
 \002,i2//\0020COLUMN =   \002,18i6)";
    static char fmt_20[] = "(\0021\002,36(\002 \002),\002     D E R I V E D \
     C O E F F I C I E N T S\002,19(\002 \002),\002PAGE NO. \002,i2,\002 OF\
 \002,i2//\0020COLUMN =   \002,18i6)";
    static char fmt_21[] = "(\002 \002/\0020 ROW = \002,i3,\002 \002,18(f6.2\
))";

    /* System generated locals */
    integer i__1, i__2;

    /* Builtin functions */
    integer s_wsfe(), do_fio(), e_wsfe();

    /* Local variables */
    static integer i__, j, k, ipage, i1, i2, j1, j2;
    static real recalc;
    static integer itotal, iswtch;

    /* Fortran I/O blocks */
    static cilist io___238 = { 0, 3, 0, fmt_19, 0 };
    static cilist io___240 = { 0, 3, 0, fmt_20, 0 };
    static cilist io___242 = { 0, 3, 0, fmt_21, 0 };



/*                                                                       M
XOT        9*/
/*    *** TAPE ASSIGNMENT                                                M
XOT       10*/
    /* Parameter adjustments */
    r__ -= 101;

    /* Function Body */
    k = 18;
    i__ = *n / k;
    if (*n - i__ * k <= 0) {
	goto L2;
    } else {
	goto L1;
    }
L1:
    ++i__;
L2:
    itotal = i__ * (i__ + 1) / 2;
    ipage = 0;
    i1 = 1 - k;
L3:
    i1 += k;
    i2 = i1 + k - 1;
    if (i2 - *n <= 0) {
	goto L5;
    } else {
	goto L4;
    }
L4:
    i2 = *n;
L5:
    j1 = 1 - k;
L6:
    j1 += k;
    j2 = j1 + k - 1;
    if (j2 - *n <= 0) {
	goto L8;
    } else {
	goto L7;
    }
L7:
    j2 = *n;
L8:
    ++ipage;
    if (*isw != 0) {
	goto L10;
    } else {
	goto L9;
    }
L9:
    if (recalc == (float)0.) {
	s_wsfe(&io___238);
	do_fio(&c__1, (char *)&ipage, (ftnlen)sizeof(integer));
	do_fio(&c__1, (char *)&itotal, (ftnlen)sizeof(integer));
	i__1 = j2;
	for (j = j1; j <= i__1; ++j) {
	    do_fio(&c__1, (char *)&j, (ftnlen)sizeof(integer));
	}
	e_wsfe();
    }
    goto L11;
L10:
    if (recalc == (float)0.) {
	s_wsfe(&io___240);
	do_fio(&c__1, (char *)&ipage, (ftnlen)sizeof(integer));
	do_fio(&c__1, (char *)&itotal, (ftnlen)sizeof(integer));
	i__1 = j2;
	for (j = j1; j <= i__1; ++j) {
	    do_fio(&c__1, (char *)&j, (ftnlen)sizeof(integer));
	}
	e_wsfe();
    }
L11:
    if (j2 - i2 >= 0) {
	goto L13;
    } else {
	goto L12;
    }
L12:
    iswtch = 2;
    goto L14;
L13:
    iswtch = 1;
L14:
    i__1 = i2;
    for (i__ = i1; i__ <= i__1; ++i__) {
	switch ((int)iswtch) {
	    case 1:  goto L15;
	    case 2:  goto L16;
	}
L15:
	j2 = i__;
L16:
	if (recalc == (float)0.) {
	    s_wsfe(&io___242);
	    do_fio(&c__1, (char *)&i__, (ftnlen)sizeof(integer));
	    i__2 = j2;
	    for (j = j1; j <= i__2; ++j) {
		do_fio(&c__1, (char *)&r__[i__ + j * 100], (ftnlen)sizeof(
			real));
	    }
	    e_wsfe();
	}
    }
    switch ((int)iswtch) {
	case 1:  goto L17;
	case 2:  goto L6;
    }
L17:
    if (i2 - *n >= 0) {
	goto L18;
    } else {
	goto L3;
    }
L18:
    return 0;

/*    *** FORMAT STATEMENTS ***                                          M
XOT       44*/
/*                                                                       M
XOT       45*/
} /* mxout_ */

/*    FIT.COMPUTE MONOTONE REGRESSION VALUES ACCORDING TO KRUXKAL'S ALGO FITK 
       2*/
/* Subroutine */ int fit_(nel, dist, indi, dhat, sumd, iblk, kd)
integer *nel;
real *dist;
integer *indi;
real *dhat, *sumd;
integer *iblk, *kd;
{
    /* System generated locals */
    integer i__1;

    /* Local variables */
    static real aver;
    static integer j, nb, ii, jj, idn, iup, ipt1;

/*    PERMUTE D TO ORDER OF P                                            F
ITK        6*/
    /* Parameter adjustments */
    --iblk;
    --sumd;
    --dhat;
    --indi;
    --dist;

    /* Function Body */
    i__1 = *nel;
    for (j = 1; j <= i__1; ++j) {
	ipt1 = indi[j];
/* L1: */
	dhat[j] = dist[ipt1];
    }
    jj = 0;
    nb = 0;
L2:
    ii = jj + 1;
    jj = ii;
    if (ii > *nel) {
	goto L13;
    }
    ++nb;
    sumd[nb] = dhat[ii];
    iblk[nb] = 1;
    iup = 0;
    idn = 0;
L3:
    if (jj - *nel != 0) {
	goto L5;
    } else {
	goto L4;
    }
L4:
    iup = 1;
    goto L11;
L5:
    if (dhat[jj + 1] > dhat[jj]) {
	goto L4;
    }
    ++jj;
    ++iblk[nb];
    sumd[nb] += dhat[jj];
    aver = sumd[nb] / (real) iblk[nb];
    i__1 = jj;
    for (j = ii; j <= i__1; ++j) {
/* L6: */
	dhat[j] = aver;
    }
    idn = 0;
L7:
    if (ii - 1 != 0) {
	goto L9;
    } else {
	goto L8;
    }
L8:
    idn = 1;
    goto L11;
L9:
    if (dhat[ii] > dhat[ii - 1]) {
	goto L8;
    }
    ii -= iblk[nb - 1];
    sumd[nb - 1] += sumd[nb];
    iblk[nb - 1] += iblk[nb];
    --nb;
    aver = sumd[nb] / (real) iblk[nb];
    i__1 = jj;
    for (j = ii; j <= i__1; ++j) {
/* L10: */
	dhat[j] = aver;
    }
    iup = 0;
    goto L3;
L11:
    if (iup * idn != 0) {
	goto L2;
    } else {
	goto L12;
    }
L12:
    if (iup != 0) {
	goto L7;
    } else {
	goto L3;
    }
/*    PERMUTE D-HAT TO ORDER OF P                                        F
ITK       46*/
L13:
    i__1 = *nel;
    for (j = 1; j <= i__1; ++j) {
	ipt1 = indi[j];
/* L14: */
	sumd[ipt1] = dhat[j];
    }
    return 0;
} /* fit_ */







/* Subroutine */ int menu_(fmt, nr, mind, maxd, isim, ifd, ifc, ifglk, ifconf,
	 iffix, ifsr, miss, ife, ifg, code, cut, fmt_len)
char *fmt;
integer *nr, *mind, *maxd, *isim, *ifd, *ifc, *ifglk, *ifconf, *iffix, *ifsr, 
	*miss, *ife, *ifg;
real *code, *cut;
ftnlen fmt_len;
{
    /* Format strings */
    static char fmt_102[] = "(a80)";
    static char fmt_104[] = "(//\002 JOB TITLE ..............\002,a80)";
    static char fmt_9001[] = "(a1)";
    static char fmt_196[] = "(\002 NUMBER OF VARIABLES ....\002,i4)";
    static char fmt_198[] = "(\002 MINIMUM DIMENSIONALITY .\002,i4)";
    static char fmt_200[] = "(\002 MAXIMUM DIMENSIONALITY..\002,i4)";
    static char fmt_204[] = "(\002 COEFFICIENT TYPE ....... SIMILARITY\002)";
    static char fmt_212[] = "(\002 COEFFICIENT TYPE ....... DISSIMILARITY\
\002)";
    static char fmt_224[] = "(\002 DATA MATRIX ............ RECTANGULAR\002)";
    static char fmt_232[] = "(\002 DATA MATRIX ............ TRIANGULAR\002)";
    static char fmt_244[] = "(\002 METRIC ................. EUCLIDEAN\002)";
    static char fmt_248[] = "(\002 METRIC ................. CITY BLOCK\002)";
    static char fmt_254[] = "(\002 MONOTONICITY ........... LOCAL\002)";
    static char fmt_258[] = "(\002 MONOTONICITY ........... GLOBAL\002)";
    static char fmt_262[] = "(\002 INPUT DATA FORMAT ......\002,a80)";

    /* Builtin functions */
    integer s_rsfe(), do_fio(), e_rsfe(), s_wsfe(), e_wsfe(), s_rsle(), 
	    do_lio(), e_rsle();

    /* Local variables */
    static real recalc;
    static integer ian;
    static char ans[1];

    /* Fortran I/O blocks */
    static cilist io___251 = { 0, 11, 0, fmt_102, 0 };
    static cilist io___253 = { 0, 3, 0, fmt_104, 0 };
    static cilist io___254 = { 0, 11, 0, 0, 0 };
    static cilist io___255 = { 0, 11, 0, 0, 0 };
    static cilist io___256 = { 0, 11, 0, 0, 0 };
    static cilist io___257 = { 0, 11, 0, 0, 0 };
    static cilist io___259 = { 0, 11, 0, fmt_9001, 0 };
    static cilist io___261 = { 0, 11, 0, 0, 0 };
    static cilist io___262 = { 0, 11, 0, 0, 0 };
    static cilist io___263 = { 0, 11, 0, 0, 0 };
    static cilist io___264 = { 0, 11, 0, 0, 0 };
    static cilist io___265 = { 0, 11, 0, 0, 0 };
    static cilist io___266 = { 0, 11, 0, 0, 0 };
    static cilist io___267 = { 0, 11, 0, 0, 0 };
    static cilist io___268 = { 0, 11, 0, 0, 0 };
    static cilist io___269 = { 0, 11, 0, 0, 0 };
    static cilist io___270 = { 0, 11, 0, fmt_102, 0 };
    static cilist io___271 = { 0, 3, 0, fmt_196, 0 };
    static cilist io___272 = { 0, 3, 0, fmt_198, 0 };
    static cilist io___273 = { 0, 3, 0, fmt_200, 0 };
    static cilist io___274 = { 0, 3, 0, fmt_204, 0 };
    static cilist io___275 = { 0, 3, 0, fmt_212, 0 };
    static cilist io___276 = { 0, 3, 0, fmt_224, 0 };
    static cilist io___277 = { 0, 3, 0, fmt_232, 0 };
    static cilist io___278 = { 0, 3, 0, fmt_244, 0 };
    static cilist io___279 = { 0, 3, 0, fmt_248, 0 };
    static cilist io___280 = { 0, 3, 0, fmt_254, 0 };
    static cilist io___281 = { 0, 3, 0, fmt_258, 0 };
    static cilist io___282 = { 0, 3, 0, fmt_262, 0 };




    *ifd = 0;
    *ifc = 0;
    *iffix = 0;
    *miss = 0;
    *code = (float)0.;
    *cut = (float)0.;
/* L100: */
    s_rsfe(&io___251);
    do_fio(&c__1, fmt, 80L);
    e_rsfe();
    if (recalc == (float)0.) {
	s_wsfe(&io___253);
	do_fio(&c__1, fmt, 80L);
	e_wsfe();
    }
/* L106: */
    s_rsle(&io___254);
    do_lio(&c__3, &c__1, (char *)&(*nr), (ftnlen)sizeof(integer));
    e_rsle();
L114:
/* L108: */
    s_rsle(&io___255);
    do_lio(&c__3, &c__1, (char *)&(*mind), (ftnlen)sizeof(integer));
    e_rsle();
    if (*mind >= 0 && *mind < 10) {
	goto L116;
    }
/* L110: */
/* L112: */
    goto L114;
L116:
/* L120: */
    s_rsle(&io___256);
    do_lio(&c__3, &c__1, (char *)&(*maxd), (ftnlen)sizeof(integer));
    e_rsle();
    if (*maxd <= 10) {
	goto L122;
    }
/* L118: */
    goto L116;
L122:
/* L124: */
/* L126: */
/* L128: */
/* L130: */
    s_rsle(&io___257);
    do_lio(&c__3, &c__1, (char *)&ian, (ftnlen)sizeof(integer));
    e_rsle();
    *isim = ian - 1;
/* L132: */
    s_rsfe(&io___259);
    do_fio(&c__1, ans, 1L);
    e_rsfe();
    if (*(unsigned char *)ans == 'y' || *(unsigned char *)ans == 'Y') {
	*ifd = 1;
    }
/* L136: */
/* L138: */
    s_rsle(&io___261);
    do_lio(&c__9, &c__1, ans, 1L);
    e_rsle();
    if (*(unsigned char *)ans == 'y' || *(unsigned char *)ans == 'Y') {
	*ifglk = 1;
    }
    if (*(unsigned char *)ans == 'n' || *(unsigned char *)ans == 'N') {
	*ifglk = 0;
    }
/* L140: */
/* L142: */
    s_rsle(&io___262);
    do_lio(&c__9, &c__1, ans, 1L);
    e_rsle();
    if (*(unsigned char *)ans == 'Y' || *(unsigned char *)ans == 'y') {
	*ifconf = 1;
    }
/*      (Following line added by Malcolm Huntley 14 October 1996 */
/*       to reset value when routine is used as a translated function ) */
    if (*(unsigned char *)ans == 'N' || *(unsigned char *)ans == 'n') {
	*ifconf = 0;
    }
    if (*ifconf != 0) {
	goto L144;
    } else {
	goto L150;
    }
L144:
/* L146: */
/*      (Following two lines commed out M. D Huntley 3rd. October 1996. */
/*      no fixed input configuration now accepted */
/*      READ(11,*)ANS */
/*      IF (ANS.EQ.'Y'.OR.ANS.EQ.'y') IFFIX=1 */
    *iffix = 0;
L150:
/* L152: */
/* L154: */
/* L156: */
/* L158: */
    s_rsle(&io___263);
    do_lio(&c__3, &c__1, (char *)&ian, (ftnlen)sizeof(integer));
    e_rsle();
    *ifsr = ian - 1;
    if (*ifsr != 0) {
	goto L170;
    } else {
	goto L160;
    }
L160:
/* L162: */
    s_rsle(&io___264);
    do_lio(&c__9, &c__1, ans, 1L);
    e_rsle();
    if (*(unsigned char *)ans == 'Y' || *(unsigned char *)ans == 'y') {
	*miss = 1;
    }
/*      Inserted by Malcolm Huntley 22 October 1996 */
    if (*(unsigned char *)ans == 'N' || *(unsigned char *)ans == 'n') {
	*miss = 0;
    }
    if (*miss != 0) {
	goto L164;
    } else {
	goto L170;
    }
L164:
/* L166: */
    s_rsle(&io___265);
    do_lio(&c__4, &c__1, (char *)&(*code), (ftnlen)sizeof(real));
    e_rsle();
L170:
/* L172: */
/* L174: */
    s_rsle(&io___266);
    do_lio(&c__9, &c__1, ans, 1L);
    e_rsle();
    if (*(unsigned char *)ans == 'Y' || *(unsigned char *)ans == 'y') {
	*ife = 1;
    }
    if (*(unsigned char *)ans == 'N' || *(unsigned char *)ans == 'n') {
	*ife = 0;
    }
/* L176: */
    s_rsle(&io___267);
    do_lio(&c__9, &c__1, ans, 1L);
    e_rsle();
    if (*(unsigned char *)ans == 'Y' || *(unsigned char *)ans == 'y') {
	*ifg = 1;
    }
    if (*(unsigned char *)ans == 'N' || *(unsigned char *)ans == 'n') {
	*ifg = 0;
    }
    if (*ifg >= 0) {
	goto L190;
    } else {
	goto L180;
    }
L180:
    if (*iffix - 1 != 0) {
	goto L182;
    } else {
	goto L190;
    }
L182:
/* L184: */
    s_rsle(&io___268);
    do_lio(&c__9, &c__1, ans, 1L);
    e_rsle();
    ian = 0;
    if (*(unsigned char *)ans == 'Y' || *(unsigned char *)ans == 'y') {
	ian = 1;
    }
    if (ian != 0) {
	goto L190;
    } else {
	goto L186;
    }
L186:
/* L188: */
    s_rsle(&io___269);
    do_lio(&c__4, &c__1, (char *)&(*cut), (ftnlen)sizeof(real));
    e_rsle();
L190:
/* L192: */
/* L194: */
    s_rsfe(&io___270);
    do_fio(&c__1, fmt, 80L);
    e_rsfe();
    if (recalc == (float)0.) {
	s_wsfe(&io___271);
	do_fio(&c__1, (char *)&(*nr), (ftnlen)sizeof(integer));
	e_wsfe();
    }
    if (recalc == (float)0.) {
	s_wsfe(&io___272);
	do_fio(&c__1, (char *)&(*mind), (ftnlen)sizeof(integer));
	e_wsfe();
    }
    if (recalc == (float)0.) {
	s_wsfe(&io___273);
	do_fio(&c__1, (char *)&(*maxd), (ftnlen)sizeof(integer));
	e_wsfe();
    }
    if (*isim != 0) {
	goto L202;
    } else {
	goto L210;
    }
L202:
    if (recalc == (float)0.) {
	s_wsfe(&io___274);
	e_wsfe();
    }
    goto L220;
L210:
    if (recalc == (float)0.) {
	s_wsfe(&io___275);
	e_wsfe();
    }
L220:
    if (*ifsr != 0) {
	goto L222;
    } else {
	goto L230;
    }
L222:
    if (recalc == (float)0.) {
	s_wsfe(&io___276);
	e_wsfe();
    }
    goto L240;
L230:
    if (recalc == (float)0.) {
	s_wsfe(&io___277);
	e_wsfe();
    }
L240:
    if (*ife != 0) {
	goto L246;
    } else {
	goto L242;
    }
L242:
    if (recalc == (float)0.) {
	s_wsfe(&io___278);
	e_wsfe();
    }
    goto L250;
L246:
    if (recalc == (float)0.) {
	s_wsfe(&io___279);
	e_wsfe();
    }
L250:
    if (*ifg != 0) {
	goto L252;
    } else {
	goto L256;
    }
L252:
    if (recalc == (float)0.) {
	s_wsfe(&io___280);
	e_wsfe();
    }
    goto L260;
L256:
    if (recalc == (float)0.) {
	s_wsfe(&io___281);
	e_wsfe();
    }
L260:
    if (recalc == (float)0.) {
	s_wsfe(&io___282);
	do_fio(&c__1, fmt, 80L);
	e_wsfe();
    }
    return 0;
} /* menu_ */




/* Subroutine */ int confin_(mind, nr, maxd, x, cmean, jone)
integer *mind, *nr, *maxd;
real *x, *cmean;
integer *jone;
{
    /* Format strings */
    static char fmt_328[] = "(10f8.3)";

    /* System generated locals */
    integer i__1, i__2;

    /* Builtin functions */
    integer s_rsfe(), do_fio(), e_rsfe();

    /* Local variables */
    static integer i__, j, k;
    static real fnr;

    /* Fortran I/O blocks */
    static cilist io___285 = { 0, 2, 0, fmt_328, 0 };



/* *************************************************************** */
/* *                     INPUT CONFIGURATION                     * */
/* *************************************************************** */


    /* Parameter adjustments */
    --cmean;
    x -= 102;

    /* Function Body */
    fnr = (real) (*nr);
    if (*mind == 0) {
	*mind = 1;
    }
    i__1 = *nr;
    for (i__ = 1; i__ <= i__1; ++i__) {
	s_rsfe(&io___285);
	i__2 = *maxd;
	for (j = 1; j <= i__2; ++j) {
	    do_fio(&c__1, (char *)&x[i__ + j * 101], (ftnlen)sizeof(real));
	}
	e_rsfe();
/* L3: */
    }

/*    SET COORDINATE MEANS TO ZERO                                       S
SA1       52*/

    i__1 = *maxd;
    for (j = 1; j <= i__1; ++j) {
	cmean[j] = (float)0.;
	i__2 = *nr;
	for (i__ = 1; i__ <= i__2; ++i__) {
	    cmean[j] += x[i__ + j * 101];
/* L4: */
	}
	cmean[j] /= fnr;
	i__2 = *nr;
	for (k = 1; k <= i__2; ++k) {
	    x[k + j * 101] -= cmean[j];
/* L5: */
	}
/* L6: */
    }


    return 0;
} /* confin_ */




/* Subroutine */ int initcon_(nr, maxd, miss, ifsr, isw, md, fmt, prox, c__, 
	isten, eval, nel, fmt_len)
integer *nr, *maxd, *miss, *ifsr, *isw, *md;
char *fmt;
real *prox, *c__;
integer *isten;
real *eval;
integer *nel;
ftnlen fmt_len;
{
    /* Format strings */
    static char fmt_324[] = "(\0021\002)";
    static char fmt_341[] = "(\002 G-L COORDINATES FOR CONDITIONAL FIT (SEMI\
-STRONG MONOTONICITY).\002/\002 DIMENSION\002,10i10)";
    static char fmt_337[] = "(\002 *\002,110x,\002 G-L'S K ITERATIONS.\002)";
    static char fmt_342[] = "(\002 \002,120(\002-\002)/\0020VARIABLE\002)";

    /* System generated locals */
    integer i__1;
    cilist ci__1;

    /* Builtin functions */
    integer s_wsfe(), e_wsfe(), do_fio(), s_rsfe(), e_rsfe();

    /* Local variables */
    static real code;
    static integer mind, j, id, jj;
    static real recalc;
    static integer mm, mr, ind;
    static real fnr;
    static integer nrp1;

    /* Fortran I/O blocks */
    static cilist io___294 = { 0, 3, 0, fmt_324, 0 };
    static cilist io___295 = { 0, 3, 0, fmt_341, 0 };
    static cilist io___297 = { 0, 3, 0, fmt_337, 0 };
    static cilist io___298 = { 0, 3, 0, fmt_342, 0 };



    /* Parameter adjustments */
    --eval;
    --isten;
    c__ -= 101;
    --prox;

    /* Function Body */
    *nel = *nr;
    fnr = (real) nrp1;
    ind = *nr;
    mind = *maxd;
    mr = *miss;
    if (recalc == (float)0.) {
	s_wsfe(&io___294);
	e_wsfe();
    }
    if (recalc == (float)0.) {
	s_wsfe(&io___295);
	i__1 = *maxd;
	for (mm = 1; mm <= i__1; ++mm) {
	    do_fio(&c__1, (char *)&mm, (ftnlen)sizeof(integer));
	}
	e_wsfe();
    }
    if (recalc == (float)0.) {
	s_wsfe(&io___297);
	e_wsfe();
    }
    if (recalc == (float)0.) {
	s_wsfe(&io___298);
	e_wsfe();
    }

/*    INPUT COEFFICIENTS FOR ADDED VARIABLE                              S
SA1       72*/

/* L9: */
    if (*ifsr != 0) {
	goto L10;
    } else {
	goto L12;
    }
L10:
    if (*miss == 0) {
	goto L16;
    }

/*    COUNT NUMBER OF NON-MISSING ELEMENTS                               S
SA1       76*/

    jj = 0;
    i__1 = *nr;
    for (j = 1; j <= i__1; ++j) {
	if (isten[j] == 1) {
	    goto L11;
	}
	++jj;
	prox[jj] = prox[j];
L11:
	;
    }
    goto L15;
L12:
    ci__1.cierr = 0;
    ci__1.ciend = 0;
    ci__1.ciunit = 1;
    ci__1.cifmt = fmt;
    s_rsfe(&ci__1);
    do_fio(&c__1, (char *)&id, (ftnlen)sizeof(integer));
    i__1 = *nr;
    for (j = 1; j <= i__1; ++j) {
	do_fio(&c__1, (char *)&prox[j], (ftnlen)sizeof(real));
    }
    e_rsfe();
    if (*miss == 0) {
	goto L16;
    }

/*    GENERATE PASSIVE CELL STENCIL                                      S
SA1       87*/

    jj = 0;
    i__1 = *nr;
    for (j = 1; j <= i__1; ++j) {
	if (prox[j] - code != (float)0.) {
	    goto L13;
	} else {
	    goto L113;
	}
L113:
	isten[j] = 1;
	goto L14;
L13:
	++jj;
	prox[jj] = prox[j];
	isten[j] = 0;
L14:
	;
    }
L15:
    *nel = jj;
L16:
    i__1 = *nel;
    for (j = 1; j <= i__1; ++j) {
/* L17: */
	eval[j] = prox[j];
    }

    return 0;
} /* initcon_ */

