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

package org.datavec.api.transform.dataquality.columns;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Quality of a Long column
 *
 * @author Alex Black
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class LongQuality extends ColumnQuality {

    private final long countNonLong;

    public LongQuality(){
        this(0,0,0,0,0);
    }

    public LongQuality(long countValid, long countInvalid, long countMissing, long countTotal, long countNonLong){
        super(countValid,countInvalid,countMissing,countTotal);
        this.countNonLong = countNonLong;
    }


    public LongQuality add(LongQuality other){
        return new LongQuality(
                countValid + other.countValid,
                countInvalid + other.countInvalid,
                countMissing + other.countMissing,
                countTotal + other.countTotal,
                countNonLong + other.countNonLong);
    }

    @Override
    public String toString(){
        return "LongQuality(" + super.toString() + ", countNonLong=" + countNonLong + ")";
    }

}
