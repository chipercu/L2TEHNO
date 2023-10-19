package ai.FakePlayersAI.PathManager;

import l2open.gameserver.geodata.GeoEngine;
import l2open.gameserver.model.L2Skill;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.skills.DocumentSkill;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PathFinder {

    public static List<Point> findPath(Point start, Point goal, List<Point> points){
        List<Point> path = new ArrayList<>();
        if (start.equals(goal)){
            path.add(start);
            path.add(goal);
            return path;
        }
        points.forEach(p -> {
            p.setDistToPrevious(p.calculateDistance(start));
            p.setDistToTarget(p.calculateDistance(goal));
        });

        points.sort((o1, o2) -> (int) (o1.getDistToPrevious() - o2.getDistToPrevious()));

        for (Point p: points){


            

        }



        while (!points.isEmpty()){
            final Point point = nextPoint(start, points, goal);
            points.forEach(p -> p.setWeight(point.calculateDistance(point) + p.calculateDistance(goal)));

            path.add(point);
            if (point.equals(goal)){
                return path;
            }
            points.remove(point);

        }
        return path;
    }


    private static Point nextPoint(Point point, List<Point> points, Point goal){
        Point nearestPoint = point;
        double toGoalDist = Double.POSITIVE_INFINITY;

        final List<Point> collect = points.stream().filter(p -> !p.equals(point)).collect(Collectors.toList());
        for (Point p : collect){
            final Point nearestPoint1 = findNearestPoint(collect, p);
            final double distance = point.calculateDistance(goal);
            if (distance < toGoalDist){
                nearestPoint = nearestPoint1;
                toGoalDist = distance;
            }
        }
        System.out.println(nearestPoint.getName());
        return nearestPoint;
    }

    private static boolean isVisible(Point point1, Point point2){
        return GeoEngine.canSeeCoord(
                point1.getLoc().x,
                point1.getLoc().y,
                point1.getLoc().z,
                point2.getLoc().x,
                point2.getLoc().y,
                point2.getLoc().z,
                false, 0);
    }


    public static Point findNearestPoint(List<Point> points, Point target) {
        if (points == null || points.isEmpty()) {
            return null; // Если список пуст, вернуть null
        }

        Point nearestPoint = points.get(0);

        double minDistance = target.calculateDistance(nearestPoint);

        for (int i = 1; i < points.size(); i++) {
            Point currentPoint = points.get(i);
            double distance = target.calculateDistance(nearestPoint);
            if (distance < minDistance) {
                minDistance = distance;
                nearestPoint = currentPoint;
            }
        }
        return nearestPoint;
    }



}
