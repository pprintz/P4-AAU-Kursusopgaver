/**
 * Created by Teitur on 10-04-2017.
 */
public class TypeChecker extends Visitor {

    public Boolean programHasTypeErrors = false;

    private void TypeErrorOccured(ASTNode node){
        programHasTypeErrors = true;
        System.out.format("[%d:%d] Type error of unknown kind.\n", node.lineNumber, node.columnNumber);
    }

    private void TypeErrorOccured(ASTNode node, Type actualType, Type expectedType){
        programHasTypeErrors = true;

        System.out.format("[%d:%d] Type error: expected %s, got %s\n", node.lineNumber, node.columnNumber, expectedType, actualType);
    }

    private void TypeErrorOccured(ASTNode node, Type typeOne, Type typeTwo, Type expectedType){
        programHasTypeErrors = true;

        System.out.format("[%d:%d] Type error: expected two %s, got %s and %s\n", node.lineNumber, node.columnNumber, expectedType, typeOne, typeTwo);
    }

    private void TypeErrorOccured(ASTNode node, Type typeOne, Type typeTwo, Type expectedType, Type expectedTypeAlternative){
        programHasTypeErrors = true;

        System.out.format("[%d:%d] Type error: expected two of %s OR %s, got %s and %s\n", node.lineNumber, node.columnNumber, expectedType, expectedTypeAlternative, typeOne, typeTwo);
    }

    public void visit(UnaryExprNode unaryExprNode){
        switch (unaryExprNode.unaryOperator) {
            case PARANTHESIS:
                // empty since PARANTHESIS has nothing to do with types
                break;
            case NEGATEBOOL:
                if(checkExpectedType(unaryExprNode, Type.BOOL)){
                    unaryExprNode.Type = Type.BOOL;
                }
                else{
                    TypeErrorOccured(unaryExprNode, unaryExprNode.exprNode.Type, Type.BOOL);
                }
                break;
            case NEGATE:
                if(checkExpectedType(unaryExprNode, Type.NUM)){
                    unaryExprNode.Type = Type.NUM;
                }
                else {
                    TypeErrorOccured(unaryExprNode, unaryExprNode.exprNode.Type, Type.NUM);
                }
                break;
            default:
                TypeErrorOccured(unaryExprNode);
        }
    }

    public void visit(BinaryExprNode binaryExprNode){
        switch (binaryExprNode.binaryOperator) {
            case PLUS:
                if(checkExpectedType(binaryExprNode, Type.TEXT)){
                    binaryExprNode.Type = Type.TEXT;
                    break;
                }
            case MINUS:
            case MULTIPLY:
            case DIVISION:
            case MODULO:
            case POWER:
                if(checkExpectedType(binaryExprNode, Type.NUM)){
                    binaryExprNode.Type = Type.NUM;
                }
                else {
                    TypeErrorOccured(binaryExprNode, binaryExprNode.leftNode.Type, binaryExprNode.rightNode.Type, Type.NUM);
                }
                break;

            case GREATERTHAN:
            case LESSTHANEQUAL:
            case GREATERTHANEQUAL:
            case LESSTHAN:
                if(checkExpectedType(binaryExprNode, Type.NUM)){
                    binaryExprNode.Type = Type.BOOL;
                }
                else{
                    TypeErrorOccured(binaryExprNode, binaryExprNode.leftNode.Type, binaryExprNode.rightNode.Type, Type.NUM);
                }
                break;
            case AND:
            case OR:
                if(checkExpectedType(binaryExprNode, Type.BOOL)){
                    binaryExprNode.Type = Type.BOOL;
                }
                else{
                    TypeErrorOccured(binaryExprNode, binaryExprNode.leftNode.Type, binaryExprNode.rightNode.Type, Type.BOOL);
                }
                break;
            case EQUAL:
            case NOTEQUAL:
                if(checkExpectedType(binaryExprNode, Type.BOOL)
                        || checkExpectedType(binaryExprNode, Type.NUM)) {
                    binaryExprNode.Type = Type.BOOL;
                }else{
                    TypeErrorOccured(binaryExprNode, binaryExprNode.leftNode.Type, binaryExprNode.rightNode.Type, Type.NUM, Type.BOOL);
                }
                break;
            default:
                TypeErrorOccured(binaryExprNode);
        }
    }

