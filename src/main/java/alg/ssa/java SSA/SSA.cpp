#include "stdafx.h"
#include "ssalib.h"
#include "fortran.h"

// Data structure for holding SSA parameters.

	typedef struct 
	{
		char filename[32];
		char outfile[12];
		char title[80];
		int numvars;
		int mind;
		int maxd;
		char sim_dissim;
		char plots_dist;
		char min_alien;
		char predef_inp;
		int matrix_shape;
		char metric;
		char local_mono;
		char fnote[80];
		char dichot;
		int coef;                // These two are
		char missing_data;       // mutually exclusive
	} ssa_parameters;

class SSA
{
	public:
		~SSA()
		{
			delete [] selection_vector;
		}
	protected:
		ssa_parameters params;
		char filename[32];
		fileformat fmt;
		int* selection_vector;

		void run();
};

void SSA::run()
{
	strcpy(filename, "C:\\Documents and Settings\\Greg Ross\\Desktop\\SSAdll\\msv.dat");
	fileformat fmt = inquire_file_format(filename);

	selection_vector = new int[100];
	deselect(selection_vector, RESET);
}