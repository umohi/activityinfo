package org.activityinfo.geoadmin;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.PrintStream;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.miginfocom.swing.MigLayout;

import org.activityinfo.geoadmin.model.AdminLevel;
import org.activityinfo.geoadmin.model.AdminUnit;
import org.geotools.map.FeatureLayer;
import org.geotools.map.MapContent;
import org.geotools.styling.SLD;
import org.geotools.swing.JMapPane;
import org.geotools.swing.action.ZoomInAction;
import org.geotools.swing.action.ZoomOutAction;

import com.google.common.collect.Lists;
import com.vividsolutions.jts.geom.Envelope;

public class ImportWindow extends JDialog {

	private GeoClient client;
	private List<AdminUnit> parentUnits;
	
	private ImportTableModel tableModel;
	private ImportForm importForm;
	private ImportSource source;
	private ParentGuesser scorer;
	private JLabel scoreLabel;
	private JMapPane mapPane;
	private AdminLevel parentLevel;
	

	public ImportWindow(JFrame parent, GeoClient client, AdminLevel parentLevel, File shapeFile) throws Exception {
		super(parent, "Import - " + shapeFile.getName(), Dialog.ModalityType.APPLICATION_MODAL);
		setSize(650, 350);
		setLocationRelativeTo(parent);

		this.client = client;
		this.parentLevel = parentLevel;
		source = new ImportSource(shapeFile);
		parentUnits = sort(client.getAdminEntities(parentLevel));
		scorer = new ParentGuesser(source, parentUnits);
		importForm = new ImportForm(source, parentUnits);

		tableModel = new ImportTableModel(source);
		JComboBox parentComboBox = new JComboBox(parentUnits.toArray());
		parentComboBox.setEditable(false);

		JTable table = new JTable(tableModel);
		table.getColumnModel().getColumn(0).setCellEditor(
				new DefaultCellEditor(parentComboBox));
		table.setDefaultRenderer(Object.class, 
				new ImportTableCellRenderer(tableModel, scorer));
		table.setAutoCreateRowSorter(true);
		
		table.getSelectionModel().addListSelectionListener(new  ListSelectionListener() {
			
			@Override
			public void valueChanged(ListSelectionEvent e) {
				onSelectionChanged(e);
			}
		});
		
		scoreLabel = new JLabel();
		JLabel countLabel = new JLabel(source.getFeatureCount() + " features");
		//mapPane = createMap();
		
		
		JPanel panel = new JPanel(new MigLayout("fill"));
		panel.add(importForm, "wrap");
		panel.add(new JScrollPane(table), "wrap,grow");
		
		panel.add(scoreLabel, "height 25!, growx");
		panel.add(countLabel);
		
		getContentPane().add(createToolBar(), BorderLayout.PAGE_START);
		getContentPane().add(panel, BorderLayout.CENTER);
	}


	private void onSelectionChanged(ListSelectionEvent e) {
		showScore(e.getFirstIndex());
	}

	private void showScore(int featureIndex) {
		AdminUnit parent = tableModel.getParent(featureIndex);
		if(parent == null) {
			scoreLabel.setText("");
		} else {
			scoreLabel.setText(String.format("Scores:  Geo: %.2f  Name: %.2f  Code: %.2f",
					scorer.scoreGeography(featureIndex, parent),
					scorer.scoreName(featureIndex, parent),
					scorer.scoreCodeMatch(featureIndex, parent)));
		}
	}


	private JToolBar createToolBar() {

		JButton guessButton = new JButton("Guess Parents");
		guessButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				guessParents();
			}
		});

		JButton updateButton = new JButton("Import");
		updateButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				doImport();
			}
		});
		
		JToolBar toolBar = new JToolBar();
		toolBar.setFloatable(false);
		toolBar.add(guessButton);
		toolBar.add(updateButton);
		toolBar.addSeparator();
//		
//        ButtonGroup cursorToolGrp = new ButtonGroup();
//
//		JButton zoomInAction = new JButton(new ZoomInAction(mapPane));
//		toolBar.add(zoomInAction);
//		cursorToolGrp.add(zoomInAction);
//
//		JButton zoomOut = new JButton(new ZoomOutAction(mapPane));
//		toolBar.add(zoomOut);
//		cursorToolGrp.add(zoomOut);
		

		return toolBar;
	}

	protected void doImport() {
		int nameAttribute = importForm.getNameAttributeIndex();
		int codeAttribute = importForm.getCodeAttributeIndex();

		
		List<AdminUnit> entities = Lists.newArrayList();
		
		for(int featureIndex = 0; featureIndex != source.getFeatureCount();++featureIndex) {
			
			AdminUnit entity = new AdminUnit();
			entity.setName( source.getAttributeStringValue(featureIndex, nameAttribute) );
			entity.setCode( source.getAttributeStringValue(featureIndex, codeAttribute) );
			entity.setBounds( GeoUtils.toBounds( source.getEnvelope( featureIndex )));
			entity.setParentId( tableModel.getParent(featureIndex).getId() );
			entities.add(entity);
						
		}		
		
		
		AdminLevel newLevel = new AdminLevel();
		newLevel.setName(importForm.getLevelName());
		newLevel.setParentId(parentLevel.getId());
		newLevel.setEntities(entities);
		
		client.postChildLevel(parentLevel, newLevel);
	}



	private List<AdminUnit> sort(List<AdminUnit> adminEntities) {
		Collections.sort(adminEntities, new Comparator<AdminUnit>() {

			@Override
			public int compare(AdminUnit a, AdminUnit b) {
				return a.getName().compareTo(b.getName());
			}
		});
		return adminEntities;
	}

	private JMapPane createMap() {
		MapContent context = new MapContent();
		context.addLayer(new FeatureLayer(source.getFeatureSource(), SLD.createPolygonStyle(Color.BLACK, Color.YELLOW, 0)));
		JMapPane mapPane = new JMapPane(context);
		return mapPane;
	}


	private void guessParents() {
		try {

			AdminUnit[] parents = scorer.run();
			for(int featureIndex=0;featureIndex != parents.length; ++featureIndex) {
				tableModel.setValueAt(parents[featureIndex], featureIndex, ImportTableModel.PARENT_COLUMN);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
}
