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
package com.blackducksoftware.integration.hub.detect.detector.clang;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;

import com.google.gson.Gson;

public class CompileCommandsJsonFile {

    public static List<CompileCommand> parseJsonCompilationDatabaseFile(final Gson gson, final File compileCommandsJsonFile) throws IOException {
        final String compileCommandsJson = FileUtils.readFileToString(compileCommandsJsonFile, StandardCharsets.UTF_8);
        final CompileCommandJsonData[] compileCommands = gson.fromJson(compileCommandsJson, CompileCommandJsonData[].class);
        return Arrays.stream(compileCommands).map(rawCommand -> new CompileCommand(rawCommand)).collect(Collectors.toList());
    }
}
