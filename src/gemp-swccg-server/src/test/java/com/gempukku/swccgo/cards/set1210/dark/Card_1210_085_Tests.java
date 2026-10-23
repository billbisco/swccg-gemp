package com.gempukku.swccgo.cards.set1210.dark;

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

public class Card_1210_085_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("leiapw", "108_2");
                    put("mothma", "107_2");
                    put("luke", "1_19");
                }},
                new HashMap<>() {{
                    put("seeker", "1210_85");
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
    public void LeiaSeekerStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("seeker").getBlueprint();

        assertEquals("Leia Seeker", card.getTitle());
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
            add(Icon.A_NEW_HOPE);
            add(Icon.LEGACY_BLOCK_1);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_1, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void LeiaSeekerDeploysToAnUnoccupiedSiteUsingOneForce() {
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
    public void LeiaSeekerLosesPermanentWeaponLeiaAndItself() {
        var scn = GetScenario();
        var seeker = scn.GetDSCard("seeker");
        var leiapw = scn.GetLSCard("leiapw");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, seeker);
        scn.MoveCardsToLocation(site, leiapw);
        try {
            scn.SkipToDSTurn(Phase.CONTROL);
        } catch (RuntimeException ignored) {
        }
        if (scn.DSGetDecision() != null && scn.DSHasCardChoiceAvailable(leiapw)) {
            scn.DSChooseCard(leiapw);
        }
        scn.PassAllResponses();
        assertInZone(Zone.LOST_PILE, leiapw);
        assertInZone(Zone.LOST_PILE, seeker);
    }

    @Test
    public void LeiaSeekerLosesASenatorAndItself() {
        var scn = GetScenario();
        var seeker = scn.GetDSCard("seeker");
        var mothma = scn.GetLSCard("mothma");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, seeker);
        scn.MoveCardsToLocation(site, mothma);
        try {
            scn.SkipToDSTurn(Phase.CONTROL);
        } catch (RuntimeException ignored) {
        }
        if (scn.DSGetDecision() != null && scn.DSHasCardChoiceAvailable(mothma)) {
            scn.DSChooseCard(mothma);
        }
        scn.PassAllResponses();
        assertInZone(Zone.LOST_PILE, mothma);
        assertInZone(Zone.LOST_PILE, seeker);
    }

    @Test
    public void LeiaSeekerDoesNotMakeLukeLost() {
        var scn = GetScenario();
        var seeker = scn.GetDSCard("seeker");
        var luke = scn.GetLSCard("luke");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, seeker, luke);
        scn.SkipToDSTurn(Phase.CONTROL);
        assertAtLocation(site, luke);
        assertAtLocation(site, seeker);
    }
}
