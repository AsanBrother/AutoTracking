package com.graduation.autotracking2.utils;

import java.util.Comparator;


public class SortedPoint  implements Comparator<Line>{


	@Override
	public int compare(Line lhs, Line rhs) {
		// TODO Auto-generated method stub
		
		//根据屏幕的方向来选择对X还是Y方向进行排序
		//return (int)(lhs.getMidX()-rhs.getMidX());
		
		return (int)(lhs.getMidY()-rhs.getMidY());
	}

}
