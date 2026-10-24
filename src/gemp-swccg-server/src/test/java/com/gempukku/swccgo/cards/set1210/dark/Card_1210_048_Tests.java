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
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class Card_1210_048_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("rifle", "1_152");
                    put("luke", "1_19");
                }},
                new HashMap<>() {{
                    put("detachment", "1210_48");
                    put("stormtrooper", "1_194");
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
    public void SendADetachmentDownStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("detachment").getBlueprint();

        assertEquals("Send A Detachment Down", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(4, card.getDestiny(), scn.epsilon);
        assertEquals(CardSubtype.UTINNI, card.getCardSubtype());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.EFFECT);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.EFFECT);
            add(Icon.LEGACY_BLOCK_1);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_1, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void SendADetachmentDownDeploysOnOpponentsStealableWeapon() {
        var scn = GetScenario();
        var detachment = scn.GetDSCard("detachment");
        var rifle = scn.GetLSCard("rifle");
        var luke = scn.GetLSCard("luke");
        var stormtrooper = scn.GetDSCard("stormtrooper");
        var lsSite = scn.GetLSStartingLocation();
        var dsSite = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(lsSite, luke);
        scn.AttachCardsTo(luke, rifle);
        scn.MoveCardsToLocation(dsSite, stormtrooper);
        scn.MoveCardsToDSHand(detachment);
        scn.SkipToDSTurn(Phase.DEPLOY);
        scn.EnsureDSForcePile(4);

        assertTrue(scn.DSCardPlayAvailable(detachment));
        scn.DSPlayCard(detachment);
        if (scn.DSHasCardChoiceAvailable(rifle)) {
            scn.DSChooseCard(rifle);
        }
        if (scn.DSHasCardChoiceAvailable(stormtrooper)) {
            scn.DSChooseCard(stormtrooper);
        }
        scn.PassAllResponses();

        assertEquals(rifle, detachment.getAttachedTo());
    }
}
