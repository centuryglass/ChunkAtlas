INSERT INTO tile_sizes VALUES(DEFAULT, 512);
INSERT INTO tile_sizes VALUES(DEFAULT, 128);
INSERT INTO tile_sizes VALUES(DEFAULT, 64);
INSERT INTO tile_sizes VALUES(DEFAULT, 32);
INSERT INTO dimensions VALUES(DEFAULT, 'Overworld');
INSERT INTO dimensions VALUES(DEFAULT, 'Nether');
INSERT INTO dimensions VALUES(DEFAULT, 'The End');
INSERT INTO map_types VALUES(DEFAULT, 'Biome');
INSERT INTO map_types VALUES(DEFAULT, 'Structure');
INSERT INTO map_types VALUES(DEFAULT, 'Total Activity');
INSERT INTO map_types VALUES(DEFAULT, 'Errors');
INSERT INTO biomes VALUES(DEFAULT, 'Snowy Taiga', '30', 'baa3cc');
INSERT INTO textures VALUES
(
    DEFAULT
    ,(SELECT id FROM biomes WHERE name='Snowy Taiga')
    ,'resources/images/biomeTile/SNOWY_TAIGA.png'
);

INSERT INTO biomes VALUES(DEFAULT, 'Shattered Savanna', '163', '899a12');
INSERT INTO textures VALUES
(
    DEFAULT
    ,(SELECT id FROM biomes WHERE name='Shattered Savanna')
    ,'resources/images/biomeTile/SHATTERED_SAVANNA.png'
);

INSERT INTO biomes VALUES(DEFAULT, 'Snowy Mountains', '13', 'a3bdcc');
INSERT INTO textures VALUES
(
    DEFAULT
    ,(SELECT id FROM biomes WHERE name='Snowy Mountains')
    ,'resources/images/biomeTile/SNOWY_MOUNTAINS.png'
);

INSERT INTO biomes VALUES(DEFAULT, 'End Highlands', '42', 'ff0066');
INSERT INTO textures VALUES
(
    DEFAULT
    ,(SELECT id FROM biomes WHERE name='End Highlands')
    ,'resources/images/biomeTile/END_HIGHLANDS.png'
);

INSERT INTO biomes VALUES(DEFAULT, 'Desert Hills', '17', 'd3d1a1');
INSERT INTO textures VALUES
(
    DEFAULT
    ,(SELECT id FROM biomes WHERE name='Desert Hills')
    ,'resources/images/biomeTile/DESERT_HILLS.png'
);

INSERT INTO biomes VALUES(DEFAULT, 'Giant Tree Taiga Hills', '33', '495a3c');
INSERT INTO textures VALUES
(
    DEFAULT
    ,(SELECT id FROM biomes WHERE name='Giant Tree Taiga Hills')
    ,'resources/images/biomeTile/GIANT_TREE_TAIGA_HILLS.png'
);

INSERT INTO biomes VALUES(DEFAULT, 'Wooded Badlands Plateau', '38', '993d3d');
INSERT INTO textures VALUES
(
    DEFAULT
    ,(SELECT id FROM biomes WHERE name='Wooded Badlands Plateau')
    ,'resources/images/biomeTile/WOODED_BADLANDS_PLATEAU.png'
);

INSERT INTO biomes VALUES(DEFAULT, 'Cold Ocean', '46', '5c6099');
INSERT INTO textures VALUES
(
    DEFAULT
    ,(SELECT id FROM biomes WHERE name='Cold Ocean')
    ,'resources/images/biomeTile/COLD_OCEAN.png'
);

INSERT INTO biomes VALUES(DEFAULT, 'Sunflower Plains', '129', '29ac0c');
INSERT INTO textures VALUES
(
    DEFAULT
    ,(SELECT id FROM biomes WHERE name='Sunflower Plains')
    ,'resources/images/biomeTile/SUNFLOWER_PLAINS.png'
);

INSERT INTO biomes VALUES(DEFAULT, 'Giant Spruce Taiga Hills', '161', '733818');
INSERT INTO textures VALUES
(
    DEFAULT
    ,(SELECT id FROM biomes WHERE name='Giant Spruce Taiga Hills')
    ,'resources/images/biomeTile/GIANT_SPRUCE_TAIGA_HILLS.png'
);

