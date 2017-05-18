import CompilerError.UndefinedError;
import java.util.*;

public class BindingVisitor extends Visitor {
    private Stack<HashMap<String, ASTNode>> symbolTable;
    public static boolean hasBindingErrorOccured = false;
    public static HashMap<String, String> roboFunctions;


    //For properly handling functions private to a strategy
    private HashMap<String, HashMap<String, DefineFunctionNode>> strategyEnvironment;
    private boolean insideStrategy;
    private HashMap<String, DefineFunctionNode> functionEnvironment;


    public BindingVisitor(Stack<HashMap<String, ASTNode>> symbolTableWithFunctions,
                          HashMap<String, HashMap<String, DefineFunctionNode>> strategyEnvironment) {
        symbolTable = symbolTableWithFunctions;
        this.strategyEnvironment = strategyEnvironment;
        roboFunctions = new HashMap<>();
        addFunctionTokens();
        translateRoboFunctionsToStrava();
    }

    /**
     * Adds a function specific to Strava to the symbol table so that TypeChecker doesn't complain.
     *
     * @param returnTypeString The function return type. Must be either "num", "text", or "bool".
     * @param functionId       The name of the function.
     * @param paramTypes       A list of parameter types as an array of String. Amount must match with amount of paramIds.
     * @param paramIds         A list of parameter IDs as an array of String. Amount must match with amount of paramTypes.
     */
    private void addRoboFunctionToSymbolTable(String returnTypeString, String functionId, String[] paramTypes, String[] paramIds) {
        ArrayList<TypeNode> typeNodes = new ArrayList<>();
        ArrayList<IdNode> paramNodes = new ArrayList<>();


        if (paramTypes != null) {
            for (String paramType : paramTypes) {
                typeNodes.add(new TypeNode(paramType));
            }
        }
        if (paramIds != null) {
            for (String paramId : paramIds) {
                paramNodes.add(new IdNode(paramId));
            }
        }
        if (typeNodes.size() != paramNodes.size()) {
            throw new IllegalArgumentException("Mismatch between number of parameter types and parameter IDs in function definition.");
        }

        DefineFunctionNode defFuncNode = new DefineFunctionNode(
            new TypeNode(returnTypeString),
            new IdNode(functionId),
            new FormalParamsNode(typeNodes, paramNodes)
        );

        symbolTable.peek().put(functionId, defFuncNode);
    }

    private void translateRoboFunctionsToStrava() {
        roboFunctions.put("log", "System.out.println");
        roboFunctions.put("move", "ahead");
        roboFunctions.put("rotate", "turnRight");
        roboFunctions.put("rotateGun", "turnGunRight");
        roboFunctions.put("rotateRadar", "turnRadarRight");
    }

