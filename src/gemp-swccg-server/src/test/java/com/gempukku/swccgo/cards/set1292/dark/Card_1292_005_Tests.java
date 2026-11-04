package com.gempukku.swccgo.cards.set1292.dark;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
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
import static org.junit.Assert.assertTrue;

public class Card_1292_005_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("gold1", "1_141");
                    put("luke", "1_19");
                }},
                new HashMap<>() {{
                    put("novasword", "1292_5");
                    put("tatooine", "1_289");
                    put("trench", "212_5");
                    put("bast", "1_165");
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

    // Durge (1291_6) has no implementation yet; a stand-in with its printed title and stats is used.
    private static final String DURGE_ID = "2000_9292";

    private static class TestDurge extends AbstractAlien {
        TestDurge() {
            super(Side.DARK, 3, 4, 4, 3, 5, "Durge", Uniqueness.UNIQUE, ExpansionSet.PREMIERE, Rarity.R);
            addIcons(Icon.PILOT, Icon.WARRIOR, Icon.SEPARATIST, Icon.EPISODE_I);
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
    public void removeDurgeStub() {
        blueprintMap().remove(DURGE_ID);
    }

    private PhysicalCardImpl createDurge(VirtualTableScenario scn) {
        blueprintMap().put(DURGE_ID, new TestDurge());
        PhysicalCardImpl card = (PhysicalCardImpl) scn.gameState().createPhysicalCard(scn.DS, VirtualTableScenario._cardLibrary, DURGE_ID);
        scn.gameState().addCardToZone(card, Zone.HAND, scn.DS);
        return card;
    }

    @Test
    public void DurgesNovaSwordFighterStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("novasword").getBlueprint();

        assertEquals("Durge's NovaSword Fighter", card.getTitle());
        assertEquals(false, card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(3, card.getDestiny(), scn.epsilon);
        assertEquals(3, card.getDeployCost(), scn.epsilon);
        assertEquals(3, card.getPower(), scn.epsilon);
        assertEquals(4, card.getManeuver(), scn.epsilon);
        assertEquals(5, card.getHyperspeed(), scn.epsilon);
        assertEquals(5, card.getForfeit(), scn.epsilon);
        assertEquals(2, card.getPilotOrPassengerCapacity());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.STARSHIP);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintModelTypeCheck(card, new ArrayList<>() {{
            add(ModelType.SPACE_SUPERIORITY_FIGHTER);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.STARSHIP);
            add(Icon.NAV_COMPUTER);
            add(Icon.SCOMP_LINK);
            add(Icon.SEPARATIST);
            add(Icon.EPISODE_I);
            add(Icon.LEGACY_BLOCK_9);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_9_2, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void DurgesNovaSwordFighterDurgeDeploysMinusOneAboard() {
        var scn = GetScenario();
        var novasword = scn.GetDSCard("novasword");
        var tatooine = scn.GetDSCard("tatooine");

        scn.StartGame();
        scn.MoveLocationToTable(tatooine);
        scn.MoveCardsToLocation(tatooine, novasword);
        var durge = createDurge(scn);
        scn.SkipToDSTurn(Phase.DEPLOY);
        scn.EnsureDSForcePile(6);
        int force = scn.GetDSForcePileCount();

        assertTrue(scn.DSDeployAvailable(durge));
        scn.DSDeployCard(durge);
        scn.DSChooseCard(novasword);
        if (scn.DSDecisionAvailable("capacity slot")) {
            scn.DSChoose("Pilot");
        }
        scn.PassAllResponses();

        assertTrue(scn.IsAboardAsPilot(novasword, durge));
        assertEquals(force - 3, scn.GetDSForcePileCount());
    }

    private void battleAtTatooine(VirtualTableScenario scn, PhysicalCardImpl pilot) {
        var novasword = scn.GetDSCard("novasword");
        var tatooine = scn.GetDSCard("tatooine");
        var gold1 = scn.GetLSCard("gold1");
        var luke = scn.GetLSCard("luke");

        scn.MoveLocationToTable(tatooine);
        scn.MoveCardsToLocation(tatooine, novasword, gold1);
        scn.BoardAsPilot(novasword, pilot);
        scn.BoardAsPilot(gold1, luke);
        scn.SkipToDSTurn(Phase.BATTLE);
        scn.DSActivateForceCheat(2);
        scn.DSInitiateBattle(tatooine);
        scn.PassAllResponses();
    }

    private int destinyDrawsToPower(VirtualTableScenario scn) {
        return scn.game().getModifiersQuerying().getNumDestinyDrawsToTotalPowerOnly(scn.gameState(), scn.DS, false, false);
    }

    private float immunity(VirtualTableScenario scn) {
        return scn.game().getModifiersQuerying().getImmunityToAttritionLessThan(scn.gameState(), scn.GetDSCard("novasword"));
    }

    @Test
    public void DurgesNovaSwordFighterAddsDestinyToPowerWithYourSeparatistCardAboard() {
        var scn = GetScenario();
        scn.StartGame();
        battleAtTatooine(scn, scn.GetDSCard("trench"));

        assertEquals(1, destinyDrawsToPower(scn));
        assertEquals(4, immunity(scn), scn.epsilon);
    }

    @Test
    public void DurgesNovaSwordFighterDoesNotAddDestinyToPowerWithoutSeparatistCardAboard() {
        var scn = GetScenario();
        scn.StartGame();
        battleAtTatooine(scn, scn.GetDSCard("bast"));

        assertEquals(0, destinyDrawsToPower(scn));
        assertEquals(4, immunity(scn), scn.epsilon);
    }

    @Test
    public void DurgesNovaSwordFighterWithDurgeAboardAddsBattleDestinyAndImmunityLessThanSix() {
        var scn = GetScenario();
        scn.StartGame();
        var durge = createDurge(scn);
        battleAtTatooine(scn, durge);
        int withDurge = scn.GetDSBattleDestinyCount();

        var scn2 = GetScenario();
        scn2.StartGame();
        battleAtTatooine(scn2, scn2.GetDSCard("trench"));
        int withTrench = scn2.GetDSBattleDestinyCount();

        assertEquals(withTrench + 1, withDurge);
        // "or one battle destiny if Durge aboard": the destiny to power is replaced, not added
        assertEquals(0, destinyDrawsToPower(scn));
        assertEquals(6, immunity(scn), scn.epsilon);
    }
}
