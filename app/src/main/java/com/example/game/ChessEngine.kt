package com.example.game

import kotlin.math.abs

enum class PieceColor { WHITE, BLACK }
enum class PieceType(val symbolWhite: String, val symbolBlack: String, val value: Int) {
    PAWN("♙", "♟", 1),
    KNIGHT("♘", "♞", 3),
    BISHOP("♗", "♝", 3),
    ROOK("♖", "♜", 5),
    QUEEN("♕", "♛", 9),
    KING("♔", "♚", 100)
}

data class ChessPiece(
    val type: PieceType,
    val color: PieceColor
)

data class ChessMove(
    val fromRow: Int,
    val fromCol: Int,
    val toRow: Int,
    val toCol: Int
)

data class ChessState(
    val board: Array<Array<ChessPiece?>>,
    val currentTurn: PieceColor = PieceColor.WHITE,
    val selectedSquare: Pair<Int, Int>? = null,
    val validMovesForSelected: List<Pair<Int, Int>> = emptyList(),
    val isCheck: Boolean = false,
    val winner: PieceColor? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ChessState) return false
        return board.contentDeepEquals(other.board) && currentTurn == other.currentTurn
    }

    override fun hashCode(): Int {
        return board.contentDeepHashCode() * 31 + currentTurn.hashCode()
    }
}

object ChessEngine {

    fun createInitialBoard(): Array<Array<ChessPiece?>> {
        val board = Array(8) { Array<ChessPiece?>(8) { null } }
        // Black main pieces
        board[0][0] = ChessPiece(PieceType.ROOK, PieceColor.BLACK)
        board[0][1] = ChessPiece(PieceType.KNIGHT, PieceColor.BLACK)
        board[0][2] = ChessPiece(PieceType.BISHOP, PieceColor.BLACK)
        board[0][3] = ChessPiece(PieceType.QUEEN, PieceColor.BLACK)
        board[0][4] = ChessPiece(PieceType.KING, PieceColor.BLACK)
        board[0][5] = ChessPiece(PieceType.BISHOP, PieceColor.BLACK)
        board[0][6] = ChessPiece(PieceType.KNIGHT, PieceColor.BLACK)
        board[0][7] = ChessPiece(PieceType.ROOK, PieceColor.BLACK)
        for (c in 0..7) board[1][c] = ChessPiece(PieceType.PAWN, PieceColor.BLACK)

        // White main pieces
        for (c in 0..7) board[6][c] = ChessPiece(PieceType.PAWN, PieceColor.WHITE)
        board[7][0] = ChessPiece(PieceType.ROOK, PieceColor.WHITE)
        board[7][1] = ChessPiece(PieceType.KNIGHT, PieceColor.WHITE)
        board[7][2] = ChessPiece(PieceType.BISHOP, PieceColor.WHITE)
        board[7][3] = ChessPiece(PieceType.QUEEN, PieceColor.WHITE)
        board[7][4] = ChessPiece(PieceType.KING, PieceColor.WHITE)
        board[7][5] = ChessPiece(PieceType.BISHOP, PieceColor.WHITE)
        board[7][6] = ChessPiece(PieceType.KNIGHT, PieceColor.WHITE)
        board[7][7] = ChessPiece(PieceType.ROOK, PieceColor.WHITE)
        return board
    }

    fun getValidMoves(board: Array<Array<ChessPiece?>>, r: Int, c: Int): List<Pair<Int, Int>> {
        val piece = board[r][c] ?: return emptyList()
        val moves = mutableListOf<Pair<Int, Int>>()

        when (piece.type) {
            PieceType.PAWN -> {
                val dir = if (piece.color == PieceColor.WHITE) -1 else 1
                val startRow = if (piece.color == PieceColor.WHITE) 6 else 1
                // Forward 1
                if (r + dir in 0..7 && board[r + dir][c] == null) {
                    moves.add(Pair(r + dir, c))
                    // Forward 2 from start
                    if (r == startRow && board[r + 2 * dir][c] == null) {
                        moves.add(Pair(r + 2 * dir, c))
                    }
                }
                // Diagonal capture
                for (dc in listOf(-1, 1)) {
                    val nc = c + dc
                    val nr = r + dir
                    if (nr in 0..7 && nc in 0..7 && board[nr][nc] != null && board[nr][nc]?.color != piece.color) {
                        moves.add(Pair(nr, nc))
                    }
                }
            }
            PieceType.KNIGHT -> {
                val jumps = listOf(
                    Pair(-2, -1), Pair(-2, 1), Pair(-1, -2), Pair(-1, 2),
                    Pair(1, -2), Pair(1, 2), Pair(2, -1), Pair(2, 1)
                )
                for ((dr, dc) in jumps) {
                    val nr = r + dr
                    val nc = c + dc
                    if (nr in 0..7 && nc in 0..7 && (board[nr][nc] == null || board[nr][nc]?.color != piece.color)) {
                        moves.add(Pair(nr, nc))
                    }
                }
            }
            PieceType.BISHOP -> addRayMoves(board, r, c, piece.color, listOf(Pair(-1, -1), Pair(-1, 1), Pair(1, -1), Pair(1, 1)), moves)
            PieceType.ROOK -> addRayMoves(board, r, c, piece.color, listOf(Pair(-1, 0), Pair(1, 0), Pair(0, -1), Pair(0, 1)), moves)
            PieceType.QUEEN -> addRayMoves(board, r, c, piece.color, listOf(
                Pair(-1, 0), Pair(1, 0), Pair(0, -1), Pair(0, 1),
                Pair(-1, -1), Pair(-1, 1), Pair(1, -1), Pair(1, 1)
            ), moves)
            PieceType.KING -> {
                for (dr in -1..1) {
                    for (dc in -1..1) {
                        if (dr == 0 && dc == 0) continue
                        val nr = r + dr
                        val nc = c + dc
                        if (nr in 0..7 && nc in 0..7 && (board[nr][nc] == null || board[nr][nc]?.color != piece.color)) {
                            moves.add(Pair(nr, nc))
                        }
                    }
                }
            }
        }
        return moves
    }

    private fun addRayMoves(
        board: Array<Array<ChessPiece?>>,
        r: Int,
        c: Int,
        color: PieceColor,
        directions: List<Pair<Int, Int>>,
        outMoves: MutableList<Pair<Int, Int>>
    ) {
        for ((dr, dc) in directions) {
            var currR = r + dr
            var currC = c + dc
            while (currR in 0..7 && currC in 0..7) {
                val target = board[currR][currC]
                if (target == null) {
                    outMoves.add(Pair(currR, currC))
                } else {
                    if (target.color != color) {
                        outMoves.add(Pair(currR, currC))
                    }
                    break
                }
                currR += dr
                currC += dc
            }
        }
    }
}
