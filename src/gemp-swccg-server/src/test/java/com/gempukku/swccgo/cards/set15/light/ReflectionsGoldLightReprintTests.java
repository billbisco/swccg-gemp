package com.gempukku.swccgo.cards.set15.light;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.SwccgCardBlueprintLibrary;
import org.junit.Test;
import java.util.HashMap;
import java.util.List;
import static org.junit.Assert.*;

/** Printed metadata read from the remastered card images; abilities reuse identical Decipher text. */
public class ReflectionsGoldLightReprintTests {
    private static final SwccgCardBlueprintLibrary LIBRARY = new SwccgCardBlueprintLibrary();
    private static final VirtualTableScenario SCENARIO = new VirtualTableScenario(new HashMap<>(), new HashMap<>());

    @Test
    public void AimHighStatsAndKeywordsAreCorrect() {
        var card = LIBRARY.getSwccgoCardBlueprint("15_22");
        assertEquals(2, card.getCredits());
        assertEquals("Aim High", card.getTitle());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(3f, card.getDestiny(), SCENARIO.epsilon);
        assertEquals(3f, card.getAlternateDestiny(), SCENARIO.epsilon);
        assertEquals(ExpansionSet.REFLECTIONS_GOLD, card.getExpansionSet());
        assertEquals(Rarity.UNKNOWN, card.getRarity());
        assertEquals("", card.getLore());
        assertTrue(card.isHorizontal());
        assertEquals("Deploy on table. When you fire two weapons (except lightsabers) in a battle, your total power is +5. Also, whenever opponent retrieves X cards, opponent must first use X Force or that retrieval is canceled. (Immune to Alter.)", card.getGameText());
        assertEquals(CardSubtype.NORMAL, card.getCardSubtype());
        SCENARIO.BlueprintIconCheck(card, List.of(Icon.EFFECT, Icon.REFLECTIONS_GOLD));
        SCENARIO.BlueprintCardTypeCheck(card, List.of(CardType.EFFECT));
        SCENARIO.BlueprintKeywordCheck(card, List.of());
        SCENARIO.BlueprintPersonaCheck(card, List.of());
        SCENARIO.BlueprintModelTypeCheck(card, List.of());
    }

    @Test
    public void BriskyMorningMunchenStatsAndKeywordsAreCorrect() {
        var card = LIBRARY.getSwccgoCardBlueprint("15_26");
        assertEquals(3, card.getCredits());
        assertEquals("Brisky Morning Munchen", card.getTitle());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(3f, card.getDestiny(), SCENARIO.epsilon);
        assertEquals(3f, card.getAlternateDestiny(), SCENARIO.epsilon);
        assertEquals(ExpansionSet.REFLECTIONS_GOLD, card.getExpansionSet());
        assertEquals(Rarity.UNKNOWN, card.getRarity());
        assertEquals("", card.getLore());
        assertTrue(card.isHorizontal());
        assertEquals("Deploy on table. Once per game, may deploy Jar Jar from Reserve Deck; reshuffle. If you just lost Jar Jar from a site, may place him here. At any time, may use 3 Force to take Jar Jar from here into hand.", card.getGameText());
        assertEquals(CardSubtype.NORMAL, card.getCardSubtype());
        SCENARIO.BlueprintIconCheck(card, List.of(Icon.EFFECT, Icon.REFLECTIONS_GOLD, Icon.EPISODE_I));
        SCENARIO.BlueprintCardTypeCheck(card, List.of(CardType.EFFECT));
        SCENARIO.BlueprintKeywordCheck(card, List.of());
        SCENARIO.BlueprintPersonaCheck(card, List.of());
        SCENARIO.BlueprintModelTypeCheck(card, List.of());
    }

    @Test
    public void CloseAirSupportStatsAndKeywordsAreCorrect() {
        var card = LIBRARY.getSwccgoCardBlueprint("15_27");
        assertEquals(2, card.getCredits());
        assertEquals("Close Air Support", card.getTitle());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(5f, card.getDestiny(), SCENARIO.epsilon);
        assertEquals(5f, card.getAlternateDestiny(), SCENARIO.epsilon);
        assertEquals(ExpansionSet.REFLECTIONS_GOLD, card.getExpansionSet());
        assertEquals(Rarity.UNKNOWN, card.getRarity());
        assertEquals("", card.getLore());
        assertTrue(card.isHorizontal());
        assertEquals("Deploy on table. While your Z-95 occupies a system or cloud sector, once per turn your scout at a related exterior site may add one battle destiny (if Z-95 is Tala 1 or Tala 2, may also add one destiny to total power only).", card.getGameText());
        assertEquals(CardSubtype.NORMAL, card.getCardSubtype());
        SCENARIO.BlueprintIconCheck(card, List.of(Icon.EFFECT, Icon.REFLECTIONS_GOLD));
        SCENARIO.BlueprintCardTypeCheck(card, List.of(CardType.EFFECT));
        SCENARIO.BlueprintKeywordCheck(card, List.of());
        SCENARIO.BlueprintPersonaCheck(card, List.of());
        SCENARIO.BlueprintModelTypeCheck(card, List.of());
    }

    @Test
    public void GunganEnergyShieldStatsAndKeywordsAreCorrect() {
        var card = LIBRARY.getSwccgoCardBlueprint("15_33");
        assertEquals(3, card.getCredits());
        assertEquals("Gungan Energy Shield", card.getTitle());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(3f, card.getDestiny(), SCENARIO.epsilon);
        assertEquals(3f, card.getAlternateDestiny(), SCENARIO.epsilon);
        assertEquals(ExpansionSet.REFLECTIONS_GOLD, card.getExpansionSet());
        assertEquals(Rarity.UNKNOWN, card.getRarity());
        assertEquals("", card.getLore());
        assertTrue(card.isHorizontal());
        assertEquals("Deploy on an exterior site. While your Fambaa here: your characters here may not be targeted by weapons, opponent's battle destiny draws here are -1, and characters must use +1 Force when moving to or from here using their landspeed.", card.getGameText());
        assertEquals(CardSubtype.NORMAL, card.getCardSubtype());
        SCENARIO.BlueprintIconCheck(card, List.of(Icon.EFFECT, Icon.REFLECTIONS_GOLD, Icon.EPISODE_I));
        SCENARIO.BlueprintCardTypeCheck(card, List.of(CardType.EFFECT));
        SCENARIO.BlueprintKeywordCheck(card, List.of(Keyword.DEPLOYS_ON_SITE));
        SCENARIO.BlueprintPersonaCheck(card, List.of());
        SCENARIO.BlueprintModelTypeCheck(card, List.of());
    }

