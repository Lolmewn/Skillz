package nl.lolmen.Skillz;

import nl.lolmen.Skills.SkillsSettings;

public class MathProcessor {

	private static final char[] validOperators = {'/','*','+','-'};

	private static double evaluate(String leftSide, char oper, String rightSide) throws IllegalArgumentException{
		if(SkillsSettings.isDebug()){
			System.out.println("Evaluating: " + leftSide +  " (" + oper + ") " + rightSide);
		}
		double total = 0;
		double leftResult = 0;
		double rightResult = 0;

		int operatorLoc = findOperatorLocation(leftSide);
		if( operatorLoc > 0 && operatorLoc < leftSide.length()-1 ){
			leftResult = evaluate(leftSide.substring(0,operatorLoc),
					leftSide.charAt(operatorLoc),
					leftSide.substring(operatorLoc+1,leftSide.length()));
		}else{
			try	{
				leftResult = Integer.parseInt(leftSide);
			}catch(Exception e){
				throw new IllegalArgumentException("[Skillz] Invalid value found in portion of equation: "
						+ leftSide);
			}
		}

		operatorLoc = findOperatorLocation(rightSide);
		if( operatorLoc > 0 && operatorLoc < rightSide.length()-1 ){
			rightResult = evaluate(rightSide.substring(0,operatorLoc),
					rightSide.charAt(operatorLoc),
					rightSide.substring(operatorLoc+1,rightSide.length()));
		}else{
			try{
				rightResult = Integer.parseInt(rightSide);
			}catch(Exception e){
				throw new IllegalArgumentException("[Skillz] Invalid value found in portion of equation: "
						+ rightSide);
			}
		}
		if(SkillsSettings.isDebug()){
			System.out.println("Getting result of: " + leftResult + " " + oper + " " + rightResult);
		}
		switch(oper){
		case '/':
			total = leftResult / rightResult; break;
		case '*':
			total = leftResult * rightResult; break;
		case '+':
			total = leftResult + rightResult; break;
		case '-':
			total = leftResult - rightResult; break;
		default:
			throw new IllegalArgumentException("Unknown operator.");
		}
		if(SkillsSettings.isDebug()){
			System.out.println("Returning a result of: " + total);
		}
		return total;
	}

	private static int findOperatorLocation(String string){
		int index = -1;
		for(int i = validOperators.length-1; i >= 0; i--){
			index = string.indexOf(validOperators[i]);
			if(index >= 0)
				return index;
		}
		return index;
	}


	public static double processEquation(String equation) throws IllegalArgumentException	{
		return evaluate(equation,'+',"0");
	}

}
