package com.gempukku.swccgo.cards.set15.light;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.SwccgCardBlueprintLibrary;
import org.junit.Test;
import java.util.HashMap;
import java.util.List;
import static org.junit.Assert.*;

/** Printed metadata read from the remastered card images; abilities reuse identical Decipher text. */
public class ReflectionsGoldLightEquivalentReprintTests {
    private static final SwccgCardBlueprintLibrary LIBRARY = new SwccgCardBlueprintLibrary();
    private static final VirtualTableScenario SCENARIO = new VirtualTableScenario(new HashMap<>(), new HashMap<>());

    @Test
    public void HidingInTheGarbageStatsAndKeywordsAreCorrect() {
        var card = LIBRARY.getSwccgoCardBlueprint("15_34");
        assertEquals(3, card.getCredits());
        assertEquals("Hiding In The Garbage", card.getTitle());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(3f, card.getDestiny(), SCENARIO.epsilon);
        assertEquals(3f, card.getAlternateDestiny(), SCENARIO.epsilon);
        assertEquals(ExpansionSet.REFLECTIONS_GOLD, card.getExpansionSet());
        assertEquals(Rarity.UNKNOWN, card.getRarity());
        assertEquals("", card.getLore());
        assertTrue(card.isHorizontal());
        assertEquals("Use 2 Force to deploy on table. At any time, you may declare a card type. Use Force one by one, revealing each card used, until a card of that type is revealed (take it into hand) or Force Pile is depleted.", card.getGameText());
        assertEquals(CardSubtype.NORMAL, card.getCardSubtype());
        SCENARIO.BlueprintIconCheck(card, List.of(Icon.EFFECT, Icon.REFLECTIONS_GOLD));
        SCENARIO.BlueprintCardTypeCheck(card, List.of(CardType.EFFECT));
        SCENARIO.BlueprintKeywordCheck(card, List.of());
        SCENARIO.BlueprintPersonaCheck(card, List.of());
        SCENARIO.BlueprintModelTypeCheck(card, List.of());
    }

    @Test
    public void LaunchingTheAssaultStatsAndKeywordsAreCorrect() {
        var card = LIBRARY.getSwccgoCardBlueprint("15_43");
        assertEquals(3, card.getCredits());
        assertEquals("Launching The Assault", card.getTitle());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(4f, card.getDestiny(), SCENARIO.epsilon);
        assertEquals(4f, card.getAlternateDestiny(), SCENARIO.epsilon);
        assertEquals(ExpansionSet.REFLECTIONS_GOLD, card.getExpansionSet());
        assertEquals(Rarity.UNKNOWN, card.getRarity());
        assertEquals("", card.getLore());
        assertTrue(card.isHorizontal());
        assertEquals("Deploy on a system. Once per game, you may take Home One into hand from Reserve Deck; reshuffle. Force drains are +1 at battleground systems where you have a Star Cruiser piloted by a character. Effect canceled if Rendezvous Point on table. (Immune to Alter.)", card.getGameText());
        assertEquals(CardSubtype.NORMAL, card.getCardSubtype());
        SCENARIO.BlueprintIconCheck(card, List.of(Icon.EFFECT, Icon.REFLECTIONS_GOLD));
        SCENARIO.BlueprintCardTypeCheck(card, List.of(CardType.EFFECT));
        SCENARIO.BlueprintKeywordCheck(card, List.of(Keyword.DEPLOYS_ON_LOCATION));
        SCENARIO.BlueprintPersonaCheck(card, List.of());
        SCENARIO.BlueprintModelTypeCheck(card, List.of());
    }

    @Test
    public void TrafficControlStatsAndKeywordsAreCorrect() {
        var card = LIBRARY.getSwccgoCardBlueprint("15_55");
        assertEquals(2, card.getCredits());
        assertEquals("Traffic Control", card.getTitle());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(3f, card.getDestiny(), SCENARIO.epsilon);
        assertEquals(3f, card.getAlternateDestiny(), SCENARIO.epsilon);
        assertEquals(ExpansionSet.REFLECTIONS_GOLD, card.getExpansionSet());
        assertEquals(Rarity.UNKNOWN, card.getRarity());
        assertEquals("", card.getLore());
        assertTrue(card.isHorizontal());
        assertEquals("Use 1 Force to deploy on table. During your control phase, you may return any cards from your hand to the top of your Used Pile.", card.getGameText());
        assertEquals(CardSubtype.NORMAL, card.getCardSubtype());
        SCENARIO.BlueprintIconCheck(card, List.of(Icon.EFFECT, Icon.REFLECTIONS_GOLD));
        SCENARIO.BlueprintCardTypeCheck(card, List.of(CardType.EFFECT));
        SCENARIO.BlueprintKeywordCheck(card, List.of());
        SCENARIO.BlueprintPersonaCheck(card, List.of());
        SCENARIO.BlueprintModelTypeCheck(card, List.of());
    }

