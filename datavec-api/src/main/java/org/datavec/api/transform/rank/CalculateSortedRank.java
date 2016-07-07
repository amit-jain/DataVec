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

package org.datavec.api.transform.rank;

import org.datavec.api.transform.metadata.ColumnMetaData;
import org.datavec.api.transform.schema.Schema;
import org.datavec.api.transform.metadata.LongMetaData;
import lombok.Data;
import org.datavec.api.writable.Writable;
import org.datavec.api.transform.schema.SequenceSchema;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * CalculateSortedRank: calculate the rank of each example, after sorting example.
 * For example, we might have some numerical "score" column, and we want to know for the rank (sort order) for each
 * example, according to that column.<br>
 * The rank of each example (after sorting) will be added in a new Long column. Indexing is done from 0; examples will have
 * values 0 to dataSetSize-1.<br>
 *
 * Currently, CalculateSortedRank can only be applied on standard (i.e., non-sequence) data.
 * Furthermore, the current implementation can only sort on one column
 *
 * @author Alex Black
 */
@Data
public class CalculateSortedRank implements Serializable {

    private final String newColumnName;
    private final String sortOnColumn;
    private final Comparator<Writable> comparator;
    private final boolean ascending;
    private Schema inputSchema;

    /**
     *
     * @param newColumnName    Name of the new column (will contain the rank for each example)
     * @param sortOnColumn     Name of the column to sort on
     * @param comparator       Comparator used to sort examples
     */
    public CalculateSortedRank(String newColumnName, String sortOnColumn, Comparator<Writable> comparator) {
        this(newColumnName, sortOnColumn, comparator, true);
    }

    /**
     *
     * @param newColumnName    Name of the new column (will contain the rank for each example)
     * @param sortOnColumn     Name of the column to sort on
     * @param comparator       Comparator used to sort examples
     * @param ascending        Whether examples should be ascending or descending, using the comparator
     */
    public CalculateSortedRank(String newColumnName, String sortOnColumn, Comparator<Writable> comparator, boolean ascending) {
        this.newColumnName = newColumnName;
        this.sortOnColumn = sortOnColumn;
        this.comparator = comparator;
        this.ascending = ascending;
    }

    public Schema transform(Schema inputSchema){
        if(inputSchema instanceof SequenceSchema) throw new IllegalStateException("Calculating sorted rank on sequences: not yet supported");

        List<String> origNames = inputSchema.getColumnNames();
        List<ColumnMetaData> origMeta = inputSchema.getColumnMetaData();

        List<String> newNames = new ArrayList<>(origNames);
        List<ColumnMetaData> newMeta = new ArrayList<>(origMeta);

        newNames.add(newColumnName);
        newMeta.add(new LongMetaData(0L,null));

        return inputSchema.newSchema(newNames, newMeta);
    }

    public void setInputSchema(Schema schema){
        this.inputSchema = schema;
    }

    public Schema getInputSchema(){
        return inputSchema;
    }

    @Override
    public String toString(){
        return "CalculateSortedRank(newColumnName=\"" + newColumnName + "\", comparator=" + comparator + ")";
    }
}
