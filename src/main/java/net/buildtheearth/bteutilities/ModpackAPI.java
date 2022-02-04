/*
 *  MIT License
 *
 *  Copyright (c) 2021-2022 BuildTheEarth.net
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation
 *  files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy,
 *  modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software
 *  is furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 *  OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS
 *  BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */

package net.buildtheearth.bteutilities;

import com.google.gson.JsonObject;
import lombok.Getter;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

@SuppressWarnings({ "CatchMayIgnoreException", "MismatchedReadAndWriteOfArray" })
public class ModpackAPI implements Runnable {

    private static final String IMAGE_API = "https://buildtheearth.net/api/modpack/images";
    @Getter
    private static final ModpackPicture[] modpackPictures = new ModpackPicture[5];
    @Getter
    private static ModpackPicture logo;

    public static void load() {
        // Load logo
        File logoFile = new File(BTEUtilities.getInstance().dynamicImageFolder, "logo.png");
        if (logoFile.exists()) {
            logo = ModpackPicture.of(logoFile, null);
        } else {
            logo = ModpackPicture.of("bteutilities:gui/btelogo.png", null);
        }

        // Load backgrounds
        JsonObject def = new JsonObject();
        JsonObject dyn = new JsonObject();
        try {
            def = BTEUtilities.parser.parse(new FileReader(new File(BTEUtilities.getInstance().defaultImageFolder, "default.json"))).getAsJsonObject();
        } catch (FileNotFoundException e) {
        }
        try {
            dyn = BTEUtilities.parser.parse(new FileReader(new File(BTEUtilities.getInstance().dynamicImageFolder, "dynamic.json"))).getAsJsonObject();
        } catch (FileNotFoundException e) {
        }
        for (int i = 0; i < 5; i++) {
            boolean dynamic = new File(BTEUtilities.getInstance().dynamicImageFolder, "background" + (i + 1) + ".png").exists();
            File image = new File(dynamic ? BTEUtilities.getInstance().dynamicImageFolder : BTEUtilities.getInstance().defaultImageFolder, "background" + (i + 1) + ".png");
            JsonObject data = dynamic ? dyn : def;
            String credit = "No Credit Provided";
            if (data.has(String.valueOf(i + 1))) {
                credit = data.get(String.valueOf(i + 1)).getAsJsonObject().get("credit").getAsString();
            }
            modpackPictures[i] = ModpackPicture.of(image, credit);
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Override
    public void run() {
        JsonObject api = getImageAPI();
        if (api == null) {
            return;
        }

        File dynamic = new File(BTEUtilities.getInstance().dynamicImageFolder, "dynamic.json");
        boolean exists = dynamic.exists();
        if (!exists) {
            try {
                dynamic.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        String[] images = { "background1", "background2", "background3", "background4", "background5", "logo" };

        if (!exists) {
            for (String s : images) {
                File image = new File(BTEUtilities.getInstance().dynamicImageFolder, s + ".png");
                if (image.exists()) {
                    image.delete();
                }
            }
        }

        JsonObject dynamicData = new JsonObject();
        if (exists) {
            try {
                dynamicData = BTEUtilities.parser.parse(new FileReader(dynamic)).getAsJsonObject();
            } catch (IOException ignored) {
            }
        }

        for (String s : images) {
            File imageFile = new File(BTEUtilities.getInstance().dynamicImageFolder, s + ".png");

            String query = s.replace("background", "");

            JsonObject result = api.getAsJsonObject(query);
            if (result == null) {
                if (imageFile.exists()) {
                    imageFile.delete();
                }
                if (exists) {
                    dynamicData.remove(query);
                }
                continue;
            }

            String url = result.get("url").getAsString();
            String credit = result.get("credit").getAsString();
            JsonObject localQuery = dynamicData.getAsJsonObject(query);

            if (localQuery != null && localQuery.get("url").getAsString().equalsIgnoreCase(url)) {
                continue;
            }

            boolean saveDownload = true;
            BufferedImage image;
            try {
                URL imageURL = new URL(url);
                image = ImageIO.read(imageURL);
            } catch (IOException e) {
                BTEUtilities.getLogger().error("Failed to download image: " + query);
                continue;
            }

            if (image == null) {
                saveDownload = false;
            }

            if (saveDownload) {
                if (imageFile.exists()) {
                    imageFile.delete();
                }
                dynamicData.remove(query);

                try {
                    ImageIO.write(image, "png", imageFile);
                } catch (IOException e) {
                    BTEUtilities.getLogger().error("Failed to save image: " + query);
                    continue;
                }

                JsonObject storableImage = new JsonObject();
                storableImage.addProperty("url", url);
                storableImage.addProperty("credit", credit);
                dynamicData.add(query, storableImage);
            }
        }

        try {
            FileWriter dynamicWriter = new FileWriter(dynamic);
            dynamicWriter.write(BTEUtilities.gson.toJson(dynamicData));
            dynamicWriter.flush();
            dynamicWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        load();
    }

    private JsonObject getImageAPI() {
        try {
            URL url = new URL(IMAGE_API);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("Content-Type", "application/json");
            con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");

            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuilder content = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();
            return BTEUtilities.parser.parse(content.toString()).getAsJsonObject();
        } catch (IOException e) {
            e.printStackTrace();
            BTEUtilities.getLogger().warn("Could not contact the BuildTheEarth Modpack API! Will try again later.");
            return null;
        }
    }
}
