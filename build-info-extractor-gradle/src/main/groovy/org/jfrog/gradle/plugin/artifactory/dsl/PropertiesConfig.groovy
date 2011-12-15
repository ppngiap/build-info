/*
 * Copyright (C) 2011 JFrog Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jfrog.gradle.plugin.artifactory.dsl

import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.api.artifacts.UnknownConfigurationException
import org.jfrog.build.client.ArtifactSpec
import org.jfrog.build.client.ConfigArtifactSpecs

/**
 * @author Yoav Landman
 */
class PropertiesConfig {
    final Project project
    final ConfigArtifactSpecs artifactSpecs = new ConfigArtifactSpecs()

    PropertiesConfig(org.gradle.api.Project project) {
        this.project = project
    }

    def methodMissing(String name, args) {
        def artifactSpecNotation
        def props
        switch (args.length) {
            case 2:
                //Verify the configuration exists
                try {
                    project.getConfigurations().getByName(name)
                } catch (UnknownConfigurationException e) {
                    project.logger.error(e.message)
                }
                artifactSpecNotation = args[1]
                props = args[0]
                break
            default:
                throw new GradleException("Invalid artifact properties spec: $name, $args.\nExpected: configName artifactSpec key:val, key:val")
        }
        def spec = ArtifactSpec.builder().artifactNotation(artifactSpecNotation).configuration(name).properties(props).build()
        def specs = artifactSpecs[name] ?: new ArrayList<ArtifactSpec>()
        specs.add(spec)
        artifactSpecs.put(name, specs)
    }
}