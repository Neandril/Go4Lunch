package com.neandril.go4lunch;

import android.content.Context;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class AssertReaderUtil {

    @NotNull
    public final String asset(@NotNull Context context, @NotNull String assetPath) {
        try {
            InputStream inputStream = context.getAssets().open("json/" + assetPath);
            return this.inputStreamToString(inputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String inputStreamToString(InputStream inputStream) {
        StringBuilder builder = new StringBuilder();
        InputStreamReader reader;
        reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
        BufferedReader buffer = new BufferedReader(reader);

        buffer.lines().forEach(builder::append);

        return builder.toString();
    }
}
