package com.gempukku.swccgo.cards.set1281.dark;

import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Persona;
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

public class Card_1281_025_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("trooper", "1_28");
                    put("luke", "1_19");
                }},
                new HashMap<>() {{
                    put("saber", "1281_25");
                    put("mara", "110_10");
                    put("blaster", "1_317");
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
    public void MaraJadesLightsaberVStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("saber").getBlueprint();

        assertEquals("Mara Jade's Lightsaber", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(1, card.getDestiny(), scn.epsilon);
        assertEquals(CardSubtype.CHARACTER, card.getCardSubtype());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.WEAPON);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.LIGHTSABER);
        }});
        scn.BlueprintPersonaCheck(card, new ArrayList<>() {{
            add(Persona.MARA_JADES_LIGHTSABER);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.WEAPON);
            add(Icon.LEGACY_BLOCK_8);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_8_1, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void MaraJadesLightsaberVDeploysOnlyOnMaraJade() {
        var scn = GetScenario();
        var saber = scn.GetDSCard("saber");
        var mara = scn.GetDSCard("mara");
        var stormtrooper = scn.GetDSCard("stormtrooper");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, mara, stormtrooper);
        scn.MoveCardsToDSHand(saber);
        scn.SkipToDSTurn(Phase.DEPLOY);
        assertTrue(scn.DSDeployAvailable(saber));
        scn.DSDeployCard(saber);
        assertTrue(scn.DSHasCardChoiceAvailable(mara));
        assertFalse(scn.DSHasCardChoiceAvailable(stormtrooper));
        scn.DSChooseCard(mara);
        scn.PassAllResponses();
        assertTrue(scn.IsAttachedTo(mara, saber));
    }

    @Test
    public void MaraJadesLightsaberVLetsMaraUseABlasterAndThisWeaponDuringBattle() {
        var scn = GetScenario();
        var saber = scn.GetDSCard("saber");
        var blaster = scn.GetDSCard("blaster");
        var mara = scn.GetDSCard("mara");
        var trooper = scn.GetLSCard("trooper");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, mara, trooper);
        scn.AttachCardsTo(mara, saber);
        scn.AttachCardsTo(mara, blaster);
        int before = scn.game().getModifiersQuerying().numWeaponsAllowedToUse(scn.gameState(), mara, false);
        scn.DSActivateForceCheat(2);
        scn.DSStartBattleAndSkipToWeaponsSegment(site);
        assertEquals(before + 1, scn.game().getModifiersQuerying().numWeaponsAllowedToUse(scn.gameState(), mara, false));
    }

    @Test
    public void MaraJadesLightsaberVGivesNoExtraWeaponWithoutABlaster() {
        var scn = GetScenario();
        var saber = scn.GetDSCard("saber");
        var mara = scn.GetDSCard("mara");
        var trooper = scn.GetLSCard("trooper");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, mara, trooper);
        scn.AttachCardsTo(mara, saber);
        int before = scn.game().getModifiersQuerying().numWeaponsAllowedToUse(scn.gameState(), mara, false);
        scn.DSActivateForceCheat(2);
        scn.DSStartBattleAndSkipToWeaponsSegment(site);
        assertEquals(before, scn.game().getModifiersQuerying().numWeaponsAllowedToUse(scn.gameState(), mara, false));
    }

    @Test
    public void MaraJadesLightsaberVMayAddOneToForceDrainWherePresent() {
        var scn = GetScenario();
        var saber = scn.GetDSCard("saber");
        var mara = scn.GetDSCard("mara");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, mara);
        scn.AttachCardsTo(mara, saber);
        scn.SkipToDSTurn(Phase.CONTROL);
        int lsLife = scn.GetLSLifeForceRemaining();
        int drain = (int) scn.game().getModifiersQuerying().getForceDrainAmount(scn.gameState(), site, scn.DS);
        scn.DSForceDrainAt(site);
        assertTrue(Vb81RestTestSupport.passUntil(scn, () -> scn.DSCardActionAvailable(saber, "Add 1 to Force drain")));
        scn.DSUseCardAction(saber, "Add 1 to Force drain");
        scn.PassAllResponses();
        Vb81RestTestSupport.payForceLossFromReserveDeck(scn);
        assertEquals(lsLife - drain - 1, scn.GetLSLifeForceRemaining());
    }

    @Test
    public void MaraJadesLightsaberVHitsAndZeroesForfeitIfTotalOfTwoDestinyExceedsDefenseValue() {
        var scn = GetScenario();
        var saber = scn.GetDSCard("saber");
        var mara = scn.GetDSCard("mara");
        var trooper = scn.GetLSCard("trooper");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, mara, trooper);
        scn.AttachCardsTo(mara, saber);
        scn.DSActivateForceCheat(2);
        scn.DSStartBattleAndSkipToWeaponsSegment(site);
        scn.PrepareDSDestiny(1);
        scn.PrepareDSDestiny(1);

        assertTrue(scn.DSCardActionAvailable(saber, "Fire"));
        scn.DSUseCardAction(saber, "Fire");
        scn.DSChooseCard(trooper);
        scn.PassWeaponFireWithDestinyDraw(2);
        scn.PassAllResponses();

        assertTrue(trooper.isHit());
        assertEquals(0, scn.GetForfeit(trooper));
    }

    @Test
    public void MaraJadesLightsaberVMissesIfTotalDestinyIsNotGreaterThanDefenseValue() {
        var scn = GetScenario();
        var saber = scn.GetDSCard("saber");
        var mara = scn.GetDSCard("mara");
        var luke = scn.GetLSCard("luke");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, mara, luke);
        scn.AttachCardsTo(mara, saber);
        scn.DSActivateForceCheat(2);
        scn.DSStartBattleAndSkipToWeaponsSegment(site);
        scn.PrepareDSDestiny(2);
        scn.PrepareDSDestiny(2);

        scn.DSUseCardAction(saber, "Fire");
        scn.DSChooseCard(luke);
        scn.PassWeaponFireWithDestinyDraw(2);
        scn.PassAllResponses();

        assertFalse(luke.isHit());
    }
}
