package lab;

import javafx.geometry.Point2D;
import lab.entity.Ghost;
import lab.entity.WorldEntity;
import lab.enums.GhostTexture;
import lab.enviroment.Game;
import org.json.*;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

public class GhostLoader {

    private final String source;
    private JSONArray ghostsArr;
    private final Game game;

    public GhostLoader(Game game, String source) {
        this.game = game;
        this.source = source;
    }

    public void load() {
        StringBuilder str = new StringBuilder("");

        try (BufferedReader br = new BufferedReader(new FileReader(source))) {
            String line;
            while ((line = br.readLine()) != null) {
                str.append(line);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        ghostsArr = new JSONArray(str.toString());
    }

    public GhostTexture convertToEnum(String texture) {
        GhostTexture tmp = null;
        switch (texture) {
            case "BLINKY" -> tmp = GhostTexture.BLINKY;
            case "INKY" -> tmp = GhostTexture.INKY;
            case "PINKY" -> tmp = GhostTexture.PINKY;
            case "CLYDE" -> tmp = GhostTexture.CLYDE;
        }

        return tmp;
    }

    public ArrayList<WorldEntity> createGhosts() {
        ArrayList<WorldEntity> tmp = new ArrayList<WorldEntity>();

        for (int i = 0; i < ghostsArr.length(); i++) {
            JSONObject jsonObject = ghostsArr.getJSONObject(i);
            double x, y;
            x = jsonObject.getDouble("x");
            y = jsonObject.getDouble("y");
            Point2D position = new Point2D(x, y);

            String texture = jsonObject.getString("texture");
            GhostTexture gTexture = convertToEnum(texture);

            Ghost tmpGhost = new Ghost(game, position, gTexture);

            tmp.add(tmpGhost);
        }

        return tmp;
    }
}
