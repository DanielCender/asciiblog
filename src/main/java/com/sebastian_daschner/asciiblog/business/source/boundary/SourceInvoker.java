/*
 * Copyright (C) 2015 Sebastian Daschner, sebastian-daschner.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sebastian_daschner.asciiblog.business.source.boundary;

import com.sebastian_daschner.asciiblog.business.entries.control.EntriesCache;
import com.sebastian_daschner.asciiblog.business.source.control.EntryCompiler;
import com.sebastian_daschner.asciiblog.business.source.control.GitExtractor;

import javax.annotation.PostConstruct;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import java.io.File;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@Singleton
@Startup
public class SourceInvoker {

    @Inject
    GitExtractor gitExtractor;

    @Inject
    EntryCompiler entryCompiler;

    @Inject
    EntriesCache cache;

    @PostConstruct
    @Schedule(second = "0", minute = "*", hour = "*")
    public void checkNewEntries() {
        final Map<String, String> changedFiles = gitExtractor.getChangedFiles();
        changedFiles.entrySet().stream()
                .map(e -> entryCompiler.compile(e.getKey(), e.getValue())).filter(Objects::nonNull)
                .forEach(cache::store);
    }

}