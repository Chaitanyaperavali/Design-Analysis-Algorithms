package edu.umkc.da;

import java.util.Arrays;

public class HungImp {
	  private final double[][] costMatrix;
	  private final int rows, cols, dim;
	  private final double[] labelByCourse, labelByProfessor;
	  private final int[] minSlackCourseByProfessor;
	  private final double[] minSlackValueByProfessor;
	  private final int[] matchProfessorByCourse, matchCourseByProfessor;
	  private final int[] parentCourseByCommittedProfessor;
	  private final boolean[] committedCourses;

	  /**
	   * Construct an instance of the algorithm.
	   * 
	   * @param costMatrix
	   *          the cost matrix, where matrix[i][j] holds the cost of assigning
	   *          course i to professor j, for all i, j. The cost matrix must not be
	   *          irregular in the sense that all rows must be the same length; in
	   *          addition, all entries must be non-infinite numbers.
	   */
	  public HungImp(double[][] costMatrix) {
	    this.dim = Math.max(costMatrix.length, costMatrix[0].length);
	    this.rows = costMatrix.length;
	    this.cols = costMatrix[0].length;
	    this.costMatrix = new double[this.dim][this.dim];
	    for (int w = 0; w < this.dim; w++) {
	      if (w < costMatrix.length) {
	        if (costMatrix[w].length != this.cols) {
	          throw new IllegalArgumentException("Irregular cost matrix");
	        }
	        for (int j = 0; j < this.cols; j++) {
	          if (Double.isInfinite(costMatrix[w][j])) {
	            throw new IllegalArgumentException("Infinite cost");
	          }
	          if (Double.isNaN(costMatrix[w][j])) {
	            throw new IllegalArgumentException("NaN cost");
	          }
	        }
	        this.costMatrix[w] = Arrays.copyOf(costMatrix[w], this.dim);
	      } else {
	        this.costMatrix[w] = new double[this.dim];
	      }
	    }
	    labelByCourse = new double[this.dim];
	    labelByProfessor = new double[this.dim];
	    minSlackCourseByProfessor = new int[this.dim];
	    minSlackValueByProfessor = new double[this.dim];
	    committedCourses = new boolean[this.dim];
	    parentCourseByCommittedProfessor = new int[this.dim];
	    matchProfessorByCourse = new int[this.dim];
	    Arrays.fill(matchProfessorByCourse, -1);
	    matchCourseByProfessor = new int[this.dim];
	    Arrays.fill(matchCourseByProfessor, -1);
	  }

	  /**
	   * Compute an initial feasible solution by assigning zero labels to the
	   * courses and by assigning to each professor a label equal to the minimum cost
	   * among its incident edges.
	   */
	  protected void computeInitialFeasibleSolution() {
	    for (int j = 0; j < dim; j++) {
	      labelByProfessor[j] = Double.POSITIVE_INFINITY;
	    }
	    for (int w = 0; w < dim; w++) {
	      for (int j = 0; j < dim; j++) {
	        if (costMatrix[w][j] < labelByProfessor[j]) {
	          labelByProfessor[j] = costMatrix[w][j];
	        }
	      }
	    }
	  }

	  /**
	   * Execute the algorithm.
	   * 
	   * @return the minimum cost matching of courses to professors based upon the
	   *         provided cost matrix. A matching value of -1 indicates that the
	   *         corresponding course is unassigned.
	   */
	  public int[] execute() {
	    /*
	     * Heuristics to improve performance: Reduce rows and columns by their
	     * smallest element, compute an initial non-zero dual feasible solution and
	     * create a greedy matching from courses to professors of the cost matrix.
	     */
	    reduce();
	    computeInitialFeasibleSolution();
	    greedyMatch();

	    int w = fetchUnmatchedCourse();
	    while (w < dim) {
	      initializePhase(w);
	      executePhase();
	      w = fetchUnmatchedCourse();
	    }
	    int[] result = Arrays.copyOf(matchProfessorByCourse, rows);
	    for (w = 0; w < result.length; w++) {
	      if (result[w] >= cols) {
	        result[w] = -1;
	      }
	    }
	    return result;
	  }

