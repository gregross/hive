/* ***** BEGIN LICENSE BLOCK *****
 * Version: MPL 1.1/GPL 2.0/LGPL 2.1
 *
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * The Original Code is HIVE .
 *
 * The Initial Developer of the Original Code is
 * Greg Ross.
 * Portions created by the Initial Developer are Copyright (C) 2000-2004
 * the Initial Developer. All Rights Reserved.
 *
 * Contributor(s): Greg Ross <gr@dcs.gla.ac.uk>
 		   Matthew Chalmers <matthew@dcs.gla.ac.uk>
 *                 Alistair Morrison <morrisaj@dcs.gla.ac.uk>
 *		   Andrew Didsbury
 *           		
 *	
 *
 * Alternatively, the contents of this file may be used under the terms of
 * either the GNU General Public License Version 2 or later (the "GPL"), or
 * the GNU Lesser General Public License Version 2.1 or later (the "LGPL"),
 * in which case the provisions of the GPL or the LGPL are applicable instead
 * of those above. If you wish to allow use of your version of this file only
 * under the terms of either the GPL or the LGPL, and not to allow others to
 * use your version of this file under the terms of the MPL, indicate your
 * decision by deleting the provisions above and replace them with the notice
 * and other provisions required by the GPL or the LGPL. If you do not delete
 * the provisions above, a recipient may use your version of this file under
 * the terms of any one of the MPL, the GPL or the LGPL.
 *
 * ***** END LICENSE BLOCK ***** */
/**
 * Algorithmic testbed
 *
 * ValidTextFieldDocument
 *
 * Class to define how text boxes deal with entering numeric data
 *
 *  @author Greg Ross
 */
package parent_gui.dataVolumeThresholding;

import javax.swing.JTextField;
import javax.swing.text.*;
import javax.swing.undo.CannotUndoException;

public class ValidTextFieldDocument extends PlainDocument
{
	private int maxValue;
	private int minValue;
	private JTextField txtBox;
	
	private boolean bUseRange = false;
	
	// If the user selects all of the text and then deletes it, either
	// by overwriting or explicit deletion, the following is set to true
	
	private boolean bSelectAllAndReplace = false;
	
	// If the above is true and the user is trying to replace the text with
	// an invalid character, e.g. space, then we need to store the removed
	// text so that the removal change can be undone
	
	private AbstractDocument.DefaultDocumentEvent delTextEvent;
	
	public ValidTextFieldDocument(int maxValue, int minValue, JTextField txtBox)
	{
		bUseRange = true;
		
		// The maximum allowable value that the text box can hold
		
		this.maxValue = maxValue;
		
		// The minimum allowable value that the text box can hold
		
		this.minValue = minValue;
		
		// Reference to the text box per se
		
		this.txtBox = txtBox;
	}
	
	public ValidTextFieldDocument(JTextField txtBox)
	{
		bUseRange = false;
		
		// Reference to the text box per se
		
		this.txtBox = txtBox;
	}
	
	public void insertString(int offs, String str, AttributeSet a) throws BadLocationException
	{
		// Allow only numerals to be entered
		
		char[] source = str.toCharArray();
		char[] result = new char[source.length];
		int j = 0;
		
		for (int i = 0; i < result.length; i++) 
		{
			if (Character.isDigit(source[i]))
				result[j++] = source[i];
		}
		
		// don't allow the first number to be 0
		
		if ((offs == 0) && str.equals("0"))
		{
			if (bSelectAllAndReplace)
				if (delTextEvent != null)
					delTextEvent.undo();
			
			return;
		}
		
		// Exit here if a non-numeric entry was attempted.
		// Even though the text was blocked, a parse exception would
		// still arise
		
		if ((txtBox.getText() + new String(result, 0, j)).equals(""))
		{
			if (bSelectAllAndReplace)
				if (delTextEvent != null)
					delTextEvent.undo();
			
			return;
		}
		
		try
		{
			if (bUseRange)
			{
				// Don't allow the number to be larger than maxValue
				
				if (Integer.parseInt(txtBox.getText() + new String(result, 0, j)) < maxValue)
					super.insertString(offs, new String(result, 0, j), a);
				
				// Don't allow the number to be smaller than minValue
				
				if (Integer.parseInt(txtBox.getText() + new String(result, 0, j)) > minValue)
					super.insertString(offs, new String(result, 0, j), a);
			}
			else
			{
				// Default maximum should be the biggest possible integer
				
				if (Integer.parseInt(txtBox.getText() + new String(result, 0, j)) < Integer.MAX_VALUE)
					super.insertString(offs, new String(result, 0, j), a);
			}
		}
		catch (java.lang.Exception e){}
	}
	
	protected void removeUpdate(AbstractDocument.DefaultDocumentEvent chng)
	{
		if (chng.getLength() == txtBox.getText().length())
		{	
			bSelectAllAndReplace = true;
			delTextEvent = chng;
		}
		else
		{
			bSelectAllAndReplace = false;
			delTextEvent = null;
		}
		
		super.removeUpdate(chng);
	}
	
	/**
	* Accessor method for determining the text box that uses this document
	*/
	
	public JTextField getSource()
	{
		return txtBox;
	}
}