    @Test
    public void AFewManeuversStatsAndKeywordsAreCorrect() {
        var card = LIBRARY.getSwccgoCardBlueprint("15_59");
        assertEquals(1, card.getCredits());
        assertEquals("A Few Maneuvers", card.getTitle());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(6f, card.getDestiny(), SCENARIO.epsilon);
        assertEquals(6f, card.getAlternateDestiny(), SCENARIO.epsilon);
        assertEquals(ExpansionSet.REFLECTIONS_GOLD, card.getExpansionSet());
        assertEquals(Rarity.UNKNOWN, card.getRarity());
        assertEquals("", card.getLore());
        assertTrue(card.isHorizontal());
        assertEquals("Add 2 to hyperspeed and maneuver of any starfighter for remainder of turn. (Interrupt may even affect the result immediately after a destiny draw targeting the starfighter's maneuver.)", card.getGameText());
        assertEquals(CardSubtype.USED, card.getCardSubtype());
        SCENARIO.BlueprintIconCheck(card, List.of(Icon.INTERRUPT, Icon.REFLECTIONS_GOLD));
        SCENARIO.BlueprintCardTypeCheck(card, List.of(CardType.INTERRUPT));
        SCENARIO.BlueprintKeywordCheck(card, List.of());
        SCENARIO.BlueprintPersonaCheck(card, List.of());
        SCENARIO.BlueprintModelTypeCheck(card, List.of());
    }

    @Test
    public void AllWingsReportInStatsAndKeywordsAreCorrect() {
        var card = LIBRARY.getSwccgoCardBlueprint("15_61");
        assertEquals(3, card.getCredits());
        assertEquals("All Wings Report In", card.getTitle());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(5f, card.getDestiny(), SCENARIO.epsilon);
        assertEquals(5f, card.getAlternateDestiny(), SCENARIO.epsilon);
        assertEquals(ExpansionSet.REFLECTIONS_GOLD, card.getExpansionSet());
        assertEquals(Rarity.UNKNOWN, card.getRarity());
        assertEquals("", card.getLore());
        assertTrue(card.isHorizontal());
        assertEquals("Once per game, target one non-unique starfighter on table. Draw destiny. If destiny < X, retrieve X Force; X = number of copies of that card you have on table. (Immune to Sense.) OR Take one non-unique starfighter into hand from Reserve Deck; reshuffle.", card.getGameText());
        assertEquals(CardSubtype.USED, card.getCardSubtype());
        SCENARIO.BlueprintIconCheck(card, List.of(Icon.INTERRUPT, Icon.REFLECTIONS_GOLD));
        SCENARIO.BlueprintCardTypeCheck(card, List.of(CardType.INTERRUPT));
        SCENARIO.BlueprintKeywordCheck(card, List.of());
        SCENARIO.BlueprintPersonaCheck(card, List.of());
        SCENARIO.BlueprintModelTypeCheck(card, List.of());
    }

    @Test
    public void ClashOfSabersStatsAndKeywordsAreCorrect() {
        var card = LIBRARY.getSwccgoCardBlueprint("15_70");
        assertEquals(3, card.getCredits());
        assertEquals("Clash Of Sabers", card.getTitle());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(3f, card.getDestiny(), SCENARIO.epsilon);
        assertEquals(3f, card.getAlternateDestiny(), SCENARIO.epsilon);
        assertEquals(ExpansionSet.REFLECTIONS_GOLD, card.getExpansionSet());
        assertEquals(Rarity.UNKNOWN, card.getRarity());
        assertEquals("", card.getLore());
        assertTrue(card.isHorizontal());
        assertEquals("Use 2 Force to target a character present with your warrior with a lightsaber. Target cannot move or battle until end of your next turn. OR Cancel Presence Of The Force. OR Use 1 Force to take Uncontrollable Fury into hand from Reserve Deck; reshuffle.", card.getGameText());
        assertEquals(CardSubtype.LOST, card.getCardSubtype());
        SCENARIO.BlueprintIconCheck(card, List.of(Icon.INTERRUPT, Icon.REFLECTIONS_GOLD));
        SCENARIO.BlueprintCardTypeCheck(card, List.of(CardType.INTERRUPT));
        SCENARIO.BlueprintKeywordCheck(card, List.of());
        SCENARIO.BlueprintPersonaCheck(card, List.of());
        SCENARIO.BlueprintModelTypeCheck(card, List.of());
    }

