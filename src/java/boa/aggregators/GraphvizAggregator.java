package boa.aggregators;

import java.io.IOException;


/**
 * A Boa aggregator to output graph data in GraphViz format.
 * 
 * @author rdyer
 */
@AggregatorSpec(name = "graphviz", weightType = "any", canCombine = true)
public class GraphvizAggregator extends GraphAggregator {
	/** {@inheritDoc} */
	@Override
	public void finish() throws IOException, InterruptedException {
		for (final String s : this.neighbors)
			this.collect(s, weights.get(s));
	}

	/** {@inheritDoc} */
	@Override
	protected String format(final String idx, final String data, final String metadata) {
		if (metadata == null)
			return "\"" + idx.substring(1, idx.length() - 1)  + "\" -> " + data + ";";
		return "\"" + idx.substring(1, idx.length() - 1)  + "\" -> " + data + " [label=" + metadata + "];";
	}
}