INSERT INTO biomes VALUES(DEFAULT, 'Savanna', '35', '899a12');
INSERT INTO textures VALUES
(
    DEFAULT
    ,(SELECT id FROM biomes WHERE name='Savanna')
    ,'resources/images/biomeTile/SAVANNA.png'
);

INSERT INTO biomes VALUES(DEFAULT, 'Deep Ocean', '24', '141a66');
INSERT INTO textures VALUES
(
    DEFAULT
    ,(SELECT id FROM biomes WHERE name='Deep Ocean')
    ,'resources/images/biomeTile/DEEP_OCEAN.png'
);

INSERT INTO biomes VALUES(DEFAULT, 'Taiga Hills', '19', '495a3c');
INSERT INTO textures VALUES
(
    DEFAULT
    ,(SELECT id FROM biomes WHERE name='Taiga Hills')
    ,'resources/images/biomeTile/TAIGA_HILLS.png'
);

INSERT INTO biomes VALUES(DEFAULT, 'Nether', '8', 'ff0000');
INSERT INTO textures VALUES
(
    DEFAULT
    ,(SELECT id FROM biomes WHERE name='Nether')
    ,'resources/images/biomeTile/NETHER.png'
);

INSERT INTO biomes VALUES(DEFAULT, 'Gravelly Mountains+', '162', 'ccc4a3');
INSERT INTO textures VALUES
(
    DEFAULT
    ,(SELECT id FROM biomes WHERE name='Gravelly Mountains+')
    ,'resources/images/biomeTile/MODIFIED_GRAVELLY_MOUNTAINS.png'
);

INSERT INTO biomes VALUES(DEFAULT, 'Dark Forest', '29', '1f331f');
INSERT INTO textures VALUES
(
    DEFAULT
    ,(SELECT id FROM biomes WHERE name='Dark Forest')
    ,'resources/images/biomeTile/DARK_FOREST.png'
);

INSERT INTO biomes VALUES(DEFAULT, 'Small End Islands', '40', '660029');
INSERT INTO textures VALUES
(
    DEFAULT
    ,(SELECT id FROM biomes WHERE name='Small End Islands')
    ,'resources/images/biomeTile/SMALL_END_ISLANDS.png'
);

INSERT INTO biomes VALUES(DEFAULT, 'Giant Tree Taiga', '32', '495a3c');
INSERT INTO textures VALUES
(
    DEFAULT
    ,(SELECT id FROM biomes WHERE name='Giant Tree Taiga')
    ,'resources/images/biomeTile/GIANT_TREE_TAIGA.png'
);

INSERT INTO biomes VALUES(DEFAULT, 'Mountain Edge', '20', '6cb223');
INSERT INTO textures VALUES
(
    DEFAULT
    ,(SELECT id FROM biomes WHERE name='Mountain Edge')
    ,'resources/images/biomeTile/MOUNTAIN_EDGE.png'
);

INSERT INTO biomes VALUES(DEFAULT, 'Mountains', '3', '44352c');
INSERT INTO textures VALUES
(
    DEFAULT
    ,(SELECT id FROM biomes WHERE name='Mountains')
    ,'resources/images/biomeTile/MOUNTAINS.png'
);

INSERT INTO biomes VALUES(DEFAULT, 'Frozen Ocean', '10', '525f66');
INSERT INTO textures VALUES
(
    DEFAULT
    ,(SELECT id FROM biomes WHERE name='Frozen Ocean')
    ,'resources/images/biomeTile/FROZEN_OCEAN.png'
);

INSERT INTO biomes VALUES(DEFAULT, 'Flower Forest', '132', '3a7a09');
INSERT INTO textures VALUES
(
    DEFAULT
    ,(SELECT id FROM biomes WHERE name='Flower Forest')
    ,'resources/images/biomeTile/FLOWER_FOREST.png'
);

INSERT INTO biomes VALUES(DEFAULT, 'Warm Ocean', '44', '52ccc8');
INSERT INTO textures VALUES
(
    DEFAULT
    ,(SELECT id FROM biomes WHERE name='Warm Ocean')
    ,'resources/images/biomeTile/WARM_OCEAN.png'
);

