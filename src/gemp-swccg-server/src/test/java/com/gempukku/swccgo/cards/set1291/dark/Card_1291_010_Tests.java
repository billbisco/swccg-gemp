package com.gempukku.swccgo.cards.set1291.dark;

import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1291_010_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1_19"); // defense value 4
                }},
                new HashMap<>() {{
                    put("cape", "1291_10");
                    put("vader", "1_168"); // ability 6, warrior
                    put("nute", "1291_5"); // ability 4
                    put("motti", "1_164"); // ability 3
                    put("rifle", "1_312"); // Blaster Rifle: target hit if destiny +1 > defense value
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
    public void MaulsCapeStatsAndIconsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("cape").getBlueprint();
        assertEquals("Maul's Cape", card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(5, card.getDestiny(), scn.epsilon);
        assertEquals(CardSubtype.NORMAL, card.getCardSubtype());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.EFFECT);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.EPISODE_I);
            add(Icon.EFFECT);
            add(Icon.LEGACY_BLOCK_9);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_9_1, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void MaulsCapeDeploysOnlyOnYourCharacterOfAbilityMoreThan3() {
        var scn = GetScenario();
        var cape = scn.GetDSCard("cape");
        var nute = scn.GetDSCard("nute");
        var motti = scn.GetDSCard("motti");
        var site = scn.GetDSStartingLocation();
        scn.StartGame();
        scn.MoveCardsToLocation(site, nute, motti);
        scn.MoveCardsToHand(cape);
        scn.SkipToDSTurn(Phase.DEPLOY);
        assertTrue(scn.DSDeployAvailable(cape));
        scn.DSDeployCard(cape);
        assertTrue(scn.DSHasCardChoiceAvailable(nute));
        assertFalse(scn.DSHasCardChoiceAvailable(motti));
        scn.DSChooseCard(nute);
        scn.PassAllResponses();
        assertTrue(scn.IsAttachedTo(nute, cape));
    }

    @Test
    public void MaulsCapeCharacterGainsScoutAndImmunities() {
        var scn = GetScenario();
        var cape = scn.GetDSCard("cape");
        var nute = scn.GetDSCard("nute");
        var site = scn.GetDSStartingLocation();
        scn.StartGame();
        scn.MoveCardsToLocation(site, nute);
        var mq = scn.game().getModifiersQuerying();
        assertFalse(scn.HasKeyword(nute, Keyword.SCOUT));
        assertFalse(mq.isImmuneToCardTitle(scn.gameState(), nute, Title.Clash_Of_Sabers));
        assertEquals(0, mq.getImmunityToAttritionLessThan(scn.gameState(), nute), scn.epsilon);
        scn.AttachCardsTo(nute, cape);
        assertTrue(scn.HasKeyword(nute, Keyword.SCOUT));
        assertTrue(mq.isImmuneToCardTitle(scn.gameState(), nute, Title.Clash_Of_Sabers));
        assertEquals(5, mq.getImmunityToAttritionLessThan(scn.gameState(), nute), scn.epsilon);
    }

    @Test
    public void MaulsCapeCharacterWeaponDestinyDrawsArePlus1() {
        var scn = GetScenario();
        var cape = scn.GetDSCard("cape");
        var vader = scn.GetDSCard("vader");
        var rifle = scn.GetDSCard("rifle");
        var luke = scn.GetLSCard("luke");
        var site = scn.GetDSStartingLocation();
        scn.StartGame();
        scn.MoveCardsToLocation(site, vader, luke);
        scn.AttachCardsTo(vader, rifle);
        scn.AttachCardsTo(vader, cape);
        scn.SkipToDSTurn(Phase.BATTLE);
        scn.DSInitiateBattle(site);
        scn.PassAllResponses();
        assertTrue(scn.AwaitingDSWeaponsSegmentActions());
        // destiny 3 + 1 (rifle) = 4 is not > Luke's defense value 4; the Cape's +1 makes it 5
        scn.PrepareDSDestiny(3);
        scn.DSUseCardAction(rifle, "Fire");
        scn.DSChooseCard(luke);
        scn.PassAllResponses();
        assertTrue(luke.isHit());
    }
}