    @Test
    public void QuiteAMercenaryStatsAndKeywordsAreCorrect() {
        var card = LIBRARY.getSwccgoCardBlueprint("15_122");
        assertEquals(1, card.getCredits());
        assertEquals("Quite A Mercenary", card.getTitle());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(Uniqueness.UNRESTRICTED, card.getUniqueness());
        assertEquals(5f, card.getDestiny(), SCENARIO.epsilon);
        assertEquals(5f, card.getAlternateDestiny(), SCENARIO.epsilon);
        assertEquals(ExpansionSet.REFLECTIONS_GOLD, card.getExpansionSet());
        assertEquals(Rarity.UNKNOWN, card.getRarity());
        assertEquals("", card.getLore());
        assertTrue(card.isHorizontal());
        assertEquals("If Elis Helrot just completed a transport, use 2 Force. Elis and all characters transported are lost. (Immune to Sense.) OR Prevent a just-deployed smuggler from moving this turn.", card.getGameText());
        assertEquals(CardSubtype.USED, card.getCardSubtype());
        SCENARIO.BlueprintIconCheck(card, List.of(Icon.INTERRUPT, Icon.REFLECTIONS_GOLD));
        SCENARIO.BlueprintCardTypeCheck(card, List.of(CardType.INTERRUPT));
        SCENARIO.BlueprintKeywordCheck(card, List.of());
        SCENARIO.BlueprintPersonaCheck(card, List.of());
        SCENARIO.BlueprintModelTypeCheck(card, List.of());
    }

    @Test
    public void RebelLeadershipStatsAndKeywordsAreCorrect() {
        var card = LIBRARY.getSwccgoCardBlueprint("15_123");
        assertEquals(3, card.getCredits());
        assertEquals("Rebel Leadership", card.getTitle());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(4f, card.getDestiny(), SCENARIO.epsilon);
        assertEquals(4f, card.getAlternateDestiny(), SCENARIO.epsilon);
        assertEquals(ExpansionSet.REFLECTIONS_GOLD, card.getExpansionSet());
        assertEquals(Rarity.UNKNOWN, card.getRarity());
        assertEquals("", card.getLore());
        assertTrue(card.isHorizontal());
        assertEquals("Your admirals and generals are forfeit +1 for remainder of turn. OR If your admiral is in a battle at a system (or your general, except Obi-Wan, is in a battle at a site), either add one battle destiny or prevent opponent from drawing more than one battle destiny.", card.getGameText());
        assertEquals(CardSubtype.USED, card.getCardSubtype());
        SCENARIO.BlueprintIconCheck(card, List.of(Icon.INTERRUPT, Icon.REFLECTIONS_GOLD));
        SCENARIO.BlueprintCardTypeCheck(card, List.of(CardType.INTERRUPT));
        SCENARIO.BlueprintKeywordCheck(card, List.of());
        SCENARIO.BlueprintPersonaCheck(card, List.of());
        SCENARIO.BlueprintModelTypeCheck(card, List.of());
    }

    @Test
    public void RecoilInFearStatsAndKeywordsAreCorrect() {
        var card = LIBRARY.getSwccgoCardBlueprint("15_125");
        assertEquals(3, card.getCredits());
        assertEquals("Recoil In Fear", card.getTitle());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(3f, card.getDestiny(), SCENARIO.epsilon);
        assertEquals(3f, card.getAlternateDestiny(), SCENARIO.epsilon);
        assertEquals(ExpansionSet.REFLECTIONS_GOLD, card.getExpansionSet());
        assertEquals(Rarity.UNKNOWN, card.getRarity());
        assertEquals("", card.getLore());
        assertTrue(card.isHorizontal());
        assertEquals("Use 3 Force. Each player counts cards in hand, then places entire hand and Used Pile onto Reserve Deck; reshuffle. Each player then draws from Reserve Deck the counted number of cards to create a new hand.", card.getGameText());
        assertEquals(CardSubtype.LOST, card.getCardSubtype());
        SCENARIO.BlueprintIconCheck(card, List.of(Icon.INTERRUPT, Icon.REFLECTIONS_GOLD));
        SCENARIO.BlueprintCardTypeCheck(card, List.of(CardType.INTERRUPT));
        SCENARIO.BlueprintKeywordCheck(card, List.of());
        SCENARIO.BlueprintPersonaCheck(card, List.of());
        SCENARIO.BlueprintModelTypeCheck(card, List.of());
    }

