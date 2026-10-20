package com.gempukku.swccgo.game.save;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.KryoException;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.SerializerFactory;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.serializers.CollectionSerializer;
import com.esotericsoftware.kryo.serializers.FieldSerializer;
import com.esotericsoftware.kryo.serializers.MapSerializer;
import com.esotericsoftware.kryo.util.DefaultInstantiatorStrategy;
import com.esotericsoftware.kryo.util.Pool;
import com.gempukku.swccgo.ai.SwccgAiController;
import com.gempukku.swccgo.game.SwccgCardBlueprint;
import com.gempukku.swccgo.game.SwccgCardBlueprintLibrary;
import com.gempukku.swccgo.game.SwccgFormat;
import com.gempukku.swccgo.game.formats.SwccgoFormatLibrary;
import com.gempukku.swccgo.logic.timing.DefaultSwccgGame;
import com.gempukku.swccgo.logic.timing.DefaultUserFeedback;
import org.objenesis.strategy.StdInstantiatorStrategy;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

/**
 * Writes and reads Kryo snapshots of a live game.
 * <ul>
 * <li>Registration is required, from the build-time allowlist ({@link SaveBuild}); anything else is refused.</li>
 * <li>The card library, format and blueprints are shared by all games, so they are written as ids, not copied.</li>
 * <li>Listeners (client channels, hall, recorder) and the revert history are left out; the server re-attaches
 * listeners after a load, and revert starts fresh from the load point.</li>
 * <li>Anonymous classes keep their captured values (synthetic fields are written).</li>
 * </ul>
 * Needs {@code --add-opens} for java.base/java.util, java.lang, java.util.concurrent, java.util.concurrent.atomic,
 * java.util.regex and java.lang.invoke (set in the web.jar manifest and the test JVM).
 */
public final class GameSaveCodec {
    private static final String[] GAME_FIELDS_NOT_SAVED = {"_gameStateListeners", "_gameResultListeners", "_inGameStatisticsListeners", "_snapshots", "_snapshotToRestore"};

    private final SwccgCardBlueprintLibrary _library;
    private final SwccgoFormatLibrary _formats;
    private final SaveBuild _build;
    private final Pool<Kryo> _pool;

    public GameSaveCodec(SwccgCardBlueprintLibrary library, SwccgoFormatLibrary formats, SaveBuild build) {
        _library = library;
        _formats = formats;
        _build = build;
        _pool = new Pool<>(true, false, 8) {
            @Override
            protected Kryo create() {
                return newKryo();
            }
        };
    }

    public SaveBuild getBuild() {
        return _build;
    }

    /**
     * Snapshots a game.
     * @param root the game, its pending decisions and its bots
     * @return the compressed snapshot
     * @throws KryoException if the game holds something that can't be saved (for example a class not on the allowlist)
     */
    public byte[] write(SaveRoot root) {
        return encode(new Object[]{root.game(), root.feedback(), new HashMap<>(root.bots())});
    }

    /**
     * Reads a snapshot written by a build with the same class fingerprint.
     * @param snapshot the compressed snapshot
     * @return a new, independent copy of the game
     * @throws KryoException if the snapshot can't be read
     */
    @SuppressWarnings("unchecked")
    public SaveRoot read(byte[] snapshot) {
        Object[] r = (Object[]) decode(snapshot);
        return new SaveRoot((DefaultSwccgGame) r[0], (DefaultUserFeedback) r[1], (Map<String, SwccgAiController>) r[2]);
    }

    byte[] encode(Object root) {
        Kryo kryo = _pool.obtain();
        try {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream(64 * 1024);
            try (Output out = new Output(new DeflaterOutputStream(bytes, new Deflater(Deflater.BEST_SPEED)), 64 * 1024)) {
                kryo.writeClassAndObject(out, root);
            }
            return bytes.toByteArray();
        } catch (KryoException e) {
            throw e;
        } catch (RuntimeException e) {
            // Kryo reports an unlisted class as IllegalArgumentException("Class is not registered: ...")
            throw new KryoException(e.getMessage(), e);
        } finally {
            _pool.free(kryo);
        }
    }

