package com.gempukku.swccgo.cards.set15;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.SwccgCardBlueprint;
import com.gempukku.swccgo.game.SwccgCardBlueprintLibrary;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.*;

/** Expectations transcribed from full card images independently by the parent reviewer. */
public class ReflectionsGoldExtraBlankTests {
    private final VirtualTableScenario scn = new VirtualTableScenario(new HashMap<>(), new HashMap<>());

    private SwccgCardBlueprint blueprint(int number, String title, Side side, CardType type, Icon... icons) {
        var card = new SwccgCardBlueprintLibrary().getSwccgoCardBlueprint("15_" + number);
        assertEquals(title, card.getTitle());
        assertEquals(side, card.getSide());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(ExpansionSet.REFLECTIONS_GOLD, card.getExpansionSet());
        assertEquals(Rarity.UNKNOWN, card.getRarity());
        assertEquals("", card.getGameText());
        scn.BlueprintCardTypeCheck(card, List.of(type));
        scn.BlueprintIconCheck(card, List.of(icons));
        return card;
    }

    @Test
    public void GunganCelebrationTrumpetHasPrintedStatsAndNoInventedDeviceAbility() {
        var card = blueprint(19, "Gungan Celebration Trumpet", Side.LIGHT, CardType.DEVICE,
                Icon.DEVICE, Icon.REFLECTIONS_GOLD, Icon.EPISODE_I);
        assertEquals(4, card.getDestiny(), scn.epsilon);
        scn.BlueprintKeywordCheck(card, List.of());
    }

    @Test
    public void SecurityActivatorsHasPrintedStatsAndNoInventedDeviceAbility() {
        var card = blueprint(169, "Security Activators", Side.DARK, CardType.DEVICE,
                Icon.DEVICE, Icon.REFLECTIONS_GOLD, Icon.EPISODE_I);
        assertEquals(3, card.getDestiny(), scn.epsilon);
        scn.BlueprintKeywordCheck(card, List.of());
    }

    @Test
    public void NabooCeremonialChamberPreservesPrintedExteriorAndZeroForceIcons() {
        var card = blueprint(156, "Naboo: Ceremonial Chamber", Side.LIGHT, CardType.LOCATION,
                Icon.REFLECTIONS_GOLD, Icon.EPISODE_I, Icon.PLANET, Icon.EXTERIOR_SITE);
        assertEquals(CardSubtype.SITE, card.getCardSubtype());
        assertEquals("Naboo", card.getSystemName());
        assertEquals("", card.getLocationDarkSideGameText());
        assertEquals("", card.getLocationLightSideGameText());
        scn.BlueprintKeywordCheck(card, List.of());
    }

    @Test
    public void StarDestroyerExecutorHasPrintedStatsWithoutInventedPilotOrCapacity() {
        var card = blueprint(309, "Star Destroyer Executor", Side.DARK, CardType.STARSHIP,
                Icon.STARSHIP, Icon.REFLECTIONS_GOLD);
        assertEquals(CardSubtype.CAPITAL, card.getCardSubtype());
        assertEquals(3, card.getDestiny(), scn.epsilon);
        assertEquals(9, card.getDeployCost(), scn.epsilon);
        assertEquals(9, card.getPower(), scn.epsilon);
        assertEquals(12, card.getArmor(), scn.epsilon);
        assertEquals(2, card.getHyperspeed(), scn.epsilon);
        assertEquals(12, card.getForfeit(), scn.epsilon);
        assertEquals(0, card.getPilotCapacity());
        assertEquals(0, card.getPassengerCapacity());
        scn.BlueprintPersonaCheck(card, List.of(Persona.EXECUTOR));
        scn.BlueprintModelTypeCheck(card, List.of(ModelType.IMPERIAL_CLASS_STAR_DESTROYER));
        scn.BlueprintKeywordCheck(card, List.of(Keyword.SHIP_DOCKING_CAPABILITY));
    }

    @Test
    public void BlizzardScout4HasPrintedStatsWithoutInventedPilotOrCapacity() {
        var card = blueprint(310, "Blizzard Scout 4", Side.DARK, CardType.VEHICLE,
                Icon.VEHICLE, Icon.REFLECTIONS_GOLD);
        assertEquals("AT-ST walker assigned to the left flank at the Battle of Hoth. One of the first Imperial units to break through the Rebel defense perimeter. Enclosed.", card.getLore());
        assertEquals(CardSubtype.COMBAT, card.getCardSubtype());
        assertEquals(4, card.getDestiny(), scn.epsilon);
        assertEquals(2, card.getDeployCost(), scn.epsilon);
        assertEquals(3, card.getPower(), scn.epsilon);
        assertEquals(4, card.getArmor(), scn.epsilon);
        assertEquals(3, card.getLandspeed(), scn.epsilon);
        assertEquals(4, card.getForfeit(), scn.epsilon);
        assertEquals(0, card.getPilotCapacity());
        assertEquals(0, card.getPassengerCapacity());
        scn.BlueprintModelTypeCheck(card, List.of(ModelType.AT_ST));
        scn.BlueprintKeywordCheck(card, List.of(Keyword.ENCLOSED));
    }

    @Test
    public void BlankDevicesDoNotOfferInventedDeploymentActions() {
        var light = new HashMap<String, String>();
        light.put("device", "15_19");
        var dark = new HashMap<String, String>();
        dark.put("device", "15_169");
        var table = new VirtualTableScenario(light, dark);
        table.StartGame();
        var trumpet = table.GetLSCard("device");
        var activators = table.GetDSCard("device");
        table.MoveCardsToLSHand(trumpet);
        table.MoveCardsToDSHand(activators);
        table.SkipToLSTurn(Phase.DEPLOY);
        assertFalse(table.LSCardPlayAvailable(trumpet));
        table.SkipToDSTurn(Phase.DEPLOY);
        assertFalse(table.DSCardPlayAvailable(activators));
    }
}
