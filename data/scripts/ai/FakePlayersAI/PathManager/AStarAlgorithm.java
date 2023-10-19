package ai.FakePlayersAI.PathManager;

import java.util.*;

public class AStarAlgorithm {
    public static List<Point> findPath(Point start, Point goal, List<Point> points) {
        PriorityQueue<Point> openList = new PriorityQueue<>();
        Set<Point> closedList = new HashSet<>();

        start.setGScore(0);
        start.setFScore(start.calculateHeuristic(goal));

        openList.add(start);

        while (!openList.isEmpty()) {
            Point currentPoint = openList.poll();

            if (currentPoint.equals(goal)) {
                return reconstructPath(currentPoint);
            }

            closedList.add(currentPoint);

            List<Point> neighbors = getNeighbors(currentPoint, points);

            for (Point neighbor : neighbors) {
                if (closedList.contains(neighbor)) {
                    continue;
                }

                double tentativeGScore = currentPoint.getGScore() + currentPoint.calculateDistance(neighbor);

                boolean isInOpenList = openList.contains(neighbor);

                if (!isInOpenList || tentativeGScore < neighbor.getGScore()) {
                    neighbor.setPrevious(currentPoint);
                    neighbor.setGScore(tentativeGScore);
                    neighbor.setFScore(neighbor.calculateHeuristic(goal));

                    if (!isInOpenList) {
                        openList.add(neighbor);
                    }
                }
            }
        }

        return new ArrayList<>(); // No path found
    }

    private static List<Point> reconstructPath(Point point) {
        List<Point> path = new ArrayList<>();
        while (point != null) {
            path.add(0, point);
            point = point.getPrevious();
        }
        return path;
    }

    private static List<Point> getNeighbors(Point point, List<Point> points) {
        List<Point> neighbors = new ArrayList<>();
        for (Point p : points) {
            if (p.equals(point)) {
                continue;
            }
            neighbors.add(p);
        }
        return neighbors;
    }


}
