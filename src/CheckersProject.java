import java.util.Scanner;



public class CheckersProject{
	//NOTE: if(coordinateVallid(INPUT_X, INPUT_Y)){...} is called inside certain grid checking functions so that an ArayIndexOutOfBoundsException does not occur
	Scanner input;
	boolean gameOver, moving, hasMovedThisTurn, pieceHopped;
	final int boardSize;
	byte[][] checkerboard;//0 - empty spot, 1 - red piece, 2 - black piece
	int fromX, fromY, toX, toY, pieceHoppedX, pieceHoppedY, valueCheck, hopDirection;//hopDirection: 1 - upper left, 2 - lower left, 3 - upper right, 4 - lower right
	int numRedCheckers, numBlackCheckers;//num[REMAINING_VARIABLE_NAME] is often used as a short notation for "number of [THING]"
	boolean redTurn;//false = black's turn
	
	public CheckersProject(){//main class constructor - performs all program initialization
		input = new Scanner(System.in);//getting the input from the Console
		gameOver = false;
		moving = false;
		hasMovedThisTurn = false;
		pieceHopped = false;
		boardSize = 8;
		checkerboard = new byte[boardSize][boardSize];
		for(int x = 0; x < boardSize; x++){
			for(int y = 0; y < boardSize; y++){
				checkerboard[x][y] = 0;
			}
		}
		fromX = -1;
		fromY = -1;
		toX = -1;
		toY = -1;
		pieceHoppedX = -1;
		pieceHoppedY = -1;
		valueCheck = 0;
		hopDirection = 0;
		numRedCheckers = 8;
		numBlackCheckers = 8;
		redTurn = true;
		runGame();//this function has an internal loop
	}
	
	public static void main(String[] args){
		CheckersProject ce = new CheckersProject();//instantiating an instance of the main class
	}
	
	void runGame(){
		System.out.println("Welcome to Saras's Checkers Game! A new game of checkers has begun.");
		System.out.println("Row counting starts at (0, 0), from left to right and top to bottom.");
		setBoard();
		while(!gameOver){
			if(redTurn){
				System.out.println("It is now Red's turn.");
			}else{
				System.out.println("It is now Black's turn.");
			}
			moving = true;
			hasMovedThisTurn = false;
			while(moving){
				System.out.println("Input X of space to move from");
				fromX = input.nextInt();
				System.out.println("Input Y of space to move from");
				fromY = input.nextInt();
				if(coordinateVallid(fromX, fromY)){
					if(!spotEmpty(fromX, fromY)){
						if(pieceOwned(fromX, fromY)){
							System.out.println("Input X of space to move to");
							toX = input.nextInt();
							System.out.println("Input Y of space to move to");
							toY = input.nextInt();
							if(coordinateVallid(fromX, fromY)){
								if(spotEmpty(toX, toY)){
									if(isDiagonalMove()){
										//this is printed manually before the function call because it would be out of order for it to be printed after what is printed inside the function call
										System.out.println("Moved a piece from (" + fromX + ", " + fromY + ") to (" + toX + ", " + toY + ")");
										movePiece();
									}else if(isDiagonalHop()){
										System.out.println("Moved a piece from (" + fromX + ", " + fromY + ") to (" + toX + ", " + toY + ")");
										movePiece();
									}else{
										System.out.println("That space is not a valid move space");
									}
								}else{
									System.out.println("Cannot move tot hat space - it is occupied.");
								}
							}else{
								System.out.println("Move-from coordinate is incorrect.");
							}
						}else{
							System.out.println("That piece does not belong to you.");
						}
					}else{
						System.out.println("That spot is empty.");
					}
				}else{
					System.out.println("Move-from coordinate is incorrect.");
				}
				if(canMultiHop()){
					System.out.println("Input '10' to move again or a different value to end turn.");
					valueCheck = input.nextInt();
					if(valueCheck != 10){//a value besides 10 has been given
						moving = false;
						if(redTurn){//ending turn by changing turn to the other side's turn
							redTurn = false;
						}else{//black's turn
							redTurn = true;
						}
						System.out.println("End of turn.");
					}
				}else{//end of turn because there are no available multihops
					moving = false;//allowing the internal moving loop to end so that the turns may progress
					System.out.println("End of turn.");
				}
			}
			printBoard();
			if(gameOver){
				System.out.println("Input '1' to play again. Input a different number to end program.");
				valueCheck = input.nextInt();
				if(valueCheck == 1){
					resetGame();
					System.out.println("Beginning new game...");
				}else{
					System.out.println("End of program.");
					System.exit(0);
				}
			}
		}
	}
	
