package eightpuzzle.player;

import gridgames.grid.Board;
import gridgames.data.action.Action;
import gridgames.data.action.MoveAction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.text.html.HTMLDocument.Iterator;

import eightpuzzle.data.EightPuzzleItem;
import gridgames.display.Display;

public class AStarSearchPlayer extends EightPuzzlePlayer {

	private boolean wasSolved;

	public AStarSearchPlayer(List<Action> actions, Display display, Board board) {
		super(actions, display, board);
	}

	@Override
	public Action getAction() {
		int currentDepth = 0;
		int depthLimit = 1;

		//List<Action> possibleMoves = getEmptyCellNeighbors();

		// keep incrementing depth and depthLimit until we found a solution
		while (wasSolved == false) // once true, we won't ever run this again
		{
			wasSolved = solvePuzzle(currentDepth, depthLimit);// we only come back here to increment depthLimit
			if (!wasSolved) {
				resetToInitialState();
			}
			depthLimit++;
		}

		// we return next planned action
		return getNextPlannedAction();
	}

	// RECURSIVE METHOD
	private boolean solvePuzzle(int currentDepth, int depthLimit) {

		// -----------------------------------------------------------------------
		// this checks if the root puzzle is already solved
		wasSolved = isGoalReached();

		if (wasSolved) {
			return wasSolved;
		}
		// -----------------------------------------------------------------------

		// get neighboring cells
		List<Action> possibleMoves = getEmptyCellNeighbors();

		// make the move & add to our action plans & increment current depth & check for
		// solution
		for (int i = 0; i < possibleMoves.size(); i++) {
			if (wasSolved) {
				break;
			}
			// possibleMoves = getEmptyCellNeighbors();
			moveEmptyCell(possibleMoves.get(i));
			addPlannedAction(possibleMoves.get(i));
			incrementNumActionsExecuted();
			currentDepth++;
			wasSolved = isGoalReached();

			if (wasSolved == true) {
				//incrementNumActionsExecuted();
				return wasSolved;
			}
			if (currentDepth == depthLimit) {
				undoMoveAndRemovePlannedAction();
				currentDepth--;
			} else if (currentDepth != depthLimit) {
				solvePuzzle(currentDepth, depthLimit);
				// if we found solution, stop evaluating
				if (!wasSolved) {
					currentDepth--;
					undoMoveAndRemovePlannedAction();
				}
			}
		}
		return this.wasSolved;
	}

	// returns a list of empty Cell's neighbors to determine possible moves to makes
	private List<Action> getEmptyCellNeighbors() {
		List<Action> actions = new ArrayList<Action>();
		int currentRow = getEmptyCell().getRow();
		int currentCol = getEmptyCell().getCol();
		
		//HashMap<Integer, Action> myList = new HashMap<>(); 
		//HashMap<Action, Integer> myList2 = new HashMap<>();	// we can have duplicated values
		
		Integer heuristicUP = 0;
		Integer heuristicRIGHT = 0;
		Integer heuristicDOWN = 0;
		Integer heuristicLEFT = 0;
		
		ArrayList<Integer> list = new ArrayList();
		List<Action> actionList = new ArrayList<>();
	

		int maxRows = getBoard().getNumRows(); // want to find max row/cols so we don't check non-existing neighbors
		int maxCols = getBoard().getNumCols();

		if (currentRow - 1 >= 0) // check if UP is a neighbor
		{
			moveEmptyCell(MoveAction.UP);
			addPlannedAction(MoveAction.UP);
			heuristicUP = getHeuristicValue();
			undoMoveAndRemovePlannedAction();
			list.add(heuristicUP);
			actionList.add(MoveAction.UP);
		}
		if (currentCol + 1 < maxCols) // check if RIGHT is a neighbor
		{
			moveEmptyCell(MoveAction.RIGHT);
			addPlannedAction(MoveAction.RIGHT);
			heuristicRIGHT = getHeuristicValue();
			undoMoveAndRemovePlannedAction();
			list.add(heuristicRIGHT);
			actionList.add(MoveAction.RIGHT);
		}
		if (currentRow + 1 < maxRows) // check if DOWN is a neighbor
		{
			moveEmptyCell(MoveAction.DOWN);
			addPlannedAction(MoveAction.DOWN);
			heuristicDOWN = getHeuristicValue();
			undoMoveAndRemovePlannedAction();
			list.add(heuristicDOWN);
			actionList.add(MoveAction.DOWN);
		}
		if (currentCol - 1 >= 0) // check if LEFT is a neighbor
		{
			moveEmptyCell(MoveAction.LEFT);
			addPlannedAction(MoveAction.LEFT);
			heuristicLEFT = getHeuristicValue();
			undoMoveAndRemovePlannedAction();
			list.add(heuristicLEFT);
			actionList.add(MoveAction.LEFT);
		}
		
		int min = list.get(0);	// assume the first element is min
		int i = 0;
		while(!list.isEmpty())
		{		
			if(list.get(i) < min)
			{
				min = list.get(i);
			}
			
			if(i == list.size()-1)
			{
				for(int j = 0; j < list.size(); j++)
				{
					if(list.get(j) == min)
					{
						actions.add(actionList.get(j));
						actionList.remove(j);
						list.remove(j);
					}
				}
				i = 0;
				if(!list.isEmpty())
				{
					min = list.get(i);
				}
			}
			else
			{
				i++;
			}	
		}
		return actions;
	}
	
	
	