    /**
     * Adds all of Robocode's functions to symbol table in Strava's format (e.g. text instead of String)
     * Very nice and reliable and pretty code. I'm lovin' it!
     */
    private void addFunctionTokens() {
        addRoboFunctionToSymbolTable("void", "addCustomEvent", new String[]{"Condition"}, new String[]{"condition"});
        addRoboFunctionToSymbolTable("void", "ahead", new String[]{"num"}, new String[]{"distance"});
        addRoboFunctionToSymbolTable("void", "back", new String[]{"num"}, new String[]{"distance"});
        addRoboFunctionToSymbolTable("void", "changeStrategy", new String[]{"text"}, new String[]{"newStrategy"});
        addRoboFunctionToSymbolTable("void", "clearAllEvents", null, null);
        addRoboFunctionToSymbolTable("void", "doNothing", null, null);
        addRoboFunctionToSymbolTable("void", "execute", null, null);
        addRoboFunctionToSymbolTable("void", "fire", new String[]{"num"}, new String[]{"power"});
        addRoboFunctionToSymbolTable("num", "getBattleFieldHeight", null, null);
        addRoboFunctionToSymbolTable("num", "getBattleFieldWidth", null, null);
        addRoboFunctionToSymbolTable("num", "getDataQuotaAvailable", null, null);
        addRoboFunctionToSymbolTable("num", "getDistanceRemaining", null, null);
        addRoboFunctionToSymbolTable("num", "getEnergy", null, null);
        addRoboFunctionToSymbolTable("num", "getEventPriority", new String[]{"text"}, new String[]{"eventClass"});
        addRoboFunctionToSymbolTable("num", "getGunCoolingRate", null, null);
        addRoboFunctionToSymbolTable("num", "getGunHeading", null, null);
        addRoboFunctionToSymbolTable("num", "getGunHeadingRadians", null, null);
        addRoboFunctionToSymbolTable("num", "getGunHeat", null, null);
        addRoboFunctionToSymbolTable("num", "getGunTurnRemaining", null, null);
        addRoboFunctionToSymbolTable("num", "getGunTurnRemainingRadians", null, null);
        addRoboFunctionToSymbolTable("num", "getHeading", null, null);
        addRoboFunctionToSymbolTable("num", "getHeadingRadians", null, null);
        addRoboFunctionToSymbolTable("num", "getHeight", null, null);
        addRoboFunctionToSymbolTable("text", "getName", null, null);
        addRoboFunctionToSymbolTable("num", "getNumRounds", null, null);
        addRoboFunctionToSymbolTable("num", "getNumSentries", null, null);
        addRoboFunctionToSymbolTable("num", "getOthers", null, null);
        addRoboFunctionToSymbolTable("num", "getRadarHeading", null, null);
        addRoboFunctionToSymbolTable("num", "getRadarHeadingRadians", null, null);
        addRoboFunctionToSymbolTable("num", "getRadarTurnRemaining", null, null);
        addRoboFunctionToSymbolTable("num", "getRadarTurnRemainingRadians", null, null);
        addRoboFunctionToSymbolTable("num", "getRoundNum", null, null);
        addRoboFunctionToSymbolTable("num", "getSentryBorderSize", null, null);
        addRoboFunctionToSymbolTable("num", "getTime", null, null);
        addRoboFunctionToSymbolTable("num", "getTurnRemaining", null, null);
        addRoboFunctionToSymbolTable("num", "getTurnRemainingRadians", null, null);
        addRoboFunctionToSymbolTable("num", "getVelocity", null, null);
        addRoboFunctionToSymbolTable("num", "getWidth", null, null);
        addRoboFunctionToSymbolTable("num", "getX", null, null);
        addRoboFunctionToSymbolTable("num", "getY", null, null);
        addRoboFunctionToSymbolTable("bool", "isAdjustGunForRobotTurn", null, null);
        addRoboFunctionToSymbolTable("bool", "isAdjustRadarForGunTurn", null, null);
        addRoboFunctionToSymbolTable("bool", "isAdjustRadarForRobotTurn", null, null);
        addRoboFunctionToSymbolTable("void", "log", new String[]{"text"}, new String[]{"input"});
        addRoboFunctionToSymbolTable("void", "move", new String[]{"num"}, new String[]{"distance"});
        addRoboFunctionToSymbolTable("void", "onBattleEnded", new String[]{"BattleEndedEvent"}, new String[]{"event"});
        addRoboFunctionToSymbolTable("void", "onBulletHit", new String[]{"BulletHitEvent"}, new String[]{"event"});
        addRoboFunctionToSymbolTable("void", "onBulletHitBullet", new String[]{"BulletHitBulletEvent"}, new String[]{"event"});
        addRoboFunctionToSymbolTable("void", "onBulletMissed", new String[]{"BulletMissedEvent"}, new String[]{"event"});
        addRoboFunctionToSymbolTable("void", "onCustomEvent", new String[]{"CustomEvent"}, new String[]{"event"});
        addRoboFunctionToSymbolTable("void", "onDeath", new String[]{"DeathEvent"}, new String[]{"event"});
        addRoboFunctionToSymbolTable("void", "onHitByBullet", new String[]{"HitByBulletEvent"}, new String[]{"event"});
        addRoboFunctionToSymbolTable("void", "onHitRobot", new String[]{"HitRobotEvent"}, new String[]{"event"});
        addRoboFunctionToSymbolTable("void", "onHitWall", new String[]{"HitWallEvent"}, new String[]{"event"});
        addRoboFunctionToSymbolTable("void", "onKeyPressed", new String[]{"KeyEvent"}, new String[]{"event"});
        addRoboFunctionToSymbolTable("void", "onKeyReleased", new String[]{"KeyEvent"}, new String[]{"event"});
        addRoboFunctionToSymbolTable("void", "onKeyTyped", new String[]{"KeyEvent"}, new String[]{"event"});
        addRoboFunctionToSymbolTable("void", "onMouseClicked", new String[]{"MouseEvent"}, new String[]{"event"});
        addRoboFunctionToSymbolTable("void", "onMouseDragged", new String[]{"MouseEvent"}, new String[]{"event"});
        addRoboFunctionToSymbolTable("void", "onMouseEntered", new String[]{"MouseEvent"}, new String[]{"event"});
        addRoboFunctionToSymbolTable("void", "onMouseExited", new String[]{"MouseEvent"}, new String[]{"event"});
        addRoboFunctionToSymbolTable("void", "onMouseMoved", new String[]{"MouseEvent"}, new String[]{"event"});
        addRoboFunctionToSymbolTable("void", "onMousePressed", new String[]{"MouseEvent"}, new String[]{"event"});
        addRoboFunctionToSymbolTable("void", "onMouseReleased", new String[]{"MouseEvent"}, new String[]{"event"});
        addRoboFunctionToSymbolTable("void", "onMouseWheelMoved", new String[]{"MouseWheelEvent"}, new String[]{"event"});
        addRoboFunctionToSymbolTable("void", "onPaint", new String[]{"Graphics2D"}, new String[]{"g"});
        addRoboFunctionToSymbolTable("void", "onRobotDeath", new String[]{"RobotDeathEvent"}, new String[]{"event"});
        addRoboFunctionToSymbolTable("void", "onRoundEnded", new String[]{"RoundEndedEvent"}, new String[]{"event"});
        addRoboFunctionToSymbolTable("void", "onScannedRobot", new String[]{"ScannedRobotEvent"}, new String[]{"event"});
        addRoboFunctionToSymbolTable("void", "onSkippedTurn", new String[]{"SkippedTurnEvent"}, new String[]{"event"});
        addRoboFunctionToSymbolTable("void", "onStatus", new String[]{"StatusEvent"}, new String[]{"event"});
        addRoboFunctionToSymbolTable("void", "onWin", new String[]{"WinEvent"}, new String[]{"event"});
        addRoboFunctionToSymbolTable("void", "removeCustomEvent", new String[]{"Condition"}, new String[]{"condition"});
        addRoboFunctionToSymbolTable("void", "resume", null, null);
        addRoboFunctionToSymbolTable("void", "rotate", new String[]{"num"}, new String[]{"degrees"});
        addRoboFunctionToSymbolTable("void", "run", null, null);
        addRoboFunctionToSymbolTable("void", "scan", null, null);
        addRoboFunctionToSymbolTable("void", "setAdjustGunForRobotTurn", new String[]{"bool"}, new String[]{"independent"});
        addRoboFunctionToSymbolTable("void", "setAdjustRadarForGunTurn", new String[]{"bool"}, new String[]{"independent"});
        addRoboFunctionToSymbolTable("void", "setAdjustRadarForRobotTurn", new String[]{"bool"}, new String[]{"independent"});
        addRoboFunctionToSymbolTable("void", "setAhead", new String[]{"num"}, new String[]{"distance"});
        addRoboFunctionToSymbolTable("void", "setAllColors", new String[]{"Color"}, new String[]{"color"});
        addRoboFunctionToSymbolTable("void", "setBack", new String[]{"num"}, new String[]{"distance"});
        addRoboFunctionToSymbolTable("void", "setBodyColor", new String[]{"Color"}, new String[]{"color"});
        addRoboFunctionToSymbolTable("void", "setBulletColor", new String[]{"Color"}, new String[]{"color"});
        addRoboFunctionToSymbolTable("void", "setColors", new String[]{"Color", "Color", "Color", "Color", "Color"}, new String[]{"bodyColor", "gunColor", "radarColor", "bulletColor", "scanArcColor"});
        addRoboFunctionToSymbolTable("void", "setDebugProperty", new String[]{"text", "text"}, new String[]{"key", "value"});
        addRoboFunctionToSymbolTable("void", "setEventPriority", new String[]{"text"}, new String[]{"eventClass"});
        addRoboFunctionToSymbolTable("void", "setFire", new String[]{"num"}, new String[]{"power"});
        addRoboFunctionToSymbolTable("void", "setGunColor", new String[]{"Color"}, new String[]{"color"});
        addRoboFunctionToSymbolTable("void", "setInterruptible", new String[]{"bool"}, new String[]{"interruptible"});
        addRoboFunctionToSymbolTable("void", "setMaxTurnRate", new String[]{"num"}, new String[]{"newMaxTurnRate"});
        addRoboFunctionToSymbolTable("void", "setMaxVelocity", new String[]{"num"}, new String[]{"newMaxVelocity"});
        addRoboFunctionToSymbolTable("void", "setRadarColor", new String[]{"Color"}, new String[]{"color"});
        addRoboFunctionToSymbolTable("void", "setResume", null, null);
        addRoboFunctionToSymbolTable("void", "setScanColor", new String[]{"Color"}, new String[]{"color"});
        addRoboFunctionToSymbolTable("void", "setStop", null, null);
        addRoboFunctionToSymbolTable("void", "setStop", new String[]{"bool"}, new String[]{"overwrite"});
        addRoboFunctionToSymbolTable("void", "setTurnGunLeft", new String[]{"num"}, new String[]{"degrees"});
        addRoboFunctionToSymbolTable("void", "setTurnGunLeftRadians", new String[]{"num"}, new String[]{"radians"});
        addRoboFunctionToSymbolTable("void", "setTurnGunRight", new String[]{"num"}, new String[]{"degrees"});
        addRoboFunctionToSymbolTable("void", "setTurnGunRightRadians", new String[]{"num"}, new String[]{"radians"});
        addRoboFunctionToSymbolTable("void", "setTurnLeft", new String[]{"num"}, new String[]{"degrees"});
        addRoboFunctionToSymbolTable("void", "setTurnLeftRadians", new String[]{"num"}, new String[]{"radians"});
        addRoboFunctionToSymbolTable("void", "setTurnRadarLeft", new String[]{"num"}, new String[]{"degrees"});
        addRoboFunctionToSymbolTable("void", "setTurnRadarLeftRadians", new String[]{"num"}, new String[]{"radians"});
        addRoboFunctionToSymbolTable("void", "setTurnRadarRight", new String[]{"num"}, new String[]{"degrees"});
        addRoboFunctionToSymbolTable("void", "setTurnRadarRightRadians", new String[]{"num"}, new String[]{"radians"});
        addRoboFunctionToSymbolTable("void", "setTurnRight", new String[]{"num"}, new String[]{"degrees"});
        addRoboFunctionToSymbolTable("void", "setTurnRightRadians", new String[]{"num"}, new String[]{"radians"});
        addRoboFunctionToSymbolTable("void", "stop", null, null);
        addRoboFunctionToSymbolTable("void", "turnGunLeft", new String[]{"num"}, new String[]{"degrees"});
        addRoboFunctionToSymbolTable("void", "turnGunLeftRadians", new String[]{"num"}, new String[]{"radians"});
        addRoboFunctionToSymbolTable("void", "turnGunRight", new String[]{"num"}, new String[]{"degrees"});
        addRoboFunctionToSymbolTable("void", "turnGunRightRadians", new String[]{"num"}, new String[]{"radians"});
        addRoboFunctionToSymbolTable("void", "turnLeft", new String[]{"num"}, new String[]{"degrees"});
        addRoboFunctionToSymbolTable("void", "turnLeftRadians", new String[]{"num"}, new String[]{"radians"});
        addRoboFunctionToSymbolTable("void", "turnRadarLeft", new String[]{"num"}, new String[]{"degrees"});
        addRoboFunctionToSymbolTable("void", "turnRadarLeftRadians", new String[]{"num"}, new String[]{"radians"});
        addRoboFunctionToSymbolTable("void", "turnRadarRight", new String[]{"num"}, new String[]{"degrees"});
        addRoboFunctionToSymbolTable("void", "turnRadarRightRadians", new String[]{"num"}, new String[]{"radians"});
        addRoboFunctionToSymbolTable("void", "turnRight", new String[]{"num"}, new String[]{"degrees"});
        addRoboFunctionToSymbolTable("void", "turnRightRadians", new String[]{"num"}, new String[]{"radians"});
        addRoboFunctionToSymbolTable("void", "waitFor", new String[]{"Condition"}, new String[]{"condition"});
    }

