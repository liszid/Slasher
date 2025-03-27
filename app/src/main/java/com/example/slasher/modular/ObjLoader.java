package com.example.slasher.modular;

import android.content.Context;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

public class ObjLoader {

    public static FloatBuffer loadObj(Context context, int resourceId) {
        List<Float> vertexList = new ArrayList<>();
        try {
            InputStream inputStream = context.getResources().openRawResource(resourceId);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] tokens = line.split(" ");
                if (tokens[0].equals("v")) {
                    for (int i = 1; i < tokens.length; i++) {
                        vertexList.add(Float.parseFloat(tokens[i]));
                    }
                }
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        FloatBuffer vertexBuffer = FloatBuffer.allocate(vertexList.size());
        for (Float vertex : vertexList) {
            vertexBuffer.put(vertex);
        }
        vertexBuffer.position(0);
        return vertexBuffer;
    }
}
