package ai.FakePlayersAI.PathManager;

import l2open.util.Location;

import java.util.List;

public class PathMap {
    private String pathMapName;
    private List<Point> pathPointList;


    public PathMap() {
    }

    public PathMap(String pathMapName, List<Point> pathPointList) {
        this.pathMapName = pathMapName;
        this.pathPointList = pathPointList;
    }

    public List<Point> getPathPointList() {
        return pathPointList;
    }

    public String getPathMapName() {
        return pathMapName;
    }

    public void setPathPointList(List<Point> pathPointList) {
        this.pathPointList = pathPointList;
    }

    public void setPathMapName(String pathMapName) {
        this.pathMapName = pathMapName;
    }

}