    private void openScope() {
        symbolTable.push(new HashMap<>());
    }

    private void closeScope() {
        symbolTable.pop();
    }

    private void bindIdToDeclaration(IdNode idNode) {
        boolean isDeclared = false;
        for (int i = symbolTable.size() - 1; i >= 0; i--) {
            if (symbolTable.get(i).containsKey(idNode.id)) {
                idNode.declarationNode = (DeclarationNode) symbolTable.get(i).get(idNode.id);
                isDeclared = true;
            }
        }
        if (!isDeclared) {
            hasBindingErrorOccured = true;
            Main.CompileErrors.add(new UndefinedError(idNode.columnNumber, idNode.lineNumber, idNode.id));
        }
    }

    private StructDefinitionNode bindFieldXToDeclaration(List<IdNode> idNodes) {
        IdNode structId = idNodes.get(0);
        for (int i = symbolTable.size() - 1; i >= 0; i--) {
            if (symbolTable.get(i).containsKey(structId.id)) {
                ASTNode astNode = symbolTable.get(i).get(structId.id);
                DeclarationNode declarationNode = (DeclarationNode) astNode;
                if (declarationNode.structDefinitionNode != null) {
                    return declarationNode.structDefinitionNode;
                }
            }
        }
        return null;
    }

    private DefineFunctionNode bindFunctionCallToDeclaration(ASTNode node, String idName
    ) {
        DefineFunctionNode defineFunctionNode = null;
        boolean isDeclared = false;
        if (roboFunctions.containsKey(idName)) {
            isDeclared = true;
        }
        if(insideStrategy) {
            if (functionEnvironment.containsKey(idName)) {
                defineFunctionNode = functionEnvironment.get(idName);
                isDeclared = true;
            } else if (strategyEnvironment.get("default").containsKey(idName)) {
                defineFunctionNode = strategyEnvironment.get("default").get(idName);
                isDeclared = true;
            }
        }
        if(!isDeclared && symbolTable.get(0).containsKey(idName)){
            defineFunctionNode = (DefineFunctionNode)symbolTable.get(0).get(idName);
            isDeclared = true;
        }

        if (!isDeclared) {
            hasBindingErrorOccured = true;
            String funcName = "";
            if (node instanceof FunctionCallNode) {
                funcName = ((FunctionCallNode) node).idNode.id;
            } else if (node instanceof ExprFunctionCallNode) {
                funcName = ((ExprFunctionCallNode) node).idNode.id;
            }
            Main.CompileErrors.add(new CompilerError.UndefinedError(node.columnNumber, node.lineNumber, funcName));
        }
        return defineFunctionNode;
    }

