package c4.players;

import c4.mvc.ConnectFourModelInterface;

public class ConnectFourAIPlayerOther extends ConnectFourPlayer {
	ConnectFourModelInterface model;
    int maxDepth;
    int player;
	
	public ConnectFourAIPlayerOther(ConnectFourModelInterface model, int player){
		this.model = model;
        this.maxDepth = 42;
        this.player = player;
	}

    public ConnectFourAIPlayerOther(ConnectFourModelInterface model, int player, int maxDepth){
        this(model, player);
        this.maxDepth = maxDepth;
	}


    public int dumbGetMove() {
		boolean[] moves = model.getValidMoves();

        for (int i = 0; i < 4; i++) {
            if (moves[3 + i]) {
                return 3 + i;
            } else if (moves[3 - i]) {
                return 3 - i;
            }
        }

		return 0;
    }

    

    public int[] actions(int state[][]) {
		int count = 0;
		for(int i=0; i<7; i++){
            if(state[0][i] == -1){
                count++;
            }
		}
        int[] as = new int[count];
        int index = 0;
        for(int i=0; i<7; i++){
            if(state[0][i] == -1){
                as[index] = i;
                index++;
            }
        }
        return as;
    }

    public int[][] result(int state[][], int action) {
        int[][] newstate = new int[6][7];
        for (int r = 0; r < state.length; r++) {
            for (int c = 0; c < state[r].length; c++) {
                newstate[r][c] = state[r][c];
            }
        }
        int row = 5;
        while (newstate[row][action] != -1) {
            row--;
        }
        newstate[row][action] = getTurn(state);
        return newstate;
    }


    public int getTurn(int state[][]) {
        int count = 0;
        for (int r = 0; r < state.length; r++) {
            for (int c = 0; c < state[r].length; c++) {
                if (state[r][c] != -1) {
                    count++;
                }
            }
        }
        if (count % 2 == 0) {
            return 1;
        } else {
            return 2;
        }
    }

    public boolean terminalTest(int state[][]) {
        if (getWinner(state) != 0) {
            return true;
        }
        return isDraw(state);
    }

