/*********************************************************************

COPYRIGHT (C): 2010
PROJECT: Program01
FILE: NumberConverter.java
PURPOSE: To convert a number from decimal to binary and vice versa.
COMPILER: Java J2EE 1.6
TARGET: Java Runtime Environment (multiplatform)
PROGRAMMER(S): William Agnew (agnewwt@muohio.edu)
INSTRUCTOR: Kurt Johnson (COURSE: CSE278 HA)
COURSE: CSE278 HA
START DATE: Sep 8, 2010
DUE DATE: Sep 9, 2010

 *********************************************************************/

import java.awt.event.*;
import java.awt.*;
import javax.swing.*;

/**
 * Converts a number from decimal to binary and vice versa.
 * @author William Agnew
 *
 */
public class NumberConverter extends JFrame implements ActionListener
{
	public static final int WINDOWWIDTH = 400;
	public static final int WINDOWHEIGHT = 300;
	
	/**
	 * The maximum number of places after the decimal point
	 */
	public static final int ACCURACY = 12;
	
	/**
	 * Used to keep track of conversion mode.
	 */
	public static final int DECTOBINMODE = 0, BINTODECMODE = 1;
	
	/**
	 * Stores conversion mode.
	 */
	private int conversionMode = DECTOBINMODE;
	
	/**
	 * Keeps track of whether number is a float.
	 */
	private boolean isFloat = false;
	
	/**
	 * For determining when there is an error.
	 */
	private boolean isError = false;
	
	/**
	 * Stores the number or sections of the number during conversion.
	 */
	String convertedNumber = "", leftOfDecimalPoint = "", rightOfDecimalPoint = "";
	
	//JButtons
	private JButton convertButton;
	private JButton clearButton;
	private JRadioButton decToBinRadioButton;
	private JRadioButton binToDecRadioButton;
	
	//JTextFields and JTextAreas
	private JTextField numberTextField;
	//private JTextArea logTextArea;  // Feature not implemented
	
	/**
	 * Creates Number Converter Window.
	 */
	public NumberConverter()
	{
		setTitle("Number Converter");
		setSize(WINDOWWIDTH, WINDOWHEIGHT);
		//setResizable(false);
		setLocation(100, 100);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		// Initialize the panel
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		
		// Initialize and add the components
		numberTextField = new JTextField(30);
		setDisplay("");
		numberTextField.setHorizontalAlignment(JTextField.RIGHT);
		numberTextField.setCaretPosition(numberTextField.getDocument().getLength());
		numberTextField.addActionListener(new NumberTextFieldHandler());

		c.gridwidth = 3;
		c.gridx = 0;
		c.gridy = 0;
		c.insets = new Insets(25, 0, 5, 0);
		add(numberTextField, c);
		
		decToBinRadioButton = new JRadioButton("Dec to Bin");
		decToBinRadioButton.addActionListener(this);
		decToBinRadioButton.setSelected(true);
		c.gridwidth = 1;
		c.gridx = 1;
		c.gridy = 1;
		c.insets = new Insets(10, 0, 0, 0);
		add(decToBinRadioButton, c);
		
		binToDecRadioButton = new JRadioButton("Bin to Dec");
		binToDecRadioButton.addActionListener(this);
		c.gridx = 1;
		c.gridy = 2;
		c.insets = new Insets(0, 0, 0, 0);
		add(binToDecRadioButton, c);

		ButtonGroup buttonGroup = new ButtonGroup();
		buttonGroup.add(decToBinRadioButton);
		buttonGroup.add(binToDecRadioButton);
		
		convertButton = new JButton("Convert");
		convertButton.addActionListener(this);
		convertButton.setPreferredSize(new Dimension(78, 25));
		c.gridx = 2;
		c.gridy = 1;
		c.insets = new Insets(10, 10, 5, 0);
		c.anchor = GridBagConstraints.LINE_END;
		add(convertButton, c);
		
		clearButton = new JButton("Clear");
		clearButton.addActionListener(this);
		clearButton.setPreferredSize(new Dimension(78, 25));
		c.gridx = 2;
		c.gridy = 2;
		c.insets = new Insets(0, 10, 0, 0);
		add(clearButton, c);
		
		// To align all components to the top
		JLabel tmpLabel1 = new JLabel("                              ");
		c.gridx = 0;
		c.gridy = 3;
		c.insets = new Insets(0, 0, 0, 0);
		c.weighty = 1.0;

		add(tmpLabel1, c);	
		
	}
	