    private void bindStructInitializationToDefinition(StructInitializationNode structInitializationNode) {
        boolean isStructDefined = false;
        for (int i = symbolTable.size() - 1; i >= 0; i--) {
            if (symbolTable.get(i).containsKey(structInitializationNode.typeNode.type)) {
                structInitializationNode.structDefinitionNode = (StructDefinitionNode) symbolTable.get(i).get(structInitializationNode.typeNode.type);
                isStructDefined = true;
                checkStruckInitValidity(structInitializationNode);
            }
        }
        if (!isStructDefined) {
            Main.CompileErrors.add(new CompilerError.UndefinedError(structInitializationNode.columnNumber,
                structInitializationNode.lineNumber,
                structInitializationNode.typeNode.type));
        }
    }

    private class DeclBoolTuple {
        DeclarationNode declarationNode;
        Boolean isBound;

        public DeclBoolTuple(DeclarationNode node) {
            declarationNode = node;
            isBound = false;
        }
    }

    private void checkStruckInitValidity(StructInitializationNode structInitializationNode) {
        HashMap<String, DeclBoolTuple> oneToOneDeclAssMap = new HashMap<>();

        structInitializationNode.structDefinitionNode.declarationNodes.forEach((d) -> oneToOneDeclAssMap.put(d.idNode.id, new DeclBoolTuple(d)));
        boolean structLiteralErrorOccured = false;
        for (AssignmentNode assignmentNode : structInitializationNode.assignments) {
            DeclBoolTuple tuple = oneToOneDeclAssMap.get(assignmentNode.idNode.id);
            if (tuple != null && !tuple.isBound) {
                assignmentNode.idNode.declarationNode = tuple.declarationNode;
                tuple.isBound = true;
                visit(assignmentNode.exprNode);
            } else if (tuple != null) {
                Main.CompileErrors.add(new CompilerError.DuplicateFieldAssignmentInStructLitteralError(assignmentNode.columnNumber, assignmentNode.lineNumber, assignmentNode.idNode.id));
                break;
            } else {
                structLiteralErrorOccured = true;
                Main.CompileErrors.add(new CompilerError.InvalidStructLitteralError(structInitializationNode.columnNumber,
                    structInitializationNode.lineNumber, getExpectedStructLitteralSignature(structInitializationNode.structDefinitionNode.declarationNodes),
                    getActualStructLitteralSignature(structInitializationNode.assignments)));
                break;
            }
        }

        for (DeclBoolTuple tuple : oneToOneDeclAssMap.values()) {
            if (!tuple.isBound && !structLiteralErrorOccured) {
                Main.CompileErrors.add(new CompilerError.InvalidStructLitteralError(structInitializationNode.columnNumber,
                    structInitializationNode.lineNumber, getExpectedStructLitteralSignature(structInitializationNode.structDefinitionNode.declarationNodes),
                    getActualStructLitteralSignature(structInitializationNode.assignments)));
            }
        }
    }

