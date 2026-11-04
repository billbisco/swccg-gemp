package com.gempukku.swccgo.cards.set1292.light;

import com.gempukku.swccgo.cards.AbstractStarfighter;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.PhysicalCardImpl;
import com.gempukku.swccgo.game.SwccgCardBlueprint;
import com.gempukku.swccgo.game.SwccgCardBlueprintLibrary;
import org.junit.After;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1292_009_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("jan", "1292_9");
                    put("gold1", "1_141");
                    put("leia", "1_17");
                    put("trooper", "1_28");
                }},
                new HashMap<>() {{
                    put("motti", "1_164");
                    put("tatooine", "1_289");
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

    // Moldy Crow has no GEMP card; a stand-in starfighter with that title is used.
    private static final String MOLDY_CROW_ID = "2000_9293";

    private static class TestMoldyCrow extends AbstractStarfighter {
        TestMoldyCrow() {
            super(Side.LIGHT, 2, 3, 3, null, 3, 4, 4, "Moldy Crow", Uniqueness.UNIQUE, ExpansionSet.PREMIERE, Rarity.R);
            setPilotCapacity(1);
        }
    }

    @SuppressWarnings("unchecked")
    private static Map<String, SwccgCardBlueprint> blueprintMap() {
        try {
            Field field = SwccgCardBlueprintLibrary.class.getDeclaredField("_blueprintMap");
            field.setAccessible(true);
            return (Map<String, SwccgCardBlueprint>) field.get(VirtualTableScenario._cardLibrary);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    @After
    public void removeMoldyCrowStub() {
        blueprintMap().remove(MOLDY_CROW_ID);
    }

    @Test
    public void JanOrsWithBlasterPistolStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("jan").getBlueprint();

        assertEquals("Jan Ors With Blaster Pistol", card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(2, card.getDestiny(), scn.epsilon);
        assertEquals(2, card.getDeployCost(), scn.epsilon);
        assertEquals(2, card.getPower(), scn.epsilon);
        assertEquals(2, card.getAbility(), scn.epsilon);
        assertEquals(5, card.getForfeit(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.REBEL);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.SCOUNDREL);
            add(Keyword.FEMALE);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.REBEL);
            add(Icon.PILOT);
            add(Icon.WARRIOR);
            add(Icon.PERMANENT_WEAPON);
            add(Icon.LEGACY_BLOCK_9);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_9_2, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void JanOrsWithBlasterPistolAddsTwoToPowerOfAnythingShePilots() {
        var scn = GetScenario();
        var jan = scn.GetLSCard("jan");
        var gold1 = scn.GetLSCard("gold1");
        var tatooine = scn.GetDSCard("tatooine");

        scn.StartGame();
        scn.MoveLocationToTable(tatooine);
        scn.MoveCardsToLocation(tatooine, gold1);
        scn.BoardAsPilot(gold1, jan);
        assertEquals(4, scn.GetPower(gold1), scn.epsilon);
    }

    @Test
    public void JanOrsWithBlasterPistolAddsThreeToPowerOfMoldyCrow() {
        var scn = GetScenario();
        var jan = scn.GetLSCard("jan");
        var tatooine = scn.GetDSCard("tatooine");

        scn.StartGame();
        blueprintMap().put(MOLDY_CROW_ID, new TestMoldyCrow());
        PhysicalCardImpl crow = (PhysicalCardImpl) scn.gameState().createPhysicalCard(scn.LS, VirtualTableScenario._cardLibrary, MOLDY_CROW_ID);
        scn.gameState().addCardToZone(crow, Zone.HAND, scn.LS);
        scn.MoveLocationToTable(tatooine);
        scn.MoveCardsToLocation(tatooine, crow);
        scn.BoardAsPilot(crow, jan);
        assertEquals(6, scn.GetPower(crow), scn.epsilon);
    }

    private void fireBlasterPistolAtMotti(VirtualTableScenario scn, int destiny) {
        var jan = scn.GetLSCard("jan");
        var motti = scn.GetDSCard("motti");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, jan, motti);
        scn.SkipToLSTurn(Phase.BATTLE);
        scn.LSActivateForceCheat(2);
        int force = scn.GetLSForcePileCount();
        scn.LSInitiateBattle(site);
        scn.PassAllResponses();
        assertTrue(scn.AwaitingLSWeaponsSegmentActions());
        assertTrue(scn.LSCardActionAvailable(jan, "Fire"));
        scn.PrepareLSDestiny(destiny);
        scn.LSUseCardAction(jan, "Fire");
        if (scn.LSDecisionAvailable("Choose target")) {
            scn.LSChooseCard(motti);
        }
        scn.PassAllResponses();
        // initiating the battle cost 1 Force; firing is free
        assertEquals(force - 1, scn.GetLSForcePileCount());
    }

    @Test
    public void JanOrsWithBlasterPistolHitsAndForfeitMinusThreeIfDestinyPlusOneMoreThanDefenseValue() {
        var scn = GetScenario();
        var motti = scn.GetDSCard("motti");
        // Admiral Motti: ability (defense value) 3, forfeit 5; destiny 3 + 1 > 3
        fireBlasterPistolAtMotti(scn, 3);
        assertTrue(motti.isHit());
        assertEquals(2, scn.GetForfeit(motti), scn.epsilon);
    }

    @Test
    public void JanOrsWithBlasterPistolMissesIfDestinyPlusOneNotMoreThanDefenseValue() {
        var scn = GetScenario();
        var motti = scn.GetDSCard("motti");
        // destiny 2 + 1 = 3, not > 3
        fireBlasterPistolAtMotti(scn, 2);
        assertFalse(motti.isHit());
        assertEquals(5, scn.GetForfeit(motti), scn.epsilon);
    }

    @Test
    public void JanOrsWithBlasterPistolYourCharactersHereImmuneToTrampleAndYouAreBeaten() {
        var scn = GetScenario();
        var jan = scn.GetLSCard("jan");
        var leia = scn.GetLSCard("leia");
        var trooper = scn.GetLSCard("trooper");
        var motti = scn.GetDSCard("motti");
        var site = scn.GetLSStartingLocation();
        var otherSite = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, jan, leia, motti);
        scn.MoveCardsToLocation(otherSite, trooper);
        var q = scn.game().getModifiersQuerying();
        for (String title : new String[]{Title.Trample, Title.You_Are_Beaten}) {
            assertTrue(q.isImmuneToCardTitle(scn.gameState(), jan, title));
            assertTrue(q.isImmuneToCardTitle(scn.gameState(), leia, title));
            assertFalse(q.isImmuneToCardTitle(scn.gameState(), trooper, title));
            assertFalse(q.isImmuneToCardTitle(scn.gameState(), motti, title));
        }
    }
}
