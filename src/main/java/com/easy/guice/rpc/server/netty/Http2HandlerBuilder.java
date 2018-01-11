/*
 * Copyright 2015 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package com.easy.guice.rpc.server.netty;

import io.netty.handler.codec.http2.*;

import static io.netty.handler.logging.LogLevel.INFO;

public final class Http2HandlerBuilder
        extends AbstractHttp2ConnectionHandlerBuilder<Http2Handler, Http2HandlerBuilder> {

    private static final Http2FrameLogger logger = new Http2FrameLogger(INFO, Http2Handler.class);

    public Http2HandlerBuilder() {
        frameLogger(logger);
    }

    @Override
    public Http2Handler build() {
        return super.build();
    }

    @Override
    protected Http2Handler build(Http2ConnectionDecoder decoder, Http2ConnectionEncoder encoder,
                                 Http2Settings initialSettings) {
        Http2Handler handler = new Http2Handler(decoder, encoder, initialSettings);
        frameListener(handler);
        return handler;
    }
}
