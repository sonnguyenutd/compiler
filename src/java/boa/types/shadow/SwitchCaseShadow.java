/*
 * Copyright 2017, Robert Dyer, Kaushik Nimmala
 *                 and Bowling Green State University
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package boa.types.shadow;

import boa.compiler.ast.Call;
import boa.compiler.ast.expressions.Expression;
import boa.compiler.ast.Factor;
import boa.compiler.ast.Identifier;
import boa.compiler.ast.Node;
import boa.compiler.SymbolTable;
import boa.compiler.transforms.ASTFactory;
import boa.types.BoaInt;
import boa.types.BoaProtoList;
import boa.types.BoaShadowType;
import boa.types.proto.enums.StatementKindProtoMap;
import boa.types.proto.ExpressionProtoTuple;
import boa.types.proto.StatementProtoTuple;

/**
 * A shadow type for SwitchCase.
 * 
 * @author rdyer
 * @author kaushin
 */
public class SwitchCaseShadow extends BoaShadowType  {
    /**
     * Construct a {@link SwitchCaseShadow}.
     */
    public SwitchCaseShadow() {
        super(new StatementProtoTuple());

    
        addShadow("expression", new ExpressionProtoTuple());
        addShadow("isdefault", new ExpressionProtoTuple());
       
    }

    /** {@inheritDoc} */
    @Override
	public Node lookupCodegen(final String name, final Factor node, final SymbolTable env) { 

       
        if ("expression".equals(name)) {
            // ${0}.expression
            return ASTFactory.createSelector( "expression", new ExpressionProtoTuple(), env);
        }

        if ("isdefault".equals(name)) {
           
            
             // ${0}.expression
            node.addOp(ASTFactory.createSelector("expression", new ExpressionProtoTuple(), env));

            // def(${0}.expression)
			return ASTFactory.createCallFactor("def", env, new ExpressionProtoTuple(), ASTFactory.createFactorExpr(node), ASTFactory.createIntLiteral(2), ASTFactory.createIntLiteral(-1));

          
        
        }

        

        throw new RuntimeException("invalid shadow field: " + name);
    }

    /** {@inheritDoc} */
    @Override
    public Expression getKindExpression(final SymbolTable env) {
        return getKindExpression("StatementKind", "CASE", new StatementKindProtoMap(), env);
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return "SwitchCase";
    }
}
