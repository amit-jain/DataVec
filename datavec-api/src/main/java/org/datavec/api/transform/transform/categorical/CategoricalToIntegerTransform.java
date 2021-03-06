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

package org.datavec.api.transform.transform.categorical;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.datavec.api.transform.metadata.CategoricalMetaData;
import org.datavec.api.transform.metadata.ColumnMetaData;
import org.datavec.api.transform.metadata.IntegerMetaData;
import org.datavec.api.transform.schema.Schema;
import org.datavec.api.transform.transform.BaseTransform;
import org.datavec.api.writable.IntWritable;
import org.datavec.api.writable.Writable;

import java.util.*;

/**
 * Created by Alex on 4/03/2016.
 */
@Data
@JsonIgnoreProperties({"inputSchema", "columnIdx", "stateNames", "statesMap"})
public class CategoricalToIntegerTransform extends BaseTransform {

    private String columnName;
    private int columnIdx = -1;
    private List<String> stateNames;
    private Map<String, Integer> statesMap;

    public CategoricalToIntegerTransform(@JsonProperty("columnName") String columnName) {
        this.columnName = columnName;
    }

    @Override
    public void setInputSchema(Schema inputSchema) {
        super.setInputSchema(inputSchema);

        columnIdx = inputSchema.getIndexOfColumn(columnName);
        ColumnMetaData meta = inputSchema.getMetaData(columnName);
        if (!(meta instanceof CategoricalMetaData)) throw new IllegalStateException("Cannot convert column \"" +
                columnName + "\" from categorical to one-hot: column is not categorical (is: " + meta.getColumnType() + ")");
        this.stateNames = ((CategoricalMetaData) meta).getStateNames();

        this.statesMap = new HashMap<>(stateNames.size());
        for (int i = 0; i < stateNames.size(); i++) {
            this.statesMap.put(stateNames.get(i), i);
        }
    }

    @Override
    public Schema transform(Schema schema) {

        List<String> origNames = schema.getColumnNames();
        List<ColumnMetaData> origMeta = schema.getColumnMetaData();

        int i = 0;
        Iterator<String> namesIter = origNames.iterator();
        Iterator<ColumnMetaData> typesIter = origMeta.iterator();

        List<ColumnMetaData> newMeta = new ArrayList<>(schema.numColumns());

        while (namesIter.hasNext()) {
            String s = namesIter.next();
            ColumnMetaData t = typesIter.next();

            if (i++ == columnIdx) {
                //Convert this to integer
                int nClasses = stateNames.size();
                newMeta.add(new IntegerMetaData(t.getName(), 0, nClasses - 1));
            } else {
                newMeta.add(t);
            }
        }

        return schema.newSchema(newMeta);
    }

    @Override
    public List<Writable> map(List<Writable> writables) {
        if (writables.size() != inputSchema.numColumns()) {
            throw new IllegalStateException("Cannot execute transform: input writables list length (" + writables.size() + ") does not " +
                    "match expected number of elements (schema: " + inputSchema.numColumns() + "). Transform = " + toString());
        }
        int idx = getColumnIdx();

        int n = stateNames.size();
        List<Writable> out = new ArrayList<>(writables.size() + n);

        int i = 0;
        for (Writable w : writables) {

            if (i++ == idx) {
                //Do conversion
                String str = w.toString();
                Integer classIdx = statesMap.get(str);
                if (classIdx == null) throw new RuntimeException("Unknown state (index not found): " + str);
                out.add(new IntWritable(classIdx));
            } else {
                //No change to this column
                out.add(w);
            }
        }
        return out;
    }

    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof CategoricalToIntegerTransform)) return false;

        CategoricalToIntegerTransform o2 = (CategoricalToIntegerTransform)o;

        if(columnName == null){
            return o2.columnName == null;
        } else {
            return columnName.equals(o2.columnName);
        }
    }

    public int hashCode() {
        return columnName.hashCode();
    }

    protected boolean canEqual(Object other) {
        return other instanceof CategoricalToIntegerTransform;
    }
}
