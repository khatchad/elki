package de.lmu.ifi.dbs.elki.algorithm.outlier.trivial;

import de.lmu.ifi.dbs.elki.algorithm.AbstractAlgorithm;
import de.lmu.ifi.dbs.elki.algorithm.outlier.OutlierAlgorithm;
import de.lmu.ifi.dbs.elki.data.type.TypeInformation;
import de.lmu.ifi.dbs.elki.data.type.TypeUtil;
import de.lmu.ifi.dbs.elki.database.Database;
import de.lmu.ifi.dbs.elki.database.datastore.DataStoreFactory;
import de.lmu.ifi.dbs.elki.database.datastore.DataStoreUtil;
import de.lmu.ifi.dbs.elki.database.datastore.WritableDataStore;
import de.lmu.ifi.dbs.elki.database.ids.DBID;
import de.lmu.ifi.dbs.elki.database.relation.Relation;
import de.lmu.ifi.dbs.elki.logging.Logging;
import de.lmu.ifi.dbs.elki.database.relation.MaterializedRelation;
import de.lmu.ifi.dbs.elki.result.outlier.OutlierResult;
import de.lmu.ifi.dbs.elki.result.outlier.OutlierScoreMeta;
import de.lmu.ifi.dbs.elki.result.outlier.ProbabilisticOutlierScore;

/**
 * Trivial method that claims all objects to be outliers. Can be used as
 * reference algorithm in comparisons.
 * 
 * @author Erich Schubert
 */
public class TrivialAllOutlier extends AbstractAlgorithm<OutlierResult> implements OutlierAlgorithm {
  /**
   * Our logger.
   */
  private static final Logging logger = Logging.getLogger(TrivialAllOutlier.class);

  /**
   * Constructor.
   */
  public TrivialAllOutlier() {
    super();
  }

  @Override
  public TypeInformation[] getInputTypeRestriction() {
    return TypeUtil.array(TypeUtil.ANY);
  }

  public OutlierResult run(@SuppressWarnings("unused") Database database, Relation<?> relation) throws IllegalStateException {
    WritableDataStore<Double> scores = DataStoreUtil.makeStorage(relation.getDBIDs(), DataStoreFactory.HINT_HOT, Double.class);
    for(DBID id : relation.iterDBIDs()) {
      scores.put(id, 1.0);
    }
    Relation<Double> scoreres = new MaterializedRelation<Double>("Trivial all-outlier score", "all-outlier", TypeUtil.DOUBLE, scores, relation.getDBIDs());
    OutlierScoreMeta meta = new ProbabilisticOutlierScore();
    return new OutlierResult(meta, scoreres);
  }

  @Override
  protected Logging getLogger() {
    return logger;
  }
}