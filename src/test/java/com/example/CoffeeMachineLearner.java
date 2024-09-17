package com.example;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Collections;
import java.util.Random;

import de.learnlib.algorithm.LearningAlgorithm.MealyLearner;
import de.learnlib.algorithm.lstar.ce.ObservationTableCEXHandlers;
import de.learnlib.algorithm.lstar.closing.ClosingStrategies;
import de.learnlib.algorithm.lstar.mealy.ExtensibleLStarMealy;
import de.learnlib.filter.cache.mealy.MealyCaches;
import de.learnlib.oracle.MembershipOracle;
import de.learnlib.oracle.equivalence.mealy.RandomWalkEQOracle;
import de.learnlib.oracle.membership.SULOracle;
import de.learnlib.query.DefaultQuery;
import net.automatalib.automaton.transducer.MealyMachine;
import net.automatalib.serialization.dot.GraphDOT;
import net.automatalib.visualization.Visualization;
import net.automatalib.word.Word;

/**
 Author: Srdan Todorovic
 * Created: 20.08.2024
 * Description: This class implements the learner for a coffee machine using LearnLib.
 */
public class CoffeeMachineLearner 
{
	public static void main(String args[]) throws IOException {
		int round = 1;
		CoffeeMachineSUL sul = new CoffeeMachineSUL(CoffeeMachineSUL.standardAlphabet);
		SULOracle<String,String> sulOracle = new SULOracle<>(sul);

		MembershipOracle<String, Word<String>> mqOracle = MealyCaches.createDAGCache(sul.alphabet, sulOracle);
		MealyLearner<String, String> learner = new ExtensibleLStarMealy<String,String>(
				sul.alphabet,
				mqOracle,
				Collections.singletonList(Word.<String>epsilon()),
				Collections.singletonList(Word.<String>epsilon()),
				ObservationTableCEXHandlers.RIVEST_SCHAPIRE,
				ClosingStrategies.CLOSE_SHORTEST
		);
		System.out.println("START LEARNING");
		learner.startLearning();

		RandomWalkEQOracle <String,String> eqOracle = new RandomWalkEQOracle<>(sul, 0.05, 50, new Random(46_346_293));
		DefaultQuery<String,Word<String>> counterexample = null;
		counterexample = eqOracle.findCounterExample((MealyMachine<?,String,?,String>)learner.getHypothesisModel(), sul.alphabet);
		
		while(counterexample != null) {
			round++;
			System.out.println("Counterexample found :"+counterexample+" starting learning round # "+round);
			learner.refineHypothesis(counterexample);
			counterexample = eqOracle.findCounterExample((MealyMachine<?,String,?,String>)learner.getHypothesisModel(), sul.alphabet);
		}
		
		System.out.println("Finished learining in round #"+round+" quitting.");
		
		MealyMachine<?,String,?,String> finalModel = (MealyMachine<?,String,?,String>) learner.getHypothesisModel();
		Visualization.visualize(finalModel,sul.alphabet);

		/*
		//if you want to save it to file
		String pathToFile = "path";
		try(FileWriter writer = new FileWriter(pathToFile)) {
			GraphDOT.write(finalModel, sul.alphabet, writer);
            System.out.println("Automaton successfully saved to: " + pathToFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
		*/
	}
}

