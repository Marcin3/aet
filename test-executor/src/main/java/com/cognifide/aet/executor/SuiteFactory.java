/**
 * AET
 *
 * Copyright (C) 2013 Cognifide Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.cognifide.aet.executor;

import com.cognifide.aet.communication.api.metadata.Comparator;
import com.cognifide.aet.communication.api.metadata.Operation;
import com.cognifide.aet.communication.api.metadata.Step;
import com.cognifide.aet.communication.api.metadata.Suite;
import com.cognifide.aet.communication.api.metadata.Test;
import com.cognifide.aet.communication.api.metadata.Url;
import com.cognifide.aet.executor.model.CollectorStep;
import com.cognifide.aet.executor.model.ComparatorStep;
import com.cognifide.aet.executor.model.ExtendedUrl;
import com.cognifide.aet.executor.model.ParametrizedStep;
import com.cognifide.aet.executor.model.TestRun;
import com.cognifide.aet.executor.model.TestSuiteRun;
import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;

class SuiteFactory {

  Suite suiteFromTestSuiteRun(TestSuiteRun testSuiteRun) {
    Suite suite = suiteFromTestRun(testSuiteRun);

    for (Map.Entry<String, TestRun> testRunEntry : testSuiteRun.getTestRunMap().entrySet()) {
      final TestRun testRun = testRunEntry.getValue();
      suite.addTest(testFromTestRun(testSuiteRun, testRun));
    }
    return suite;
  }

  private Test testFromTestRun(TestSuiteRun testSuiteRun, TestRun testRun) {
    Test test = new Test(testRun.getName(), testRun.getUseProxy());
    final Map<String, List<ComparatorStep>> comparatorSteps = testRun.getComparatorSteps();

    for (ExtendedUrl extendedUrl : testRun.getUrls()) {
      test.addUrl(urlFromExtendedUrl(testSuiteRun, testRun, comparatorSteps, extendedUrl));
    }
    return test;
  }

  private Url urlFromExtendedUrl(TestSuiteRun testSuiteRun, TestRun testRun,
      Map<String, List<ComparatorStep>> comparatorSteps, ExtendedUrl extendedUrl) {
    final Url url = new Url(extendedUrl.getName(), extendedUrl.getUrl(), testSuiteRun.getDomain());

    int stepIndex = 0;
    for (CollectorStep collectorStep : testRun.getCollectorSteps()) {
      url.addStep(stepFromCollectorStep(comparatorSteps, collectorStep, stepIndex++));
    }
    return url;
  }

  private Step stepFromCollectorStep(Map<String, List<ComparatorStep>> comparatorSteps,
      CollectorStep collectorStep, int stepIndex) {
    final Step step = Step.newBuilder(collectorStep.getModule(), stepIndex)
        .withName(collectorStep.getName())
        .build();
    step.addParameters(collectorStep.getParameters());

    final List<ComparatorStep> collectorComparators = comparatorSteps.get(step.getType());
    if (collectorComparators != null) {
      for (ComparatorStep comparatorStep : collectorComparators) {
        if (comparatorMatchesCollector(collectorStep, comparatorStep)) {
          step.addComparator(comparatorFromComparatorStep(comparatorStep));
        }
      }
    }
    return step;
  }

  private Comparator comparatorFromComparatorStep(ComparatorStep comparatorStep) {
    Comparator comparator = new Comparator(comparatorStep.getType());
    comparator.addParameters(comparatorStep.getParameters());
    comparator.addFilters(extractOperations(comparatorStep.getDataModifierSteps()));
    return comparator;
  }

  private Suite suiteFromTestRun(TestSuiteRun testSuiteRun) {
    String correlationId = testSuiteRun.getCorrelationId();
    String company = testSuiteRun.getCompany();
    String project = testSuiteRun.getProject();
    String name = testSuiteRun.getName();
    String patternCorrelationId = testSuiteRun.getPatternCorrelationId();

    return new Suite(correlationId, company, project, name, patternCorrelationId);
  }

  private boolean comparatorMatchesCollector(CollectorStep collectorStep,
      ComparatorStep comparatorStep) {
    return StringUtils.isEmpty(comparatorStep.getCollectorName()) || StringUtils
        .equals(comparatorStep.getCollectorName(), collectorStep.getName());
  }

  private List<Operation> extractOperations(List<? extends ParametrizedStep> steps) {
    return FluentIterable.from(steps).transform(new Function<ParametrizedStep, Operation>() {
      @Override
      public Operation apply(ParametrizedStep step) {
        final Operation operation = new Operation(step.getName());
        operation.addParameters(step.getParameters());
        return operation;
      }
    }).toList();
  }

}
