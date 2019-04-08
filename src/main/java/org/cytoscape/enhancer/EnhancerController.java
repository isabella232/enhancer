package org.cytoscape.enhancer;

import java.awt.Component;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
//import static org.cytoscape.ding.DVisualLexicon.NODE_CUSTOMGRAPHICS_1;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.events.SetCurrentNetworkEvent;
import org.cytoscape.application.events.SetCurrentNetworkListener;
import org.cytoscape.application.swing.events.CytoPanelComponentSelectedEvent;
import org.cytoscape.application.swing.events.CytoPanelComponentSelectedListener;
//import org.cytoscape.ding.customgraphics.NullCustomGraphics;
//import org.cytoscape.ding.impl.visualproperty.CustomGraphicsVisualProperty;
import org.cytoscape.model.CyColumn;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyRow;
import org.cytoscape.model.CyTable;
import org.cytoscape.property.bookmark.ObjectFactory;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.VisualProperty;
import org.cytoscape.view.vizmap.VisualMappingFunction;
import org.cytoscape.view.vizmap.VisualMappingManager;
import org.cytoscape.view.vizmap.VisualStyle;

/*
 * EnhancerController
 * 
 
 */

public class EnhancerController implements CytoPanelComponentSelectedListener, SetCurrentNetworkListener {

	private static final String ENHANCER_NAME = "egPie";
	private CyServiceRegistrar registrar;
	public EnhancerController(CyServiceRegistrar reg)
	{
		registrar = reg;
		initialize();
	}

//----------------------------------------------------------
	private CyApplicationManager cyApplicationManager;
	private CyNetwork network;
	private CyNetworkView networkView;
	private EnhancerPanel enhancerPanel;
	public void setEnhancerPanel(EnhancerPanel p) { enhancerPanel = p; }
	//----------------------------------------------------------
	private void initialize()
	{
		cyApplicationManager = registrar.getService(CyApplicationManager.class);
		network = cyApplicationManager.getCurrentNetwork();
		networkView = cyApplicationManager.getCurrentNetworkView();
}
	//-------------------------------------------------------------------
	@Override
	public void handleEvent(CytoPanelComponentSelectedEvent arg0) 
	{	
		Component comp = arg0.getCytoPanel().getSelectedComponent();
//		if (comp instanceof EnhancerPanel)
//			scanNetwork();
	}
	//-------------------------------------------------------------------
	public CyNetworkView getNetworkView()		{ 	 return networkView;		}
		
		
	public void scanNetwork() {	}
	

	public void setCurrentNetView(CyNetworkView newView)
	{
		if (newView == null) return;		// use ""
//		if (newView.getSUID() == currentNetworkView.getSUID()) return;
		currentNetworkView = newView;
		enhancerPanel.enableControls(currentNetworkView != null);
		
	}
	//-------------------------------------------------------------------------------
	private CyNetworkView currentNetworkView;
	private boolean verbose = true;
	
	public String getCurrentNetworkName() {
		if (currentNetworkView != null)
			return "" + currentNetworkView.getModel().getSUID();		// TODO -- getCurrentNetworkName
		return "";
	}
		//-------------------------------------------------------------------------------
	public void layout()
	{
		networkView = cyApplicationManager.getCurrentNetworkView();

	}
	
	List<String> getColumnNames()
	{
		List<String> strs = new ArrayList<String>();
		CyNetwork net = cyApplicationManager.getCurrentNetwork();
		if (net != null)
		{
			CyTable table = net.getDefaultNodeTable();
			for (CyColumn col : table.getColumns())
			{
				if (null == col) 	continue;
				if (!isNumeric(col)) continue;
				if ("SUID".equals(col.getName())) continue;
				strs.add(col.getName());
			}
		}
		return strs;
	}
	
	private boolean isNumeric(CyColumn col) {
//		if (col == null) return false;
		Class<?> c = col.getType();  //getClass();
		String classs = c.getName();
		if (classs.equals("java.lang.Integer")) return true;
		if (classs.equals("java.lang.Long")) return true;
		if (classs.equals("java.lang.Float")) return true;
		if (classs.equals("java.lang.Double")) return true;
		return false;
	}
	// INCOMPLETE -- not setting the passthru mapping into the style 
	public void enhance(String extracted, List<String> colNames) {
		
		String spec = extracted;
		System.out.println(spec);

		if (network == null) return ;
		CyTable nodeTable = network.getDefaultNodeTable();
		CyColumn col = nodeTable.getColumn(ENHANCER_NAME);
		if (col == null)
		{
			nodeTable.createColumn(ENHANCER_NAME, String.class, true);
			col = nodeTable.getColumn(ENHANCER_NAME);
		}
		for (CyRow row : nodeTable.getAllRows())
			if (rowHasValues(nodeTable, row, colNames)) 
				row.set(ENHANCER_NAME, extracted);


		// see VizMapPropertyBuilder ??
		VisualStyle currentStyle = getStyle();
		VisualProperty vp = null;  // "NODE_CUSTOMGRAPHICS_1";  // BasicVisualLexicon.NODE_CUSTOMGRAPHICS_1;   // TODO
		final VisualMappingFunction<?, ?> fn = currentStyle.getVisualMappingFunction(vp);
//		ObjectFactory factory = new ObjectFactory();
//		PassthroughMapping<?, ?> newMapping = factory.createPassthroughMapping();
		if (networkView != null)
			currentStyle.apply(networkView);
		networkView.updateView();
	}
//	public static final VisualProperty<CyCustomGraphics> NODE_CUSTOMGRAPHICS_1 = new CustomGraphicsVisualProperty(
//			NullCustomGraphics.getNullObject(), CG_RANGE,
//			"NODE_CUSTOMGRAPHICS_1", "Node Image/Chart 1", CyNode.class);

	private boolean rowHasValues(CyTable nodeTable, CyRow row, List<String> colNames )
	{
		for (String column : colNames)
		{
			CyColumn col = nodeTable.getColumn(column);
			Class<?> c = col.getType();
			Object d = row.get(column, c);
			if (d == null)	return false;
		}
		return true;
	}
	
	private VisualStyle getStyle()
	{
		// Now we cruise thru the list of node, then edge attributes looking for mappings.  Each mapping may potentially be a legend entry.
		VisualMappingManager manager = registrar.getService( VisualMappingManager.class);
		VisualStyle style = manager.getCurrentVisualStyle();
//		System.out.println("style: " + style.getTitle());
		return style;

	}
	
	private void getMaps(VisualStyle style)
	{
		Collection<VisualMappingFunction<?, ?>> vizmapFns =  style.getAllVisualMappingFunctions();
		for (VisualMappingFunction<?,?> fn : vizmapFns)
		{
			String mappingType = fn.toString();
			if (mappingType.contains("Passthrough")) continue;
			VisualProperty<?> vp = fn.getVisualProperty();
			if (vp.toString().contains("EDGE")) continue;
//			candidates.add(new LegendCandidate(fn));
		}
	}
	//-------------------------------------------------------------------------------
	@Override
	public void handleEvent(SetCurrentNetworkEvent e) {

		network = e.getNetwork();
		enhancerPanel.enableControls(network != null);
//		if (cyApplicationManager != null)
//		{
//			networkView = cyApplicationManager.getCurrentNetworkView();
////			scanNetwork();
//		}
	}
	
}
