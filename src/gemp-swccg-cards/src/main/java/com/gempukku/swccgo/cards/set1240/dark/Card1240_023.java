package com.gempukku.swccgo.cards.set1240.dark;

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
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.logic.modifiers.MayDeployToTargetModifier;
import com.gempukku.swccgo.logic.timing.PassthruEffect;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfTurnModifierEffect;
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
import com.gempukku.swccgo.logic.modifiers.MaximumToReduceDeployCostByToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.PlayCardResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 4: Expanded Universe
 * Type: Effect
 * Title: Dreaded Imperial Starfleet
 */
public class Card1240_023 extends AbstractNormalEffect {
    public Card1240_023() {
        super(Side.DARK, 3, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, Title.Dreaded_Imperial_Starfleet, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_4, Rarity.V);
        setVirtualSuffix(true);
        setLore("The Empire uses short-range fighters and patrol craft to maintain a presence in every system within its scope of power.");
        setGameText("Deploy on table. While you have more systems on table than opponent, your capital starships may deploy at -2 (to a maximum of -3) to battlegrounds. When you deploy an Imperial-class Star Destroyer, may deploy (for free) a [Death Star II] or [A New Hope] squadron there (no replacement necessary) from Reserve Deck; reshuffle. (Immune to Alter.)");
        addIcons(Icon.SPECIAL_EDITION, Icon.LEGACY_BLOCK_4);
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
        var maximumReduction = new MaximumToReduceDeployCostByToLocationModifier(self,
                Filters.and(Filters.your(self), Filters.capital_starship), 3, Filters.battleground);
        maximumReduction.appendCondition(moreSystemsThanOpponent);
        modifiers.add(maximumReduction);
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
                final Filter squadrons = Filters.and(Filters.squadron, Filters.or(Icon.DEATH_STAR_II, Icon.A_NEW_HOPE));
                // "No replacement necessary": only while this download resolves.
                final boolean[] downloading = {false};
                MayDeployToTargetModifier allowSquadron = new MayDeployToTargetModifier(self, squadrons,
                        Filters.sameLocation(starDestroyer));
                allowSquadron.appendCondition((gameState, modifiersQuerying) -> downloading[0]);
                action.appendEffect(new AddUntilEndOfTurnModifierEffect(action, allowSquadron, null));
                action.appendEffect(
                        new PassthruEffect(action) {
                            @Override
                            protected void doPlayEffect(SwccgGame game) {
                                downloading[0] = true;
                            }
                        });
                action.appendEffect(
                        new DeployCardToTargetFromReserveDeckEffect(action, squadrons,
                                Filters.sameLocation(starDestroyer), true, true));
                action.appendEffect(
                        new PassthruEffect(action) {
                            @Override
                            protected void doPlayEffect(SwccgGame game) {
                                downloading[0] = false;
                            }
                        });
                return Collections.singletonList(action);
            }
        }
        return null;
    }
}