    Object decode(byte[] snapshot) {
        Kryo kryo = _pool.obtain();
        try (Input in = new Input(new InflaterInputStream(new ByteArrayInputStream(snapshot)), 64 * 1024)) {
            return kryo.readClassAndObject(in);
        } catch (KryoException e) {
            throw e;
        } catch (RuntimeException e) {
            throw new KryoException(e.getMessage(), e);
        } finally {
            _pool.free(kryo);
        }
    }

    private Kryo newKryo() {
        FieldSerializer.FieldSerializerConfig config = new FieldSerializer.FieldSerializerConfig();
        config.setIgnoreSyntheticFields(false); // anonymous classes keep captured values (val$x) and this$0
        Kryo kryo = new Kryo(new AllowlistClassResolver(_build.getClassNames()), null) {
            @Override
            public Serializer getDefaultSerializer(Class type) {
                String n = type.getName();
                // JDK wrappers (Collections$Unmodifiable*, Arrays$ArrayList, List.of, HashMap$KeySet, ...) are copied field by field
                if (n.startsWith("java.util.") && n.contains("$") && !n.startsWith("java.util.concurrent")) {
                    return new FieldSerializer<>(this, type, config);
                }
                return super.getDefaultSerializer(type);
            }
        };
        kryo.setRegistrationRequired(true);
        kryo.setReferences(true);
        kryo.setInstantiatorStrategy(new DefaultInstantiatorStrategy(new StdInstantiatorStrategy()));
        kryo.setDefaultSerializer(new SerializerFactory.FieldSerializerFactory(config));

        kryo.addDefaultSerializer(SwccgCardBlueprintLibrary.class, new Serializer<SwccgCardBlueprintLibrary>() {
            @Override
            public void write(Kryo k, Output out, SwccgCardBlueprintLibrary lib) {
            }

            @Override
            public SwccgCardBlueprintLibrary read(Kryo k, Input in, Class<? extends SwccgCardBlueprintLibrary> type) {
                return _library;
            }
        });
        kryo.addDefaultSerializer(SwccgFormat.class, new Serializer<SwccgFormat>() {
            @Override
            public void write(Kryo k, Output out, SwccgFormat format) {
                out.writeString(format.getCode());
            }

            @Override
            public SwccgFormat read(Kryo k, Input in, Class<? extends SwccgFormat> type) {
                return _formats.getFormat(in.readString());
            }
        });
        kryo.addDefaultSerializer(SwccgCardBlueprint.class, new Serializer<SwccgCardBlueprint>() {
            @Override
            public void write(Kryo k, Output out, SwccgCardBlueprint blueprint) {
                String id = _library.getLoadedBlueprintId(blueprint);
                if (id == null) {
                    throw new KryoException("Blueprint not loaded by the card library: " + blueprint.getClass().getName());
                }
                out.writeString(id);
            }

            @Override
            public SwccgCardBlueprint read(Kryo k, Input in, Class<? extends SwccgCardBlueprint> type) {
                String id = in.readString();
                return id.endsWith("_BACK") ? _library.getSwccgoCardBlueprintBack(id) : _library.getSwccgoCardBlueprint(id);
            }
        });
        kryo.addDefaultSerializer(DefaultSwccgGame.class, new SerializerFactory.BaseSerializerFactory<GameSerializer>() {
            @Override
            public GameSerializer newSerializer(Kryo k, Class type) {
                return new GameSerializer(k, type, config);
            }
        });
        kryo.addDefaultSerializer(HashMap.class, new SizedHashMapSerializer());
        kryo.addDefaultSerializer(HashSet.class, new SizedHashSetSerializer());
        return kryo;
    }