    private boolean checkExpectedType(BinaryExprNode binaryExprNode, Type expectedType) {
        if(expectedType == Type.STRUCT){
            System.out.println("Don't call checkExpectedType with expectedType STRUCT");
            return false;
        }
        Boolean typesAreCompatible = false;

        // TODO do smarter
        String typeString = expectedType == Type.BOOL ? "bool" : "num";

        if(binaryExprNode.leftNode.Type == expectedType && binaryExprNode.rightNode.Type == expectedType){
            typesAreCompatible = true;
        }
        else if(binaryExprNode.leftNode instanceof IdNode){
            IdNode idNode = (IdNode) binaryExprNode.leftNode;
            if(binaryExprNode.rightNode.Type == expectedType && idNode.declarationNode.typeNode.type.equals(typeString)) {
                typesAreCompatible = true;
            }
        }
        else if(binaryExprNode.rightNode instanceof IdNode){
            IdNode idNode = (IdNode) binaryExprNode.rightNode;
            if(binaryExprNode.leftNode.Type == expectedType && idNode.declarationNode.typeNode.type.equals(typeString)) {
                typesAreCompatible = true;
            }
        }
        return typesAreCompatible;
    }

    private boolean checkExpectedType(UnaryExprNode unaryExprNode, Type expectedType) {
        if(expectedType == Type.TEXT || expectedType == Type.STRUCT){
            System.out.println("Don't call checkExpectedType with expectedType TEXT or STRUCT");
            return false;
        }
        Boolean typesAreCompatible = false;
        String typeString = null;

        // TODO this if else needs to be handled smarter
        if(expectedType == Type.BOOL){
            typeString = "bool";
        }
        else if(expectedType == Type.NUM){
            typeString = "num";
        }
        if(unaryExprNode.exprNode.Type.equals(expectedType) && unaryExprNode.exprNode.Type.equals(expectedType)){
            typesAreCompatible = true;
        }
        else if(unaryExprNode.exprNode instanceof IdNode){
            IdNode idNode = (IdNode) unaryExprNode.exprNode;
            if(unaryExprNode.exprNode.Type.equals(expectedType) && idNode.declarationNode.typeNode.type.equals(typeString)) {
                typesAreCompatible = true;
            }
        }

        return typesAreCompatible;
    }

    public void visit(LiteralNode node){
    }

    public void visit(AssignmentNode node){
        if( ! (node.idNode.declarationNode.Type == node.exprNode.Type)){
            TypeErrorOccured(node, node.exprNode.Type, node.idNode.declarationNode.Type);
        }
    }

    public void visit(IdNode node){

    }

    public void visit(BlockNode node){

    }

    public void visit(DefineFunctionNode node){

    }

    /*public void visit(StructInitializationNode node){
        for(AssignmentNode aNode : node.assignments){
            visit(aNode);
        }
    }*/

    public void visit(StructDefinitionNode node){
        for(DeclarationNode dNode : node.declarationNodes){
            visit(dNode);
        }
    }

    public void visit(StructInitializationNode node){
        for(AssignmentNode aNode : node.assignments){
            visit(aNode);
        }
    }

    //public void visit(Struc)

    public void visit(DeclarationNode node){
        node.Type = typeOfTypeNode(node.typeNode);
        if(node.exprNode != null) {
            visit(node.exprNode);
            if(node.Type != node.exprNode.Type ){
                TypeErrorOccured(node, node.exprNode.Type, node.Type);
            }
            else{
                node.idNode.Type = node.Type;
            }
        }
    }

    private Type typeOfTypeNode(TypeNode node){
        switch(node.type){
            case "num":
                return Type.NUM;
            case "text":
                return Type.TEXT;
            case "bool":
                return Type.BOOL;
            default:
                return Type.STRUCT;
        }

    }

}