    @Test
    public void IMustBeAllowedToSpeakStatsAndKeywordsAreCorrect() {
        var card = LIBRARY.getSwccgoCardBlueprint("15_36");
        assertEquals(2, card.getCredits());
        assertEquals("I Must Be Allowed To Speak", card.getTitle());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(4f, card.getDestiny(), SCENARIO.epsilon);
        assertEquals(4f, card.getAlternateDestiny(), SCENARIO.epsilon);
        assertEquals(ExpansionSet.REFLECTIONS_GOLD, card.getExpansionSet());
        assertEquals(Rarity.UNKNOWN, card.getRarity());
        assertEquals("", card.getLore());
        assertTrue(card.isHorizontal());
        assertEquals("Deploy on a Jabba's Palace site. Luke may deploy at this site regardless of presence or location deployment restrictions. When he is deployed here, relocate Effect to Luke, he is immune to attrition while on Tatooine. (Immune to Alter.)", card.getGameText());
        assertEquals(CardSubtype.NORMAL, card.getCardSubtype());
        SCENARIO.BlueprintIconCheck(card, List.of(Icon.EFFECT, Icon.REFLECTIONS_GOLD));
        SCENARIO.BlueprintCardTypeCheck(card, List.of(CardType.EFFECT));
        SCENARIO.BlueprintKeywordCheck(card, List.of(Keyword.DEPLOYS_ON_SITE));
        SCENARIO.BlueprintPersonaCheck(card, List.of());
        SCENARIO.BlueprintModelTypeCheck(card, List.of());
    }

    @Test
    public void RevolutionStatsAndKeywordsAreCorrect() {
        var card = LIBRARY.getSwccgoCardBlueprint("15_48");
        assertEquals(4, card.getCredits());
        assertEquals("Revolution", card.getTitle());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(3f, card.getDestiny(), SCENARIO.epsilon);
        assertEquals(3f, card.getAlternateDestiny(), SCENARIO.epsilon);
        assertEquals(ExpansionSet.REFLECTIONS_GOLD, card.getExpansionSet());
        assertEquals(Rarity.UNKNOWN, card.getRarity());
        assertEquals("", card.getLore());
        assertTrue(card.isHorizontal());
        assertEquals("Use 3 Force to deploy on any location. Rotate the location so that icons and game texts switch direction. (If Expand the Empire present, it also switches.) If Revolution later canceled, location rotates back again.", card.getGameText());
        assertEquals(CardSubtype.NORMAL, card.getCardSubtype());
        SCENARIO.BlueprintIconCheck(card, List.of(Icon.EFFECT, Icon.REFLECTIONS_GOLD));
        SCENARIO.BlueprintCardTypeCheck(card, List.of(CardType.EFFECT));
        SCENARIO.BlueprintKeywordCheck(card, List.of(Keyword.DEPLOYS_ON_LOCATION));
        SCENARIO.BlueprintPersonaCheck(card, List.of());
        SCENARIO.BlueprintModelTypeCheck(card, List.of());
    }

    @Test
    public void ScrambleStatsAndKeywordsAreCorrect() {
        var card = LIBRARY.getSwccgoCardBlueprint("15_49");
        assertEquals(4, card.getCredits());
        assertEquals("Scramble", card.getTitle());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(3f, card.getDestiny(), SCENARIO.epsilon);
        assertEquals(3f, card.getAlternateDestiny(), SCENARIO.epsilon);
        assertEquals(ExpansionSet.REFLECTIONS_GOLD, card.getExpansionSet());
        assertEquals(Rarity.UNKNOWN, card.getRarity());
        assertEquals("", card.getLore());
        assertTrue(card.isHorizontal());
        assertEquals("Deploy on opponent's side of table. Opponent's pilots (except Vader) present at sites may not use ability to draw battle destiny. Effect canceled when opponent has no pilots at sites.", card.getGameText());
        assertEquals(CardSubtype.NORMAL, card.getCardSubtype());
        SCENARIO.BlueprintIconCheck(card, List.of(Icon.EFFECT, Icon.REFLECTIONS_GOLD));
        SCENARIO.BlueprintCardTypeCheck(card, List.of(CardType.EFFECT));
        SCENARIO.BlueprintKeywordCheck(card, List.of());
        SCENARIO.BlueprintPersonaCheck(card, List.of());
        SCENARIO.BlueprintModelTypeCheck(card, List.of());
    }

    @Test
    public void AttackPatternDeltaStatsAndKeywordsAreCorrect() {
        var card = LIBRARY.getSwccgoCardBlueprint("15_63");
        assertEquals(1, card.getCredits());
        assertEquals("Attack Pattern Delta", card.getTitle());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(Uniqueness.UNRESTRICTED, card.getUniqueness());
        assertEquals(3f, card.getDestiny(), SCENARIO.epsilon);
        assertEquals(3f, card.getAlternateDestiny(), SCENARIO.epsilon);
        assertEquals(ExpansionSet.REFLECTIONS_GOLD, card.getExpansionSet());
        assertEquals(Rarity.UNKNOWN, card.getRarity());
        assertEquals("", card.getLore());
        assertTrue(card.isHorizontal());
        assertEquals("If a battle was just initiated at a site, identify your lead T-47 there. For remainder of this turn, all other T-47s at same site are power +1, are immune to attrition and cannot be targeted by weapons.", card.getGameText());
        assertEquals(CardSubtype.LOST, card.getCardSubtype());
        SCENARIO.BlueprintIconCheck(card, List.of(Icon.INTERRUPT, Icon.REFLECTIONS_GOLD));
        SCENARIO.BlueprintCardTypeCheck(card, List.of(CardType.INTERRUPT));
        SCENARIO.BlueprintKeywordCheck(card, List.of());
        SCENARIO.BlueprintPersonaCheck(card, List.of());
        SCENARIO.BlueprintModelTypeCheck(card, List.of());
    }

