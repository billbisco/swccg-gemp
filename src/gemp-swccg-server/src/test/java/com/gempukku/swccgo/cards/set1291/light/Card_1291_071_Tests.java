package com.gempukku.swccgo.cards.set1291.light;

import com.gempukku.swccgo.common.CardSubtype;
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
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static com.gempukku.swccgo.framework.Assertions.assertAtLocation;
import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1291_071_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("atrt", "1291_71");
                    put("cannon", "1220_168");
                    put("hothcannon", "3_74");
                    put("clonepilot", "1116_8");
                    put("luke", "1_19");
                }},
                new HashMap<>() {{
                    put("trooper", "1_194");
                    put("docking", "1_291");
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
    public void ATRTStatsAndIconsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("atrt").getBlueprint();

        assertEquals("AT-RT", card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNRESTRICTED, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(3, card.getDestiny(), scn.epsilon);
        assertEquals(2, card.getDeployCost(), scn.epsilon);
        assertEquals(3, card.getPower(), scn.epsilon);
        assertEquals(3, card.getManeuver(), scn.epsilon);
        assertEquals(3, card.getLandspeed(), scn.epsilon);
        assertEquals(3, card.getForfeit(), scn.epsilon);
        assertEquals(1, card.getPilotCapacity());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.VEHICLE);
        }});
        assertEquals(CardSubtype.COMBAT, card.getCardSubtype());
        scn.BlueprintModelTypeCheck(card, new ArrayList<>() {{
            add(ModelType.AT_RT);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.VEHICLE);
            add(Icon.CLONE_ARMY);
            add(Icon.PILOT);
            add(Icon.EPISODE_I);
            add(Icon.LEGACY_BLOCK_9);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_9_1, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void ATRTPermanentPilotProvidesAbilityOfTwoAndIsSuspendedByAddedPilot() {
        var scn = GetScenario();
        var atrt = scn.GetLSCard("atrt");
        var luke = scn.GetLSCard("luke");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, atrt);
        assertEquals(2, scn.GetLSAbilityAtLocation(site));
        scn.BoardAsPilot(atrt, luke);
        assertEquals(4, scn.GetLSAbilityAtLocation(site));
    }

    @Test
    public void ATRTMayMoveAsAReact() {
        var scn = GetScenario();
        var atrt = scn.GetLSCard("atrt");
        var luke = scn.GetLSCard("luke");
        var trooper = scn.GetDSCard("trooper");
        var docking = scn.GetDSCard("docking");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveLocationToTable(docking);
        assertTrue(scn.IsAdjacentTo(site, docking));
        scn.MoveCardsToLocation(site, atrt);
        scn.MoveCardsToLocation(docking, luke, trooper);
        scn.SkipToDSTurn(Phase.BATTLE);
        scn.DSInitiateBattle(docking);
        assertTrue(scn.LSCardActionAvailable(atrt, "react"));
        scn.LSUseCardAction(atrt, "react");
        if (scn.LSHasCardChoiceAvailable(docking)) {
            scn.LSChooseCard(docking);
        }
        scn.PassAllResponses();
        assertAtLocation(docking, atrt);
    }

    @Test
    public void ATRTHothWeaponsMayTreatT47AsATRT() {
        var scn = GetScenario();
        var atrt = scn.GetLSCard("atrt");
        var cannon = scn.GetLSCard("cannon");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, atrt);
        scn.MoveCardsToLSHand(cannon);
        scn.SkipToLSTurn(Phase.DEPLOY);
        assertTrue(scn.LSDeployAvailable(cannon));
        scn.LSDeployCard(cannon);
        if (scn.LSHasCardChoiceAvailable(atrt)) {
            scn.LSChooseCard(atrt);
        }
        scn.PassAllResponses();
        assertTrue(scn.IsAttachedTo(atrt, cannon));
    }

    @Test
    public void ATRTDecipherDualLaserCannonMayAlsoDeployOnATRT() {
        var scn = GetScenario();
        var atrt = scn.GetLSCard("atrt");
        var hothcannon = scn.GetLSCard("hothcannon");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, atrt);
        scn.MoveCardsToLSHand(hothcannon);
        scn.SkipToLSTurn(Phase.DEPLOY);
        assertTrue(scn.LSDeployAvailable(hothcannon));
    }

    @Test
    public void ATRTDualLaserCannonHasNoTargetWithoutATRTOrT47() {
        var scn = GetScenario();
        var cannon = scn.GetLSCard("cannon");
        var luke = scn.GetLSCard("luke");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, luke);
        scn.MoveCardsToLSHand(cannon);
        scn.SkipToLSTurn(Phase.DEPLOY);
        assertFalse(scn.LSDeployAvailable(cannon));
    }

    @Test
    public void ATRTDualLaserCannonFiresFromATRT() {
        var scn = GetScenario();
        var atrt = scn.GetLSCard("atrt");
        var cannon = scn.GetLSCard("cannon");
        var trooper = scn.GetDSCard("trooper");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, atrt, trooper);
        scn.AttachCardsTo(atrt, cannon);
        scn.SkipToLSTurn(Phase.BATTLE);
        scn.LSInitiateBattle(site);
        scn.PassAllResponses();
        assertTrue(scn.LSCardActionAvailable(cannon, "Fire"));
        scn.PrepareLSDestiny(1);
        scn.LSUseCardAction(cannon, "Fire");
        scn.LSChooseCard(trooper);
        scn.PassWeaponFireWithDestinyDraw();
        scn.PassAllResponses();
        assertTrue(trooper.isHit());
    }

    @Test
    public void ATRTWhenDeployedMayDeployDualLaserCannonAboardFromReserveDeck() {
        var scn = GetScenario();
        var atrt = scn.GetLSCard("atrt");
        var cannon = scn.GetLSCard("cannon");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLSHand(atrt);
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.MoveCardsToTopOfLSReserveDeck(cannon);
        scn.LSDeployCard(atrt);
        if (scn.LSHasCardChoiceAvailable(site)) {
            scn.LSChooseCard(site);
        }
        scn.PassForceUseResponses();
        scn.PassResponses("PLAY - Optional responses");
        assertTrue(scn.LSDecisionAvailable("just deployed"));
        assertTrue(scn.LSCardActionAvailable(atrt, "Dual Laser Cannon"));
        scn.LSUseCardAction(atrt, "Dual Laser Cannon");
        scn.LSChooseCard(cannon);
        scn.PassAllResponses();
        assertTrue(scn.IsAttachedTo(atrt, cannon));
    }
}