	/**
	 * Inherited abstract method from ActionListener.  The main event handler.
	 * @param e the event fired
	 * {@inheritDoc}
	 */
	public void actionPerformed(ActionEvent e)
	{
		if(e.getActionCommand().equals("Dec to Bin"))
		{
			convert();
			setConversionMode(DECTOBINMODE);
		}
		
		else if(e.getActionCommand().equals("Bin to Dec"))
		{
			convert();
			setConversionMode(BINTODECMODE);
		}
		
		else if(e.getActionCommand().equals("Convert"))
		{
			convert();
		}
		
		else if(e.getActionCommand().equals("Clear"))
		{
			setDisplay("");
			numberTextField.requestFocusInWindow();  // Return focus to the display for usability
		}
		
		else System.exit(0);
	}
	
	/**
	 * Separate event handler for pressing the enter key while in the text field.
	 */
	private class NumberTextFieldHandler implements ActionListener
	{
		/**
		 * @param e the event fired, should be enter key pressed
		 * 
		 * {@inheritDoc}
		 */
		public void actionPerformed(ActionEvent e)
		{
			convert();
		}
	}
	
	/**
	 * Performs tasks prior to actual conversion, decides conversion mode and calls associated method.<br>
	 * Called by:  actionPerformed(ActionEvent)
	 */
	private void convert()
	{
		// Reset variables
		setConvertedNumber("");
		setLeftOfDecimalPoint("");
		setRightOfDecimalPoint("");
		setFloat(false);
		setError(false);
		
		//  Determine whether number has a decimal point and if so, separate the whole from the fractional
		for(int i = 0; i < getDisplay().length(); i++)
		{
			if(getDisplay().charAt(i) == '.')
			{
				setFloat(true);
				if(i == 0)  // Add a leading 0 if not already, for consistency
				{
					setLeftOfDecimalPoint("0");  
				}
			}
			else if(isFloat()) 
			{
				setRightOfDecimalPoint(getRightOfDecimalPoint() + getDisplay().charAt(i));
			}
			else
			{
				setLeftOfDecimalPoint(getLeftOfDecimalPoint() + getDisplay().charAt(i));
			}
		}
		
		trimDisplay();
		
		switch(getConversionMode())
		{
			case DECTOBINMODE:
				if(!isDisplayBlank()) convertDecToBin();
				break;
			case BINTODECMODE:
				if(!isDisplayBlank()) convertBinToDec();
				break;
			default:
				break;
		}
		
		numberTextField.requestFocusInWindow();  // Return focus to the display for usability
	}
	
