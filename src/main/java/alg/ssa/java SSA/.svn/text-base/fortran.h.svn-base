/*************************** FORTRAN.H **********************************/
/*						                                                       */                            
/* MALCOLM HUNTLEY                                                       */
/* UNIVERSITY OF LIVERPOOL                                               */
/* 12 AUGUST 1997                                                     */
/*                                                                       */
/* COPYRIGHT MALCOLM HUNTLEY, DAVID CANTER & THE UNIVERITY               */
/* OF LIVERPOOL 1997.                                                    */
/*                                                                       */
/*************************************************************************/

/* This file contains preproc directives to enable use of the
definitions and prototypes in LIFE library FORTRAN.LIB */

// global defines
#define RESET 101
#define VECTOR_LENGTH 100
// end global defines

 

#ifdef __cplusplus
extern "C" {
#endif


typedef struct {
   bool exists;
   bool recognized_fmt;
   bool isdichot;
   bool isbinary;
   double hival;
	int ncols;
   int nrows;
   int shape;    // square 0, rectangle 1, low triangle 2
} fileformat;

// function prototypes
fileformat inquire_file_format(char *);
int get_nrows(char *);
void printf_fortran(double, int, int);
void fprintf_fortran(FILE *, double, int, int);
void sprintf_fortran(char *, double, int, int);
int write_fortran(char *, char *, fileformat, int, int);
int write_subs_fortran(char *, char *, fileformat, int, int, int *);
int deselect(int *, int); // sets unselect flags in selection vector
// end function prototypes



#ifdef __cplusplus
}
#endif
