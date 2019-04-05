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
	public String getTitle() 					{		return "Enhancer Panel";	}
	public Icon getIcon() 						{		return null;	}

	
	JButton adder = new JButton("Add");
	JButton doIt = new JButton("Enhance");		
	JButton clearAll = new JButton("Clear");

	List<ColumnMapPane> categories = new ArrayList<ColumnMapPane>();
	int lineHeight = 40;
	Dimension dim = new Dimension(400, lineHeight);
	Dimension dim4lines = new Dimension(400, 4*lineHeight);
	Dimension numDimension = new Dimension(40, 30);
	Dimension columnDimension = new Dimension(140, lineHeight);
	Dimension colorDimension = new Dimension(24, 24);
	Dimension colorLabDimension = new Dimension(64, lineHeight);
	Dimension rangeDimension = new Dimension(100, lineHeight);
	
	JComboBox<String> graphType;

	
JPanel makeIntro()
{
	JPanel intro = new JPanel();
	intro.setLayout(new BoxLayout(intro, BoxLayout.PAGE_AXIS));
	
	JLabel label0 = new JLabel("A variety of graph types are supported for any node.");
	LookAndFeelUtil.makeSmall(label0);
   String types[] = { "Bar Chart", "Circos Chart", "Heat Strip Chart", "Line Chart", "Pie Chart", "Stripe Chart" }; 
	graphType = new JComboBox<String>(types);
	graphType.setSelectedItem("Pie Chart");
//	graphType.setEnabled(false);
	intro.add(label0);
	intro.add(graphType);
	
	JLabel label1 = new JLabel("Select the columns and colors to assign to the pies.");
	JLabel label2 = new JLabel("Range can be optionally set to get normalized colors.");
	intro.add(line(label1));
	intro.add(line(label2));
	intro.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
	LookAndFeelUtil.makeSmall(label1);
	LookAndFeelUtil.makeSmall(label2);
	intro.add(Box.createVerticalGlue());
	return intro;

}
	JPanel makeHeader()
	{
		JLabel lab1 = new JLabel("Column");
		JLabel lab2 = new JLabel("Color");
		JLabel lab3 = new JLabel("Range");
		LookAndFeelUtil.makeSmall(lab1,lab2, lab3);
		setSizes(lab1, columnDimension);
		setSizes(lab2, colorLabDimension);
		setSizes(lab3, rangeDimension);
		JPanel line = new JPanel();
		setSizes(line, dim);
		line.setLayout(new BoxLayout(line, BoxLayout.LINE_AXIS));
		line.add(Box.createHorizontalStrut(20));
		line.add(lab1);
		line.add(lab2);
		line.add(lab3);
		return line;
		
	}

	static void setSizes(Component p, Dimension d)
	{
		p.setMinimumSize(d);
		p.setMaximumSize(d);
		p.setPreferredSize(d);
		
	}
	class ColumnMapPane extends JPanel
	{
		JComboBox<String> column;
		ColorMenuButton colorButton;
		JTextField minVal;
		JTextField maxVal;
		String[] colNames = { "Name", "Age", "JurkatScore", "HEKScore" } ;
			
		private ColumnMapPane()
		{
			setSizes(this, dim);
			column = new JComboBox<String>(colNames); 	
			column.setSize(columnDimension);
			colorButton = new ColorMenuButton();
			setSizes(colorButton,colorDimension); 
			colorButton.setMinimumSize(colorDimension);
			colorButton.setMaximumSize(colorDimension);
			colorButton.setPreferredSize(colorDimension);
			JPanel around = new JPanel();
			around.add(colorButton);
//			around.setBorder(BorderFactory.createDashedBorder(Color.blue));
			//			colorButton.setSize(colorDimension);
			minVal = new JTextField("0.0"); 				
			setSizes(minVal,numDimension);
			maxVal = new JTextField("1.0");				
			maxVal.setSize(numDimension);
			setSizes(maxVal,numDimension);
//			colorButton.setBorder(BorderFactory.createLineBorder(Color.red));
			setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
			add(column);		add(Box.createHorizontalStrut(20));
//			JButton b = new JButton("Push");
//			add(b);
			add(around);	add(Box.createHorizontalStrut(20));
			LookAndFeelUtil.makeSmall(minVal);
			LookAndFeelUtil.makeSmall(maxVal);
			JLabel lab1 = new JLabel("Min:");
			JLabel lab2 = new JLabel("Max:");
			LookAndFeelUtil.makeSmall(lab1);
			LookAndFeelUtil.makeSmall(lab2);
			add(lab1); add(minVal); add(Box.createHorizontalStrut(20));
			add(lab2); add(maxVal);
//			setBorder(BorderFactory.createLineBorder(Color.ORANGE));
		}
		
		public String getColumn()	{ return "" + column.getSelectedItem(); }
		public Color getCatColor()	{ return colorButton.getColor(); }
		public double getMin()		{	return Double.parseDouble(minVal.getText());			}
		public double getMax()		{	return Double.parseDouble(maxVal.getText());			}
	}
	
	private void addCategory()	
	{ 	
		ColumnMapPane pane = new ColumnMapPane();
		categories.add(pane);
		categoryParentPanel.add(pane);
	}
	//--------------------------------------------------------------------
	private void buildUI() {
		
		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		add(makeIntro());
		categoryParentPanel = new JPanel();
//		optionsPanel.setAlignmentX(0f);
		categoryParentPanel.setBorder(BorderFactory.createEtchedBorder());
		categoryParentPanel.setLayout(new BoxLayout(categoryParentPanel, BoxLayout.PAGE_AXIS));
		categoryParentPanel.add(Box.createRigidArea(new Dimension(10,3)));
		categoryParentPanel.add(makeHeader());
		add(categoryParentPanel);
		categoryParentPanel.setMinimumSize(dim4lines);
		categoryParentPanel.setPreferredSize(dim4lines);
		add(Box.createRigidArea(new Dimension(20, 20)));

		add( makeButtonRow());
		add(Box.createVerticalGlue());		//-------
	}

	private JPanel makeButtonRow() {
		//a row of buttons and their actions
		ActionListener addCategory = new ActionListener() {
			@Override public void actionPerformed(ActionEvent e) 
			{
				addCategory(); 
				setVisible(false);
				setVisible(true);
			}
		};
		adder.addActionListener(addCategory);


		ActionListener clrAll = new ActionListener() {
			@Override public void actionPerformed(ActionEvent e) { resetOptionsPanel();  }
		};
		clearAll.addActionListener(clrAll);

		doIt.addActionListener(new ActionListener() {
			@Override public void actionPerformed(ActionEvent e) { controller.enhance(extract());  }
		});
		return (line(doIt, clearAll, adder));
				
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
	
	public void resetOptionsPanel()
	{
		categoryParentPanel.removeAll();
		categoryParentPanel.setVisible(false);
		categoryParentPanel.setVisible(true);

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

    public String extract()
	{
		StringBuilder builder = new StringBuilder("piechart: ");
		// pull the data out of the GUI components
		StringBuilder attributes = new StringBuilder("attributelist=\"");
		StringBuilder colors = new StringBuilder("colorlist=\"");
		StringBuilder ranges = new StringBuilder("range=\"");
		for (ColumnMapPane pane : categories)
		{
			attributes.append(pane.getColumn()).append(",");
			Color c = pane.getCatColor();
			colors.append(Colors.toString(c)).append(",");
			ranges.append(pane.getMin()).append ("-").append(pane.getMax()).append (",");
		}
		String attr = changeLastCommaToQuoteSpace(attributes.toString());
		String colrs = changeLastCommaToQuoteSpace(colors.toString());
		String rnges = changeLastCommaToQuoteSpace(ranges.toString());

		builder.append(attr).append(colrs).append(rnges);
		return builder.toString();
	}
	
	private String changeLastCommaToQuoteSpace(String attr) {
		int idx = attr.lastIndexOf(",");
		return idx < 0 ? "" : (attr.substring(0, idx) + "\" ");
		
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


}

