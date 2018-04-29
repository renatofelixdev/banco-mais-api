import play.http.HttpErrorHandler;
import play.libs.Json;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Results;
import util.HeaderKey;
import util.Utils;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

@Singleton
public class GlobalExceptionHandler implements HttpErrorHandler {
    @Inject
    private Utils utils;

    @Override
    public CompletionStage<Result> onClientError(Http.RequestHeader request, int statusCode, String message) {
        if(statusCode == Http.Status.INTERNAL_SERVER_ERROR)
            return CompletableFuture.completedFuture(
                    Results.internalServerError(Json.toJson(utils.notificationError()))
            );
        return null;
    }

    @Override
    public CompletionStage<Result> onServerError(Http.RequestHeader request, Throwable exception) {
        exception.printStackTrace();
        return CompletableFuture.completedFuture(
                Results.internalServerError(Json.toJson(utils.notificationError()))
                        .withHeader(HeaderKey.ACCESS_CONTROL_ALLOW_ORIGIN, HeaderKey.ORIGINS)
        );
    }
}
