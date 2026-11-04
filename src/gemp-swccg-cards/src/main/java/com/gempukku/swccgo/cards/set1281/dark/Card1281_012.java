package com.gempukku.swccgo.cards.set1281.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.effects.RetrieveCardEffect;
import com.gempukku.swccgo.logic.modifiers.DefenseValueModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 8.1
 * Type: Effect
 * Title: On The Hunt
 */
public class Card1281_012 extends AbstractNormalEffect {
    public Card1281_012() {
        super(Side.DARK, 5, PlayCardZoneOption.ATTACHED, "On The Hunt", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_8_1, Rarity.V);
        setLore("Blank.");
        setGameText("Deploy on Contract Killers. Assassins are defense value +1. Once per game may retrieve (Virtual Block 8) Death Mark. At end of opponent's turn, if their character targeted by (Virtual Block 8) Death Mark is at a non-battleground or aboard their starship, opponent loses 3 Force. (Immune to Alter.)");
        addImmuneToCardTitle(Title.Alter);
    }

    private static Filter vb8DeathMark() {
        return Filters.and(Filters.Death_Mark, Icon.LEGACY_BLOCK_8);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.title("Contract Killers");
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DefenseValueModifier(self, Filters.assassin, 1));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__ON_THE_HUNT__RETRIEVE_DEATH_MARK;

        // Check condition(s)
        if (GameConditions.isOncePerGame(game, self, gameTextActionId)
                && GameConditions.canSearchLostPile(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Retrieve Death Mark");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerGameEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new RetrieveCardEffect(action, playerId, vb8DeathMark()));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        String opponent = game.getOpponent(self.getOwner());

        // Check condition(s)
        if (TriggerConditions.isEndOfOpponentsTurn(game, effectResult, self)) {
            Filter theirStarship = Filters.and(Filters.owner(opponent), Filters.starship);
            Filter markAway = Filters.and(Filters.owner(opponent), Filters.character,
                    Filters.hasAttached(vb8DeathMark()),
                    Filters.or(Filters.at(Filters.non_battleground_location), Filters.aboard(theirStarship)));
            if (GameConditions.canSpot(game, self, markAway)) {

                RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                action.setText("Make opponent lose 3 Force");
                // Perform result(s)
                action.appendEffect(
                        new LoseForceEffect(action, opponent, 3));
                return Collections.singletonList(action);
            }
        }
        return null;
    }
}
