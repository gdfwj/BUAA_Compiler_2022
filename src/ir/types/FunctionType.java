package ir.types;


import java.util.ArrayList;

public class FunctionType extends Type{
    private ArrayList<Type> parametersType;
    private Type retType;
    public FunctionType(ArrayList<Type> parametersType, Type retType) {
        super(false);
        this.parametersType = parametersType;
        this.retType = retType;
    }

    public ArrayList<Type> getParametersType() {
        return parametersType;
    }

    public Type getRetType() {
        return retType;
    }
}
