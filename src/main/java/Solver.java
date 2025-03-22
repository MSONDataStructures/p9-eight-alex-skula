import edu.princeton.cs.algs4.MinPQ;

import java.awt.List;
import java.util.ArrayList;
import java.util.Stack;
import java.util.concurrent.BlockingQueue;

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

// Alex's Part
public class Solver {
	// you will need to add a class for search nodes,
	// and a MinPQ to store them in as you solve
	MinPQ<SearchNode> priorityQueue;
	Stack<Board> solution;


	public Solver(Board initial) {
		priorityQueue = new MinPQ<>();
		solution = solve(initial);
		// find a solution to the initial board (using the A* algorithm)
	}

	public boolean isSolvable() {
		return solution != null;
	}

	public int moves() {
		return solution == null ? -1 : solution.size();
	}

	public Iterable<Board> solution() {
		return solution;
	}

	public static void main(String[] args) {
		// solve a slider puzzle (given below)
	}

	private Stack<Board> solve(Board inital) {

		if (inital.isGoal()) {
			return makeList(new SearchNode(inital, null, 0));
		}

		for (Board board : inital.neighbors()) { // kinda messy
			priorityQueue.insert(new SearchNode(board, new SearchNode(inital, null, 0), 1));
		}

		while (priorityQueue.size() != 0) {
			SearchNode current = priorityQueue.delMin();

			if (current.board.isGoal()) {
				return makeList(current);
			}

			for (Board board : current.board.neighbors()) {
				if (!board.equals(current.prevNode.board))
					priorityQueue.insert(new SearchNode(board, current, current.steps + 1));
			}
		}

		return null;
	}

	private Stack<Board> makeList(Solver.SearchNode current) {
		SearchNode currentNode = current;
		Stack<Board> stack = new Stack<>();

		while (current.prevNode != null) {
			stack.add(current.board);
			current = current.prevNode;
		}

		return stack;
	}

	class SearchNode implements Comparable {
		public Board board;
		public SearchNode prevNode;
		public int distance;
		public int steps;
		public int priority;

		public SearchNode(Board board, SearchNode prevNode, int steps) {
			this.board = board;
			this.prevNode = prevNode;
			this.steps = steps;

			distance = board.manhattan();
			priority = distance + steps;
		}

		@Override
		public int compareTo(Object arg0) {
			SearchNode other = (SearchNode) arg0;

			return this.priority - other.priority;
		}
	}
}