    @Test
    public void BlasterDeflectionStatsAndKeywordsAreCorrect() {
        var card = LIBRARY.getSwccgoCardBlueprint("15_67");
        assertEquals(2, card.getCredits());
        assertEquals("Blaster Deflection", card.getTitle());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(4f, card.getDestiny(), SCENARIO.epsilon);
        assertEquals(4f, card.getAlternateDestiny(), SCENARIO.epsilon);
        assertEquals(ExpansionSet.REFLECTIONS_GOLD, card.getExpansionSet());
        assertEquals(Rarity.UNKNOWN, card.getRarity());
        assertEquals("", card.getLore());
        assertTrue(card.isHorizontal());
        assertEquals("USED: Cancel an attempt to use a character weapon to target your character of ability > 4. LOST: If your character of ability > 4 with a lightsaber was just targeted by a blaster, use 3 Force to re-target that blaster to an opponent's character present.", card.getGameText());
        assertEquals(CardSubtype.USED_OR_LOST, card.getCardSubtype());
        SCENARIO.BlueprintIconCheck(card, List.of(Icon.INTERRUPT, Icon.REFLECTIONS_GOLD));
        SCENARIO.BlueprintCardTypeCheck(card, List.of(CardType.INTERRUPT));
        SCENARIO.BlueprintKeywordCheck(card, List.of());
        SCENARIO.BlueprintPersonaCheck(card, List.of());
        SCENARIO.BlueprintModelTypeCheck(card, List.of());
    }

    @Test
    public void CorellianSlipStatsAndKeywordsAreCorrect() {
        var card = LIBRARY.getSwccgoCardBlueprint("15_73");
        assertEquals(1, card.getCredits());
        assertEquals("Corellian Slip", card.getTitle());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(Uniqueness.UNRESTRICTED, card.getUniqueness());
        assertEquals(4f, card.getDestiny(), SCENARIO.epsilon);
        assertEquals(4f, card.getAlternateDestiny(), SCENARIO.epsilon);
        assertEquals(ExpansionSet.REFLECTIONS_GOLD, card.getExpansionSet());
        assertEquals(Rarity.UNKNOWN, card.getRarity());
        assertEquals("", card.getLore());
        assertTrue(card.isHorizontal());
        assertEquals("If opponent just initiated a Tallon Roll, add your maneuver + ability of one pilot on your targeted starfighter to your total. OR Decrease the maneuver of an opponent's starfighter by 1 for remainder of this turn.", card.getGameText());
        assertEquals(CardSubtype.USED, card.getCardSubtype());
        SCENARIO.BlueprintIconCheck(card, List.of(Icon.INTERRUPT, Icon.REFLECTIONS_GOLD));
        SCENARIO.BlueprintCardTypeCheck(card, List.of(CardType.INTERRUPT));
        SCENARIO.BlueprintKeywordCheck(card, List.of());
        SCENARIO.BlueprintPersonaCheck(card, List.of());
        SCENARIO.BlueprintModelTypeCheck(card, List.of());
    }

    @Test
    public void EnteringTheArenaStatsAndKeywordsAreCorrect() {
        var card = LIBRARY.getSwccgoCardBlueprint("15_77");
        assertEquals(3, card.getCredits());
        assertEquals("Entering The Arena", card.getTitle());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(3f, card.getDestiny(), SCENARIO.epsilon);
        assertEquals(3f, card.getAlternateDestiny(), SCENARIO.epsilon);
        assertEquals(ExpansionSet.REFLECTIONS_GOLD, card.getExpansionSet());
        assertEquals(Rarity.UNKNOWN, card.getRarity());
        assertEquals("", card.getLore());
        assertTrue(card.isHorizontal());
        assertEquals("USED: Once per game may take Podracer Bay into hand from Reserve Deck; reshuffle. LOST: During a battle at Podracer Bay, add one battle destiny.", card.getGameText());
        assertEquals(CardSubtype.USED_OR_LOST, card.getCardSubtype());
        SCENARIO.BlueprintIconCheck(card, List.of(Icon.INTERRUPT, Icon.REFLECTIONS_GOLD, Icon.EPISODE_I));
        SCENARIO.BlueprintCardTypeCheck(card, List.of(CardType.INTERRUPT));
        SCENARIO.BlueprintKeywordCheck(card, List.of());
        SCENARIO.BlueprintPersonaCheck(card, List.of());
        SCENARIO.BlueprintModelTypeCheck(card, List.of());
    }

    @Test
    public void EventuallyYoullLoseStatsAndKeywordsAreCorrect() {
        var card = LIBRARY.getSwccgoCardBlueprint("15_78");
        assertEquals(3, card.getCredits());
        assertEquals("Eventually You'll Lose", card.getTitle());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(4f, card.getDestiny(), SCENARIO.epsilon);
        assertEquals(4f, card.getAlternateDestiny(), SCENARIO.epsilon);
        assertEquals(ExpansionSet.REFLECTIONS_GOLD, card.getExpansionSet());
        assertEquals(Rarity.UNKNOWN, card.getRarity());
        assertEquals("", card.getLore());
        assertTrue(card.isHorizontal());
        assertEquals("USED: If a battle was just initiated, place the top card of your Used Pile on top of your Reserve Deck. LOST: Use 3 Force to cause opponent to reveal hand. All cards opponent has 3 or more of in hand are lost.", card.getGameText());
        assertEquals(CardSubtype.USED_OR_LOST, card.getCardSubtype());
        SCENARIO.BlueprintIconCheck(card, List.of(Icon.INTERRUPT, Icon.REFLECTIONS_GOLD, Icon.EPISODE_I));
        SCENARIO.BlueprintCardTypeCheck(card, List.of(CardType.INTERRUPT));
        SCENARIO.BlueprintKeywordCheck(card, List.of());
        SCENARIO.BlueprintPersonaCheck(card, List.of());
        SCENARIO.BlueprintModelTypeCheck(card, List.of());
    }

