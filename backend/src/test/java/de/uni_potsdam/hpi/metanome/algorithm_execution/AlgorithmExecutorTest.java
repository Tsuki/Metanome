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

package de.uni_potsdam.hpi.metanome.algorithm_execution;

import de.uni_potsdam.hpi.metanome.algorithm_integration.AlgorithmConfigurationException;
import de.uni_potsdam.hpi.metanome.algorithm_integration.AlgorithmExecutionException;
import de.uni_potsdam.hpi.metanome.algorithm_integration.algorithm_execution.FileGenerator;
import de.uni_potsdam.hpi.metanome.algorithm_integration.input.RelationalInputGenerator;
import de.uni_potsdam.hpi.metanome.algorithm_integration.results.FunctionalDependency;
import de.uni_potsdam.hpi.metanome.algorithm_integration.results.InclusionDependency;
import de.uni_potsdam.hpi.metanome.algorithm_integration.results.UniqueColumnCombination;
import de.uni_potsdam.hpi.metanome.algorithm_loading.AlgorithmLoadingException;
import de.uni_potsdam.hpi.metanome.configuration.ConfigurationValue;
import de.uni_potsdam.hpi.metanome.configuration.ConfigurationValueRelationalInputGenerator;
import de.uni_potsdam.hpi.metanome.configuration.ConfigurationValueString;
import de.uni_potsdam.hpi.metanome.result_receiver.CloseableOmniscientResultReceiver;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class AlgorithmExecutorTest {

    protected CloseableOmniscientResultReceiver resultReceiver;
    protected ProgressCache progressCache;
    protected FileGenerator fileGenerator;

    protected AlgorithmExecutor executor;

    @Before
    public void setUp() throws UnsupportedEncodingException {
        resultReceiver = mock(CloseableOmniscientResultReceiver.class);
        progressCache = mock(ProgressCache.class);
        fileGenerator = new TempFileGenerator();

        executor = new AlgorithmExecutor(resultReceiver, progressCache, fileGenerator);
    }

    /**
     * Test method for {@link de.uni_potsdam.hpi.metanome.algorithm_execution.AlgorithmExecutor#executeAlgorithm(String, List)}
     * <p/>
     * Tests the execution of an fd algorithm. The elapsed time should be greater than 0ns.
     *
     * @throws de.uni_potsdam.hpi.metanome.algorithm_loading.AlgorithmLoadingException
     * @throws AlgorithmConfigurationException
     * @throws AlgorithmExecutionException
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     * @throws InstantiationException
     * @throws ClassNotFoundException
     * @throws IOException
     * @throws SecurityException
     * @throws IllegalArgumentException
     */
    @Test
    public void executeFunctionalDependencyAlgorithmTest() throws AlgorithmLoadingException, AlgorithmExecutionException, IllegalArgumentException, SecurityException, IOException, ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        // Setup
        List<ConfigurationValue> configs = new ArrayList<ConfigurationValue>();
        configs.add(new ConfigurationValueString("pathToOutputFile", "path/to/file"));

        // Execute functionality
        long elapsedTime = executor.executeAlgorithmWithValues("example_fd_algorithm.jar", configs);

        // Check result
        verify(resultReceiver).receiveResult(isA(FunctionalDependency.class));
        assertTrue(0 <= elapsedTime);
    }

    /**
     * Tests the execution of an ind algorithm.
     *
     * @throws AlgorithmConfigurationException
     * @throws AlgorithmLoadingException
     * @throws AlgorithmExecutionException
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     * @throws InstantiationException
     * @throws ClassNotFoundException
     * @throws IOException
     * @throws SecurityException
     * @throws IllegalArgumentException
     */
    @Test
    public void executeInclusionDependencyTest() throws AlgorithmLoadingException, AlgorithmExecutionException, IllegalArgumentException, SecurityException, IOException, ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        // Setup
        List<ConfigurationValue> configs = new ArrayList<ConfigurationValue>();
        configs.add(new ConfigurationValueString("tableName", "table1"));
        configs.add(new ConfigurationValueRelationalInputGenerator(
                "input file",
                mock(RelationalInputGenerator.class),
                mock(RelationalInputGenerator.class)));

        // Execute functionality
        executor.executeAlgorithmWithValues("example_ind_algorithm.jar", configs);

        // Check result
        verify(resultReceiver).receiveResult(isA(InclusionDependency.class));
    }

    /**
     * Tests the execution of an ucc algorithm.
     *
     * @throws AlgorithmConfigurationException
     * @throws AlgorithmLoadingException
     * @throws AlgorithmExecutionException
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     * @throws InstantiationException
     * @throws ClassNotFoundException
     * @throws IOException
     * @throws SecurityException
     * @throws IllegalArgumentException
     */
    @Test
    public void executeUniqueColumnCombinationsAlgorithmTest() throws AlgorithmLoadingException, AlgorithmExecutionException, IllegalArgumentException, SecurityException, IOException, ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        // Setup
        List<ConfigurationValue> configs = new ArrayList<ConfigurationValue>();
        configs.add(new ConfigurationValueString("pathToInputFile", "path/to/file1", "path/to/file2"));

        // Execute functionality
        executor.executeAlgorithmWithValues("example_ucc_algorithm.jar", configs);

        // Check result
        verify(resultReceiver).receiveResult(isA(UniqueColumnCombination.class));
        // After finishing the progress should be 1;
        verify(progressCache).updateProgress(1);
    }

    /**
     * Tests the execution of an holistic algorithm.
     *
     * @throws AlgorithmExecutionException
     * @throws AlgorithmConfigurationException
     * @throws AlgorithmLoadingException
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     * @throws InstantiationException
     * @throws ClassNotFoundException
     * @throws IOException
     * @throws SecurityException
     * @throws IllegalArgumentException
     */
    @Test
    public void testExecuteHolisticAlgorithm() throws AlgorithmLoadingException, AlgorithmExecutionException, IllegalArgumentException, SecurityException, IOException, ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        // Setup
        List<ConfigurationValue> configs = new ArrayList<ConfigurationValue>();
        configs.add(new ConfigurationValueString("pathToOutputFile", "path/to/file1"));

        // Execute functionality
        executor.executeAlgorithmWithValues("example_holistic_algorithm.jar", configs);

        // Check result
        verify(resultReceiver).receiveResult(isA(FunctionalDependency.class));
        verify(resultReceiver).receiveResult(isA(UniqueColumnCombination.class));
    }

    //FIXME add test for incorrect file name

    /**
     * Test method for {@link de.uni_potsdam.hpi.metanome.algorithm_execution.AlgorithmExecutor#close()}
     * <p/>
     * When closing the executor all attached result receiver should be closed.
     *
     * @throws IOException
     */
    @Test
    public void testClose() throws IOException {
        // Execute functionality
        executor.close();

        // Check result
        verify(resultReceiver).close();
    }

}