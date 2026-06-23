package com.snakegame.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Responsible for saving and loading the best scores in a local file.
 *
 * Each line of the file contains a name and a score separated by a comma.
 * The list is kept sorted from highest to lowest and limited to the top positions.
 */
public class HighScoreManager {

    private static final String FILE_NAME  = "highscores.txt";
    private static final int    MAX_SCORES = 5;

    private final List<ScoreEntry> scores = new ArrayList<>();

    public HighScoreManager() {
        load();
    }

    /**
     * Adds a new score to the list.
     * After inserting, the list is resorted and exceeding entries are discarded.
     * The file is updated immediately so no data is lost.
     */
    public void addScore(String name, int score) {
        scores.add(new ScoreEntry(name, score));
        Collections.sort(scores);
        while (scores.size() > MAX_SCORES) {
            scores.remove(scores.size() - 1);
        }
        save();
    }

    public List<ScoreEntry> getScores() {
        return Collections.unmodifiableList(scores);
    }

    /** Returns true if the received score enters the current ranking. */
    public boolean isHighScore(int score) {
        if (scores.size() < MAX_SCORES) return true;
        return score > scores.get(scores.size() - 1).score;
    }

    /** Reads the scores file and populates the internal list. */
    private void load() {
        scores.clear();
        try {
            FileHandle fh = Gdx.files.local(FILE_NAME);
            if (!fh.exists()) return;
            for (String line : fh.readString().split("\n")) {
                line = line.trim();
                if (line.isEmpty()) continue;
                String[] parts = line.split(",", 2);
                if (parts.length == 2) {
                    scores.add(new ScoreEntry(parts[0].trim(), Integer.parseInt(parts[1].trim())));
                }
            }
            Collections.sort(scores);
        } catch (Exception e) {
            Gdx.app.log("HighScoreManager", "Error loading scores: " + e.getMessage());
        }
    }

    /** Writes the current list to the file, overwriting the previous content. */
    private void save() {
        try {
            StringBuilder sb = new StringBuilder();
            for (ScoreEntry e : scores) {
                sb.append(e.name).append(",").append(e.score).append("\n");
            }
            Gdx.files.local(FILE_NAME).writeString(sb.toString(), false);
        } catch (Exception e) {
            Gdx.app.log("HighScoreManager", "Error saving scores: " + e.getMessage());
        }
    }

    /** Represents an entry in the ranking with the player's name and score. */
    public static class ScoreEntry implements Comparable<ScoreEntry> {
        public final String name;
        public final int    score;

        public ScoreEntry(String name, int score) {
            this.name  = name;
            this.score = score;
        }

        // Descending sort: higher scores come first
        @Override
        public int compareTo(ScoreEntry other) {
            return Integer.compare(other.score, this.score);
        }
    }
}