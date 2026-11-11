package com.gempukku.swccgo.async.handler;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/** A name with a space is not a role entry in the chat list (#249, t122u). */
public class ChatListRoleMarkerTests {
    @Test
    public void spacedNamesAreNotRoles() {
        assertTrue(ChatRequestHandler.hasRoleMarker("* Bill"));
        assertTrue(ChatRequestHandler.hasRoleMarker("+&beta; Rey"));
        assertTrue(ChatRequestHandler.hasRoleMarker("&#231; Finn"));
        assertFalse(ChatRequestHandler.hasRoleMarker("Luke Sky"));
        assertFalse(ChatRequestHandler.hasRoleMarker("Luke"));
    }
}