	void setBoard(){
		//setting black at top of board
		for(int y = 0; y < 2; y++){
			if(y == 0){
				checkerboard[0][y] = 1;
				checkerboard[2][y] = 1;
				checkerboard[4][y] = 1;
				checkerboard[6][y] = 1;
			}else if(y == 1){
				checkerboard[1][y] = 1;
				checkerboard[3][y] = 1;
				checkerboard[5][y] = 1;
				checkerboard[7][y] = 1;
			}
		}
		//setting red at bottom of board
		for(int y = 0; y < 2; y++){
			if(y == 0){
				checkerboard[0][y + 6] = 1;
				checkerboard[2][y + 6] = 1;
				checkerboard[4][y + 6] = 1;
				checkerboard[6][y + 6] = 1;
			}else if(y == 1){
				checkerboard[1][y + 6] = 1;
				checkerboard[3][y + 6] = 1;
				checkerboard[5][y + 6] = 1;
				checkerboard[7][y + 6] = 1;
			}
		}
	}
	
	boolean isDiagonalMove(){
		if((toX == fromX - 1 && toY == fromY - 1) ||//upper left diagonal move
				(toX == fromX - 1 && toY == fromY + 1) ||//lower left diagonal move
				(toX == fromX + 1 && toY == fromY - 1) ||//upper right diagonal move
				(toX == fromX + 1 && toY == fromY + 1)){//lower right diagonal move
			return true;
		}
		return false;
	}
	
	boolean isDiagonalHop(){
		if(toX == fromX - 2 && toY == fromY - 2){//upper left diagonal move
			if(coordinateVallid(fromX - 1, fromX - 1)){
				if(checkerboard[fromX - 1][fromY - 1] != 0){//if there is piece to hop over
					pieceHoppedX = fromX - 1;
					pieceHoppedY = fromY - 1;
					pieceHopped = true;
					hopDirection = 1;//upper left
					return true;
				}
			}
		}else if(toX == fromX - 2 && toY == fromY + 2){//lower left diagonal move
			if(coordinateVallid(fromX - 1, fromX + 1)){
				if(checkerboard[fromX - 1][fromY + 1] != 0){//if there is piece to hop over
					pieceHoppedX = fromX - 1;
					pieceHoppedY = fromY + 1;
					pieceHopped = true;
					hopDirection = 2;//lower left
					return true;
				}
			}
		}else if(toX == fromX + 2 && toY == fromY - 2){//upper right diagonal move
			if(coordinateVallid(fromX + 1, fromX - 1)){
				if(checkerboard[fromX + 1][fromY - 1] != 0){//if there is piece to hop over
					pieceHoppedX = fromX + 1;
					pieceHoppedY = fromY - 1;
					pieceHopped = true;
					hopDirection = 3;//upper right
					return true;
				}
			}
		}else if(toX == fromX + 2 && toY == fromY + 2){//lower right diagonal move
			if(coordinateVallid(fromX + 1, fromX + 1)){
				if(checkerboard[fromX + 1][fromY + 1] != 0){//if there is piece to hop over
					pieceHoppedX = fromX + 1;
					pieceHoppedY = fromY + 1;
					pieceHopped = true;
					hopDirection = 4;//lower right
					return true;
				}
			}
		}
		return false;
	}
	
	void movePiece(){
		checkerboard[fromX][fromY] = 0;//clearing board where piece was
		if(redTurn){//setting piece to where it is moved to
			checkerboard[toX][toY] = 1;
		}else{
			checkerboard[toX][toY] = 2;
		}
		if(pieceHopped){
			if(redTurn){
				if(checkerboard[pieceHoppedX][pieceHoppedY] == 2){//if piece is black
					checkerboard[pieceHoppedX][pieceHoppedY] = 0;//deleting enemy piece
					numBlackCheckers--;
					System.out.println("The enemy has lost a piece and now has " + numBlackCheckers + " pieces.");
					if(numBlackCheckers == 0){
						System.out.println("GAME OVER! VICTOR: Red");
						gameOver = true;
					}
				}else{
					System.out.println("Jumped over a friendly piece to move to (" + toX + ", " + toY + ").");
				}
			}else{//black's turn
				if(checkerboard[pieceHoppedX][pieceHoppedY] == 1){//if piece is red
					checkerboard[pieceHoppedX][pieceHoppedY] = 0;//deleting enemy piece
					numRedCheckers--;
					System.out.println("The enemy has lost a piece and now has " + numRedCheckers + " pieces.");
					if(numRedCheckers == 0){
						System.out.println("GAME OVER! VICTOR: Black");
						gameOver = true;
					}
				}else{
					System.out.println("Jumped over a friendly piece to move to (" + toX + ", " + toY + ").");
				}
			}
			pieceHopped = false;//resetting this check boolean for later use
		}
		hasMovedThisTurn = true;
	}
	
