package com.gempukku.swccgo.cards.set1272.light;

import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.PhysicalCardImpl;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1272_028_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("roche", "1272_28");
                    put("kessel", "1_126");
                    put("field", "4_81");
                    put("sanctuary", "4_17");
                    put("corvette", "1_140");
                    put("secondCorvette", "1_140");
                }},
                new HashMap<>() {{
                    put("cpi", "2_130");
                    put("isd", "1_302");
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
    public void RocheVStatsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("roche").getBlueprint();

        assertEquals(Title.Roche, card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(7, card.getParsec());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.LOCATION);
        }});
        assertEquals(CardSubtype.SYSTEM, card.getCardSubtype());
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.LIGHT_FORCE);
            add(Icon.DARK_FORCE);
            add(Icon.SPECIAL_EDITION);
            add(Icon.PLANET);
            add(Icon.LEGACY_BLOCK_7);
        }});
        assertEquals(2, card.getIconCount(Icon.LIGHT_FORCE));
        assertEquals(1, card.getIconCount(Icon.DARK_FORCE));
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_7_2, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void RocheVCommencePrimaryIgnitionMayNotTargetItUnlessOpponentControlsIt() {
        var scn = GetScenario();
        var roche = scn.GetLSCard("roche");
        var cpi = scn.GetDSCard("cpi");
        var isd = scn.GetDSCard("isd");

        //test1: Commence Primary Ignition may not target Roche while opponent does not control it
        //test2: Commence Primary Ignition may target Roche once opponent controls it
        scn.StartGame();
        scn.MoveLocationToTable(roche);
        assertFalse(scn.game().getModifiersQuerying().canBeTargetedBy(scn.gameState(), roche, cpi)); //test1
        scn.MoveCardsToLocation(roche, isd);
        assertTrue(scn.game().getModifiersQuerying().canBeTargetedBy(scn.gameState(), roche, cpi)); //test2
    }

    @Test
    public void RocheVDarkSideForceDrainIsMinusOneHere() {
        var scn = GetScenario();
        var roche = scn.GetLSCard("roche");
        var isd = scn.GetDSCard("isd");

        //test1: Dark Side Force drains for 2 Light Side icons - 1 = 1
        scn.StartGame();
        scn.MoveLocationToTable(roche);
        scn.MoveCardsToLocation(roche, isd);
        scn.SkipToDSTurn(Phase.CONTROL);
        assertTrue(scn.DSForceDrainAvailable(roche));
        scn.DSForceDrainAt(roche);
        scn.PassForceDrainStartResponses();
        assertEquals(1, scn.GetForceDrainTotal()); //test1
    }

    @Test
    public void RocheVCancelsAsteroidSanctuaryAtRelatedSectorWhenDarkSideControlsSystem() {
        //test1: Dark Side controls Roche while Light Side controls the related asteroid sector.
        //test2: A legally deployed Asteroid Sanctuary is canceled by Roche.
        var scn = GetScenario();
        var roche = scn.GetLSCard("roche");
        var field = scn.GetLSCard("field");
        var sanctuary = scn.GetLSCard("sanctuary");

        scn.StartGame();
        scn.MoveLocationToTable(roche);
        PlaceSectorAtSystem(scn, field, roche);
        scn.MoveCardsToLocation(roche, scn.GetDSCard("isd"));
        scn.MoveCardsToLocation(field, scn.GetLSCard("corvette"));
        assertTrue(Filters.controls(scn.DS).accepts(scn.game(), roche)); //test1
        assertTrue(Filters.relatedSector(roche).accepts(scn.game(), field)); //test1
        DeploySanctuary(scn, sanctuary, field);
        assertInZone(Zone.LOST_PILE, sanctuary); //test2
    }

    @Test
    public void RocheVDoesNotCancelAsteroidSanctuaryUnlessDarkSideControlsSystem() {
        //test1: Light Side controls Roche and can legally deploy Sanctuary at its related sector.
        //test2: Sanctuary remains attached because Dark Side does not control Roche.
        var scn = GetScenario();
        var roche = scn.GetLSCard("roche");
        var field = scn.GetLSCard("field");
        var sanctuary = scn.GetLSCard("sanctuary");

        scn.StartGame();
        scn.MoveLocationToTable(roche);
        PlaceSectorAtSystem(scn, field, roche);
        scn.MoveCardsToLocation(roche, scn.GetLSCard("secondCorvette"));
        scn.MoveCardsToLocation(field, scn.GetLSCard("corvette"));
        assertFalse(Filters.controls(scn.DS).accepts(scn.game(), roche)); //test1
        assertTrue(Filters.relatedSector(roche).accepts(scn.game(), field)); //test1
        DeploySanctuary(scn, sanctuary, field);
        assertEquals(field, sanctuary.getAttachedTo()); //test2
        assertInZone(Zone.ATTACHED, sanctuary); //test2
    }

    @Test
    public void RocheVDoesNotCancelAsteroidSanctuaryAtUnrelatedSector() {
        //test1: Dark Side controls Roche, but Sanctuary is deployed at Kessel's asteroid sector.
        //test2: Sanctuary remains attached to the unrelated sector.
        var scn = GetScenario();
        var roche = scn.GetLSCard("roche");
        var kessel = scn.GetLSCard("kessel");
        var field = scn.GetLSCard("field");
        var sanctuary = scn.GetLSCard("sanctuary");

        scn.StartGame();
        scn.MoveLocationToTable(roche);
        scn.MoveLocationToTable(kessel);
        PlaceSectorAtSystem(scn, field, kessel);
        scn.MoveCardsToLocation(roche, scn.GetDSCard("isd"));
        scn.MoveCardsToLocation(field, scn.GetLSCard("corvette"));
        assertTrue(Filters.controls(scn.DS).accepts(scn.game(), roche)); //test1
        assertFalse(Filters.relatedSector(roche).accepts(scn.game(), field)); //test1
        DeploySanctuary(scn, sanctuary, field);
        assertEquals(field, sanctuary.getAttachedTo()); //test2
        assertInZone(Zone.ATTACHED, sanctuary); //test2
    }

    private void PlaceSectorAtSystem(VirtualTableScenario scn, PhysicalCardImpl sector, PhysicalCardImpl system) {
        var placement = scn.gameState().getLocationPlacement(scn.game(), sector, null, null).stream()
                .filter(candidate -> system.getBlueprint().getTitle().equals(candidate.getParentSystem()))
                .findFirst().orElseThrow();
        scn.MoveLocationToTable(sector, placement);
    }

    private void DeploySanctuary(VirtualTableScenario scn, PhysicalCardImpl sanctuary, PhysicalCardImpl field) {
        scn.MoveCardsToLSHand(sanctuary);
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.EnsureLSForcePile(5);
        assertTrue(scn.LSDeployAvailable(sanctuary));
        scn.LSDeployCardAndPassResponses(sanctuary, field);
    }
}
