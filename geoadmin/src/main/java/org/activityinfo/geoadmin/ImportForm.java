package org.activityinfo.geoadmin;

import java.util.List;

import javax.swing.*;

import net.miginfocom.swing.MigLayout;

import org.activityinfo.geoadmin.model.AdminEntity;

public class ImportForm extends JPanel {

    private ImportSource source;
    private JTextField levelNameField;
    private JComboBox nameCombo;
    private JComboBox codeCombo;
    private JCheckBox importGeometryCheckBox;
    private List<AdminEntity> parentUnits;

    public ImportForm(ImportSource source, List<AdminEntity> parents) {
        super(new MigLayout());

        this.source = source;
        this.parentUnits = parents;

        levelNameField = new JTextField();
        levelNameField.setText(nameFromFile());

        nameCombo = new JComboBox(source.getAttributeNames());
        nameCombo.setSelectedIndex(guessNameColumn());

        codeCombo = new JComboBox(codeChoices(source));
        codeCombo.setSelectedIndex(0);

        add(new JLabel("Level Name:"));
        add(levelNameField, "width 100!, wrap");

        add(new JLabel("Name Attribute"));
        add(nameCombo, "width 160!, wrap");

        add(new JLabel("Code Attribute"));
        add(codeCombo, "width 160!, wrap");

        importGeometryCheckBox = new JCheckBox("Import Geometry");
        add(importGeometryCheckBox);
    }

	private String[] codeChoices(ImportSource source) {	
		String[] names = source.getAttributeNames();
		String[] choices = new String[names.length+1];
		choices[0] = "--NONE--";
		for(int i=0;i!=names.length;++i) {
			choices[i+1] = names[i];
		}
		return choices;
	}

    private String nameFromFile() {
        String fileName = source.getFile().getName();
        int dot = fileName.lastIndexOf(".");
        if (dot == -1) {
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
        return codeCombo.getSelectedIndex()-1;
    }

    public boolean isGeometryImported() {
        return importGeometryCheckBox.isSelected();
    }

}