	//checks to see if can make the checker hop farther, relative to the position the piece has been moved to, which is still held in toX, toY
	boolean canMultiHop(){
		if(redTurn){
			switch(hopDirection){
			case 1://upper left
				if(coordinateVallid(fromX - 1, fromX - 1)){
					if(checkerboard[toX - 1][toY - 1] == 2){//if the next space in this direction is filled with an enemy piece
						if(coordinateVallid(fromX - 2, fromX - 2)){
							if(checkerboard[toX - 2][toY - 2] == 0){//if the space after the one filled with an enemy piece is empty
								return true;
							}
						}
					}
				}
				break;
			case 2://lower left
				if(coordinateVallid(fromX - 1, fromX + 1)){
					if(checkerboard[toX - 1][toY + 1] == 2){//if the next space in this direction is filled with an enemy piece
						if(coordinateVallid(fromX - 2, fromX + 2)){
							if(checkerboard[toX - 2][toY + 2] == 0){//if the space after the one filled with an enemy piece is empty
								return true;
							}
						}
					}
				}
				break;
			case 3://upper right
				if(coordinateVallid(fromX + 1, fromX - 1)){
					if(checkerboard[toX + 1][toY - 1] == 2){//if the next space in this direction is filled with an enemy piece
						if(coordinateVallid(fromX + 2, fromX - 2)){
							if(checkerboard[toX + 2][toY - 2] == 0){//if the space after the one filled with an enemy piece is empty
								return true;
							}
						}
					}
				}
				break;
			case 4://lower right
				if(coordinateVallid(fromX + 1, fromX + 1)){
					if(checkerboard[toX + 1][toY + 1] == 2){//if the next space in this direction is filled with an enemy piece
						if(coordinateVallid(fromX + 2, fromX + 2)){
							if(checkerboard[toX + 2][toY + 2] == 0){//if the space after the one filled with an enemy piece is empty
								return true;
							}
						}
					}
				}
				break;
			default: break;
			}
		}else{//black's turn
			
		}
		return false;
	}
	
	boolean spotEmpty(int x, int y){
		if(checkerboard[x][y] == 0){//as stated in variable declaration, 0 = empty board spot
			return true;
		}
		return false;//the spot is not empty
	}
	
	boolean pieceOwned(int x, int y){//if the piece is owned by the side whose turn it currently is
		if(redTurn){
			if(checkerboard[x][y] == 1){//piece is a red piece
				return true;
			}
		}else{//black's turn
			if(checkerboard[x][y] == 2){//piece is a black piece
				return true;
			}
		}
		return false;//return true did not occur, so the piece is not owned by the side whose turn it currently is
	}
	
	boolean coordinateVallid(int x, int y){
		if(x > -1 && x < boardSize && y > -1 && y < boardSize){//if the value of the coordinate is in the bounds of the board's grid;
			//	-1 is less than the minimum value which may be used as an index into the array; 
			//	< boardSize is used because less than boardSize + 1 would result in the Java Virtual Machine throwing an IndexOutOfBoundsException - 
			//	the maximum value which may be used as an index in this case is boardSize - 1, and checking < bouadSize - 1 would prevent vallid values of 
			//	boardSize - 1 from returning true
			return true;
		}
		return false;
	}
	
	void resetGame(){
		gameOver = false;
		moving = false;
		pieceHopped = false;
		for(int x = 0; x < boardSize; x++){
			for(int y = 0; y < boardSize; y++){
				checkerboard[x][y] = 0;
			}
		}
		setBoard();
		fromX = -1;
		fromY = -1;
		toX = -1;
		toY = -1;
		pieceHoppedX = -1;
		pieceHoppedY = -1;
		valueCheck = 0;
		numRedCheckers = 8;
		numBlackCheckers = 8;
		redTurn = true;
		System.out.println();//printing an extra line to separate output of prior game from input of next game
	}
	
	void printBoard(){
		String output;
		output = "";
		System.out.println("New board state:");
		for(int y = 0; y < boardSize; y++){//iterating in order of y, do all x, then y again, ETC, because System.out.println() outputs data from left to right on the Console; 
			//	this is like reading a book: next line(going down the page by increasing y coordinate value), then go left to right across the page(going right by increasing x coordinate value)
			for(int x = 0; x < boardSize; x++){
				output = "";//resetting output so that the prior line is not printed with the new one
				if(checkerboard[x][y] == 0){//building total output for this left-to-right line of the board
					output += "_";
				}else if(checkerboard[x][y] == 1){
					output += "R";
				}else if(checkerboard[x][y] == 2){
					output += "B";
				}
			}
			System.out.println(output);//printing each iterated line of the board
		}
	}
}
