/**************************** FORTRAN.CPP ********************************/
/*						                                                       */                            
/* MALCOLM HUNTLEY                                                       */
/* UNIVERSITY OF LIVERPOOL                                               */
/* 29 OCTOBER 1966                                                       */
/*                                                                       */
/* COPYRIGHT MALCOLM HUNTLEY, DAVID CANTER & THE UNIVERSITY              */
/* OF LIVERPOOL 1996.                                                    */
/*                                                                       */
/*************************************************************************/

/* component of FORTRAN.LIB | part of the Livepool University, department
   Psychology LIFE multivariate data analysis and visualization package  */

#include <iostream>
#include <fstream>
#include <string>
#include <strstream>
#include <stdio.h>
#include <math.h>
#include "fortran.h"

using namespace std;


// this file contains library functions for reading and manipulating SSV
// and FORTRAN format files


fileformat inquire_file_format(char *filename)
{
   fileformat fmt;

  // set default values for fmt members
   fmt.recognized_fmt = true;
   fmt.isdichot = true;
   fmt.isbinary = true;
	fmt.ncols = 0;
   fmt.nrows = 0;
   fmt.shape = 0;    //  rectangle 1, low triangle 2

   // open the file
   ifstream is(filename);

	if(!is) {                    // file unavailable
   	fmt.exists = false;
		return fmt;
	}
   int count = 0;
   double firstval, secondval;
   bool oneval = false, twoval = false;;
   int oldlength = 0, maxline = 0, ncols = 0;
	char linebuf[999];
	while (is.getline(linebuf, 999)) {
   	if (is.gcount() > 1) {  // not 0; gcount retains the linefeed
			fmt.nrows++;
         double val;
         istrstream istr(linebuf);  // declare a stream object
         while (istr >> val){
            if (val != 0.0 && val !=1.0) fmt.isdichot																																																																												 = false;
            ncols++;
            if (!oneval) {
              firstval = val;
              oneval = true;
            }
            if (oneval && !twoval && val != firstval) {
              secondval = val;
              twoval = true;
            }
            if (twoval && val != firstval && val != secondval)
              fmt.isbinary = false;
         }
         if (fmt.nrows == 1) {
           oldlength = ncols;  // length of the first line
           if (ncols == 1) fmt.shape = 2;  // assume triangular
           else fmt.shape = 1; // assume rectangular
         }
         if (fmt.shape == 1){
         	if(ncols == oldlength) fmt.shape = 1; // confirm rectangular
            else fmt.shape = 4;
         }
         if (fmt.shape == 2) {
         	 if (ncols == fmt.nrows) fmt.shape = 2; // confirm lower triangular
             else fmt.shape = 4;
         }
         if (ncols > maxline)  maxline = ncols;
         ncols = 0;
      }
   }
   // for binary data, find the higher value
   if (fmt.isbinary) {
     if (firstval > secondval) fmt.hival = firstval;
     else fmt.hival = secondval;
   }


   fmt.ncols = maxline;
   if (fmt.shape == 4) fmt.recognized_fmt = false;
   return fmt;
}


int get_nrows(char *filename)
{

	ifstream is(filename);

	if(!is) {                    // file unavailable
   	// fmt.exists = false;
		return 0;
	}
	char linebuf[999];

	int lines = 0;

	while (is.getline(linebuf, 999)) {
   	if (is.gcount() > 1) lines++;     // not 0; gcount retains the linefeed
	}
   return lines;
}



// Fortram format printing routines


void printf_fortran(double realnum, int width, int decimal)
{
  // converts a real number to a fortran input with format
  // code given by (FINTEGER.DECIMAL)
  char fm[80];
  sprintf(fm,"%%%d.%df", width + 1 - decimal, decimal);
  char numstring[80];
  sprintf(numstring, fm, realnum);
  char outstring[80];

  for (int i = 0, j = 0; i < strlen(numstring); i++)
     if (numstring[i] != '.') {
     		outstring[j] = numstring[i];
         j++;
     }
     outstring[strlen(numstring)-1] = 0;

  printf("%s", outstring);
}

void sprintf_fortran(char* outstring, double realnum, int width, int decimal)
{
  // converts a real number to a fortran input with format
  // code given by (FINTEGER.DECIMAL)
  char fm[80];
  sprintf(fm,"%%%d.%dlf", width + 1, decimal);
  char numstring[80];
  sprintf(numstring, fm, realnum);
  for (int i = 0, j = 0; i < strlen(numstring); i++)
     if (numstring[i] != '.') {
     		outstring[j] = numstring[i];
         j++;
     }
     outstring[strlen(numstring)-1] = 0;
}