    @Test
    public void GlancingBlowStatsAndKeywordsAreCorrect() {
        var card = LIBRARY.getSwccgoCardBlueprint("15_83");
        assertEquals(3, card.getCredits());
        assertEquals("Glancing Blow", card.getTitle());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(3f, card.getDestiny(), SCENARIO.epsilon);
        assertEquals(3f, card.getAlternateDestiny(), SCENARIO.epsilon);
        assertEquals(ExpansionSet.REFLECTIONS_GOLD, card.getExpansionSet());
        assertEquals(Rarity.UNKNOWN, card.getRarity());
        assertEquals("", card.getLore());
        assertTrue(card.isHorizontal());
        assertEquals("If you have a character with a lightsaber present in a battle with Vader, lose 1 Force to reduce Vader's power to zero. OR If you have a character with a lightsaber in a duel, cancel one opponent's destiny draw immediately after it is revealed.", card.getGameText());
        assertEquals(CardSubtype.LOST, card.getCardSubtype());
        SCENARIO.BlueprintIconCheck(card, List.of(Icon.INTERRUPT, Icon.REFLECTIONS_GOLD));
        SCENARIO.BlueprintCardTypeCheck(card, List.of(CardType.INTERRUPT));
        SCENARIO.BlueprintKeywordCheck(card, List.of());
        SCENARIO.BlueprintPersonaCheck(card, List.of());
        SCENARIO.BlueprintModelTypeCheck(card, List.of());
    }

    @Test
    public void HowDidWeGetIntoThisMessStatsAndKeywordsAreCorrect() {
        var card = LIBRARY.getSwccgoCardBlueprint("15_86");
        assertEquals(3, card.getCredits());
        assertEquals("How Did We Get Into This Mess?", card.getTitle());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(4f, card.getDestiny(), SCENARIO.epsilon);
        assertEquals(4f, card.getAlternateDestiny(), SCENARIO.epsilon);
        assertEquals(ExpansionSet.REFLECTIONS_GOLD, card.getExpansionSet());
        assertEquals(Rarity.UNKNOWN, card.getRarity());
        assertEquals("", card.getLore());
        assertTrue(card.isHorizontal());
        assertEquals("If one of your droids is at the Dune Sea or where there is a Scomp link, use 1 Force to discard your entire hand to your Used Pile. Draw the same number of cards from your Reserve Deck. Draw one extra card if droid is C-3PO or R2-D2.", card.getGameText());
        assertEquals(CardSubtype.USED, card.getCardSubtype());
        SCENARIO.BlueprintIconCheck(card, List.of(Icon.INTERRUPT, Icon.REFLECTIONS_GOLD));
        SCENARIO.BlueprintCardTypeCheck(card, List.of(CardType.INTERRUPT));
        SCENARIO.BlueprintKeywordCheck(card, List.of());
        SCENARIO.BlueprintPersonaCheck(card, List.of());
        SCENARIO.BlueprintModelTypeCheck(card, List.of());
    }

    @Test
    public void IHaveAReallyBadFeelingAboutThisStatsAndKeywordsAreCorrect() {
        var card = LIBRARY.getSwccgoCardBlueprint("15_88");
        assertEquals(3, card.getCredits());
        assertEquals("I Have A Really Bad Feeling About This", card.getTitle());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(5f, card.getDestiny(), SCENARIO.epsilon);
        assertEquals(5f, card.getAlternateDestiny(), SCENARIO.epsilon);
        assertEquals(ExpansionSet.REFLECTIONS_GOLD, card.getExpansionSet());
        assertEquals(Rarity.UNKNOWN, card.getRarity());
        assertEquals("", card.getLore());
        assertTrue(card.isHorizontal());
        assertEquals("If opponent just initiated a battle where opponent has more than double your power, target your highest-ability character in that battle. Draw destiny. If destiny < target's ability, deploy one character there from hand (for free).", card.getGameText());
        assertEquals(CardSubtype.USED, card.getCardSubtype());
        SCENARIO.BlueprintIconCheck(card, List.of(Icon.INTERRUPT, Icon.REFLECTIONS_GOLD));
        SCENARIO.BlueprintCardTypeCheck(card, List.of(CardType.INTERRUPT));
        SCENARIO.BlueprintKeywordCheck(card, List.of());
        SCENARIO.BlueprintPersonaCheck(card, List.of());
        SCENARIO.BlueprintModelTypeCheck(card, List.of());
    }

    @Test
    public void InconsequentialBarriersStatsAndKeywordsAreCorrect() {
        var card = LIBRARY.getSwccgoCardBlueprint("15_90");
        assertEquals(1, card.getCredits());
        assertEquals("Inconsequential Barriers", card.getTitle());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(4f, card.getDestiny(), SCENARIO.epsilon);
        assertEquals(4f, card.getAlternateDestiny(), SCENARIO.epsilon);
        assertEquals(ExpansionSet.REFLECTIONS_GOLD, card.getExpansionSet());
        assertEquals(Rarity.UNKNOWN, card.getRarity());
        assertEquals("", card.getLore());
        assertTrue(card.isHorizontal());
        assertEquals("Cancel Imperial Barrier, None Shall Pass, or Set For Stun. OR Peek at the top card of your Force Pile; either return it, or place it under your Force Pile or Reserve Deck.", card.getGameText());
        assertEquals(CardSubtype.USED, card.getCardSubtype());
        SCENARIO.BlueprintIconCheck(card, List.of(Icon.INTERRUPT, Icon.REFLECTIONS_GOLD, Icon.EPISODE_I));
        SCENARIO.BlueprintCardTypeCheck(card, List.of(CardType.INTERRUPT));
        SCENARIO.BlueprintKeywordCheck(card, List.of());
        SCENARIO.BlueprintPersonaCheck(card, List.of());
        SCENARIO.BlueprintModelTypeCheck(card, List.of());
    }

