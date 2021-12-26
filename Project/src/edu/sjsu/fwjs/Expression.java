package edu.sjsu.fwjs;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * FWJS expressions.
 */
public interface Expression {
    /**
     * Evaluate the expression in the context of the specified environment.
     */
    public Value evaluate(Environment env);
}

// NOTE: Using package access so that all implementations of Expression
// can be included in the same file.

/**
 * FWJS constants.
 */
class ValueExpr implements Expression {
    private Value val;
    public ValueExpr(Value v) {
        this.val = v;
    }
    public Value evaluate(Environment env) {
        return this.val;
    }
}

/**
 * Expressions that are a FWJS variable.
 */
class VarExpr implements Expression {
    private String varName;
    public VarExpr(String varName) {
        this.varName = varName;
    }
    public Value evaluate(Environment env) {
        return env.resolveVar(varName);
    }
}

/**
 * A print expression.
 */
class PrintExpr implements Expression {
    private Expression exp;
    public PrintExpr(Expression exp) {
        this.exp = exp;
    }
    public Value evaluate(Environment env) {
        Value v = exp.evaluate(env);
        System.out.println(v.toString());
        return v;
    }
}
/**
 * Binary operators (+, -, *, etc).
 * Currently only numbers are supported.
 */
class BinOpExpr implements Expression {
    private Op op;
    private Expression e1;
    private Expression e2;
    public BinOpExpr(Op op, Expression e1, Expression e2) {
        this.op = op;
        this.e1 = e1;
        this.e2 = e2;
    }

    @SuppressWarnings("incomplete-switch")
    public Value evaluate(Environment env) {
        // YOUR CODE HERE
        List<Expression> expr = new ArrayList<>();
        expr.add(e1);
        expr.add(e2);

        List<Value> v = expr.stream().map(x -> x.evaluate(env)).collect(Collectors.toList());
        List<Integer> ints = v.stream().map(
                x -> {
                    if (x instanceof BoolVal)
                        return ((BoolVal) x).toBoolean() ? 1 : 0;
                    else if (x instanceof NullVal)
                        return 0;
                    else if (x instanceof ClosureVal)
                        return -1;
                    return ((IntVal) x).toInt();
                }
        ).collect(Collectors.toList());

        int int1 = ints.get(0);
        int int2 = ints.get(1);
        Value val = new NullVal();


        switch (op){
            case ADD:
                val = new IntVal(int1 + int2);
                break;
            case SUBTRACT:
                val = new IntVal(int1-int2);
                break;
            case MULTIPLY:
                val = new IntVal(int1*int2);
                break;
            case DIVIDE:
                val = new IntVal(int1/int2);
                break;
            case MOD:
                val = new IntVal(int1%int2);
                break;
            case GT:
                val = new BoolVal(int1 > int2);
                break;
            case LT:
                val = new BoolVal(int1 < int2);
                break;
            case GE:
                val = new BoolVal(int1 >= int2);
                break;
            case LE:
                val = new BoolVal(int1 <= int2);
                break;
            case EQ:
                val = new BoolVal(int1 == int2);
                break;
        }
        return val;
    }
}

/**
 * If-then-else expressions.
 * Unlike JS, if expressions return a value.
 */
class IfExpr implements Expression {
    private Expression cond;
    private Expression thn;
    private Expression els;

    public IfExpr(Expression cond, Expression thn, Expression els) {
        this.cond = cond;
        this.thn = thn;
        this.els = els;
    }
    public Value evaluate(Environment env) {
        // YOUR CODE HERE
        Value val = cond.evaluate(env);
        Boolean condition = false;

        if(val instanceof IntVal && ((IntVal) val).toInt() != 0){
            condition = true;
        } else if (val instanceof BoolVal && ((BoolVal) val).toBoolean()){
            condition = true;
        }

        if(condition == true){
            return thn.evaluate(env);
        } else if (this.els != null){
            return els.evaluate(env);
        } else {
            return new NullVal();
        }
    }
}

/**
 * While statements (treated as expressions in FWJS, unlike JS).
 */
class WhileExpr implements Expression {
    private Expression cond;
    private Expression body;
    public WhileExpr(Expression cond, Expression body) {
        this.cond = cond;
        this.body = body;
    }
    public Value evaluate(Environment env) {
        // YOUR CODE HERE
        while(((BoolVal)cond.evaluate(env)).toBoolean()){
            body.evaluate(env);
        }
        return null;
    }
}

/**
 * Sequence expressions (i.e. 2 back-to-back expressions).
 */
class SeqExpr implements Expression {
    private Expression e1;
    private Expression e2;
    public SeqExpr(Expression e1, Expression e2) {
        this.e1 = e1;
        this.e2 = e2;
    }
    public Value evaluate(Environment env) {
        // YOUR CODE HERE
        e1.evaluate(env);
        return e2.evaluate(env);
    }
}

/**
 * Declaring a variable in the local scope.
 */
class VarDeclExpr implements Expression {
    private String varName;
    private Expression exp;
    public VarDeclExpr(String varName, Expression exp) {
        this.varName = varName;
        this.exp = exp;
    }
    public Value evaluate(Environment env) {
        // YOUR CODE HERE
        Value val = new NullVal();
        val = exp.evaluate(env);
        env.createVar(varName, val);
        return val;
    }
}

/**
 * Updating an existing variable.
 * If the variable is not set already, it is added
 * to the global scope.
 */
class AssignExpr implements Expression {
    private String varName;
    private Expression e;
    public AssignExpr(String varName, Expression e) {
        this.varName = varName;
        this.e = e;
    }
    public Value evaluate(Environment env) {
        // YOUR CODE HERE
        if(e == null) {
            return null;
        }
        Value val = e.evaluate(env);
        env.updateVar(varName, val);
        return val;
    }
}

/**
 * A function declaration, which evaluates to a closure.
 */
class FunctionDeclExpr implements Expression {
    private List<String> params;
    private Expression body;
    public FunctionDeclExpr(List<String> params, Expression body) {
        this.params = params;
        this.body = body;
    }
    public Value evaluate(Environment env) {
        // YOUR CODE HERE
        return new ClosureVal(params, body, env);
    }
}

/**
 * Function application.
 */
class FunctionAppExpr implements Expression {
    private Expression f;
    private List<Expression> args;
    public FunctionAppExpr(Expression f, List<Expression> args) {
        this.f = f;
        this.args = args;
    }
    public Value evaluate(Environment env) {
        // YOUR CODE HERE
        List arg = new ArrayList<Value>();
        for(Expression e:this.args){
            arg.add(e.evaluate(env));
        }
        ClosureVal c = ((ClosureVal) this.f.evaluate(env));
        return c.apply(arg);
    }
}

