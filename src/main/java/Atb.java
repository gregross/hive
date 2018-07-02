/**
 * Algorithmic test bed
 *
 * ATB
 *
 * Main file to be executed. Opens the mdi GUI frame.
 *
 *  @author Greg Ross
 */
  
 import parent_gui.Mdi;
 
 import javax.swing.UIManager;
 
 class Atb
{
    public static void main( String[] args)
    {
	// Set the windows look and feel.
	
	try
	{
		String plafName = UIManager.getSystemLookAndFeelClassName();
		UIManager.setLookAndFeel(plafName);
	}
	catch (Exception e){}
		
	// If the application has been opened by double-clicking a file then get
	// the file path and name.
		
	String fileName = null;	
	if (args.length > 0)
	{
		fileName = "werwe";
	}
	new Mdi(fileName);
    }
}
