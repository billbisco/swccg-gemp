package com.gempukku.swccgo.game.save;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.KryoException;
import com.esotericsoftware.kryo.Registration;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.util.DefaultClassResolver;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Registration-required Kryo resolver backed by the build-time class list. A class's id is fixed by its position in
 * the sorted list, but the class is only registered (and its serializer built) the first time a save uses it, so
 * startup stays cheap. A class that isn't on the list is refused: Kryo throws "Class is not registered".
 *
 * Ids never depend on the order classes are met in: the same list gives the same ids in every JVM and every Kryo
 * instance, and a class is registered under its listed id on first use, whether that use is a write or a read.
 */
final class AllowlistClassResolver extends DefaultClassResolver {
    static final int FIRST_ID = 100;

    private final Map<String, Integer> _ids = new HashMap<>();
    private final String[] _byId;
    private boolean _registering;

    AllowlistClassResolver(List<String> names) {
        _byId = names.toArray(new String[0]);
        for (int i = 0; i < _byId.length; i++) {
            _ids.put(_byId[i], FIRST_ID + i);
        }
    }

    @Override
    public Registration getRegistration(Class type) {
        Registration r = super.getRegistration(type);
        if (r != null || _registering) {
            return r;
        }
        Integer id = _ids.get(type.getName());
        if (id == null) {
            return null;
        }
        return registerNow(type, id);
    }

    @Override
    public Registration getRegistration(int classId) {
        Registration r = super.getRegistration(classId);
        if (r != null || _registering || classId < FIRST_ID || classId - FIRST_ID >= _byId.length) {
            return r;
        }
        try {
            return registerNow(Class.forName(_byId[classId - FIRST_ID], false, AllowlistClassResolver.class.getClassLoader()), classId);
        } catch (ClassNotFoundException e) {
            throw new KryoException(e);
        }
    }

    /**
     * Reads a class id. {@link DefaultClassResolver#readClass} looks the id up in the registered classes only, so a
     * class this Kryo instance hasn't written yet (a fresh JVM, or another instance from the pool) would fail with
     * "Encountered unregistered class ID". Here the id goes through {@link #getRegistration(int)}, which registers
     * the listed class on first use.
     */
    @Override
    public Registration readClass(Input input) {
        int classId = input.readVarInt(true);
        if (classId == Kryo.NULL) {
            return null;
        }
        if (classId == NAME + 2) {
            return readName(input);
        }
        Registration registration = getRegistration(classId - 2);
        if (registration == null) {
            throw new KryoException("Encountered unregistered class ID: " + (classId - 2));
        }
        return registration;
    }

    private Registration registerNow(Class<?> type, int id) {
        _registering = true;
        try {
            return kryo.register(type, id);
        } finally {
            _registering = false;
        }
    }
}
