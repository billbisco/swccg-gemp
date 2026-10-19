package com.gempukku.swccgo.cards.set1272.light;

import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1272_019_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("shield", "1272_19");
                    put("luke", "1_19");
                }},
                new HashMap<>() {{
                    put("commence", "2_130");
                    put("expand", "1_215");
                    put("db327", "1_285");
                    put("deathstar", "2_143");
                    put("core", "1_283");
                    put("bombs", "4_179");
                    put("bomber", "9_166");
                    put("pilot", "1_180");
                    put("tatooine", "1_289");
                    put("db94", "1_291");
                    put("cantina", "1_290");
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
    public void PlanetaryDefensesStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("shield").getBlueprint();

        assertEquals("Planetary Defenses", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(0, card.getDestiny(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.DEFENSIVE_SHIELD);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.DEFENSIVE_SHIELD);
            add(Icon.EPISODE_I);
            add(Icon.REFLECTIONS_III);
            add(Icon.LEGACY_BLOCK_7);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_7_2, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void PlanetaryDefensesPlaysOnAnySite() {
        var scn = GetScenario();
        var shield = scn.GetLSCard("shield");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLSHand(shield);
        scn.SkipToLSTurn(Phase.DEPLOY);
        assertTrue(scn.LSCardPlayAvailable(shield));
        scn.LSPlayCard(shield);
        assertTrue(scn.LSHasCardChoiceAvailable(site));
        scn.LSChooseCard(site);
        scn.PassAllResponses();
        assertTrue(scn.IsAttachedTo(site, shield));
    }

    @Test
    public void PlanetaryDefensesCancelsCommencePrimaryIgnitionUnlessDockingBay327OnTable() {
        var scn = GetScenario();
        var shield = scn.GetLSCard("shield");
        var commence = scn.GetDSCard("commence");
        var deathstar = scn.GetDSCard("deathstar");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveLocationToTable(deathstar);
        scn.AttachCardsTo(site, shield);
        scn.MoveCardsToDSSideOfTable(commence);
        scn.SkipToLSTurn(Phase.CONTROL);
        assertFalse(commence.getZone().isInPlay());
        assertInZone(Zone.LOST_PILE, commence);
    }

    @Test
    public void PlanetaryDefensesDoesNotCancelCommencePrimaryIgnitionWhenDockingBay327OnTable() {
        var scn = GetScenario();
        var shield = scn.GetLSCard("shield");
        var commence = scn.GetDSCard("commence");
        var deathstar = scn.GetDSCard("deathstar");
        var db327 = scn.GetDSCard("db327");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveLocationToTable(deathstar);
        scn.MoveLocationToTable(db327);
        scn.AttachCardsTo(site, shield);
        scn.MoveCardsToDSSideOfTable(commence);
        scn.SkipToLSTurn(Phase.CONTROL);
        assertTrue(commence.getZone().isInPlay());
    }

    @Test
    public void PlanetaryDefensesMakesDeathStarSitesImmuneToExpandTheEmpire() {
        var scn = GetScenario();
        var shield = scn.GetLSCard("shield");
        var expand = scn.GetDSCard("expand");
        var db327 = scn.GetDSCard("db327");
        var deathstar = scn.GetDSCard("deathstar");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveLocationToTable(deathstar);
        scn.MoveLocationToTable(db327);
        scn.AttachCardsTo(site, shield);
        scn.MoveCardsToDSHand(expand);
        scn.SkipToDSTurn(Phase.DEPLOY);
        assertTrue(scn.DSCardPlayAvailable(expand));
        scn.DSPlayCard(expand);
        assertFalse(scn.DSHasCardChoiceAvailable(db327));
    }

    @Test
    public void PlanetaryDefensesSiteMayNotBeTargetedByProtonBombs() {
        var scn = GetScenario();
        var shield = scn.GetLSCard("shield");
        var bombs = scn.GetDSCard("bombs");
        var bomber = scn.GetDSCard("bomber");
        var pilot = scn.GetDSCard("pilot");
        var tatooine = scn.GetDSCard("tatooine");
        var db94 = scn.GetDSCard("db94");
        var cantina = scn.GetDSCard("cantina");

        scn.StartGame();
        scn.MoveLocationToTable(tatooine);
        scn.MoveLocationToTable(db94);
        scn.MoveLocationToTable(cantina);
        scn.AttachCardsTo(db94, shield);
        scn.MoveCardsToLocation(tatooine, bomber);
        scn.BoardAsPilot(bomber, pilot);
        scn.AttachCardsTo(bomber, bombs);
        scn.SkipToDSTurn(Phase.CONTROL);
        assertTrue(scn.DSForceDrainAvailable(tatooine));
        scn.DSForceDrainAt(tatooine);
        scn.LSPass();
        assertTrue(scn.DSCardActionAvailable(bombs, "collapse"));
        scn.DSUseCardAction(bombs, "collapse");
        assertFalse(scn.DSHasCardChoiceAvailable(db94));
        assertTrue(scn.DSHasCardChoiceAvailable(cantina));
    }
}