	  /**
	   * Execute a single phase of the algorithm. A phase of the Hungarian algorithm
	   * consists of building a set of committed Courses and a set of committed Professors
	   * from a root unmatched Course by following alternating unmatched/matched
	   * zero-slack edges. If an unmatched Professor is encountered, then an augmenting
	   * path has been found and the matching is grown. If the connected zero-slack
	   * edges have been exhausted, the labels of committed Courses are increased by
	   * the minimum slack among committed Courses and non-committed Professors to create
	   * more zero-slack edges (the labels of committed Professors are simultaneously
	   * decreased by the same amount in order to maintain a feasible labeling).
	   * <p>
	   * 
	   * The runtime of a single phase of the algorithm is O(n^2), where n is the
	   * dimension of the internal square cost matrix, since each edge is visited at
	   * most once and since increasing the labeling is accomplished in time O(n) by
	   * maintaining the minimum slack values among non-committed Professors. When a phase
	   * completes, the matching will have increased in size.
	   */
	  protected void executePhase() {
	    while (true) {
	      int minSlackCourse = -1, minSlackProfessor = -1;
	      double minSlackValue = Double.POSITIVE_INFINITY;
	      for (int j = 0; j < dim; j++) {
	        if (parentCourseByCommittedProfessor[j] == -1) {
	          if (minSlackValueByProfessor[j] < minSlackValue) {
	            minSlackValue = minSlackValueByProfessor[j];
	            minSlackCourse = minSlackCourseByProfessor[j];
	            minSlackProfessor = j;
	          }
	        }
	      }
	      if (minSlackValue > 0) {
	        updateLabeling(minSlackValue);
	      }
	      parentCourseByCommittedProfessor[minSlackProfessor] = minSlackCourse;
	      if (matchCourseByProfessor[minSlackProfessor] == -1) {
	        /*
	         * An augmenting path has been found.
	         */
	        int committedProfessor = minSlackProfessor;
	        int parentCourse = parentCourseByCommittedProfessor[committedProfessor];
	        while (true) {
	          int temp = matchProfessorByCourse[parentCourse];
	          match(parentCourse, committedProfessor);
	          committedProfessor = temp;
	          if (committedProfessor == -1) {
	            break;
	          }
	          parentCourse = parentCourseByCommittedProfessor[committedProfessor];
	        }
	        return;
	      } else {
	        /*
	         * Update slack values since we increased the size of the committed
	         * Courses set.
	         */
	        int Course = matchCourseByProfessor[minSlackProfessor];
	        committedCourses[Course] = true;
	        for (int j = 0; j < dim; j++) {
	          if (parentCourseByCommittedProfessor[j] == -1) {
	            double slack = costMatrix[Course][j] - labelByCourse[Course]
	                - labelByProfessor[j];
	            if (minSlackValueByProfessor[j] > slack) {
	              minSlackValueByProfessor[j] = slack;
	              minSlackCourseByProfessor[j] = Course;
	            }
	          }
	        }
	      }
	    }
	  }

	  /**
	   * 
	   * @return the first unmatched Course or {@link #dim} if none.
	   */
	  protected int fetchUnmatchedCourse() {
	    int w;
	    for (w = 0; w < dim; w++) {
	      if (matchProfessorByCourse[w] == -1) {
	        break;
	      }
	    }
	    return w;
	  }

	  /**
	   * Find a valid matching by greedily selecting among zero-cost matchings. This
	   * is a heuristic to jump-start the augmentation algorithm.
	   */
	  protected void greedyMatch() {
	    for (int w = 0; w < dim; w++) {
	      for (int j = 0; j < dim; j++) {
	        if (matchProfessorByCourse[w] == -1 && matchCourseByProfessor[j] == -1
	            && costMatrix[w][j] - labelByCourse[w] - labelByProfessor[j] == 0) {
	          match(w, j);
	        }
	      }
	    }
	  }

	  /**
	   * Initialize the next phase of the algorithm by clearing the committed
	   * Courses and Professors sets and by initializing the slack arrays to the values
	   * corresponding to the specified root Course.
	   * 
	   * @param w
	   *          the Course at which to root the next phase.
	   */
	  protected void initializePhase(int w) {
	    Arrays.fill(committedCourses, false);
	    Arrays.fill(parentCourseByCommittedProfessor, -1);
	    committedCourses[w] = true;
	    for (int j = 0; j < dim; j++) {
	      minSlackValueByProfessor[j] = costMatrix[w][j] - labelByCourse[w]
	          - labelByProfessor[j];
	      minSlackCourseByProfessor[j] = w;
	    }
	  }

	  /**
	   * Helper method to record a matching between Course w and Professor j.
	   */
	  protected void match(int w, int j) {
	    matchProfessorByCourse[w] = j;
	    matchCourseByProfessor[j] = w;
	  }

	  /**
	   * Reduce the cost matrix by subtracting the smallest element of each row from
	   * all elements of the row as well as the smallest element of each column from
	   * all elements of the column. Note that an optimal assignment for a reduced
	   * cost matrix is optimal for the original cost matrix.
	   */
	  protected void reduce() {
	    for (int w = 0; w < dim; w++) {
	      double min = Double.POSITIVE_INFINITY;
	      for (int j = 0; j < dim; j++) {
	        if (costMatrix[w][j] < min) {
	          min = costMatrix[w][j];
	        }
	      }
	      for (int j = 0; j < dim; j++) {
	        costMatrix[w][j] -= min;
	      }
	    }
	    double[] min = new double[dim];
	    for (int j = 0; j < dim; j++) {
	      min[j] = Double.POSITIVE_INFINITY;
	    }
	    for (int w = 0; w < dim; w++) {
	      for (int j = 0; j < dim; j++) {
	        if (costMatrix[w][j] < min[j]) {
	          min[j] = costMatrix[w][j];
	        }
	      }
	    }
	    for (int w = 0; w < dim; w++) {
	      for (int j = 0; j < dim; j++) {
	        costMatrix[w][j] -= min[j];
	      }
	    }
	  }

	  /**
	   * Update labels with the specified slack by adding the slack value for
	   * committed Courses and by subtracting the slack value for committed Professors. In
	   * addition, update the minimum slack values appropriately.
	   */
	  protected void updateLabeling(double slack) {
	    for (int w = 0; w < dim; w++) {
	      if (committedCourses[w]) {
	        labelByCourse[w] += slack;
	      }
	    }
	    for (int j = 0; j < dim; j++) {
	      if (parentCourseByCommittedProfessor[j] != -1) {
	        labelByProfessor[j] -= slack;
	      } else {
	        minSlackValueByProfessor[j] -= slack;
	      }
	    }
	  }
	}