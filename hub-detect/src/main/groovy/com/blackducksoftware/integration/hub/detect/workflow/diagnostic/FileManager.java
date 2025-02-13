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
package com.blackducksoftware.integration.hub.detect.workflow.diagnostic;

import java.io.File;
import java.nio.file.Path;
import java.util.stream.Stream;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.integration.hub.detect.detector.ExtractionId;
import com.blackducksoftware.integration.hub.detect.workflow.file.DirectoryManager;

public class FileManager {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private enum InterestMode {
        AllFiles,
        NoProtectedFiles,
        NoFiles
    }

    private InterestMode interestMode;
    private File relevantDirectory;

    public FileManager(boolean diagnosticMode, boolean protectedMode, DirectoryManager directoryManager) {
        if (diagnosticMode && protectedMode) {
            this.interestMode = InterestMode.AllFiles;
        } else if (diagnosticMode) {
            this.interestMode = InterestMode.NoProtectedFiles;
        } else {
            this.interestMode = InterestMode.NoFiles;
        }

        if (this.interestMode != InterestMode.NoFiles) {
            relevantDirectory = directoryManager.getRelevantOutputDirectory();
        }
    }

    public void registerOutputFileForCleanup(final File file) {
        boolean wasNotInterested = !registerGlobalFileOfInterest(file);
        if (wasNotInterested) {
            file.delete();
        }
    }

    public boolean registerFileOfInterest(final ExtractionId extractionId, final File file) {
        if (this.interestMode == InterestMode.AllFiles) {
            return registerFileOfInterest(file, extractionId.toUniqueString());
        }
        return false;
    }

    public boolean registerGlobalFileOfInterest(final File file) {
        if (this.interestMode == InterestMode.AllFiles) {
            return registerFileOfInterest(file, "global");
        }
        return false;
    }

    private boolean registerFileOfInterest(final File file, final String directoryName) {
        try {
            if (file == null) {
                return false;
            }
            if (isChildOfTrackedFolder(file)) {
                logger.debug("Asked to track file '" + file.getPath() + "' but it is already being tracked.");
                return false;
            }
            if (file.isFile()) {
                final File dest = findNextAvailableRelevant(directoryName, file.getName());
                FileUtils.copyFile(file, dest);
            } else if (file.isDirectory()) {
                final File dest = findNextAvailableRelevant(directoryName, file.getName());
                FileUtils.copyDirectory(file, dest);
            }
            return true;
        } catch (final Exception e) {
            logger.trace("Failed to copy file to relevant directory:" + file.toString());
            return false;
        }
    }

    private boolean isChildOfTrackedFolder(final File file) {
        final Path filePath = file.toPath();
        return Stream.of(relevantDirectory).anyMatch(trackedFile -> filePath.startsWith(trackedFile.toPath()));
    }

    private File findNextAvailableRelevant(final String directoryName, final String name) {
        final File given = new File(new File(relevantDirectory, directoryName), name);
        if (given.exists()) {
            return findNextAvailableRelevant(directoryName, name, 1);
        } else {
            return given;
        }
    }

    private File findNextAvailableRelevant(final String directoryName, final String name, final int attempt) {
        final File next = new File(new File(relevantDirectory, directoryName), name + "_" + attempt);
        if (next.exists()) {
            return findNextAvailableRelevant(directoryName, name, attempt + 1);
        } else {
            return next;
        }
    }
}
