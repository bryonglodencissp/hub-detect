/**
 * hub-detect
 *
 * Copyright (C) 2018 Black Duck Software, Inc.
 * http://www.blackducksoftware.com/
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.blackducksoftware.integration.hub.detect.detector.rubygems;

import java.io.File;

import com.blackducksoftware.integration.hub.detect.detector.Detector;
import com.blackducksoftware.integration.hub.detect.detector.DetectorEnvironment;
import com.blackducksoftware.integration.hub.detect.detector.DetectorType;
import com.blackducksoftware.integration.hub.detect.detector.ExtractionId;
import com.blackducksoftware.integration.hub.detect.workflow.extraction.Extraction;
import com.blackducksoftware.integration.hub.detect.workflow.file.DetectFileFinder;
import com.blackducksoftware.integration.hub.detect.workflow.search.result.DetectorResult;
import com.blackducksoftware.integration.hub.detect.workflow.search.result.FileNotFoundDetectorResult;
import com.blackducksoftware.integration.hub.detect.workflow.search.result.PassedDetectorResult;

public class GemlockDetector extends Detector {
    public static final String GEMFILE_LOCK_FILENAME = "Gemfile.lock";

    private final DetectFileFinder fileFinder;
    private final GemlockExtractor gemlockExtractor;

    File gemlock;

    public GemlockDetector(final DetectorEnvironment environment, final DetectFileFinder fileFinder, final GemlockExtractor gemlockExtractor) {
        super(environment, "Gemlock", DetectorType.RUBYGEMS);
        this.fileFinder = fileFinder;
        this.gemlockExtractor = gemlockExtractor;
    }

    @Override
    public DetectorResult applicable() {
        gemlock = fileFinder.findFile(environment.getDirectory(), GEMFILE_LOCK_FILENAME);
        if (gemlock == null) {
            return new FileNotFoundDetectorResult(GEMFILE_LOCK_FILENAME);
        }

        return new PassedDetectorResult();
    }

    @Override
    public DetectorResult extractable() {
        return new PassedDetectorResult();
    }

    @Override
    public Extraction extract(final ExtractionId extractionId) {
        return gemlockExtractor.extract(environment.getDirectory(), gemlock);
    }

}
