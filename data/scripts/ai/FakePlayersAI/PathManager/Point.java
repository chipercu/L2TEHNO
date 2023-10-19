package ai.FakePlayersAI.PathManager;

import l2open.util.Location;

import java.util.List;
import java.util.Objects;

public class Point implements Comparable<Point> {
    private final Location loc;
    private String name;
    private double gScore; // Cost from start to this point
    private double fScore; // Total cost from start to goal through this point
    private Point previous;
    private Point target;
    private Double distToPrevious;
    private Double distToTarget;

    private double weight;

    public Point(String name, Location loc) {
        this.name = name;
        this.loc = loc;
        this.gScore = Double.POSITIVE_INFINITY;
        this.fScore = Double.POSITIVE_INFINITY;
        this.previous = null;
    }


    public void setName(String name) {
        this.name = name;
    }

    public double getgScore() {
        return gScore;
    }

    public void setgScore(double gScore) {
        this.gScore = gScore;
    }

    public double getfScore() {
        return fScore;
    }

    public void setfScore(double fScore) {
        this.fScore = fScore;
    }

    public Point getTarget() {
        return target;
    }

    public void setTarget(Point target) {
        this.target = target;
    }

    public Double getDistToPrevious() {
        return distToPrevious;
    }

    public void setDistToPrevious(Double distToPrevious) {
        this.distToPrevious = distToPrevious;
    }

    public Double getDistToTarget() {
        return distToTarget;
    }

    public void setDistToTarget(Double distToTarget) {
        this.distToTarget = distToTarget;
    }

    public Location getLoc() {
        return loc;
    }

    public String getName() {
        return name;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }


    public double getGScore() {
        return gScore;
    }

    public void setGScore(double gScore) {
        this.gScore = gScore;
    }

    public double getFScore() {
        return fScore;
    }

    public void setFScore(double fScore) {
        this.fScore = fScore;
    }

    public Point getPrevious() {
        return previous;
    }

    public void setPrevious(Point previous) {
        this.previous = previous;
    }

    public double calculateDistance(Point other) {
        return this.loc.distance(other.loc);
    }

    public double calculateHeuristic(Point goal) {
        return calculateDistance(goal);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof Point)) {
            return false;
        }

        Point other = (Point) obj;
        return this.loc.equals(other.loc);
    }

    @Override
    public int hashCode() {
        return Objects.hash(loc);
    }

    @Override
    public int compareTo(Point other) {
        return Double.compare(this.weight, other.weight);
    }

}
