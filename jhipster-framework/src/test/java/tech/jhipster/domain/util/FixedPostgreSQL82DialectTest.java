/*
 * Copyright 2016-2021 the original author or authors from the JHipster project.
 *
 * This file is part of the JHipster project, see https://www.jhipster.tech/
 * for more information.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package tech.jhipster.domain.util;

import tech.jhipster.test.LogbackRecorder;
import org.hibernate.dialect.Dialect;
import org.hibernate.type.descriptor.sql.BinaryTypeDescriptor;
import org.hibernate.type.descriptor.sql.BlobTypeDescriptor;
import org.hibernate.type.descriptor.sql.BooleanTypeDescriptor;
import org.hibernate.type.descriptor.sql.SqlTypeDescriptor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Types;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class FixedPostgreSQL82DialectTest {

    private final List<LogbackRecorder> recorders = new LinkedList<>();

    private final Map<Integer, String> registered = new LinkedHashMap<>();

    private FixedPostgreSQL82Dialect dialect;

    @BeforeEach
    void setup() {
        recorders.add(LogbackRecorder.forName("org.jboss.logging").reset().capture("ALL"));
        recorders.add(LogbackRecorder.forClass(Dialect.class).reset().capture("ALL"));

        dialect = new FixedPostgreSQL82Dialect() {

            @Override
            protected void registerColumnType(int code, String name) {
                registered.put(code, name);
                super.registerColumnType(code, name);
            }

        };
    }

    @AfterEach
    void teardown() {
        recorders.forEach(LogbackRecorder::release);
        recorders.clear();
        registered.clear();
    }

    @Test
    void testBlobTypeRegister() {
        assertThat(registered.get(Types.BLOB)).isEqualTo("bytea");
    }

    @Test
    void testBlobTypeRemap() {
        SqlTypeDescriptor descriptor = dialect.remapSqlTypeDescriptor(BlobTypeDescriptor.DEFAULT);
        assertThat(descriptor).isEqualTo(BinaryTypeDescriptor.INSTANCE);
    }

    @Test
    void testOtherTypeRemap() {
        SqlTypeDescriptor descriptor = dialect.remapSqlTypeDescriptor(BooleanTypeDescriptor.INSTANCE);
        assertThat(descriptor).isEqualTo(BooleanTypeDescriptor.INSTANCE);
    }
}
