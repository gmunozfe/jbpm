/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.test.persistence.scripts.util;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Predicate;

import org.jbpm.test.persistence.scripts.DatabaseType;


public class ScriptFilter {

    public enum Option {
        DISALLOW_EMTPY_RESULTS, // if the filter allow no results
        THROW_ON_SCRIPT_ERROR // if the filter allows script errors
    }

    private Set<DatabaseType> dbTypes;
    private Set<Option> options;
    private List<Predicate<File>> predicates;

    @SafeVarargs
    public ScriptFilter(Predicate<File>... filters) {
        this.predicates = new ArrayList<>();
        this.options = new TreeSet<>();
        this.dbTypes = new TreeSet<>();
        Collections.addAll(this.dbTypes, DatabaseType.values());
        Collections.addAll(this.predicates, filters);
    }

    @SafeVarargs
    public static ScriptFilter create(Predicate<File>... filters) {
        return new ScriptFilter(filters);
    }

    public static ScriptFilter filter(String... scripts) {
        Predicate<File> predicate = Arrays.asList(scripts).stream().map(s -> (Predicate<File>) file -> file.getName().contains(s)).reduce(x -> true, Predicate::or);
        ScriptFilter filter = new ScriptFilter(predicate);
        return filter;
    }

    public static ScriptFilter init(boolean springboot, boolean create) {
        Predicate<File> filterExtension = file -> file.getName().toLowerCase().endsWith(".sql");

        Predicate<File> filterSpringboot = file -> file.getName().toLowerCase().contains("springboot");
        filterSpringboot = springboot ? filterSpringboot : filterSpringboot.negate();

        Predicate<File> filterBytea = file -> !file.getName().toLowerCase().contains("bytea");

        Predicate<File> filterName = file -> file.getName().contains("drop");
        filterName = !create ? filterName : filterName.negate();
        ScriptFilter filter = new ScriptFilter(filterExtension, filterName, filterSpringboot, filterBytea);
        if (create) {
            filter.setOptions(Option.DISALLOW_EMTPY_RESULTS, Option.THROW_ON_SCRIPT_ERROR);
        }
        return filter;
    }

    public ScriptFilter setSupportedDatabase(DatabaseType... types) {
        this.dbTypes.clear();
        Collections.addAll(this.dbTypes, types);
        return this;
    }

    public boolean isSupportedDatabase(DatabaseType type) {
        return dbTypes.contains(type);
    }

    public ScriptFilter setOptions(Option... elements) {
        Collections.addAll(this.options, elements);
        return this;
    }

    public boolean hasOption(Option option) {
        return options.contains(option);
    }

    public Predicate<File> build() {
        return predicates.stream().reduce(x -> true, Predicate::and);
    }

}