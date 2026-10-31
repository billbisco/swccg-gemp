package com.gempukku.swccgo.cards.set1112.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.AttachedCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
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
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.CancelCardActionBuilder;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.UnrespondableEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployStackedCardEffect;
import com.gempukku.swccgo.logic.evaluators.Evaluator;
import com.gempukku.swccgo.logic.modifiers.IconModifier;
import com.gempukku.swccgo.logic.modifiers.InfluenceModifier;
import com.gempukku.swccgo.logic.modifiers.KeywordModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotDeployModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PlaceInUsedPileWhenCanceledModifier;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 12: Martyrs & Magistrates
 * Type: Effect
 * Title: There Is No Try & Oppressive Enforcement
 */
public class Card1112_042 extends AbstractNormalEffect {
    public Card1112_042() {
        super(Side.DARK, 1, PlayCardZoneOption.ATTACHED, "There Is No Try & Oppressive Enforcement", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_ORIGINAL_SET_12, Rarity.V);
        setVirtualSuffix(true);
        addComboCardTitles(Title.There_Is_No_Try, Title.Oppressive_Enforcement);
        setGameText("Deploy on Conference Room. Revolution is canceled here. You may not deploy aliens (except Mara Jade and bounty hunters), [Episode I] cards, or Luke? Luuuuke!. Emperor and your admirals, generals, and moffs are Imperial Council Members and influence +1. While present, each Imperial Council Member adds one [Dark Side Force]. During your control phase, may use 2 Force to take one Laser Gate, an Imperial Council Member, or a Political Effect into hand from Reserve Deck; reshuffle. Your cards may deploy to table from Political Effects. May place your canceled Political Effect in Used Pile. (Immune to Alter.)");
        addIcons(Icon.REFLECTIONS_II);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.Death_Star_Conference_Room;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();
        Filter yourLeaders = Filters.and(Filters.your(self), Filters.or(Filters.Emperor, Filters.admiral, Filters.general, Filters.moff));
        Filter bannedAliens = Filters.and(Filters.alien, Filters.not(Filters.or(Filters.bounty_hunter, Filters.Mara_Jade)));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayNotDeployModifier(self, bannedAliens, playerId));
        modifiers.add(new MayNotDeployModifier(self, Icon.EPISODE_I, playerId));
        modifiers.add(new MayNotDeployModifier(self, Filters.title("Luke? Luuuuke!"), playerId));
        modifiers.add(new KeywordModifier(self, yourLeaders, Keyword.IMPERIAL_COUNCIL_MEMBER));
        modifiers.add(new InfluenceModifier(self, yourLeaders, 1));
        modifiers.add(new IconModifier(self, Filters.hasAttached(self), new AttachedCondition(self, Filters.Death_Star_Conference_Room), Icon.DARK_FORCE, councilMembersHere(self)));
        modifiers.add(new PlaceInUsedPileWhenCanceledModifier(self, Filters.and(Filters.your(self), Filters.Political_Effect)));
        return modifiers;
    }

    private Evaluator councilMembersHere(final PhysicalCard self) {
        return new Evaluator() {
            @Override
            public float evaluateExpression(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard cardAffected) {
                return Filters.countActive(gameState.getGame(), self, Filters.and(Keyword.IMPERIAL_COUNCIL_MEMBER, Filters.present(self)));
            }

            @Override
            public float evaluateExpression(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard cardAffected, PhysicalCard otherCard) {
                return evaluateExpression(gameState, modifiersQuerying, cardAffected);
            }
        };
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<TopLevelGameTextAction>();

        if (GameConditions.isDuringYourPhase(game, playerId, Phase.CONTROL)
                && GameConditions.canUseForce(game, playerId, 2)
                && GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, GameTextActionId.LEGACY__THERE_IS_NO_TRY_V__DEPLOY_LASER_GATE_OR_POLITICAL_EFFECT)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId);
            action.setText("Take Laser Gate, council member, or Political Effect into hand");
            action.appendCost(
                    new UseForceEffect(action, playerId, 2));
            action.appendEffect(
                    new com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect(action, playerId,
                            Filters.or(Filters.Laser_Gate, Keyword.IMPERIAL_COUNCIL_MEMBER, Filters.Political_Effect), true));
            actions.add(action);
        }

        GameTextActionId deployStackedId = GameTextActionId.LEGACY__THERE_IS_NO_TRY_V__DEPLOY_CARD_FROM_POLITICAL_EFFECT;
        Filter yourPoliticalEffectWithStacked = Filters.and(Filters.your(self), Filters.Political_Effect, Filters.hasStacked(Filters.any));
        if (GameConditions.isDuringYourPhase(game, playerId, Phase.DEPLOY)
                && GameConditions.canSpot(game, self, yourPoliticalEffectWithStacked)) {
            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, deployStackedId);
            action.setText("Deploy a card stacked on a Political Effect");
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose a Political Effect", yourPoliticalEffectWithStacked) {
                        @Override
                        protected void cardTargeted(int targetGroupId, final PhysicalCard politicalEffect) {
                            action.allowResponses(
                                    new UnrespondableEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            action.appendEffect(
                                                    new DeployStackedCardEffect(action, politicalEffect, Filters.any, false));
                                        }
                                    });
                        }
                    });
            actions.add(action);
        }
        return actions;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredBeforeTriggers(final SwccgGame game, Effect effect, final PhysicalCard self, int gameTextSourceCardId) {
        if (TriggerConditions.isPlayingCard(game, effect, Filters.Revolution)
                && GameConditions.canCancelCardBeingPlayed(game, self, effect)) {
            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            CancelCardActionBuilder.buildCancelCardBeingPlayedAction(action, effect);
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, final EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        List<RequiredGameTextTriggerAction> actions = new LinkedList<RequiredGameTextTriggerAction>();
        if (TriggerConditions.isTableChanged(game, effectResult)
                && GameConditions.canTargetToCancel(game, self, Filters.Revolution)) {
            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            CancelCardActionBuilder.buildCancelCardAction(action, Filters.Revolution, Title.Revolution);
            actions.add(action);
        }
        return actions;
    }
}
