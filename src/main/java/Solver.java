import edu.princeton.cs.algs4.MinPQ;
import edu.princeton.cs.algs4.Queue;

import java.awt.List;
import java.lang.Thread.State;
import java.util.ArrayList;
import java.util.Stack;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TransferQueue;
import java.util.concurrent.atomic.AtomicReference;

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

// Alex's Part
public class Solver {
	// you will need to add a class for search nodes,
	// and a MinPQ to store them in as you solve
	Thread thisThread, thisSolver, twinSolver;
	AtomicReference<ThreadState> thisState, twinState;
	Stack<Board> thisSolution, twinSolution;

	enum ThreadState { // very rust thing to do here
		Solved,
		Running,
		Unsolvable
	}


	public Solver(Board initial) {
		thisThread = Thread.currentThread();
		thisSolver = new Thread() {
			@Override
			public void run() {
				//try {
				//	thisThread.wait();
				//} catch (InterruptedException e) {
				//	// TODO Auto-generated catch block
				//	e.printStackTrace();
				//}
				thisSolution = solve(initial, thisState, twinState);
				//thisThread.notify();
			}
		};

		twinSolver = new Thread() {
			@Override
			public void run() {
				twinSolution = solve(initial.twin(), twinState, thisState);
			}
		};
		thisState = new AtomicReference<Solver.ThreadState>(ThreadState.Running);
		twinState = new AtomicReference<Solver.ThreadState>(ThreadState.Running);

		twinSolver.start();
		thisSolver.start();

		try {
			thisSolver.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// find a solution to the initial board (using the A* algorithm)
	}

	public boolean isSolvable() {
		return thisSolution != null;
	}

	public int moves() {
		return thisSolution == null ? -1 : thisSolution.size();
	}

	public Iterable<Board> solution() {
		return thisSolution;
	}

	public static void main(String[] args) {
		// solve a slider puzzle (given below)
	}

	private Stack<Board> solve(
			Board initial,
			AtomicReference<ThreadState> thisState,
			AtomicReference<ThreadState> otherState) {

		MinPQ<SearchNode> priorityQueue;
		priorityQueue = new MinPQ<>();

		if (initial.isGoal()) {
			thisState.set(ThreadState.Solved);
			return makeList(new SearchNode(initial, null, 0));
		}

		for (Board board : initial.neighbors()) { // kinda messy
			priorityQueue.insert(new SearchNode(board, new SearchNode(initial, null, 0), 1));
		}

		while (priorityQueue.size() != 0) {
			SearchNode current = priorityQueue.delMin();

			if (current.board.isGoal()) {
				thisState.set(ThreadState.Solved);
				return makeList(current);
			}

			for (Board board : current.board.neighbors()) {
				if (!board.equals(current.prevNode.board))
					priorityQueue.insert(new SearchNode(board, current, current.steps + 1));
			}

			if (otherState.get() == ThreadState.Solved) {
				thisState.set(ThreadState.Unsolvable);
				return null;
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
