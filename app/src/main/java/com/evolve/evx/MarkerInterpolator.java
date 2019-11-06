package com.evolve.evx;

/**
 * Created by eleazerarcilla on 02/12/2018.
 */

public class MarkerInterpolator implements android.view.animation.Interpolator {
    double mAmplitude = 1;
    double mFrequency = 10;

    public MarkerInterpolator(double amplitude, double frequency) {
        mAmplitude = amplitude;
        mFrequency = frequency;
    }

    public float getInterpolation(float time) {
        double amplitude = mAmplitude;
        if (amplitude == 0) { amplitude = 0.05; }

        // The interpolation curve equation:
        //    -e^(-time / amplitude) * cos(frequency * time) + 1
        //
        // View the graph live: https://www.desmos.com/calculator/6gbvrm5i0s
        return (float) (-1 * Math.pow(Math.E, -time/ mAmplitude) * Math.cos(mFrequency * time) + 1);
    }
}