INSERT INTO biomes VALUES(DEFAULT, 'Wooded Hills', '18', '448105');
INSERT INTO textures VALUES
(
    DEFAULT
    ,(SELECT id FROM biomes WHERE name='Wooded Hills')
    ,'resources/images/biomeTile/WOODED_HILLS.png'
);

INSERT INTO biomes VALUES(DEFAULT, 'Desert', '2', 'd3d1a1');
INSERT INTO textures VALUES
(
    DEFAULT
    ,(SELECT id FROM biomes WHERE name='Desert')
    ,'resources/images/biomeTile/DESERT.png'
);

INSERT INTO biomes VALUES(DEFAULT, 'Snowy Tundra', '12', '7a9998');
INSERT INTO textures VALUES
(
    DEFAULT
    ,(SELECT id FROM biomes WHERE name='Snowy Tundra')
    ,'resources/images/biomeTile/SNOWY_TUNDRA.png'
);

INSERT INTO biomes VALUES(DEFAULT, 'Badlands Plateau+', '167', 'ff9999');
INSERT INTO textures VALUES
(
    DEFAULT
    ,(SELECT id FROM biomes WHERE name='Badlands Plateau+')
    ,'resources/images/biomeTile/MODIFIED_BADLANDS_PLATEAU.png'
);

INSERT INTO biomes VALUES(DEFAULT, 'Wooded Mountains', '34', '44352c');
INSERT INTO textures VALUES
(
    DEFAULT
    ,(SELECT id FROM biomes WHERE name='Wooded Mountains')
    ,'resources/images/biomeTile/WOODED_MOUNTAINS.png'
);

INSERT INTO biomes VALUES(DEFAULT, 'Badlands Plateau', '39', 'cc7a7a');
INSERT INTO textures VALUES
(
    DEFAULT
    ,(SELECT id FROM biomes WHERE name='Badlands Plateau')
    ,'resources/images/biomeTile/BADLANDS_PLATEAU.png'
);

INSERT INTO biomes VALUES(DEFAULT, 'Taiga', '5', '495a3c');
INSERT INTO textures VALUES
(
    DEFAULT
    ,(SELECT id FROM biomes WHERE name='Taiga')
    ,'resources/images/biomeTile/TAIGA.png'
);

INSERT INTO biomes VALUES(DEFAULT, 'Birch Forest', '27', '448105');
INSERT INTO textures VALUES
(
    DEFAULT
    ,(SELECT id FROM biomes WHERE name='Birch Forest')
    ,'resources/images/biomeTile/BIRCH_FOREST.png'
);

INSERT INTO biomes VALUES(DEFAULT, 'Shattered Savanna Plateau', '164', '899a12');
INSERT INTO textures VALUES
(
    DEFAULT
    ,(SELECT id FROM biomes WHERE name='Shattered Savanna Plateau')
    ,'resources/images/biomeTile/SHATTERED_SAVANNA_PLATEAU.png'
);

INSERT INTO biomes VALUES(DEFAULT, 'Tall Birch Hills', '156', '448105');
INSERT INTO textures VALUES
(
    DEFAULT
    ,(SELECT id FROM biomes WHERE name='Tall Birch Hills')
    ,'resources/images/biomeTile/TALL_BIRCH_HILLS.png'
);

INSERT INTO biomes VALUES(DEFAULT, 'Bamboo Jungle', '168', '007311');
INSERT INTO textures VALUES
(
    DEFAULT
    ,(SELECT id FROM biomes WHERE name='Bamboo Jungle')
    ,'resources/images/biomeTile/BAMBOO_JUNGLE.png'
);

INSERT INTO biomes VALUES(DEFAULT, 'Jungle', '21', '007311');
INSERT INTO textures VALUES
(
    DEFAULT
    ,(SELECT id FROM biomes WHERE name='Jungle')
    ,'resources/images/biomeTile/JUNGLE.png'
);

INSERT INTO biomes VALUES(DEFAULT, 'Deep Lukewarm Ocean', '48', '144866');
INSERT INTO textures VALUES
(
    DEFAULT
    ,(SELECT id FROM biomes WHERE name='Deep Lukewarm Ocean')
    ,'resources/images/biomeTile/DEEP_LUKEWARM_OCEAN.png'
);

