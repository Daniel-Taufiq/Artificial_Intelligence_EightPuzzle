package eightpuzzle.player;

import java.util.ArrayList;
import java.util.List;

import gridgames.data.action.Action;
import gridgames.data.action.MoveAction;
import gridgames.display.Display;
import gridgames.grid.Board;
import gridgames.grid.Cell;

public class IterativeDeepeningSearchPlayer extends EightPuzzlePlayer {

	private boolean wasSolved;

	public IterativeDeepeningSearchPlayer(List<Action> actions, Display display, Board board) {
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
			if(!wasSolved)
			{
				resetToInitialState();
			}
			depthLimit++;
		}

		// we return next planned action
		return getNextPlannedAction();
	}

	// RECURSIVE METHOD
	private boolean solvePuzzle(int currentDepth, int depthLimit) {
	
		//-----------------------------------------------------------------------
		// this checks if the root puzzle is already solved
		wasSolved = isGoalReached();

		if (wasSolved) {
			return wasSolved;
		}		
		//-----------------------------------------------------------------------
		
		
		
		// get neighboring cells
		List<Action> possibleMoves = getEmptyCellNeighbors();


		// make the move & add to our action plans & increment current depth & check for solution
		for(int i = 0; i < possibleMoves.size(); i++){
			if (wasSolved) {
				break;
			}
			//possibleMoves = getEmptyCellNeighbors();
			moveEmptyCell(possibleMoves.get(i));
			addPlannedAction(possibleMoves.get(i));
			incrementNumActionsExecuted();
			currentDepth++;
			wasSolved = isGoalReached();

			if (wasSolved == true) {
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
		////Cell currentLocation; // will be used to get current location of the cell
		int currentRow = getEmptyCell().getRow();
		int currentCol = getEmptyCell().getCol();

		////currentLocation = getBoard().getCell(currentRow, currentCol);

		int maxRows = getBoard().getNumRows(); // want to find max row/cols so we don't check non-existing neighbors
		int maxCols = getBoard().getNumCols();

		if (currentRow - 1 >= 0) // check if UP is a neighbor
		{
			actions.add(MoveAction.UP);
		}
		if (currentCol + 1 < maxCols) // check if RIGHT is a neighbor
		{
			actions.add(MoveAction.RIGHT);
		}
		if (currentRow + 1 < maxRows) // check if DOWN is a neighbor
		{
			actions.add(MoveAction.DOWN);
		}
		if (currentCol - 1 >= 0) // check if LEFT is a neighbor
		{
			actions.add(MoveAction.LEFT);
		}

		return actions;
	}

	private void undoMoveAndRemovePlannedAction() {
		List<Action> actionToRemove = getPlannedActions();
		Action actionToUndo;

		//Action undoAction = null;
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
