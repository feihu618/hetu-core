/*
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
package io.prestosql.protocol;

import io.airlift.http.client.FullJsonResponseHandler;
import io.airlift.http.client.Request;
import io.airlift.http.client.Response;
import io.airlift.http.client.ResponseHandler;
import io.airlift.json.JsonCodec;

import static io.airlift.http.client.FullJsonResponseHandler.createFullJsonResponseHandler;
import static io.prestosql.protocol.JsonResponseWrapper.wrapJsonResponse;
import static java.util.Objects.requireNonNull;

/**
 * This response handler helps clients convert responses of type `JsonResponse`
 * to `BaseResponse`, which simplifies the client code for handling both
 * JSON and SMILE responses.
 */
public class AdaptingJsonResponseHandler<T>
        implements ResponseHandler<BaseResponse<T>, RuntimeException>
{
    private final FullJsonResponseHandler<T> jsonResponseHandler;

    private AdaptingJsonResponseHandler(FullJsonResponseHandler<T> jsonResponseHandler)
    {
        this.jsonResponseHandler = requireNonNull(jsonResponseHandler, "jsonResponseHandler is null");
    }

    public static <T> AdaptingJsonResponseHandler<T> createAdaptingJsonResponseHandler(JsonCodec<T> jsonCodec)
    {
        return new AdaptingJsonResponseHandler<>(createFullJsonResponseHandler(jsonCodec));
    }

    @Override
    public BaseResponse<T> handleException(Request request, Exception exception)
            throws RuntimeException
    {
        return wrapJsonResponse(jsonResponseHandler.handleException(request, exception));
    }

    @Override
    public BaseResponse<T> handle(Request request, Response response)
            throws RuntimeException
    {
        return wrapJsonResponse(jsonResponseHandler.handle(request, response));
    }
}
