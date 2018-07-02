/**
 * Algorithmic test bed
 *
 * ValidateTriggerField
 *
 * This class extends PlainDocument to ensure that the user can only
 * enter valid values into the text box for determining the scalar
 * threshold values for spring model converegence
 *
 *@author Greg Ross
 */
 
 package alg.springModel;
 
import javax.swing.*; 
import javax.swing.text.*; 
import java.awt.Toolkit;
import java.text.*;
import java.util.Locale;
 
public class ValidateTriggerField extends PlainDocument implements java.io.Serializable
{
	// Versioning for serialisation
	
	static final long serialVersionUID = 50L;
	
	private Format format;
	
	public ValidateTriggerField(Format f) 
	{
        	format = f;
	}
	
	public Format getFormat() 
	{
        	return format;
	}
	
	public void insertString(int offs, String str, AttributeSet a) 
        					throws BadLocationException 
	{
		char[] source;
		
		if (format != null)
		{
			// The field should only accept int/double numbers
			
        		String currentText = getText(0, getLength());
			String beforeOffset = currentText.substring(0, offs);
			String afterOffset = currentText.substring(offs, currentText.length());
			String proposedResult = beforeOffset + str + afterOffset;
			
			boolean bContinue = true;
			
			// Allow only one decimal point to be entered
			
			if (currentText.indexOf(".") > -1)
			{
				if (str.equals("."))
					bContinue = false;
			}
			
			// Ensure that no other non-numeric characters can be entered
			
			source = str.toCharArray();
			
			if (bContinue)
				if (!Character.isDigit(source[0]) && (!str.equals(".")))
					bContinue = false;
			
			if (bContinue)
			{
				try 
				{
					format.parseObject(proposedResult);
					super.insertString(offs, str, a);
				} 
				catch (ParseException e){}
			}
		}
		else
		{
			// The field should only accept integer numbers
			
			source = str.toCharArray();
			char[] result = new char[source.length];
			int j = 0;
			
			for (int i = 0; i < result.length; i++) 
			{
				if (Character.isDigit(source[i]))
					result[j++] = source[i];
			}
			
			// Make sure that the number is not too large
			
			try
			{
				if (getLength() > 1)
					Integer.parseInt(getText(0, getLength()));
					
				super.insertString(offs, new String(result, 0, j), a);
			}
			catch (java.lang.Exception e){}
		}
	}
	
	public void remove(int offs, int len) throws BadLocationException 
	{
		if (format != null)
		{
			// The field should only accept int/double numbers
			
        		String currentText = getText(0, getLength());
			String beforeOffset = currentText.substring(0, offs);
			String afterOffset = currentText.substring(len + offs,currentText.length());
                        
			String proposedResult = beforeOffset + afterOffset;
			
			try 
			{
				if (proposedResult.length() != 0)
					format.parseObject(proposedResult);
					
				super.remove(offs, len);
			} 
			catch (ParseException e){}
		}
		else
		{
			super.remove(offs, len);
		}
	}
}
