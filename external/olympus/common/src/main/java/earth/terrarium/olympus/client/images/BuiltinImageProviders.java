package earth.terrarium.olympus.client.images;

import com.google.common.hash.Hashing;
import com.mojang.blaze3d.platform.NativeImage;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.time.Duration;

public final class BuiltinImageProviders {

    private static final HttpClient CLIENT = HttpClient.newBuilder().followRedirects(HttpClient.Redirect.NORMAL).build();
    public static final ImageProvider<URI> URL = ImageProviders.register(
            "url",
            url -> {
                var request = java.net.http.HttpRequest.newBuilder()
                        .uri(url)
                        .GET()
                        .build();

                return CLIENT.sendAsync(request, HttpResponse.BodyHandlers.ofInputStream()).thenApply(response -> {
                    if (response.statusCode() / 100 != 2) {
                        throw new RuntimeException("Failed to fetch image from URL: " + url + " with status code: " + response.statusCode());
                    }
                    try {
                        return NativeImage.read(response.body());
                    } catch (Exception e) {
                        throw new RuntimeException("Failed to read image from URL: " + url, e);
                    }
                });
            },
            url -> Hashing.sha256().hashUnencodedChars(url.toString()),
            Duration.ofMinutes(5)
    );
}
