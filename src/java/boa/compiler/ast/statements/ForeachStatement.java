package boa.compiler.ast.statements;

import boa.compiler.ast.Component;
import boa.compiler.ast.Node;
import boa.compiler.ast.expressions.Expression;
import boa.compiler.visitors.AbstractVisitor;
import boa.compiler.visitors.AbstractVisitorNoArg;

/**
 * 
 * @author rdyer
 */
public class ForeachStatement extends Statement {
	protected Component var;
	protected Expression condition;
	protected Block body;

	public Component getVar() {
		return var;
	}

	public Expression getCondition() {
		return condition;
	}

	public void setCondition(final Expression condition) {
		condition.setParent(this);
		this.condition = condition;
	}

	public Block getBody() {
		return body;
	}

	public ForeachStatement(final Component var, final Expression condition, final Statement s) {
		this(var, condition, Node.ensureBlock(s));
	}

	public ForeachStatement(final Component var, final Expression condition, final Block body) {
		if (var != null)
			var.setParent(this);
		if (condition != null)
			condition.setParent(this);
		if (body != null)
			body.setParent(this);
		this.var = var;
		this.condition = condition;
		this.body = body;
	}

	/** {@inheritDoc} */
	@Override
	public <A> void accept(final AbstractVisitor<A> v, A arg) {
		v.visit(this, arg);
	}

	/** {@inheritDoc} */
	@Override
	public void accept(final AbstractVisitorNoArg v) {
		v.visit(this);
	}

	public ForeachStatement clone() {
		final ForeachStatement s = new ForeachStatement(var.clone(), condition.clone(), body.clone());
		copyFieldsTo(s);
		return s;
	}
}