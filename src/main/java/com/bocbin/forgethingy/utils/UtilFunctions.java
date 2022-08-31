package com.bocbin.forgethingy.utils;

public class UtilFunctions {

	public static float scale(float val, float oldMin, float oldMax, float newMin, float newMax) {
		if (oldMax == oldMin) return (newMax + newMin) / 2;
		float a = (val - oldMin) / (oldMax - oldMin);
		return a * (newMax - newMin) + newMin;
	}
}
