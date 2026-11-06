package com.gempukku.swccgo.cards.set1230.light;

import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.Zone;
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
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static com.gempukku.swccgo.framework.Assertions.assertInZone;

public class Card_1230_118_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(new HashMap<>() {{

            put("torps", "1230_118");
            put("xwing", "1_146");
            put("heroic", "1230_113");

            put("han", "1_11");
            put("corvette", "1_140");
            put("forest", "8_72");
        }}, new HashMap<>() {{
            put("devastator", "1_301");
            put("tie", "1_304"); }},
        30, 30, StartingSetup.DefaultLSSpaceSystem, StartingSetup.DefaultDSSpaceSystem,
        StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
        StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test
    public void EnhancedProtonTorpedoesStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("torps").getBlueprint();

        assertEquals("Enhanced Proton Torpedoes", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNRESTRICTED, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(3, card.getDestiny(), scn.epsilon);
        assertEquals(CardSubtype.STARSHIP, card.getCardSubtype());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.WEAPON);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.PROTON_TORPEDOES);
            add(Keyword.STARSHIP_WEAPON_THAT_DEPLOYS_ON_STARFIGHTERS);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.DEATH_STAR_II);
            add(Icon.WEAPON);
            add(Icon.LEGACY_BLOCK_3);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_3, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void EnhancedProtonTorpedoesDeployOnYourStarfighterForOneForce() {
        var scn = GetScenario();
        scn.StartGame();
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), scn.GetLSCard("xwing"));
        scn.MoveCardsToLSHand(scn.GetLSCard("torps"));
        scn.EnsureLSForcePile(5);
        scn.SkipToLSTurn(Phase.DEPLOY);
        int force = scn.GetLSForcePileCount();
        scn.LSDeployCard(scn.GetLSCard("torps"));
        scn.LSChooseCard(scn.GetLSCard("xwing"));
        scn.PassAllResponses();
        assertEquals(force - 1, scn.GetLSForcePileCount());
        assertEquals(scn.GetLSCard("xwing"), scn.GetLSCard("torps").getAttachedTo());
    }

    @Test
    public void EnhancedProtonTorpedoesCannotDeployOnCapitalShipOrOpponentsStarfighter() {
        var scn = GetScenario();
        scn.StartGame();
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), scn.GetLSCard("corvette"), scn.GetDSCard("tie"));
        scn.MoveCardsToLSHand(scn.GetLSCard("torps"));
        scn.EnsureLSForcePile(5);
        scn.SkipToLSTurn(Phase.DEPLOY);
        assertTrue(scn.AwaitingLSDeployPhaseActions());
        assertFalse(scn.LSDeployAvailable(scn.GetLSCard("torps")));
    }

    @Test
    public void EnhancedProtonTorpedoesCannotDeployWithoutTheOneForceCost() {
        var scn = GetScenario();
        scn.StartGame();
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), scn.GetLSCard("xwing"));
        scn.MoveCardsToLSHand(scn.GetLSCard("torps"), scn.GetLSCard("forest"));
        scn.SkipToLSTurn(Phase.CONTROL);
        scn.DrainLSForcePileTo(0);
        scn.SkipToPhase(Phase.DEPLOY);
        assertTrue(scn.AwaitingLSDeployPhaseActions());
        assertTrue(scn.LSDeployAvailable(scn.GetLSCard("forest")));
        assertFalse(scn.LSDeployAvailable(scn.GetLSCard("torps")));
    }

    @Test
    public void EnhancedProtonTorpedoesFireForFreeWithoutHeroicBoostOnOrdinaryStarfighter() {
        var scn = FireReady(false);
        scn.PrepareLSDestiny(4);
        int force = scn.GetLSForcePileCount();
        assertTrue(scn.LSDecisionAvailable("Optional responses"));
        assertFalse(scn.LSCardActionAvailable(scn.GetLSCard("torps"), "Use 1 Force to add 3"));
        scn.PassAllResponses();
        assertFalse(scn.GetDSCard("devastator").isHit());
        assertEquals(force, scn.GetLSForcePileCount());
        assertInZone(Zone.USED_PILE, scn.GetLSDestiny(4));
        assertTrue(scn.AwaitingLSWeaponsSegmentActions() || scn.AwaitingDSWeaponsSegmentActions());
    }

    @Test
    public void EnhancedProtonTorpedoesHeroicBoostIsPaidBeforeDrawingAndHitsWithSeven() {
        var scn = FireReady(true);
        scn.PrepareLSDestiny(4);
        int force = scn.GetLSForcePileCount();
        int used = scn.GetLSUsedPileCount();
        assertInZone(Zone.RESERVE_DECK, scn.GetLSDestiny(4));
        assertTrue(scn.LSCardActionAvailable(scn.GetLSCard("torps"), "Use 1 Force to add 3"));
        scn.LSUseCardAction(scn.GetLSCard("torps"), "Use 1 Force to add 3");
        scn.PassAllResponses();
        assertTrue(scn.GetDSCard("devastator").isHit());
        assertEquals(force - 1, scn.GetLSForcePileCount());
        assertEquals(used + 2, scn.GetLSUsedPileCount());
        assertInZone(Zone.USED_PILE, scn.GetLSDestiny(4));
        assertTrue(scn.AwaitingLSWeaponsSegmentActions() || scn.AwaitingDSWeaponsSegmentActions());
    }

    @Test
    public void EnhancedProtonTorpedoesModifiedDestinyEqualToArmorDoesNotHit() {
        var scn = FireReady(true);
        scn.PrepareLSDestiny(3);
        int force = scn.GetLSForcePileCount();
        scn.LSUseCardAction(scn.GetLSCard("torps"), "Use 1 Force to add 3");
        scn.PassAllResponses();
        assertFalse(scn.GetDSCard("devastator").isHit());
        assertEquals(force - 1, scn.GetLSForcePileCount());
        assertInZone(Zone.USED_PILE, scn.GetLSDestiny(3));
        assertTrue(scn.AwaitingLSWeaponsSegmentActions() || scn.AwaitingDSWeaponsSegmentActions());
    }

    @Test
    public void EnhancedProtonTorpedoesHeroicBoostMayBeDeclined() {
        var scn = FireReady(true);
        scn.PrepareLSDestiny(4);
        int force = scn.GetLSForcePileCount();
        assertTrue(scn.LSCardActionAvailable(scn.GetLSCard("torps"), "Use 1 Force to add 3"));
        scn.PassAllResponses();
        assertFalse(scn.GetDSCard("devastator").isHit());
        assertEquals(force, scn.GetLSForcePileCount());
        assertInZone(Zone.USED_PILE, scn.GetLSDestiny(4));
    }

    @Test
    public void EnhancedProtonTorpedoesCannotBoostWithNoAvailableForce() {
        var scn = FireReady(true, true);
        scn.PrepareLSDestiny(4);
        assertTrue(scn.LSDecisionAvailable("Optional responses"));
        assertFalse(scn.LSCardActionAvailable(scn.GetLSCard("torps"), "Use 1 Force to add 3"));
        scn.PassAllResponses();
        assertFalse(scn.GetDSCard("devastator").isHit());
        assertEquals(0, scn.GetLSForcePileCount());
        assertInZone(Zone.USED_PILE, scn.GetLSDestiny(4));
    }

    private VirtualTableScenario FireReady(boolean heroic) { return FireReady(heroic, false); }
    private VirtualTableScenario FireReady(boolean heroic, boolean noForce) {
        var scn = GetScenario();
        scn.StartGame();
        var ship = scn.GetLSCard(heroic ? "heroic" : "xwing");
        var system = scn.GetLSStartingLocation();
        scn.MoveCardsToLocation(system, ship, scn.GetDSCard("devastator"));
        if (heroic) scn.BoardAsPilot(ship, scn.GetLSCard("han"));
        scn.AttachCardsTo(ship, scn.GetLSCard("torps"));
        scn.EnsureLSForcePile(5);
        scn.SkipToLSTurn(Phase.BATTLE);
        scn.LSInitiateBattle(system);
        scn.PassAllResponses();
        if (noForce) scn.DrainLSForcePileTo(0);
        assertTrue(scn.LSCardActionAvailable(scn.GetLSCard("torps"), "Fire"));
        scn.LSUseCardAction(scn.GetLSCard("torps"), "Fire");
        scn.LSChooseCard(scn.GetDSCard("devastator"));
        assertTrue(scn.DSDecisionAvailable("Optional responses"));
        scn.DSPass();
        assertTrue(scn.LSDecisionAvailable("Optional responses"));
        return scn;
    }
}
