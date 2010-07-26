package de.lmu.ifi.dbs.elki.distance.distancefunction;

import de.lmu.ifi.dbs.elki.data.DatabaseObject;
import de.lmu.ifi.dbs.elki.database.ids.DBID;
import de.lmu.ifi.dbs.elki.database.query.DistanceQuery;
import de.lmu.ifi.dbs.elki.distance.distancevalue.Distance;

/**
 * Distance function to proxy computations to another distance (that probably
 * was run before).
 * 
 * @author Erich Schubert
 * 
 * @param <O> object type
 * @param <D> distance type
 */
public class ProxyDistanceFunction<O extends DatabaseObject, D extends Distance<D>> extends AbstractDBIDDistanceFunction<D> {
  /**
   * Distance query
   */
  final DistanceQuery<O, D> inner;

  /**
   * Constructor
   * 
   * @param inner Inner distance
   */
  public ProxyDistanceFunction(DistanceQuery<O, D> inner) {
    super();
    this.inner = inner;
  }

  @Override
  public D distance(DBID o1, DBID o2) {
    return inner.distance(o1, o2);
  }

  @Override
  public D getDistanceFactory() {
    return inner.getDistanceFactory();
  }

  /**
   * Get the inner query
   * 
   * @return query
   */
  public DistanceQuery<O, D> getDistanceQuery() {
    return inner;
  }
}