	private int getHeuristicValue()
	{
		ArrayList<EightPuzzleItem> eightPuzzleItems = new ArrayList<EightPuzzleItem>(
				Arrays.asList(EightPuzzleItem.EIGHT_PUZZLE_ITEMS));

		// we know we'll always have values for 1-3

		int movesForOne = 0;
		int movesForTwo = 0;
		int movesForThree = 0;
		int movesForFour = 0;
		int movesForFive = 0;
		int movesForSix = 0;
		int movesForSeven = 0;
		int movesForEight = 0;

		// WHERE VALUES SHOULD BE FOR 2x2
		int oneRow3 = (int) ((1 - 1) / 2);
		int oneCol3 = (1 - 1) % 2;
		int twoRow3 = (int) ((2 - 1) / 2);
		int twoCol3 = (2 - 1) % 2;
		int threeRow3 = (int) ((3 - 1) / 2);
		int threeCol3 = (3 - 1) % 2;
		
		int fourRow3 = (int) ((4 - 1) / 2);
		int fourCol3 = (4 - 1) % 2;
		int fiveRow3 = (int) ((5 - 1) / 2);
		int fiveCol3 = (5 - 1) % 2;
		int sixRow3 = (int) ((6 - 1) / 2);
		int sixCol3 = (6 - 1) % 2;
		int sevenRow3 = (int) ((7 - 1) / 2);
		int sevenCol3 = (7 - 1) % 2;
		int eightRow3 = (int) ((8 - 1) / 2);
		int eightCol3 = (8 - 1) % 2;

			// WHERE VALUES SHOULD BE FOR 3x3
			int oneRow8 = (int) ((1 - 1) / 3);
			int oneCol8 = (1 - 1) % 3;
			int twoRow8 = (int) ((2 - 1) / 3);
			int twoCol8 = (2 - 1) % 3;
			int threeRow8 = (int) ((3 - 1) / 3);
			int threeCol8 = (3 - 1) % 3;
			
			int fourRow8 = (int) ((4 - 1) / 3);
			int fourCol8 = (4 - 1) % 3;
			int fiveRow8 = (int) ((5 - 1) / 3);
			int fiveCol8 = (5 - 1) % 3;
			int sixRow8 = (int) ((6 - 1) / 3);
			int sixCol8 = (6 - 1) % 3;
			int sevenRow8 = (int) ((7 - 1) / 3);
			int sevenCol8 = (7 - 1) % 3;
			int eightRow8 = (int) ((8 - 1) / 3);
			int eightCol8 = (8 - 1) % 3;


		// if we're dealing with 2x2 puzzle
		// take outside if statement, make if statement for puzzles bigger than this

		int whereOneIsRow = 0;
		int whereOneIsCol = 0;
		int whereTwoIsRow = 0;
		int whereTwoIsCol = 0;
		int whereThreeIsRow = 0;
		int whereThreeIsCol = 0;
		int whereFourIsRow = 0;
		int whereFourIsCol = 0;
		int whereFiveIsRow = 0;
		int whereFiveIsCol = 0;
		int whereSixIsRow = 0;
		int whereSixIsCol = 0;
		int whereSevenIsRow = 0;
		int whereSevenIsCol = 0;
		int whereEightIsRow = 0;
		int whereEightIsCol = 0;

		// find where they are
		for (int row = 0; row < getBoard().getNumCols(); row++) {
			for (int col = 0; col < getBoard().getNumCols(); col++) {
				if (getBoard().getCell(row, col).contains(EightPuzzleItem.ONE)) {
					whereOneIsRow = row;
					whereOneIsCol = col;
				} else if (getBoard().getCell(row, col).contains(EightPuzzleItem.TWO)) {
					whereTwoIsRow = row;
					whereTwoIsCol = col;
				} else if (getBoard().getCell(row, col).contains(EightPuzzleItem.THREE)) {
					whereThreeIsRow = row;
					whereThreeIsCol = col;
				} else if (getBoard().getCell(row, col).contains(EightPuzzleItem.FOUR)) {
					whereFourIsRow = row;
					whereFourIsCol = col;
				} else if (getBoard().getCell(row, col).contains(EightPuzzleItem.FIVE)) {
					whereFiveIsRow = row;
					whereFiveIsCol = col;
				} else if (getBoard().getCell(row, col).contains(EightPuzzleItem.SIX)) {
					whereSixIsRow = row;
					whereSixIsCol = col;
				} else if (getBoard().getCell(row, col).contains(EightPuzzleItem.SEVEN)) {
					whereSevenIsRow = row;
					whereSevenIsCol = col;
				} else if (getBoard().getCell(row, col).contains(EightPuzzleItem.EIGHT)) {
					whereEightIsRow = row;
					whereEightIsCol = col;
				}
				
				// add more else if for the next 4-8 numbers
			}
		}

		// evaluate for 2x2
		if(getBoard().getNumCols() == 2)
		{
			movesForOne = Math.abs(oneRow3 - whereOneIsRow) + Math.abs(oneCol3 - whereOneIsCol);
			movesForTwo = Math.abs(twoRow3 - whereTwoIsRow) + Math.abs(twoCol3 - whereTwoIsCol);
			movesForThree = Math.abs(threeRow3 - whereThreeIsRow) + Math.abs(threeCol3 - whereThreeIsCol);
		}
		else if(getBoard().getNumCols() == 3)
		{
			movesForOne = Math.abs(oneRow8 - whereOneIsRow) + Math.abs(oneCol8 - whereOneIsCol);
			movesForTwo = Math.abs(twoRow8 - whereTwoIsRow) + Math.abs(twoCol8 - whereTwoIsCol);
			movesForThree = Math.abs(threeRow8 - whereThreeIsRow) + Math.abs(threeCol8 - whereThreeIsCol);
			movesForFour = Math.abs(fourRow8 - whereFourIsRow) + Math.abs(fourCol8 - whereFourIsCol);
			movesForFive = Math.abs(fiveRow8 - whereFiveIsRow) + Math.abs(fiveCol8 - whereFiveIsCol);
			movesForSix = Math.abs(sixRow8 - whereSixIsRow) + Math.abs(sixCol8 - whereSixIsCol);
			movesForSeven = Math.abs(sevenRow8 - whereSevenIsRow) + Math.abs(sevenCol8 - whereSevenIsCol);
			movesForEight = Math.abs(eightRow8 - whereEightIsRow) + Math.abs(eightCol8 - whereEightIsCol);
		}
		

		// check board size, if col (or row) is greater than 2, we know to check 4 - 8

		int total = movesForOne + movesForTwo + movesForThree + movesForFour + movesForFive + movesForSix + movesForSeven + movesForEight;
		return total;
	}

	private void undoMoveAndRemovePlannedAction() {
		List<Action> actionToRemove = getPlannedActions();
		Action actionToUndo;

		// Action undoAction = null;
		actionToUndo = actionToRemove.remove(actionToRemove.size() - 1); // hopefully removes the previous action
																			// (last/latest action) that was added
		// now find opposite of this action to go back
		if (actionToUndo.getDescription().toUpperCase().equals("UP")) {
			moveEmptyCell(MoveAction.DOWN);
		} else if (actionToUndo.getDescription().toUpperCase().equals("DOWN")) {
			moveEmptyCell(MoveAction.UP);
		} else if (actionToUndo.getDescription().toUpperCase().equals("LEFT")) {
			moveEmptyCell(MoveAction.RIGHT);
		} else {
			moveEmptyCell(MoveAction.LEFT);
		}
	}
}
