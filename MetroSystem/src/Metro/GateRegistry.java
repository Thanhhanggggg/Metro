package Metro;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class GateRegistry {
    private static GateRegistry instance;
    private final Map<String, SmartGate> gates = new LinkedHashMap<>();

    private GateRegistry() {}

    public static GateRegistry getInstance() {
        if (instance == null) instance = new GateRegistry();
        return instance;
    }

    public void register(SmartGate gate) {
        gates.put(gate.getGateId(), gate);
    }

    public boolean remove(String gateId) {
        return gates.remove(gateId) != null;
    }

    public SmartGate findById(String id) {
        return gates.get(id);
    }

    public Collection<SmartGate> getAll() {
        return Collections.unmodifiableCollection(gates.values());
    }

    public boolean exists(String gateId) {
        return gates.containsKey(gateId);
    }
}
