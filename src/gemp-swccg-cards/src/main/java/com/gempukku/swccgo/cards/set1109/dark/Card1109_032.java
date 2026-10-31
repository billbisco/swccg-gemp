package com.gempukku.swccgo.cards.set1109.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.evaluators.OnTableEvaluator;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.conditions.GreaterThanCondition;
import com.gempukku.swccgo.logic.effects.choose.DeployCardToTargetFromReserveDeckEffect;
import com.gempukku.swccgo.logic.evaluators.Evaluator;
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.MinimumDeployCostToTargetModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.PlayCardResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 9: Sanctuary Moon
 * Type: Effect
 * Title: Dreaded Imperial Starfleet
 */
public class Card1109_032 extends AbstractNormalEffect {
    public Card1109_032() {
        super(Side.DARK, 3, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, Title.Dreaded_Imperial_Starfleet, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_ORIGINAL_SET_9, Rarity.V);
        setVirtualSuffix(true);
        setLore("The Empire uses short-range fighters and patrol craft to maintain a presence in every system within its scope of power.");
        setGameText("Deploy on table. While you have more systems on table than opponent, your capital starships deploy -2 (to a minimum of 3) to battlegrounds. When you deploy an Imperial-Class Star Destroyer, may deploy (for free) a [Death Star II] or [A New Hope] squadron there (no replacement is necessary) from Reserve Deck; reshuffle. (Immune to Alter.)");
        addIcons(Icon.SPECIAL_EDITION);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();
        String opponent = game.getOpponent(playerId);

        Evaluator yourSystems = new OnTableEvaluator(self, Filters.and(Filters.system, Filters.owner(playerId)));
        Evaluator opponentSystems = new OnTableEvaluator(self, Filters.and(Filters.system, Filters.owner(opponent)));
        Condition moreSystemsThanOpponent = new GreaterThanCondition(yourSystems, opponentSystems);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostToLocationModifier(self, Filters.and(Filters.your(self), Filters.capital_starship), moreSystemsThanOpponent, -2, Filters.battleground));
        modifiers.add(new MinimumDeployCostToTargetModifier(self, Filters.and(Filters.your(self), Filters.capital_starship), moreSystemsThanOpponent, 3, Filters.battleground));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, final EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.OVS__DREADED_IMPERIAL_STARFLEET__DEPLOY_SQUADRON;

        if (TriggerConditions.justDeployed(game, effectResult, playerId, Filters.Imperial_class_Star_Destroyer)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId)
                && effectResult instanceof PlayCardResult) {
            final PhysicalCard starDestroyer = ((PlayCardResult) effectResult).getPlayedCard();
            if (starDestroyer != null) {
                final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, playerId, gameTextSourceCardId, gameTextActionId);
                action.setText("Deploy a squadron there");
                action.setActionMsg("Deploy a [Death Star II] or [A New Hope] squadron for free to same location as " + GameUtils.getCardLink(starDestroyer) + " from Reserve Deck");
                action.appendEffect(
                        new DeployCardToTargetFromReserveDeckEffect(action,
                                Filters.and(Filters.squadron, Filters.or(Icon.DEATH_STAR_II, Icon.A_NEW_HOPE)),
                                Filters.sameLocation(starDestroyer), true, true));
                return Collections.singletonList(action);
            }
        }
        return null;
    }
}
