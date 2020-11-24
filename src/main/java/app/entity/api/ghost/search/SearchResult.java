package app.entity.api.ghost.search;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

import java.util.Date;

@Value.Immutable
@JsonSerialize(as = ImmutableSearchResult.class)
public interface SearchResult {

    @JsonProperty("title")
    String title();

    @JsonProperty("slug")
    String slug();

    @JsonProperty("summary")
    String summary();

    @JsonProperty("updatedAt")
    Date updatedAt();

}
