package com.olbimacoojam.heaven.minesweeper.domain;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class BoardGenerator {
    private final Integer rows;
    private final Integer columns;
    private final MinePositionGenerator minePositionGenerator;

    public BoardGenerator(final Integer rows, final Integer columns, final MinePositionGenerator minePositionGenerator) {
        this.rows = rows;
        this.columns = columns;
        this.minePositionGenerator = minePositionGenerator;
    }

    public Board generate() {
        Map<Position, Block> board = new HashMap<>();
        Set<Position> minePositions = minePositionGenerator.generate();

        for (Integer y = 0; y < rows; y++) {
            board.putAll(generateRow(y, minePositions));
        }

        return Board.of(board);
    }

    private Map<Position, Block> generateRow(Integer y, Set<Position> minePositions) {
        Map<Position, Block> row = new HashMap<>();

        for (Integer x = 0; x < columns; x++) {
            Position position = Position.of(x, y);
            Block block = generateBlock(position, minePositions);
            row.put(position, block);
        }

        return row;
    }

    private Block generateBlock(Position position, Set<Position> minePositions) {
        if (minePositions.contains(position)) {
            return Block.of(BlockType.MINE, BlockStatus.UNCLICKED);
        }

        return Block.of(BlockType.BLOCK, BlockStatus.UNCLICKED);
    }
}
