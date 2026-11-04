package com.gempukku.swccgo.cards.set1283.light;

import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1283_023_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("rughug", "1283_23");
                    put("rughug2", "1283_23");
                    put("luke", "1_19");
                    put("han", "1_11");
                    put("boshek", "1_4");
                    put("momaw", "1_20");
                    put("quarters", "5_80");
                }},
                new HashMap<>() {{
                    put("trooper", "1_194");
                }},
                10, 10,
                StartingSetup.DefaultLSGroundLocation,
                StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts,
                StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields,
                StartingSetup.NoDSShields,
                VirtualTableScenario.Open
        );
    }

    @Test
    public void RugHugVStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("rughug").getBlueprint();

        assertEquals("Rug Hug", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(4, card.getDestiny(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.INTERRUPT);
        }});
        assertEquals(CardSubtype.LOST, card.getCardSubtype());
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.INTERRUPT);
            add(Icon.LEGACY_BLOCK_8);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_8_3, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void RugHugUsesTwoForceToMoveYourCharacterAsAReactToABattleOpponentInitiated() {
        var scn = GetScenario();
        var rughug = scn.GetLSCard("rughug");
        var han = scn.GetLSCard("han");
        var boshek = scn.GetLSCard("boshek");
        var quarters = scn.GetLSCard("quarters");
        var trooper = scn.GetDSCard("trooper");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveLocationToTable(quarters);
        scn.MoveCardsToLocation(site, boshek, trooper);
        scn.MoveCardsToLocation(quarters, han);
        scn.MoveCardsToLSHand(rughug);
        scn.EnsureDSForcePile(3);
        scn.SkipToDSTurn(Phase.BATTLE);
        scn.EnsureLSForcePile(4);
        int force = scn.GetLSForcePileCount();
        scn.DSInitiateBattle(site);
        assertTrue(scn.LSCardPlayAvailable(rughug, "react"));
        scn.LSPlayCard(rughug, "react");
        if (scn.LSHasCardChoiceAvailable(han)) {
            scn.LSChooseCard(han);
        }
        scn.PassAllResponses();
        assertTrue(scn.CardsAtLocation(site, han));
        assertEquals(force - 2, scn.GetLSForcePileCount());
    }

    @Test
    public void RugHugAddsOneBattleDestinyWhenLukeAndYourAlienAreInBattleTogether() {
        var scn = GetScenario();
        var rughug = scn.GetLSCard("rughug");
        var luke = scn.GetLSCard("luke");
        var boshek = scn.GetLSCard("boshek");
        var trooper = scn.GetDSCard("trooper");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, luke, boshek, trooper);
        scn.MoveCardsToLSHand(rughug);
        scn.SkipToLSTurn(Phase.BATTLE);
        scn.LSInitiateBattle(site);
        scn.PassAllResponses();
        int before = scn.GetLSBattleDestinyCount();
        assertTrue(scn.LSCardPlayAvailable(rughug, "battle destiny"));
        scn.LSPlayCard(rughug, "battle destiny");
        scn.PassAllResponses();
        assertEquals(before + 1, scn.GetLSBattleDestinyCount());
    }

    @Test
    public void RugHugMayNotAddBattleDestinyWithoutAnAlienInBattle() {
        var scn = GetScenario();
        var rughug = scn.GetLSCard("rughug");
        var luke = scn.GetLSCard("luke");
        var han = scn.GetLSCard("han");
        var trooper = scn.GetDSCard("trooper");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, luke, han, trooper);
        scn.MoveCardsToLSHand(rughug);
        scn.SkipToLSTurn(Phase.BATTLE);
        scn.LSInitiateBattle(site);
        scn.PassAllResponses();
        assertFalse(scn.LSCardPlayAvailable(rughug, "battle destiny"));
    }

    @Test
    public void RugHugActivatesForceEqualToYourAliensOnTableOncePerGame() {
        var scn = GetScenario();
        var rughug = scn.GetLSCard("rughug");
        var rughug2 = scn.GetLSCard("rughug2");
        var boshek = scn.GetLSCard("boshek");
        var momaw = scn.GetLSCard("momaw");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, boshek, momaw);
        scn.MoveCardsToLSHand(rughug, rughug2);
        scn.SkipToLSTurn(Phase.DEPLOY);
        int force = scn.GetLSForcePileCount();
        assertTrue(scn.LSCardPlayAvailable(rughug, "Activate"));
        scn.LSPlayCard(rughug, "Activate");
        scn.PassAllResponses();
        assertEquals(force + 2, scn.GetLSForcePileCount());
        if (scn.DSAnyDecisionsAvailable()) {
            scn.DSPass();
        }
        assertFalse(scn.LSCardPlayAvailable(rughug2, "Activate"));
    }
}