    @Test
    public void InsertionPlanningStatsAndKeywordsAreCorrect() {
        var card = LIBRARY.getSwccgoCardBlueprint("15_91");
        assertEquals(3, card.getCredits());
        assertEquals("Insertion Planning", card.getTitle());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(6f, card.getDestiny(), SCENARIO.epsilon);
        assertEquals(6f, card.getAlternateDestiny(), SCENARIO.epsilon);
        assertEquals(ExpansionSet.REFLECTIONS_GOLD, card.getExpansionSet());
        assertEquals(Rarity.UNKNOWN, card.getRarity());
        assertEquals("", card.getLore());
        assertTrue(card.isHorizontal());
        assertEquals("If your scout is in battle at an exterior planet site, subtract 3 from opponent's total battle destiny. OR Target opponent's character aboard an open vehicle moving to your scout's site. Draw destiny. If destiny +2 > ability, target goes to Used Pile.", card.getGameText());
        assertEquals(CardSubtype.USED, card.getCardSubtype());
        SCENARIO.BlueprintIconCheck(card, List.of(Icon.INTERRUPT, Icon.REFLECTIONS_GOLD));
        SCENARIO.BlueprintCardTypeCheck(card, List.of(CardType.INTERRUPT));
        SCENARIO.BlueprintKeywordCheck(card, List.of());
        SCENARIO.BlueprintPersonaCheck(card, List.of());
        SCENARIO.BlueprintModelTypeCheck(card, List.of());
    }

    @Test
    public void JediPresenceStatsAndKeywordsAreCorrect() {
        var card = LIBRARY.getSwccgoCardBlueprint("15_94");
        assertEquals(4, card.getCredits());
        assertEquals("Jedi Presence", card.getTitle());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(Uniqueness.UNRESTRICTED, card.getUniqueness());
        assertEquals(3f, card.getDestiny(), SCENARIO.epsilon);
        assertEquals(3f, card.getAlternateDestiny(), SCENARIO.epsilon);
        assertEquals(ExpansionSet.REFLECTIONS_GOLD, card.getExpansionSet());
        assertEquals(Rarity.UNKNOWN, card.getRarity());
        assertEquals("", card.getLore());
        assertTrue(card.isHorizontal());
        assertEquals("If one of your Jedi is present during any battle, use 1 Force to cause all other Rebels there to battle at double power.", card.getGameText());
        assertEquals(CardSubtype.LOST, card.getCardSubtype());
        SCENARIO.BlueprintIconCheck(card, List.of(Icon.INTERRUPT, Icon.REFLECTIONS_GOLD));
        SCENARIO.BlueprintCardTypeCheck(card, List.of(CardType.INTERRUPT));
        SCENARIO.BlueprintKeywordCheck(card, List.of());
        SCENARIO.BlueprintPersonaCheck(card, List.of());
        SCENARIO.BlueprintModelTypeCheck(card, List.of());
    }

    @Test
    public void LosingTrackStatsAndKeywordsAreCorrect() {
        var card = LIBRARY.getSwccgoCardBlueprint("15_98");
        assertEquals(3, card.getCredits());
        assertEquals("Losing Track", card.getTitle());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(6f, card.getDestiny(), SCENARIO.epsilon);
        assertEquals(6f, card.getAlternateDestiny(), SCENARIO.epsilon);
        assertEquals(ExpansionSet.REFLECTIONS_GOLD, card.getExpansionSet());
        assertEquals(Rarity.UNKNOWN, card.getRarity());
        assertEquals("", card.getLore());
        assertTrue(card.isHorizontal());
        assertEquals("USED: During battle, place top card of opponent's Lost Pile on top of their Reserve Deck. LOST: Use 1 Force to 'repair' your Podracer. OR Target a Podracer. Draw destiny. If destiny > Podracer's destiny number, target is 'damaged.'", card.getGameText());
        assertEquals(CardSubtype.USED_OR_LOST, card.getCardSubtype());
        SCENARIO.BlueprintIconCheck(card, List.of(Icon.INTERRUPT, Icon.REFLECTIONS_GOLD, Icon.EPISODE_I));
        SCENARIO.BlueprintCardTypeCheck(card, List.of(CardType.INTERRUPT));
        SCENARIO.BlueprintKeywordCheck(card, List.of());
        SCENARIO.BlueprintPersonaCheck(card, List.of());
        SCENARIO.BlueprintModelTypeCheck(card, List.of());
    }

    @Test
    public void MightOfTheRepublicStatsAndKeywordsAreCorrect() {
        var card = LIBRARY.getSwccgoCardBlueprint("15_102");
        assertEquals(2, card.getCredits());
        assertEquals("Might Of The Republic", card.getTitle());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(5f, card.getDestiny(), SCENARIO.epsilon);
        assertEquals(5f, card.getAlternateDestiny(), SCENARIO.epsilon);
        assertEquals(ExpansionSet.REFLECTIONS_GOLD, card.getExpansionSet());
        assertEquals(Rarity.UNKNOWN, card.getRarity());
        assertEquals("", card.getLore());
        assertTrue(card.isHorizontal());
        assertEquals("Once per game, retrieve 1 Force for each of your senators at Galactic Senate. (Immune to Sense.) OR Take one senator (or Coruscant Guard, Sei Taria or Mas Amedda) into hand from Reserve Deck; reshuffle.", card.getGameText());
        assertEquals(CardSubtype.USED, card.getCardSubtype());
        SCENARIO.BlueprintIconCheck(card, List.of(Icon.INTERRUPT, Icon.REFLECTIONS_GOLD, Icon.EPISODE_I));
        SCENARIO.BlueprintCardTypeCheck(card, List.of(CardType.INTERRUPT));
        SCENARIO.BlueprintKeywordCheck(card, List.of());
        SCENARIO.BlueprintPersonaCheck(card, List.of());
        SCENARIO.BlueprintModelTypeCheck(card, List.of());
    }

