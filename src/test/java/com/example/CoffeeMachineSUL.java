package com.example;

import de.learnlib.sul.SUL;
import net.automatalib.alphabet.Alphabet;
import net.automatalib.alphabet.Alphabets;
import net.automatalib.alphabet.GrowingMapAlphabet;

/**
 Author: Srdan Todorovic
 * Created: 20.08.2024
 * Description: This class defines the System Under Learning (SUL) for the coffee machine.
 *              It simulates the coffee machine's behavior and responses for the learner.
 */
public class CoffeeMachineSUL implements SUL<String,String>{

	public static String[] standardAlphabet = new String[] {"ON","OFF","WATER","COFFEE","FULL","START"};
	protected Alphabet<String> alphabet = new GrowingMapAlphabet<String>();
	
	public CoffeeMachineSUL(String[] alphabet) {
		this.alphabet = Alphabets.fromArray(alphabet);
	}
	
	boolean ON = false, OFF=true, WATER=false, COFFEE=false, FULL=false, START=false, DONE=false;
	@Override
	public void pre() {
		// TODO Auto-generated method stub
		ON = WATER = COFFEE = START = FULL = DONE = false;
		OFF = true;
	}

	@Override
	public void post() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String step(String in) {
		// TODO Auto-generated method stub
		if(in.equals("OFF")) {
			if(ON) 
			{
				ON = false;
				OFF = true;
				return "Shut down";
			}
			else return "-";
		}
		if(in.equals("ON")) {
			if(OFF) 
			{
				ON = true;
				OFF = false;
				return "BLINK";
			}
			else return "-";
		}		
		if(in.equals("WATER")) {
			if(!WATER) 
			{
				WATER = true;
				return "FILL";
			}
			else 
			{
				WATER = false;
				return "-";
			}
							
		}
		if(in.equals("COFFEE")) {
			if(!COFFEE) 
			{
				COFFEE = true;
				return "FILL";
			}
			else 
			{
				COFFEE = false;
				return "-";
			}
		}
		if(in.equals("FULL")) {
			if(!FULL) 
			{
				FULL = false;
				return "-";
			}
			else 
			{
				FULL = true;
				return "PLEASE EMPTY";
			}		
		}
		if(in.equals("START")) {
			if(!START) 
			{
				START = false;
				return "-";
			}
			else if (START && WATER && COFFEE && ON)
			{
				START = true;
				return "MAKING COFFEE";
			}		
		}
	
		return "ERROR!";
	}

}
