package com.gempukku.swccgo.cards.set1210.light;

import com.gempukku.swccgo.common.CardSubtype;
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

public class Card_1210_172_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("rifle", "1210_172");
                    put("luke", "1_19");
                    put("owen", "1_22");
                    put("crell", "6_2");
                    put("binocs", "1_35");
                }},
                new HashMap<>() {{
                    put("trooper", "1_194");
                    put("bantha", "1_307");
                    put("mynock", "4_110");
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
    public void LukesHuntingRifleStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("rifle").getBlueprint();

        assertEquals("Luke's Hunting Rifle", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(5, card.getDestiny(), scn.epsilon);
        assertEquals(CardSubtype.CHARACTER, card.getCardSubtype());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.WEAPON);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.RIFLE);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.WEAPON);
            add(Icon.A_NEW_HOPE);
            add(Icon.LEGACY_BLOCK_1);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_1, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void LukesHuntingRifleDeploysOnLukeUsingOneForce() {
        var scn = GetScenario();
        var rifle = scn.GetLSCard("rifle");
        var luke = scn.GetLSCard("luke");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, luke);
        scn.MoveCardsToLSHand(rifle);
        scn.EnsureLSForcePile(2);
        scn.SkipToLSTurn(Phase.DEPLOY);
        int force = scn.GetLSForcePileCount();
        assertTrue(scn.LSDeployAvailable(rifle));
        scn.LSDeployCard(rifle);
        scn.LSChooseCard(luke);
        scn.PassAllResponses();
        assertTrue(scn.IsAttachedTo(luke, rifle));
        assertEquals(force - 1, scn.GetLSForcePileCount());
    }

    @Test
    public void LukesHuntingRifleDeploysOnOwenLars() {
        var scn = GetScenario();
        var rifle = scn.GetLSCard("rifle");
        var owen = scn.GetLSCard("owen");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, owen);
        scn.MoveCardsToLSHand(rifle);
        scn.EnsureLSForcePile(2);
        scn.SkipToLSTurn(Phase.DEPLOY);
        assertTrue(scn.LSDeployAvailable(rifle));
        scn.LSDeployCard(rifle);
        scn.LSChooseCard(owen);
        scn.PassAllResponses();
        assertTrue(scn.IsAttachedTo(owen, rifle));
    }

    @Test
    public void LukesHuntingRifleDeploysOnArdonCrell() {
        var scn = GetScenario();
        var rifle = scn.GetLSCard("rifle");
        var crell = scn.GetLSCard("crell");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, crell);
        scn.MoveCardsToLSHand(rifle);
        scn.EnsureLSForcePile(2);
        scn.SkipToLSTurn(Phase.DEPLOY);
        assertTrue(scn.LSDeployAvailable(rifle));
        scn.LSDeployCard(rifle);
        scn.LSChooseCard(crell);
        scn.PassAllResponses();
        assertTrue(scn.IsAttachedTo(crell, rifle));
    }

    @Test
    public void LukesHuntingRifleResetsCharacterPowerToZeroUsingDevicesAsX() {
        var scn = GetScenario();
        var rifle = scn.GetLSCard("rifle");
        var luke = scn.GetLSCard("luke");
        var binocs = scn.GetLSCard("binocs");
        var trooper = scn.GetDSCard("trooper");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, luke, trooper);
        scn.AttachCardsTo(luke, rifle);
        scn.AttachCardsTo(luke, binocs);
        scn.LSActivateForceCheat(2);
        scn.SkipToLSTurn(Phase.BATTLE);
        scn.LSInitiateBattle(site);
        scn.PassAllResponses();
        scn.PrepareLSDestiny(1);
        assertTrue(scn.LSCardActionAvailable(rifle, "Fire"));
        scn.LSUseCardAction(rifle, "Fire");
        scn.LSChooseCard(trooper);
        scn.PassAllResponses();
        assertEquals(0, scn.GetPower(trooper));
    }

    @Test
    public void LukesHuntingRifleDoesNotResetPowerWhenDestinyPlusXDoesNotExceedDefense() {
        var scn = GetScenario();
        var rifle = scn.GetLSCard("rifle");
        var luke = scn.GetLSCard("luke");
        var trooper = scn.GetDSCard("trooper");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, luke, trooper);
        scn.AttachCardsTo(luke, rifle);
        scn.LSActivateForceCheat(2);
        scn.SkipToLSTurn(Phase.BATTLE);
        scn.LSInitiateBattle(site);
        scn.PassAllResponses();
        int power = scn.GetPower(trooper);
        scn.PrepareLSDestiny(1);
        assertTrue(scn.LSCardActionAvailable(rifle, "Fire"));
        scn.LSUseCardAction(rifle, "Fire");
        scn.LSChooseCard(trooper);
        scn.PassAllResponses();
        assertEquals(power, scn.GetPower(trooper));
        assertFalse(trooper.getZone() == Zone.LOST_PILE);
    }

    @Test
    public void LukesHuntingRifleMakesACreatureLost() {
        var scn = GetScenario();
        var rifle = scn.GetLSCard("rifle");
        var luke = scn.GetLSCard("luke");
        var mynock = scn.GetDSCard("mynock");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, luke, mynock);
        scn.AttachCardsTo(luke, rifle);
        scn.LSActivateForceCheat(2);
        scn.SkipToLSTurn(Phase.BATTLE);
        assertTrue(scn.LSCardActionAvailable(site, "attack"));
        scn.LSUseCardAction(site, "attack");
        scn.PassAllResponses();
        scn.PrepareLSDestiny(5);
        assertTrue(scn.LSCardActionAvailable(rifle, "Fire"));
        scn.LSUseCardAction(rifle, "Fire");
        scn.LSChooseCard(mynock);
        scn.PassAllResponses();
        assertInZone(Zone.LOST_PILE, mynock);
    }
}
