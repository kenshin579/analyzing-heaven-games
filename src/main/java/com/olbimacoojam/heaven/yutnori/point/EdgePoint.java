package com.olbimacoojam.heaven.yutnori.point;

import com.olbimacoojam.heaven.yutnori.piece.moveresult.Route;
import com.olbimacoojam.heaven.yutnori.yut.Yut;
import lombok.Getter;

@Getter
public class EdgePoint implements Point {

    private final PointName pointName;
    private Point nextPoint;
    private Point previousPoint;
    private Point inflectPoint;

    EdgePoint(PointName pointName) {
        this.pointName = pointName;
    }

    @Override
    public Point findNextDestination(Route route, Yut yut) {
        if (route.isStartingPoint()) {
            return inflectPoint;
        }
        return nextPoint;
    }

    @Override
    public void makeConnection(Point previousPoint, Point nextPoint) {
        this.previousPoint = previousPoint;
        this.nextPoint = nextPoint;
    }

    @Override
    public void addInflectPoint(Point inflectPoint) {
        this.inflectPoint = inflectPoint;
    }

    @Override
    public String toString() {
        return "EdgePoint{" +
                "pointName=" + pointName +
                ", nextPoint=" + nextPoint.getPointName() +
                ", inflectPoint=" + inflectPoint.getPointName() +
                '}';
    }
}
