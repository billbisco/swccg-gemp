package com.gempukku.swccgo.game.save;

import com.esotericsoftware.kryo.KryoException;
import com.esotericsoftware.kryo.Registration;
import com.esotericsoftware.kryo.util.DefaultClassResolver;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Registration-required Kryo resolver backed by the build-time class list. A class's id is fixed by its position in
 * the sorted list, but the class is only registered (and its serializer built) the first time a save uses it, so
 * startup stays cheap. A class that isn't on the list is refused: Kryo throws "Class is not registered".
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

    private Registration registerNow(Class<?> type, int id) {
        _registering = true;
        try {
            return kryo.register(type, id);
        } finally {
            _registering = false;
        }
    }
}
