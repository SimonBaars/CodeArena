package nl.sandersimon.clonedetection.model;

import net.minecraft.scoreboard.Score;

public class CloneScore {
	private Score score;
	private int scorePoints = 0;
	
	public CloneScore () {
		this.score = null;
	}
	
	public CloneScore (Score score) {
		this.score = score;
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
	}

	@Override
	public String toString() {
		return "CloneScore [score=" + scorePoints + "]";
	}
}