    private static Field field(Class<?> type, String name) {
        try {
            Field f = type.getDeclaredField(name);
            f.setAccessible(true);
            return f;
        } catch (NoSuchFieldException e) {
            throw new IllegalStateException(e);
        }
    }

    /** Leaves out listeners and the revert history; a loaded game gets empty ones. */
    static final class GameSerializer extends FieldSerializer<DefaultSwccgGame> {
        GameSerializer(Kryo kryo, Class<DefaultSwccgGame> type, FieldSerializerConfig config) {
            super(kryo, type, config);
            for (String name : GAME_FIELDS_NOT_SAVED) {
                removeField(name);
            }
        }

        @Override
        public DefaultSwccgGame read(Kryo kryo, Input input, Class<? extends DefaultSwccgGame> type) {
            DefaultSwccgGame game = super.read(kryo, input, type);
            try {
                field(DefaultSwccgGame.class, "_gameStateListeners").set(game, new HashSet<>());
                field(DefaultSwccgGame.class, "_gameResultListeners").set(game, new HashSet<>());
                field(DefaultSwccgGame.class, "_inGameStatisticsListeners").set(game, new HashSet<>());
                field(DefaultSwccgGame.class, "_snapshots").set(game, new LinkedList<>());
            } catch (IllegalAccessException e) {
                throw new KryoException(e);
            }
            return game;
        }
    }

    private static final Field HASHMAP_TABLE = field(HashMap.class, "table");
    private static final Field HASHSET_MAP = field(HashSet.class, "map");

    private static int tableLength(HashMap<?, ?> map) {
        try {
            Object[] table = (Object[]) HASHMAP_TABLE.get(map);
            return table == null ? 0 : table.length;
        } catch (IllegalAccessException e) {
            throw new KryoException(e);
        }
    }

    /** Capacity whose table matches the original, so the rebuilt map iterates in the same order. */
    private static int capacityFor(int tableLength) {
        return tableLength == 0 ? 16 : (int) (tableLength * 0.75f);
    }

    /** A HashMap rebuilt with the original table size (iteration order follows hash and table size). */
    static final class SizedHashMapSerializer extends MapSerializer<Map> {
        private int _pendingTableLength;

        @Override
        public void write(Kryo kryo, Output output, Map map) {
            output.writeVarInt(tableLength((HashMap<?, ?>) map), true);
            super.write(kryo, output, map);
        }

        @Override
        public Map read(Kryo kryo, Input input, Class<? extends Map> type) {
            _pendingTableLength = input.readVarInt(true);
            return super.read(kryo, input, type);
        }

        @Override
        protected Map create(Kryo kryo, Input input, Class<? extends Map> type, int size) {
            if (type == HashMap.class) {
                return new HashMap<>(capacityFor(_pendingTableLength));
            }
            return super.create(kryo, input, type, size);
        }
    }

    /** A HashSet rebuilt with the original table size. */
    static final class SizedHashSetSerializer extends CollectionSerializer<Collection> {
        private int _pendingTableLength;

        @Override
        public void write(Kryo kryo, Output output, Collection set) {
            try {
                output.writeVarInt(tableLength((HashMap<?, ?>) HASHSET_MAP.get(set)), true);
            } catch (IllegalAccessException e) {
                throw new KryoException(e);
            }
            super.write(kryo, output, set);
        }

        @Override
        public Collection read(Kryo kryo, Input input, Class<? extends Collection> type) {
            _pendingTableLength = input.readVarInt(true);
            return super.read(kryo, input, type);
        }

        @Override
        protected Collection create(Kryo kryo, Input input, Class<? extends Collection> type, int size) {
            if (type == HashSet.class) {
                return new HashSet<>(capacityFor(_pendingTableLength));
            }
            return super.create(kryo, input, type, size);
        }
    }

    static List<String> notSavedGameFields() {
        return List.of(GAME_FIELDS_NOT_SAVED);
    }
}