INSERT INTO biomes VALUES(DEFAULT, 'Snowy Beach', '26', 'cccfff');
INSERT INTO textures VALUES
(
    DEFAULT
    ,(SELECT id FROM biomes WHERE name='Snowy Beach')
    ,'resources/images/biomeTile/SNOWY_BEACH.png'
);

INSERT INTO biomes VALUES(DEFAULT, 'Ice Spikes', '140', 'ccfffd');
INSERT INTO textures VALUES
(
    DEFAULT
    ,(SELECT id FROM biomes WHERE name='Ice Spikes')
    ,'resources/images/biomeTile/ICE_SPIKES.png'
);

INSERT INTO biomes VALUES(DEFAULT, 'Savanna Plateau', '36', '899a12');
INSERT INTO textures VALUES
(
    DEFAULT
    ,(SELECT id FROM biomes WHERE name='Savanna Plateau')
    ,'resources/images/biomeTile/SAVANNA_PLATEAU.png'
);

INSERT INTO biomes VALUES(DEFAULT, 'Deep Frozen Ocean', '50', '292f33');
INSERT INTO textures VALUES
(
    DEFAULT
    ,(SELECT id FROM biomes WHERE name='Deep Frozen Ocean')
    ,'resources/images/biomeTile/DEEP_FROZEN_OCEAN.png'
);

INSERT INTO biomes VALUES(DEFAULT, 'Tall Birch Forest', '155', '448105');
INSERT INTO textures VALUES
(
    DEFAULT
    ,(SELECT id FROM biomes WHERE name='Tall Birch Forest')
    ,'resources/images/biomeTile/TALL_BIRCH_FOREST.png'
);

INSERT INTO biomes VALUES(DEFAULT, 'Stone Shore', '25', '6c6d6b');
INSERT INTO textures VALUES
(
    DEFAULT
    ,(SELECT id FROM biomes WHERE name='Stone Shore')
    ,'resources/images/biomeTile/STONE_SHORE.png'
);

INSERT INTO biomes VALUES(DEFAULT, 'Jungle Edge', '23', '007311');
INSERT INTO textures VALUES
(
    DEFAULT
    ,(SELECT id FROM biomes WHERE name='Jungle Edge')
    ,'resources/images/biomeTile/JUNGLE_EDGE.png'
);

INSERT INTO biomes VALUES(DEFAULT, 'Snowy Taiga Hills', '31', 'e8ccff');
INSERT INTO textures VALUES
(
    DEFAULT
    ,(SELECT id FROM biomes WHERE name='Snowy Taiga Hills')
    ,'resources/images/biomeTile/SNOWY_TAIGA_HILLS.png'
);

INSERT INTO biomes VALUES(DEFAULT, 'Wooded Badlands Plateau+', '166', 'ff6666');
INSERT INTO textures VALUES
(
    DEFAULT
    ,(SELECT id FROM biomes WHERE name='Wooded Badlands Plateau+')
    ,'resources/images/biomeTile/MODIFIED_WOODED_BADLANDS_PLATEAU.png'
);

INSERT INTO biomes VALUES(DEFAULT, 'Lukewarm Ocean', '45', '1f6c99');
INSERT INTO textures VALUES
(
    DEFAULT
    ,(SELECT id FROM biomes WHERE name='Lukewarm Ocean')
    ,'resources/images/biomeTile/LUKEWARM_OCEAN.png'
);

INSERT INTO biomes VALUES(DEFAULT, 'End Barrens', '43', 'a8a38d');
INSERT INTO textures VALUES
(
    DEFAULT
    ,(SELECT id FROM biomes WHERE name='End Barrens')
    ,'resources/images/biomeTile/END_BARRENS.png'
);

INSERT INTO biomes VALUES(DEFAULT, 'Forest', '4', '448105');
INSERT INTO textures VALUES
(
    DEFAULT
    ,(SELECT id FROM biomes WHERE name='Forest')
    ,'resources/images/biomeTile/FOREST.png'
);

INSERT INTO biomes VALUES(DEFAULT, 'River', '7', '7aaecc');
INSERT INTO textures VALUES
(
    DEFAULT
    ,(SELECT id FROM biomes WHERE name='River')
    ,'resources/images/biomeTile/RIVER.png'
);