void fprintf_fortran(FILE* fp, double realnum, int width, int decimal)
{
  // converts a real number to a fortran input with format
  // code given by (FINTEGER.DECIMAL)
  char fm[80];
  sprintf(fm,"%%%d.%df", width + 1, decimal);
  char numstring[80];
  sprintf(numstring, fm, realnum);
  char outstring[80];

  for (int i = 0, j = 0; i < strlen(numstring); i++)
     if (numstring[i] != '.') {
     		outstring[j] = numstring[i];
         j++;
     }
     outstring[strlen(numstring)-1] = 0;

  fprintf(fp, "%s", outstring);
}




int write_fortran(char* infile, char* outfile, fileformat fmt, int w, int d)
{
 // converts INFILE into a file of values in formatted as (nw.dF)
 // in FORTRAN convention format

 FILE* ifp;
 FILE* ofp;

 ifp = fopen(infile, "r");
 ofp = fopen(outfile, "w");

 if (!ifp || !ofp) return 0; // file error

 double buf;
 for (int i = 0; i < fmt.nrows; i++) {
   // for rectangular data matrices
   if (fmt.shape == 1) {
 		for (int j = 0; j < fmt.ncols; j++) {
      	fscanf(ifp, "%lf", &buf);
         if (fmt.isbinary && buf - fmt.hival < 0) buf = 0;
         else if (fmt.isbinary) buf = 1;
   		fprintf_fortran(ofp, buf, w, d);
      }
      fprintf(ofp, "\n");
   }

   if (fmt.shape == 2) {
   // for triangular data
   	for(int j = 0; j < i + 1; j++) {
      	fscanf(ifp, "%lf", &buf);
         if (fmt.isbinary && buf - fmt.hival < 0) buf = 0;
         else if (fmt.isbinary) buf = 1;
   		fprintf_fortran(ofp, buf, w, d);
      }
      fprintf(ofp, "\n");
   }
 }
 if (fmt.shape == 4) return 0; // unrecognized format
 fclose(ofp);
 fclose(ifp);
 // normal exit
 return 1;
}


int write_subs_fortran(char* infile, char* outfile, fileformat fmt,
                                    int w, int d, int* selection_vector)
{
 // converts INFILE into a file of values in formatted as (nw.dF)
 // in FORTRAN convention format

 FILE* ifp;
 FILE* ofp;

 ifp = fopen(infile, "r");
 ofp = fopen(outfile, "w"); // append because configuration already written

 if (!ifp || !ofp) return 0; // file error

 double buf;
 int deselected = 0;
 for (int i = 0; i < fmt.nrows; i++) {
   // for rectangular data matrices
   if (fmt.shape == 1) {
		for (int j = 0; j < fmt.ncols; j++) {
         fscanf(ifp, "%lf", &buf);
         // test selection vector
         if (!selection_vector[j]) {
            deselected++;
         	continue;
         }

         if (fmt.isbinary && buf - fmt.hival < 0) buf = 0;
         else if (fmt.isbinary) buf = 1;
   		fprintf_fortran(ofp, buf, w, d);
      }
      fprintf(ofp, "\n");
   }

   if (fmt.shape == 2) {
   // for triangular data
     // test selection vector
      if (!selection_vector[i]){
            deselected++;
         	continue;
      }
   	for(int j = 0; j < i + 1; j++) {
      	fscanf(ifp, "%lf", &buf);
         // test selection vector
         if (!selection_vector[j]) {
         // fmt.ncols--; // deselected both rows and columns
         	continue;
         }
         if (fmt.isbinary && buf - fmt.hival < 0) buf = 0;
         else if (fmt.isbinary) buf = 1;
   		fprintf_fortran(ofp, buf, w, d);
      }
      fprintf(ofp, "\n");
   }
 }
 if (fmt.shape == 4) return 0; // unrecognized format
 // adjust the row count for ssa processing
 fmt.ncols -= deselected;
 // deselected the rows in the case of triangular data matrices
 if (fmt.shape == 2) fmt.nrows -= deselected;

 fclose(ofp);
 fclose(ifp);
 
 // normal exit
 return deselected;
}


int deselect(int *svector, int n)
{
	 int i;
	 // sets unselect flags in selection vector
    if (n == RESET) {
    	for(i = 0; i < VECTOR_LENGTH; i++) svector[i] = 1;
      return 0;
    }
    else svector[n] = 0;
    return n;
}














