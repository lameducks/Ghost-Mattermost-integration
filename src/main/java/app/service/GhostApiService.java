package app.service;

import app.config.ApplicationConfig;
import app.entity.api.ghost.content.Authors;
import app.entity.api.ghost.content.Tags;
import app.util.JsonMapper;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.function.Consumer;

@Service
public class GhostApiService {

    private static final Logger logger = LoggerFactory.getLogger(GhostApiService.class);

    @Autowired
    ApplicationConfig appConfig;

    @Autowired
    OkHttpClient httpClient;

    @Autowired
    JsonMapper jsonMapper;

    public Tags getTags() {
        return get("/tags/", "&limit=all&include=count.posts&order=count.posts%20desc", Tags.class);
    }

    public Authors getAuthors() {
        return get("/authors/", "&limit=all&include=count.posts&order=count.posts%20desc", Authors.class);
    }

    <T> T get(final String path, final String additionalQueryString, final Class<T> mappedClass) {
        final Request request = buildRequest(path, additionalQueryString, null);
        return call(request, mappedClass);
    }

    Request buildRequest(final String path, final String additionalQueryString,
            final Consumer<Request.Builder> additionalBuilder) {
        final Request.Builder builder = new Request.Builder();
        builder.url(String.format("%s/ghost/api/v3/content%s?key=%s%s", appConfig.getGhost().getApi().getUrl(), path,
                appConfig.getGhost().getApi().getKey(), additionalQueryString));
        if (additionalBuilder != null) {
            additionalBuilder.accept(builder);
        }
        return builder.build();
    }

    <T> T call(final Request request, final Class<T> mappedClass) {
        try (final var response = httpClient.newCall(request).execute()) {
            if (logger.isDebugEnabled()) {
                logger.debug("{}: {} {}", response.request(), response.code(), response.message());
            }
            final T mappedData = jsonMapper.readValue(response.body().byteStream(), mappedClass);
            return mappedData;
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }
    }

}