INSERT INTO biomes VALUES(DEFAULT, 'Gravelly Mountains', '131', '6c6d6b');
INSERT INTO textures VALUES
(
    DEFAULT
    ,(SELECT id FROM biomes WHERE name='Gravelly Mountains')
    ,'resources/images/biomeTile/GRAVELLY_MOUNTAINS.png'
);

INSERT INTO biomes VALUES(DEFAULT, 'Mushroom Fields', '14', '9e8ba3');
INSERT INTO textures VALUES
(
    DEFAULT
    ,(SELECT id FROM biomes WHERE name='Mushroom Fields')
    ,'resources/images/biomeTile/MUSHROOM_FIELDS.png'
);

INSERT INTO biomes VALUES(DEFAULT, 'Jungle Edge+', '151', '007311');
INSERT INTO textures VALUES
(
    DEFAULT
    ,(SELECT id FROM biomes WHERE name='Jungle Edge+')
    ,'resources/images/biomeTile/MODIFIED_JUNGLE_EDGE.png'
);

INSERT INTO biomes VALUES(DEFAULT, 'Ocean', '0', '1f2799');
INSERT INTO textures VALUES
(
    DEFAULT
    ,(SELECT id FROM biomes WHERE name='Ocean')
    ,'resources/images/biomeTile/OCEAN.png'
);

INSERT INTO biomes VALUES(DEFAULT, 'Mushroom Fields Shore', '15', '9e8ba3');
INSERT INTO textures VALUES
(
    DEFAULT
    ,(SELECT id FROM biomes WHERE name='Mushroom Fields Shore')
    ,'resources/images/biomeTile/MUSHROOM_FIELDS_SHORE.png'
);

INSERT INTO biomes VALUES(DEFAULT, 'Taiga Mountains', '133', '733818');
INSERT INTO textures VALUES
(
    DEFAULT
    ,(SELECT id FROM biomes WHERE name='Taiga Mountains')
    ,'resources/images/biomeTile/TAIGA_MOUNTAINS.png'
);

INSERT INTO biomes VALUES(DEFAULT, 'Deep Warm Ocean', '47', '3d9996');
INSERT INTO textures VALUES
(
    DEFAULT
    ,(SELECT id FROM biomes WHERE name='Deep Warm Ocean')
    ,'resources/images/biomeTile/DEEP_WARM_OCEAN.png'
);

INSERT INTO biomes VALUES(DEFAULT, 'Swamp', '6', '003331');
INSERT INTO textures VALUES
(
    DEFAULT
    ,(SELECT id FROM biomes WHERE name='Swamp')
    ,'resources/images/biomeTile/SWAMP.png'
);

INSERT INTO biomes VALUES(DEFAULT, 'Badlands', '37', '773b3a');
INSERT INTO textures VALUES
(
    DEFAULT
    ,(SELECT id FROM biomes WHERE name='Badlands')
    ,'resources/images/biomeTile/BADLANDS.png'
);

INSERT INTO biomes VALUES(DEFAULT, 'Dark Forest Hills', '157', '3d663d');
INSERT INTO textures VALUES
(
    DEFAULT
    ,(SELECT id FROM biomes WHERE name='Dark Forest Hills')
    ,'resources/images/biomeTile/DARK_FOREST_HILLS.png'
);

INSERT INTO biomes VALUES(DEFAULT, 'Beach', '16', 'a3a6cc');
INSERT INTO textures VALUES
(
    DEFAULT
    ,(SELECT id FROM biomes WHERE name='Beach')
    ,'resources/images/biomeTile/BEACH.png'
);

INSERT INTO biomes VALUES(DEFAULT, 'Giant Spruce Taiga', '160', '733818');
INSERT INTO textures VALUES
(
    DEFAULT
    ,(SELECT id FROM biomes WHERE name='Giant Spruce Taiga')
    ,'resources/images/biomeTile/GIANT_SPRUCE_TAIGA.png'
);

INSERT INTO biomes VALUES(DEFAULT, 'Desert Lakes', '130', 'd3d1a1');
INSERT INTO textures VALUES
(
    DEFAULT
    ,(SELECT id FROM biomes WHERE name='Desert Lakes')
    ,'resources/images/biomeTile/DESERT_LAKES.png'
);

