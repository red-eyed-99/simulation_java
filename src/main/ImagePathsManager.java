package main;

import main.entities.Entity;
import main.entities.creatures.Creature;
import main.entities.creatures.herbivores.*;
import main.entities.creatures.predators.*;
import main.entities.landscape.LandscapeEntity;
import main.entities.landscape.food_resources.Grass;
import main.entities.landscape.food_resources.Meat;
import main.entities.landscape.static_objects.Rock;
import main.entities.landscape.static_objects.Tree;
import main.entities.landscape.surface.Ground;
import main.entities.landscape.surface.Water;

import java.util.HashMap;
import java.util.Map;

public class ImagePathsManager {
    private static final Map<Class<? extends LandscapeEntity>, String> LANDSCAPE_PATHS_TO_IMAGE = new HashMap<>();

    static {
        setEntityImagePaths();
    }

    private static final Map<Class<? extends Creature>, HashMap<CreatureViewDirection, String>> CREATURE_PATHS_TO_IMAGE = new HashMap<>();

    static {
        setCreatureImagePaths();
    }

    private static void setEntityImagePaths() {
        LANDSCAPE_PATHS_TO_IMAGE.put(Ground.class, "/main/ui/images/landscape/ground.png");
        LANDSCAPE_PATHS_TO_IMAGE.put(Water.class, "/main/ui/images/landscape/water.png");
        LANDSCAPE_PATHS_TO_IMAGE.put(Tree.class, "/main/ui/images/landscape/tree.png");
        LANDSCAPE_PATHS_TO_IMAGE.put(Rock.class, "/main/ui/images/landscape/rock.png");
        LANDSCAPE_PATHS_TO_IMAGE.put(Grass.class, "/main/ui/images/landscape/grass.png");
        LANDSCAPE_PATHS_TO_IMAGE.put(Meat.class, "/main/ui/images/landscape/meat.png");
    }

    private static void setCreatureImagePaths() {
        // predators
        CREATURE_PATHS_TO_IMAGE.put(Lion.class, getCreatureImagePaths(Lion.class));
        CREATURE_PATHS_TO_IMAGE.put(Crocodile.class, getCreatureImagePaths(Crocodile.class));
        CREATURE_PATHS_TO_IMAGE.put(Panther.class, getCreatureImagePaths(Panther.class));
        CREATURE_PATHS_TO_IMAGE.put(Tiger.class, getCreatureImagePaths(Tiger.class));

        //herbivores
        CREATURE_PATHS_TO_IMAGE.put(Elephant.class, getCreatureImagePaths(Elephant.class));
        CREATURE_PATHS_TO_IMAGE.put(Girafee.class, getCreatureImagePaths(Girafee.class));
        CREATURE_PATHS_TO_IMAGE.put(Ostrich.class, getCreatureImagePaths(Ostrich.class));
        CREATURE_PATHS_TO_IMAGE.put(Rhino.class, getCreatureImagePaths(Rhino.class));
    }

    private static HashMap<CreatureViewDirection, String> getCreatureImagePaths(Class<? extends Creature> creatureType) {
        HashMap<CreatureViewDirection, String> creatureImagePaths = new HashMap<>();

        String[] pathToImages = new String[2];

        // predators paths

        if (creatureType == Lion.class) {
            pathToImages[0] = "/main/ui/images/creatures/predators/lion/lion_left.png";
            pathToImages[1] = "/main/ui/images/creatures/predators/lion/lion_right.png";
        }
        if (creatureType == Crocodile.class) {
            pathToImages[0] = "/main/ui/images/creatures/predators/crocodile/crocodile_left.png";
            pathToImages[1] = "/main/ui/images/creatures/predators/crocodile/crocodile_right.png";
        }
        if (creatureType == Panther.class) {
            pathToImages[0] = "/main/ui/images/creatures/predators/panther/panther_left.png";
            pathToImages[1] = "/main/ui/images/creatures/predators/panther/panther_right.png";
        }
        if (creatureType == Tiger.class) {
            pathToImages[0] = "/main/ui/images/creatures/predators/tiger/tiger_left.png";
            pathToImages[1] = "/main/ui/images/creatures/predators/tiger/tiger_right.png";
        }

        // herbivores paths

        if (creatureType == Elephant.class) {
            pathToImages[0] = "/main/ui/images/creatures/herbivores/elephant/elephant_left.png";
            pathToImages[1] = "/main/ui/images/creatures/herbivores/elephant/elephant_right.png";
        }
        if (creatureType == Girafee.class) {
            pathToImages[0] = "/main/ui/images/creatures/herbivores/girafee/girafee_left.png";
            pathToImages[1] = "/main/ui/images/creatures/herbivores/girafee/girafee_right.png";
        }
        if (creatureType == Ostrich.class) {
            pathToImages[0] = "/main/ui/images/creatures/herbivores/ostrich/ostrich_left.png";
            pathToImages[1] = "/main/ui/images/creatures/herbivores/ostrich/ostrich_right.png";
        }
        if (creatureType == Rhino.class) {
            pathToImages[0] = "/main/ui/images/creatures/herbivores/rhino/rhino_left.png";
            pathToImages[1] = "/main/ui/images/creatures/herbivores/rhino/rhino_right.png";
        }

        creatureImagePaths.put(CreatureViewDirection.LEFT, pathToImages[0]);
        creatureImagePaths.put(CreatureViewDirection.RIGHT, pathToImages[1]);

        return creatureImagePaths;
    }

    public static String getEntityImagePath(Entity entity) {
        if (entity instanceof Creature creature) {
            Map<CreatureViewDirection, String> imagePaths = CREATURE_PATHS_TO_IMAGE.get(entity.getClass());
            return imagePaths.get(creature.viewDirection);
        }

        return LANDSCAPE_PATHS_TO_IMAGE.get(entity.getClass());
    }
}
