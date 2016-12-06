/*
 * Copyright (c) 2014 Spotify AB.
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.spotify.docker;

import org.apache.maven.plugin.logging.Log;

import com.spotify.docker.client.ProgressHandler;
import com.spotify.docker.client.exceptions.DockerException;
import com.spotify.docker.client.messages.ProgressMessage;

public class MavenLogProgressHandler implements ProgressHandler {
  private final Log log;

  public MavenLogProgressHandler(Log log) {
    this.log = log;
  }

  @Override
  public void progress(ProgressMessage message) throws DockerException {
    // Copied from AnsiProgressHandler, and adjusted to drop progress events.
    // XXX: Could we delegate to AnsiProgressHandler or a derived class instead?
    if (message.error() != null) {
      throw new DockerException(message.error());
    }

    if (message.progressDetail() != null) {
      return;
    }

    String value = message.stream();
    if (value != null) {
      // trim trailing new lines which are present in streams
      value = value.replaceFirst("\n$", "");
    } else {
      value = message.status();
    }
    // if value is null it's an unknown message type so just print the whole
    // thing out
    if (value == null) {
      value = message.toString();
    }

    log.info(value);
  }
}