    private String getActualStructLitteralSignature(List<AssignmentNode> assignmentNodes) {
        int length = assignmentNodes.size();
        String stringRep = "[";
        for (int i = 0; i < length; i++) {
            if (i == length - 1) {
                stringRep += assignmentNodes.get(i).idNode.id + " := expression]";
            } else {
                stringRep += assignmentNodes.get(i).idNode.id + " := expression, ";
            }
        }
        return stringRep;
    }

    private String getExpectedStructLitteralSignature(List<DeclarationNode> declarationNodes) {
        int lenght = declarationNodes.size();
        String stringRep = "[";
        for (int i = 0; i < lenght; i++) {
            if (i == lenght - 1) {
                stringRep += declarationNodes.get(i).idNode.id + " := expression]";
            } else {
                stringRep += declarationNodes.get(i).idNode.id + " := expression, ";
            }
        }
        return stringRep;
    }

    private void bindInstantiatedStructToDef(DeclarationNode node) {
        boolean isStructDefined = false;
        for (int i = symbolTable.size() - 1; i >= 0; i--) {
            if (symbolTable.get(i).containsKey(node.typeNode.type)) {
                node.structDefinitionNode = (StructDefinitionNode) symbolTable.get(i).get(node.typeNode.type);
                isStructDefined = true;
            }
        }
        if (!isStructDefined) {
            Main.CompileErrors.add(new CompilerError.UndefinedError(node.columnNumber, node.lineNumber, node.typeNode.type));
        }
    }

