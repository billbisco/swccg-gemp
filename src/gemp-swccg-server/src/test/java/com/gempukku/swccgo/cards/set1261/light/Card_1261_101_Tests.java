package com.gempukku.swccgo.cards.set1261.light;

import com.gempukku.swccgo.common.*;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class Card_1261_101_Tests extends SoloDefenseTestSupport {
    public Card_1261_101_Tests() { super("1261_101", "1_11", "13_21", "5_79"); }

    @Test public void currentTextIsThePCCardListText() {
        // Master List (2013, PC-E3) is the latest text (Bill 2026-11-11).
        var text = fixture().GetLSCard("interrupt").getBlueprint().getGameText();
        assertEquals("Use 1 Force to ▼ (or retrieve) non-[Reflections III] Han. OR If a battle just initiated, and Han defending alone at a site, his forfeit cannot be reduced, he adds one destiny to power (+1 for each character and vehicle present), and draws one battle destiny if not able to otherwise. (Immune to Sense.)", text);
        assertTrue(text.contains("Reflections III"));
    }

    @Test public void exactFinalLegacyInterrupt() {
        var scn = fixture(); var b = scn.GetLSCard("interrupt").getBlueprint();
        assertEquals("Solo Han", b.getTitle()); assertEquals(4, b.getDestiny(), scn.epsilon);
        assertEquals(Uniqueness.UNIQUE, b.getUniqueness()); assertTrue(b.hasVirtualSuffix());
        assertEquals(Side.LIGHT, b.getSide()); assertEquals(Rarity.V, b.getRarity());
        assertEquals(CardSubtype.LOST, b.getCardSubtype()); assertEquals(ExpansionSet.VIRTUAL_BLOCK_6_1, b.getExpansionSet());
        scn.BlueprintCardTypeCheck(b, new ArrayList<>(List.of(CardType.INTERRUPT)));
        scn.BlueprintIconCheck(b, new ArrayList<>(List.of(Icon.INTERRUPT, Icon.LEGACY_BLOCK_6)));
        scn.BlueprintKeywordCheck(b, new ArrayList<>());
    }

    @Test public void soloHanCannotRetrieveTheReflectionsThreeFalconWithHanAboard() {
        var scn = fixture(); var card = scn.GetLSCard("interrupt"); var excluded = scn.GetLSCard("inverse");
        scn.MoveCardsToLSHand(card); scn.MoveCardsToTopOfLSLostPile(excluded);
        scn.SkipToLSTurn(Phase.CONTROL); scn.EnsureLSForcePile(3);
        if (scn.LSCardPlayAvailable(card, "Retrieve from Lost Pile")) {
            scn.LSPlayCard(card, "Retrieve from Lost Pile"); scn.PassAllResponses();
        }
        assertTrue(scn.GetLSLostPile().contains(excluded));
    }

    @Test public void soloHanCannotRetrieveAPermanentWeaponHan() {
        // "non-[Permanent Weapon] Han": Han With Heavy Blaster Pistol is excluded.
        var scn = new com.gempukku.swccgo.framework.VirtualTableScenario(new java.util.HashMap<>(java.util.Map.of("interrupt", "1261_101", "hwhbp", "108_1")),
                new java.util.HashMap<>(), 40, 40, com.gempukku.swccgo.framework.StartingSetup.LSStartingLocation("5_79"),
                com.gempukku.swccgo.framework.StartingSetup.DefaultDSGroundLocation, com.gempukku.swccgo.framework.StartingSetup.NoLSStartingInterrupts,
                com.gempukku.swccgo.framework.StartingSetup.NoDSStartingInterrupts, com.gempukku.swccgo.framework.StartingSetup.NoLSShields,
                com.gempukku.swccgo.framework.StartingSetup.NoDSShields, "legacy");
        scn.StartGame();
        var card = scn.GetLSCard("interrupt"); var excluded = scn.GetLSCard("hwhbp");
        scn.MoveCardsToLSHand(card); scn.MoveCardsToTopOfLSLostPile(excluded);
        scn.SkipToLSTurn(Phase.CONTROL); scn.EnsureLSForcePile(3);
        if (scn.LSCardPlayAvailable(card, "Retrieve from Lost Pile")) {
            scn.LSPlayCard(card, "Retrieve from Lost Pile"); scn.PassAllResponses();
        }
        assertTrue(scn.GetLSLostPile().contains(excluded));
    }
    // The Master List (2013, current) and PC card image put the "(+1 for each character and vehicle present)" on the
    // added destiny: the shared SoloDefenseTestSupport tests run as written. The card-list overrides are on 2261_1101.
}