    @Test
    public void SorryAboutTheMessStatsAndKeywordsAreCorrect() {
        var card = LIBRARY.getSwccgoCardBlueprint("15_132");
        assertEquals(3, card.getCredits());
        assertEquals("Sorry About The Mess", card.getTitle());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(4f, card.getDestiny(), SCENARIO.epsilon);
        assertEquals(4f, card.getAlternateDestiny(), SCENARIO.epsilon);
        assertEquals(ExpansionSet.REFLECTIONS_GOLD, card.getExpansionSet());
        assertEquals(Rarity.UNKNOWN, card.getRarity());
        assertEquals("", card.getLore());
        assertTrue(card.isHorizontal());
        assertEquals("During your control phase, fire one of your weapons. If Han firing, may add 1 to each weapon destiny draw. (A seeker may be targeted by a character weapon using defense value 4.) Any 'hit' targets are immediately lost.", card.getGameText());
        assertEquals(CardSubtype.LOST, card.getCardSubtype());
        SCENARIO.BlueprintIconCheck(card, List.of(Icon.INTERRUPT, Icon.REFLECTIONS_GOLD));
        SCENARIO.BlueprintCardTypeCheck(card, List.of(CardType.INTERRUPT));
        SCENARIO.BlueprintKeywordCheck(card, List.of());
        SCENARIO.BlueprintPersonaCheck(card, List.of());
        SCENARIO.BlueprintModelTypeCheck(card, List.of());
    }

    @Test
    public void WalkerSightingStatsAndKeywordsAreCorrect() {
        var card = LIBRARY.getSwccgoCardBlueprint("15_144");
        assertEquals(1, card.getCredits());
        assertEquals("Walker Sighting", card.getTitle());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(3f, card.getDestiny(), SCENARIO.epsilon);
        assertEquals(3f, card.getAlternateDestiny(), SCENARIO.epsilon);
        assertEquals(ExpansionSet.REFLECTIONS_GOLD, card.getExpansionSet());
        assertEquals(Rarity.UNKNOWN, card.getRarity());
        assertEquals("", card.getLore());
        assertTrue(card.isHorizontal());
        assertEquals("If opponent just moved or deployed an AT-AT to a planet site, deploy up to three related exterior sites from your Reserve Deck; reshuffle.", card.getGameText());
        assertEquals(CardSubtype.LOST, card.getCardSubtype());
        SCENARIO.BlueprintIconCheck(card, List.of(Icon.INTERRUPT, Icon.REFLECTIONS_GOLD));
        SCENARIO.BlueprintCardTypeCheck(card, List.of(CardType.INTERRUPT));
        SCENARIO.BlueprintKeywordCheck(card, List.of());
        SCENARIO.BlueprintPersonaCheck(card, List.of());
        SCENARIO.BlueprintModelTypeCheck(card, List.of());
    }

    @Test
    public void WeCanStillOutmaneuverThemStatsAndKeywordsAreCorrect() {
        var card = LIBRARY.getSwccgoCardBlueprint("15_145");
        assertEquals(3, card.getCredits());
        assertEquals("We Can Still Outmaneuver Them", card.getTitle());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(3f, card.getDestiny(), SCENARIO.epsilon);
        assertEquals(3f, card.getAlternateDestiny(), SCENARIO.epsilon);
        assertEquals(ExpansionSet.REFLECTIONS_GOLD, card.getExpansionSet());
        assertEquals(Rarity.UNKNOWN, card.getRarity());
        assertEquals("", card.getLore());
        assertTrue(card.isHorizontal());
        assertEquals("USED: Take Collision! or Egregious Pilot Error into hand from Reserve Deck; reshuffle. LOST: If opponent just initiated a battle with at least two capital starships at a system where you have a lone starfighter, add two battle destiny.", card.getGameText());
        assertEquals(CardSubtype.USED_OR_LOST, card.getCardSubtype());
        SCENARIO.BlueprintIconCheck(card, List.of(Icon.INTERRUPT, Icon.REFLECTIONS_GOLD));
        SCENARIO.BlueprintCardTypeCheck(card, List.of(CardType.INTERRUPT));
        SCENARIO.BlueprintKeywordCheck(card, List.of());
        SCENARIO.BlueprintPersonaCheck(card, List.of());
        SCENARIO.BlueprintModelTypeCheck(card, List.of());
    }
}
