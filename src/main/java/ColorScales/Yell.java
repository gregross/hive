//==============================================================================
// BTC ColorRGB SCALE Class
//
//   Thrasos Papas, AT&T Bell Labs, Murray Hill, NJ
// 
// AGG - Alexander Gee
//
// 041497 - created
//==============================================================================

package ColorScales;

import java.awt.Color;

public class Yell
{
	static public Color[] rgb = new Color[256];

	static
	{
		for (int i = 0; i < 255; i++)
			rgb[i] = new Color(   255,   255,   0 );
	}
}

