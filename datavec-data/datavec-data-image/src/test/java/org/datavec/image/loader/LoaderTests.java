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

package org.datavec.image.loader;

import org.apache.commons.io.FilenameUtils;
import org.datavec.api.io.filters.BalancedPathFilter;
import org.datavec.api.records.reader.RecordReader;
import org.datavec.api.split.FileSplit;
import org.datavec.api.split.InputSplit;
import org.datavec.image.recordreader.ImageRecordReader;
import org.junit.Test;

import java.io.*;
import java.util.List;
import java.util.Random;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 *
 */
public class LoaderTests {

    @Test
    public void testLfwReader() throws Exception {
        String subDir = "lfw-a/lfw";
        File path = new File(FilenameUtils.concat(System.getProperty("user.home"), subDir));
        FileSplit fileSplit = new FileSplit(path, LFWLoader.ALLOWED_FORMATS, new Random(42));
        BalancedPathFilter pathFilter = new BalancedPathFilter(new Random(42), LFWLoader.LABEL_PATTERN, 1, 1, 1);
        InputSplit[] inputSplit = fileSplit.sample(pathFilter, 1);
        RecordReader rr = new ImageRecordReader(250, 250, 3, LFWLoader.LABEL_PATTERN);
        rr.initialize(inputSplit[0]);
        List<String> exptedLabel = rr.getLabels();

        RecordReader rr2 = new LFWLoader(new int[] {250, 250, 3}, true).getRecordReader(1, 1, 1, new Random(42));

        assertEquals(exptedLabel.get(0), rr2.getLabels().get(0));
    }

    @Test
    public void testCifarLoader() {
        File dir = new File(FilenameUtils.concat(System.getProperty("user.home"), "cifar"));
        CifarLoader cifar = new CifarLoader(false, dir.toString());
        assertTrue(dir.exists());

        cifar.load();
        assertTrue(cifar.getLabels() != null);
    }

    @Test
    public void testCifarInputStream() throws Exception {
        String subDir = "cifar/cifar-10-batches-bin/test_batch.bin";
        String path = FilenameUtils.concat(System.getProperty("user.home"), subDir);
        byte[] fullDataExpected = new byte[3073];
        FileInputStream inExpected = new FileInputStream(new File(path));
        inExpected.read(fullDataExpected);

        byte[] fullDataActual = new byte[3073];
        CifarLoader cifarLoad = new CifarLoader(false);
        InputStream inActual = cifarLoad.getInputStream();
        inActual.read(fullDataActual);
        assertEquals(fullDataExpected[0], fullDataActual[0]);
    }
}
