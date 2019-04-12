package org.cytoscape.enhancer;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.cytoscape.application.swing.CytoPanelComponent;
import org.cytoscape.application.swing.CytoPanelName;
import org.cytoscape.util.swing.LookAndFeelUtil;
import org.cytoscape.view.model.CyNetworkView;

public class EnhancerPanel extends JPanel implements CytoPanelComponent {
	
	//--------------------------------------------------------------------
	public EnhancerPanel(EnhancerController ctrl) {
		controller = ctrl;
		controller.setEnhancerPanel(this);
		buildUI();
		setVisible(true);
		addCategory();
	}

	//--------------------------------------------------------------------
	private static final long serialVersionUID = 12L;

	private EnhancerController controller;
	private JPanel categoryParentPanel;
	
	public Component getComponent() 			{		return this;	}
	public CytoPanelName getCytoPanelName() 	{		return CytoPanelName.WEST;	}
	public String getTitle() 					{		return "Enhanced Graphics";	}
	public Icon getIcon() 						{		return null;	}

	
	JButton adder = new JButton("Add");
	JButton newRing = new JButton("Add Ring");
	JButton doIt = new JButton("Enhance");		
	JButton link = new JButton("More...");		
	JButton clearAll = new JButton("Clear");

	List<ColumnMapPane> categories = new ArrayList<ColumnMapPane>();
	int lineHeight = 32;
	Dimension dim = new Dimension(400, lineHeight);
	Dimension dim4lines = new Dimension(400, 6*lineHeight);
	Dimension dim12lines = new Dimension(400, 12*lineHeight);
	Dimension introDim = new Dimension(400, 3*lineHeight);
	Dimension numDimension = new Dimension(40, 30);
	Dimension columnDimension = new Dimension(240, lineHeight);
	Dimension adderDimension = new Dimension(36, 30);
	Dimension colorDimension = new Dimension(24, 24);
	Dimension colorLabDimension = new Dimension(64, lineHeight);
	Dimension rangeDimension = new Dimension(100, lineHeight);
	
	JComboBox<String> graphType;

	
JPanel makeIntro()
{
	JPanel intro = new JPanel();
	intro.setLayout(new BoxLayout(intro, BoxLayout.PAGE_AXIS));
	
	JLabel label0 = new JLabel("A variety of graph types will be available.");
	JLabel labelA = new JLabel("Only Pie Charts are supported at this point.");
	LookAndFeelUtil.makeSmall(label0, labelA);
   String types[] = { "Bar Chart", "Circos Chart", "Heat Strip Chart", "Line Chart", "Pie Chart", "Stripe Chart" }; 
	graphType = new JComboBox<String>(types);
	graphType.setSelectedItem("Pie Chart");
//	graphType.setEnabled(false);
	intro.add(line(label0));
	intro.add(line(labelA));
	intro.add(line(graphType));

	JLabel label1 = new JLabel("Select the columns and colors to assign to the wedges of the pie.");
	intro.add(line(label1));
	intro.setBorder(BorderFactory.createEmptyBorder(6,6,0,6));
	LookAndFeelUtil.makeSmall(label1);
	intro.add(Box.createVerticalGlue());
	intro.setPreferredSize(introDim);
	intro.setMaximumSize(introDim);
	return intro;

}
	JPanel makeHeader()
	{
		JButton adder = new JButton("+");
		adder.addActionListener(addCategory);
		JLabel lab1 = new JLabel("Column");
		JLabel lab2 = new JLabel("Color");
//		JLabel lab3 = new JLabel("Range");
		LookAndFeelUtil.makeSmall(adder, lab1,lab2);		//, lab3
		setSizes(adder, adderDimension);
		setSizes(lab1, columnDimension);
		setSizes(lab2, colorLabDimension);
//		setSizes(lab3, rangeDimension);
		JPanel line = new JPanel();
		setSizes(line, dim);
		line.setLayout(new BoxLayout(line, BoxLayout.LINE_AXIS));
		line.add(adder);
		line.add(Box.createHorizontalStrut(10));
		line.add(lab1);
		line.add(lab2);
//		line.add(lab3);
		return line;
		
	}

	static void setSizes(Component p, Dimension d)
	{
		p.setMinimumSize(d);
		p.setMaximumSize(d);
		p.setPreferredSize(d);
		
	}
	

