#!/usr/bin/perl
# Generates an SQL script that will populate the ChunkAtlas PostgreSQL database
# with initial values.
use strict;
use warnings;
use File::Slurp;

## File and Directory Paths: ##
# Java class containing biome data that will go in the database:
my $biomeFile = '../src/main/java/com/centuryglass/chunk_atlas/worldinfo/Biome.java';
# Directory containing all biome texture images:
my $textureDir = '../Resources/biomeTile';
# Resource directory where biome textures will be placed later:
my $resourceDir = 'resources/images/biomeTile/';
# SQL File where results will be saved:
my $outFile = '../database/initDB.sql';

## Regex Patterns:
# match NAMES_FORMATTED_LIKE_THIS:
my $matchName = '[A-Z]+(?:_[A-Z]+)*'; 
# match color object declarations:
my $matchColor = 'new Color\(0x[0-9a-f]{2}, 0x[0-9a-f]{2}, 0x[0-9a-f]{2}';
# as above, but capture all color components:
my $captureColor = 'new Color\(0x([0-9a-f]{2}), 0x([0-9a-f]{2}), '
        .'0x([0-9a-f]{2})';

# Array where the SQL script will be built:
my @sqlLines;
# Start with fixed initial values that require no further processing:
my %tables =
(
    'dimensions' => ["'Overworld'", "'Nether'", "'The End'"],
    'map_types' => ["'Biome'", "'Structure'", "'Total Activity'", "'Errors'"],
    'tile_sizes' => [512, 128, 64, 32]
);
foreach my $table (keys %tables)
{
    foreach my $value(@{($tables{$table})})
    {
        push(@sqlLines, "INSERT INTO $table VALUES(DEFAULT, $value);\n");
    }
}

# Extract biome definitions from the biome file:
my $biomeClass = read_file($biomeFile);
my %biomes;
my @biomeMatches = ($biomeClass =~ /($matchName \(\d+\))/g);
foreach my $biome (@biomeMatches)
{
    if ($biome =~ /($matchName) \((\d+)\)/)
    {
        my $name = $1;
        my $id = $2;
        my $formattedName = $name;
        $formattedName =~ s/MODIFIED_(.+)/$1+/;
        $formattedName =~ s/_/ /g;
        $formattedName =~ s/(\w)(\w*)/$1\L$2/g;
        my $biomeData = 
        {
            'name' => $name,
            'formattedName' => $formattedName,
            'id' => $id
        };
        $biomes{$name} = $biomeData;
    }
    else
    {
        die("Failed to find name or id in biome \"$biome\"\n");
    }
}
# Extract biome colors:
my @colorMatches
        = ($biomeClass =~ /colorMap\.put\(($matchName.*?$matchColor)/gsi);
foreach my $colorMatch (@colorMatches)
{
    if ($colorMatch =~ /($matchName).*?$captureColor/si)
    {
        my $name = $1;
        my $color = $2.$3.$4;
        $biomes{$name}->{'color'} = $color;
    }
    else
    {
        die("Failed to find name or color in assignment \"$colorMatch\"\n");
    }
}
# Scan for biome textures:
opendir(my $dh, $textureDir) || die("Failed to open $textureDir\n");
while (readdir $dh)
{
    my $filename = $_;
    if ($filename =~ /(.+)\.png$/)
    {
        my $name = $1;
        if (! exists($biomes{$name}))
        {
            print("Skipping invalid texture $filename\n");
            next;
        }
        $biomes{$name}->{'texture'} = "$resourceDir$filename";
    }
}
closedir($dh);
# Add each biome and texture to the database:
foreach my $biome (keys %biomes)
{
    my $biomeData = $biomes{$biome};
    push(@sqlLines, "INSERT INTO biomes VALUES(DEFAULT, "
            ."'$biomeData->{'formattedName'}', "
            ."'$biomeData->{'id'}', "
            ."'$biomeData->{'color'}');\n");
    if (! defined($biomeData->{'texture'}))
    {
        print("Warning: biome \"$biomeData->{'name'}\""
                ."has no texture file.\n");
        next;
    }
    push(@sqlLines, "INSERT INTO textures VALUES\n(\n    DEFAULT\n    ,"
            ."(SELECT id FROM biomes WHERE name="
            ."'$biomeData->{'formattedName'}')\n    ,"
            ."'$biomeData->{'texture'}'\n);\n\n");
}
# Generate biome key values from table data:
push(@sqlLines, 
(
    "INSERT INTO key_items (map_type_id, description, color, icon_url)\n",
    "SELECT (SELECT id FROM map_types WHERE name='Biome'), bi.name, bi.color,",
    "tex.url\n",
    "FROM biomes bi\n",
    "JOIN textures tex on bi.id = tex.biome_id;\n"
));
write_file($outFile, @sqlLines);

