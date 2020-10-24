import gridgames.display.ConsoleDisplay;
import gridgames.display.Display;
import gridgames.grid.Board;
import eightpuzzle.EightPuzzle;
import gridgames.data.action.Action;
import gridgames.data.action.MoveAction;
import gridgames.player.HumanPlayer;
import gridgames.player.Player;
import eightpuzzle.player.AStarSearchPlayer;
import eightpuzzle.player.EightPuzzlePlayer;
import eightpuzzle.player.IterativeDeepeningSearchPlayer;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Main {
	
    public static void main(String[] args) {
    	List<Action> allActions = Arrays.asList(MoveAction.MOVE_ACTIONS);
    	runOnConsole(allActions);
    }
    
    public static void runOnConsole(List<Action> allActions) {
    	Scanner scanner = new Scanner(System.in);
    	Display display = new ConsoleDisplay();
        String choice;
        Player player = null;
        EightPuzzle game = null;
        
        do {
        	do {
        		System.out.print("3 or 8 puzzle? [3, 8]: ");
        		choice = scanner.next();
        	} while(!choice.equals("3") && !choice.equals("8"));
        	if("3".equals(choice)) {
        		game = new EightPuzzle(display, 2);
        	} else {
        		game = new EightPuzzle(display, 3);
        	}
        	player = getPlayer(scanner, game);
            do {
                game.play(player);
				System.out.print("Play again? [YES, NO]: ");
                choice = scanner.next().toLowerCase();
            } while(!choice.equals("yes") && !choice.equals("no"));
        } while(choice.equals("yes"));
        scanner.close();
    }
    
    private static Player getPlayer(Scanner scanner, EightPuzzle game) {
    	List<Action> actions = MoveAction.getAllActions();
    	Display display = game.getDisplay();
    	Board board = game.getBoard();
    	Player player = null;
    	String choice;
    	 do {
             System.out.print("Human play or computer play? [HUMAN, COMPUTER]: ");
             choice = scanner.next().toLowerCase();
         } while(!choice.equals("human") && !choice.equals("computer"));
    	 
    	 if(choice.equals("human")) {
    		 EightPuzzlePlayer eightPuzzlePlayer = new EightPuzzlePlayer(actions, display, board);
    		 player = new HumanPlayer(eightPuzzlePlayer, scanner);
         } else {
        	 do {
                 System.out.print("Unformed or informed agent? [UNINFORMED, INFORMED]: ");
                 choice = scanner.next().toLowerCase();
             } while(!choice.equals("uninformed") && !choice.equals("informed"));
             if(choice.equals("uninformed")) {
                 player = new IterativeDeepeningSearchPlayer(actions, display, board);
             } else {
                player = new AStarSearchPlayer(actions, display, board);
             }
         }
    	return player;
    }
}