    @Override
    public void visit(StrategyNode node) {
        insideStrategy = true;
        functionEnvironment = strategyEnvironment.get(node.idNode.id);
        super.visit(node);
        insideStrategy = false;
    }

    @Override
    public void visit(DefaultStrategyNode node) {
        insideStrategy = true;
        functionEnvironment = strategyEnvironment.get("default");
        super.visit(node);
        insideStrategy = false;
    }

    @Override
    public void visit(FunctionCallNode node) {
        node.defineFunctionNode = bindFunctionCallToDeclaration(node, node.idNode.id);
        if (node.actualParams != null) {
            visit(node.actualParams);
        }
    }

    @Override
    public void visit(ExprFunctionCallNode node) {
        node.defineFunctionNode = bindFunctionCallToDeclaration(node, node.idNode.id);
    }

    @Override
    public void visit(IdNode node) {
        if (!node.isDeclaration)
            bindIdToDeclaration(node);
    }

    @Override
    public void visit(StructInitializationNode node) {

        bindStructInitializationToDefinition(node);

    }

    @Override
    public void visit(DeclarationNode node) {
        if (!doesDeclExistLocally(node)) {
            switch (node.typeNode.type) {
                case "num":
                case "text":
                case "bool":
                    symbolTable.peek().put(node.idNode.id, node);
                    break;
                default:
                    bindInstantiatedStructToDef(node);
                    symbolTable.peek().put(node.idNode.id, node);
            }
            if (node.exprNode != null) {
                visit(node.exprNode);
            }
        } else hasBindingErrorOccured = true;

    }

