package nl.sandersimon.clonedetection.model;

import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;

public class CloneScore {
	private Score score;
	private int scorePoints = 0;
	private final String name;
	
	public CloneScore (String name) {
		this.score = null;
		this.name = name;
	}
	
	public CloneScore (String name, Score score) {
		this.score = score;
		this.name = name;
	}
	
	public void increaseScore(int s) {
		scorePoints+=s;
		if(score != null) score.increaseScore(s);
	}
	
	public void incrementScore() {
		scorePoints++;
		if(score != null) score.incrementScore();
	}

	public int getScorePoints() {
		return scorePoints;
	}

	public void setScorePoints(int scorePoints) {
		this.scorePoints = scorePoints;
		if(score != null) score.setScorePoints(scorePoints);
	}

	public Score getScore() {
		return score;
	}

	public void setScore(Score score) {
		this.score = score;
		this.score.setScorePoints(0);
	}

	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return "CloneScore [score=" + scorePoints + "]";
	}

	public void setScore(ScoreObjective scoreBoard) {
		setScore(scoreBoard.getScoreboard().getOrCreateScore(getName(), scoreBoard));
	}
}
