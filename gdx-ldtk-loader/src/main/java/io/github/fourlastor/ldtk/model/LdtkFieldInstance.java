package io.github.fourlastor.ldtk.model;

import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.Null;
import io.github.fourlastor.json.JsonParser;
import javax.inject.Inject;

public class LdtkFieldInstance {
    /**
     * Field definition identifier
     * SerialName("__identifier")
     */
    public final String identifier;

    /**
     * Type of the field, such as `Int`, `Float`, `Enum(my_enum_name)`, `Bool`, etc.
     * SerialName("__type")
     */
    public final String type;

    public final float floatValue;
    public final boolean booleanValue;

    /**
     * Reference of the **Field definition** UID
     */
    public final int defUid;

    /**
     * Optional TilesetRect used to display this field (this can be the field own Tile, or some
     * other Tile guessed from the value, like an Enum)
     * SerialName("__tile")
     */
    @Null
    public final LdtkTileRect tile;

    public LdtkFieldInstance(
            String identifier, String type, float floatValue, boolean booleanValue, int defUid, @Null LdtkTileRect tile) {
        this.identifier = identifier;
        this.type = type;
        this.floatValue = floatValue;
        this.booleanValue = booleanValue;
        this.defUid = defUid;
        this.tile = tile;
    }

    public static class Parser extends JsonParser<LdtkFieldInstance> {
        private final JsonParser<LdtkTileRect> tileParser;

        @Inject
        public Parser(JsonParser<LdtkTileRect> tileParser) {
            this.tileParser = tileParser;
        }

        @Override
        public LdtkFieldInstance parse(JsonValue value) {
            String type = value.getString("__type");
            boolean booleanVal = false;
            float floatVal = 0f;
            switch (type) {
                case "Bool":
                    booleanVal = value.get("__value").asBoolean();
                    break;
                case "Float":
                    floatVal = value.get("__value").asFloat();
                    break;
            }
            return new LdtkFieldInstance(
                    value.getString("__identifier"),
                    type,
                    floatVal, booleanVal,
                    value.getInt("defUid"),
                    getOptional(value, "__tile", tileParser::parse));
        }
    }
}
