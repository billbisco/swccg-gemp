package com.gempukku.swccgo.cards.set1240.light;

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

public class Card_1240_106_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("academy", "1240_106");
                    put("luke", "9_24");       // Luke Skywalker, Jedi Knight
                    put("quigon", "11_10");    // [Episode I] Jedi
                    put("boshek", "1_4");
                    put("skull", "1240_97");
                    put("disarming", "1240_82");
                    put("alter", "1_71");
                }},
                new HashMap<>() {{
                    put("vader", "1_168");
                    put("blastdoor", "1_210");
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
    public void JediAcademyStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("academy").getBlueprint();

        assertEquals("Yavin 4: Jedi Academy", card.getTitle());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(0, card.getDestiny(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.LOCATION);
        }});
        assertEquals(CardSubtype.SITE, card.getCardSubtype());
        assertEquals("Yavin 4", card.getSystemName());
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.A_NEW_HOPE);
            add(Icon.EXTERIOR_SITE);
            add(Icon.PLANET);
            add(Icon.LIGHT_FORCE);
            add(Icon.DARK_FORCE);
            add(Icon.LEGACY_BLOCK_4);
        }});
        assertEquals(2, card.getIconCount(Icon.LIGHT_FORCE));
        assertEquals(2, card.getIconCount(Icon.DARK_FORCE));
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_4, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void JediAcademyForceDrainMinus1HereOnlyIfDarkJediOnTable() {
        var scn = GetScenario();
        var academy = scn.GetLSCard("academy");
        var boshek = scn.GetLSCard("boshek");

        scn.StartGame();
        scn.MoveLocationToTable(academy);
        scn.MoveCardsToLocation(academy, boshek);
        scn.SkipToLSTurn(Phase.CONTROL);
        assertTrue(scn.LSForceDrainAvailable(academy));
        scn.LSForceDrainAt(academy);
        scn.PassForceDrainStartResponses();
        assertEquals(2, scn.GetForceDrainTotal());

        var scn2 = GetScenario();
        var academy2 = scn2.GetLSCard("academy");
        scn2.StartGame();
        scn2.MoveLocationToTable(academy2);
        scn2.MoveCardsToLocation(academy2, scn2.GetLSCard("boshek"));
        scn2.MoveCardsToLocation(scn2.GetDSStartingLocation(), scn2.GetDSCard("vader"));
        scn2.SkipToLSTurn(Phase.CONTROL);
        scn2.LSForceDrainAt(academy2);
        scn2.PassForceDrainStartResponses();
        assertEquals(1, scn2.GetForceDrainTotal());
    }

    @Test
    public void JediAcademyDeploysJediLukeHereFromReserveDeckOncePerGame() {
        var scn = GetScenario();
        var academy = scn.GetLSCard("academy");
        var luke = scn.GetLSCard("luke");

        scn.StartGame();
        scn.MoveLocationToTable(academy);
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.EnsureForcePile(scn.LS, 9);
        scn.MoveCardsToTopOfLSReserveDeck(luke);

        assertTrue(scn.LSCardActionAvailable(academy, "Deploy Jedi Luke here"));
        scn.LSUseCardAction(academy, "Deploy Jedi Luke here");
        scn.PassAllResponses();
        if (scn.LSHasCardChoiceAvailable(luke)) {
            scn.LSChooseCard(luke);
        }
        scn.PassAllResponses();
        assertEquals(academy, luke.getAtLocation());

        scn.MoveCardsToTopOfLSReserveDeck(luke);
        scn.DSPass();
        assertFalse(scn.LSCardActionAvailable(academy, "Deploy Jedi Luke here"));
    }

    /**
     * Plays Alter (draw destiny < ability of Luke = 6) against Blast Door Controls with Skull on top of Reserve Deck.
     * Skull's printed destiny is 4; Disarming Creature (V) makes it 6, so Alter only succeeds if the draw is unmodified.
     */
    private boolean alterCancelsEffect(String lukeOrQuiGonAtAcademy, boolean jediAtAcademy) {
        var scn = GetScenario();
        var academy = scn.GetLSCard("academy");
        var jedi = scn.GetLSCard(lukeOrQuiGonAtAcademy);
        var luke = scn.GetLSCard("luke");
        var skull = scn.GetLSCard("skull");
        var disarming = scn.GetLSCard("disarming");
        var alter = scn.GetLSCard("alter");
        var blastdoor = scn.GetDSCard("blastdoor");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveLocationToTable(academy);
        scn.MoveCardsToLSSideOfTable(disarming);
        scn.MoveCardsToDSSideOfTable(blastdoor);
        scn.MoveCardsToLocation(jediAtAcademy ? academy : site, jedi);
        if (jedi != luke) {
            scn.MoveCardsToLocation(site, luke);
        }
        scn.MoveCardsToLSHand(alter);
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.EnsureForcePile(scn.LS, 6);
        scn.MoveCardsToTopOfLSReserveDeck(skull);

        scn.LSPlayCard(alter);
        if (scn.LSHasCardChoiceAvailable(blastdoor)) {
            scn.LSChooseCard(blastdoor);
        }
        if (scn.LSHasCardChoiceAvailable(luke)) {
            scn.LSChooseCard(luke);
        }
        for (int i = 0; i < 10 && alter.getZone().isInPlay(); i++) {
            scn.PassAllResponses();
        }
        scn.PassAllResponses();
        return blastdoor.getZone() != com.gempukku.swccgo.common.Zone.SIDE_OF_TABLE;
    }

    @Test
    public void JediAcademyAlterDestinyMayNotBeModifiedWhileNonEpisodeIJediHere() {
        assertTrue(alterCancelsEffect("luke", true));
    }

    @Test
    public void JediAcademyAlterDestinyIsModifiedWithoutJediHere() {
        assertFalse(alterCancelsEffect("luke", false));
    }

    @Test
    public void JediAcademyEpisodeIJediHereDoesNotStopAlterDestinyModifiers() {
        assertFalse(alterCancelsEffect("quigon", true));
    }
}