INSERT INTO biomes VALUES(DEFAULT, 'Jungle+', '149', '007311');
INSERT INTO textures VALUES
(
    DEFAULT
    ,(SELECT id FROM biomes WHERE name='Jungle+')
    ,'resources/images/biomeTile/MODIFIED_JUNGLE.png'
);

INSERT INTO biomes VALUES(DEFAULT, 'Snowy Taiga Mountains', '158', 'ffffff');
INSERT INTO textures VALUES
(
    DEFAULT
    ,(SELECT id FROM biomes WHERE name='Snowy Taiga Mountains')
    ,'resources/images/biomeTile/SNOWY_TAIGA_MOUNTAINS.png'
);

INSERT INTO biomes VALUES(DEFAULT, 'Plains', '1', '29ac0c');
INSERT INTO textures VALUES
(
    DEFAULT
    ,(SELECT id FROM biomes WHERE name='Plains')
    ,'resources/images/biomeTile/PLAINS.png'
);

INSERT INTO biomes VALUES(DEFAULT, 'Frozen River', '11', '99daff');
INSERT INTO textures VALUES
(
    DEFAULT
    ,(SELECT id FROM biomes WHERE name='Frozen River')
    ,'resources/images/biomeTile/FROZEN_RIVER.png'
);

INSERT INTO biomes VALUES(DEFAULT, 'End Midlands', '41', '99003d');
INSERT INTO textures VALUES
(
    DEFAULT
    ,(SELECT id FROM biomes WHERE name='End Midlands')
    ,'resources/images/biomeTile/END_MIDLANDS.png'
);

INSERT INTO biomes VALUES(DEFAULT, 'Deep Cold Ocean', '49', '3d4066');
INSERT INTO textures VALUES
(
    DEFAULT
    ,(SELECT id FROM biomes WHERE name='Deep Cold Ocean')
    ,'resources/images/biomeTile/DEEP_COLD_OCEAN.png'
);

INSERT INTO biomes VALUES(DEFAULT, 'The Void', '127', '330a1b');
INSERT INTO textures VALUES
(
    DEFAULT
    ,(SELECT id FROM biomes WHERE name='The Void')
    ,'resources/images/biomeTile/THE_VOID.png'
);

INSERT INTO biomes VALUES(DEFAULT, 'Bamboo Jungle Hills', '169', '007311');
INSERT INTO textures VALUES
(
    DEFAULT
    ,(SELECT id FROM biomes WHERE name='Bamboo Jungle Hills')
    ,'resources/images/biomeTile/BAMBOO_JUNGLE_HILLS.png'
);

INSERT INTO biomes VALUES(DEFAULT, 'Eroded Badlands', '165', 'cc0000');
INSERT INTO textures VALUES
(
    DEFAULT
    ,(SELECT id FROM biomes WHERE name='Eroded Badlands')
    ,'resources/images/biomeTile/ERODED_BADLANDS.png'
);

INSERT INTO biomes VALUES(DEFAULT, 'Birch Forest Hills', '28', '448105');
INSERT INTO textures VALUES
(
    DEFAULT
    ,(SELECT id FROM biomes WHERE name='Birch Forest Hills')
    ,'resources/images/biomeTile/BIRCH_FOREST_HILLS.png'
);

INSERT INTO biomes VALUES(DEFAULT, 'Swamp Hills', '134', '006663');
INSERT INTO textures VALUES
(
    DEFAULT
    ,(SELECT id FROM biomes WHERE name='Swamp Hills')
    ,'resources/images/biomeTile/SWAMP_HILLS.png'
);

INSERT INTO biomes VALUES(DEFAULT, 'The End', '9', 'cc0052');
INSERT INTO textures VALUES
(
    DEFAULT
    ,(SELECT id FROM biomes WHERE name='The End')
    ,'resources/images/biomeTile/THE_END.png'
);

INSERT INTO biomes VALUES(DEFAULT, 'Jungle Hills', '22', '007311');
INSERT INTO textures VALUES
(
    DEFAULT
    ,(SELECT id FROM biomes WHERE name='Jungle Hills')
    ,'resources/images/biomeTile/JUNGLE_HILLS.png'
);

INSERT INTO key_items (map_type_id, description, color, icon_url)
SELECT (SELECT id FROM map_types WHERE name='Biome'), bi.name, bi.color,tex.url
FROM biomes bi
JOIN textures tex on bi.id = tex.biome_id;
