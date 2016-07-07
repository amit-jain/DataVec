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

package org.datavec.spark.transform.quality.categorical;

import org.apache.spark.api.java.function.Function2;
import org.datavec.api.transform.dataquality.columns.CategoricalQuality;

/**
 * Created by Alex on 5/03/2016.
 */
public class CategoricalQualityMergeFunction implements Function2<CategoricalQuality,CategoricalQuality,CategoricalQuality> {
    @Override
    public CategoricalQuality call(CategoricalQuality v1, CategoricalQuality v2) throws Exception {
        return v1.add(v2);
    }
}
