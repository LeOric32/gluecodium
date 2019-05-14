/*
 * Copyright (C) 2016-2019 HERE Europe B.V.
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
 *
 * SPDX-License-Identifier: Apache-2.0
 * License-Filename: LICENSE
 */

package com.here.genium.loader;

import com.here.genium.common.ModelBuilderContext;
import com.here.genium.common.ModelBuilderContextStack;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.eclipse.emf.ecore.EObject;
import org.franca.core.franca.*;

public abstract class AbstractModelBuilder<E> implements ModelBuilder {

  private final ModelBuilderContextStack<E> contextStack;
  private ModelBuilderContext<E> resultContext;

  protected AbstractModelBuilder(final ModelBuilderContextStack<E> contextStack) {
    this.contextStack = contextStack;
  }

  @Override
  public void startBuilding(EObject object) {
    openContext();
  }

  @Override
  public void startBuilding(FInterface francaInterface) {
    openContext();
  }

  @Override
  public void startBuilding(FTypeCollection francaTypeCollection) {
    openContext();
  }

  @Override
  public void startBuildingInputArgument(FArgument francaArgument) {
    openContext();
  }

  @Override
  public void startBuildingOutputArgument(FArgument francaArgument) {
    openContext();
  }

  /**
   * Get final results of the model builder execution. Intended to be called by clients of the model
   * builder but not within the model builder itself.
   *
   * @return A list of results
   */
  public List<E> getFinalResults() {
    return resultContext != null ? resultContext.getCurrentResults() : Collections.emptyList();
  }

  /**
   * Get the first item of the given type from the list of final results of the model builder
   * execution. Intended to be called by clients of the model builder but not within the model
   * builder itself.
   *
   * @param clazz Class object representing the type of the result to get
   * @param <T> Type of the result to get
   * @return A result item
   */
  public <T extends E> T getFinalResult(final Class<T> clazz) {
    return resultContext != null
        ? resultContext
            .getCurrentResults()
            .stream()
            .filter(clazz::isInstance)
            .map(clazz::cast)
            .findFirst()
            .orElse(null)
        : null;
  }

  /**
   * Get the results of the previous ("child") step of model builder execution.
   *
   * @param clazz Class object representing the type of the result to get
   * @param <T> Type of the result to get
   * @return A list of results
   */
  protected <T extends E> T getPreviousResult(final Class<T> clazz) {
    return getCurrentContext()
        .getPreviousResults()
        .stream()
        .filter(clazz::isInstance)
        .map(clazz::cast)
        .findFirst()
        .orElse(null);
  }

  /**
   * Get the first item of the given type from the list of results of the previous ("child") step of
   * model builder execution.
   *
   * @param clazz Class object representing the type of the result to get
   * @param <T> Type of the result to get
   * @return A result item
   */
  protected <T extends E> List<T> getPreviousResults(final Class<T> clazz) {
    return getCurrentContext()
        .getPreviousResults()
        .stream()
        .filter(clazz::isInstance)
        .map(clazz::cast)
        .collect(Collectors.toList());
  }

  protected final void openContext() {
    contextStack.openContext();
  }

  protected final void closeContext() {
    resultContext = contextStack.getCurrentContext();
    ModelBuilderContext<E> parentContext = contextStack.getParentContext();
    if (parentContext != null) {
      parentContext.getPreviousResults().addAll(resultContext.getCurrentResults());
    }

    contextStack.closeContext();
  }

  protected final ModelBuilderContext<E> getCurrentContext() {
    return contextStack.getCurrentContext();
  }

  protected final ModelBuilderContext<E> getParentContext() {
    return contextStack.getParentContext();
  }

  protected final void storeResult(final E element) {
    contextStack.getCurrentContext().getCurrentResults().add(element);
  }
}