	//--------------------------------------------------------
	class ColumnMapPane extends JPanel
	{
		JComboBox<String> column;
		ColorMenuButton colorButton;
		String[] colNames = { "Name", "Age", "JurkatScore", "HEKScore" } ;
			
		private ColumnMapPane(List<String> columNameList)
		{
			setSizes(this, dim);
			column = new JComboBox<String>(columNameList.toArray(colNames)); 
			column.setMaximumRowCount(40);
			column.setSize(columnDimension);
			colorButton = new ColorMenuButton();
			setSizes(colorButton,colorDimension); 
			JPanel around = new JPanel();
			around.add(colorButton);
			setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
			add(column);	add(Box.createHorizontalStrut(20));
			add(around);	add(Box.createHorizontalStrut(20));
		}
		
		public String getColumn()	{ return "" + column.getSelectedItem(); }
		public Color getCatColor()	{ return colorButton.getColor(); }
	}
	//--------------------------------------------------------
	ActionListener addCategory = new ActionListener() {
		@Override public void actionPerformed(ActionEvent e) 
		{
			addCategory(); 
			setVisible(false);
			setVisible(true);
		}
	};
	ActionListener addRing = new ActionListener() {
		@Override public void actionPerformed(ActionEvent e) 
		{
			addCategory(); 
			setVisible(false);
			setVisible(true);
		}
	};
	
	private void addCategory()	
	{ 	
		List<String> names = controller.getColumnNames();
		if (!names.isEmpty())
		{
			ColumnMapPane pane = new ColumnMapPane(names);
			categories.add(pane);
			categoryParentPanel.add(pane);
		}
	}
	//--------------------------------------------------------------------
	private void buildUI() {
		
		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		add(makeIntro());
		add(makeCategoriesPanel());
		add(Box.createRigidArea(new Dimension(20, 8)));

		JLabel label2 = new JLabel("Range can be optionally set to get normalized colors.");
		LookAndFeelUtil.makeSmall(label2);
		add(line(label2));
		add( makeRangePanel());
		JLabel labelQ = new JLabel("Other arguments:  arcstart, labellist, labelsize, minimumslice, position");
		JLabel labelR = new JLabel("scale, showlabels, size, valuelist, ybase");
		LookAndFeelUtil.makeSmall(labelQ, labelR);
		add(Box.createVerticalGlue());		
		add(line(labelQ));
		add(line(labelR));
		add( makeButtonRow());
		add(Box.createVerticalGlue());		
	}

	private Component makeCategoriesPanel() {
		categoryParentPanel = new JPanel();
//		optionsPanel.setAlignmentX(0f);
		categoryParentPanel.setBorder(BorderFactory.createEtchedBorder());
		categoryParentPanel.setMinimumSize(dim4lines);
		categoryParentPanel.setPreferredSize(dim4lines);
		categoryParentPanel.setMaximumSize(dim12lines);
		categoryParentPanel.setLayout(new BoxLayout(categoryParentPanel, BoxLayout.PAGE_AXIS));
		categoryParentPanel.add(Box.createRigidArea(new Dimension(10,3)));
		categoryParentPanel.add(makeHeader());
		return categoryParentPanel;
	}

	private JPanel makeButtonRow() {
		//a row of buttons and their actions
		adder.addActionListener(addCategory);

//		newRing.addActionListener(addRing);

		ActionListener clrAll = new ActionListener() {
			@Override public void actionPerformed(ActionEvent e) { clearCategories();  }
		};
		clearAll.addActionListener(clrAll);

		doIt.addActionListener(new ActionListener() {
			@Override public void actionPerformed(ActionEvent e) { controller.enhance(extract(), getColumnNames());  }
		});
		return (line(doIt, clearAll, adder));		//, newRing
				
	}
	JTextField minVal = new JTextField();
	JTextField maxVal = new JTextField();
	JLabel lab1 = new JLabel("Min:");
	JLabel lab2 = new JLabel("Max:");