    @Test
    public void NeckAndNeckStatsAndKeywordsAreCorrect() {
        var card = LIBRARY.getSwccgoCardBlueprint("15_108");
        assertEquals(3, card.getCredits());
        assertEquals("Neck And Neck", card.getTitle());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(4f, card.getDestiny(), SCENARIO.epsilon);
        assertEquals(4f, card.getAlternateDestiny(), SCENARIO.epsilon);
        assertEquals(ExpansionSet.REFLECTIONS_GOLD, card.getExpansionSet());
        assertEquals(Rarity.UNKNOWN, card.getRarity());
        assertEquals("", card.getLore());
        assertTrue(card.isHorizontal());
        assertEquals("If difference between opponent's and your highest race totals < 4, lose 1 Force. Opponent draws no race destiny next control phase. OR If both players have < 10 Life Force remaining, both players retrieve 4 Force.", card.getGameText());
        assertEquals(CardSubtype.LOST, card.getCardSubtype());
        SCENARIO.BlueprintIconCheck(card, List.of(Icon.INTERRUPT, Icon.REFLECTIONS_GOLD, Icon.EPISODE_I));
        SCENARIO.BlueprintCardTypeCheck(card, List.of(CardType.INTERRUPT));
        SCENARIO.BlueprintKeywordCheck(card, List.of());
        SCENARIO.BlueprintPersonaCheck(card, List.of());
        SCENARIO.BlueprintModelTypeCheck(card, List.of());
    }

    @Test
    public void NiceOfYouGuysToDropByStatsAndKeywordsAreCorrect() {
        var card = LIBRARY.getSwccgoCardBlueprint("15_110");
        assertEquals(1, card.getCredits());
        assertEquals("Nice Of You Guys To Drop By", card.getTitle());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(Uniqueness.UNRESTRICTED, card.getUniqueness());
        assertEquals(5f, card.getDestiny(), SCENARIO.epsilon);
        assertEquals(5f, card.getAlternateDestiny(), SCENARIO.epsilon);
        assertEquals(ExpansionSet.REFLECTIONS_GOLD, card.getExpansionSet());
        assertEquals(Rarity.UNKNOWN, card.getRarity());
        assertEquals("", card.getLore());
        assertTrue(card.isHorizontal());
        assertEquals("Send one of your missing characters at same site as one of your piloted or driven vehicles to your Used Pile. OR Cancel any attempt by one character to 'hide' from a battle.", card.getGameText());
        assertEquals(CardSubtype.USED, card.getCardSubtype());
        SCENARIO.BlueprintIconCheck(card, List.of(Icon.INTERRUPT, Icon.REFLECTIONS_GOLD));
        SCENARIO.BlueprintCardTypeCheck(card, List.of(CardType.INTERRUPT));
        SCENARIO.BlueprintKeywordCheck(card, List.of());
        SCENARIO.BlueprintPersonaCheck(card, List.of());
        SCENARIO.BlueprintModelTypeCheck(card, List.of());
    }

    @Test
    public void NoGibenUpGeneralJarJarStatsAndKeywordsAreCorrect() {
        var card = LIBRARY.getSwccgoCardBlueprint("15_112");
        assertEquals(3, card.getCredits());
        assertEquals("No Giben Up, General Jar Jar!", card.getTitle());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(5f, card.getDestiny(), SCENARIO.epsilon);
        assertEquals(5f, card.getAlternateDestiny(), SCENARIO.epsilon);
        assertEquals(ExpansionSet.REFLECTIONS_GOLD, card.getExpansionSet());
        assertEquals(Rarity.UNKNOWN, card.getRarity());
        assertEquals("", card.getLore());
        assertTrue(card.isHorizontal());
        assertEquals("If you just lost a Gungan from table, place him in your hand. OR If Jar Jar and Tarpals are in a battle together, add two battle destiny.", card.getGameText());
        assertEquals(CardSubtype.LOST, card.getCardSubtype());
        SCENARIO.BlueprintIconCheck(card, List.of(Icon.INTERRUPT, Icon.REFLECTIONS_GOLD, Icon.EPISODE_I));
        SCENARIO.BlueprintCardTypeCheck(card, List.of(CardType.INTERRUPT));
        SCENARIO.BlueprintKeywordCheck(card, List.of());
        SCENARIO.BlueprintPersonaCheck(card, List.of());
        SCENARIO.BlueprintModelTypeCheck(card, List.of());
    }

    @Test
    public void OffTheEdgeStatsAndKeywordsAreCorrect() {
        var card = LIBRARY.getSwccgoCardBlueprint("15_114");
        assertEquals(3, card.getCredits());
        assertEquals("Off The Edge", card.getTitle());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(2f, card.getDestiny(), SCENARIO.epsilon);
        assertEquals(2f, card.getAlternateDestiny(), SCENARIO.epsilon);
        assertEquals(ExpansionSet.REFLECTIONS_GOLD, card.getExpansionSet());
        assertEquals(Rarity.UNKNOWN, card.getRarity());
        assertEquals("", card.getLore());
        assertTrue(card.isHorizontal());
        assertEquals("Target one of your characters at a Cloud City site. Draw destiny. If destiny > character's destiny number, retrieve Force equal to the difference. If destiny < character's destiny number, lose difference. If destiny = character's destiny number, character is lost.", card.getGameText());
        assertEquals(CardSubtype.LOST, card.getCardSubtype());
        SCENARIO.BlueprintIconCheck(card, List.of(Icon.INTERRUPT, Icon.REFLECTIONS_GOLD));
        SCENARIO.BlueprintCardTypeCheck(card, List.of(CardType.INTERRUPT));
        SCENARIO.BlueprintKeywordCheck(card, List.of());
        SCENARIO.BlueprintPersonaCheck(card, List.of());
        SCENARIO.BlueprintModelTypeCheck(card, List.of());
    }

    @Test
    public void OutOfCommissionStatsAndKeywordsAreCorrect() {
        var card = LIBRARY.getSwccgoCardBlueprint("15_119");
        assertEquals(2, card.getCredits());
        assertEquals("Out Of Commission", card.getTitle());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(Uniqueness.UNRESTRICTED, card.getUniqueness());
        assertEquals(5f, card.getDestiny(), SCENARIO.epsilon);
        assertEquals(5f, card.getAlternateDestiny(), SCENARIO.epsilon);
        assertEquals(ExpansionSet.REFLECTIONS_GOLD, card.getExpansionSet());
        assertEquals(Rarity.UNKNOWN, card.getRarity());
        assertEquals("", card.getLore());
        assertTrue(card.isHorizontal());
        assertEquals("During your control phase, use 2 Force to release a starship held by any Tractor Beam. (Not effective on Death Star Tractor Beam if Central Core in play.) OR Randomly select one card from opponent's Lost Pile or Blaster Rack and place out of play.", card.getGameText());
        assertEquals(CardSubtype.USED, card.getCardSubtype());
        SCENARIO.BlueprintIconCheck(card, List.of(Icon.INTERRUPT, Icon.REFLECTIONS_GOLD));
        SCENARIO.BlueprintCardTypeCheck(card, List.of(CardType.INTERRUPT));
        SCENARIO.BlueprintKeywordCheck(card, List.of());
        SCENARIO.BlueprintPersonaCheck(card, List.of());
        SCENARIO.BlueprintModelTypeCheck(card, List.of());
    }