	/**
	 * Converts the number from decimal format to binary format.  To be used by convert().<br>
	 * Called by:  convert()
	 * @throws NumberFormatException when invalid characters are encountered
	 */
	private void convertDecToBin()
	{
		/**
		 * The number left of the decimal point
		 */
		int left = 0;
		
		/**
		 * To keep track of the remainder during conversion
		 */
		int remainder = 0;
		
		try
		{
			left = Integer.parseInt(getLeftOfDecimalPoint());
		}
		catch(NumberFormatException e)
		{
			setError(true);
		}
		
		if(left == 0) setConvertedNumber("0");  // Prepend a 0 for consistency
		else if(left < 0) setError(true);  // Positive numbers only
			
		while(left != 0)
		{
			remainder = left % 2;
			appendConvertedNumber(Integer.toString(remainder));
			left = left / 2;
		}
		
		setConvertedNumber(reverseString(getConvertedNumber()));
		
		if(isFloat())
		{
			/**
			 * The number right of the decimal point
			 */
			double right = 0.0;
			
			/**
			 * To store one digit at a time during conversion
			 */
			int digit = 0;
			
			/**
			 * Stores number of digits to the right of the decimal point, for truncation purposes
			 */
			int numRight = 1;
			
			try
			{
				right = Double.parseDouble("." + getRightOfDecimalPoint());
			}
			catch(NumberFormatException e)
			{
				setError(true);
			}
			
			appendConvertedNumber(".");
			
			while(right != 0.0 && numRight <= ACCURACY)  // Truncate if greater than ACCURACY decimal places
			{
				right = right * 2;
				digit = (int)right;
				if(right >= 1.0) right = right - 1.0;  // Drop the 1
				appendConvertedNumber(Integer.toString(digit));  // Append each number left of the decimal point
				numRight++;
			}
		}
		
		if(!isError()) setDisplay(getConvertedNumber());
	}
	
	/**
	 * Converts the number from binary format to decimal format.  To be used by convert().<br>
	 * Called by:  convert()
	 * @throws NumberFormatException when invalid characters are encountered
	 */
	private void convertBinToDec()
	{
		/**
		 * Stores digit one at at time during conversion
		 */
		int digit = 0;
		
		/**
		 * Stores magnitude of number's position during conversion
		 */
		int exponent = getLeftOfDecimalPoint().length() - 1;
		
		/**
		 * Stores resulting calculation during conversion
		 */
		double result = 0;
		
		/**
		 * Stores the number in a String format
		 */
		String numberAsString = "", resultAsString = "";
		
		/**
		 * Stores number of digits to the right of the decimal point, for truncation purposes
		 */
		int numRight = 0;
		
		/**
		 * To determine whether current position is after the decimal point, for truncation purposes
		 */
		boolean isAfterDecimalPoint = false;
		
		numberAsString = getLeftOfDecimalPoint() + "." + getRightOfDecimalPoint();
		
		for(int i = 0; i < numberAsString.length(); i++)
		{
			try
			{
				digit = Integer.parseInt(Character.toString(numberAsString.charAt(i)));
				if(digit > 1) throw new NumberFormatException();
				result = result + (digit * (Math.pow(2, exponent)));
				numRight++;
			}
			catch(NumberFormatException e)
			{
				exponent++;  // Do not count the magnitude of the decimal point
				isAfterDecimalPoint = true;
				numRight = 0;  // Reset number of digits when decimal point is encountered
				
				if(numberAsString.charAt(i) != '.')
				{
					setError(true);
					break;
				}
			}
			
			exponent--;
			
			if(isAfterDecimalPoint && numRight > ACCURACY) break;  // Truncate 
		}
		
		if(!isError())
		{
			resultAsString = Double.toString(result);
			if(!isFloat) resultAsString = Integer.toString((int)result);  // Remove any trailing .0
			setDisplay(resultAsString);
		}	
	}
	
	/**
	 * Removes any unnecessary leading or trailing characters.<br>
	 * Called by:  convert()
	 */
	private void trimDisplay()
	{
		// If trailing decimal point, remove and set isFloat back to false
		if(!getDisplay().equals(""))
		{
			if(getDisplay().charAt(getDisplay().length() - 1) == '.')
			{
				setDisplay(getDisplay().substring(0, getDisplay().length() - 1));
				setFloat(false);
			}
		}
	}
	
	/**
	 * Reverses the order of characters in a string.  For reversing the remainders when converting from decimal to binary left of the decimal point.<br>
	 * Called by:  convertDecToBin()
	 * @param numAsString the string to reverse
	 * @return the string reversed
	 */
	private String reverseString(String numAsString)
	{
		String reversedString = "";
			
		for(int i = numAsString.length() - 1; i >= 0; i--)
			reversedString = reversedString + numAsString.charAt(i);
		
		return reversedString;
	}
	
