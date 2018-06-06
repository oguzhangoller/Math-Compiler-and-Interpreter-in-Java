import java.io.BufferedReader;
import java.io.BufferedWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main  {
	/*
	 * Main method only runs interpreter mode 
	 */
	public static void main(String[] args) throws IOException {
		if (args.length == 0) {
			// interpreter mode
			runInterpreter();
		} else if (args.length == 1) {
			// compiler mode
		}
	}
	/*
	 * This method runs Interpreter,
	 * First reads a line from terminal
	 * Trims it and removes all the spaces
	 * passes it as argument to isValid method together with line number, which checks validity of expression
	 * if it is valid , gets its postFix notation from postFix method and evaluates it
	 * 
	 */
	public static void runInterpreter() throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String expr = br.readLine();
		int lineCount= 0;
	    	while(expr!=null){
	    	lineCount++;
	    	expr = expr.replaceAll("\\s+","");
	    	if(!isValid(expr,lineCount)){
	    		expr = br.readLine();
	    		continue;
	    	}
	    	ArrayList<String> list = postFix(expr);
	    	evaluate(list);
	    	expr = br.readLine();
	    	
	    	
	    	}
	    
	    }
		
	    static Map<String,Integer> var_map = new HashMap<String,Integer>();			//this Map holds all variables in expression and their values
	    
	    /*
	     * @param This method takes a list of postFix expressions as argument
	     * and makes operations according to those expressions
	     * if it is an assignment operation, calculates the right hand value and assigns it to left hand on variable map -
	     * using calculate method and prints the result if there is no assignment
	     */
	    public static void evaluate(ArrayList<String> list){
	    	if(list.get(list.size()-1).equals("=")){		//checks whether there is an assignment operation
	    		String var = list.get(0);
	    		List<String> newList = list.subList(1,list.size()-1);		//creates a new list consisting of elements of the right hand expression to be evaluated
	    		int value = calculate(newList);				//passes that list as argument to calculate method
	    		var_map.put(var,value);						//assigns calculated value to according variable
	    	}
	    	else{
	    		int value = calculate(list);				//if there is no assignment, calculates the result of expression and -
	    		System.out.println(value);					//prints it to output screen
	    	}
	    	
	    }
	    /*
	     *  This method uses postFix evaluation notation for calculating result of an expression
	     * @param takes a list consisting of tokens of expression to be evaluated
	     * @return calculates the value of an expression and returns it
	     */
	    
	    public static int calculate(List<String> list){
	    	Stack<Integer> st = new Stack<Integer>();		
	    	int result = 0;			
	    	for(String k : list){						//loop over the elements of the list
	    		if(isChar(k)){							//if the element is a variable
	    			if(var_map.get(k)!=null){			//search for it in variable map 
	    				st.push(var_map.get(k));		//push it's value to stack if found
	    			}
	    			else{
	    				st.push(0);						//if map doesn't contain it, push '0' to stack, as variables have default value '0'
	    			}
	    		}
	    		else if(isNumber(k)){					//if it is a number, push it to stack
	    			st.push(Integer.parseInt(k));
	    		}
	    		else{									//if it is and operator, do according operation
	    			if(k.equals("*"))
	    				st.push(st.pop()*st.pop());
	    			if(k.equals("+"))
	    				st.push(st.pop()+st.pop());
	    			if(k.equals("-")){
	    				int temp = st.pop();
	    				st.push(st.pop()-temp);
	    			}
	    			if(k.equals("/")){
	    				int temp = st.pop();
	    				if(temp == 0){										//if client trying to divide by '0' 
	    					System.err.println("Division by 0 error");		//give an error and exit program, since it is not syntax error but logic error	
	    					System.exit(1);
	    				}
	    				st.push(st.pop()/temp);								//if it isn't '0' , continue normal division 
	    			}
	    		}
	    			
	    	}
	    	result = st.pop();										//return result left in the stack
	    	
	    	return result ;
	    }
	    
	    /*	This method checks validity of an expression
	     * @param a String, whose validity is to be tested and line number for printing error methods
	     * @return boolean value whether it is a valid expression
	     */
	    public static boolean isValid(String str, int line){
	    	String reg ;
	    	Pattern p1 ;
	    	Matcher m ;
	    	
	    	if(str.isEmpty()){					//if the expression is blank, return false 
	    		return false;
	    	}
	    	
	    	ArrayList<String> tokens = getTokens(str);
	    	String listString = "";
	    	for (String strings: tokens)					//unite all the valid tokens using getTokens method
	    	     listString = listString + strings;
	    	
	    	if(!listString.equals(str)){					//if there is an invalid character, getTokens method returns a string of lesser size
	    		System.out.println("ERROR:"+ line + " invalid character");
	    		return false;
	    	}
	    	
	    	if(tokens.size()==1 && oper(tokens.get(0))!=0){				//if expression starts with operator, expression is invalid
	    		System.out.println("ERROR:"+ line + " operand missing");
	    		return false;
	    	}
	    	if(!tokens.contains("=") && (tokens.contains("(") || tokens.contains(")"))){	//if expression contains parenthesis without assignment operator, return false
	    		System.out.println("ERROR:"+ line + " operand missing");
	    		return false;
	    	}
	    			
	    	reg = "[\\=]";						//regex for getting all the assignment operators
	    	p1 = Pattern.compile(reg);
	    	m = p1.matcher(str) ;
			String temp = "";
			while(m.find()){
			temp = temp + str.substring(m.start(), m.end());
			}
			
	    	if(temp.length()>1){				//if there are more than one assignment operators, return false
	    		System.out.println("unvalid number of assignment operators");
	    		return false;
	    	}
	    	
	    	if(temp.length() == 1 && str.indexOf('=')!=1){				//if there is assignment operator but it isn't in index no 1 , return false
	    		System.out.println("ERROR:"+ line + " assignment operator at unvalid place");
	    		return false ;
	    	}
	    	
	    	if(tokens.size()>1){
	    		String prev = tokens.get(0);
	    		String current = "";
	    	for(int k=1 ; k<tokens.size(); k++){			//loop through tokens to check expression validity
	    		current = tokens.get(k);
	    		if(prev.equals("(") && isOper(current)){			//if an open parenthesis is followed by an operator, return false
	    			System.out.println("ERROR:"+ line + " operand missing");
	    			return false;
	    		}
	    		if(prev.equals(")") && (isNumber(current) || isChar(current))){    //if a close parenthesis is followed by a number, return false
	    			System.out.println("ERROR:"+ line + " operand missing");
	    			return false;
	    		}
	    		if(prev.equals("=") && isOper(current)){							//if assignment operator is followed by an operator, return false
	    			System.out.println("ERROR:"+ line + " operand missing");
    			return false;
    		}	
	    		
	    		if(isOper(prev) && isOper(current)){								//if there are consecutive operators, return false
	    			System.out.println("ERROR:"+ line + " operand missing");
    			return false;
    		}	
	    		if(isChar(prev) && isChar(current)){								//if there are consecutive characters, return false
	    			System.out.println("ERROR:"+ line + " operand missing");
    			return false;
    		}	
	    		prev = current;
	    	}
	    	
	    	if(isOper(current)){													//if last token is an operator, return false
	    		System.out.println("ERROR:"+ line + " operand missing");
    			return false;
    		}
	    	
	    	}
	    	if(tokens.size() == 1 && isNumber(tokens.get(0))){						//if there is only one token and it is a number, return false
	    		System.out.println("ERROR:"+ line + " operand missing");
    			return false;
    		}
	    	
	    	reg = "[\\(\\)]";					//this regex is for getting all parenthesis in the expression
	    	p1 = Pattern.compile(reg);
			m = p1.matcher(str) ;
			temp = "";
			while(m.find()){
			temp = temp + str.substring(m.start(), m.end());
			}	
			int count = 0;										//holds the number of unmatched parenthesis
	    	for(int i=0 ; i<temp.length() ; i++){				//checks whether number of open parenthesis equals number of closed parenthesis
	    		if(temp.charAt(i) == '(')						//if it is an open parenthesis , increase count by one, 
	    			count++;				
	    		else											//if it is a close parenthesis, decrease count by one
	    			count--;
	    		if(count<0){									//if count drops lower than zero, then there is an unmatched close parenthesis
	    			System.out.println("unmatched paranthesis");
	    			return false;
	    		}
	    	}
	    	if(count != 0){										//if count doesn't equal to zero, there is an unmatched parenthesis
	    		System.out.println("unmatched paranthesis");
	    		return false;
	    	}
	    	return true;
	    }
	    /* This method tokenizes a given String by each operation and operator according to regular expression given below
	     * @param takes a String as argument to be parsed	
	     * @return returns an ArrayList consisting of tokens of that expression
	     */
	    public static ArrayList<String> getTokens(String str){
	    	String patternStr = "[a-zA-Z\\(\\)\\+\\*\\-\\/\\=]|[0-9]+";			//regex for variables A-Z and a-z, operators '+','-','*','/' and numbers
			String str2 = str;
			Pattern p1 = Pattern.compile(patternStr);
			Matcher m = p1.matcher(str2) ;
			ArrayList<String> tokens = new ArrayList<>();
			
	    	while(m.find())
	    		tokens.add(str2.substring(m.start(),m.end()));					//add each token found to an Arraylist
	    	return tokens;														//and return it
	    }
	    /*
	     * @param takes a String as parameter
	     * @return and returns it's postFix notation as ArrayList
	     */
	    
	    public static ArrayList<String> postFix(String str){
	    	ArrayList<String> list = new ArrayList<>();
	    	Stack<Character> st = new Stack();
	    	char symbol;
			String number = "";								//this number is for getting multidigit numbers
			int multiDigit = 0 ;							//checks whether the number operand is multidigit or not
	                for(int i=0;i<str.length();++i)					//loops through the String
			{
				symbol = str.charAt(i);								//reads each character one at a time
				if(Character.isDigit(symbol)){
					multiDigit = 1 ;								//if character is a digit , assigns multiDigit variable 1
					number = number + Character.toString(symbol);		//if current character is also digit, unite it with previous digits
					if(i==str.length()-1)								//this if statement is for checking whether it reached end of the line
						list.add(number);								
				}
				else{												//if current character it not a digit
					if(multiDigit == 1){							//and previous character was a digit
						list.add(number);							//add list previously united number
					}
					multiDigit = 0;									//assigns multiDigit variable '0' to show current character is not a Digit
					number = "";									//assign empty String to number because there is no more consecutive digits to unite with number
				}
				if(Character.isDigit(symbol)){						//this if statement is for necessary for debugging
					;
				}
				else if (Character.isLetter(symbol))				//if it is a variable, add it to the list
					list.add(Character.toString(symbol));
				else if (symbol=='(')
				{
					st.push(symbol);								//if it is an open parenthesis , push it to the stack for incoming operations
				}
				else if (symbol==')')								//if it is a close parenthesis,  add all the parenthesis and operators to list
				{													//until it is an open parenthesis
					while (st.peek() != '(')
					{
						list.add(Character.toString(st.pop()));
					}
					st.pop();		
				}
				else
				{
	                while (!st.isEmpty() && !(st.peek()=='(') && oper(symbol) <= oper(st.peek()))	//loop until operator precedence of prior operators is higher than current ones		
	                	list.add(Character.toString(st.pop()));							//this one adds operators to list according to their precedence priority
					
					st.push(symbol);				//push the operator to list
				}
				
			}
			while (!st.isEmpty())
				list.add(Character.toString(st.pop()));			//add all the 
			return list;
		}
		/*
		 * @param takes a String as parameter and checks whether it is an operator 
		 * @return return true if it is an operator
		 */
	    public static boolean isOper(String str){
	    	return str.equals("+") || str.equals("-") || str.equals("*") || str.equals("/"); 
	    }
	    
	    /*
	     * @param takes a String as parameter and checks whether it is a number 
	     * @return return true if it is a number
	     */
		public static boolean isNumber(String str){
			return str.matches("[0-9]+");
		}
		 /*
	     * @param takes a String as parameter and checks whether it is a char (variable) 
	     * @return return true if it is a char
	     */
		public static boolean isChar(String str){
			return str.matches("[A-Za-z]");
		}
		 /*
	     * @param takes a char as parameter and check its precedence 
	     * @return returns 2 if it's precedence is high, returns 1 if it is low, returns 0 if it isn't an operator
	     */
		public static int oper(char x)
		{
			if (x == '+' || x == '-')
				return 1;
			if (x == '*' || x == '/')
				return 2;
			return 0;
		}
		 /*
	     * @param takes a String as parameter and checks if it is a parenthesis or operator
	     * @return returns 2 if it is a parenthesis, returns 1 if it is an operator and returns 0 if none of them
	     */
		public static int oper(String x)
		{
			if (x.equals("+") || x.equals("-") || x.equals("*") || x.equals("/"))
				return 1;
			else if(x.equals("(") || x.equals(")"))
				return 2;
			return 0;
		}
	}
	