    @Test
    public void SmokeScreenStatsAndKeywordsAreCorrect() {
        var card = LIBRARY.getSwccgoCardBlueprint("15_130");
        assertEquals(4, card.getCredits());
        assertEquals("Smoke Screen", card.getTitle());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(3f, card.getDestiny(), SCENARIO.epsilon);
        assertEquals(3f, card.getAlternateDestiny(), SCENARIO.epsilon);
        assertEquals(ExpansionSet.REFLECTIONS_GOLD, card.getExpansionSet());
        assertEquals(Rarity.UNKNOWN, card.getRarity());
        assertEquals("", card.getLore());
        assertTrue(card.isHorizontal());
        assertEquals("During a battle at a site, if you are about to draw a card for battle destiny, you may instead use the ability number of one of your characters involved in the battle.", card.getGameText());
        assertEquals(CardSubtype.LOST, card.getCardSubtype());
        SCENARIO.BlueprintIconCheck(card, List.of(Icon.INTERRUPT, Icon.REFLECTIONS_GOLD));
        SCENARIO.BlueprintCardTypeCheck(card, List.of(CardType.INTERRUPT));
        SCENARIO.BlueprintKeywordCheck(card, List.of());
        SCENARIO.BlueprintPersonaCheck(card, List.of());
        SCENARIO.BlueprintModelTypeCheck(card, List.of());
    }

    @Test
    public void StrangleStatsAndKeywordsAreCorrect() {
        var card = LIBRARY.getSwccgoCardBlueprint("15_135");
        assertEquals(1, card.getCredits());
        assertEquals("Strangle", card.getTitle());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(7f, card.getDestiny(), SCENARIO.epsilon);
        assertEquals(7f, card.getAlternateDestiny(), SCENARIO.epsilon);
        assertEquals(ExpansionSet.REFLECTIONS_GOLD, card.getExpansionSet());
        assertEquals(Rarity.UNKNOWN, card.getRarity());
        assertEquals("", card.getLore());
        assertTrue(card.isHorizontal());
        assertEquals("Target an escort alone and its captive. Draw destiny. Escort lost if destiny + captive's power > escort's ability + power. OR During a battle at a site where Leia is an escorted captive, add one battle destiny.", card.getGameText());
        assertEquals(CardSubtype.LOST, card.getCardSubtype());
        SCENARIO.BlueprintIconCheck(card, List.of(Icon.INTERRUPT, Icon.REFLECTIONS_GOLD));
        SCENARIO.BlueprintCardTypeCheck(card, List.of(CardType.INTERRUPT));
        SCENARIO.BlueprintKeywordCheck(card, List.of());
        SCENARIO.BlueprintPersonaCheck(card, List.of());
        SCENARIO.BlueprintModelTypeCheck(card, List.of());
    }

    @Test
    public void StrikeBlockedStatsAndKeywordsAreCorrect() {
        var card = LIBRARY.getSwccgoCardBlueprint("15_136");
        assertEquals(2, card.getCredits());
        assertEquals("Strike Blocked", card.getTitle());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(3f, card.getDestiny(), SCENARIO.epsilon);
        assertEquals(3f, card.getAlternateDestiny(), SCENARIO.epsilon);
        assertEquals(ExpansionSet.REFLECTIONS_GOLD, card.getExpansionSet());
        assertEquals(Rarity.UNKNOWN, card.getRarity());
        assertEquals("", card.getLore());
        assertTrue(card.isHorizontal());
        assertEquals("Take top card of Force Pile into hand. OR If opponent just revealed a combat card, it is canceled (place in opponent's Used Pile) and opponent must draw destiny instead (that destiny is reduced by 1). OR Cancel Maul Strikes, Dark Strike, or You Are Beaten.", card.getGameText());
        assertEquals(CardSubtype.USED, card.getCardSubtype());
        SCENARIO.BlueprintIconCheck(card, List.of(Icon.INTERRUPT, Icon.REFLECTIONS_GOLD, Icon.EPISODE_I));
        SCENARIO.BlueprintCardTypeCheck(card, List.of(CardType.INTERRUPT));
        SCENARIO.BlueprintKeywordCheck(card, List.of());
        SCENARIO.BlueprintPersonaCheck(card, List.of());
        SCENARIO.BlueprintModelTypeCheck(card, List.of());
    }

    @Test
    public void TooCloseForComfortStatsAndKeywordsAreCorrect() {
        var card = LIBRARY.getSwccgoCardBlueprint("15_141");
        assertEquals(3, card.getCredits());
        assertEquals("Too Close For Comfort", card.getTitle());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(5f, card.getDestiny(), SCENARIO.epsilon);
        assertEquals(5f, card.getAlternateDestiny(), SCENARIO.epsilon);
        assertEquals(ExpansionSet.REFLECTIONS_GOLD, card.getExpansionSet());
        assertEquals(Rarity.UNKNOWN, card.getRarity());
        assertEquals("", card.getLore());
        assertTrue(card.isHorizontal());
        assertEquals("USED: If your character was just targeted by a weapon, opponent must use 2 Force or the targeting is canceled. LOST: Cancel Hit Racer or Podracer Collision.", card.getGameText());
        assertEquals(CardSubtype.USED_OR_LOST, card.getCardSubtype());
        SCENARIO.BlueprintIconCheck(card, List.of(Icon.INTERRUPT, Icon.REFLECTIONS_GOLD, Icon.EPISODE_I));
        SCENARIO.BlueprintCardTypeCheck(card, List.of(CardType.INTERRUPT));
        SCENARIO.BlueprintKeywordCheck(card, List.of());
        SCENARIO.BlueprintPersonaCheck(card, List.of());
        SCENARIO.BlueprintModelTypeCheck(card, List.of());
    }

