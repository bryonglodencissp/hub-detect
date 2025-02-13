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
package com.blackducksoftware.integration.hub.detect.util;

import java.util.Deque;
import java.util.LinkedList;

import com.synopsys.integration.hub.bdio.model.dependency.Dependency;

public class DependencyHistory {
    private final Deque<Dependency> dependencyStack = new LinkedList<>();

    public void clearDependenciesDeeperThan(final int dependencyLevel) throws IllegalStateException {
        if (dependencyLevel > dependencyStack.size()) {
            throw new IllegalStateException(String.format("Level of dependency should be less than or equal to %s but was %s. Treating the dependency as though level was %s.", dependencyStack.size(), dependencyLevel, dependencyStack.size()));
        }

        final int levelDelta = (dependencyStack.size() - dependencyLevel);
        for (int levels = 0; levels < levelDelta; levels++) {
            dependencyStack.pop();
        }
    }

    public void clear() {
        dependencyStack.clear();
    }

    public void add(final Dependency dependency) {
        dependencyStack.push(dependency);
    }

    public boolean isEmpty() {
        return dependencyStack.isEmpty();
    }

    public Dependency getLastDependency() {
        return dependencyStack.peek();
    }

}
