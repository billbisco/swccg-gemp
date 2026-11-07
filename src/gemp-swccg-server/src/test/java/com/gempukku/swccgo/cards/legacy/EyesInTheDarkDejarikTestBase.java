package com.gempukku.swccgo.cards.legacy;

import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.PhysicalCardImpl;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Shared gameplay tests for the dejarik clauses of Eyes In The Dark (V) (Legacy 1240_85 and Original Virtual 1113_006):
 * "'Effect' on Alter may be treated as 'Mantellian Savrip.' The game text of your Effects here may not be suspended.
 * Except during battles here, may play your Interrupts here as if from hand."
 * Under 'Dejarik Rules' a dejarik at a holosite is not an Effect/Interrupt and its game text is suspended.
 */
public abstract class EyesInTheDarkDejarikTestBase {

    /** The blueprint id of the Eyes In The Dark printing under test. */
    protected abstract String eyesBlueprintId();

    protected VirtualTableScenario GetDejarikScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("eyes", eyesBlueprintId());
                    put("holo", "2_63");     // Dejarik Hologameboard (holosite)
                    put("savrip", "1_55");   // Mantellian Savrip (dejarik Effect)
                    put("houjix", "2_50");   // Houjix (dejarik Lost Interrupt)
                    put("lsalter", "1_71");  // Alter (LS)
                    put("luke", "1_19");
                }},
                new HashMap<>() {{
                    put("alter", "1_234");   // Alter (DS)
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

    /** Puts a card on the holosite as a dejarik (as if deployed using 'Dejarik Rules'). */
    protected static void attachAsDejarik(VirtualTableScenario scn, PhysicalCardImpl holosite, PhysicalCardImpl card) {
        scn.MoveCardsToLocation(holosite, card);
        card.setDejarikHologramAtHolosite(true);
    }

    // ---- 'Effect' on Alter may be treated as 'Mantellian Savrip' ----

    @Test
    public void EyesInTheDarkLetsOpponentsAlterCancelSavripDeployedAsDejarik() {
        var scn = GetDejarikScenario();
        var eyes = scn.GetLSCard("eyes");
        var holo = scn.GetLSCard("holo");
        var savrip = scn.GetLSCard("savrip");
        var alter = scn.GetDSCard("alter");
        var vader = scn.GetDSCard("vader");

        scn.StartGame();
        scn.MoveLocationToTable(holo);
        scn.AttachCardsTo(holo, eyes);
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), vader);
        scn.MoveCardsToLSHand(savrip);
        scn.MoveCardsToDSHand(alter);

        // Deploy the Savrip to the holosite using 'Dejarik Rules'
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.EnsureForcePile(scn.LS, 6);
        assertTrue(scn.LSCardActionAvailable(savrip, "Deploy to holosite"));
        scn.LSUseCardAction(savrip, "Deploy to holosite");
        if (scn.LSHasCardChoiceAvailable(holo)) {
            scn.LSChooseCard(holo);
        }
        scn.PassAllResponses();
        assertEquals(holo, savrip.getAtLocation());
        assertTrue(savrip.isDejarikHologramAtHolosite());

        scn.SkipToDSTurn(Phase.DEPLOY);
        scn.EnsureForcePile(scn.DS, 6);
        assertTrue(scn.DSCardPlayAvailable(alter));
        scn.DSPlayCard(alter);
        if (scn.DSHasCardChoiceAvailable(savrip)) {
            scn.DSChooseCard(savrip);
        }
        if (scn.DSHasCardChoiceAvailable(vader)) {
            scn.DSChooseCard(vader);
        }
        scn.PrepareDSDestiny(1);
        scn.PassAllResponses();

        assertTrue(inLostPile(savrip));
        assertTrue(scn.IsAttachedTo(holo, eyes));
    }

    @Test
    public void EyesInTheDarkLetsYourOwnAlterTargetSavripAtTheHolosite() {
        var scn = GetDejarikScenario();
        var eyes = scn.GetLSCard("eyes");
        var holo = scn.GetLSCard("holo");
        var savrip = scn.GetLSCard("savrip");
        var lsalter = scn.GetLSCard("lsalter");
        var luke = scn.GetLSCard("luke");

        scn.StartGame();
        scn.MoveLocationToTable(holo);
        scn.AttachCardsTo(holo, eyes);
        attachAsDejarik(scn, holo, savrip);
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), luke);
        scn.MoveCardsToLSHand(lsalter);

        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.EnsureForcePile(scn.LS, 6);
        assertTrue(scn.LSCardPlayAvailable(lsalter));
        scn.LSPlayCard(lsalter);
        // Eyes itself is an Effect, so Alter has a choice; the Savrip (a dejarik) is a valid target
        assertTrue(scn.LSHasCardChoiceAvailable(savrip));
        assertTrue(scn.LSHasCardChoiceAvailable(eyes));
    }

    @Test
    public void AlterMayNotTargetSavripAtHolositeWithoutEyesInTheDark() {
        var scn = GetDejarikScenario();
        var holo = scn.GetLSCard("holo");
        var savrip = scn.GetLSCard("savrip");
        var alter = scn.GetDSCard("alter");
        var vader = scn.GetDSCard("vader");

        scn.StartGame();
        scn.MoveLocationToTable(holo);
        attachAsDejarik(scn, holo, savrip);
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), vader);
        scn.MoveCardsToDSHand(alter);

        scn.SkipToDSTurn(Phase.DEPLOY);
        scn.EnsureForcePile(scn.DS, 6);
        // The only "Effect" on table is a dejarik at a holosite, which is not an Effect
        assertFalse(scn.DSCardPlayAvailable(alter));
    }

    // ---- The game text of your Effects here may not be suspended ----

    private static final String HOUJIX_TEXT = "Cancel all remaining battle damage";
    private static final String SAVRIP_TEXT = "Forfeit a card from hand";

    protected static boolean inLostPile(PhysicalCardImpl card) {
        return card.getZone() == Zone.LOST_PILE || card.getZone() == Zone.TOP_OF_LOST_PILE;
    }

    /** True if the Light Side player currently has a card-action decision with the given action on the card. */
    protected static boolean lsHasAction(VirtualTableScenario scn, PhysicalCardImpl card, String text) {
        try {
            return scn.LSCardActionAvailable(card, text);
        } catch (RuntimeException e) {
            return false;
        }
    }

    /**
     * Passes decisions during the damage segment until the Light Side player is offered the given action on the card
     * (returns true), or has to choose Force to lose / a card to forfeit, or the battle ends (returns false).
     */
    protected static boolean advanceUntilLSAction(VirtualTableScenario scn, PhysicalCardImpl card, String text) {
        for (int i = 0; i < 40; i++) {
            if (scn.gameState().getBattleState() == null)
                return false;
            String player = scn.GetDecidingPlayer();
            String decisionText = scn.GetAwaitingDecision(player).getText();
            if (player.equals(scn.LS) && lsHasAction(scn, card, text))
                return true;
            if (decisionText.contains("Choose Force to lose or a card from battle to forfeit"))
                return false;
            scn.PlayerPass(player);
        }
        return false;
    }

    /** DS initiates a battle at the DS starting site that LS (with one weak character) loses; stops at the damage segment. */
    private void loseBattleAt(VirtualTableScenario scn, PhysicalCardImpl site, PhysicalCardImpl lsCharacter) {
        var vader = scn.GetDSCard("vader");
        scn.MoveCardsToLocation(site, lsCharacter, vader);
        scn.SkipToDSTurn(Phase.BATTLE);
        scn.EnsureForcePile(scn.DS, 3);
        scn.DSInitiateBattle(site);
        scn.SkipToDamageSegment(false);
    }

    @Test
    public void SavripGameTextWorksAtHolositeWithEyesInTheDark() {
        var scn = GetDejarikScenario();
        var eyes = scn.GetLSCard("eyes");
        var holo = scn.GetLSCard("holo");
        var savrip = scn.GetLSCard("savrip");
        var luke = scn.GetLSCard("luke");
        var trooper = scn.GetLSFiller(1);

        scn.StartGame();
        scn.MoveLocationToTable(holo);
        scn.AttachCardsTo(holo, eyes);
        attachAsDejarik(scn, holo, savrip);
        assertFalse(scn.game().getModifiersQuerying().isGameTextCanceled(scn.gameState(), savrip));
        scn.MoveCardsToLSHand(luke);

        loseBattleAt(scn, scn.GetDSStartingLocation(), trooper);
        assertTrue(advanceUntilLSAction(scn, savrip, SAVRIP_TEXT));
        int damageBefore = scn.GetUnpaidLSBattleDamage();
        scn.LSUseCardAction(savrip, SAVRIP_TEXT);
        if (scn.LSHasCardChoiceAvailable(luke)) {
            scn.LSChooseCard(luke);
        }
        scn.PassAllResponses();
        assertTrue(inLostPile(luke));
        // Luke's forfeit (7) covers the remaining battle damage
        assertTrue(scn.gameState().getBattleState() == null || scn.GetUnpaidLSBattleDamage() < damageBefore);
        assertTrue(savrip.isDejarikHologramAtHolosite());
    }

    @Test
    public void SavripGameTextIsSuspendedAtHolositeWithoutEyesInTheDark() {
        var scn = GetDejarikScenario();
        var holo = scn.GetLSCard("holo");
        var savrip = scn.GetLSCard("savrip");
        var luke = scn.GetLSCard("luke");
        var trooper = scn.GetLSFiller(1);

        scn.StartGame();
        scn.MoveLocationToTable(holo);
        attachAsDejarik(scn, holo, savrip);
        assertTrue(scn.game().getModifiersQuerying().isGameTextCanceled(scn.gameState(), savrip));
        scn.MoveCardsToLSHand(luke);

        loseBattleAt(scn, scn.GetDSStartingLocation(), trooper);
        assertFalse(advanceUntilLSAction(scn, savrip, SAVRIP_TEXT));
        assertTrue(scn.AwaitingLSBattleDamagePayment());
    }

    @Test
    public void OnlyDejarikEffectsAtTheHolositeWithEyesKeepTheirGameText() {
        var scn = GetDejarikScenario();
        var eyes = scn.GetLSCard("eyes");
        var holo = scn.GetLSCard("holo");
        var savrip = scn.GetLSCard("savrip");

        scn.StartGame();
        scn.MoveLocationToTable(holo);
        attachAsDejarik(scn, holo, savrip);
        assertTrue(scn.game().getModifiersQuerying().isGameTextCanceled(scn.gameState(), savrip));
        scn.AttachCardsTo(holo, eyes);
        assertFalse(scn.game().getModifiersQuerying().isGameTextCanceled(scn.gameState(), savrip));
        // Once Eyes leaves, 'Dejarik Rules' suspend the Savrip's game text again
        scn.MoveCardsToLSHand(eyes);
        assertTrue(scn.game().getModifiersQuerying().isGameTextCanceled(scn.gameState(), savrip));
    }

    // ---- Except during battles here, may play your Interrupts here as if from hand ----

    /** Forfeits the LS character to battle damage, then advances to the next point Houjix could be offered. */
    private boolean forfeitThenLookForHoujix(VirtualTableScenario scn, PhysicalCardImpl trooper, PhysicalCardImpl houjix) {
        // A participant can still be forfeited, so Houjix is not playable yet
        assertFalse(advanceUntilLSAction(scn, houjix, HOUJIX_TEXT));
        assertTrue(scn.AwaitingLSBattleDamagePayment());
        scn.LSChooseCard(trooper);
        return advanceUntilLSAction(scn, houjix, HOUJIX_TEXT);
    }

    @Test
    public void HoujixAtHolositeMayBePlayedAfterLosingBattleElsewhere() {
        var scn = GetDejarikScenario();
        var eyes = scn.GetLSCard("eyes");
        var holo = scn.GetLSCard("holo");
        var houjix = scn.GetLSCard("houjix");
        var trooper = scn.GetLSFiller(1);

        scn.StartGame();
        scn.MoveLocationToTable(holo);
        scn.AttachCardsTo(holo, eyes);
        attachAsDejarik(scn, holo, houjix);

        loseBattleAt(scn, scn.GetDSStartingLocation(), trooper);
        assertTrue(forfeitThenLookForHoujix(scn, trooper, houjix));
        assertTrue(scn.GetUnpaidLSBattleDamage() > 0);
        scn.LSUseCardAction(houjix, HOUJIX_TEXT);
        scn.PassAllResponses();

        // Played as a Lost Interrupt: it leaves the holosite for the Lost Pile and the battle damage is gone
        assertTrue(inLostPile(houjix));
        assertFalse(houjix.isDejarikHologramAtHolosite());
        assertTrue(scn.gameState().getBattleState() == null || scn.GetUnpaidLSBattleDamage() == 0);
    }

    @Test
    public void HoujixAtHolositeMayNotBePlayedWithoutEyesInTheDark() {
        var scn = GetDejarikScenario();
        var holo = scn.GetLSCard("holo");
        var houjix = scn.GetLSCard("houjix");
        var trooper = scn.GetLSFiller(1);

        scn.StartGame();
        scn.MoveLocationToTable(holo);
        attachAsDejarik(scn, holo, houjix);

        loseBattleAt(scn, scn.GetDSStartingLocation(), trooper);
        assertFalse(forfeitThenLookForHoujix(scn, trooper, houjix));
        assertTrue(scn.AwaitingLSBattleDamagePayment());
        assertEquals(holo, houjix.getAtLocation());
    }

    @Test
    public void HoujixAtHolositeMayNotBePlayedDuringBattleAtTheHolosite() {
        var scn = GetDejarikScenario();
        var eyes = scn.GetLSCard("eyes");
        var holo = scn.GetLSCard("holo");
        var houjix = scn.GetLSCard("houjix");
        var trooper = scn.GetLSFiller(1);

        scn.StartGame();
        scn.MoveLocationToTable(holo);
        scn.AttachCardsTo(holo, eyes);
        attachAsDejarik(scn, holo, houjix);

        loseBattleAt(scn, holo, trooper);
        assertFalse(forfeitThenLookForHoujix(scn, trooper, houjix));
        assertTrue(scn.AwaitingLSBattleDamagePayment());
        assertEquals(holo, houjix.getAtLocation());
    }

    @Test
    public void HoujixInHandIsStillPlayableDuringBattleAtTheHolosite() {
        // Control: the battle setup at the holosite does let Houjix be played normally from hand
        var scn = GetDejarikScenario();
        var eyes = scn.GetLSCard("eyes");
        var holo = scn.GetLSCard("holo");
        var houjix = scn.GetLSCard("houjix");
        var trooper = scn.GetLSFiller(1);

        scn.StartGame();
        scn.MoveLocationToTable(holo);
        scn.AttachCardsTo(holo, eyes);
        scn.MoveCardsToLSHand(houjix);

        loseBattleAt(scn, holo, trooper);
        assertTrue(forfeitThenLookForHoujix(scn, trooper, houjix));
    }
}
