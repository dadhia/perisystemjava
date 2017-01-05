package guiWidgets;

import java.awt.Color;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

import javax.swing.JTextField;

/**
 * TextFieldWithGhostText is a class that allows for a normal JTextField to display
 * a ghost text or prompt to the user.
 */
public class TextFieldWithGhostText extends JTextField {

	private static final long serialVersionUID = 1;
	private JTextField textField;
	private String ghostText;
	
	/**
	 * Constructor.
	 * @param ghostText String the prompt that will be displayed to the user with a light gray color
	 * @param columns int the specified number of columns for this textfield--same as with JTextField
	 */
	public TextFieldWithGhostText(String ghostText, int columns) {
		super(ghostText, columns);
		textField = this;
		this.ghostText = ghostText;
		setForeground(Color.LIGHT_GRAY);
		addFocusListener(new FocusAdapter() {
			/**
			* When focus has been gained, remove the ghost text and allow user to type.
			*/
			@Override
			public void focusGained(FocusEvent arg0) {
				textField.setText("");
				textField.setForeground(Color.BLACK);
			}
			
			/**
			 * When the text area has lost focus and no text has been entered (or just whitespace),
			 * display the ghost text prompt again
			 */
			@Override
			public void focusLost(FocusEvent arg0) {
				if (textField.getText().trim().contentEquals("")) {
					textField.setText(ghostText);
					textField.setForeground(Color.LIGHT_GRAY);
				}

			}
		});
	}
	
	/**
	 * Gets the text from the text field.
	 * @return String
	 */
	@Override
	public String getText() {
		String text = super.getText();
		//if the text if the same as the ghostText, this is a blank submission
		if (text.contentEquals(ghostText))
			return "";
		return text;
	}
	
	/**
	 * Resets the text field with the ghost text.
	 */
	public void clear() {
		textField.setText(ghostText);
		textField.setForeground(Color.LIGHT_GRAY);
	}
}