    // TODO : fields with same names in different struct definit
    private boolean doesDeclExistLocally(DeclarationNode node) {
        for (int i = symbolTable.size() - 1; i >= 0; i--) {
            if (symbolTable.get(i).containsKey(node.idNode.id)) {
                DeclarationNode declNodeFound = (DeclarationNode) symbolTable.get(i).get(node.idNode.id);
                if (!declNodeFound.IsGlobal) {
                    Main.CompileErrors.add(new CompilerError.RedeclarationError(node.columnNumber, node.lineNumber, node.idNode.id));
                    return true;
                } else {
                    if (i == symbolTable.size() - 1) {
                        Main.CompileErrors.add(new CompilerError.RedeclarationError(node.columnNumber, node.lineNumber, node.idNode.id));
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public void visit(DefineFunctionNode node) {
        if (node.formalParamsNode != null) {
            isParamsVisited = true;
            formalParamsNode = node.formalParamsNode;
        }
        visit(node.blockNode);
    }

    private void includeFormalParamsInScope() {
        for (int i = 0; i < formalParamsNode.idNodes.size(); i++) {
            DeclarationNode dcNode = new DeclarationNode();
            dcNode.typeNode = formalParamsNode.typeNodes.get(i);
            dcNode.idNode = formalParamsNode.idNodes.get(i);
            visit(dcNode);
        }
        isParamsVisited = false;
    }


    @Override
    public void visit(StructDefinitionNode node) {
        symbolTable.peek().put(node.typeNode.type, node);
    }

    @Override
    public void visit(FieldIdNode node) {
        node.structDefinitionNode = bindFieldXToDeclaration(node.idNodes);
        if (node.structDefinitionNode == null) {
            Main.CompileErrors.add(new CompilerError.UndefinedError(node.columnNumber, node.lineNumber, node.idNodes.get(0).id));
        }
    }

    @Override
    public void visit(FieldValueNode node) {
        node.structDefinitionNode = bindFieldXToDeclaration(node.idNodes);
        if (node.structDefinitionNode == null) {
            Main.CompileErrors.add(new CompilerError.UndefinedError(node.columnNumber, node.lineNumber, node.idNodes.get(0).id));
        }
    }


    private boolean isParamsVisited = false;
    private FormalParamsNode formalParamsNode = null;

    @Override
    public void visit(BlockNode node) {
        openScope();
        if (isParamsVisited) includeFormalParamsInScope();
        super.visit(node);
        closeScope();
    }
}
