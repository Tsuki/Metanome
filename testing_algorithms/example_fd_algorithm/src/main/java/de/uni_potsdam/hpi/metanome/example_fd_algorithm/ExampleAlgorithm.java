/*
 * Copyright 2014 by the Metanome project
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

package de.uni_potsdam.hpi.metanome.example_fd_algorithm;

import de.uni_potsdam.hpi.metanome.algorithm_integration.AlgorithmConfigurationException;
import de.uni_potsdam.hpi.metanome.algorithm_integration.ColumnCombination;
import de.uni_potsdam.hpi.metanome.algorithm_integration.ColumnIdentifier;
import de.uni_potsdam.hpi.metanome.algorithm_integration.algorithm_types.FunctionalDependencyAlgorithm;
import de.uni_potsdam.hpi.metanome.algorithm_integration.algorithm_types.RelationalInputParameterAlgorithm;
import de.uni_potsdam.hpi.metanome.algorithm_integration.algorithm_types.StringParameterAlgorithm;
import de.uni_potsdam.hpi.metanome.algorithm_integration.configuration.ConfigurationSpecification;
import de.uni_potsdam.hpi.metanome.algorithm_integration.configuration.ConfigurationSpecificationCsvFile;
import de.uni_potsdam.hpi.metanome.algorithm_integration.configuration.ConfigurationSpecificationSqlIterator;
import de.uni_potsdam.hpi.metanome.algorithm_integration.configuration.ConfigurationSpecificationString;
import de.uni_potsdam.hpi.metanome.algorithm_integration.input.RelationalInputGenerator;
import de.uni_potsdam.hpi.metanome.algorithm_integration.result_receiver.CouldNotReceiveResultException;
import de.uni_potsdam.hpi.metanome.algorithm_integration.result_receiver.FunctionalDependencyResultReceiver;
import de.uni_potsdam.hpi.metanome.algorithm_integration.results.FunctionalDependency;

import java.util.ArrayList;
import java.util.List;

public class ExampleAlgorithm implements FunctionalDependencyAlgorithm, StringParameterAlgorithm, RelationalInputParameterAlgorithm {

    protected String path = null;
    protected FunctionalDependencyResultReceiver resultReceiver;

    @Override
    public List<ConfigurationSpecification> getConfigurationRequirements() {
        List<ConfigurationSpecification> configurationSpecification = new ArrayList<ConfigurationSpecification>();

        configurationSpecification.add(new ConfigurationSpecificationString("pathToOutputFile"));
        configurationSpecification.add(new ConfigurationSpecificationCsvFile("input file"));
        configurationSpecification.add(new ConfigurationSpecificationSqlIterator("DB-connection"));

        return configurationSpecification;
    }

    @Override
    public void execute() {
        if (path != null) {
            try {
                resultReceiver.receiveResult(
                        new FunctionalDependency(
                                new ColumnCombination(
                                        new ColumnIdentifier("table1", "column1"),
                                        new ColumnIdentifier("table1", "column2")),
                                new ColumnIdentifier("table1", "column5")
                        )
                );
            } catch (CouldNotReceiveResultException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    @Override
    public void setResultReceiver(FunctionalDependencyResultReceiver resultReceiver) {
        this.resultReceiver = resultReceiver;
    }

    @Override
    public void setConfigurationValue(String identifier, String... values) throws AlgorithmConfigurationException {
        if ((identifier.equals("pathToOutputFile")) && (values.length == 1)) {
            path = values[0];
        } else {
            throw new AlgorithmConfigurationException("Incorrect identifier or value list length.");
        }
    }

    @Override
    public void setConfigurationValue(String identifier,
                                      RelationalInputGenerator... values)
            throws AlgorithmConfigurationException {
        if (identifier.equals("input file")) {
            System.out.println("Input file is not being set on algorithm.");
        }
    }

}
