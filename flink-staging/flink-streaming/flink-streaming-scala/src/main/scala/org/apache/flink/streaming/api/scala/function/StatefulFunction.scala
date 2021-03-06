/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.flink.streaming.api.scala.function

import org.apache.flink.api.common.functions.RichFunction
import org.apache.flink.configuration.Configuration
import org.apache.flink.api.common.state.OperatorState

/**
 * Trait implementing the functionality necessary to apply stateful functions in 
 * RichFunctions without exposing the OperatorStates to the user. The user should
 * call the applyWithState method in his own RichFunction implementation.
 */
trait StatefulFunction[I, O, S] extends RichFunction {

  var state: OperatorState[Option[S]] = _
  val partitioned: Boolean

  def applyWithState(in: I, fun: (I, Option[S]) => (O, Option[S])): O = {
    val (o, s) = fun(in, state.value)
    state.update(s)
    o
  }

  override def open(c: Configuration) = {
    state = getRuntimeContext().getOperatorState("state", None, partitioned)
  }
}