    public boolean isDraw(int state[][]) {
        for (int r = 0; r < state.length; r++) {
            for (int c = 0; c < state[r].length; c++) {
                if (state[r][c] == -1) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public int getMove() {
        int[][] state = model.getGrid();
        int[][] newstate = new int[6][7];
        for (int r = 0; r < state.length; r++) {
            for (int c = 0; c < state[r].length; c++) {
                newstate[c][r] = state[r][c];
            }
        }
        ActionUtility action = alphaBetaSearch(newstate);
        return action.action;
    }

    class ActionUtility implements Comparable<ActionUtility> {

        int action;
        int utility;

        public ActionUtility(int action, int utility) {
            this.action = action;
            this.utility = utility;
        }

        @Override
        public int compareTo(ActionUtility o) {
            if (this.utility > o.utility) {
                return 1;
            } else if (this.utility < o.utility) {
                return -1;
            } else {
                return 0;
            }
        }
        
    }


    public ActionUtility alphaBetaSearch(int state[][]) {
        ActionUtility v = maxValue(state, Integer.MIN_VALUE, Integer.MAX_VALUE, 0);
        return v;
    }

    public ActionUtility maxValue(int state[][], int alpha, int beta, int depth) {
        if (depth > maxDepth || terminalTest(state)) {
            return new ActionUtility(-1, utility(state));
        }
        int v = Integer.MIN_VALUE;
        int[] actions = actions(state);
        int action = -1;
        for (int i = 0; i < actions.length; i++) {
            ActionUtility temp = minValue(result(state, actions[i]), alpha, beta, depth + 1);
            if (temp.utility > v) {
                v = temp.utility;
                action = actions[i];
            }
            if (v >= beta) {
                return new ActionUtility(action, v);
            }
            alpha = Math.max(alpha, v);
        }
        return new ActionUtility(action, v);
    }

    public ActionUtility minValue(int state[][], int alpha, int beta, int depth) {
        if (depth > maxDepth || terminalTest(state)) {
            return new ActionUtility(-1, utility(state));
        }
        int v = Integer.MAX_VALUE;
        int[] actions = actions(state);
        int action = -1;
        for (int i = 0; i < actions.length; i++) {
            ActionUtility temp = maxValue(result(state, actions[i]), alpha, beta, depth + 1);
            if (temp.utility < v) {
                v = temp.utility;
                action = actions[i];
            }
            if (v <= alpha) {
                return new ActionUtility(action, v);
            }
            beta = Math.min(beta, v);
        }
        return new ActionUtility(action, v);
    }



    public int utility(int state[][]) {
        if (getWinner(state) == player) {
            return 1000;
        } else if (getWinner(state) == player % 2 + 1) {
            return -1000;
        } else if (isDraw(state)) {
            return 0;
        }
        return heuristic(state);
    }

    public int heuristic(int state[][]) {
        int numWaysOpponentCanWin = 0;
        int numWaysPlayerCanWin = 0;
        for (int r = 0; r < state.length; r++) {
            for (int c = 0; c < state[r].length; c++) {
                if (state[r][c] == -1) {
                    state[r][c] = player;
                    if (getWinner(state) == player) {
                        numWaysPlayerCanWin++;
                    }
                    state[r][c] = player % 2 + 1;
                    if (getWinner(state) == player % 2 + 1) {
                        numWaysOpponentCanWin++;
                    }
                    state[r][c] = -1;
                }
            }
        }

        return numWaysPlayerCanWin - numWaysOpponentCanWin;
    }

    // public int heuristic(int state[][]) {
    //     return 2 * h3(state, player) + 2 * v3(state, player) + 2 * p3(state, player) + 2 * n3(state, player) - 2 * h3(state, player % 2 + 1) - 2 * v3(state, player % 2 + 1) - 2 * p3(state, player % 2 + 1) - 2 * n3(state, player % 2 + 1);
    // }


    public int checkHorizontalWin(int state[][]) {
        for (int r = 0; r < state.length; r++) {
            for (int c = 0; c + 3 < state[r].length; c++) {
                if (state[r][c] != -1) {
                    if (state[r][c] == state[r][c + 1] && state[r][c] == state[r][c + 2] && state[r][c] == state[r][c + 3]) {
                        return state[r][c];
                    }
                }
            }
        }
        return 0;
    }
    
    public int h3(int state[][], int playerInt) {
        int count = 0;
        for (int r = 0; r < state.length; r++) {
            for (int c = 0; c + 2 < state[r].length; c++) {
                if (state[r][c] == playerInt) {
                    if (state[r][c] == state[r][c + 1] && state[r][c] == state[r][c + 2]) {
                        count++;
                    }
                }
            }
        }
        return count;
    }
    

    public int checkVerticalWin(int state[][]) {
        for (int r = 0; r + 3 < state.length; r++) {
            for (int c = 0; c < state[r].length; c++) {
                if (state[r][c] != -1) {
                    if (state[r][c] == state[r + 1][c] && state[r][c] == state[r + 2][c] && state[r][c] == state[r + 3][c]) {
                        return state[r][c];
                    }
                }
            }
        }
        return 0;
    }

    public int v3(int state[][], int playerInt) {
        int count = 0;
        for (int r = 0; r + 2 < state.length; r++) {
            for (int c = 0; c < state[r].length; c++) {
                if (state[r][c] == playerInt) {
                    if (state[r][c] == state[r + 1][c] && state[r][c] == state[r + 2][c]) {
                        count++;
                    }
                }
            }
        }
        return count;
    }


    public int checkPosDiagonalWin(int state[][]) {
        for (int r = 0; r + 3 < state.length; r++) {
            for (int c = 0; c + 3 < state[r].length; c++) {
                if (state[r][c] != -1) {
                    if (state[r][c] == state[r + 1][c + 1] && state[r][c] == state[r + 2][c + 2] && state[r][c] == state[r + 3][c + 3]) {
                        return state[r][c];
                    }
                }
            }
        }
        return 0;
    }

    public int p3(int state[][], int playerInt) {
        int count = 0;
        for (int r = 0; r + 2 < state.length; r++) {
            for (int c = 0; c + 2 < state[r].length; c++) {
                if (state[r][c] == playerInt) {
                    if (state[r][c] == state[r + 1][c + 1] && state[r][c] == state[r + 2][c + 2]) {
                        count++;
                    }
                }
            }
        }
        return count;
    }


    public int checkNegDiagonalWin(int state[][]) {
        for (int r = 0; r + 3 < state.length; r++) {
            for (int c = 3; c < state[r].length; c++) {
                if (state[r][c] != -1) {
                    if (state[r][c] == state[r + 1][c - 1] && state[r][c] == state[r + 2][c - 2] && state[r][c] == state[r + 3][c - 3]) {
                        return state[r][c];
                    }
                }
            }
        }
        return 0;
    }

    public int n3(int state[][], int playerInt) {
        int count = 0;
        for (int r = 0; r + 2 < state.length; r++) {
            for (int c = 2; c < state[r].length; c++) {
                if (state[r][c] == playerInt) {
                    if (state[r][c] == state[r + 1][c - 1] && state[r][c] == state[r + 2][c - 2]) {
                        count++;
                    }
                }
            }
        }
        return count;
    }

    public int getWinner(int state[][]) {
        if (checkHorizontalWin(state) != 0) {
            return checkHorizontalWin(state);
        } else if (checkVerticalWin(state) != 0) {
            return checkVerticalWin(state);
        } else if (checkPosDiagonalWin(state) != 0) {
            return checkPosDiagonalWin(state);
        } else if (checkNegDiagonalWin(state) != 0) {
            return checkNegDiagonalWin(state);
        }
        return 0;
    }
    
}
