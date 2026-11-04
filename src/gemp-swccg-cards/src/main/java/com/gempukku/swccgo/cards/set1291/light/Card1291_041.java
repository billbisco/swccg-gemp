package com.gempukku.swccgo.cards.set1291.light;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.MayCarryPassengerAsIfCreatureVehicleModifier;
import com.gempukku.swccgo.logic.modifiers.MayDeployToTargetModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ModifierFlag;
import com.gempukku.swccgo.logic.modifiers.SpecialFlagModifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 9.1
 * Type: Character
 * Subtype: Alien
 * Title: Foul Moudama
 */
public class Card1291_041 extends AbstractAlien {
    public Card1291_041() {
        super(Side.LIGHT, 2, 6, 5, 6, 5, "Foul Moudama", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_9_1, Rarity.V);
        setLore("Talz.");
        setGameText("If at your prison, Light Side goes first. Deploys -2 to Coruscant or a mobile site. May carry a character of power < 4 (as if riding a creature vehicle). Guardian's Lightsaber may deploy on Foul. Once per game, may take a clone or a non-Jedi alien into hand from Reserve Deck; reshuffle.");
        addIcons(Icon.WARRIOR, Icon.EPISODE_I, Icon.CLONE_ARMY);
        setSpecies(Species.TALZ);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostToLocationModifier(self, -2, Filters.or(Filters.Coruscant_location, Filters.mobile_site)));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        // The first player is decided after starting cards are played; while Foul is at your prison then, Light Side goes first.
        modifiers.add(new SpecialFlagModifier(self, new AtCondition(self, Filters.and(Filters.your(self), Filters.prison)), ModifierFlag.LIGHT_SIDE_GOES_FIRST));
        modifiers.add(new MayCarryPassengerAsIfCreatureVehicleModifier(self, Filters.and(Filters.character, Filters.powerLessThan(4))));
        modifiers.add(new MayDeployToTargetModifier(self, Filters.title("Guardian's Lightsaber"), Filters.sameCardId(self)));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__FOUL_MOUDAMA__TAKE_CLONE_OR_ALIEN;

        // Check condition(s)
        if (GameConditions.isOncePerGame(game, self, gameTextActionId)
                && GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Take a clone or non-Jedi alien into hand from Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerGameEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.or(Filters.clone, Filters.and(Filters.alien, Filters.not(Filters.Jedi))), true));
            return Collections.singletonList(action);
        }
        return null;
    }
}
