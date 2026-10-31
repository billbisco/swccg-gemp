package com.gempukku.swccgo.cards.set1109.light;

import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Persona;
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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1109_001_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("rifle", "1109_1");
                    put("luke", "1109_13");
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
    public void A280SharpshooterRifleStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("rifle").getBlueprint();
        assertEquals("A280 Sharpshooter Rifle", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.RESTRICTED_2, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(3, card.getDestiny(), scn.epsilon);
        assertEquals(CardSubtype.CHARACTER, card.getCardSubtype());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.WEAPON);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.RIFLE);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.WEAPON);
            add(Icon.ENDOR);
        }});
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_9, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void A280SharpshooterRifleDeploysOnRebelScout() {
        var scn = GetScenario();
        var rifle = scn.GetLSCard("rifle");
        var luke = scn.GetLSCard("luke");
        var site = scn.GetLSStartingLocation();
        scn.StartGame();
        scn.MoveCardsToLocation(site, luke);
        scn.MoveCardsToLSHand(rifle);
        scn.SkipToLSTurn(Phase.DEPLOY);
        assertTrue(scn.LSDeployAvailable(rifle));
        scn.LSDeployCard(rifle);
        scn.LSChooseCard(luke);
        scn.PassAllResponses();
        assertTrue(scn.IsAttachedTo(luke, rifle));
    }

    @Test
    public void A280SharpshooterRifleFiresDuringControlPhaseUsingTwoForce() {
        var scn = GetScenario();
        var rifle = scn.GetLSCard("rifle");
        var luke = scn.GetLSCard("luke");
        var trooper = scn.GetDSCard("trooper");
        var site = scn.GetLSStartingLocation();
        scn.StartGame();
        scn.MoveCardsToLocation(site, luke, trooper);
        scn.AttachCardsTo(luke, rifle);
        scn.SkipToLSTurn(Phase.CONTROL);
        assertTrue(scn.AwaitingLSControlPhaseActions());
        scn.PrepareLSDestiny(5);
        assertTrue(scn.LSCardActionAvailable(rifle, "Fire during control phase"));
        scn.LSUseCardAction(rifle, "Fire during control phase");
        scn.PassCardAndForceUseResponses();
        if (scn.LSHasCardChoiceAvailable(trooper)) {
            scn.LSChooseCard(trooper);
        }
        scn.PassAllResponses();
        assertTrue(trooper.isHit()
                || trooper.getZone() == Zone.LOST_PILE
                || trooper.getZone() == Zone.TOP_OF_LOST_PILE);
    }
}
