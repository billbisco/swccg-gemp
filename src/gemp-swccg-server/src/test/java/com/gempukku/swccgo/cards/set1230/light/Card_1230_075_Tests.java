package com.gempukku.swccgo.cards.set1230.light;

import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.PhysicalCardImpl;
import com.gempukku.swccgo.common.Phase;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1230_075_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("lukeScout", "1230_75");
                    put("forest", "8_72");
                    put("blaster", "1_152");
                    put("blaster2", "1_152");
                    put("pilot", "1_27");
                    put("xwing", "1_145");
                    put("kessel", "1_126");
                    put("filler", "1_5");
                    put("han", "1_11");
                }},
                new HashMap<>() {{
                    put("trooper", "1_194");
                    put("motti", "1_164");
                    put("dsblaster", "1_317");
                    put("vader", "1_168");
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
    public void StatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("lukeScout").getBlueprint();
        assertEquals("Luke Skywalker, Rebel Scout", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(1, card.getDestiny(), scn.epsilon);
        assertEquals(6, card.getDeployCost(), scn.epsilon);
        assertEquals(6, card.getPower(), scn.epsilon);
        assertEquals(6, card.getAbility(), scn.epsilon);
        assertEquals(7, card.getForfeit(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.REBEL);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.SCOUT);
        }});
        scn.BlueprintPersonaCheck(card, new ArrayList<>() {{
            add(Persona.LUKE);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.REBEL);
            add(Icon.PILOT);
            add(Icon.WARRIOR);
            add(Icon.DEATH_STAR_II);
            add(Icon.REFLECTIONS_II);
            add(Icon.LEGACY_BLOCK_3);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_3, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void GameTextIsOfficialText() {
        var scn = GetScenario();
        assertEquals("Adds 2 to power of anything he pilots. Deploys -2 to a forest. Luke's weapon destiny draws are +1. At same site, opponent must first use 1 Force to fire a weapon. During opponent's draw phase, if alone on Endor, may retrieve 1 Force. Immune to attrition < 5.",
                scn.GetLSCard("lukeScout").getBlueprint().getGameText());
    }

    @Test
    public void AddsTwoToPowerOfAnythingHePilots() {
        var scn = GetScenario();
        var luke = scn.GetLSCard("lukeScout");
        var xwing = scn.GetLSCard("xwing");
        var kessel = scn.GetLSCard("kessel");

        scn.StartGame();
        scn.MoveLocationToTable(kessel);
        scn.MoveCardsToLocation(kessel, xwing);
        scn.BoardAsPilot(xwing, luke);
        assertEquals(3 + 2, scn.GetPower(xwing));
    }

    @Test
    public void DeploysMinusTwoToAForestOnly() {
        var scn = GetScenario();
        var luke = scn.GetLSCard("lukeScout");
        var forest = scn.GetLSCard("forest");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveLocationToTable(forest);
        assertEquals(4, deployCostTo(scn, luke, forest), scn.epsilon);
        assertEquals(6, deployCostTo(scn, luke, site), scn.epsilon);
    }

    @Test
    public void LukesWeaponDestinyDrawsArePlusOne() {
        var scn = GetScenario();
        var luke = scn.GetLSCard("lukeScout");
        var blaster = scn.GetLSCard("blaster");
        var motti = scn.GetDSCard("motti");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, luke, motti);
        scn.AttachCardsTo(luke, blaster);
        scn.LSActivateForceCheat(4);
        scn.LSStartBattleAndSkipToWeaponsSegment(site);
        scn.PrepareLSDestiny(3);
        assertTrue(scn.LSCardActionAvailable(blaster, "Fire"));
        scn.LSUseCardAction(blaster, "Fire");
        scn.LSChooseCard(motti);
        scn.PassAllResponses();
        // destiny 3 + 1 > defense value 3
        assertTrue(motti.isHit());
    }

    @Test
    public void AnotherCharactersWeaponDestinyIsNotPlusOne() {
        var scn = GetScenario();
        var luke = scn.GetLSCard("lukeScout");
        var pilot = scn.GetLSCard("han");
        var blaster2 = scn.GetLSCard("blaster2");
        var motti = scn.GetDSCard("motti");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, luke, pilot, motti);
        scn.AttachCardsTo(pilot, blaster2);
        scn.LSActivateForceCheat(4);
        scn.LSStartBattleAndSkipToWeaponsSegment(site);
        scn.PrepareLSDestiny(3);
        assertTrue(scn.LSCardActionAvailable(blaster2, "Fire"));
        scn.LSUseCardAction(blaster2, "Fire");
        scn.LSChooseCard(motti);
        scn.PassAllResponses();
        assertFalse(motti.isHit());
    }

    @Test
    public void OpponentMustUseOneForceToFireAWeaponAtSameSite() {
        var scn = GetScenario();
        var luke = scn.GetLSCard("lukeScout");
        var trooper = scn.GetDSCard("trooper");
        var dsblaster = scn.GetDSCard("dsblaster");
        var site = scn.GetLSStartingLocation();
        var other = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(other, trooper);
        scn.AttachCardsTo(trooper, dsblaster);
        scn.MoveCardsToLocation(site, luke);
        assertEquals(0, scn.game().getModifiersQuerying().getExtraForceRequiredToFireWeapon(scn.gameState(), dsblaster));
        scn.MoveCardsToLocation(site, trooper);
        assertEquals(1, scn.game().getModifiersQuerying().getExtraForceRequiredToFireWeapon(scn.gameState(), dsblaster));
    }

    @Test
    public void RetrievesOneForceDuringOpponentsDrawPhaseIfAloneOnEndor() {
        var scn = GetScenario();
        var luke = scn.GetLSCard("lukeScout");
        var forest = scn.GetLSCard("forest");
        var filler = scn.GetLSCard("filler");

        scn.StartGame();
        scn.MoveLocationToTable(forest);
        scn.MoveCardsToLocation(forest, luke);
        scn.MoveCardsToTopOfLSLostPile(filler);
        scn.SkipToDSTurn(Phase.DRAW);
        if (scn.AwaitingDSDrawPhaseActions())
            scn.DSPass();
        assertTrue(scn.LSCardActionAvailable(luke, "Retrieve 1 Force"));
        int lost = scn.GetLSLostPileCount();
        scn.LSUseCardAction(luke, "Retrieve 1 Force");
        scn.PassAllResponses();
        assertEquals(lost - 1, scn.GetLSLostPileCount());
    }

    @Test
    public void DoesNotRetrieveIfNotAloneOnEndor() {
        var scn = GetScenario();
        var luke = scn.GetLSCard("lukeScout");
        var pilot = scn.GetLSCard("pilot");
        var forest = scn.GetLSCard("forest");
        var filler = scn.GetLSCard("filler");

        scn.StartGame();
        scn.MoveLocationToTable(forest);
        scn.MoveCardsToLocation(forest, luke, pilot);
        scn.MoveCardsToTopOfLSLostPile(filler);
        scn.SkipToDSTurn(Phase.DRAW);
        if (scn.AwaitingDSDrawPhaseActions())
            scn.DSPass();
        assertFalse(scn.LSCardActionAvailable(luke, "Retrieve 1 Force"));
    }

    @Test
    public void DoesNotRetrieveIfAloneButNotOnEndor() {
        var scn = GetScenario();
        var luke = scn.GetLSCard("lukeScout");
        var filler = scn.GetLSCard("filler");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, luke);
        scn.MoveCardsToTopOfLSLostPile(filler);
        scn.SkipToDSTurn(Phase.DRAW);
        if (scn.AwaitingDSDrawPhaseActions())
            scn.DSPass();
        assertFalse(scn.LSCardActionAvailable(luke, "Retrieve 1 Force"));
    }

    @Test
    public void IsImmuneToAttritionLessThanFiveAndHasNoDarkJediPowerBonus() {
        var scn = GetScenario();
        var luke = scn.GetLSCard("lukeScout");
        var vader = scn.GetDSCard("vader");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, luke, vader);
        assertEquals(5, scn.game().getModifiersQuerying().getImmunityToAttritionLessThan(scn.gameState(), luke), scn.epsilon);
        assertEquals(6, scn.GetPower(luke));
    }

    private float deployCostTo(VirtualTableScenario scn, PhysicalCardImpl card, PhysicalCardImpl target) {
        return scn.game().getModifiersQuerying().getDeployCost(
                scn.gameState(), card, card, target, false, null, false, 0, null, false);
    }
}