	private JPanel makeRangePanel()
	{
		LookAndFeelUtil.makeSmall(minVal, maxVal, lab1,lab2 );
		JPanel p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.LINE_AXIS));
		p.add(lab1); p.add(minVal);
		p.add(Box.createHorizontalStrut(20));
		p.add(lab2); p.add(maxVal);
		minVal.setPreferredSize(new Dimension(60, 30));
		maxVal.setPreferredSize(new Dimension(60, 30));
		minVal.setMaximumSize(new Dimension(60, 30));
		maxVal.setMaximumSize(new Dimension(60, 30));
		p.setMaximumSize(new Dimension(400, 30));
		p.setPreferredSize(new Dimension(400, 30));
		p.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
		return p;

	}

	//--------------------------------------------------------------------
	JComponent[] ctrls = {adder, clearAll, doIt};
	public void enableControls(boolean on)
	{
		for (JComponent component : ctrls)
			component.setEnabled(on);

		for (ColumnMapPane category : categories)
			category.setEnabled(on);
	}
	
	public void clearCategories()
	{
		categoryParentPanel.removeAll();
		categoryParentPanel.setVisible(false);
		categoryParentPanel.setVisible(true);
		categories.clear();
		categoryParentPanel.add(makeHeader());
		addCategory();			// put one new category back
			
	}
	//--------------------------------------------------------------------
	public void setCurrentNetwork()
	{
		CyNetworkView view = controller.getNetworkView();
		enableControls(view != null);
	}
	//--------------------------------------------------------------------
//http://www.cgl.ucsf.edu/cytoscape/utilities3/enhancedcg.shtml
//  piechart: attributelist="Values" colorlist="contrasting" labellist="First,Second,Third,Fourth,Fifth"
//    piechart: attributelist="a,b,c,d" colorlist="red,green,blue,yellow"

	public List<String> getColumnNames()
	{
		List<String> strs = new ArrayList<String> ();
		for (ColumnMapPane pane : categories)
			strs.add(pane.getColumn());
		return strs;
		
	}
	public String extract()
	{
		StringBuilder builder = new StringBuilder("piechart: ");
		// pull the data out of the GUI components
		StringBuilder attributes = new StringBuilder("attributelist=\"");
		StringBuilder colors = new StringBuilder("colorlist=\"");
		for (ColumnMapPane pane : categories)
		{
			attributes.append(pane.getColumn()).append(",");
			Color c = pane.getCatColor();
			colors.append(Colors.toString(c)).append(",");
		}
		String attr = changeLastCommaToQuoteSpace(attributes.toString());
		String colrs = changeLastCommaToQuoteSpace(colors.toString());
		builder.append(attr).append(colrs);
		String min = minVal.getText().trim();
		String max= maxVal.getText().trim();
		if (!(min.isEmpty() || (max.isEmpty())))
		{
			StringBuilder ranges = new StringBuilder("range=\"");
			ranges.append(minVal.getText()).append (",").append(maxVal.getText()).append ("\" ");
			builder.append(ranges.toString());
		}
		builder.append("showlabels=\"false\" ");
		return builder.toString();
	}
	//--------------------------------------------------------------------
	// util
	
	private JPanel line(JComponent sub)
	{
		JPanel box = new JPanel();
		box.setLayout(new BoxLayout(box, BoxLayout.LINE_AXIS));
		box.add(sub);
		box.add(Box.createHorizontalGlue());
//		box.setBorder(BorderFactory.createLineBorder(Color.blue));
		return box;
	}
	
	private JPanel line(JComponent subA, JComponent subB, JComponent subC)
	{
		JPanel box = new JPanel();
		box.setLayout(new BoxLayout(box, BoxLayout.LINE_AXIS));
		box.add(subA);
		box.add(Box.createRigidArea(new Dimension(12,12)));
		box.add(subB);
		box.add(Box.createRigidArea(new Dimension(12,12)));
		box.add(subC);
		box.add(Box.createHorizontalGlue());
//		box.setBorder(BorderFactory.createLineBorder(Color.blue));
		return box;
	}
	
	private JPanel line(JComponent subA, JComponent subB, JComponent subC, JComponent subD)
	{
		JPanel box = new JPanel();
		box.setLayout(new BoxLayout(box, BoxLayout.LINE_AXIS));
		box.add(subA);
		box.add(Box.createRigidArea(new Dimension(12,12)));
		box.add(subB);
		box.add(Box.createRigidArea(new Dimension(12,12)));
		box.add(subC);
		box.add(Box.createRigidArea(new Dimension(12,12)));
		box.add(subD);
		box.add(Box.createHorizontalGlue());
//		box.setBorder(BorderFactory.createLineBorder(Color.blue));
		return box;
	}

	
	private String changeLastCommaToQuoteSpace(String attr) {
		int idx = attr.lastIndexOf(",");
		return idx < 0 ? "" : (attr.substring(0, idx) + "\" ");
		
	}

}

