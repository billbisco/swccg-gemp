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
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.game.PhysicalCardImpl;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1220_127_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("alderaan", "1220_127");
                    put("leia", "1_17");
                    put("dice", "1_7");
                    put("luke", "1_19");
                    put("pilot", "1_27");
                    put("scout", "1230_75");
                }},
                new HashMap<>() {{
                    put("motti", "1_164");
                    put("vader", "1_168");
                    put("baskol", "12_98");
                    put("baskol2", "12_98");
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
    public void LeiaOfAlderaanStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("alderaan").getBlueprint();

        assertEquals("Leia Of Alderaan", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(3, card.getDestiny(), scn.epsilon);
        assertEquals(CardSubtype.NORMAL, card.getCardSubtype());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.EFFECT);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.DEPLOYS_ON_CHARACTERS);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.CLOUD_CITY);
            add(Icon.EFFECT);
            add(Icon.LEGACY_BLOCK_2);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_2, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void LeiaOfAlderaanGameTextIsOfficialText() {
        var scn = GetScenario();
        assertEquals("Deploy on Leia. At same location, your unique (•) Rebels are deploy -1 and their immunity to attrition is +1. Dark Side Republic characters are deploy +2 and forfeit -2. (Immune to Alter.)",
                scn.GetLSCard("alderaan").getBlueprint().getGameText());
        assertTrue(scn.GetLSCard("alderaan").getBlueprint().isImmuneToCardTitle(Title.Alter));
    }

    @Test
    public void LeiaOfAlderaanDeploysOnLeiaButNotOnAnotherFemale() {
        var scn = GetScenario();
        var alderaan = scn.GetLSCard("alderaan");
        var leia = scn.GetLSCard("leia");
        var dice = scn.GetLSCard("dice");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, leia, dice);
        int before = scn.GetPower(leia);
        scn.MoveCardsToLSHand(alderaan);
        scn.SkipToLSTurn(Phase.DEPLOY);
        assertTrue(scn.LSCardPlayAvailable(alderaan));
        scn.LSPlayCard(alderaan);
        assertTrue(scn.LSHasCardChoiceAvailable(leia));
        assertTrue(scn.LSHasCardChoiceNotAvailable(dice));
        scn.LSChooseCard(leia);
        scn.PassAllResponses();
        assertEquals(leia, alderaan.getAttachedTo());
        // no longer power +2 on Leia
        assertEquals(before, scn.GetPower(leia));
    }

    @Test
    public void LeiaOfAlderaanYourUniqueRebelsDeployMinusOneToSameLocationOnly() {
        var scn = GetScenario();
        var alderaan = scn.GetLSCard("alderaan");
        var leia = scn.GetLSCard("leia");
        var luke = scn.GetLSCard("luke");
        var pilot = scn.GetLSCard("pilot");
        var site = scn.GetLSStartingLocation();
        var other = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, leia);
        float lukeBase = deployCostTo(scn, luke, site);
        float pilotBase = deployCostTo(scn, pilot, site);
        float lukeOther = deployCostTo(scn, luke, other);
        scn.AttachCardsTo(leia, alderaan);
        assertEquals(lukeBase - 1, deployCostTo(scn, luke, site), scn.epsilon);
        assertEquals(pilotBase, deployCostTo(scn, pilot, site), scn.epsilon);
        assertEquals(lukeOther, deployCostTo(scn, luke, other), scn.epsilon);
    }

    @Test
    public void LeiaOfAlderaanAddsOneToImmunityOfYourUniqueRebelsAtSameLocation() {
        var scn = GetScenario();
        var alderaan = scn.GetLSCard("alderaan");
        var leia = scn.GetLSCard("leia");
        var scout = scn.GetLSCard("scout");
        var pilot = scn.GetLSCard("pilot");
        var site = scn.GetLSStartingLocation();
        var other = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, leia, scout);
        float before = immunity(scn, scout);
        assertTrue(before > 0);
        scn.AttachCardsTo(leia, alderaan);
        assertEquals(before + 1, immunity(scn, scout), scn.epsilon);
        // a non-unique Rebel is not affected
        scn.MoveCardsToLocation(site, pilot);
        assertEquals(0, immunity(scn, pilot), scn.epsilon);
        // not at a different location
        scn.MoveCardsToLocation(other, scout);
        assertEquals(before, immunity(scn, scout), scn.epsilon);
    }

    @Test
    public void LeiaOfAlderaanDarkSideRepublicCharactersAreDeployPlusTwoAndForfeitMinusTwo() {
        var scn = GetScenario();
        var alderaan = scn.GetLSCard("alderaan");
        var leia = scn.GetLSCard("leia");
        var baskol = scn.GetDSCard("baskol");
        var baskol2 = scn.GetDSCard("baskol2");
        var motti = scn.GetDSCard("motti");
        var site = scn.GetLSStartingLocation();
        var dsSite = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, leia);
        scn.MoveCardsToLocation(dsSite, baskol, motti);
        float deployBase = deployCostTo(scn, baskol2, dsSite);
        float mottiDeployBase = scn.GetDeployCost(motti);
        int forfeitBase = scn.GetForfeit(baskol);
        int mottiForfeit = scn.GetForfeit(motti);
        scn.AttachCardsTo(leia, alderaan);
        assertEquals(deployBase + 2, deployCostTo(scn, baskol2, dsSite), scn.epsilon);
        assertEquals(forfeitBase - 2, scn.GetForfeit(baskol));
        assertEquals(mottiForfeit, scn.GetForfeit(motti));
        assertEquals(mottiDeployBase, scn.GetDeployCost(motti), scn.epsilon);
    }

    @Test
    public void LeiaOfAlderaanHasNoLeaderCancelAction() {
        var scn = GetScenario();
        var alderaan = scn.GetLSCard("alderaan");
        var leia = scn.GetLSCard("leia");
        var motti = scn.GetDSCard("motti");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, leia, motti);
        scn.AttachCardsTo(leia, alderaan);
        scn.SkipToLSTurn(Phase.BATTLE);
        scn.LSInitiateBattle(site);
        scn.PassAllResponses();
        assertFalse(scn.LSCardActionAvailable(alderaan, "Cancel a leader's game text"));
    }

    private float deployCostTo(VirtualTableScenario scn, PhysicalCardImpl card, PhysicalCardImpl target) {
        return scn.game().getModifiersQuerying().getDeployCost(
                scn.gameState(), card, card, target, false, null, false, 0, null, false);
    }

    private float immunity(VirtualTableScenario scn, PhysicalCardImpl card) {
        return scn.game().getModifiersQuerying().getImmunityToAttritionLessThan(scn.gameState(), card);
    }
}
