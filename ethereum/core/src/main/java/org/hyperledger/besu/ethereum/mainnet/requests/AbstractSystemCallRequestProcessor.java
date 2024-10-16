/*
 * Copyright contributors to Hyperledger Besu.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package org.hyperledger.besu.ethereum.mainnet.requests;

import org.hyperledger.besu.datatypes.Address;
import org.hyperledger.besu.ethereum.core.Request;
import org.hyperledger.besu.ethereum.mainnet.SystemCallProcessor;

import java.util.Optional;

import org.apache.tuweni.bytes.Bytes;

/**
 * Abstract base class for processing system call requests.
 *
 * @param <T> The type of request to be processed.
 */
public abstract class AbstractSystemCallRequestProcessor<T extends Request>
    implements RequestProcessor {

  /**
   * Processes a system call and converts the result into requests of type T.
   *
   * @param context The request context being processed.
   * @return An {@link Optional} containing a list of {@link T} objects if any are found
   */
  @Override
  public Request process(final ProcessRequestContext context) {

    SystemCallProcessor systemCallProcessor =
        new SystemCallProcessor(context.protocolSpec().getTransactionProcessor());

    Bytes systemCallOutput =
        systemCallProcessor.process(
            getCallAddress(),
            context.mutableWorldState().updater(),
            context.blockHeader(),
            context.operationTracer(),
            context.blockHashLookup());

    return parseRequest(systemCallOutput == null ? Bytes.EMPTY : systemCallOutput);
  }

  /**
   * Parses a single request from the provided bytes.
   *
   * @param requestBytes The bytes representing a single request.
   * @return A parsed {@link T} object.
   */
  protected abstract T parseRequest(final Bytes requestBytes);

  /**
   * Gets the call address for the specific request type.
   *
   * @return The call address.
   */
  protected abstract Address getCallAddress();
}
