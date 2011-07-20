package de.lmu.ifi.dbs.elki.algorithm.outlier;

import de.lmu.ifi.dbs.elki.algorithm.AbstractDistanceBasedAlgorithm;
import de.lmu.ifi.dbs.elki.data.type.TypeInformation;
import de.lmu.ifi.dbs.elki.data.type.TypeUtil;
import de.lmu.ifi.dbs.elki.database.Database;
import de.lmu.ifi.dbs.elki.database.datastore.DataStore;
import de.lmu.ifi.dbs.elki.database.query.distance.DistanceQuery;
import de.lmu.ifi.dbs.elki.database.relation.Relation;
import de.lmu.ifi.dbs.elki.distance.distancefunction.DistanceFunction;
import de.lmu.ifi.dbs.elki.distance.distancevalue.Distance;
import de.lmu.ifi.dbs.elki.database.relation.MaterializedRelation;
import de.lmu.ifi.dbs.elki.result.outlier.OutlierResult;
import de.lmu.ifi.dbs.elki.result.outlier.OutlierScoreMeta;
import de.lmu.ifi.dbs.elki.result.outlier.ProbabilisticOutlierScore;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.OptionID;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.parameterization.Parameterization;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.parameters.DistanceParameter;

/**
 * Simple distance based outlier detection algorithms.
 * 
 * <p>
 * Reference: E.M. Knorr, R. T. Ng: Algorithms for Mining Distance-Based
 * Outliers in Large Datasets, In: Procs Int. Conf. on Very Large Databases
 * (VLDB'98), New York, USA, 1998.
 * 
 * @author Lisa Reichert
 * 
 * @param <O> the type of DatabaseObjects handled by this Algorithm
 * @param <D> the type of Distance used by this Algorithm
 */
public abstract class AbstractDBOutlier<O, D extends Distance<D>> extends AbstractDistanceBasedAlgorithm<O, D, OutlierResult> implements OutlierAlgorithm {
  /**
   * Parameter to specify the size of the D-neighborhood
   */
  public static final OptionID D_ID = OptionID.getOrCreateOptionID("dbod.d", "size of the D-neighborhood");

  /**
   * Holds the value of {@link #D_ID}.
   */
  private D d;

  /**
   * Constructor with actual parameters.
   * 
   * @param distanceFunction distance function to use
   * @param d d value
   */
  public AbstractDBOutlier(DistanceFunction<? super O, D> distanceFunction, D d) {
    super(distanceFunction);
    this.d = d;
  }

  /**
   * Runs the algorithm in the timed evaluation part.
   * 
   */
  public OutlierResult run(Database database, Relation<O> relation) throws IllegalStateException {
    DistanceQuery<O, D> distFunc = database.getDistanceQuery(relation, getDistanceFunction());
    
    // Run the actual score process
    DataStore<Double> dbodscore = computeOutlierScores(database, distFunc, d);

    // Build result representation.
    Relation<Double> scoreResult = new MaterializedRelation<Double>("Density-Based Outlier Detection", "db-outlier", TypeUtil.DOUBLE, dbodscore, relation.getDBIDs());
    OutlierScoreMeta scoreMeta = new ProbabilisticOutlierScore();
    return new OutlierResult(scoreMeta, scoreResult);
  }

  /**
   * computes an outlier score for each object of the database.
   */
  protected abstract DataStore<Double> computeOutlierScores(Database database, DistanceQuery<O, D> distFunc, D d);

  @Override
  public TypeInformation[] getInputTypeRestriction() {
    return TypeUtil.array(getDistanceFunction().getInputTypeRestriction());
  }

  /**
   * Parameterization class.
   * 
   * @author Erich Schubert
   * 
   * @apiviz.exclude
   */
  public static abstract class Parameterizer<O, D extends Distance<D>> extends AbstractDistanceBasedAlgorithm.Parameterizer<O, D> {
    protected D d = null;
    
    @Override
    protected void makeOptions(Parameterization config) {
      super.makeOptions(config);
      configD(config, distanceFunction);
    }

    /**
     * Grab the 'd' configuration option.
     * 
     * @param config Parameterization
     */
    protected void configD(Parameterization config, DistanceFunction<?, D> distanceFunction) {
      final D distanceFactory = (distanceFunction != null) ? distanceFunction.getDistanceFactory() : null;
      final DistanceParameter<D> param = new DistanceParameter<D>(D_ID, distanceFactory);
      if(config.grab(param)) {
        d = param.getValue();
      }
    }
  }
}