    @Test
    public void VoteNowStatsAndKeywordsAreCorrect() {
        var card = LIBRARY.getSwccgoCardBlueprint("15_143");
        assertEquals(3, card.getCredits());
        assertEquals("Vote Now!", card.getTitle());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(5f, card.getDestiny(), SCENARIO.epsilon);
        assertEquals(5f, card.getAlternateDestiny(), SCENARIO.epsilon);
        assertEquals(ExpansionSet.REFLECTIONS_GOLD, card.getExpansionSet());
        assertEquals(Rarity.UNKNOWN, card.getRarity());
        assertEquals("", card.getLore());
        assertTrue(card.isHorizontal());
        assertEquals("During your control phase, draw destiny. If destiny > 2, draw up to X cards (maximum 4) from Reserve Deck, where X = your total politics at Galactic Senate minus opponent's total politics at Galactic Senate.", card.getGameText());
        assertEquals(CardSubtype.USED, card.getCardSubtype());
        SCENARIO.BlueprintIconCheck(card, List.of(Icon.INTERRUPT, Icon.REFLECTIONS_GOLD, Icon.EPISODE_I));
        SCENARIO.BlueprintCardTypeCheck(card, List.of(CardType.INTERRUPT));
        SCENARIO.BlueprintKeywordCheck(card, List.of());
        SCENARIO.BlueprintPersonaCheck(card, List.of());
        SCENARIO.BlueprintModelTypeCheck(card, List.of());
    }

    @Test
    public void WeDontNeedTheirScumStatsAndKeywordsAreCorrect() {
        var card = LIBRARY.getSwccgoCardBlueprint("15_146");
        assertEquals(2, card.getCredits());
        assertEquals("We Don't Need Their Scum", card.getTitle());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(Uniqueness.UNRESTRICTED, card.getUniqueness());
        assertEquals(2f, card.getDestiny(), SCENARIO.epsilon);
        assertEquals(2f, card.getAlternateDestiny(), SCENARIO.epsilon);
        assertEquals(ExpansionSet.REFLECTIONS_GOLD, card.getExpansionSet());
        assertEquals(Rarity.UNKNOWN, card.getRarity());
        assertEquals("", card.getLore());
        assertTrue(card.isHorizontal());
        assertEquals("USED: Cancel Res Luk Ra'auf. LOST: If two bounty hunters (or an Imperial and a bounty hunter) are in a battle together, use 2 Force to draw two destiny. Subtract total from opponent's attrition and total power (cannot fall below zero).", card.getGameText());
        assertEquals(CardSubtype.USED_OR_LOST, card.getCardSubtype());
        SCENARIO.BlueprintIconCheck(card, List.of(Icon.INTERRUPT, Icon.REFLECTIONS_GOLD));
        SCENARIO.BlueprintCardTypeCheck(card, List.of(CardType.INTERRUPT));
        SCENARIO.BlueprintKeywordCheck(card, List.of());
        SCENARIO.BlueprintPersonaCheck(card, List.of());
        SCENARIO.BlueprintModelTypeCheck(card, List.of());
    }

    @Test
    public void WeWishToBoardAtOnceStatsAndKeywordsAreCorrect() {
        var card = LIBRARY.getSwccgoCardBlueprint("15_148");
        assertEquals(3, card.getCredits());
        assertEquals("We Wish To Board At Once", card.getTitle());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(5f, card.getDestiny(), SCENARIO.epsilon);
        assertEquals(5f, card.getAlternateDestiny(), SCENARIO.epsilon);
        assertEquals(ExpansionSet.REFLECTIONS_GOLD, card.getExpansionSet());
        assertEquals(Rarity.UNKNOWN, card.getRarity());
        assertEquals("", card.getLore());
        assertTrue(card.isHorizontal());
        assertEquals("Use 3 Force to take one Effect of any kind into hand from your Reserve Deck; reshuffle. OR Take Radiant VII, or one Interrupt with the word 'Podracer(s)' in its game text, into hand from Reserve Deck; reshuffle.", card.getGameText());
        assertEquals(CardSubtype.USED, card.getCardSubtype());
        SCENARIO.BlueprintIconCheck(card, List.of(Icon.INTERRUPT, Icon.REFLECTIONS_GOLD, Icon.EPISODE_I));
        SCENARIO.BlueprintCardTypeCheck(card, List.of(CardType.INTERRUPT));
        SCENARIO.BlueprintKeywordCheck(card, List.of());
        SCENARIO.BlueprintPersonaCheck(card, List.of());
        SCENARIO.BlueprintModelTypeCheck(card, List.of());
    }

    @Test
    public void YouWillTakeMeToJabbaNowStatsAndKeywordsAreCorrect() {
        var card = LIBRARY.getSwccgoCardBlueprint("15_153");
        assertEquals(3, card.getCredits());
        assertEquals("You Will Take Me To Jabba Now", card.getTitle());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(5f, card.getDestiny(), SCENARIO.epsilon);
        assertEquals(5f, card.getAlternateDestiny(), SCENARIO.epsilon);
        assertEquals(ExpansionSet.REFLECTIONS_GOLD, card.getExpansionSet());
        assertEquals(Rarity.UNKNOWN, card.getRarity());
        assertEquals("", card.getLore());
        assertTrue(card.isHorizontal());
        assertEquals("If Jabba is at a Jabba's Palace site, relocate one of your characters to that location from a related site. OR During your deploy phase, deploy one alien (at normal use of the Force) from Reserve Deck to the Audience Chamber; reshuffle.", card.getGameText());
        assertEquals(CardSubtype.USED, card.getCardSubtype());
        SCENARIO.BlueprintIconCheck(card, List.of(Icon.INTERRUPT, Icon.REFLECTIONS_GOLD));
        SCENARIO.BlueprintCardTypeCheck(card, List.of(CardType.INTERRUPT));
        SCENARIO.BlueprintKeywordCheck(card, List.of());
        SCENARIO.BlueprintPersonaCheck(card, List.of());
        SCENARIO.BlueprintModelTypeCheck(card, List.of());
    }
}
