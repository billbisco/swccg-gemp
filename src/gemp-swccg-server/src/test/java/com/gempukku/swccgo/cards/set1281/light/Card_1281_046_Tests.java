package com.gempukku.swccgo.cards.set1281.light;

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

public class Card_1281_046_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("blaster", "1281_46");
                    put("han", "1281_27");
                    put("badfeeling", "1_91");
                    put("luke", "1_19");
                }},
                new HashMap<>() {{
                    put("trooper", "1_194");
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
    public void HansBlasterSoUncivilizedStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("blaster").getBlueprint();

        assertEquals("Han's Blaster, So Uncivilized", card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(3, card.getDestiny(), scn.epsilon);
        assertEquals(CardSubtype.CHARACTER, card.getCardSubtype());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.WEAPON);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.BLASTER);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.WEAPON);
            add(Icon.LEGACY_BLOCK_8);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_8_1, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void HansBlasterSoUncivilizedDeploysOnHan() {
        var scn = GetScenario();
        var blaster = scn.GetLSCard("blaster");
        var han = scn.GetLSCard("han");
        var luke = scn.GetLSCard("luke");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, han, luke);
        scn.MoveCardsToLSHand(blaster);
        scn.SkipToLSTurn(Phase.DEPLOY);

        assertTrue(scn.LSDeployAvailable(blaster));
        scn.LSDeployCard(blaster);
        assertTrue(scn.LSHasCardChoiceAvailable(han));
        assertFalse(scn.LSHasCardChoiceAvailable(luke));
        scn.LSChooseCard(han);
        scn.PassAllResponses();
        assertTrue(scn.IsAttachedTo(han, blaster));
    }

    @Test
    public void HansBlasterSoUncivilizedCancelsIveGotABadFeelingAboutThis() {
        var scn = GetScenario();
        var blaster = scn.GetLSCard("blaster");
        var han = scn.GetLSCard("han");
        var badfeeling = scn.GetLSCard("badfeeling");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, han);
        scn.AttachCardsTo(han, blaster);
        scn.MoveCardsToLSHand(badfeeling);
        scn.SkipToLSTurn(Phase.BATTLE);

        assertFalse(scn.LSPlayUsedInterruptAvailable(badfeeling));
    }

    @Test
    public void HansBlasterSoUncivilizedHitsForfeitZeroLosesOneForceAndPowerPlusOne() {
        var scn = GetScenario();
        var blaster = scn.GetLSCard("blaster");
        var han = scn.GetLSCard("han");
        var trooper = scn.GetDSCard("trooper");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, han, trooper);
        scn.AttachCardsTo(han, blaster);
        scn.LSActivateForceCheat(2);
        scn.LSStartBattleAndSkipToWeaponsSegment(site);
        scn.PrepareLSDestiny(5);

        int hanPower = scn.GetPower(han);
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
        assertEquals(hanPower + 1, scn.GetPower(han));
    }
}
