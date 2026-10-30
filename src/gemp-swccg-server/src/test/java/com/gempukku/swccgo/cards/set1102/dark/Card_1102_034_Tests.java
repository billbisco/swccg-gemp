package com.gempukku.swccgo.cards.set1102.dark;

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

import static com.gempukku.swccgo.framework.Assertions.assertAtLocation;
import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class Card_1102_034_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("hanpw", "108_1");
                    put("han", "1_11");
                    put("theron", "1210_113");
                }},
                new HashMap<>() {{
                    put("seeker", "1102_34");
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
    public void HanSeekerStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("seeker").getBlueprint();

        assertEquals("Han Seeker", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNRESTRICTED, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(3, card.getDestiny(), scn.epsilon);
        assertEquals(CardSubtype.AUTOMATED, card.getCardSubtype());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.WEAPON);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.SEEKER);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.WEAPON);
        }});
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_2, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void HanSeekerDeploysToAnUnoccupiedSiteUsingOneForce() {
        var scn = GetScenario();
        var seeker = scn.GetDSCard("seeker");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToDSHand(seeker);
        scn.EnsureDSForcePile(2);
        scn.SkipToDSTurn(Phase.DEPLOY);
        int force = scn.GetDSForcePileCount();
        assertTrue(scn.DSDeployAvailable(seeker));
        scn.DSDeployCardAndPassResponses(seeker, site);
        assertAtLocation(site, seeker);
        assertEquals(force - 1, scn.GetDSForcePileCount());
    }

    @Test
    public void HanSeekerLosesPermanentWeaponHanAndItself() {
        var scn = GetScenario();
        var seeker = scn.GetDSCard("seeker");
        var hanpw = scn.GetLSCard("hanpw");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, seeker);
        scn.MoveCardsToLocation(site, hanpw);
        try {
            scn.SkipToDSTurn(Phase.CONTROL);
        } catch (RuntimeException ignored) {
        }
        if (scn.DSGetDecision() != null && scn.DSHasCardChoiceAvailable(hanpw)) {
            scn.DSChooseCard(hanpw);
        }
        scn.PassAllResponses();
        assertInZone(Zone.LOST_PILE, hanpw);
        assertInZone(Zone.LOST_PILE, seeker);
    }

    @Test
    public void HanSeekerLosesASmugglerOfAbilityLessThanThreeAndItself() {
        var scn = GetScenario();
        var seeker = scn.GetDSCard("seeker");
        var theron = scn.GetLSCard("theron");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, seeker);
        scn.MoveCardsToLocation(site, theron);
        try {
            scn.SkipToDSTurn(Phase.CONTROL);
        } catch (RuntimeException ignored) {
        }
        if (scn.DSGetDecision() != null && scn.DSHasCardChoiceAvailable(theron)) {
            scn.DSChooseCard(theron);
        }
        scn.PassAllResponses();
        assertInZone(Zone.LOST_PILE, theron);
        assertInZone(Zone.LOST_PILE, seeker);
    }

    @Test
    public void HanSeekerDoesNotMakePremiereHanLost() {
        var scn = GetScenario();
        var seeker = scn.GetDSCard("seeker");
        var han = scn.GetLSCard("han");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, seeker, han);
        scn.SkipToDSTurn(Phase.CONTROL);
        assertAtLocation(site, han);
        assertAtLocation(site, seeker);
    }
}