	/**
	 * Called by:  convert(), isDisplayBlank(), trimDisplay()
	 * @return the number currently in the text field
	 */
	private String getDisplay()
	{
		return numberTextField.getText();
	}

	/**
	 * Sets the display.<br>
	 * Called by: actionPerformed(ActionEvent), convertBinToDec(), convertDecToBin(), NumberConverter(), setError(boolean), trimDisplay()  
	 * @param display the number to display in the text field
	 */
	private void setDisplay(String display)
	{
		numberTextField.setText(display);
	}
	
	/**
	 * 
	 * @return conversionMode the integer corresponding to the current conversion mode
	 */
	private int getConversionMode()
	{
		return conversionMode;
	}
	
	/**
	 * 
	 * @param conversionMode the integer corresponding to the mode to set
	 */
	private void setConversionMode(int conversionMode)
	{
		this.conversionMode = conversionMode;
	}

	/**
	 * Called by:  convert(), convertDecToBin()
	 * @return whether the number to be converted is a floating point decimal
	 */
	private boolean isFloat()
	{
		return isFloat;
	}

	/**
	 * Called by:  convert(), trimDisplay()
	 * @param isFloat sets whether the number to be converted is a floating point decimal
	 */
	private void setFloat(boolean isFloat)
	{
		this.isFloat = isFloat;
	}

	/**
	 * Called by:  convertBinToDec(), convertDecToBin()
	 * @return whether the number contains invalid characters
	 */
	private boolean isError()
	{
		return isError;
	}
	
	/**
	 * Called by:  convert(), convertBinToDec(), convertDecToBin()
	 * @param isError sets whether the number contains invalid characters
	 */
	private void setError(boolean isError)
	{
		this.isError = isError;
		if(isError) setDisplay("ERROR");
	}

	/**
	 * Called by:  convert()
	 * @return whether the display is blank
	 */
	private boolean isDisplayBlank()
	{
		if(getDisplay().equals("")) return true;
		else return false;
	}

	/**
	 * 
	 * @return the number after conversion
	 */
	private String getConvertedNumber()
	{
		return convertedNumber;
	}

	/**
	 * 
	 * @param convertedNumber stores the number after conversion
	 */
	private void setConvertedNumber(String convertedNumber)
	{
		this.convertedNumber = convertedNumber;
	}
	
	/**
	 * Called by:  convertDecToBin()
	 * @param convertedNumber appends subsequent digits to the converted number during conversion
	 */
	private void appendConvertedNumber(String convertedNumber)
	{
		this.convertedNumber = this.convertedNumber + convertedNumber;
	}

	/**
	 * 
	 * @return the whole number in the display, or left of the decimal point
	 */
	private String getLeftOfDecimalPoint()
	{
		return leftOfDecimalPoint;
	}

	/**
	 * 
	 * @param leftOfDecimalPoint the number to append to the left of the decimal point
	 */
	private void setLeftOfDecimalPoint(String leftOfDecimalPoint)
	{
		this.leftOfDecimalPoint = leftOfDecimalPoint;
	}

	/**
	 * 
	 * @return the fractional part of the number in the display, or right of the decimal point
	 */
	private String getRightOfDecimalPoint()
	{
		return rightOfDecimalPoint;
	}

	/**
	 * 
	 * @param rightOfDecimalPoint the number to append to the right of the decimal point
	 */
	private void setRightOfDecimalPoint(String rightOfDecimalPoint)
	{
		this.rightOfDecimalPoint = rightOfDecimalPoint;
	}

	/**
	 * The driver.
	 * @param args
	 */
	public static void main(String[] args)
	{
		NumberConverter nc = new NumberConverter();
		nc.setVisible(true);

	}
}
