package com.gempukku.swccgo.async.handler;

import com.gempukku.swccgo.game.Player;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

/** The chat user list carries what the player menu shows (swccg-guild/work#264, AC6), and nothing private. */
public class ChatPlayerMenuTests {
    @Test
    public void UsersCarryKindAndPlayerId() throws Exception {
        GuestHandlerFixture f = new GuestHandlerFixture(GuestHandlerFixture.enabled());
        when(f.playerDao.getPlayer("Luke Sky")).thenReturn(new Player(3812, "Luke Sky", "secret-hash", "u", null, null, "1.2.3.4", "5.6.7.8"));
        when(f.playerDao.getPlayer("Bill")).thenReturn(new Player(2, "Bill", "secret-hash", "a", null, null, "1.2.3.4", "5.6.7.8"));
        ChatRequestHandler handler = new ChatRequestHandler(f.context, null);
        Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();

        handler.serializeChatRoomData("Game1", new ArrayList<>(),
                List.of("Luke Sky", "Bill", "~OzzelBot", "Light Side", "guest-AbCdEf123456"), doc);

        Map<String, Element> byName = new HashMap<>();
        NodeList users = doc.getElementsByTagName("user");
        for (int i = 0; i < users.getLength(); i++) {
            Element u = (Element) users.item(i);
            byName.put(u.getAttribute("name"), u);
            //test1: only the name, the kind and the player id; no password, IP, type or anything else
            NamedNodeMap attrs = u.getAttributes();
            for (int a = 0; a < attrs.getLength(); a++)
                assertTrue(List.of("name", "kind", "playerId").contains(attrs.item(a).getNodeName()), attrs.item(a).getNodeName());
            assertFalse(u.getTextContent().contains("secret") || u.getTextContent().contains("1.2.3.4"));
        }
        //test2: an account has its id; an admin's entry keeps its role marker in the text, the name is plain
        assertEquals("3812", byName.get("Luke Sky").getAttribute("playerId"));
        assertEquals("player", byName.get("Luke Sky").getAttribute("kind"));
        assertEquals("2", byName.get("Bill").getAttribute("playerId"));
        assertTrue(byName.get("Bill").getTextContent().startsWith("* "));
        //test3: bots and guests (a seat or a guest id) have a kind and no id
        assertEquals("bot", byName.get("~OzzelBot").getAttribute("kind"));
        assertFalse(byName.get("~OzzelBot").hasAttribute("playerId"));
        assertEquals("guest", byName.get("Light Side").getAttribute("kind"));
        assertFalse(byName.get("Light Side").hasAttribute("playerId"));
        assertEquals("guest", byName.get("guest-AbCdEf123456").getAttribute("kind"));
        assertFalse(byName.get("guest-AbCdEf123456").hasAttribute("playerId"));
    }
}
