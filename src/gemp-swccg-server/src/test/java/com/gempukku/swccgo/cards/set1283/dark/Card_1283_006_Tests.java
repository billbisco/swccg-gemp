package com.gempukku.swccgo.cards.set1283.dark;

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

import static com.gempukku.swccgo.framework.Assertions.assertAtLocation;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1283_006_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1_19");
                }},
                new HashMap<>() {{
                    put("ommni", "1283_6");
                    put("techmor", "2_105"); // Tech Mo'r
                    put("umpass", "7_209"); // "Each of your other musicians at same site is power +2"
                    put("dodo", "7_176"); // "peek at top X cards ... X = number of other musicians at same site"
                    put("lynme", "7_188"); // "Subtracts 1 from deploy cost of Bounty hunters at same site"
                    put("lirin", "2_094"); // 'cover charge'
                    put("akrev", "7_164");
                    put("barquin", "7_166");
                    put("djas", "1_171"); // bounty hunter
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
    public void ModalNodesOmmniBoxStatsAndIconsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("ommni").getBlueprint();
        assertEquals("The Modal Nodes' Ommni Box", card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(2, card.getDestiny(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.DEVICE);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.DEVICE);
            add(Icon.LEGACY_BLOCK_8);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_8_3, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void ModalNodesOmmniBoxDeploysFreeOnTechMor() {
        //test1: deploys on Tech Mo'r without using Force
        var scn = GetScenario();
        var ommni = scn.GetDSCard("ommni");
        var techmor = scn.GetDSCard("techmor");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, techmor);
        scn.MoveCardsToDSHand(ommni);
        scn.SkipToDSTurn(Phase.DEPLOY);
        int force = scn.GetDSForcePileCount();

        scn.DSDeployCard(ommni);
        if (scn.DSHasCardChoiceAvailable(techmor)) {
            scn.DSChooseCard(techmor);
        }
        scn.PassAllResponses();
        assertEquals(techmor, ommni.getAttachedTo()); //test1
        assertEquals(force, scn.GetDSForcePileCount()); //test1
    }

    @Test
    public void ModalNodesOmmniBoxUsesTwoForceToDeployOnYourMusicianOrSite() {
        //test1: 2 Force to deploy on your musician
        //test2: a site is also a valid target
        var scn = GetScenario();
        var ommni = scn.GetDSCard("ommni");
        var umpass = scn.GetDSCard("umpass");
        var site = scn.GetDSStartingLocation();
        var lsSite = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, umpass);
        scn.MoveCardsToDSHand(ommni);
        scn.SkipToDSTurn(Phase.DEPLOY);
        int force = scn.GetDSForcePileCount();

        scn.DSDeployCard(ommni);
        assertTrue(scn.DSHasCardChoiceAvailable(lsSite)); //test2
        scn.DSChooseCard(umpass);
        scn.PassAllResponses();
        assertEquals(umpass, ommni.getAttachedTo()); //test1
        assertEquals(force - 2, scn.GetDSForcePileCount()); //test1
    }

    @Test
    public void ModalNodesOmmniBoxMakesYourOtherMusiciansHereHeroic() {
        //test1: your musicians here not carrying it are Heroic
        //test2: the musician carrying it is not
        //test3: musicians elsewhere are not
        var scn = GetScenario();
        var ommni = scn.GetDSCard("ommni");
        var techmor = scn.GetDSCard("techmor");
        var umpass = scn.GetDSCard("umpass");
        var dodo = scn.GetDSCard("dodo");
        var site = scn.GetDSStartingLocation();
        var lsSite = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, techmor, umpass);
        scn.MoveCardsToLocation(lsSite, dodo);
        scn.AttachCardsTo(techmor, ommni);

        assertTrue(scn.HasKeyword(umpass, Keyword.HEROIC)); //test1
        assertFalse(scn.HasKeyword(techmor, Keyword.HEROIC)); //test2
        assertFalse(scn.HasKeyword(dodo, Keyword.HEROIC)); //test3
    }

    @Test
    public void ModalNodesOmmniBoxLetsUmpassStayPumpMusiciansAtBattlegroundSites() {
        //test1: without Ommni Box, Umpass-stay pumps only musicians at same site
        //test2: with it, musicians at another battleground site are power +2
        var scn = GetScenario();
        var ommni = scn.GetDSCard("ommni");
        var umpass = scn.GetDSCard("umpass");
        var techmor = scn.GetDSCard("techmor");
        var dodo = scn.GetDSCard("dodo");
        var site = scn.GetDSStartingLocation();
        var lsSite = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(lsSite, dodo);
        float dodoPower = scn.GetPower(dodo);
        scn.MoveCardsToLocation(site, umpass, techmor);
        assertEquals(dodoPower, scn.GetPower(dodo), scn.epsilon); //test1

        scn.AttachCardsTo(techmor, ommni);
        assertEquals(dodoPower + 2, scn.GetPower(dodo), scn.epsilon); //test2
    }

    @Test
    public void ModalNodesOmmniBoxLetsDodoCountMusiciansAtBattlegroundSites() {
        //test1: without Ommni Box, X = other musicians at same site
        //test2: with it, X may be other musicians at battleground sites
        var scn = GetScenario();
        var ommni = scn.GetDSCard("ommni");
        var dodo = scn.GetDSCard("dodo");
        var techmor = scn.GetDSCard("techmor");
        var umpass = scn.GetDSCard("umpass");
        var akrev = scn.GetDSCard("akrev");
        var site = scn.GetDSStartingLocation();
        var lsSite = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, dodo, techmor);
        scn.MoveCardsToLocation(lsSite, umpass, akrev);
        scn.SkipToDSTurn(Phase.CONTROL);
        assertTrue(scn.DSCardActionAvailable(dodo, "Peak at top 1 cards")); //test1

        scn.AttachCardsTo(techmor, ommni);
        scn.SkipToDSTurn(Phase.CONTROL);
        assertTrue(scn.DSCardActionAvailable(dodo, "Peak at top 3 cards")); //test2
    }

    @Test
    public void ModalNodesOmmniBoxLetsLynMeReduceBountyHuntersAtBattlegroundSites() {
        //test1: a bounty hunter deploys -1 to another battleground site
        var scn = GetScenario();
        var ommni = scn.GetDSCard("ommni");
        var lynme = scn.GetDSCard("lynme");
        var djas = scn.GetDSCard("djas");
        var site = scn.GetDSStartingLocation();
        var lsSite = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, lynme);
        scn.AttachCardsTo(lynme, ommni);
        scn.MoveCardsToDSHand(djas);
        scn.SkipToDSTurn(Phase.DEPLOY);
        scn.EnsureDSForcePile(8);
        int force = scn.GetDSForcePileCount();
        int cost = scn.GetDeployCost(djas);

        scn.DSDeployCard(djas);
        scn.DSChooseCard(lsSite);
        scn.PassAllResponses();
        assertAtLocation(lsSite, djas);
        assertEquals(force - (cost - 1), scn.GetDSForcePileCount()); //test1
    }

    @Test
    public void ModalNodesOmmniBoxLimitsCoverChargesToThreeForOpponentOnly() {
        //test1: opponent's cover charge is limited to 3 (4 other musicians present)
        //test2: your characters pay no cover charge
        var scn = GetScenario();
        var ommni = scn.GetDSCard("ommni");
        var lirin = scn.GetDSCard("lirin");
        var techmor = scn.GetDSCard("techmor");
        var umpass = scn.GetDSCard("umpass");
        var dodo = scn.GetDSCard("dodo");
        var akrev = scn.GetDSCard("akrev");
        var trooper = scn.GetDSCard("trooper");
        var luke = scn.GetLSCard("luke");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, lirin, techmor, umpass, dodo, akrev);
        scn.AttachCardsTo(techmor, ommni);
        scn.MoveCardsToLSHand(luke);
        scn.MoveCardsToDSHand(trooper);

        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.EnsureLSForcePile(8);
        int lsForce = scn.GetLSForcePileCount();
        int lukeCost = scn.GetDeployCost(luke);
        scn.LSDeployCard(luke);
        scn.LSChooseCard(site);
        scn.PassAllResponses();
        assertAtLocation(site, luke);
        assertEquals(lsForce - (lukeCost + 3), scn.GetLSForcePileCount()); //test1

        scn.SkipToDSTurn(Phase.DEPLOY);
        scn.EnsureDSForcePile(10);
        int dsForce = scn.GetDSForcePileCount();
        int trooperCost = scn.GetDeployCost(trooper);
        scn.DSDeployCard(trooper);
        scn.DSChooseCard(site);
        scn.PassAllResponses();
        assertAtLocation(site, trooper);
        assertEquals(dsForce - trooperCost, scn.GetDSForcePileCount()); //test2
    }

    @Test
    public void ModalNodesOmmniBoxLeavesCoverChargeFullWithoutIt() {
        //test1: without Ommni Box, cover charge is 1 per other musician present for both players
        var scn = GetScenario();
        var lirin = scn.GetDSCard("lirin");
        var techmor = scn.GetDSCard("techmor");
        var umpass = scn.GetDSCard("umpass");
        var dodo = scn.GetDSCard("dodo");
        var akrev = scn.GetDSCard("akrev");
        var trooper = scn.GetDSCard("trooper");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, lirin, techmor, umpass, dodo, akrev);
        scn.MoveCardsToDSHand(trooper);
        scn.SkipToDSTurn(Phase.DEPLOY);
        scn.EnsureDSForcePile(10);
        int dsForce = scn.GetDSForcePileCount();
        int trooperCost = scn.GetDeployCost(trooper);
        scn.DSDeployCard(trooper);
        scn.DSChooseCard(site);
        scn.PassAllResponses();
        assertAtLocation(site, trooper);
        assertEquals(dsForce - (trooperCost + 4), scn.GetDSForcePileCount()); //test1
    }
}
