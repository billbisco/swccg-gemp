package com.gempukku.swccgo.cards.set1108.dark;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.MoveCardAsRegularMoveEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardToLocationFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.EachWeaponDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 8
 * Type: Character
 * Subtype: Alien
 * Title: Ur'Ru'r
 */
public class Card1108_048 extends AbstractAlien {
    public Card1108_048() {
        super(Side.DARK, 3, 2, 2, 1, 4, "Ur'Ru'r", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_ORIGINAL_SET_8, Rarity.V);
        setVirtualSuffix(true);
        setLore("Fierce Tusken Raider. Ransacks homes and moisture farms on the borders of Mos Espa.");
        setGameText("Deploys only on Tatooine. During your deploy phase, if with URoRRuR'R'R, may deploy a weapon with 'Tusken' in lore here from Reserve Deck; reshuffle; your weapon destiny draws for such weapons are +2. During your deploy phase, may move Ur'Ru'r as a regular move.");
        addIcons(Icon.WARRIOR, Icon.SPECIAL_EDITION);
        setSpecies(Species.TUSKEN_RAIDER);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.Deploys_on_Tatooine;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new EachWeaponDestinyModifier(self, Filters.and(Filters.your(self), Filters.weapon, Filters.loreContains("Tusken")), 2));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<TopLevelGameTextAction>();

        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__UR_RU_R_V__DEPLOY_TUSKEN_WEAPON;

        // Check condition(s)
        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, gameTextActionId, Phase.DEPLOY)
                && GameConditions.isWith(game, self, Filters.URoRRuRRR)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy Tusken weapon from Reserve Deck");
            action.setActionMsg("Deploy a weapon with 'Tusken' in lore here from Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerPhaseEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new DeployCardToLocationFromReserveDeckEffect(action, Filters.and(Filters.weapon, Filters.loreContains("Tusken")), Filters.here(self), true));
            actions.add(action);
        }

        // Check condition(s)
        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, Phase.DEPLOY)
                && Filters.movableAsRegularMove(playerId, false, 0, false, Filters.any).accepts(game, self)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId);
            action.setText("Move as a regular move");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerPhaseEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new MoveCardAsRegularMoveEffect(action, playerId, self, false, false, Filters.any));
            actions.add(action);
        }
        return actions;
    }
}
