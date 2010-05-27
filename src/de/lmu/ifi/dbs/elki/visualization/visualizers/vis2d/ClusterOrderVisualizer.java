package de.lmu.ifi.dbs.elki.visualization.visualizers.vis2d;

import org.w3c.dom.Element;

import de.lmu.ifi.dbs.elki.data.NumberVector;
import de.lmu.ifi.dbs.elki.database.Database;
import de.lmu.ifi.dbs.elki.database.ids.DBID;
import de.lmu.ifi.dbs.elki.math.linearalgebra.Vector;
import de.lmu.ifi.dbs.elki.result.ClusterOrderEntry;
import de.lmu.ifi.dbs.elki.result.ClusterOrderResult;
import de.lmu.ifi.dbs.elki.visualization.VisualizationProjection;
import de.lmu.ifi.dbs.elki.visualization.css.CSSClass;
import de.lmu.ifi.dbs.elki.visualization.css.CSSClassManager.CSSNamingConflict;
import de.lmu.ifi.dbs.elki.visualization.style.StyleLibrary;
import de.lmu.ifi.dbs.elki.visualization.svg.SVGPlot;
import de.lmu.ifi.dbs.elki.visualization.svg.SVGUtil;
import de.lmu.ifi.dbs.elki.visualization.visualizers.StaticVisualization;
import de.lmu.ifi.dbs.elki.visualization.visualizers.Visualization;
import de.lmu.ifi.dbs.elki.visualization.visualizers.Visualizer;
import de.lmu.ifi.dbs.elki.visualization.visualizers.VisualizerContext;

/**
 * Visualize an OPTICS cluster order by drawing connection lines.
 * 
 * @author Erich Schubert
 *
 * @param <NV> object type
 */
public class ClusterOrderVisualizer<NV extends NumberVector<NV,?>> extends Projection2DVisualizer<NV> {
  /**
   * A short name characterizing this Visualizer.
   */
  private static final String NAME = "Cluster Order";
  
  /**
   * CSS class name
   */
  private static final String CSSNAME = "co";
  
  /**
   * The result we visualize
   */
  private ClusterOrderResult<?> result;
  
  /**
   * Initialize the visualizer.
   * 
   * @param context Context
   * @param result Result class.
   */
  public void init(VisualizerContext<? extends NV> context, ClusterOrderResult<?> result) {
    super.init(NAME, context);
    this.result = result;
  }

  @Override
  public Visualization visualize(SVGPlot svgp, VisualizationProjection proj, double width, double height) {
    Database<? extends NV> database = context.getDatabase();
    double margin = context.getStyleLibrary().getSize(StyleLibrary.MARGIN);
    Element layer = Projection2DVisualization.setupCanvas(svgp, proj, margin, width, height);
    
    CSSClass cls = new CSSClass(this, CSSNAME);
    context.getLineStyleLibrary().formatCSSClass(cls, 0, context.getStyleLibrary().getLineWidth(StyleLibrary.CLUSTERORDER));
    
    try {
      svgp.getCSSClassManager().addClass(cls);
    }
    catch(CSSNamingConflict e) {
      logger.error("CSS naming conflict.", e);
    }
    
    for (ClusterOrderEntry<?> ce : result) {
      DBID thisId = ce.getID();
      DBID prevId = ce.getPredecessorID();
      if (thisId == null || prevId == null) {
        continue;
      }
      Vector thisVec = proj.projectDataToRenderSpace(database.get(thisId));
      Vector prevVec = proj.projectDataToRenderSpace(database.get(prevId));
      
      Element arrow = svgp.svgLine(prevVec.get(0), prevVec.get(1), thisVec.get(0), thisVec.get(1));
      SVGUtil.setCSSClass(arrow, cls.getName());
      
      layer.appendChild(arrow);
    }
    
    Integer level = this.getMetadata().getGenerics(Visualizer.META_LEVEL, Integer.class);
    return new StaticVisualization(level, layer, width, height);
  }
}