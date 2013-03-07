package org.activityinfo.geoadmin;

import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;

import org.activityinfo.geoadmin.model.AdminUnit;
import org.opengis.feature.type.PropertyDescriptor;

public class ImportForm extends JPanel {

	private ImportSource source;
	private JTextField levelNameField;
	private JComboBox nameCombo;
	private JComboBox codeCombo;
	private List<AdminUnit> parentUnits;

	public ImportForm(ImportSource source, List<AdminUnit> parents) {
		super(new MigLayout());
		
		this.source = source;
		this.parentUnits = parents;
		
		levelNameField = new JTextField();
		levelNameField.setText(nameFromFile());
		
		nameCombo = new JComboBox(source.getAttributeNames());
		nameCombo.setSelectedIndex( guessNameColumn() );
		
		codeCombo = new JComboBox(source.getAttributeNames());
		codeCombo.setSelectedIndex( guessCodeColumn() );
		
		add(new JLabel("Level Name:"));  
		add(levelNameField, "width 100!, wrap");  
		
		add(new JLabel("Name Attribute"));  
		add(nameCombo, "width 160!, wrap");  

		add(new JLabel("Code Attribute"));  
		add(codeCombo, "width 160!, wrap");  

	}
	
	private String nameFromFile() {
		String fileName = source.getFile().getName();
		int dot = fileName.lastIndexOf(".");
		if(dot == -1) {
			return fileName;
		} else {
			return fileName.substring(0, dot);
		}
	}

	private int guessNameColumn() {
		return new ColumnGuesser()
		.forPattern("[A-Za-z-' ]+")
		.favoringUniqueValues()
		.findBest(source);
	}
	
	private int guessCodeColumn() {
		return new ColumnGuesser()
		.forPattern("[0-9]+")
		.favoringUniqueValues()
		.findBest(source);
	}

	public String getLevelName() {
		return levelNameField.getText();
	}

	public int getNameAttributeIndex() {
		return nameCombo.getSelectedIndex();
	}

	public int getCodeAttributeIndex() {
		return codeCombo.getSelectedIndex();
	}

}
