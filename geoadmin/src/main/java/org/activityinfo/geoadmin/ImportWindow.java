package org.activityinfo.geoadmin;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.miginfocom.swing.MigLayout;

import org.activityinfo.geoadmin.model.ActivityInfoClient;
import org.activityinfo.geoadmin.model.AdminEntity;
import org.activityinfo.geoadmin.model.AdminLevel;
import org.activityinfo.geoadmin.model.Bounds;
import org.activityinfo.geoadmin.model.Country;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * User interface for matching imported features with their parents in the
 * existing hierarchy.
 * 
 */
public class ImportWindow extends JDialog {

	private static final Logger LOGGER = Logger.getLogger(ImportWindow.class.getName());
	
    private ActivityInfoClient client;
    private List<AdminEntity> parentEntities;

    private ImportTableModel tableModel;
    private ImportForm importForm;
    private ImportSource source;
    private ParentGuesser scorer;
    private JLabel scoreLabel;
    private Country country;
    private AdminLevel parentLevel;
    private JTable table;

    public ImportWindow(JFrame parent, ActivityInfoClient client,
        Country country,
        AdminLevel parentLevel,
        File shapeFile) throws Exception {

        super(parent, "Import - " + shapeFile.getName(), Dialog.ModalityType.APPLICATION_MODAL);
        setSize(650, 350);
        setLocationRelativeTo(parent);

        this.client = client;
        this.country = country;
        this.parentLevel = parentLevel;

        source = new ImportSource(shapeFile);
        if (parentLevel == null) {
            parentEntities = Lists.newArrayList();
        } else {
            parentEntities = sort(client.getAdminEntities(parentLevel));
        }

        scorer = new ParentGuesser(source, parentEntities);
        importForm = new ImportForm(source, parentEntities);

        tableModel = new ImportTableModel(source);
        JComboBox parentComboBox = new JComboBox(parentEntities.toArray());
        parentComboBox.setEditable(false);
        
        JComboBox actionComboBox = new JComboBox(ImportAction.values());
        actionComboBox.setEditable(false);

        table = new JTable(tableModel);
        table.getColumnModel().getColumn(ImportTableModel.PARENT_COLUMN).setCellEditor(
            new DefaultCellEditor(parentComboBox));
        table.getColumnModel().getColumn(ImportTableModel.ACTION_COLUMN).setCellEditor(
            new DefaultCellEditor(actionComboBox));
        table.setDefaultRenderer(Object.class,
            new ImportTableCellRenderer(tableModel, scorer));
        table.setAutoCreateRowSorter(true);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent e) {
                onSelectionChanged(e);
            }
        });

        scoreLabel = new JLabel();
        JLabel countLabel = new JLabel(source.getFeatureCount() + " features");

        JPanel panel = new JPanel(new MigLayout("fill"));
        panel.add(importForm, "wrap");
        panel.add(new JScrollPane(table,
            JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
            JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED), "span, wrap,grow");

        panel.add(scoreLabel, "height 25!, growx");
        panel.add(countLabel);

        getContentPane().add(createToolBar(), BorderLayout.PAGE_START);
        getContentPane().add(panel, BorderLayout.CENTER);
    }

    private void onSelectionChanged(ListSelectionEvent e) {
        int row = e.getFirstIndex();
        int featureIndex = table.convertRowIndexToModel(row);
        showScore(featureIndex);
    }

    /**
     * Display the parent match score of the selected item in the status bar
     * 
     * @param featureIndex
     */
    private void showScore(int featureIndex) {
        AdminEntity parent = tableModel.getParent(featureIndex);
        if (parent == null) {
            scoreLabel.setText("");
        } else {
            ImportFeature feature = tableModel.getFeatureAt(featureIndex);
            scoreLabel.setText(String.format("Scores:  Geo: %.2f  Name: %.2f  Code: %.2f",
                scorer.scoreGeography(feature, parent),
                scorer.scoreName(feature, parent),
                scorer.scoreCodeMatch(feature, parent)));
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
                try {
                    doImport();
                } catch (FileNotFoundException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
            }
        });

        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);
        toolBar.add(guessButton);
        toolBar.add(updateButton);
        toolBar.addSeparator();

        return toolBar;
    }

    protected void doImport() throws FileNotFoundException {
        int nameAttribute = importForm.getNameAttributeIndex();
        int codeAttribute = importForm.getCodeAttributeIndex();

        List<AdminEntity> entities = Lists.newArrayList();
        Map<ImportKey, AdminEntity> entityMap = Maps.newHashMap();
        
        for (int i = 0; i != tableModel.getRowCount(); ++i) {        	
        	if(tableModel.getActionAt(i) == ImportAction.IMPORT) {
	            ImportFeature feature = tableModel.getFeatureAt(i);
	            String featureName = feature.getAttributeStringValue(nameAttribute);
	            AdminEntity parent = tableModel.getParent(i);
	           
	            if(Strings.isNullOrEmpty(featureName)) {
	            	throw new RuntimeException("Feature " + i + " has an empty name");
	            }
	            
	            // we can't have two entities with the same name within a
	            // given parent. This happens often because secondary exterior rings
	            // are stored as separate features.
	        	ImportKey key = new ImportKey(parent, featureName);
	        	
	        	if(!entityMap.containsKey(key)) {
	        		// create a new entity
		            AdminEntity entity = new AdminEntity();
					entity.setName(featureName);
		            if(codeAttribute != -1) {
		            	entity.setCode(feature.getAttributeStringValue(codeAttribute));
		            }
		            Bounds bounds = GeoUtils.toBounds(feature.getEnvelope());
		            entity.setBounds(bounds);
		            entity.setGeometry(feature.getGeometry());
		
		            if (parentLevel != null) {
		                entity.setParentId(parent.getId());
		            }
		            entities.add(entity);
		            entityMap.put(key, entity);
	        	} else {
	        		// add this geometry to the existing entity
	        		
	        		LOGGER.info("Merging geometry for entity named '" + featureName + "'");
	        		
	        		AdminEntity entity = entityMap.get(key);
	        		entity.setGeometry( entity.getGeometry().union(feature.getGeometry()) );
	        	}
        	}
        }

        AdminLevel newLevel = new AdminLevel();
        newLevel.setName(importForm.getLevelName());
        if (parentLevel != null) {
            newLevel.setParentId(parentLevel.getId());
        }
        newLevel.setEntities(entities);

        if(parentLevel != null) {
            client.postChildLevel(parentLevel, newLevel);
        } else {
            client.postRootLevel(country, newLevel);
        }

        // hide window
        setVisible(false);
    }

    private List<AdminEntity> sort(List<AdminEntity> adminEntities) {
        Collections.sort(adminEntities, new Comparator<AdminEntity>() {

            @Override
            public int compare(AdminEntity a, AdminEntity b) {
                return a.getName().compareTo(b.getName());
            }
        });
        return adminEntities;
    }

    private void guessParents() {
        try {

            AdminEntity[] parents = scorer.run();
            for (int featureIndex = 0; featureIndex != parents.length; ++featureIndex) {
                tableModel.setValueAt(parents[featureIndex], featureIndex, ImportTableModel.PARENT_COLUMN);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
