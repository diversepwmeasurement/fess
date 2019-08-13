/*
 * Copyright 2012-2019 CodeLibs Project and the Others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.codelibs.fess.helper;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.codelibs.core.io.InputStreamUtil;
import org.codelibs.core.io.ResourceUtil;
import org.codelibs.fess.Constants;
import org.codelibs.fess.exception.FessSystemException;
import org.codelibs.fess.helper.PluginHelper.Artifact;
import org.codelibs.fess.helper.PluginHelper.PluginType;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.lastaflute.di.exception.IORuntimeException;

public class PluginHelperTest extends UnitFessTestCase {
    private PluginHelper pluginHelper;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        pluginHelper = new PluginHelper() {
            protected String[] getRepositories() {
                return new String[] { "plugin/repo1/", "plugin/repo2/" };
            }

            protected String getRepositoryContent(String url) {
                if (url.contains("plugin/repo1")) {
                    try (InputStream is = ResourceUtil.getResourceAsStream(url)) {
                        return new String(InputStreamUtil.getBytes(is), Constants.UTF_8);
                    } catch (IOException e) {
                        throw new IORuntimeException(e);
                    }
                } else if (url.contains("plugin/repo2")) {
                    try (InputStream is = ResourceUtil.getResourceAsStream(url)) {
                        return new String(InputStreamUtil.getBytes(is), Constants.UTF_8);
                    } catch (IOException e) {
                        throw new IORuntimeException(e);
                    }
                }
                throw new FessSystemException("unknown");
            }
        };
    }

    public void test_processRepository1() {
        List<Artifact> list = pluginHelper.processRepository(PluginType.DATA_STORE, "plugin/repo1/", "index.html");
        assertEquals(7, list.size());
        assertEquals("fess-ds-atlassian", list.get(0).getName());
        assertEquals("12.2.0", list.get(0).getVersion());
        assertEquals("plugin/repo1/fess-ds-atlassian/12.2.0/fess-ds-atlassian-12.2.0.jar", list.get(0).getUrl().replace("//", "/"));
    }

    public void test_processRepository2() {
        List<Artifact> list = pluginHelper.processRepository(PluginType.DATA_STORE, "plugin/repo2/", "index.html");
        assertEquals(7, list.size());
        assertEquals("fess-ds-atlassian", list.get(0).getName());
        assertEquals("12.2.0", list.get(0).getVersion());
        assertEquals("plugin/repo2/fess-ds-atlassian/12.2.0/fess-ds-atlassian-12.2.0.jar", list.get(0).getUrl().replace("//", "/"));
    }

    public void test_getArtifactFromFileName1() {
        Artifact artifact = pluginHelper.getArtifactFromFileName(PluginType.DATA_STORE, "fess-ds-atlassian-13.2.0.jar");
        assertEquals("fess-ds-atlassian", artifact.getName());
        assertEquals("13.2.0", artifact.getVersion());
    }

    public void test_getArtifactFromFileName2() {
        Artifact artifact = pluginHelper.getArtifactFromFileName(PluginType.DATA_STORE, "fess-ds-atlassian-13.2.1-20190708.212247-1.jar");
        assertEquals("fess-ds-atlassian", artifact.getName());
        assertEquals("13.2.1-20190708.212247-1", artifact.getVersion());
    }
}
