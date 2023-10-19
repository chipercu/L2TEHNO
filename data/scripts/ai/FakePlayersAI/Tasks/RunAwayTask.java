package ai.FakePlayersAI.Tasks;

import ai.FakePlayersAI.PathManager.AStarAlgorithm;
import ai.FakePlayersAI.PathManager.PathFinder;
import ai.FakePlayersAI.PathManager.PathMap;
import ai.FakePlayersAI.PathManager.Point;
import l2open.common.RunnableImpl;
import l2open.gameserver.model.L2Player;
import l2open.util.Location;

import java.util.ArrayList;
import java.util.List;

import static ai.FakePlayersAI.Tasks.TASK_STATUS.*;

public class RunAwayTask extends RunnableImpl {

    private L2Player player;
    private TASK_STATUS task_status = ON;
    private PathMap pathMap;


    private int currentPoint = 0;
    private Point[] route;

    public RunAwayTask(L2Player player, PathMap pathMap) {
        this.player = player;
        this.pathMap = pathMap;
    }

    @Override
    public void runImpl() throws Exception {
//        if (player == null){
//            return;
//        }
        if (getTask_status() == ON){

            List<Point> points = new ArrayList<>();
           Point a = new Point("A", new Location(-20,5, 0)); points.add(a);
           Point b = new Point("B", new Location(-15,-10, 0)); points.add(b);
           Point c = new Point("C", new Location(5,-10, 0)); points.add(c);
           Point d = new Point("D", new Location(-5,5,0)); points.add(d);
           Point e = new Point("E", new Location(10,5,0)); points.add(e);
           Point f = new Point("F", new Location(-5,15,0)); points.add(f);
           Point g = new Point("G", new Location(-15,20,0)); points.add(g);
           Point h = new Point("H", new Location(20,-5,0)); points.add(h);
           Point i = new Point("I", new Location(10,15,0)); points.add(i);
           Point k = new Point("K", new Location(20,15,0)); points.add(k);
           Point l = new Point("L", new Location(5,25,0)); points.add(l);
           Point m = new Point("M", new Location(20,-20,0)); points.add(m);
// Добавьте остальные точки из списка
            Point j = new Point("J", new Location(-30,10,0));
            Point n = new Point("N", new Location(30,10,0));



            List<Point> path = PathFinder.findPath(j, n, points);
            if (!path.isEmpty()) {
                for (Point point : path) {
                    System.out.println(point.getName() + " " + point.getLoc().x + " - " + point.getLoc().y);
                }
            } else {
                System.out.println("Маршрут не найден!");
            }
//            followRoute();
        }
    }

//    public void followRoute() {
//        Point currentDestination = route[currentPoint];
//
//        // Перемещение NPC к текущей точке маршрута
//        player.getAI().addTaskMove(new Location(currentDestination.getX(), currentDestination.getY(), currentDestination.getZ()), true);
//
//        // Проверка достижения текущей точки маршрута
//        if (npcReachedDestination(currentDestination, 20)) {
//            currentPoint++;
//            // Проверка, достигнут ли конец маршрута
//            if (currentPoint >= route.length) {
//                currentPoint = 0; // NPC начинает снова с начала маршрута
//            }
//        }
//    }

//    private boolean npcReachedDestination(Point destination, int tolerance) {
//        // Получение текущих координат NPC
//        int currentX = destination.getX();
//        int currentY = destination.getY();
//
//        // Проверка, достиг ли NPC заданной точки маршрута с учетом погрешности
//        if (Math.abs(currentX - destination.getX()) <= tolerance && Math.abs(currentY - destination.getY()) <= tolerance) {
//            return true; // NPC достиг заданной точки маршрута с учетом погрешности
//        } else {
//            return false; // NPC еще не достиг заданной точки маршрута с учетом погрешности
//        }
//    }



    public L2Player getPlayer() {
        return player;
    }

    public TASK_STATUS getTask_status() {
        return task_status;
    }

    public void setTask_status(TASK_STATUS task_status) {
        this.task_status = task_status;
    }
}
