import Enums.BinaryOperator;

import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class JavaCodeGenerator extends Visitor {
	private int indentationLevel = 0;

    private String className;
	private ArrayList<String> strategies;
	private ArrayList<NewEventNode> newCustomEvents;
	private PrintWriter writer;
    private ArrayList<String> events;

	JavaCodeGenerator(ArrayList<String> strategies, ArrayList<NewEventNode> newCustomEvents) {
		super();
		this.strategies = strategies;
		this.newCustomEvents = newCustomEvents;
		events = new ArrayList<>();
		AddAllEventsToList();
		try {
			writer = new PrintWriter(new FileOutputStream("./StrategyJava/JavaCodeGeneratorOutput.java", false));
		} catch (Exception e) {
			e.printStackTrace();
		}
        className = "robot_MyRobot";
    }

    @Override
    public void visit(FunctionCallNode node) {
	    if (BindingVisitor.roboFunctions.containsKey(node.idNode.id)) {
            Emit(BindingVisitor.roboFunctions.get(node.idNode.id) + "(", 0);
        } else {
	        Emit(node.idNode.id + "(", 0);
        }
        visit(node.actualParams);
        EmitNoIndent(");\n");
    }

    @Override
    public void visit(ReturnStatementNode node) {
        Emit("return ", 0);
        visit(node.exprNode);
        EmitNoIndent(";\n");
    }

    private String RoboToJavaType(String roboType) {
		switch(roboType) {
			case "num":
				return "double";
			case "text":
				return "String";
			case "bool":
				return "boolean";
			default:
				return roboType;
		}
	}

	private void Emit(String emitString){
		writer.print(indent() + emitString);
	}

	private void Emit(String emitString, int numberOfNewLines){
		writer.print(indent() + emitString + new String(new char[numberOfNewLines]).replace("\0", "\n"));
	}

	private void EmitNoIndent(String emitString) {
		writer.print(emitString);
	}

	private String indent(){
		return new String(new char[indentationLevel]).replace("\0", "    ");
	}

    private void EmitImports() {
        Emit("import robocode.*; \n" +
                "import java.awt.Color; \n" +
                "import java.lang.Math; \n" +
                "import java.util.HashMap;", 2);
    }

    private void EmitAutoGenDoc() {
        Emit("/**\n * Automatically generated by Strava for " +
                System.getProperty("user.name") +  " on " +
                new SimpleDateFormat("yyyy-MM-dd").format(new Date()) +
                ".\n */", 2);
    }

	private void EmitChangeStrategyDefinition() {
	    Emit("private void changeStrategy(String strategyName) {", 1);
	    indentationLevel++;
	    Emit("Strategy newStrategy = strategies.get(strategyName);", 1);
	    Emit("if (newStrategy != null) {", 1);
	    indentationLevel++;
	    Emit("System.out.println(\"Changing to: \" + strategyName);", 1);
	    Emit("currentStrategy = newStrategy;", 1);
	    indentationLevel--;
	    Emit("} else {", 1);
	    indentationLevel++;
	    Emit("throw new RuntimeException(\"Cannot find strategy!\");", 1);
	    indentationLevel--;
        Emit("}", 1);
        indentationLevel--;
	    Emit("}", 2);
    }

	@Override
	public void visit(TypeNode node) {
		EmitNoIndent(RoboToJavaType(node.type));
	}

	@Override
	public void visit(StmtNode node) {
		if (node != null) {
			super.visit(node);
		}
	}

	@Override
	public void visit(BlockNode node) {
		if (node == null) return;

		EmitNoIndent(" {\n");
		indentationLevel++;
		for (StmtNode stmtNode : node.functionStmtNodes) {
			visit(stmtNode);
		}
		indentationLevel--;
		Emit("}", 1);
	}

	@Override
	public void visit(BinaryExprNode node) {
		if (node.binaryOperator == BinaryOperator.POWER) {
			EmitNoIndent("Math.pow(");
			visit(node.leftNode);
			EmitNoIndent(", ");
			visit(node.rightNode);
			EmitNoIndent(")");
		} else {
			visit(node.leftNode);
			EmitNoIndent(BinaryOperatorToJavaOperator(node.binaryOperator));
			visit(node.rightNode);
		}

	}

	// Capitalizes first letter
	public void visit(TypeNode node, boolean indent) {
		String javaType = RoboToJavaType(node.type);
		if (javaType.endsWith("Event")) {
            if(indent) {
                Emit(javaType.substring(0,1).toUpperCase() + javaType.substring(1), 0);
            } else {
                EmitNoIndent(javaType.substring(0,1).toUpperCase() + javaType.substring(1));
            }
        } else {
		    if (indent) {
		        Emit(javaType, 0);
		    } else {
		        EmitNoIndent(javaType);
            }
        }
	}

    @Override
    public void visit(BehaviorFunctionNode node) {
	    Emit("\n", 0);
        Emit("public void " + node.eventName.id + "(", 0);
        if(node.eventType != null) {
            EmitNoIndent(node.eventName.id.replace("on", "") + "Event e");
		}
        EmitNoIndent(")");
        visit(node.blockNode);
    }

    @Override
	public void visit(DeclarationNode node) {
		visit(node.typeNode, true);
		EmitNoIndent(" " + node.idNode.id);
		if (node.exprNode != null) {
			EmitNoIndent(" = ");
			visit(node.exprNode);
		}
		// This fucks up structInit
		if(node.exprNode instanceof StructInitializationNode) {
			EmitNewLine();
		} else {
			EmitNoIndent("; \n");
		}
	}

    /**
     * Check if user wants to specifically implement an event in default strategy.
     * If not, implement an empty event.
     */
	@Override
	public void visit(DefaultStrategyNode node) {
		Emit("class strategy_defaultStrategy implements Strategy {", 1);
		indentationLevel++;
        visit(node.strategyDefinition.runNode);
        if(node.strategyDefinition.runNode == null) {
            Emit("public void run () { }", 1);
        }
		visit(node.strategyDefinition.functionsNode);

        String fullEventName;
        for (String event : events) {
            fullEventName = "on" + event;
            boolean isImplemented = false;
            if (node.strategyDefinition.functionsNode != null) {
                for (BehaviorFunctionNode behavior : node.strategyDefinition.functionsNode.behaviorFunctions) {
                    String eventName = behavior.eventName.id;
                    if (fullEventName.equals(eventName)) {
                        isImplemented = true;
                    }
                }
                if (!isImplemented) {
                    EmitDefaultInterfaceEventImplementation(event);
                }
            }
        }
        indentationLevel--;
		Emit("}", 2);
	}

	@Override
	public void visit(DefineFunctionNode node) {
		Emit("public ", 0);
		visit(node.typeNode);
		EmitNoIndent(" " + node.idNode.id);
		EmitNoIndent("(");
		if (node.formalParamsNode != null) {
			visit(node.formalParamsNode);
		}
		EmitNoIndent(")");
		visit(node.blockNode);
	}

	@Override
	public void visit(UnaryExprNode node) {
		switch(node.unaryOperator){
			case NEGATEBOOL:
				EmitNoIndent("!");
				visit(node.exprNode);
				break;
			case NEGATE:
				EmitNoIndent("-");
				visit(node.exprNode);
				break;
			case PARANTHESIS:
				EmitNoIndent("(");
				visit(node.exprNode);
				EmitNoIndent(")");
				break;
		}
	}

    private void AddAllEventsToList() {
        events.add("onBattleEnded");
        events.add("onBulletHit");
        events.add("onBulletHitBullet");
        events.add("onBulletMissed");
        events.add("onDeath");
        events.add("onHitByBullet");
        events.add("onHitRobot");
        events.add("onHitWall");
        events.add("onRobotDeath");
        events.add("onRoundEnded");
        events.add("onScannedRobot");
        events.add("onStatus");
        events.add("onWin");
        newCustomEvents.forEach(x -> events.add(x.idNode.id));
    }

    private void EmitAllInterfaceEventDefinitions() {
	    for (String eventString : events) {
            EmitDefaultInterfaceEventImplementation(eventString);
        }
	}

    private void EmitDefaultInterfaceEventImplementation(String event) {
        boolean isCustomEvent = false;
	    for (NewEventNode newCustomEvent : newCustomEvents) {
            if (event.equals(newCustomEvent.idNode.id))
                isCustomEvent = true;
        }

        if (isCustomEvent) {
            Emit("public void " + event + "();", 1);

        } else {
            Emit("public void " + event + "(" + event.replace("on", "") + "Event e);", 1);
        }
    }

    // TODO: Split this marvelous monster into sub-functions
	@Override
	public void visit(ProgNode node) {
        EmitAutoGenDoc();
		Emit("package Strava;", 2);
		EmitImports();

        Emit("interface Strategy {", 1);
		indentationLevel++;
		Emit("void run();", 1);
        EmitAllInterfaceEventDefinitions();
		indentationLevel--;
		Emit("}", 2);

		Emit("public class " + className + " extends AdvancedRobot implements Strategy {", 1);
		indentationLevel++;

		Emit("public HashMap<String, Strategy> strategies;", 1);
		Emit("public Strategy currentStrategy;", 2);

		Emit("public " + className + "() {", 1);
		indentationLevel++;
		if(node.setupNode != null) {
            Emit("setup();", 1);
        }
        Emit("currentStrategy = new strategy_defaultStrategy();", 1);
        Emit("strategies = new HashMap<String, Strategy>();", 1);
        for (String strategy : strategies) {
            if (!strategy.startsWith("default")) {
                Emit("strategies.put(\"" + strategy + "\", " + "new strategy_" + strategy + "Strategy());", 1);
            } else {
                Emit("strategies.put(\"" + strategy + "\", currentStrategy);", 1);
            }
        }
        indentationLevel--;
        Emit("}", 2);

		Emit("public void run() {", 1);
		indentationLevel++;
        Emit("System.out.println(\"Run: \" + currentStrategy.toString());", 1);

        for	(NewEventNode newCustomEvent : newCustomEvents) {
			Emit("addCustomEvent(new Condition(\"" + newCustomEvent.idNode.id + "\") {", 1);
			indentationLevel++;
			Emit("public boolean test()");
			visit(newCustomEvent.blockNode);
			indentationLevel--;
			Emit("});", 1); // end addCustomEvent

		}
		EmitNewLine();

		Emit("while (true) {", 1);
		indentationLevel++;
		Emit("currentStrategy.run();", 1);
		indentationLevel--;
		Emit("}", 1); // end while true
		indentationLevel--;
		Emit("}", 2); // end run

		EmitChangeStrategyDefinition();
		EmitOnCustomEvent();

        for (String event : events) {
            Emit("public void " + event + "() { currentStrategy." + event + "(); }", 1);
        }
        EmitNewLine();

		super.visit(node);
		indentationLevel--;
		Emit("}", 2);

		EmitStructDefinitions(node);

		writer.close();
	}

	private void EmitNewLine() {
	    Emit("", 1);
    }

	private void EmitOnCustomEvent() {
	    if(newCustomEvents == null || newCustomEvents.size() == 0) return;
		Emit("public void onCustomEvent(CustomEvent e) {", 1);
		indentationLevel++;
		for (NewEventNode newCustomEvent : newCustomEvents) {
			Emit("if (e.getCondition().getName().equals(\"" + newCustomEvent.idNode.id + "\")) {", 1);
			indentationLevel++;
			Emit("currentStrategy.on" + newCustomEvent.idNode.id + "();", 1);
			// trigger -= 20;
			indentationLevel--;
			Emit("}", 1);
		}
		indentationLevel--;
		Emit("}", 2); // end onCustomEvent
	}

	@Override
	public void visit(StructDefinitionNode node) {
		// This method is intentionally empty
        // It is handled in EmitStructDefinitions
	}

	private void EmitStructDefinitions(ProgNode node) {
		if(node.setupNode == null) return;
        for (StmtNode stmtNode : node.setupNode.setupBlockNode.setupStmts) {
            if (stmtNode instanceof StructDefinitionNode) {
                Emit("class " + ((StructDefinitionNode) stmtNode).typeNode.type + " {", 1);
                indentationLevel++;
                ((StructDefinitionNode) stmtNode).declarationNodes.forEach(dn -> visit(dn));

                EmitNewLine();
                Emit("public " + ((StructDefinitionNode) stmtNode).typeNode.type + "(");
                List<DeclarationNode> declarationNodes = ((StructDefinitionNode) stmtNode).declarationNodes;
                for (int i = 0; i < declarationNodes.size(); i++) {
                    DeclarationNode declarationNode = declarationNodes.get(i);
                    EmitNoIndent(declarationNode.typeNode.type + " " + declarationNode.idNode.id);
                    if(i + 1 != declarationNodes.size()) {
                        EmitNoIndent(", ");
                    }
                }
                EmitNoIndent(") {\n");
                indentationLevel++;
                for (DeclarationNode declarationNode : ((StructDefinitionNode) stmtNode).declarationNodes) {
                    Emit("this." + declarationNode.idNode.id + " = " + declarationNode.idNode.id + ";", 1);
                }
                indentationLevel--;
                Emit("}", 1);
                indentationLevel--;
                Emit("}", 2);
            }
        }
    }

	@Override
	public void visit(RunNode node) {
	    if(node == null) return;

		Emit("public void run()", 0);
		super.visit(node);
	}

	@Override
	public void visit(IdNode node) {
		EmitNoIndent(node.id);
	}

	@Override
	public void visit(LiteralNode node) {
		EmitNoIndent(node.literalText);
	}

	public void visit(IdNode node, boolean indent) {
		if (indent) {
			Emit(node.id, 0);
		} else {
			EmitNoIndent(" " + node.id);
		}
	}

	@Override
	public void visit(SetupNode node) {
        for (StmtNode decl : node.setupBlockNode.setupStmts) {
            if (decl instanceof DeclarationNode) {
                visit(decl);
            }
        }

		Emit("public void setup() {", 1);
        indentationLevel++;
        for (StmtNode decl : node.setupBlockNode.setupStmts) {
            if (!(decl instanceof DeclarationNode)) {
                visit(decl);
            }
        }
		indentationLevel--;
		Emit("}", 2);
	}

	@Override
	public void visit(StrategyNode node) {
		Emit("class ", 0);
		EmitNoIndent("strategy_" + node.idNode.id);
		EmitNoIndent("Strategy extends strategy_defaultStrategy { \n");
		indentationLevel++;
		visit(node.strategyDefinition);
		indentationLevel--;
		Emit("}", 2);
	}

    @Override
    public void visit(ActualParamsNode node) {
		for (int i = 0; i < node.exprs.size(); i++) {
			visit(node.exprs.get(i));
			if(i != node.exprs.size() - 1) {
				EmitNoIndent(", ");
			}
		}
    }

    @Override
    public void visit(AssignmentNode node) {
		Emit(node.idNode.id);
		EmitNoIndent(" = ");
		visit(node.exprNode);
		EmitNoIndent(";\n");
    }

    public void visit(AssignmentNode node, boolean indent) {
		if (indent) {
		    Emit(node.idNode.id);
        } else {
		    EmitNoIndent(node.idNode.id);
        }
		EmitNoIndent(" = ");
		visit(node.exprNode);
    }

    @Override
    public void visit(ElseIfStatementNode node) {
		Emit("else if ", 0);
		EmitNoIndent("(");
		visit(node.predicate);
		EmitNoIndent(")");
		visit(node.blockNode);
	}

    @Override
    public void visit(FieldAssignmentNode node) {
		// Purely for indent
	    Emit("");
	    visit(node.fieldIdNode);
		EmitNoIndent(" = ");
		visit(node.exprNode);
		EmitNewLine();
    }

    @Override
    public void visit(FieldIdNode node) {
		for (int i = 0; i < node.idNodes.size(); i++) {
			visit(node.idNodes.get(i));

			if(i != node.idNodes.size() - 1) {
				EmitNoIndent(".");
			}
		}
	}

    @Override
    public void visit(FormalParamsNode node) {
		for (int i = 0; i < node.idNodes.size(); i++) {
			visit(node.typeNodes.get(i), false);
			visit(node.idNodes.get(i), false);

			if(i != node.idNodes.size() - 1) {
				EmitNoIndent(", ");
			}
		}
    }

    @Override
    public void visit(FunctionsNode node) {
	    if (node == null) return;
        super.visit(node);
    }

    @Override
    public void visit(IfStatementNode node) {
        Emit("if ", 0);
        EmitNoIndent("(");
        visit(node.predicate);
		EmitNoIndent(")");
        visit(node.ifBlockNode);
        for (ElseIfStatementNode elif : node.elseIfNodes) {
			visit(elif);
		}
		if(node.elseBlockNode != null) {
			Emit("else ", 0);
		}
        visit(node.elseBlockNode);

    }

    @Override
    public void visit(ExprFunctionCallNode node) {
        if(node.fieldIdNode != null)
            visit(node.fieldIdNode);
        else {
            visit(node.idNode);
        }

        EmitNoIndent("(");

        if(node.actualParams != null)
            node.actualParams.accept(this);

        EmitNoIndent(")");

    }

    @Override
    public void visit(LoopNode node) {
		if(node.exprNode == null) {
			Emit("while (true)");
		} else {
			Emit("while (");
			visit(node.exprNode);
			EmitNoIndent(")");
		}
		visit(node.block);
    }

    @Override
    public void visit(NewEventNode node) {
		// This method is intentionally empty
    }

    @Override
    public void visit(SetupBlockNode node) {
		super.visit(node);
    }

    @Override
    public void visit(StrategyDefinitionNode node) {
        visit(node.runNode);
        visit(node.functionsNode);
    }

    @Override
    public void visit(StructInitializationNode node) {
		EmitNoIndent("new ");
		visit(node.typeNode);
		EmitNoIndent("(");
        for (int i = 0; i < node.assignments.size(); i++) {
            AssignmentNode n = node.assignments.get(i);
            visit(n.exprNode);
//            if (node.assignments.size() != 1 && i + 1 != node.assignments.size()) {
            if (i + 1 != node.assignments.size()) {
                EmitNoIndent(", ");
            }
        }
        EmitNoIndent(");\n");
	}

    public String BinaryOperatorToJavaOperator(BinaryOperator binaryOperator) {
		switch (binaryOperator) {
			case PLUS:
				return " + ";
			case MINUS:
				return " - ";
			case MULTIPLY:
				return " * ";
			case DIVISION:
				return " / ";
			case MODULO:
				return " % ";
			case LESSTHANEQUAL:
				return " <= ";
			case GREATERTHANEQUAL:
				return " >= ";
			case AND:
				return " && ";
			case OR:
				return " || ";
			case LESSTHAN:
				return " < ";
			case GREATERTHAN:
				return " > ";
			case EQUAL:
				return " == ";
			case NOTEQUAL:
				return " != ";
			default:
				throw new RuntimeException("Unknown binary operator. This should NEVER happen!");
		}
	}
}
