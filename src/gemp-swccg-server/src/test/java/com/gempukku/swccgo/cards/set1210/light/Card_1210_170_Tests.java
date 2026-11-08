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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1210_170_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("blaster", "1210_170");
                    put("beckett", "213_42");
                    put("han", "1_11");
                    put("tk422", "7_48");
                }},
                new HashMap<>() {{
                    put("trooper", "1_194");
                    put("mynock", "4_110");
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
    public void HansHeavyBlasterPistolStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("blaster").getBlueprint();

        assertEquals("Han's Heavy Blaster Pistol", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(2, card.getDestiny(), scn.epsilon);
        assertEquals(CardSubtype.CHARACTER, card.getCardSubtype());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.WEAPON);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.BLASTER);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.WEAPON);
            add(Icon.LEGACY_BLOCK_1);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_1, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void HansHeavyBlasterPistolDeploysOnHan() {
        var scn = GetScenario();
        var blaster = scn.GetLSCard("blaster");
        var han = scn.GetLSCard("han");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, han);
        scn.MoveCardsToLSHand(blaster);
        scn.SkipToLSTurn(Phase.DEPLOY);
        assertTrue(scn.LSDeployAvailable(blaster));
        scn.LSDeployCard(blaster);
        scn.LSChooseCard(han);
        scn.PassAllResponses();
        assertTrue(scn.IsAttachedTo(han, blaster));
    }

    @Test
    public void HansHeavyBlasterPistolDoesNotDeployOnTk422() {
        var scn = GetScenario();
        var blaster = scn.GetLSCard("blaster");
        var tk422 = scn.GetLSCard("tk422");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, tk422);
        scn.MoveCardsToLSHand(blaster);
        scn.SkipToLSTurn(Phase.DEPLOY);
        assertFalse(scn.LSDeployAvailable(blaster));
    }

    @Test
    public void HansHeavyBlasterPistolDeploysAsReactToBattleAtHansSite() {
        var scn = GetScenario();
        var blaster = scn.GetLSCard("blaster");
        var han = scn.GetLSCard("han");
        var vader = scn.GetDSCard("vader");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, han, vader);
        scn.MoveCardsToLSHand(blaster);
        scn.SkipToDSTurn(Phase.BATTLE);
        scn.DSInitiateBattle(site);
        assertTrue(scn.LSCardPlayAvailable(blaster));
        scn.LSPlayCard(blaster);
        scn.LSChooseCard(han);
        scn.PassAllResponses();
        assertTrue(scn.IsAttachedTo(han, blaster));
    }

    @Test
    public void HansHeavyBlasterPistolHitsCharacterSetsForfeitToZeroAndOpponentLosesOneForce() {
        var scn = GetScenario();
        var blaster = scn.GetLSCard("blaster");
        var han = scn.GetLSCard("han");
        var trooper = scn.GetDSCard("trooper");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, han, trooper);
        scn.AttachCardsTo(han, blaster);
        scn.LSStartBattleAndSkipToWeaponsSegment(site);
        scn.PrepareLSDestiny(5);
        int dsLife = scn.GetDSLifeForceRemaining();
        assertTrue(scn.LSCardActionAvailable(blaster, "Fire"));
        scn.LSUseCardAction(blaster, "Fire");
        scn.LSChooseCard(trooper);
        scn.PassWeaponFireWithDestinyDraw();
        scn.PassAllResponses();
        scn.DSChooseCard(scn.GetTopOfDSForcePile());
        scn.PassAllResponses();
        assertTrue(trooper.isHit());
        assertEquals(0, scn.GetForfeit(trooper));
        assertEquals(dsLife - 1, scn.GetDSLifeForceRemaining());
    }

    @Test
    public void HansHeavyBlasterPistolMayTargetACreature() {
        var scn = GetScenario();
        var blaster = scn.GetLSCard("blaster");
        var han = scn.GetLSCard("han");
        var mynock = scn.GetDSCard("mynock");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, han, mynock);
        scn.AttachCardsTo(han, blaster);
        scn.LSActivateForceCheat(2);
        scn.SkipToLSTurn(Phase.BATTLE);
        assertTrue(scn.LSCardActionAvailable(site, "attack"));
        scn.LSUseCardAction(site, "attack");
        scn.PassAllResponses();
        scn.PrepareLSDestiny(5);
        assertTrue(scn.LSCardActionAvailable(blaster, "Fire"));
        scn.LSUseCardAction(blaster, "Fire");
        scn.LSChooseCard(mynock);
        scn.PassWeaponFireWithDestinyDraw();
        scn.PassAllResponses();
        assertTrue(mynock.isHit()
                || mynock.getZone() == Zone.LOST_PILE
                || mynock.getZone() == Zone.TOP_OF_LOST_PILE);
    }

    @Test
    public void HansHeavyBlasterPistolFiresDuringControlPhaseUsingOneForce() {
        var scn = GetScenario();
        var blaster = scn.GetLSCard("blaster");
        var han = scn.GetLSCard("han");
        var trooper = scn.GetDSCard("trooper");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, han, trooper);
        scn.AttachCardsTo(han, blaster);
        scn.SkipToLSTurn(Phase.CONTROL);
        assertTrue(scn.AwaitingLSControlPhaseActions());
        int lsForce = scn.GetLSForcePileCount();
        scn.PrepareLSDestiny(5);
        assertTrue(scn.LSCardActionAvailable(blaster, "Fire during control phase"));
        scn.LSUseCardAction(blaster, "Fire during control phase");
        scn.PassCardAndForceUseResponses();
        if (scn.LSHasCardChoiceAvailable(trooper)) {
            scn.LSChooseCard(trooper);
        }
        scn.PassAllResponses();
        assertTrue(trooper.isHit()
                || trooper.getZone() == Zone.LOST_PILE
                || trooper.getZone() == Zone.TOP_OF_LOST_PILE);
        assertEquals(lsForce - 1, scn.GetLSForcePileCount());
    }

    @Test
    public void HansHeavyBlasterPistolGameTextIsOfficialText() {
        var scn = GetScenario();
        assertEquals("Deploy on Han (except TK-422), even as a 'react.' May target a character or creature for free. Draw destiny. Target hit, its forfeit = 0, and opponent loses 1 Force if destiny +1 > defense value. Once during your control phase, may fire using 1 Force.",
                scn.GetLSCard("blaster").getBlueprint().getGameText());
    }

    @Test
    public void HansHeavyBlasterPistolDoesNotDeployOnBeckett() {
        var scn = GetScenario();
        var blaster = scn.GetLSCard("blaster");
        var beckett = scn.GetLSCard("beckett");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, beckett);
        scn.MoveCardsToLSHand(blaster);
        scn.SkipToLSTurn(Phase.DEPLOY);
        assertFalse(scn.LSDeployAvailable(blaster));
    }

    @Test
    public void HansHeavyBlasterPistolMissesWhenDestinyPlusOneIsNotGreaterThanDefenseValue() {
        var scn = GetScenario();
        var blaster = scn.GetLSCard("blaster");
        var han = scn.GetLSCard("han");
        var vader = scn.GetDSCard("vader");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, han, vader);
        scn.AttachCardsTo(han, blaster);
        scn.LSStartBattleAndSkipToWeaponsSegment(site);
        scn.PrepareLSDestiny(5);
        int dsLife = scn.GetDSLifeForceRemaining();
        assertTrue(scn.LSCardActionAvailable(blaster, "Fire"));
        scn.LSUseCardAction(blaster, "Fire");
        scn.LSChooseCard(vader);
        scn.PassWeaponFireWithDestinyDraw();
        scn.PassAllResponses();
        // 5 + 1 is not > Vader's defense value 6
        assertFalse(vader.isHit());
        assertEquals(dsLife, scn.GetDSLifeForceRemaining());
    }

    @Test
    public void HansHeavyBlasterPistolFiresOnlyOnceDuringControlPhaseAndHasNoCancelDestinyAction() {
        var scn = GetScenario();
        var blaster = scn.GetLSCard("blaster");
        var han = scn.GetLSCard("han");
        var trooper = scn.GetDSCard("trooper");
        var vader = scn.GetDSCard("vader");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, han, trooper, vader);
        scn.AttachCardsTo(han, blaster);
        scn.SkipToLSTurn(Phase.CONTROL);
        scn.PrepareLSDestiny(5);
        assertTrue(scn.LSCardActionAvailable(blaster, "Fire during control phase"));
        scn.LSUseCardAction(blaster, "Fire during control phase");
        scn.PassCardAndForceUseResponses();
        if (scn.LSHasCardChoiceAvailable(trooper)) {
            scn.LSChooseCard(trooper);
        }
        scn.PassAllResponses();
        // opponent loses 1 Force from the hit
        if (scn.DSDecisionAvailable("Force to lose")) {
            scn.DSChooseCard(scn.GetTopOfDSForcePile());
            scn.PassAllResponses();
        }
        if (!scn.AwaitingLSControlPhaseActions())
            scn.DSPass();
        assertTrue(scn.AwaitingLSControlPhaseActions());
        assertFalse(scn.LSCardActionAvailable(blaster, "Fire during control phase"));
        assertFalse(scn.LSCardActionAvailable(blaster, "Place in Used Pile to cancel weapon destiny"));
    }
}
