package com.gempukku.swccgo.cards.set1220.light;

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

public class Card_1220_168_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("cannon", "1220_168");
                    put("rogue", "3_66");
                    put("luke", "1_19");
                }},
                new HashMap<>() {{
                    put("stormtrooper", "1_194");
                    put("bantha", "1_307");
                    put("sandcrawler", "1_309");
                    put("vader", "1_168");
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
    public void DualLaserCannonStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("cannon").getBlueprint();

        assertEquals("Dual Laser Cannon", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNRESTRICTED, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(3, card.getDestiny(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.WEAPON);
        }});
        assertEquals(CardSubtype.VEHICLE, card.getCardSubtype());
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.LASER_CANNON);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.WEAPON);
            add(Icon.HOTH);
            add(Icon.LEGACY_BLOCK_2);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_2, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void DualLaserCannonDeploysOnAT47ForZeroForce() {
        var scn = GetScenario();
        var cannon = scn.GetLSCard("cannon");
        var rogue = scn.GetLSCard("rogue");
        var luke = scn.GetLSCard("luke");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, rogue);
        scn.BoardAsPilot(rogue, luke);
        scn.MoveCardsToLSHand(cannon);
        scn.SkipToLSTurn(Phase.DEPLOY);
        int usedBefore = scn.GetLSUsedPileCount();
        assertTrue(scn.LSDeployAvailable(cannon));
        scn.LSDeployCard(cannon);
        assertTrue(scn.LSHasCardChoiceAvailable(rogue));
        scn.LSChooseCard(rogue);
        scn.PassAllResponses();
        assertEquals(usedBefore, scn.GetLSUsedPileCount());
        assertTrue(scn.IsAttachedTo(rogue, cannon));
    }

    @Test
    public void DualLaserCannonHitsCharacterWhenDestinyPlusTwoExceedsDefenseValueAndResetsForfeitToZero() {
        var scn = GetScenario();
        var cannon = scn.GetLSCard("cannon");
        var rogue = scn.GetLSCard("rogue");
        var luke = scn.GetLSCard("luke");
        var stormtrooper = scn.GetDSCard("stormtrooper");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, rogue, stormtrooper);
        scn.BoardAsPilot(rogue, luke);
        scn.AttachCardsTo(rogue, cannon);
        scn.SkipToLSTurn(Phase.BATTLE);
        scn.LSInitiateBattle(site);
        scn.PassAllResponses();
        assertTrue(scn.AwaitingLSWeaponsSegmentActions());
        assertTrue(scn.LSCardActionAvailable(cannon, "Fire"));
        scn.PrepareLSDestiny(1);
        scn.LSUseCardAction(cannon, "Fire");
        scn.LSChooseCard(stormtrooper);
        scn.PassWeaponFireWithDestinyDraw();
        scn.PassAllResponses();
        assertTrue(stormtrooper.isHit());
        assertEquals(0, scn.GetForfeit(stormtrooper));
    }

    @Test
    public void DualLaserCannonDoesNotHitCharacterWhenDestinyPlusTwoDoesNotExceedDefenseValue() {
        var scn = GetScenario();
        var cannon = scn.GetLSCard("cannon");
        var rogue = scn.GetLSCard("rogue");
        var luke = scn.GetLSCard("luke");
        var vader = scn.GetDSCard("vader");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, rogue, vader);
        scn.BoardAsPilot(rogue, luke);
        scn.AttachCardsTo(rogue, cannon);
        scn.SkipToLSTurn(Phase.BATTLE);
        scn.LSInitiateBattle(site);
        scn.PassAllResponses();
        scn.PrepareLSDestiny(0);
        scn.LSUseCardAction(cannon, "Fire");
        scn.LSChooseCard(vader);
        scn.PassWeaponFireWithDestinyDraw();
        scn.PassAllResponses();
        assertFalse(vader.isHit());
    }

    @Test
    public void DualLaserCannonHitsVehicleWhenDestinyPlusPilotAbilityExceedsDefenseValue() {
        var scn = GetScenario();
        var cannon = scn.GetLSCard("cannon");
        var rogue = scn.GetLSCard("rogue");
        var luke = scn.GetLSCard("luke");
        var sandcrawler = scn.GetDSCard("sandcrawler");
        var stormtrooper = scn.GetDSCard("stormtrooper");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, rogue, sandcrawler, stormtrooper);
        scn.BoardAsPilot(rogue, luke);
        scn.AttachCardsTo(rogue, cannon);
        scn.SkipToLSTurn(Phase.BATTLE);
        scn.LSInitiateBattle(site);
        scn.PassAllResponses();
        assertTrue(scn.LSCardActionAvailable(cannon, "Fire"));
        scn.PrepareLSDestiny(1);
        scn.LSUseCardAction(cannon, "Fire");
        scn.LSChooseCard(sandcrawler);
        scn.PassWeaponFireWithDestinyDraw();
        scn.PassAllResponses();
        assertTrue(sandcrawler.isHit());
    }
}
