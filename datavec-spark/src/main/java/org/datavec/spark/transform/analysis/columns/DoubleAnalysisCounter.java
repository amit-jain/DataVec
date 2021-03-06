/*
 *  * Copyright 2016 Skymind, Inc.
 *  *
 *  *    Licensed under the Apache License, Version 2.0 (the "License");
 *  *    you may not use this file except in compliance with the License.
 *  *    You may obtain a copy of the License at
 *  *
 *  *        http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  *    Unless required by applicable law or agreed to in writing, software
 *  *    distributed under the License is distributed on an "AS IS" BASIS,
 *  *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  *    See the License for the specific language governing permissions and
 *  *    limitations under the License.
 */

package org.datavec.spark.transform.analysis.columns;

import org.datavec.spark.transform.analysis.AnalysisCounter;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.datavec.api.writable.Writable;

/**
 * A counter function for doing analysis on Double columns, on Spark
 *
 * @author Alex Black
 */
@AllArgsConstructor @Data
public class DoubleAnalysisCounter implements AnalysisCounter<DoubleAnalysisCounter> {

    private long countZero;
    private long countPositive;
    private long countNegative;
    private long countMinValue;
    private double minValueSeen = Double.MAX_VALUE;
    private long countMaxValue;
    private double maxValueSeen = -Double.MIN_VALUE;
    private long countNaN;
    private double sum;
    private long countTotal;

    private double mean;    //Running mean
    private double m2;      //Running variance numerator (sum (x-mean)^2)

    public DoubleAnalysisCounter(){

    }

    @Override
    public DoubleAnalysisCounter add(Writable writable) {
        double value = writable.toDouble();

        if(value == 0) countZero++;
        else if(value < 0) countNegative++;
        else if(value > 0) countPositive++;
        else if(Double.isNaN(value)) countNaN++;

        if(value == minValueSeen){
            countMinValue++;
        } else if( value < minValueSeen ){
            //New minimum value
            minValueSeen = value;
            countMinValue = 1;
        } //Don't need an else condition: if value > minValueSeen, no change to min value or count

        if(value == maxValueSeen){
            countMaxValue++;
        } else if(value > maxValueSeen){
            //new maximum value
            maxValueSeen = value;
            countMaxValue = 1;
        } //Don't need else condition: if value < maxValueSeen, no change to max value or count

        sum += value;
        countTotal++;

        double delta = value - mean;
        mean += delta / countTotal;
        m2 += delta * (value - mean);

        return this;
    }

    public DoubleAnalysisCounter merge(DoubleAnalysisCounter other){
        if(minValueSeen == other.minValueSeen){
            countMinValue += other.countMinValue;
        } else if(minValueSeen > other.minValueSeen) {
            //Keep other, take count from other
            minValueSeen = other.minValueSeen;
            countMinValue = other.countMinValue;
        } //else: Keep this min, no change to count

        if(maxValueSeen == other.maxValueSeen){
            countMaxValue += other.countMaxValue;
        } else if(maxValueSeen < other.maxValueSeen) {
            //Keep other, take count from other
            maxValueSeen = other.maxValueSeen;
            countMaxValue = other.countMaxValue;
        } //else: Keep this max, no change to count

        if(countTotal == 0){
            mean = other.mean;
            m2 = other.m2;
        } else if(other.countTotal != 0){
            double delta  = other.mean - mean;
            long tCount = countTotal + other.countTotal;
            //For numerical stability, as per Spark StatCounter
            if(10 * other.countTotal < countTotal ){
                mean = mean + (delta * other.countTotal) / tCount;
            } else if(10 * countTotal < other.countTotal ){
                mean = other.mean - (delta * countTotal) / tCount;
            } else {
                mean = (mean * countTotal + other.mean*other.countTotal) / tCount;
            }
            m2 += other.m2 + (delta * delta * countTotal * other.countTotal) / tCount;
        }

        countZero += other.countZero;
        countPositive += other.countPositive;
        countNegative += other.countNegative;
        sum += other.sum;
        countNaN += other.countNaN;
        countTotal += other.countTotal;


        return this;
    }

    public double getSampleVariance(){
        if(countTotal <= 1) return Double.NaN;
        return m2 / (countTotal - 1);
    }

    public double getSampleStdev(){
        return Math.sqrt(getSampleVariance());
    }


}
