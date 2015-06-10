package com.graduation.autotracking2.utils;

import org.opencv.core.Point;

public class Line {
	public Point start;
	public Point end;
	public Line(Point start,Point end) {
		this.start=start;
		this.end = end;
	}
	public double getMidX() {
		return (start.x+end.x)/2;
	}
	public double getMidY() {
		return (start.y+end.y)/2;
	}
}
