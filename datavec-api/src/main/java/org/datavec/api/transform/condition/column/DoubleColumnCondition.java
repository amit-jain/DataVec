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

package org.datavec.api.transform.condition.column;

import org.datavec.api.transform.condition.SequenceConditionMode;
import org.datavec.api.writable.Writable;
import org.datavec.api.transform.condition.ConditionOp;

import java.util.Set;

/**
 * Condition that applies to the values in a Double column, using a {@link ConditionOp}
 *
 * @author Alex Black
 */
public class DoubleColumnCondition extends BaseColumnCondition {

    private final ConditionOp op;
    private final double value;
    private final Set<Double> set;

    /**
     * Constructor for operations such as less than, equal to, greater than, etc.
     * Uses default sequence condition mode, {@link BaseColumnCondition#DEFAULT_SEQUENCE_CONDITION_MODE}
     *
     * @param column Column to check for the condition
     * @param op     Operation (<, >=, !=, etc)
     * @param value  Value to use in the condition
     */
    public DoubleColumnCondition(String column, ConditionOp op, double value) {
        this(column, DEFAULT_SEQUENCE_CONDITION_MODE, op, value);
    }

    /**
     * Constructor for operations such as less than, equal to, greater than, etc.
     *
     * @param column                Column to check for the condition
     * @param sequenceConditionMode Mode for handling sequence data
     * @param op                    Operation (<, >=, !=, etc)
     * @param value                 Value to use in the condition
     */
    public DoubleColumnCondition(String column, SequenceConditionMode sequenceConditionMode,
                                 ConditionOp op, double value) {
        super(column, sequenceConditionMode);
        if (op == ConditionOp.InSet || op == ConditionOp.NotInSet) {
            throw new IllegalArgumentException("Invalid condition op: cannot use this constructor with InSet or NotInSet ops");
        }
        this.op = op;
        this.value = value;
        this.set = null;
    }

    /**
     * Constructor for operations: ConditionOp.InSet, ConditionOp.NotInSet.
     * Uses default sequence condition mode, {@link BaseColumnCondition#DEFAULT_SEQUENCE_CONDITION_MODE}
     *
     * @param column Column to check for the condition
     * @param op     Operation. Must be either ConditionOp.InSet, ConditionOp.NotInSet
     * @param set    Set to use in the condition
     */
    public DoubleColumnCondition(String column, ConditionOp op, Set<Double> set) {
        this(column, DEFAULT_SEQUENCE_CONDITION_MODE, op, set);
    }

    /**
     * Constructor for operations: ConditionOp.InSet, ConditionOp.NotInSet
     *
     * @param column                Column to check for the condition
     * @param sequenceConditionMode Mode for handling sequence data
     * @param op                    Operation. Must be either ConditionOp.InSet, ConditionOp.NotInSet
     * @param set                   Set to use in the condition
     */
    public DoubleColumnCondition(String column, SequenceConditionMode sequenceConditionMode,
                                 ConditionOp op, Set<Double> set) {
        super(column, sequenceConditionMode);
        if (op != ConditionOp.InSet && op != ConditionOp.NotInSet) {
            throw new IllegalArgumentException("Invalid condition op: can ONLY use this constructor with InSet or NotInSet ops");
        }
        this.op = op;
        this.value = 0;
        this.set = set;
    }


    @Override
    public boolean columnCondition(Writable writable) {
        switch (op) {
            case LessThan:
                return writable.toDouble() < value;
            case LessOrEqual:
                return writable.toDouble() <= value;
            case GreaterThan:
                return writable.toDouble() > value;
            case GreaterOrEqual:
                return writable.toDouble() >= value;
            case Equal:
                return writable.toDouble() == value;
            case NotEqual:
                return writable.toDouble() != value;
            case InSet:
                return set.contains(writable.toDouble());
            case NotInSet:
                return !set.contains(writable.toDouble());
            default:
                throw new RuntimeException("Unknown or not implemented op: " + op);
        }
    }
}
